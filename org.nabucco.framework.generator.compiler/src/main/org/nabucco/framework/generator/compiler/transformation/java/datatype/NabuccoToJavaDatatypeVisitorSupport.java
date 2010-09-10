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
package org.nabucco.framework.generator.compiler.transformation.java.datatype;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.basetype.NabuccoToJavaBasetypeReferences;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.common.extension.NabuccoToJavaExtensionEvaluator;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethod;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaDatatypeVisitorSupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
final class NabuccoToJavaDatatypeVisitorSupport implements ServerConstants {

    private static final String GET_VALUE = "getValue";

    private static final String SET_VALUE = "setValue";

    private static final String VALUE_OF = "valueOf";

    private static final String ENUM_ID_TYPE = "String";

    private static final String INIT_METHOD_NAME = "initDefaults";

    private static final JavaAstMethodSignature INIT_METHOD = new JavaAstMethodSignature(
            INIT_METHOD_NAME, new String[] {});

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaDatatypeVisitorSupport.class);

    private NabuccoToJavaDatatypeVisitorSupport() {
    }

    /**
     * Creates a basetype wrapper getter for a field name and type.
     * <p/>
     * 
     * E.g. a reference to a basetype of type NString gets a getter containing the simple type as
     * argument <code>String getName()</code>.
     * 
     * @param fieldName
     *            name of the field
     * @param fieldType
     *            type of the field
     * @param basetypeType
     *            type of the basetype delegate
     * 
     * @return a {@link JavaAstContainter} with the getter method
     * 
     * @throws NabuccoVisitorException
     */
    public static JavaAstContainter<MethodDeclaration> createBasetypeWrapperGetter(
            String fieldName, String fieldType, String basetypeType) throws NabuccoVisitorException {

        try {
            String methodName = PREFIX_GETTER
                    + NabuccoTransformationUtility.firstToUpper(fieldName);

            JavaAstModelProducer modelProducer = JavaAstModelProducer.getInstance();

            TypeReference type = modelProducer.createTypeReference(fieldType, false);
            TypeReference delegateType = modelProducer.createTypeReference(basetypeType, false);

            MethodDeclaration method = modelProducer.createMethodDeclaration(methodName, null,
                    false);

            final JavaAstMethod methodFactory = JavaAstElementFactory.getInstance()
                    .getJavaAstMethod();
            methodFactory.setMethodName(method, methodName);
            methodFactory.setReturnType(method, delegateType);

            FieldReference fieldReference = modelProducer.createFieldThisReference(fieldName);

            // If statement

            FieldReference thisReference = modelProducer.createFieldThisReference(String
                    .valueOf(fieldName));
            Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

            BinaryExpression condition = modelProducer.createBinaryExpression(
                    BinaryExpressionType.EQUAL_EXPRESSION, thisReference, nullLiteral,
                    EqualExpression.EQUAL_EQUAL);

            AllocationExpression constructor = modelProducer.createAllocationExpression(type, null);

            Block thenStatement = modelProducer.createBlock(new Statement[] { modelProducer
                    .createAssignment(fieldReference, constructor) });

            IfStatement ifStatement = modelProducer.createIfStatement(condition, thenStatement,
                    null);

            MessageSend getValue = modelProducer.createMessageSend(GET_VALUE, fieldReference, null);
            ReturnStatement returnStatement = modelProducer.createReturnStatement(getValue);

            method.statements = new Statement[] { ifStatement, returnStatement };

            return new JavaAstContainter<MethodDeclaration>(method, JavaAstType.METHOD);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST field modification.");
            throw new NabuccoVisitorException("Error during Java AST field modification.", jme);
        }
    }

    /**
     * Creates a basetype wrapper setter for a field name and type.
     * <p/>
     * 
     * E.g. a reference to a basetype of type NString gets a setter containing the simple type as
     * argument <code>void setName(String name)</code>.
     * 
     * @param fieldName
     *            name of the field
     * @param fieldType
     *            type of the field
     * @param basetypeType
     *            type of the basetype delegate
     * 
     * @return a {@link JavaAstContainter} with the setter method
     * 
     * @throws NabuccoVisitorException
     */
    public static JavaAstContainter<MethodDeclaration> createBasetypeWrapperSetter(
            String fieldName, String fieldType, String basetypeType) throws NabuccoVisitorException {

        try {
            String methodName = PREFIX_SETTER
                    + NabuccoTransformationUtility.firstToUpper(fieldName);

            JavaAstModelProducer modelProducer = JavaAstModelProducer.getInstance();

            TypeReference type = modelProducer.createTypeReference(fieldType, false);
            TypeReference delegateType = modelProducer.createTypeReference(basetypeType, false);

            MethodDeclaration method = modelProducer.createMethodDeclaration(methodName, null,
                    false);

            JavaAstElementFactory.getInstance().getJavaAstMethod()
                    .setMethodName(method, methodName);

            Argument argument = modelProducer.createArgument(fieldName, delegateType);
            JavaAstElementFactory.getInstance().getJavaAstMethod().addArgument(method, argument);

            FieldReference fieldReference = modelProducer.createFieldThisReference(fieldName);
            SingleNameReference nameReference = modelProducer.createSingleNameReference(fieldName);

            // If statement

            FieldReference thisReference = modelProducer.createFieldThisReference(String
                    .valueOf(fieldName));
            Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

            BinaryExpression condition = modelProducer.createBinaryExpression(
                    BinaryExpressionType.EQUAL_EXPRESSION, thisReference, nullLiteral,
                    EqualExpression.EQUAL_EQUAL);

            AllocationExpression constructor = modelProducer.createAllocationExpression(type, null);

            Block thenStatement = modelProducer.createBlock(new Statement[] { modelProducer
                    .createAssignment(fieldReference, constructor) });

            IfStatement ifStatement = modelProducer.createIfStatement(condition, thenStatement,
                    null);

            MessageSend setValue = modelProducer.createMessageSend(SET_VALUE, fieldReference,
                    Arrays.asList(nameReference));

            method.statements = new Statement[] { ifStatement, setValue };

            return new JavaAstContainter<MethodDeclaration>(method, JavaAstType.METHOD);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST field modification.");
            throw new NabuccoVisitorException("Error during Java AST field modification.", jme);
        }
    }

    /**
     * Creates an enumeration ID setter for a field name and type.
     * <p/>
     * 
     * E.g. a reference to an enum gets a setter containing the enum itself as argument
     * <code>setType(Type type)</code> and a setter containing the enum ID as argument
     * <code>setType(String type)</code>.
     * 
     * @param enumName
     *            name of the field
     * @param enumType
     *            type of the field
     * 
     * @return a {@link JavaAstContainter} with the setter method
     * 
     * @throws NabuccoVisitorException
     */
    public static JavaAstContainter<MethodDeclaration> createEnumIdSetter(String enumName,
            String enumType) throws NabuccoVisitorException {

        try {
            String methodName = PREFIX_SETTER + NabuccoTransformationUtility.firstToUpper(enumName);

            JavaAstModelProducer modelProducer = JavaAstModelProducer.getInstance();

            TypeReference type = modelProducer.createTypeReference(enumType, false);
            TypeReference delegateType = modelProducer.createTypeReference(ENUM_ID_TYPE, false);

            MethodDeclaration method = modelProducer.createMethodDeclaration(methodName.toString(),
                    null, false);

            JavaAstElementFactory.getInstance().getJavaAstMethod()
                    .setMethodName(method, methodName);

            Argument argument = modelProducer.createArgument(enumName, delegateType);
            JavaAstElementFactory.getInstance().getJavaAstMethod().addArgument(method, argument);

            FieldReference fieldReference = modelProducer.createFieldThisReference(enumName);
            SingleNameReference nameReference = modelProducer.createSingleNameReference(enumName);

            // If statement

            Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

            BinaryExpression condition = modelProducer.createBinaryExpression(
                    BinaryExpressionType.EQUAL_EXPRESSION, nameReference, nullLiteral,
                    EqualExpression.EQUAL_EQUAL);

            Block thenStatement = modelProducer.createBlock(new Statement[] { modelProducer
                    .createAssignment(fieldReference, nullLiteral) });

            MessageSend valueOf = modelProducer.createMessageSend(VALUE_OF, type, Arrays
                    .asList(nameReference));

            Block elseStatement = modelProducer.createBlock(modelProducer.createAssignment(
                    fieldReference, valueOf));

            IfStatement ifStatement = modelProducer.createIfStatement(condition, thenStatement,
                    elseStatement);

            method.statements = new Statement[] { ifStatement };

            return new JavaAstContainter<MethodDeclaration>(method, JavaAstType.METHOD);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST field modification.");
            throw new NabuccoVisitorException("Error during Java AST field modification.", jme);
        }
    }

    /**
     * Resolves a basetype to its original java delegate.
     * 
     * @param rootDirectory
     *            the project root
     * @param importString
     *            the import string of the basetype
     * @param outDirectory
     *            the project out directory
     * 
     * @return the basetype delegate
     */
    public static String resolveBasetypeDelegate(String rootDirectory, String pkg,
            String importString, String outDirectory) {
        try {
            NabuccoUnit unit = NabuccoDependencyResolver.getInstance().resolveDependency(
                    rootDirectory, pkg, importString, outDirectory).getModel().getUnit();

            String extension = NabuccoToJavaExtensionEvaluator.getInstance().getExtension(unit);

            return NabuccoToJavaBasetypeReferences.mapToJavaType(extension);
        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException(e);
        }
    }

    /**
     * Adds the expressions to the given datatype {@link TypeDeclaration}'s init method.
     * 
     * @param type
     *            the datatype type declaration
     * @param expressionList
     *            the list of expressions
     */
    public static void handleInitDefaults(TypeDeclaration type, List<Expression> expressionList) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            MethodDeclaration setDefaultMethod = (MethodDeclaration) javaFactory.getJavaAstType()
                    .getMethod(type, INIT_METHOD);

            Statement[] methodBody = new Statement[expressionList.size()];

            for (int i = 0; i < expressionList.size(); i++) {
                methodBody[i] = expressionList.get(i);
            }
            setDefaultMethod.statements = methodBody;
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException(
                    "Exception during appending of initDefaults() method content", e);
        }

    }

    /**
     * Creates an enumeration default initialization expression.
     * 
     * @param nabuccoEnum
     *            the appropriate enum declaration
     * 
     * @return the expression
     */
    public static Expression createEnumInitializer(EnumerationDeclaration nabuccoEnum) {

        String name = nabuccoEnum.nodeToken2.tokenImage;
        String type = ((NodeToken) nabuccoEnum.nodeChoice1.choice).tokenImage;

        // contribute to default value setting
        try {

            NabuccoAnnotation defaultValue = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                    nabuccoEnum.annotationDeclaration, NabuccoAnnotationType.DEFAULT);

            if (defaultValue != null) {
                JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
                SingleNameReference fieldReference = jamp.createSingleNameReference(name);
                QualifiedNameReference literalToSet = jamp.createQualifiedNameReference(type,
                        defaultValue.getValue());
                return jamp.createAssignment(fieldReference, literalToSet);
            }

            return null;

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException(
                    "Exception creating element while generating content for initDefaults() of enum declaration "
                            + name, e);
        }
    }

    /**
     * Creates an basetype default initialization expression.
     * 
     * @param nabuccoBasetype
     *            the appropriate basetype declaration
     * @param type
     *            the internal basetype type
     * 
     * @return the expression
     */
    public static Expression createBasetypeInitializer(BasetypeDeclaration nabuccoBasetype,
            String type) {

        String name = nabuccoBasetype.nodeToken3.tokenImage;
        String typeName = nabuccoBasetype.nodeToken1.tokenImage;

        // contribute to default value setting
        try {
            NabuccoAnnotation defaultValue = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                    nabuccoBasetype.annotationDeclaration, NabuccoAnnotationType.DEFAULT);

            if (defaultValue != null) {
                JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
                SingleNameReference fieldReference = jamp.createSingleNameReference(name);
                Literal defaultLiteral = jamp.createLiteral(defaultValue.getValue(), LiteralType
                        .mapFromString(type));
                TypeReference createTypeReference = jamp.createTypeReference(typeName, false);
                AllocationExpression createAllocationExpression = jamp.createAllocationExpression(
                        createTypeReference, Arrays.asList(new Expression[] { defaultLiteral }));
                return jamp.createAssignment(fieldReference, createAllocationExpression);
            }

            return null;

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException(
                    "Exception creating element while generating content for initDefaults() for basetype declaration "
                            + name, e);
        }
    }

    /**
     * Checks whether a basetype is redefined or not.
     * 
     * @param nabuccoBasetype
     *            the basetype to check
     * 
     * @return <b>true</b> if the basetype is redefined, <b>false</b> if not
     */
    public static boolean isRedefinition(BasetypeDeclaration nabuccoBasetype) {
        if (NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoBasetype.annotationDeclaration, NabuccoAnnotationType.REDEFINED) == null) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether an enumeration is redefined or not.
     * 
     * @param nabuccoEnum
     *            the enum to check
     * 
     * @return <b>true</b> if the enum is redefined, <b>false</b> if not
     */
    public static boolean isRedefinition(EnumerationDeclaration nabuccoEnum) {
        if (NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoEnum.annotationDeclaration, NabuccoAnnotationType.REDEFINED) == null) {
            return false;
        }

        return true;
    }
}
