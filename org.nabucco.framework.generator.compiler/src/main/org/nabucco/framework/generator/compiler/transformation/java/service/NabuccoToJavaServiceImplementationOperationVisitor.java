/*
 * Copyright 2012 PRODYNA AG
 *
 * Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/eclipse-1.0.php or
 * http://www.nabucco.org/License.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nabucco.framework.generator.compiler.transformation.java.service;

import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoServiceType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceImplementationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceImplementationOperationVisitor extends NabuccoToJavaVisitorSupport implements ServerConstants {

    private AnnotationDeclaration statementAnnotations;

    /** Already created service types. */
    private Set<NabuccoServiceType> serviceTypes;

    /**
     * Creates a new {@link NabuccoToJavaServiceImplementationOperationVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param serviceTypes
     *            the already created service types
     */
    public NabuccoToJavaServiceImplementationOperationVisitor(NabuccoToJavaVisitorContext visitorContext,
            Set<NabuccoServiceType> serviceTypes) {
        super(visitorContext);

        this.serviceTypes = serviceTypes;
    }

    /**
     * Getter for the serviceTypes.
     * 
     * @return Returns the serviceTypes.
     */
    public Set<NabuccoServiceType> getServiceTypes() {
        return this.serviceTypes;
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, MdaModel<JavaModel> target) {
        // Package must not be redefined!
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<JavaModel> target) {

        this.statementAnnotations = nabuccoService.annotationDeclaration;

        super.visit(nabuccoService, target);

        NabuccoServiceType serviceType = NabuccoServiceType.valueOf(nabuccoService);

        if (!this.serviceTypes.add(serviceType)) {
            return;
        }

        switch (serviceType) {

        case PERSISTENCE: {

            String name = NabuccoTransformationUtility.firstToLower(ENTITY_MANAGER);

            JavaAstContainter<? extends ASTNode> container = JavaAstSupport.createField(ENTITY_MANAGER, name,
                    NabuccoModifierType.PRIVATE);

            container.getImports().add(IMPORT_ENTITY_MANAGER);

            this.getVisitorContext().getContainerList().add(container);

            break;
        }

        case RESOURCE: {

            String name = NabuccoTransformationUtility.firstToLower(SESSION_CONTEXT);

            JavaAstContainter<? extends ASTNode> container = JavaAstSupport.createField(SESSION_CONTEXT, name,
                    NabuccoModifierType.PRIVATE);

            container.getImports().add(IMPORT_SESSION_CONTEXT);

            this.getVisitorContext().getContainerList().add(container);

            break;
        }
        }
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, MdaModel<JavaModel> target) {

        NabuccoToJavaVisitorContext context = super.getVisitorContext();

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);

            String name = nabuccoMethod.nodeToken1.tokenImage;

            String rq = this.getRequest(nabuccoMethod);
            String rs = this.getResponse(nabuccoMethod);
            String exception = this.getException(nabuccoMethod);

            // Method
            JavaAstContainter<org.eclipse.jdt.internal.compiler.ast.MethodDeclaration> operation;
            operation = NabuccoToJavaServiceVisitorSupport.createServiceOperation(name, rq, rs, exception, type);

            context.getContainerList().add(operation);

            NabuccoServiceType serviceType = NabuccoServiceType.valueOf(this.statementAnnotations);

            // ServiceHandler
            context.getContainerList().addAll(
                    NabuccoToJavaServiceVisitorSupport.createPostConstructStatements(name, serviceType));

            NabuccoToJavaServiceHandlerVisitor handlerVisitor = new NabuccoToJavaServiceHandlerVisitor(
                    new NabuccoToJavaVisitorContext(context), this.statementAnnotations);

            nabuccoMethod.accept(handlerVisitor, target);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST service modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template service processing.", te);
        }
    }

    /**
     * Resolves the service request message.
     * 
     * @param nabuccoMethod
     *            the service operation
     * 
     * @return the request
     */
    private String getRequest(MethodDeclaration nabuccoMethod) {
        String requestMsg;
        if (nabuccoMethod.parameterList.nodeListOptional.nodes.isEmpty()) {
            requestMsg = EMPTY_SERVICE_MESSAGE;
        } else {
            Parameter param = (Parameter) nabuccoMethod.parameterList.nodeListOptional.nodes.get(0);
            requestMsg = param.nodeToken.tokenImage;
        }
        return requestMsg;
    }

    /**
     * Resolves the service response message.
     * 
     * @param nabuccoMethod
     *            the service operation
     * 
     * @return the response
     */
    private String getResponse(MethodDeclaration nabuccoMethod) {
        return ((NodeToken) nabuccoMethod.nodeChoice.choice).tokenImage;
    }

    /**
     * Resolves the service exception
     * 
     * @param nabuccoMethod
     *            the service operation
     * 
     * @return the exception
     */
    private String getException(MethodDeclaration nabuccoMethod) {
        String exception = null;

        if (nabuccoMethod.nodeOptional.present()) {
            NodeSequence exceptionNode = (NodeSequence) nabuccoMethod.nodeOptional.node;
            exception = ((NodeToken) exceptionNode.nodes.get(1)).tokenImage;
        }
        return exception;
    }

}
