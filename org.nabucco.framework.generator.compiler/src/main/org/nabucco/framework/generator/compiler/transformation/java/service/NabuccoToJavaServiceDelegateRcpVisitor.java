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

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
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
 * NabuccoToJavaServiceDelegateRcpVisitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
class NabuccoToJavaServiceDelegateRcpVisitor extends NabuccoToJavaVisitorSupport implements
        ViewConstants {

    private static final String EMPTY_SERVICE_MSG = "EmptyServiceMessage";

    private static final String FACADE_SERVICE = "facade.service";

    private static final String IMPORT_ACTIVATOR = "org.nabucco.framework.plugin.base.Activator";

    private static final String IMPORT_LOGMSG = "org.nabucco.framework.plugin.base.logging.NabuccoLogMessage";

    private static final String IMPORT_DELEGATE = "org.nabucco.framework.plugin.base.component.communication.ServiceDelegateSupport";

    private static final String IMPORT_EMPTY_SERVICE_MSG = "org.nabucco.framework.base.facade.message.EmptyServiceMessage";

    private static final JavaAstMethodSignature SERVICE_OPERATION_SIGNATURE = new JavaAstMethodSignature(
            "serviceDelegateOperationRcpTemplate", "ServiceMsgTemplate");

    private String serviceName;

    public NabuccoToJavaServiceDelegateRcpVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement serviceStatement, MdaModel<JavaModel> target) {

        this.serviceName = serviceStatement.nodeToken2.tokenImage;

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // Visit methods first
        super.visit(serviceStatement, target);

        String delegateName = this.serviceName + DELEGATE;
        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SERVICE_DELEGATE_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SERVICE_DELEGATE_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, delegateName);

            // handle service field
            TypeReference serviceType = handleServiceField(unit, type, serviceStatement);

            handleConstructor(serviceType, type);

            // set package
            String pkg = super.getVisitorContext().getPackage();
            pkg = pkg.replace(FACADE_SERVICE, UI_RCP_COMMUNICATION);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(serviceStatement.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(),
                    super.getVisitorContext().getImportList());

            // Add the missing RCP imports
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
     * Adds the RCP specific imports.
     * 
     * @param unit
     *            the compilation unit to add the imports to
     * 
     * @throws JavaModelException
     */
    private void addImports(CompilationUnitDeclaration unit) throws JavaModelException {
        JavaAstUnit factory = JavaAstElementFactory.getInstance().getJavaAstUnit();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        factory.addImport(unit, producer.createImportReference(IMPORT_ACTIVATOR));
        factory.addImport(unit, producer.createImportReference(IMPORT_LOGMSG));
        factory.addImport(unit, producer.createImportReference(IMPORT_DELEGATE));
    }

    private void handleConstructor(TypeReference serviceType, TypeDeclaration type)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        AbstractMethodDeclaration declaration = javaFactory.getJavaAstType().getConstructors(type)
                .get(0);
        for (Argument currentArgument : javaFactory.getJavaAstMethod().getAllArguments(declaration)) {
            javaFactory.getJavaAstArgument().setType(currentArgument, serviceType);

        }
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, MdaModel<JavaModel> target) {

        try {
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);
            AbstractMethodDeclaration method = javaFactory.getJavaAstType().getMethod(type,
                    SERVICE_OPERATION_SIGNATURE);

            JavaAstContainter<AbstractMethodDeclaration> container = new JavaAstContainter<AbstractMethodDeclaration>(
                    method, JavaAstType.METHOD);

            // Method serviceName
            javaFactory.getJavaAstMethod().setMethodName(method,
                    methodDeclaration.nodeToken1.tokenImage);

            // Set returnType
            String rsMsg = this.getResponse(methodDeclaration, container);
            TypeReference rsType = producer.createTypeReference(rsMsg, false);
            javaFactory.getJavaAstMethod().setReturnType(
                    (org.eclipse.jdt.internal.compiler.ast.MethodDeclaration) method, rsType);

            // Set parameter
            String rqMsg = this.getRequest(methodDeclaration, container);
            TypeReference parameterType = producer.createTypeReference(rqMsg, false);

            // set serviceName and type
            for (Argument currentArgument : javaFactory.getJavaAstMethod().getAllArguments(method)) {
                javaFactory.getJavaAstArgument().setType(currentArgument, parameterType);

            }

            String exceptionTypeName = null;
            if (methodDeclaration.nodeOptional.present()) {
                NodeSequence exceptionNode = (NodeSequence) methodDeclaration.nodeOptional.node;
                exceptionTypeName = ((NodeToken) exceptionNode.nodes.get(1)).tokenImage;
                TypeReference exceptionType = producer
                        .createTypeReference(exceptionTypeName, false);
                javaFactory.getJavaAstMethod().setException(
                        (org.eclipse.jdt.internal.compiler.ast.MethodDeclaration) method,
                        exceptionType);
            }

            // Rq creation
            LocalDeclaration requestCreation = (LocalDeclaration) method.statements[0];

            ((ParameterizedSingleTypeReference) requestCreation.type).typeArguments[0] = producer
                    .createTypeReference(rqMsg, false);

            ((ParameterizedSingleTypeReference) ((AllocationExpression) requestCreation.initialization).type).typeArguments[0] = producer
                    .createTypeReference(rqMsg, false);

            // Rs field definition
            LocalDeclaration responseCreation = (LocalDeclaration) method.statements[2];
            ((ParameterizedSingleTypeReference) responseCreation.type).typeArguments[0] = producer
                    .createTypeReference(rsMsg, false);

            // start of the IF content
            // calling the operation on the actual service
            Statement[] statementsInIf = ((Block) ((IfStatement) method.statements[3]).thenStatement).statements;
            Assignment operationCall = (Assignment) statementsInIf[1];
            ((MessageSend) operationCall.expression).selector = methodDeclaration.nodeToken1.tokenImage
                    .toCharArray();

            // Setting the Classliteral
            Expression[] expressions = ((AllocationExpression) ((MessageSend) statementsInIf[3]).arguments[0]).arguments;
            ((ClassLiteralAccess) expressions[0]).type = producer.createTypeReference(
                    this.serviceName + DELEGATE, false);
            // Creating the String containing Service.serivceOperation
            expressions[2] = producer.createLiteral(this.serviceName
                    + PKG_SEPARATOR + methodDeclaration.nodeToken1.tokenImage,
                    LiteralType.STRING_LITERAL);

            Statement[] statementsInIfElse = ((Block) ((IfStatement) method.statements[3]).elseStatement).statements;
            AllocationExpression exceptionAllocation = ((AllocationExpression) ((ThrowStatement) statementsInIfElse[0]).exception);
            exceptionAllocation.type = producer.createTypeReference(exceptionTypeName, false);
            exceptionAllocation.arguments[0] = producer.createLiteral(
                    "Cannot execute service operation: "
                            + this.serviceName + PKG_SEPARATOR
                            + methodDeclaration.nodeToken1.tokenImage, LiteralType.STRING_LITERAL);

            container.getImports().add(rsMsg);
            container.getImports().add(rqMsg);
            if (exceptionTypeName != null) {
                container.getImports().add(exceptionTypeName);
            }

            super.getVisitorContext().getContainerList().add(container);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException(
                    "Error during Java AST method Declaration modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template method Declaration processing.", te);
        }
        super.visit(methodDeclaration, target);
    }

    /**
     * Resolves the service request message
     * 
     * @param method
     *            the service operation
     * @param container
     *            the method container
     * 
     * @return the request as string
     * 
     * @throws JavaModelException
     *             if the import cannot be created
     */
    private String getRequest(MethodDeclaration method,
            JavaAstContainter<AbstractMethodDeclaration> container) throws JavaModelException {
        if (method.parameterList.nodeListOptional.nodes.isEmpty()) {
            container.getImports().add(IMPORT_EMPTY_SERVICE_MSG);
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
     * @param container
     *            the method container
     * 
     * @return the response as string
     * 
     * @throws JavaModelException
     *             if the import cannot be created
     */
    private String getResponse(MethodDeclaration method,
            JavaAstContainter<AbstractMethodDeclaration> container) throws JavaModelException {
        String rs = ((NodeToken) method.nodeChoice.choice).tokenImage;
        if (rs == null || rs.equalsIgnoreCase(VOID)) {
            container.getImports().add(IMPORT_EMPTY_SERVICE_MSG);
            return EMPTY_SERVICE_MSG;
        }
        return rs;
    }

    private TypeReference handleServiceField(JavaCompilationUnit unit, TypeDeclaration type,
            ServiceStatement serviceStatement) throws JavaModelException {
        // get service field
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration fieldDeclaration = javaFactory.getJavaAstType().getField(type, "service");

        // set field type
        TypeReference serviceTypeReference = JavaAstModelProducer.getInstance()
                .createTypeReference(serviceStatement.nodeToken2.tokenImage, false);
        javaFactory.getJavaAstField().setFieldType(fieldDeclaration, serviceTypeReference);

        ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                super.getVisitorContext().getPackage()
                        + PKG_SEPARATOR + serviceStatement.nodeToken2.tokenImage);
        javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

        return serviceTypeReference;
    }
}
