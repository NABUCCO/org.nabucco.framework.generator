/*
* Copyright 2010 PRODYNA AG
*
* Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.opensource.org/licenses/eclipse-1.0.php or
* http://www.nabucco-source.org/nabucco-license.html
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.nabucco.framework.generator.compiler.transformation.java.service;

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceInterfaceVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceInterfaceVisitor extends NabuccoToJavaVisitorSupport implements
        ServerConstants {

    /**
     * Creates a new {@link NabuccoToJavaServiceInterfaceVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceInterfaceVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoService, target);

        String name = nabuccoService.nodeToken2.tokenImage;
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String componentName = super
                .getProjectName(NabuccoModelType.SERVICE, NabuccoModifierComponentMapper
                        .getModifierType(nabuccoService.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SERVICE_INTERFACE_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SERVICE_INTERFACE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    super.getVisitorContext().getPackage());

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoService.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());

            // File creation
            unit.setProjectName(componentName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST service modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template service processing.", te);
        }
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, MdaModel<JavaModel> target) {

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);

            String name = nabuccoMethod.nodeToken1.tokenImage;

            String rq = this.getRequest(nabuccoMethod);
            String rs = this.getResponse(nabuccoMethod);
            String exception = this.getException(nabuccoMethod);

            JavaAstContainter<org.eclipse.jdt.internal.compiler.ast.MethodDeclaration> javaMethod = NabuccoToJavaServiceVisitorSupport
                    .createServiceInterfaceOperation(name, rq, rs, exception, type);
            
            JavaAstSupport.convertJavadocAnnotations(nabuccoMethod.annotationDeclaration,
                    javaMethod.getAstNode());
            
            this.getVisitorContext().getContainerList().add(javaMethod);

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
            requestMsg = EMPTY_SERVICE_MSG;
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
