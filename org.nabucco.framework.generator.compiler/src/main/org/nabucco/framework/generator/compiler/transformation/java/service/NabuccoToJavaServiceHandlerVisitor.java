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

import java.text.MessageFormat;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
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
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceHandlerVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceHandlerVisitor extends NabuccoToJavaVisitorSupport implements
        ServerConstants {

    private static final String EMPTY_SERVICE_MSG_IMPORT = "org.nabucco.framework.base.facade.message.EmptyServiceMessage";

    private static final String IMPORT_VALIDATIONEXCEPTION = "org.nabucco.framework.base.facade.exception.validation.ValidationException";

    private static final String IMPORT_VALIDATIONRESULT = "org.nabucco.framework.base.facade.datatype.validation.ValidationResult";

    private static final String DEFAULT_SERVICE_EXCEPTION = "org.nabucco.framework.base.facade.exception.service.ServiceException";

    private static final JavaAstMethodSignature VALIDATE_SIGNATURE = new JavaAstMethodSignature(
            "validateRequest", SERVICE_REQUEST);

    private AnnotationDeclaration typeAnnotations;

    /**
     * Creates a new {@link NabuccoToJavaServiceHandlerVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param statementAnnotations
     *            the typeAnnotations
     */
    public NabuccoToJavaServiceHandlerVisitor(NabuccoToJavaVisitorContext visitorContext,
            AnnotationDeclaration statementAnnotations) {
        super(visitorContext);
        this.typeAnnotations = statementAnnotations;
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, MdaModel<JavaModel> target) {

        String methodName = nabuccoMethod.nodeToken1.tokenImage;
        String servicePackage = this.getVisitorContext().getPackage().replace(PKG_FACADE, PKG_IMPL);
        String handlerName = NabuccoToJavaServiceVisitorSupport.convertMethodToHandler(methodName);
        String componentName = super.getComponentName(NabuccoModelType.SERVICE,
                NabuccoModifierType.PRIVATE);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SERVICE_HANDLER_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SERVICE_HANDLER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, handlerName);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), servicePackage);

            String exception = null;

            if (nabuccoMethod.nodeOptional.present()) {
                NodeSequence exceptionNode = (NodeSequence) nabuccoMethod.nodeOptional.node;
                exception = ((NodeToken) exceptionNode.nodes.get(1)).tokenImage;
                super.removeImport(unit.getUnitDeclaration(), DEFAULT_SERVICE_EXCEPTION);
            }

            String rq = this.getRequest(nabuccoMethod, unit.getUnitDeclaration());
            String rs = this.getResponse(nabuccoMethod, unit.getUnitDeclaration());

            NabuccoToJavaServiceVisitorSupport.prepareInvokeMethod(methodName, rq, rs, exception,
                    type);

            // prepare/remvove validateRequest() method
            if (this.isValidatable(nabuccoMethod)) {
                this.prepareValidateMethod(methodName, type);
            } else {
                this.removeValidateMethod(unit.getUnitDeclaration(), type);
            }

            JavaAstContainter<org.eclipse.jdt.internal.compiler.ast.MethodDeclaration> handlerMethod;
            handlerMethod = NabuccoToJavaServiceVisitorSupport.createServiceHandlerMethod(
                    methodName, rq, rs, exception, type);

            org.eclipse.jdt.internal.compiler.ast.MethodDeclaration method = handlerMethod
                    .getAstNode();

            super.getVisitorContext().getContainerList().add(handlerMethod);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(this.typeAnnotations, type);
            JavaAstSupport.convertJavadocAnnotations(nabuccoMethod.annotationDeclaration, method);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());

            this.changeInjectionId(nabuccoMethod.annotationDeclaration, servicePackage,
                    handlerName, type);

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

    /**
     * Resolves the service request message
     * 
     * @param method
     *            the service operation
     * @param unit
     *            compilation unit to add missing imports
     * 
     * @return the request as string
     * 
     * @throws JavaModelException
     *             if the import cannot be created
     */
    private String getRequest(MethodDeclaration method, CompilationUnitDeclaration unit)
            throws JavaModelException {

        if (method.parameterList.nodeListOptional.nodes.isEmpty()) {
            ImportReference importReference = JavaAstModelProducer.getInstance()
                    .createImportReference(EMPTY_SERVICE_MSG_IMPORT);

            JavaAstElementFactory.getInstance().getJavaAstUnit().addImport(unit, importReference);
            return EMPTY_SERVICE_MSG;
        }
        Parameter param = (Parameter) method.parameterList.nodeListOptional.nodes.get(0);
        return param.nodeToken.tokenImage;
    }

    /**
     * Resolves the service response message.
     * 
     * @param method
     *            the service operation
     * @param unit
     *            compilation unit to add missing imports
     * 
     * @return the response as string
     * 
     * @throws JavaModelException
     *             if the import cannot be created
     */
    private String getResponse(MethodDeclaration method, CompilationUnitDeclaration unit)
            throws JavaModelException {
        String rs = ((NodeToken) method.nodeChoice.choice).tokenImage;

        if (rs == null || rs.equalsIgnoreCase(VOID)) {
            ImportReference importReference = JavaAstModelProducer.getInstance()
                    .createImportReference(EMPTY_SERVICE_MSG_IMPORT);

            JavaAstElementFactory.getInstance().getJavaAstUnit().addImport(unit, importReference);
            return EMPTY_SERVICE_MSG;
        }

        return rs;
    }

    /**
     * Checks whether the @Validate annotation is applied or not.
     * 
     * @param nabuccoMethod
     *            the method to check
     * 
     * @return <b>true</b> if the annotation is applied, <b>false</b> if not
     */
    private Boolean isValidatable(MethodDeclaration nabuccoMethod) {
        return NabuccoAnnotationMapper.getInstance().hasAnnotation(
                nabuccoMethod.annotationDeclaration, NabuccoAnnotationType.VALIDATABLE);
    }

    /**
     * Adjust the validateRequst() method for the given service operation name.
     * 
     * @param methodName
     *            name of the service operation
     * @param type
     *            the type containing the method
     * 
     * @throws JavaModelException
     */
    private void prepareValidateMethod(String methodName, TypeDeclaration type)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        AbstractMethodDeclaration method = javaFactory.getJavaAstType().getMethod(type,
                VALIDATE_SIGNATURE);

        // Try Block

        TryStatement tryBlock = (TryStatement) method.statements[1];
        IfStatement ifStatement = (IfStatement) tryBlock.tryBlock.statements[2];
        ThrowStatement throwStatement = (ThrowStatement) ((Block) ifStatement.thenStatement).statements[0];

        AllocationExpression exception = (AllocationExpression) throwStatement.exception;
        String message = new String(((StringLiteral) exception.arguments[0]).source());

        message = MessageFormat.format(message, methodName);
        Literal literal = producer.createLiteral(message, LiteralType.STRING_LITERAL);

        exception.arguments[0] = literal;

        // Catch Block

        throwStatement = (ThrowStatement) tryBlock.catchBlocks[0].statements[0];
        exception = (AllocationExpression) throwStatement.exception;
        exception.arguments[0] = literal;
    }

    /**
     * Remove the validateRequst() method from the given type.
     * 
     * @param unit
     *            the unit containing the imports
     * @param type
     *            the type containing the method
     * 
     * @throws JavaModelException
     */
    private void removeValidateMethod(CompilationUnitDeclaration unit, TypeDeclaration type)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        AbstractMethodDeclaration method = javaFactory.getJavaAstType().getMethod(type,
                VALIDATE_SIGNATURE);
        javaFactory.getJavaAstType().removeMethod(type, method);

        super.removeImport(unit, IMPORT_VALIDATIONRESULT);
        super.removeImport(unit, IMPORT_VALIDATIONEXCEPTION);
    }

    /**
     * Changes the static ID field
     * 
     * @param annotations
     *            the method annotations
     * @param pkg
     *            the package
     * @param name
     *            the name
     * @param type
     *            the type
     */
    private void changeInjectionId(AnnotationDeclaration annotations, String pkg, String name,
            TypeDeclaration type) {

        String id = pkg + PKG_SEPARATOR + name;

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
}
