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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoServiceType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstMethodStatementContainer;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.common.javadoc.NabuccoToJavaJavadocCreator;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethod;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaServiceVisitorSupport
 * <p/>
 * Utility class for NabuccoToJavaServiceTransformation and NabuccoToJavaServiceVisitor.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceVisitorSupport implements ServerConstants {

    private static final String METHOD_CREATE_PM = "createPersistenceManager";

    private static final String METHOD_CREATE_RM = "createResourceManager";

    private static final String METHOD_SET_PM = "setPersistenceManager";

    private static final String METHOD_SET_RM = "setResourceManager";

    private static final String METHOD_SET_LOGGER = "setLogger";

    private static final String METHOD_GET_LOGGER = "getLogger";

    private static final String INJECTOR_METHOD = "inject";

    private static final String INJECTOR_NAME = "injector";

    private static final String INJECTOR_ID = "getId";

    private static final String JAVADOC_INVOKE = "Invokes the service handler method.";

    private static final JavaAstMethodSignature SERVICE_INTERFACE_SIGNATURE = new JavaAstMethodSignature(
            "serviceInterfaceOperation", SERVICE_REQUEST);

    private static final JavaAstMethodSignature SERVICE_IMPL_SIGNATURE = new JavaAstMethodSignature(
            "serviceImplementationOperation", SERVICE_REQUEST);

    private static final JavaAstMethodSignature HANDLER_SIGNATURE = new JavaAstMethodSignature(
            "serviceHandlerOperation", SERVICE_MESSAGE);

    private static final JavaAstMethodSignature HANDLER_INVOKE_SIGNATURE = new JavaAstMethodSignature("invoke",
            SERVICE_REQUEST);

    private static final JavaAstMethodSignature POST_CONSTRUCT_SIGNATURE = new JavaAstMethodSignature("postConstruct");

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoToJavaServiceVisitorSupport() {
    }

    /**
     * Creates a service operation for service interfaces with the default exception.
     * 
     * @param name
     *            name of the operation
     * @param requestMsg
     *            name of the request message
     * @param responseMsg
     *            name of the response message
     * @param type
     *            the service operation template
     * 
     * @return an {@link JavaAstContainter} with all necessary method information
     * 
     * @throws JavaModelException
     */
    public static JavaAstContainter<MethodDeclaration> createServiceInterfaceOperation(String name, String requestMsg,
            String responseMsg, TypeDeclaration type) throws JavaModelException {
        return createServiceInterfaceOperation(name, requestMsg, responseMsg, null, type);
    }

    /**
     * Creates a service operation for service interfaces.
     * 
     * @param name
     *            name of the operation
     * @param requestMsg
     *            name of the request message
     * @param responseMsg
     *            name of the response message
     * @param exception
     *            name of the exception
     * @param type
     *            the service operation template
     * 
     * @return an {@link JavaAstContainter} with all necessary method information
     * 
     * @throws JavaModelException
     */
    public static JavaAstContainter<MethodDeclaration> createServiceInterfaceOperation(String name, String requestMsg,
            String responseMsg, String exception, TypeDeclaration type) throws JavaModelException {

        // Extract method
        MethodDeclaration method = createServiceOperation(type, SERVICE_INTERFACE_SIGNATURE);

        JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(method,
                JavaAstType.METHOD);

        // Name and modifier
        JavaAstMethod methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethod();
        methodFactory.setMethodName(method, name);

        // ServiceResponse
        ParameterizedSingleTypeReference serviceResponse = (ParameterizedSingleTypeReference) methodFactory
                .getReturnType(method);

        if (responseMsg == null || responseMsg.equalsIgnoreCase(VOID)) {
            responseMsg = EMPTY_SERVICE_MESSAGE;
            container.getImports().add(IMPORT_EMPTY_SERVICE_MESSAGE);
        }

        serviceResponse.typeArguments = new TypeReference[] { JavaAstModelProducer.getInstance().createTypeReference(
                responseMsg, false) };

        // ServiceRequest
        ParameterizedSingleTypeReference serviceRequest = (ParameterizedSingleTypeReference) JavaAstElementFactory
                .getInstance().getJavaAstArgument().getType(methodFactory.getAllArguments(method).get(0));

        if (requestMsg == null || requestMsg.equals(EMPTY) || requestMsg.equals(EMPTY_SERVICE_MESSAGE)) {
            requestMsg = EMPTY_SERVICE_MESSAGE;
            container.getImports().add(IMPORT_EMPTY_SERVICE_MESSAGE);
        }

        serviceRequest.typeArguments = new TypeReference[] { JavaAstModelProducer.getInstance().createTypeReference(
                requestMsg, false) };

        createException(exception, method, container);

        container.getImports().add(requestMsg);
        container.getImports().add(responseMsg);

        return container;
    }

    /**
     * Creates a service operation for service implementations with the default exception.
     * 
     * @param name
     *            name of the operation
     * @param requestMsg
     *            name of the request message
     * @param responseMsg
     *            name of the response message
     * @param type
     *            the service operation template
     * 
     * @return an {@link JavaAstContainter} with all necessary method information
     * 
     * @throws JavaModelException
     */
    public static JavaAstContainter<MethodDeclaration> createServiceOperation(String name, String requestMsg,
            String responseMsg, TypeDeclaration type) throws JavaModelException {
        return createServiceOperation(name, requestMsg, responseMsg, null, type);
    }

    /**
     * Creates a service operation for service implementations.
     * 
     * @param name
     *            name of the operation
     * @param rq
     *            name of the request message
     * @param rs
     *            name of the response message
     * @param exception
     *            name of the exception
     * @param type
     *            the service operation template
     * 
     * @return an {@link JavaAstContainter} with all necessary method information
     * 
     * @throws JavaModelException
     */
    public static JavaAstContainter<MethodDeclaration> createServiceOperation(String name, String rq, String rs,
            String exception, TypeDeclaration type) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstMethod methodFactory = javaFactory.getJavaAstMethod();

        // Extract method
        MethodDeclaration method = createServiceOperation(type, SERVICE_IMPL_SIGNATURE);

        JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(method,
                JavaAstType.METHOD);

        // Name and modifier
        methodFactory.setMethodName(method, name);
        methodFactory.setModifier(method,
                NabuccoModifierComponentMapper.mapModifierToJava(NabuccoModifierType.PUBLIC, false));

        // ServiceResponse
        ParameterizedSingleTypeReference serviceResponse = (ParameterizedSingleTypeReference) methodFactory
                .getReturnType(method);

        if (rs == null || rs.equalsIgnoreCase(VOID)) {
            rs = EMPTY_SERVICE_MESSAGE;
            container.getImports().add(IMPORT_EMPTY_SERVICE_MESSAGE);
        }

        TypeReference rsType = producer.createTypeReference(rs, false);

        serviceResponse.typeArguments = new TypeReference[] { rsType };

        // ServiceRequest
        ParameterizedSingleTypeReference serviceRequest = (ParameterizedSingleTypeReference) javaFactory
                .getJavaAstArgument().getType(methodFactory.getAllArguments(method).get(0));

        if (rq == null || rq.equals(EMPTY) || rq.equals(EMPTY_SERVICE_MESSAGE)) {
            rq = EMPTY_SERVICE_MESSAGE;
            container.getImports().add(IMPORT_EMPTY_SERVICE_MESSAGE);
        }

        serviceRequest.typeArguments = new TypeReference[] { producer.createTypeReference(rq, false) };

        createException(exception, method, container);
        createServiceOperationBody(method, rsType);

        container.getImports().add(rq);
        container.getImports().add(rs);

        return container;
    }

    /**
     * Creates a service handler operation for the abstract service handler.
     * 
     * @param name
     *            name of the operation
     * @param requestMsg
     *            name of the request message
     * @param responseMsg
     *            name of the response message
     * @param exception
     *            name of the exception
     * @param type
     *            the service operation template
     * 
     * @return an {@link JavaAstContainter} with all necessary method information
     * 
     * @throws JavaModelException
     */
    public static JavaAstContainter<MethodDeclaration> createServiceHandlerMethod(String name, String requestMsg,
            String responseMsg, String exception, TypeDeclaration type) throws JavaModelException {

        MethodDeclaration method = (MethodDeclaration) JavaAstElementFactory.getInstance().getJavaAstType()
                .getMethod(type, HANDLER_SIGNATURE);

        JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(method,
                JavaAstType.METHOD);
        JavaAstMethod methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethod();
        methodFactory.setMethodName(method, name);

        if (responseMsg == null || responseMsg.equalsIgnoreCase(VOID)) {
            responseMsg = EMPTY_SERVICE_MESSAGE;
            container.getImports().add(IMPORT_EMPTY_SERVICE_MESSAGE);
        }

        methodFactory.setReturnType(method, JavaAstModelProducer.getInstance().createTypeReference(responseMsg, false));

        if (requestMsg == null || requestMsg.equals(EMPTY) || requestMsg.equals(EMPTY_SERVICE_MESSAGE)) {
            requestMsg = EMPTY_SERVICE_MESSAGE;
            container.getImports().add(IMPORT_EMPTY_SERVICE_MESSAGE);
        }

        JavaAstElementFactory
                .getInstance()
                .getJavaAstArgument()
                .setType(methodFactory.getAllArguments(method).get(0),
                        JavaAstModelProducer.getInstance().createTypeReference(requestMsg, false));

        createException(exception, method, container);

        container.getImports().add(requestMsg);
        container.getImports().add(responseMsg);

        // Remove old method from handler.
        JavaAstElementFactory.getInstance().getJavaAstType().removeMethod(type, method);

        return container;
    }

    /**
     * Creates references of service handlers within service implementations.
     * 
     * @param serviceOperation
     *            name of the operation
     * @param serviceType
     *            type of the service
     * 
     * @return the list of ast elements
     * 
     * @throws JavaModelException
     */
    public static List<JavaAstContainter<?>> createPostConstructStatements(String serviceOperation,
            NabuccoServiceType serviceType) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        String handlerType = convertMethodToHandler(serviceOperation);
        String handlerName = NabuccoTransformationUtility.firstToLower(handlerType);

        NameReference receiver = producer.createSingleNameReference(handlerType);
        FieldReference handler = producer.createFieldThisReference(handlerName);

        List<JavaAstContainter<?>> containers = new ArrayList<JavaAstContainter<?>>();

        containers.add(JavaAstSupport.createField(handlerType, handlerName, NabuccoModifierType.PRIVATE));

        // Injection
        SingleNameReference injector = producer.createSingleNameReference(INJECTOR_NAME);
        MessageSend argument = producer.createMessageSend(INJECTOR_ID, receiver, null);
        MessageSend injectCall = producer.createMessageSend(INJECTOR_METHOD, injector, Arrays.asList(argument));
        Assignment injection = producer.createAssignment(handler, injectCall);

        containers.add(new JavaAstMethodStatementContainer<Statement>(injection, POST_CONSTRUCT_SIGNATURE));

        IfStatement ifStatement = createHandlerInitialization(handler, serviceType);

        containers.add(new JavaAstMethodStatementContainer<Statement>(ifStatement, POST_CONSTRUCT_SIGNATURE));

        return containers;
    }

    private static IfStatement createHandlerInitialization(FieldReference handler, NabuccoServiceType serviceType)
            throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        Literal nullLiteral = producer.createLiteral(null, LiteralType.NULL_LITERAL);

        BinaryExpression condition = producer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION, handler,
                nullLiteral, BinaryExpression.NOT_EQUAL);

        List<Statement> blockStatements = new ArrayList<Statement>();

        switch (serviceType) {
        case PERSISTENCE: {
            String persistenceManager = NabuccoTransformationUtility.firstToLower(PERSISTENCE_MANAGER);
            SingleNameReference pm = producer.createSingleNameReference(persistenceManager);
            MessageSend setPersistenceManager = producer.createMessageSend(METHOD_SET_PM, handler, Arrays.asList(pm));

            blockStatements.add(setPersistenceManager);
            break;
        }

        case RESOURCE: {
            String resourceManager = NabuccoTransformationUtility.firstToLower(RESOURCE_MANAGER);
            SingleNameReference rm = producer.createSingleNameReference(resourceManager);
            MessageSend setResourceManager = producer.createMessageSend(METHOD_SET_RM, handler, Arrays.asList(rm));

            blockStatements.add(setResourceManager);

            break;
        }
        }

        // Logger

        SuperReference superReference = producer.createSuperReference();
        MessageSend getLogger = producer.createMessageSend(METHOD_GET_LOGGER, superReference, null);
        blockStatements.add(producer.createMessageSend(METHOD_SET_LOGGER, handler, Arrays.asList(getLogger)));

        Block thenStatement = producer.createBlock(blockStatements.toArray(new Statement[blockStatements.size()]));

        return producer.createIfStatement(condition, thenStatement);
    }

    /**
     * Prepares the <code>invoke</code> method of the abstract service handler.
     * 
     * @param methodName
     *            name of the method to call
     * @param requestMsg
     *            the request message
     * @param responseMsg
     *            the response message
     * @param exception
     *            the exception
     * @param type
     *            the type declaration to modify
     */
    public static void prepareInvokeMethod(String methodName, String requestMsg, String responseMsg, String exception,
            TypeDeclaration type) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                HANDLER_INVOKE_SIGNATURE);

        TypeReference rqType = producer.createTypeReference(requestMsg, false);
        TypeReference rsType = producer.createTypeReference(responseMsg, false);
        TypeReference exType = producer.createTypeReference(exception, false);

        ParameterizedSingleTypeReference argument = (ParameterizedSingleTypeReference) javaFactory.getJavaAstArgument()
                .getType(method.arguments[0]);

        ParameterizedSingleTypeReference returnType = (ParameterizedSingleTypeReference) javaFactory.getJavaAstMethod()
                .getReturnType(method);

        argument.typeArguments[0] = rqType;
        returnType.typeArguments[0] = rsType;

        javaFactory.getJavaAstMethod().setException(method, exType);

        // 1. Statement

        LocalDeclaration responseDeclaration = (LocalDeclaration) method.statements[0];
        ((ParameterizedSingleTypeReference) responseDeclaration.type).typeArguments[0] = rsType;

        // 2. Statement

        LocalDeclaration msgDeclaration = (LocalDeclaration) method.statements[1];
        msgDeclaration.type = rsType;

        // Try Block
        TryStatement tryBlock = (TryStatement) method.statements[2];

        // 3. Try Statement

        Assignment msgAssignment = (Assignment) tryBlock.tryBlock.statements[2];
        MessageSend concreteMethodCall = (MessageSend) msgAssignment.expression;
        javaFactory.getJavaAstMethodCall().setMethodName(methodName, concreteMethodCall);

        // 3. Try Statement
        Assignment rsAssignment = (Assignment) tryBlock.tryBlock.statements[4];
        AllocationExpression rsAllocation = (AllocationExpression) rsAssignment.expression;
        rsAllocation.type = returnType;

        // Catch Block
        javaFactory.getJavaAstArgument().setType(tryBlock.catchArguments[0], exType);

        LocalDeclaration wrappedException = (LocalDeclaration) tryBlock.catchBlocks[1].statements[1];
        wrappedException.type = exType;
        ((AllocationExpression) wrappedException.initialization).type = exType;

        ThrowStatement throwStatement = (ThrowStatement) tryBlock.catchBlocks[2].statements[1];
        ((AllocationExpression) throwStatement.exception).type = exType;

        NabuccoToJavaJavadocCreator.createJavadoc(JAVADOC_INVOKE, method);
    }

    /**
     * Prepares the post construct method of the service implementation.
     * 
     * @param nabuccoService
     *            the nabucco service statement
     * @param unit
     *            the compilation unit declaration
     * 
     * @throws JavaModelException
     */
    public static void preparePostConstruct(ServiceStatement nabuccoService, JavaCompilationUnit unit)
            throws JavaModelException {

        NabuccoServiceType serviceType = NabuccoServiceType.valueOf(nabuccoService);

        switch (serviceType) {

        case PERSISTENCE:
            createPersistenceManager(unit);
            break;

        case RESOURCE:
            createResourceManager(unit);
            break;
        }

    }

    /**
     * Create the persistence manager initialization for post construct.
     * 
     * @param unit
     *            the java compilation unit
     * 
     * @throws JavaModelException
     */
    private static void createPersistenceManager(JavaCompilationUnit unit) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        TypeDeclaration type = unit.getType();

        MethodDeclaration postConstruct = (MethodDeclaration) JavaAstElementFactory.getInstance().getJavaAstType()
                .getMethod(type, POST_CONSTRUCT_SIGNATURE);

        TypeReference persistenceManager = producer.createTypeReference(PERSISTENCE_MANAGER, false);
        TypeReference persistenceManagerFactory = producer.createTypeReference(PERSISTENCE_MANAGER_FACTORY, false);

        FieldReference em = producer
                .createFieldThisReference(NabuccoTransformationUtility.firstToLower(ENTITY_MANAGER));

        MessageSend logger = producer.createMessageSend(METHOD_GET_LOGGER, producer.createSuperReference(), null);

        MessageSend getInstance = producer.createMessageSend(SINGLETON_GETTER, persistenceManagerFactory, null);
        MessageSend createManager = producer.createMessageSend(METHOD_CREATE_PM, getInstance,
                Arrays.<Expression> asList(em, logger));

        LocalDeclaration declaration = producer.createLocalDeclaration(persistenceManager,
                NabuccoTransformationUtility.firstToLower(PERSISTENCE_MANAGER));

        declaration.initialization = createManager;

        javaFactory.getJavaAstMethod().addStatement(postConstruct, declaration);

        {
            ImportReference importReference = producer.createImportReference(IMPORT_PERSISTENCE_MANAGER);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
        }

        {
            ImportReference importReference = producer.createImportReference(IMPORT_PERSISTENCE_MANAGER_FACTORY);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
        }
    }

    /**
     * Create the resource manager initialization for post construct.
     * 
     * @param unit
     *            the java compilation unit
     * 
     * @throws JavaModelException
     */
    private static void createResourceManager(JavaCompilationUnit unit) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        TypeDeclaration type = unit.getType();

        MethodDeclaration postConstruct = (MethodDeclaration) JavaAstElementFactory.getInstance().getJavaAstType()
                .getMethod(type, POST_CONSTRUCT_SIGNATURE);

        TypeReference persistenceManager = producer.createTypeReference(RESOURCE_MANAGER, false);
        TypeReference persistenceManagerFactory = producer.createTypeReference(RESOURCE_MANAGER_FACTORY, false);

        FieldReference em = producer
                .createFieldThisReference(NabuccoTransformationUtility.firstToLower(SESSION_CONTEXT));

        MessageSend logger = producer.createMessageSend(METHOD_GET_LOGGER, producer.createSuperReference(), null);

        MessageSend getInstance = producer.createMessageSend(SINGLETON_GETTER, persistenceManagerFactory, null);
        MessageSend createManager = producer.createMessageSend(METHOD_CREATE_RM, getInstance,
                Arrays.<Expression> asList(em, logger));

        LocalDeclaration declaration = producer.createLocalDeclaration(persistenceManager,
                NabuccoTransformationUtility.firstToLower(RESOURCE_MANAGER));

        declaration.initialization = createManager;

        javaFactory.getJavaAstMethod().addStatement(postConstruct, declaration);

        {
            ImportReference importReference = producer.createImportReference(IMPORT_RESOURCE_MANAGER);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
        }

        {
            ImportReference importReference = producer.createImportReference(IMPORT_RESOURCE_MANAGER_FACTORY);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
        }
    }

    /**
     * Prepares the pre destroy method of the service implementation.
     * 
     * @param type
     *            the type declaration
     * 
     * @throws JavaModelException
     */
    public static void preparePreDestroy(TypeDeclaration type) {
        // Nothing to do here!
    }

    /**
     * Converts an service operation name to its related service handler name.
     * 
     * @param operationName
     *            name of the service operation
     * 
     * @return name of the related service handler
     */
    public static String convertMethodToHandler(String operationName) {
        StringBuilder handlerName = new StringBuilder();
        handlerName.append(NabuccoTransformationUtility.firstToUpper(operationName));
        handlerName.append(SERVICE_HANDLER);
        return handlerName.toString();
    }

    /**
     * Adjusts the method body of a service operation.
     * 
     * @param method
     *            the service operation to adjust.
     * @param responseType
     *            type of the response
     * 
     * @throws JavaModelException
     */
    private static void createServiceOperationBody(MethodDeclaration method, TypeReference responseType)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        String methodName = javaFactory.getJavaAstMethod().getMethodName(method);
        String fieldName = NabuccoTransformationUtility.firstToLower(convertMethodToHandler(methodName));

        // 1. Statement

        IfStatement ifStatement = (IfStatement) method.statements[0];
        EqualExpression condition = (EqualExpression) ifStatement.condition;
        FieldReference handler = (FieldReference) condition.left;
        javaFactory.getJavaAstReference().setName(fieldName, handler);

        Block then = (Block) ifStatement.thenStatement;
        MessageSend loggerCall = (MessageSend) then.statements[0];
        String message = new String(((StringLiteral) loggerCall.arguments[0]).source());
        message = MessageFormat.format(message, methodName);

        Literal literal = producer.createLiteral(message, LiteralType.STRING_LITERAL);
        loggerCall.arguments[0] = literal;

        ThrowStatement exception = (ThrowStatement) then.statements[1];
        AllocationExpression exceptionAllocation = (AllocationExpression) exception.exception;
        exceptionAllocation.arguments[0] = literal;

        // 2. Statement

        LocalDeclaration msgDeclaration = (LocalDeclaration) method.statements[1];
        ParameterizedSingleTypeReference rsType = (ParameterizedSingleTypeReference) msgDeclaration.type;
        rsType.typeArguments[0] = responseType;

        // 3. Statement

        MessageSend init = (MessageSend) method.statements[2];
        javaFactory.getJavaAstMethodCall().setMethodReceiver(handler, init);

        // 4. Statement

        Assignment invoke = (Assignment) method.statements[3];
        javaFactory.getJavaAstMethodCall().setMethodReceiver(handler, (MessageSend) invoke.expression);

        // 5. Statement

        MessageSend finish = (MessageSend) method.statements[4];
        javaFactory.getJavaAstMethodCall().setMethodReceiver(handler, finish);
    }

    /**
     * Adds an exception to a servicer operation.
     * 
     * @param exception
     *            the exception to add
     * @param method
     *            the method to add the exception
     * @param container
     *            the container to add the imports
     * 
     * @throws JavaModelException
     */
    private static void createException(String exception, MethodDeclaration method,
            JavaAstContainter<MethodDeclaration> container) throws JavaModelException {

        if (exception != null) {
            TypeReference serviceException = JavaAstModelProducer.getInstance().createTypeReference(exception, false);
            JavaAstElementFactory.getInstance().getJavaAstMethod().setException(method, serviceException);
            container.getImports().add(exception);
        } else {
            container.getImports().add(IMPORT_SERVICE_EXCEPTION);
        }
    }

    /**
     * Creates a service operation of the given type.
     * 
     * @param type
     *            the type to extract the operation.
     * @param signature
     *            the method signature
     * 
     * @return the extracted service operation
     * 
     * @throws JavaModelException
     */
    private static MethodDeclaration createServiceOperation(TypeDeclaration type, JavaAstMethodSignature signature)
            throws JavaModelException {
        MethodDeclaration method = (MethodDeclaration) JavaAstElementFactory.getInstance().getJavaAstType()
                .getMethod(type, signature);
        return method;
    }

}
