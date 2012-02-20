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
package org.nabucco.framework.generator.compiler.transformation.java.service.delegate;

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoServiceType;
import org.nabucco.framework.generator.compiler.transformation.common.counter.ServiceOperationCounter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstUnit;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceDelegateWebVisitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class NabuccoToJavaServiceDelegateWebVisitor extends NabuccoToJavaVisitorSupport implements ViewConstants {

    private static final String FACADE_SERVICE = "facade.service";

    private static final String IMPORT_SESSION = "org.nabucco.framework.base.facade.datatype.session.NabuccoSession";

    private static final String IMPORT_DELEGATE = "org.nabucco.framework.base.ui.web.communication.ServiceDelegateSupport";

    private static final String IMPORT_SUBCONTEXT = "org.nabucco.framework.base.facade.datatype.context.ServiceSubContext";

    private static final JavaAstMethodSignature SIGNATURE_SERVICE_OPERATION = new JavaAstMethodSignature(
            "serviceDelegateOperationWebTemplate", "ServiceMsgTemplate", "NabuccoSession", "ServiceSubContext");

    private String serviceName;

    /**
     * Creates a new {@link NabuccoToJavaServiceDelegateWebVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceDelegateWebVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement serviceStatement, MdaModel<JavaModel> target) {

        // Adapter Services must not generate UI elements.
        if (NabuccoServiceType.valueOf(serviceStatement) == NabuccoServiceType.RESOURCE) {
            return;
        }

        // Services wihout operations must not generate UI elements.
        ServiceOperationCounter counter = new ServiceOperationCounter();
        serviceStatement.accept(counter);
        if (counter.isEmpty()) {
            return;
        }

        this.serviceName = serviceStatement.nodeToken2.tokenImage;

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // Visit methods first
        super.visit(serviceStatement, target);

        String delegateName = this.serviceName + DELEGATE;
        String projectName = super.getComponentName(NabuccoClientType.WEB);

        try {
            // Load Template (Common for RCP and Web)
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SERVICE_DELEGATE_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SERVICE_DELEGATE_TEMPLATE);
            javaFactory.getJavaAstType().setTypeName(type, delegateName);

            // handle service field
            TypeReference serviceType = handleServiceField(unit, type, serviceStatement);

            handleConstructor(serviceType, type);

            // set package
            String pkg = super.getVisitorContext().getPackage();
            pkg = pkg.replace(FACADE_SERVICE, UI_WEB_COMMUNICATION);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(serviceStatement.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(), super
                    .getVisitorContext().getImportList());

            // Add Web specific imports.
            this.addImports(unit.getUnitDeclaration());

            // set project
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST service modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template service processing.", te);
        }

        super.visit(serviceStatement, target);
    }

    /**
     * Adds the Web specific imports.
     * 
     * @param unit
     *            the compilation unit to add the imports to
     * 
     * @throws JavaModelException
     */
    private void addImports(CompilationUnitDeclaration unit) throws JavaModelException {
        JavaAstUnit factory = JavaAstElementFactory.getInstance().getJavaAstUnit();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        factory.addImport(unit, producer.createImportReference(IMPORT_SESSION));
        factory.addImport(unit, producer.createImportReference(IMPORT_DELEGATE));
        factory.addImport(unit, producer.createImportReference(IMPORT_SUBCONTEXT));
    }

    private void handleConstructor(TypeReference serviceType, TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        AbstractMethodDeclaration declaration = javaFactory.getJavaAstType().getConstructors(type).get(0);
        for (Argument currentArgument : javaFactory.getJavaAstMethod().getAllArguments(declaration)) {
            javaFactory.getJavaAstArgument().setType(currentArgument, serviceType);
        }
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, MdaModel<JavaModel> target) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);

            org.eclipse.jdt.internal.compiler.ast.MethodDeclaration method;

            method = (org.eclipse.jdt.internal.compiler.ast.MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                    type, SIGNATURE_SERVICE_OPERATION);

            prepareMethod(methodDeclaration, method);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST method Declaration modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template method Declaration processing.", te);
        }

        super.visit(methodDeclaration, target);
    }

    /**
     * Prepares the webDelegate operation.
     * 
     * @param methodDeclaration
     *            the NABUCCO method
     * @param method
     *            the java method
     * 
     * @throws JavaModelException
     */
    private void prepareMethod(MethodDeclaration methodDeclaration,
            org.eclipse.jdt.internal.compiler.ast.MethodDeclaration method) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        String methodName = methodDeclaration.nodeToken1.tokenImage;

        // Method serviceName
        javaFactory.getJavaAstMethod().setMethodName(method, methodName);

        JavaAstContainter<AbstractMethodDeclaration> container = new JavaAstContainter<AbstractMethodDeclaration>(
                method, JavaAstType.METHOD);

        // Set returnType
        String returnTypeName = NabuccoToJavaCommonDelegateSupport.getResponse(methodDeclaration, container);
        TypeReference returnType = producer.createTypeReference(returnTypeName, false);
        javaFactory.getJavaAstMethod().setReturnType(method, returnType);

        // Set parameter
        String paramaterTypeName;
        TypeReference parameterType;
        if (methodDeclaration.parameterList.nodeListOptional.nodes.isEmpty()) {
            paramaterTypeName = ServerConstants.EMPTY_SERVICE_MESSAGE;
            parameterType = producer.createTypeReference(paramaterTypeName, false);
        } else {
            Parameter parameter = ((Parameter) methodDeclaration.parameterList.nodeListOptional.elementAt(0));
            paramaterTypeName = parameter.nodeToken.tokenImage;
            parameterType = producer.createTypeReference(paramaterTypeName, false);
        }

        // set serviceName and type
        List<Argument> arguments = javaFactory.getJavaAstMethod().getAllArguments(method);
        if (!arguments.isEmpty()) {
            Argument argument = arguments.get(0);
            javaFactory.getJavaAstArgument().setType(argument, parameterType);
        }

        String exceptionTypeName = null;
        if (methodDeclaration.nodeOptional.present()) {
            NodeSequence exceptionNode = (NodeSequence) methodDeclaration.nodeOptional.node;
            exceptionTypeName = ((NodeToken) exceptionNode.nodes.get(1)).tokenImage;
            TypeReference exceptionType = producer.createTypeReference(exceptionTypeName, false);
            javaFactory.getJavaAstMethod().setException(method, exceptionType);
        }

        // Rq creation
        LocalDeclaration requestCreation = (LocalDeclaration) method.statements[0];

        ((ParameterizedSingleTypeReference) requestCreation.type).typeArguments[0] = producer.createTypeReference(
                paramaterTypeName, false);

        ((ParameterizedSingleTypeReference) ((AllocationExpression) requestCreation.initialization).type).typeArguments[0] = producer
                .createTypeReference(paramaterTypeName, false);

        // Service Response
        LocalDeclaration responseCreation = (LocalDeclaration) method.statements[2];
        ((ParameterizedSingleTypeReference) responseCreation.type).typeArguments[0] = producer.createTypeReference(
                returnTypeName, false);

        IfStatement ifStatement = (IfStatement) method.statements[4];
        Block then = (Block) ifStatement.thenStatement;
        TryStatement tryStatement = (TryStatement) then.statements[2];

        // Service Call
        Assignment operationCall = (Assignment) tryStatement.tryBlock.statements[0];
        javaFactory.getJavaAstMethodCall().setMethodName(methodName, (MessageSend) operationCall.expression);

        // Monitor
        MessageSend monitorResult = (MessageSend) tryStatement.finallyBlock.statements[2];
        ClassLiteralAccess serviceClass = (ClassLiteralAccess) monitorResult.arguments[0];
        serviceClass.type = producer.createTypeReference(this.serviceName, false);
        monitorResult.arguments[1] = producer.createLiteral(methodName, LiteralType.STRING_LITERAL);

        // Throw Statement
        ThrowStatement throwStatement = (ThrowStatement) method.statements[5];

        AllocationExpression exception = ((AllocationExpression) throwStatement.exception);
        exception.type = producer.createTypeReference(exceptionTypeName, false);
        exception.arguments[0] = producer.createLiteral("Cannot execute service operation: "
                + this.serviceName + PKG_SEPARATOR + methodName, LiteralType.STRING_LITERAL);

        if (methodDeclaration.parameterList.nodeListOptional.nodes.isEmpty()) {
            paramaterTypeName = ServerConstants.IMPORT_EMPTY_SERVICE_MESSAGE;
        }

        container.getImports().add(returnTypeName);
        container.getImports().add(paramaterTypeName);

        if (exceptionTypeName != null) {
            container.getImports().add(exceptionTypeName);
        }

        super.getVisitorContext().getContainerList().add(container);
    }

    /**
     * Handles the service field generation.
     * 
     * @param unit
     *            the java unit
     * @param type
     *            the java field type
     * @param serviceStatement
     *            the service statement
     * 
     * @return the type reference for the field
     * 
     * @throws JavaModelException
     */
    private TypeReference handleServiceField(JavaCompilationUnit unit, TypeDeclaration type,
            ServiceStatement serviceStatement) throws JavaModelException {

        // get service field
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration fieldDeclaration = javaFactory.getJavaAstType().getField(type,
                NabuccoTransformationUtility.firstToLower(SERVICE));

        // set field type
        TypeReference serviceTypeReference = JavaAstModelProducer.getInstance().createTypeReference(
                serviceStatement.nodeToken2.tokenImage, false);
        javaFactory.getJavaAstField().setFieldType(fieldDeclaration, serviceTypeReference);

        ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                super.getVisitorContext().getPackage() + PKG_SEPARATOR + serviceStatement.nodeToken2.tokenImage);
        javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

        return serviceTypeReference;
    }
}
