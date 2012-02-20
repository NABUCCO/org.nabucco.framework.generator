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
package org.nabucco.framework.generator.compiler.transformation.java.common.ast.util;

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstField;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethod;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * JavaAstGetterSetterProducer
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class JavaAstGetterSetterProducer implements CollectionConstants {

    private JavaAstModelProducer modelProducer = JavaAstModelProducer.getInstance();

    private JavaAstField fieldFactory = JavaAstElementFactory.getInstance().getJavaAstField();

    private JavaAstMethod methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethod();

    /**
     * Singleton instance.
     */
    private static JavaAstGetterSetterProducer instance = new JavaAstGetterSetterProducer();

    /**
     * Private constructor.
     */
    private JavaAstGetterSetterProducer() {
    }

    /**
     * Singleton access.
     * 
     * @return the JavaAstGetterSetterProducer instance.
     */
    public static JavaAstGetterSetterProducer getInstance() {
        return instance;
    }

    /**
     * Creates a setter method for a field with default producing options.
     * 
     * @param field
     *            the {@link FieldDeclaration}
     * 
     * @return the created {@link MethodDeclaration}
     * 
     * @throws JavaModelException
     */
    public MethodDeclaration produceGetter(FieldDeclaration field) throws JavaModelException {
        return this.produceGetter(field, null);
    }

    /**
     * Creates a getter method for a field.
     * 
     * @param field
     *            the {@link FieldDeclaration}
     * @param options
     *            the getter producing information
     * 
     * @return the created {@link MethodDeclaration}
     * 
     * @throws JavaModelException
     */
    public MethodDeclaration produceGetter(FieldDeclaration field, FieldOptions options) throws JavaModelException {

        if (options == null) {
            options = new FieldOptions();
        }

        String fieldName = fieldFactory.getFieldName(field);
        MethodDeclaration method = modelProducer.createMethodDeclaration(createMethodName(field, PREFIX_GETTER), null,
                false);

        methodFactory.setReturnType(method, field.type);

        FieldReference fieldReference = modelProducer.createFieldThisReference(fieldName);

        CollectionType collectionType = this.getCollectionType(field);

        if (collectionType == null) {
            this.produceDefaultGetter(method, fieldReference);
        } else {

            switch (collectionType) {

            case LIST: {
                switch (options.getCollectionImplementationType()) {
                case NABUCCO:
                    this.produceNabuccoListGetter(field, method, fieldReference);
                    break;
                default:
                    this.produceDefaultListGetter(field, method, fieldReference);
                    break;
                }
                break;
            }
            case SET: {
                switch (options.getCollectionImplementationType()) {
                case NABUCCO:
                    this.produceNabuccoSetGetter(field, method, fieldReference);
                    break;
                default:
                    this.produceDefaultSetGetter(field, method, fieldReference);
                    break;
                }
                break;

            }
            case MAP: {
                switch (options.getCollectionImplementationType()) {
                case NABUCCO:
                    this.produceNabuccoMapGetter(field, method, fieldReference);
                    break;
                default:
                    this.produceDefaultMapGetter(field, method, fieldReference);
                    break;
                }
                break;
            }
            }
        }

        return method;
    }

    /**
     * Create the default getter implementation.
     * 
     * @param getter
     *            the getter
     * @param fieldReference
     *            the field reference
     * 
     * @throws JavaModelException
     */
    private void produceDefaultGetter(MethodDeclaration getter, FieldReference fieldReference)
            throws JavaModelException {
        ReturnStatement returnStatement = modelProducer.createReturnStatement(fieldReference);
        getter.statements = new Statement[] { returnStatement };
    }

    /**
     * Creates the List getter with the NABUCCO implementation.
     * 
     * @param field
     *            the field
     * @param getter
     *            the method
     * @param fieldReference
     *            the field reference
     * 
     * @see CollectionConstants#NABUCCO_LIST_IMPL
     * 
     * @throws JavaModelException
     */
    private void produceNabuccoListGetter(FieldDeclaration field, MethodDeclaration getter,
            FieldReference fieldReference) throws JavaModelException {

        Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

        BinaryExpression condition = modelProducer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION,
                fieldReference, nullLiteral, EqualExpression.EQUAL_EQUAL);

        TypeReference list = modelProducer.createParameterizedTypeReference(NABUCCO_LIST_IMPL, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        QualifiedNameReference state = JavaAstModelProducer.getInstance().createQualifiedNameReference(
                COLLECTION_STATE, COLLECTION_STATE_INIT);

        AllocationExpression constructor = modelProducer.createAllocationExpression(list, Arrays.asList(state));

        Assignment assignment = modelProducer.createAssignment(fieldReference, constructor);
        Block then = modelProducer.createBlock(assignment);
        IfStatement ifStatement = modelProducer.createIfStatement(condition, then, null);

        ReturnStatement returnStatement = modelProducer.createReturnStatement(fieldReference);

        getter.statements = new Statement[] { ifStatement, returnStatement };
    }

    /**
     * Creates the List getter with the default implementation.
     * 
     * @param field
     *            the field
     * @param setter
     *            the method
     * @param fieldReference
     *            the field reference
     * 
     * @see CollectionConstants#DEFAULT_LIST
     * 
     * @throws JavaModelException
     */
    private void produceDefaultListGetter(FieldDeclaration field, MethodDeclaration method,
            FieldReference fieldReference) throws JavaModelException {

        Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

        BinaryExpression condition = modelProducer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION,
                fieldReference, nullLiteral, EqualExpression.EQUAL_EQUAL);

        TypeReference list = modelProducer.createParameterizedTypeReference(DEFAULT_LIST, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        AllocationExpression constructor = modelProducer.createAllocationExpression(list, null);
        Assignment assignment = modelProducer.createAssignment(fieldReference, constructor);
        Block then = modelProducer.createBlock(assignment);
        IfStatement ifStatement = modelProducer.createIfStatement(condition, then, null);

        ReturnStatement returnStatement = modelProducer.createReturnStatement(fieldReference);

        method.statements = new Statement[] { ifStatement, returnStatement };
    }

    /**
     * Creates the Set getter with the NABUCCO implementation.
     * 
     * @param field
     *            the field
     * @param setter
     *            the method
     * @param fieldReference
     *            the field reference
     * 
     * @see CollectionConstants#NABUCCO_SET_IMPL
     * 
     * @throws JavaModelException
     */
    private void produceNabuccoSetGetter(FieldDeclaration field, MethodDeclaration method, FieldReference fieldReference)
            throws JavaModelException {

        Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

        BinaryExpression condition = modelProducer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION,
                fieldReference, nullLiteral, EqualExpression.EQUAL_EQUAL);

        TypeReference list = modelProducer.createParameterizedTypeReference(NABUCCO_SET_IMPL, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        QualifiedNameReference state = JavaAstModelProducer.getInstance().createQualifiedNameReference(
                COLLECTION_STATE, COLLECTION_STATE_INIT);

        AllocationExpression constructor = modelProducer.createAllocationExpression(list, Arrays.asList(state));

        Assignment assignment = modelProducer.createAssignment(fieldReference, constructor);
        Block then = modelProducer.createBlock(assignment);
        IfStatement ifStatement = modelProducer.createIfStatement(condition, then, null);

        ReturnStatement returnStatement = modelProducer.createReturnStatement(fieldReference);

        method.statements = new Statement[] { ifStatement, returnStatement };
    }

    /**
     * Creates the Set getter with the default implementation.
     * 
     * @param field
     *            the field
     * @param setter
     *            the method
     * @param fieldReference
     *            the field reference
     * 
     * @see CollectionConstants#DEFAULT_SET
     * 
     * @throws JavaModelException
     */
    private void produceDefaultSetGetter(FieldDeclaration field, MethodDeclaration method, FieldReference fieldReference)
            throws JavaModelException {

        Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

        BinaryExpression condition = modelProducer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION,
                fieldReference, nullLiteral, EqualExpression.EQUAL_EQUAL);

        TypeReference list = modelProducer.createParameterizedTypeReference(DEFAULT_SET, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        AllocationExpression constructor = modelProducer.createAllocationExpression(list, null);
        Assignment assignment = modelProducer.createAssignment(fieldReference, constructor);
        Block then = modelProducer.createBlock(assignment);
        IfStatement ifStatement = modelProducer.createIfStatement(condition, then, null);

        ReturnStatement returnStatement = modelProducer.createReturnStatement(fieldReference);

        method.statements = new Statement[] { ifStatement, returnStatement };
    }

    /**
     * Creates the Map getter with the NABUCCO implementation.
     * 
     * @param field
     *            the field
     * @param setter
     *            the method
     * @param fieldReference
     *            the field reference
     * 
     * @see CollectionConstants#NABUCCO_MAP_IMPL
     * 
     * @throws JavaModelException
     */
    private void produceNabuccoMapGetter(FieldDeclaration field, MethodDeclaration method, FieldReference fieldReference)
            throws JavaModelException {

        Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

        BinaryExpression condition = modelProducer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION,
                fieldReference, nullLiteral, EqualExpression.EQUAL_EQUAL);

        TypeReference map = modelProducer.createParameterizedTypeReference(NABUCCO_MAP_IMPL, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        AllocationExpression constructor = modelProducer.createAllocationExpression(map, null);
        Assignment assignment = modelProducer.createAssignment(fieldReference, constructor);
        Block then = modelProducer.createBlock(assignment);
        IfStatement ifStatement = modelProducer.createIfStatement(condition, then, null);

        ReturnStatement returnStatement = modelProducer.createReturnStatement(fieldReference);

        method.statements = new Statement[] { ifStatement, returnStatement };
    }

    /**
     * Creates the Map getter with the default implementation.
     * 
     * @param field
     *            the field
     * @param setter
     *            the method
     * @param fieldReference
     *            the field reference
     * 
     * @see CollectionConstants#DEFAULT_MAP
     * 
     * @throws JavaModelException
     */
    private void produceDefaultMapGetter(FieldDeclaration field, MethodDeclaration method, FieldReference fieldReference)
            throws JavaModelException {

        Literal nullLiteral = modelProducer.createLiteral(null, LiteralType.NULL_LITERAL);

        BinaryExpression condition = modelProducer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION,
                fieldReference, nullLiteral, EqualExpression.EQUAL_EQUAL);

        TypeReference map = modelProducer.createParameterizedTypeReference(DEFAULT_MAP, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        AllocationExpression constructor = modelProducer.createAllocationExpression(map, null);
        Assignment assignment = modelProducer.createAssignment(fieldReference, constructor);
        Block then = modelProducer.createBlock(assignment);
        IfStatement ifStatement = modelProducer.createIfStatement(condition, then, null);

        ReturnStatement returnStatement = modelProducer.createReturnStatement(fieldReference);

        method.statements = new Statement[] { ifStatement, returnStatement };
    }

    /**
     * Creates a setter method for a field with producing information.
     * 
     * @param field
     *            the {@link FieldDeclaration}
     * 
     * @return the created {@link MethodDeclaration}
     * 
     * @throws JavaModelException
     */
    public MethodDeclaration produceSetter(FieldDeclaration field) throws JavaModelException {

        String fieldName = fieldFactory.getFieldName(field);
        String methodName = this.createMethodName(field, PREFIX_SETTER);

        MethodDeclaration setter = modelProducer.createMethodDeclaration(methodName, null, false);

        Argument argument = modelProducer.createArgument(fieldName, field.type);
        methodFactory.addArgument(setter, argument);

        CollectionType collectionType = this.getCollectionType(field);

        if (collectionType == null) {
            this.produceDefaultSetter(fieldName, setter);
        } else {

            switch (collectionType) {
            case LIST:
                this.produceDefaultListSetter(field, fieldName, setter);
                break;
            case SET:
                this.produceDefaultSetSetter(field, fieldName, setter);
                break;
            case MAP:
                this.produceDefaultMapSetter(field, fieldName, setter);
                break;
            }
        }

        return setter;
    }

    /**
     * Create the default setter implementation.
     * 
     * @param fieldName
     *            the field name
     * @param setter
     *            the setter
     * 
     * @throws JavaModelException
     */
    private void produceDefaultSetter(String fieldName, MethodDeclaration setter) throws JavaModelException {

        FieldReference fieldReference = modelProducer.createFieldThisReference(fieldName);
        SingleNameReference nameReference = modelProducer.createSingleNameReference(fieldName);

        Assignment assignment = modelProducer.createAssignment(fieldReference, nameReference);
        setter.statements = new Statement[] { assignment };
    }

    /**
     * Creates the list setter with the default implementation.
     * 
     * @param field
     *            the field
     * @param fieldName
     *            the field name
     * @param setter
     *            the method
     * 
     * @see CollectionConstants#DEFAULT_LIST
     * 
     * @throws JavaModelException
     */
    private void produceDefaultListSetter(FieldDeclaration field, String fieldName, MethodDeclaration setter)
            throws JavaModelException {

        FieldReference fieldReference = modelProducer.createFieldThisReference(fieldName);
        SingleNameReference nameReference = modelProducer.createSingleNameReference(fieldName);

        TypeReference list = modelProducer.createParameterizedTypeReference(DEFAULT_LIST, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        AllocationExpression constructor = modelProducer.createAllocationExpression(list, Arrays.asList(nameReference));

        setter.statements = new Statement[] { modelProducer.createAssignment(fieldReference, constructor) };
    }

    /**
     * Creates the Set setter with the default implementation.
     * 
     * @param field
     *            the field
     * @param fieldName
     *            the field name
     * @param setter
     *            the method
     * 
     * @see CollectionConstants#DEFAULT_SET
     * 
     * @throws JavaModelException
     */
    private void produceDefaultSetSetter(FieldDeclaration field, String fieldName, MethodDeclaration setter)
            throws JavaModelException {

        FieldReference fieldReference = modelProducer.createFieldThisReference(fieldName);
        SingleNameReference nameReference = modelProducer.createSingleNameReference(fieldName);

        TypeReference list = modelProducer.createParameterizedTypeReference(DEFAULT_SET, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        AllocationExpression constructor = modelProducer.createAllocationExpression(list, Arrays.asList(nameReference));

        setter.statements = new Statement[] { modelProducer.createAssignment(fieldReference, constructor) };
    }

    /**
     * Creates the Map setter with the default implementation.
     * 
     * @param field
     *            the field
     * @param fieldName
     *            the field name
     * @param setter
     *            the method
     * 
     * @see CollectionConstants#DEFAULT_MAP
     * 
     * @throws JavaModelException
     */
    private void produceDefaultMapSetter(FieldDeclaration field, String fieldName, MethodDeclaration setter)
            throws JavaModelException {

        FieldReference fieldReference = modelProducer.createFieldThisReference(fieldName);
        SingleNameReference nameReference = modelProducer.createSingleNameReference(fieldName);

        TypeReference map = modelProducer.createParameterizedTypeReference(DEFAULT_MAP, false,
                Arrays.asList(((ParameterizedSingleTypeReference) field.type).typeArguments));

        AllocationExpression constructor = modelProducer.createAllocationExpression(map, Arrays.asList(nameReference));

        Assignment assignment = modelProducer.createAssignment(fieldReference, constructor);

        setter.statements = new Statement[] { assignment };
    }

    /**
     * Checks whether a field is of type List, Set or Map;
     * 
     * @param field
     *            the field declaration to check
     * 
     * @return <b>true</b> if a field contains multiple elements, <b>false</b> if not
     */
    private CollectionType getCollectionType(FieldDeclaration field) {

        if (Arrays.equals(field.type.getLastToken(), LIST.toCharArray())) {
            return CollectionType.LIST;
        }
        if (Arrays.equals(field.type.getLastToken(), NABUCCO_LIST.toCharArray())) {
            return CollectionType.LIST;
        }
        if (Arrays.equals(field.type.getLastToken(), SET.toCharArray())) {
            return CollectionType.SET;
        }
        if (Arrays.equals(field.type.getLastToken(), NABUCCO_SET.toCharArray())) {
            return CollectionType.SET;
        }
        if (Arrays.equals(field.type.getLastToken(), MAP.toCharArray())) {
            return CollectionType.MAP;
        }
        if (Arrays.equals(field.type.getLastToken(), NABUCCO_MAP.toCharArray())) {
            return CollectionType.MAP;
        }

        return null;
    }

    /**
     * Creates the method name.
     * 
     * @param field
     *            the field to create the method name for
     * @param prefix
     *            the method prefix
     * 
     * @return the method name
     */
    private String createMethodName(FieldDeclaration field, String prefix) {
        StringBuilder methodName = new StringBuilder();
        methodName.append(prefix);
        methodName.append(new String(field.name, 0, 1).toUpperCase());
        methodName.append(new String(field.name, 1, field.name.length - 1));
        return methodName.toString();
    }
}
