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

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.NodeListOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceDefaultVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceDefaultVisitor extends NabuccoToJavaVisitorSupport {

    private static final String NEW_SERVICE_MESSAGE_CONTEXT = "newServiceMessageContext";

    private static final String SERVICE_CONTEXT_FACTORY = "ServiceContextFactory";

    private static final String DEFAULT_IMPL = "DefaultImpl";

    private static final String RS = "rs";

    private static final String RQ = "rq";

    /**
     * Creates a new {@link NabuccoToJavaServiceDefaultVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceDefaultVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoService, target);

        String interfaceName = nabuccoService.nodeToken2.tokenImage;
        String implName = interfaceName + DEFAULT_IMPL;
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String projectName = super.getProjectName(NabuccoModelType.SERVICE,
                NabuccoModifierComponentMapper.getModifierType(nabuccoService.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SERVICE_DEFAULT_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SERVICE_DEFAULT_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, implName);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), super.getVisitorContext().getPackage());

            super.createInterface(interfaceName);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoService.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST service modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template service processing.", te);
        }
    }

    @Override
    public void visit(org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration nabuccoMethod,
            MdaModel<JavaModel> target) {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String operationName = nabuccoMethod.nodeToken1.tokenImage;
        String response = ((NodeToken) nabuccoMethod.nodeChoice.choice).tokenImage;
        String request = null;

        NodeListOptional parameters = nabuccoMethod.parameterList.nodeListOptional;
        if (parameters.present()) {
            Parameter parameter = (Parameter) parameters.elementAt(0);
            request = parameter.nodeToken.tokenImage;
        }

        try {
            MethodDeclaration operation = producer.createMethodDeclaration(operationName, null, false);

            JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(operation,
                    JavaAstType.METHOD);

            // Return Type
            TypeReference responseType;
            if (response.equals(VOID)) {
                responseType = producer.createTypeReference(ServerConstants.EMPTY_SERVICE_MESSAGE, false);
                container.getImports().add(ServerConstants.IMPORT_EMPTY_SERVICE_MESSAGE);
            } else {
                responseType = producer.createTypeReference(response, false);
                container.getImports().add(super.resolveImport(response));
            }

            TypeReference rs = producer.createParameterizedTypeReference(ServerConstants.SERVICE_RESPONSE, false,
                    Arrays.asList(responseType));
            javaFactory.getJavaAstMethod().setReturnType(operation, rs);

            // Arguments
            TypeReference requestType;
            if (request != null) {
                requestType = producer.createTypeReference(request, false);
                container.getImports().add(super.resolveImport(request));
            } else {
                requestType = producer.createTypeReference(ServerConstants.EMPTY_SERVICE_MESSAGE, false);
                container.getImports().add(ServerConstants.IMPORT_EMPTY_SERVICE_MESSAGE);
            }

            TypeReference rq = producer.createParameterizedTypeReference(ServerConstants.SERVICE_REQUEST, false,
                    Arrays.asList(requestType));

            Argument argument = producer.createArgument(RQ, rq);
            javaFactory.getJavaAstMethod().addArgument(operation, argument);

            this.getVisitorContext().getContainerList().add(container);

            // Body
            SingleNameReference receiver = producer.createSingleNameReference(SERVICE_CONTEXT_FACTORY);
            MessageSend getInstance = producer.createMessageSend(ServerConstants.SINGLETON_GETTER, receiver, null);
            MessageSend newContext = producer.createMessageSend(NEW_SERVICE_MESSAGE_CONTEXT, getInstance, null);
            LocalDeclaration localDeclaration = producer.createLocalDeclaration(rs, RS);
            localDeclaration.initialization = producer.createAllocationExpression(rs, Arrays.asList(newContext));

            SingleNameReference localVariable = producer.createSingleNameReference(RS);
            ReturnStatement returnStatement = producer.createReturnStatement(localVariable);

            AllocationExpression messageAllocation = producer.createAllocationExpression(responseType, null);
            MessageSend setter = producer.createMessageSend(ServerConstants.RS_MSG_SETTER, localVariable,
                    Arrays.asList(messageAllocation));

            operation.statements = new Statement[] { localDeclaration, setter, returnStatement };

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error creating default service implementation.", me);
        }
    }
}
