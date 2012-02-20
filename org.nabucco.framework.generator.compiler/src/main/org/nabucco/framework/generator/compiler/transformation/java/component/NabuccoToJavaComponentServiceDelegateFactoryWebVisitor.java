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
package org.nabucco.framework.generator.compiler.transformation.java.component;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
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
 * NabuccoToJavaComponentServiceDelegateFactoryWebVisitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
class NabuccoToJavaComponentServiceDelegateFactoryWebVisitor extends NabuccoToJavaVisitorSupport
        implements ServerConstants {

    private static String PKG_COMMUNICATION = PKG_SEPARATOR + "communication";

    private static String FACTORY_SUPPORT_IMPORT = "org.nabucco.framework.base.ui.web.communication.ServiceDelegateFactorySupport";

    private static final JavaAstMethodSignature GET_DELEGATE_SIGNATURE = new JavaAstMethodSignature(
            "getServiceDelegateTemplate", new String[] {});

    public NabuccoToJavaComponentServiceDelegateFactoryWebVisitor(
            NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<JavaModel> target) {

        // Visit service-declarations first
        super.visit(nabuccoComponent, target);

        String projectName = super.getComponentName(NabuccoClientType.WEB);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SERVICE_COMPONENT_DELEGATE_FACTORY_PROVIDER_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SERVICE_COMPONENT_DELEGATE_FACTORY_PROVIDER_TEMPLATE);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoComponent.annotationDeclaration, type);

            // Set name
            String name = nabuccoComponent.nodeToken2.tokenImage + SERVICE_DELEGATE_FACTORY;
            javaFactory.getJavaAstType().setTypeName(type, name);

            // handle instance field
            handleInstanceField(type, name);

            // handle getInstance()
            handleGetInstance(type, name);

            // add import for ServiceDelegateFactorySupport
            ImportReference serviceDelegateFactorySupportImport = JavaAstModelProducer
                    .getInstance().createImportReference(FACTORY_SUPPORT_IMPORT);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(),
                    serviceDelegateFactorySupportImport);

            // handle type parameter for ServiceDelegateFactorySupport
            handleFactorySupportParameter(type, nabuccoComponent.nodeToken2.tokenImage);

            // handle component field & add import for Locator
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(),
                    handleConstrutor(type, nabuccoComponent));

            // handle import of Component
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(),
                    handleComponentImport(type, nabuccoComponent));

            // handle service fields
            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(),
                    super.getVisitorContext().getImportList());

            // Set package
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    projectName + PKG_COMMUNICATION);

            // set project
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST Component modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template Component processing.",
                    te);
        }

    }

    /**
     * @param type
     * @param tokenImage
     */
    private void handleFactorySupportParameter(TypeDeclaration type, String tokenImage) {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        try {
            TypeReference superClass = javaFactory.getJavaAstType().getSuperClass(type);
            if (superClass instanceof ParameterizedSingleTypeReference) {
                ParameterizedSingleTypeReference superType = (ParameterizedSingleTypeReference) superClass;
                superType.typeArguments[0] = JavaAstModelProducer.getInstance()
                        .createTypeReference(tokenImage, false);
            }

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST Component modification.", jme);
        }

    }

    private ImportReference handleComponentImport(TypeDeclaration type,
            ComponentStatement nabuccoComponent) throws JavaModelException {
        String componentName = nabuccoComponent.nodeToken2.tokenImage;
        String componentPackage = super.getProjectName(NabuccoModelType.COMPONENT,
                NabuccoModifierType.PUBLIC);
        return JavaAstModelProducer.getInstance().createImportReference(
                componentPackage + PKG_SEPARATOR + componentName);
    }

    private ImportReference handleConstrutor(TypeDeclaration type,
            ComponentStatement nabuccoComponent) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        String locatorPackage = super.getProjectName(NabuccoModelType.COMPONENT,
                NabuccoModifierType.PUBLIC);
        String typeName = javaFactory.getJavaAstType().getTypeName(type);
        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type,
                new JavaAstMethodSignature(typeName, new String[] {}));
        TypeReference locatorTypeReference = JavaAstModelProducer.getInstance()
                .createTypeReference(nabuccoComponent.nodeToken2.tokenImage + LOCATOR, false);
        MessageSend locatorGetInstanceCall = JavaAstModelProducer.getInstance().createMessageSend(
                SINGLETON_GETTER, locatorTypeReference,
                Collections.<org.eclipse.jdt.internal.compiler.ast.Expression> emptyList());
        ExplicitConstructorCall constructorCall = constructor.constructorCall;
        constructorCall.arguments[0] = locatorGetInstanceCall;

        return JavaAstModelProducer.getInstance().createImportReference(
                locatorPackage + PKG_SEPARATOR + nabuccoComponent.nodeToken2.tokenImage + LOCATOR);
    }

    private void handleGetInstance(TypeDeclaration type, String name) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                new JavaAstMethodSignature(SINGLETON_GETTER, new String[] {}));
        TypeReference selfType = JavaAstModelProducer.getInstance()
                .createTypeReference(name, false);
        javaFactory.getJavaAstMethod().setReturnType(method, selfType);
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<JavaModel> target) {

        String type = nabuccoService.nodeToken1.tokenImage;
        String delegateType = type + DELEGATE;
        String delegateName = NabuccoTransformationUtility.firstToLower(delegateType);

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(delegateType,
                delegateName, NabuccoModifierType.PRIVATE);

        JavaAstContainter<MethodDeclaration> getter = new JavaAstContainter<MethodDeclaration>(
                handleGetterMethod(field.getAstNode()), JavaAstType.METHOD);

        String importString = super.resolveImport(type).replace(".facade.service.",
                ".ui.web" + PKG_COMMUNICATION + PKG_SEPARATOR);
        getter.getImports().add(importString + DELEGATE);

        List<JavaAstContainter<? extends ASTNode>> containerList = super.getVisitorContext()
                .getContainerList();
        containerList.add(field);
        containerList.add(getter);

    }

    private MethodDeclaration handleGetterMethod(FieldDeclaration fieldDeclaration) {

        MethodDeclaration result = null;

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);

            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
            MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                    type, GET_DELEGATE_SIGNATURE);

            javaFactory.getJavaAstMethod().setReturnType(method,
                    javaFactory.getJavaAstField().getFieldType(fieldDeclaration));

            String fieldName = javaFactory.getJavaAstField().getFieldName(fieldDeclaration);
            TypeReference fieldType = javaFactory.getJavaAstField().getFieldType(fieldDeclaration);

            String serviceName = NabuccoTransformationUtility.firstToUpper(fieldName).replace(
                    DELEGATE, "");

            String buildMethodName = PREFIX_GETTER + serviceName;
            Statement[] statementsInTryBlock = (((TryStatement) method.statements[0]).tryBlock).statements;

            ((AllocationExpression) (((ThrowStatement) (((TryStatement) method.statements[0]).catchBlocks[0]).statements[0]).exception)).arguments[0] = JavaAstModelProducer
                    .getInstance().createLiteral("Cannot locate service: " + serviceName,
                            LiteralType.STRING_LITERAL);

            EqualExpression ifBlockConditionStatement = (EqualExpression) ((IfStatement) statementsInTryBlock[0]).condition;
            ((FieldReference) ifBlockConditionStatement.left).token = fieldName.toCharArray();

            Statement[] ifBlockStatements = ((Block) ((IfStatement) statementsInTryBlock[0]).thenStatement).statements;
            ((FieldReference) ((Assignment) ifBlockStatements[0]).lhs).token = fieldName
                    .toCharArray();

            ((AllocationExpression) ((Assignment) ifBlockStatements[0]).expression).type = fieldType;
            ((MessageSend) ((AllocationExpression) ((Assignment) ifBlockStatements[0]).expression).arguments[0]).selector = buildMethodName
                    .toCharArray();

            ReturnStatement returnStatement = (ReturnStatement) statementsInTryBlock[1];
            ((FieldReference) returnStatement.expression).token = fieldName.toCharArray();

            javaFactory.getJavaAstMethod().setMethodName(method, buildMethodName);

            result = method;

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException(
                    "Error during Java AST private getter method generation modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template private getter method generation processing.", te);
        }

        return result;
    }

    private void handleInstanceField(TypeDeclaration type, String name) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration instanceField = javaFactory.getJavaAstType().getField(type, "instance");

        // reference to own type
        TypeReference selfType = JavaAstModelProducer.getInstance()
                .createTypeReference(name, false);
        // set field type
        javaFactory.getJavaAstField().setFieldType(instanceField, selfType);

        // create new assignment
        javaFactory.getJavaAstField().setFieldInitializer(instanceField,
                JavaAstModelProducer.getInstance().createAllocationExpression(selfType, null));

    }
}
