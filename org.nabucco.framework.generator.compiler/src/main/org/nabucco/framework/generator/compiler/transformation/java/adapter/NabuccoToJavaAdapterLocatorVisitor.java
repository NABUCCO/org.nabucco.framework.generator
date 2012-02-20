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
package org.nabucco.framework.generator.compiler.transformation.java.adapter;

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.javadoc.NabuccoToJavaJavadocCreator;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.AdapterStatement;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethod;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaAdapterInterfaceVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaAdapterLocatorVisitor extends NabuccoToJavaVisitorSupport implements ServerConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaAdapterLocatorVisitor.class);

    private static final String[] CONSTRUCTOR_ARGUMENTS = { "String", "Class" };

    private static final JavaAstMethodSignature GET_INSTANCE = new JavaAstMethodSignature(SINGLETON_GETTER);

    /**
     * Creates a new {@link NabuccoToJavaAdapterLocatorVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaAdapterLocatorVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(AdapterStatement nabuccoAdapter, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoAdapter, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String interfaceName = nabuccoAdapter.nodeToken2.tokenImage;
        String pkg = this.getVisitorContext().getPackage();
        String name = interfaceName + LOCATOR;
        String projectName = super.getProjectName(NabuccoModelType.ADAPTER,
                NabuccoModifierComponentMapper.getModifierType(nabuccoAdapter.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.ADAPTER_LOCATOR_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.ADAPTER_LOCATOR_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            this.configureExtension(interfaceName, type);
            this.configureSingleton(interfaceName, type);
            this.configureConstructor(interfaceName, type);
            this.configureGetter(interfaceName, type);

            // Javadoc
            NabuccoToJavaJavadocCreator.createJavadoc("Locator for " + interfaceName + PKG_SEPARATOR, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST adapter modification.");
            throw new NabuccoVisitorException("Error during Java AST adapter modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template adapter processing.");
            throw new NabuccoVisitorException("Error during Java template adapter processing.", te);
        }
    }

    /**
     * Configures the locator super class and interface.
     * 
     * @param adapterName
     *            name of the adapter interface
     * @param type
     *            the type to modify
     */
    private void configureExtension(String adapterName, TypeDeclaration type) {
        ParameterizedSingleTypeReference superType = (ParameterizedSingleTypeReference) type.superclass;
        SingleTypeReference param = (SingleTypeReference) superType.typeArguments[0];
        param.token = adapterName.toCharArray();

        superType = (ParameterizedSingleTypeReference) type.superInterfaces[0];
        param = (SingleTypeReference) superType.typeArguments[0];
        param.token = adapterName.toCharArray();
    }

    /**
     * Configures the singleton call method of the locator.
     * 
     * @param adapterName
     *            name of the locator
     * @param type
     *            the type to modify
     * 
     * @throws JavaModelException
     */
    private void configureSingleton(String adapterName, TypeDeclaration type) throws JavaModelException {

        String name = adapterName + LOCATOR;

        JavaAstModelProducer javaProducer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        TypeReference locatorType = javaProducer.createTypeReference(name, false);

        TypeReference adapterType = javaProducer.createTypeReference(adapterName, false);

        // Singleton Field
        FieldDeclaration field = javaFactory.getJavaAstType().getField(type, SINGLETON_INSTANCE);

        field.type = locatorType;

        // Method getInstance()
        MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, GET_INSTANCE);

        javaFactory.getJavaAstMethod().setReturnType(method, locatorType);

        IfStatement ifStatement = (IfStatement) method.statements[0];
        Assignment thenAssignment = (Assignment) ((Block) ifStatement.thenStatement).statements[0];

        AllocationExpression constructorCall = (AllocationExpression) thenAssignment.expression;

        constructorCall.type = locatorType;
        ((QualifiedNameReference) constructorCall.arguments[0]).tokens[0] = adapterName.toCharArray();
        ((ClassLiteralAccess) constructorCall.arguments[1]).type = adapterType;
    }

    /**
     * Configures the private constructor of the locator.
     * 
     * @param adapterName
     *            name of the adapter interface
     * @param type
     *            the type to modify
     * 
     * @throws JavaModelException
     */
    private void configureConstructor(String adapterName, TypeDeclaration type) throws JavaModelException {

        String name = adapterName + LOCATOR;
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        JavaAstMethodSignature constructorSignature = new JavaAstMethodSignature(name, CONSTRUCTOR_ARGUMENTS);
        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type, constructorSignature);

        List<Argument> arguments = javaFactory.getJavaAstMethod().getAllArguments(constructor);
        this.changeGenerics(adapterName, arguments);
    }

    /**
     * Changes the generic types of the constructor arguments.
     * 
     * @param adapterName
     *            the adapter name
     * @param arguments
     *            the constructor arguments
     * 
     * @throws JavaModelException
     */
    private void changeGenerics(String adapterName, List<Argument> arguments) throws JavaModelException {

        if (arguments.size() != 2) {
            throw new IllegalStateException("");
        }

        TypeReference adapterType = JavaAstModelProducer.getInstance().createTypeReference(adapterName, false);

        TypeReference argumentType = arguments.get(1).type;

        if (!(argumentType instanceof ParameterizedSingleTypeReference)) {
            throw new IllegalStateException("Argument is not parameterized.");
        }

        ParameterizedSingleTypeReference type = (ParameterizedSingleTypeReference) argumentType;
        type.typeArguments[0] = adapterType;
    }

    /**
     * Changes the adapter getter.
     * 
     * @param adapterName
     *            name of the adapter
     * @param type
     *            the java type
     * 
     * @throws JavaModelException
     */
    private void configureGetter(String adapterName, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstMethod methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethod();

        TypeReference adapterInterface = producer.createTypeReference(adapterName, false);
        TypeReference localInterface = producer.createTypeReference(adapterName + ServerConstants.LOCAL, false);

        MethodDeclaration getAdapter = (MethodDeclaration) JavaAstElementFactory.getInstance().getJavaAstType()
                .getMethod(type, new JavaAstMethodSignature("getAdapter"));

        // Return type;
        methodFactory.setReturnType(getAdapter, adapterInterface);

        // Statement 1 (super.getAdapter())
        LocalDeclaration adapter = (LocalDeclaration) getAdapter.statements[0];
        adapter.type = adapterInterface;

        // Statement 2 (If Statement)
        IfStatement ifStatement = (IfStatement) getAdapter.statements[1];
        InstanceOfExpression condition = (InstanceOfExpression) ifStatement.condition;
        condition.type = localInterface;

        // Return
        Block then = (Block) ifStatement.thenStatement;
        ReturnStatement returnStatement = (ReturnStatement) then.statements[0];
        AllocationExpression allocation = (AllocationExpression) returnStatement.expression;
        allocation.type = producer.createTypeReference(adapterName + ServerConstants.LOCAL + ServerConstants.PROXY,
                false);

        // Cast
        CastExpression cast = (CastExpression) allocation.arguments[0];
        cast.type = localInterface;
    }
}
