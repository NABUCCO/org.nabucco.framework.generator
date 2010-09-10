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

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierTypeMapper;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.CustomDeclaration;
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
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceImplementationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceImplementationVisitor extends NabuccoToJavaVisitorSupport implements
        ServerConstants {

    private String entityManager;

    private AnnotationDeclaration statementAnnotations;

    /**
     * Creates a new {@link NabuccoToJavaServiceImplementationVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceImplementationVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<JavaModel> target) {

        this.statementAnnotations = nabuccoService.annotationDeclaration;

        // Visit sub-nodes first!
        super.visit(nabuccoService, target);

        String interfaceName = nabuccoService.nodeToken2.tokenImage;

        String interfacePackage = this.getVisitorContext().getPackage();
        String name = interfaceName + IMPLEMENTATION;
        String componentName = super.getComponentName(NabuccoModelType.SERVICE,
                NabuccoModifierType.PRIVATE);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SERVICE_IMPLEMENTATION_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SERVICE_IMPLEMENTATION_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    interfacePackage.replace(PKG_FACADE, PKG_IMPL));

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(this.statementAnnotations, type);

            // Interfaces
            super.createSuperClass();
            super.createInterface(interfaceName);

            // PostConstruct
            NabuccoToJavaServiceVisitorSupport.preparePostConstruct(type);

            // PreDestroy
            NabuccoToJavaServiceVisitorSupport.preparePreDestroy(type);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoService.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(),
                    super.getVisitorContext().getImportList());

            this.changeInjectionId(this.statementAnnotations, interfaceName, type);

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
    public void visit(CustomDeclaration nabuccoCustom, MdaModel<JavaModel> target) {
        String type = nabuccoCustom.nodeToken.tokenImage;
        String name = nabuccoCustom.nodeToken2.tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoCustom.nodeToken1.tokenImage);

        NabuccoModifierType modifier = NabuccoModifierTypeMapper.getInstance().mapToModifier(
                ((NodeToken) nabuccoCustom.nodeChoice.choice).tokenImage);

        boolean isList = multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY;

        JavaAstContainter<? extends ASTNode> container = JavaAstSupport.createField(type, name,
                modifier, isList);

        if (NabuccoToJavaServiceReferences.isServiceReference(type)) {
            container.getImports().add(NabuccoToJavaServiceReferences.getServiceReference(type));
        }

        this.entityManager = name;

        this.getVisitorContext().getContainerList().add(container);
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, MdaModel<JavaModel> target) {

        NabuccoToJavaVisitorContext context = super.getVisitorContext();

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

            // Method
            JavaAstContainter<org.eclipse.jdt.internal.compiler.ast.MethodDeclaration> operation;
            operation = NabuccoToJavaServiceVisitorSupport.createServiceOperation(name, rq, rs,
                    exception, type);

            context.getContainerList().add(operation);

            // ServiceHandler
            context.getContainerList().addAll(
                    NabuccoToJavaServiceVisitorSupport.createPostConstructStatements(name,
                            this.entityManager));

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
     * Change the injection id of the service implementation.
     * 
     * @param annotations
     *            the annotations
     * @param name
     *            the service name
     * @param type
     *            the type to change
     */
    private void changeInjectionId(AnnotationDeclaration annotations, String name,
            TypeDeclaration type) {

        String id = name;

        try {

            NabuccoAnnotation injectionId;
            injectionId = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations,
                    NabuccoAnnotationType.INJECTION_ID);

            if (injectionId != null) {
                id = injectionId.getValue();
            }

            FieldDeclaration field = JavaAstElementFactory.getInstance().getJavaAstType()
                    .getField(type, INJECTION_ID);

            field.initialization = JavaAstModelProducer.getInstance().createLiteral(id,
                    LiteralType.STRING_LITERAL);

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error changing injection ID.", me);
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
