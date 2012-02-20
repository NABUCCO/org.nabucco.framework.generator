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
package org.nabucco.framework.generator.compiler.transformation.java.application.connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.application.connector.util.ServiceLinkResolver;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.ApplicationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceLinkDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceTargetServiceLinkVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceTargetServiceLinkVisitor extends NabuccoToJavaVisitorSupport {

    private JavaCompilationUnit unit;

    private ApplicationStatement application;

    private static final String PRE = "pre";

    private static final String POST = "post";

    private static final String INVOKE = "invoke";

    private static final JavaAstMethodSignature SIGNATURE_INTERNAL_CONNECT = new JavaAstMethodSignature(
            "internalConnect");

    private static final JavaAstMethodSignature SIGNATURE_PRE_OPERATION = new JavaAstMethodSignature(
            "preServiceOperation", "ServiceMessageTemplate");

    private static final JavaAstMethodSignature SIGNATURE_POST_OPERATION = new JavaAstMethodSignature(
            "postServiceOperation", "ServiceMessageTemplate");

    private static final JavaAstMethodSignature SIGNATURE_INVOKE = new JavaAstMethodSignature("invoke",
            "ServiceMessageTemplate");

    /**
     * Creates a new {@link NabuccoToJavaServiceTargetServiceLinkVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceTargetServiceLinkVisitor(NabuccoToJavaVisitorContext visitorContext,
            ApplicationStatement application, JavaCompilationUnit unit) {
        super(visitorContext);

        this.application = application;
        this.unit = unit;
    }

    @Override
    public void visit(ServiceLinkDeclaration serviceLink, MdaModel<JavaModel> target) {
        try {
            ServiceLinkResolver resolver = new ServiceLinkResolver(this.application, super.getVisitorContext());

            resolver.resolve(serviceLink);

            JavaCompilationUnit template = super.extractAst(NabuccoJavaTemplateConstants.CONNECTOR_CALLBACK_TEMPLATE);

            this.createInternalConnectStatements(resolver, template);
            this.createAbstractMethods(resolver, template);
            this.createInvokeMethod(resolver, template);

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Cannot generate target service link.", me);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Cannot generate target service link.", te);
        }
    }

    /**
     * Modify the 'internalConnect()' method.
     * 
     * @param resolver
     *            the service link resolver
     * @param template
     *            the connector callback template
     * 
     * @throws JavaModelException
     */
    private void createInternalConnectStatements(ServiceLinkResolver resolver, JavaCompilationUnit template)
            throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        AbstractMethodDeclaration internalConnect = javaFactory.getJavaAstType().getMethod(unit.getType(),
                SIGNATURE_INTERNAL_CONNECT);

        AbstractMethodDeclaration internalConnectTemlate = javaFactory.getJavaAstType().getMethod(template.getType(),
                SIGNATURE_INTERNAL_CONNECT);

        List<Statement> blockList;
        if (internalConnect.statements == null) {
            blockList = new ArrayList<Statement>();
        } else {
            blockList = new ArrayList<Statement>(Arrays.asList(internalConnect.statements));
        }

        String operation = resolver.getServiceOperation();

        Statement[] statements = new Statement[4];

        if (internalConnectTemlate.statements[0] instanceof LocalDeclaration) {
            LocalDeclaration rqMsg = (LocalDeclaration) internalConnectTemlate.statements[0];

            String typeName = resolver.getRequestMessage();

            ImportReference importReference = producer.createImportReference(typeName);
            javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(), importReference);

            typeName = typeName.substring(typeName.lastIndexOf(PKG_SEPARATOR) + 1);
            TypeReference type = producer.createTypeReference(typeName, false);

            rqMsg.type = type;
            rqMsg.initialization = producer.createAllocationExpression(type, null);

            statements[0] = rqMsg;
        }

        if (internalConnectTemlate.statements[1] instanceof MessageSend) {
            MessageSend pre = (MessageSend) internalConnectTemlate.statements[1];

            String name = PRE + NabuccoTransformationUtility.firstToUpper(operation);
            javaFactory.getJavaAstMethodCall().setMethodName(name, pre);

            statements[1] = pre;
        }

        if (internalConnectTemlate.statements[2] instanceof LocalDeclaration) {
            LocalDeclaration rsMsg = (LocalDeclaration) internalConnectTemlate.statements[2];

            String typeName = resolver.getResponseMessage();

            ImportReference importReference = producer.createImportReference(typeName);
            javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(), importReference);

            typeName = typeName.substring(typeName.lastIndexOf(PKG_SEPARATOR) + 1);
            rsMsg.type = producer.createTypeReference(typeName, false);

            if (rsMsg.initialization instanceof MessageSend) {
                MessageSend invoke = (MessageSend) rsMsg.initialization;
                String methodName = INVOKE + NabuccoTransformationUtility.firstToUpper(operation);
                javaFactory.getJavaAstMethodCall().setMethodName(methodName, invoke);
            }

            statements[2] = rsMsg;
        }

        if (internalConnectTemlate.statements[3] instanceof MessageSend) {
            MessageSend post = (MessageSend) internalConnectTemlate.statements[3];

            String name = POST + NabuccoTransformationUtility.firstToUpper(operation);
            javaFactory.getJavaAstMethodCall().setMethodName(name, post);

            statements[3] = post;
        }

        blockList.add(producer.createBlock(statements));
        internalConnect.statements = blockList.toArray(new Statement[blockList.size()]);
    }

    /**
     * Create the abstract pre/post callback methods.
     * 
     * @param resolver
     *            the service link resolver
     * @param template
     *            the template
     * 
     * @throws JavaModelException
     */
    private void createAbstractMethods(ServiceLinkResolver resolver, JavaCompilationUnit template)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String serviceOperation = resolver.getServiceOperation();
        String preName = PRE + NabuccoTransformationUtility.firstToUpper(serviceOperation);
        String postName = POST + NabuccoTransformationUtility.firstToUpper(serviceOperation);

        String rqMsg = resolver.getRequestMessage();
        String rsMsg = resolver.getResponseMessage();

        MethodDeclaration preOperation = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(template.getType(),
                SIGNATURE_PRE_OPERATION);

        MethodDeclaration postOperation = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                template.getType(), SIGNATURE_POST_OPERATION);

        this.createAbstractMethod(rqMsg, preName, preOperation);
        this.createAbstractMethod(rsMsg, postName, postOperation);
    }

    /**
     * Create the callback method.
     * 
     * @param messageName
     *            the message name
     * @param operationName
     *            the operation name
     * @param operation
     *            the method declaration
     * 
     * @throws JavaModelException
     */
    protected void createAbstractMethod(String messageName, String operationName, MethodDeclaration operation)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        javaFactory.getJavaAstMethod().setMethodName(operation, operationName);
        List<Argument> arguments = javaFactory.getJavaAstMethod().getAllArguments(operation);

        if (arguments.size() != 1) {
            throw new IllegalStateException("Connector Callback Template is not valid.");
        }

        Argument argument = arguments.get(0);

        messageName = messageName.substring(messageName.lastIndexOf(PKG_SEPARATOR) + 1);
        TypeReference type = producer.createTypeReference(messageName, false);
        javaFactory.getJavaAstArgument().setType(argument, type);

        javaFactory.getJavaAstType().addMethod(this.unit.getType(), operation);
    }

    /**
     * Create the 'invoke()' method for concrete service invocation.
     * 
     * @param resolver
     *            the service link resolver
     * @param template
     *            the template
     * 
     * @throws JavaModelException
     */
    private void createInvokeMethod(ServiceLinkResolver resolver, JavaCompilationUnit template)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        MethodDeclaration invoke = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(template.getType(),
                SIGNATURE_INVOKE);

        this.createInvokeSignature(resolver, invoke);
        this.createInvokeStatements(resolver, invoke);

        javaFactory.getJavaAstType().addMethod(this.unit.getType(), invoke);
    }

    /**
     * Create the method signature.
     * 
     * @param resolver
     *            the service link resolver
     * @param invoke
     *            the invoke method
     * 
     * @throws JavaModelException
     */
    private void createInvokeSignature(ServiceLinkResolver resolver, MethodDeclaration invoke)
            throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        TypeReference rqMsg, rsMsg;

        {
            String message = resolver.getRequestMessage();
            message = message.substring(message.lastIndexOf(PKG_SEPARATOR) + 1);
            rqMsg = producer.createTypeReference(message, false);
        }

        {
            String message = resolver.getResponseMessage();
            message = message.substring(message.lastIndexOf(PKG_SEPARATOR) + 1);
            rsMsg = producer.createTypeReference(message, false);
        }

        String methodName = INVOKE + NabuccoTransformationUtility.firstToUpper(resolver.getServiceOperation());

        javaFactory.getJavaAstMethod().setMethodName(invoke, methodName);
        javaFactory.getJavaAstMethod().setReturnType(invoke, rsMsg);

        List<Argument> arguments = javaFactory.getJavaAstMethod().getAllArguments(invoke);
        if (arguments.size() != 1) {
            throw new IllegalStateException("Connector Callback Template is not valid.");
        }

        javaFactory.getJavaAstArgument().setType(arguments.get(0), rqMsg);
    }

    /**
     * Create the method body.
     * 
     * @param resolver
     *            the service link resolver
     * @param invoke
     *            the invoke method
     * 
     * @throws JavaModelException
     */
    private void createInvokeStatements(ServiceLinkResolver resolver, MethodDeclaration invoke)
            throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String componentImport = resolver.getComponent();
        String component = componentImport.substring(componentImport.lastIndexOf(PKG_SEPARATOR) + 1);

        String componentLocator = component + LOCATOR;
        String componentLocatorImport = componentImport + LOCATOR;

        String serviceImport = resolver.getService();
        String service = serviceImport.substring(serviceImport.lastIndexOf(PKG_SEPARATOR) + 1);
        String serviceOperation = resolver.getServiceOperation();

        String requestMsgImport = resolver.getRequestMessage();
        String requestMsg = requestMsgImport.substring(requestMsgImport.lastIndexOf(PKG_SEPARATOR) + 1);

        String responseMsgImport = resolver.getResponseMessage();
        String responseMsg = responseMsgImport.substring(responseMsgImport.lastIndexOf(PKG_SEPARATOR) + 1);

        javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(),
                producer.createImportReference(componentImport));

        javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(),
                producer.createImportReference(componentLocatorImport));

        javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(),
                producer.createImportReference(serviceImport));

        if (invoke.statements[0] instanceof LocalDeclaration) {
            LocalDeclaration componentDeclaration = (LocalDeclaration) invoke.statements[0];

            componentDeclaration.type = producer.createTypeReference(component, false);

            if (componentDeclaration.initialization instanceof MessageSend) {
                MessageSend getInstance = (MessageSend) componentDeclaration.initialization;
                MessageSend locator = (MessageSend) getInstance.receiver;
                locator.receiver = producer.createTypeReference(componentLocator, false);
            }
        }

        if (invoke.statements[1] instanceof LocalDeclaration) {
            LocalDeclaration serviceDeclaration = (LocalDeclaration) invoke.statements[1];

            serviceDeclaration.type = producer.createTypeReference(service, false);

            MessageSend getService = (MessageSend) serviceDeclaration.initialization;
            javaFactory.getJavaAstMethodCall().setMethodName(PREFIX_GETTER + service, getService);
        }

        if (invoke.statements[2] instanceof LocalDeclaration) {
            LocalDeclaration request = (LocalDeclaration) invoke.statements[2];
            ParameterizedSingleTypeReference type = (ParameterizedSingleTypeReference) request.type;

            type.typeArguments[0] = producer.createTypeReference(requestMsg, false);

            AllocationExpression allocation = (AllocationExpression) request.initialization;
            allocation.type = type;
        }

        if (invoke.statements[4] instanceof LocalDeclaration) {
            LocalDeclaration response = (LocalDeclaration) invoke.statements[4];
            ParameterizedSingleTypeReference type = (ParameterizedSingleTypeReference) response.type;

            type.typeArguments[0] = producer.createTypeReference(responseMsg, false);

            MessageSend operation = (MessageSend) response.initialization;
            javaFactory.getJavaAstMethodCall().setMethodName(serviceOperation, operation);
        }
    }
}
