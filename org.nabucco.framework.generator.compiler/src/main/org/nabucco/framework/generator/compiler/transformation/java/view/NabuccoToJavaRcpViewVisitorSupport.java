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
package org.nabucco.framework.generator.compiler.transformation.java.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.JavaAstGetterSetterProducer;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewEditVisitorSupport
 * 
 * @author Silas Schwarz, Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewVisitorSupport extends NabuccoToJavaVisitorSupport implements
        ViewConstants, CollectionConstants {

    /** Import String for DatatypeState. */
    private static final String COM_PRODYNA_NABUCCO_BASE_FACADE_DATATYPE_DATATYPE_STATE = "org.nabucco.framework.base.facade.datatype.DatatypeState";

    /** Mapping from field name to field type properties of all datatypes. */
    private Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> fieldNameToFieldTypeProperties = new HashMap<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>>();

    /** Mapping from Datatype/Enumeration/Basetype to field name to type reference. */
    private Map<String, Map<String, JavaAstContainter<TypeReference>>> fieldNameToTypeReference = new HashMap<String, Map<String, JavaAstContainter<TypeReference>>>();

    /** Mapping from field name to field type */
    private Map<String, String> fieldTypeMap = new HashMap<String, String>();

    /** All collected imports. */
    private Set<String> collectedImports = new HashSet<String>();

    /** All mapped fields that are used. */
    private Set<String> mappedFieldsInUse;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewVisitorSupport} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewVisitorSupport(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    /**
     * @return Returns the mappedFieldsInUse.
     */
    public Set<String> getMappedFieldsInUse() {
        return mappedFieldsInUse;
    }

    /**
     * @param mappedFieldsInUse
     *            The mappedFieldsInUse to set.
     */
    public void setMappedFieldsInUse(Set<String> mappedFieldsInUse) {
        this.mappedFieldsInUse = mappedFieldsInUse;
    }

    /**
     * @return Returns the fieldNameToFieldTypeProperties.
     */
    public Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> getFieldNameToFieldTypeProperties() {
        return fieldNameToFieldTypeProperties;
    }

    /**
     * @return Returns the fieldNameToTypeReference.
     */
    public Map<String, Map<String, JavaAstContainter<TypeReference>>> getFieldNameToTypeReference() {
        return fieldNameToTypeReference;
    }

    /**
     * Returns the collected ImportStatements.
     * 
     * @return List of imports
     */
    public List<ImportReference> getCollectedImports() {
        List<ImportReference> result = new ArrayList<ImportReference>();
        // Assumption that we found at least on datetype so we will need DatatypeState
        if (!this.collectedImports.isEmpty()) {
            this.collectedImports.add(COM_PRODYNA_NABUCCO_BASE_FACADE_DATATYPE_DATATYPE_STATE);
        }
        for (String currentImport : this.collectedImports) {
            try {
                result.add(JavaAstModelProducer.getInstance().createImportReference(currentImport));
            } catch (JavaModelException e) {
                throw new NabuccoVisitorException(
                        "Error creating import reference for view support.", e);
            }
        }
        return result;
    }

    /**
     * Manipulates a given setter method by adding a setter call for a given field
     * 
     * @param setter
     *            setter method to manipulate
     * @param mappedField
     *            field to be set by setter
     * @throws NabuccoVisitorException
     *             if the manipulation fails
     * 
     * @throws JavaModelException
     */
    public void changeSetter(MethodDeclaration setter, String mappedField)
            throws NabuccoVisitorException, JavaModelException {
        String[] accessPath = mappedField.split(FIELD_SEPARATOR);
        String localField = accessPath[0];
        String referencedFieldTypeKey = accessPath[1];

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String parameterName = NEW
                + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);

        // method signature
        String setterName = PREFIX_SETTER
                + NabuccoTransformationUtility.firstToUpper(localField)
                + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);

        javaFactory.getJavaAstMethod().setMethodName(setter, setterName);
        javaFactory.getJavaAstArgument().setName(setter.arguments[0], parameterName);

        IfStatement ifStatement = (IfStatement) setter.statements[0];
        OR_OR_Expression ifChecks = (OR_OR_Expression) ifStatement.condition;
        SingleNameReference receiver = producer.createSingleNameReference(localField);
        ((BinaryExpression) ifChecks.left).left = receiver;
        MessageSend getterCall = (MessageSend) ((EqualExpression) ifChecks.right).left;

        String methodName = GET + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);
        javaFactory.getJavaAstMethodCall().setMethodName(methodName, getterCall);
        javaFactory.getJavaAstMethodCall().setMethodReceiver(receiver, getterCall);

        // IF BLOCK CONTENT
        Block ifBlock = (Block) ifStatement.thenStatement;
        LocalDeclaration createNew = ((LocalDeclaration) ifBlock.statements[0]);

        JavaAstContainter<TypeReference> container;

        Map<String, JavaAstContainter<TypeReference>> basetypeMap = this.fieldNameToFieldTypeProperties
                .get(localField).get(BASETYPE);
        Map<String, JavaAstContainter<TypeReference>> enumerationMap = this.fieldNameToFieldTypeProperties
                .get(localField).get(ENUMERATION);
        if (basetypeMap != null && basetypeMap.containsKey(referencedFieldTypeKey)) {
            createNew.type = this.fieldNameToFieldTypeProperties.get(localField).get(BASETYPE)
                    .get(referencedFieldTypeKey).getAstNode();
            container = this.fieldNameToFieldTypeProperties.get(localField).get(BASETYPE)
                    .get(referencedFieldTypeKey);
        } else if (enumerationMap != null && enumerationMap.containsKey(referencedFieldTypeKey)) {
            createNew.type = this.fieldNameToFieldTypeProperties.get(localField).get(ENUMERATION)
                    .get(referencedFieldTypeKey).getAstNode();
            container = this.fieldNameToFieldTypeProperties.get(localField).get(ENUMERATION)
                    .get(referencedFieldTypeKey);
        } else {
            throw new NabuccoVisitorException("Used MappedField \""
                    + referencedFieldTypeKey + "\" is no Basetype or Enumeration.");
        }

        createNew.name = referencedFieldTypeKey.toCharArray();

        for (String importString : container.getImports()) {
            this.collectedImports.add(importString);
        }

        ((AllocationExpression) createNew.initialization).type = container.getAstNode();
        ((SingleNameReference) ((MessageSend) ifBlock.statements[1]).receiver).token = localField
                .toCharArray();
        ((MessageSend) ifBlock.statements[1]).selector = (PREFIX_SETTER + NabuccoTransformationUtility
                .firstToUpper(referencedFieldTypeKey)).toCharArray();
        ((SingleNameReference) ((MessageSend) ifBlock.statements[1]).arguments[0]).token = referencedFieldTypeKey
                .toCharArray();

        // OUTSIDE THE IF
        // getting old value
        MessageSend gettingOldValue = (MessageSend) ((LocalDeclaration) setter.statements[1]).initialization;

        ((SingleNameReference) (((MessageSend) gettingOldValue.receiver)).receiver).token = localField
                .toCharArray();
        ((MessageSend) (((MessageSend) ((LocalDeclaration) setter.statements[1]).initialization).receiver)).selector = (GET + NabuccoTransformationUtility
                .firstToUpper(referencedFieldTypeKey)).toCharArray();
        // setting new value
        MessageSend settingNewValue = ((MessageSend) setter.statements[2]);
        ((SingleNameReference) ((MessageSend) settingNewValue.receiver).receiver).token = localField
                .toCharArray();
        ((MessageSend) settingNewValue.receiver).selector = (GET + NabuccoTransformationUtility
                .firstToUpper(referencedFieldTypeKey)).toCharArray();
        ((SingleNameReference) settingNewValue.arguments[0]).token = parameterName.toCharArray();
        ((SingleNameReference) ((MessageSend) setter.statements[3]).arguments[0]).token = (PROPERTY
                + UNDERSCORE + mappedField.toUpperCase().replace(PKG_SEPARATOR, UNDERSCORE))
                .toCharArray();
        ((SingleNameReference) ((MessageSend) setter.statements[3]).arguments[2]).token = parameterName
                .toCharArray();
        AND_AND_Expression lateIfConfition = ((AND_AND_Expression) ((IfStatement) setter.statements[4]).condition);
        ((SingleNameReference) ((MessageSend) ((UnaryExpression) lateIfConfition.left).expression).arguments[0]).token = parameterName
                .toCharArray();
        ((SingleNameReference) ((MessageSend) ((MessageSend) lateIfConfition.right).receiver).receiver).token = localField
                .toCharArray();
        ((SingleNameReference) ((MessageSend) ((Block) ((IfStatement) setter.statements[4]).thenStatement).statements[0]).receiver).token = localField
                .toCharArray();

    }

    public void changeSetterDatatype(MethodDeclaration setter, String mappedField)
            throws NabuccoVisitorException, JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        String[] accessPath = mappedField.split(FIELD_SEPARATOR);
        String localField = accessPath[0];
        String referencedFieldTypeKey = accessPath[1];
        String basetype = accessPath[2];
        String basetypeType = NabuccoTransformationUtility.firstToUpper(basetype);

        // method signature
        setter.selector = (PREFIX_SETTER
                + NabuccoTransformationUtility.firstToUpper(localField)
                + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey) + NabuccoTransformationUtility
                .firstToUpper(basetype)).toCharArray();

        SingleNameReference localFieldReference = jamp.createSingleNameReference(localField);
        MessageSend leftOfElseCondition = jamp.createMessageSend(
                GET + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey),
                localFieldReference, null);
        MessageSend third = jamp.createMessageSend(
                GET + NabuccoTransformationUtility.firstToUpper(basetype), leftOfElseCondition,
                null);
        MessageSend fourth = jamp.createMessageSend(GET_VALUE, third, null);
        SingleNameReference fieldValueReference = jamp.createSingleNameReference(FIELD_VALUE);

        List<Expression> args = new ArrayList<Expression>();
        args.add(fieldValueReference);
        MessageSend thirdStatementMS = jamp.createMessageSend(SET_VALUE, third, args);

        String propertyString = PROPERTY + UNDERSCORE;
        propertyString = propertyString + localField.toUpperCase() + UNDERSCORE;
        propertyString = propertyString + referencedFieldTypeKey.toUpperCase() + UNDERSCORE;
        propertyString = propertyString + basetype.toUpperCase();
        SingleNameReference property = jamp.createSingleNameReference(propertyString);

        MessageSend datatypeStateMS = jamp.createMessageSend(GET_DATATYPE_STATE,
                leftOfElseCondition, null);
        List<Expression> arguments = new ArrayList<Expression>();
        SingleNameReference datatypeStateReference = jamp
                .createSingleNameReference(DATATYPE_STATE_PERSISTENT);
        arguments.add(datatypeStateReference);
        MessageSend fifthStatementConditionMS = jamp.createMessageSend(EQUALS, datatypeStateMS,
                arguments);
        datatypeStateReference = jamp.createSingleNameReference(DATATYPE_STATE_MODIFIED);
        arguments.clear();
        arguments.add(datatypeStateReference);
        MessageSend fifthStatementThenStatementMS = jamp.createMessageSend(SET_DATATYPE_STATE,
                leftOfElseCondition, arguments);

        TypeReference fieldReference = jamp.createTypeReference(basetypeType, false);
        AllocationExpression initializationExpression = jamp.createAllocationExpression(
                fieldReference, null);
        datatypeStateReference = jamp.createSingleNameReference(FIELD);
        arguments.clear();
        arguments.add(datatypeStateReference);
        MessageSend setFieldMS = jamp.createMessageSend(PREFIX_SETTER
                + NabuccoTransformationUtility.firstToUpper(basetype), leftOfElseCondition,
                arguments);

        IfStatement ifStatement = (IfStatement) setter.statements[0];
        OR_OR_Expression ifStatementCondition = (OR_OR_Expression) ifStatement.condition;

        // change condition
        EqualExpression leftCondition = (EqualExpression) ifStatementCondition.left;
        EqualExpression rightCondition = (EqualExpression) ifStatementCondition.right;

        leftCondition.left = localFieldReference;
        rightCondition.left = third.receiver;

        IfStatement firstStatement = (IfStatement) setter.statements[1];
        LocalDeclaration secondStatement = (LocalDeclaration) setter.statements[2];
        MessageSend fourthStatement = (MessageSend) setter.statements[4];
        IfStatement fifthStatement = (IfStatement) setter.statements[5];

        // change first statement
        EqualExpression condition = (EqualExpression) firstStatement.condition;
        condition.left = third;
        Block thenStatement = (Block) firstStatement.thenStatement;
        LocalDeclaration firstStatementOfThenStatement = (LocalDeclaration) thenStatement.statements[0];
        firstStatementOfThenStatement.type = fieldReference;
        firstStatementOfThenStatement.initialization = initializationExpression;
        thenStatement.statements[2] = setFieldMS;

        // change second statement
        secondStatement.initialization = fourth;

        // change third statement
        setter.statements[3] = thirdStatementMS;

        // change fourth statement
        fourthStatement.arguments[0] = property;

        // change fifth statement
        AND_AND_Expression fifthStatementCondition = (AND_AND_Expression) fifthStatement.condition;
        fifthStatementCondition.right = fifthStatementConditionMS;
        Block fifthStatementThenStatement = (Block) fifthStatement.thenStatement;
        fifthStatementThenStatement.statements[0] = fifthStatementThenStatementMS;
    }

    /**
     * Manipulates a given getter to return a given field
     * 
     * @param getterMethod
     *            a getter method definition
     * @param mappedField
     *            a mapped field
     * @throws JavaModelException
     */
    public void changeGetter(MethodDeclaration getterMethod, String mappedField)
            throws JavaModelException {
        String[] accessPath = mappedField.split(FIELD_SEPARATOR);
        String localField = accessPath[0];
        String referencedFieldTypeKey = accessPath[1];

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = GET
                + NabuccoTransformationUtility.firstToUpper(localField)
                + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);

        javaFactory.getJavaAstMethod().setMethodName(getterMethod, name);

        OR_OR_Expression or = ((OR_OR_Expression) ((IfStatement) getterMethod.statements[0]).condition);

        EqualExpression first = (EqualExpression) ((OR_OR_Expression) or.left).left;
        EqualExpression second = (EqualExpression) ((OR_OR_Expression) or.left).right;
        EqualExpression third = (EqualExpression) or.right;

        MessageSend getValueCall = (MessageSend) third.left;
        MessageSend getFieldCall = (MessageSend) getValueCall.receiver;

        String methodName = GET + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);
        SingleNameReference receiver = producer.createSingleNameReference(localField);
        javaFactory.getJavaAstMethodCall().setMethodName(methodName, getFieldCall);
        javaFactory.getJavaAstMethodCall().setMethodReceiver(receiver, getFieldCall);

        getFieldCall = (MessageSend) second.left;

        methodName = GET + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);
        receiver = producer.createSingleNameReference(localField);
        javaFactory.getJavaAstMethodCall().setMethodName(methodName, getFieldCall);
        javaFactory.getJavaAstMethodCall().setMethodReceiver(receiver, getFieldCall);

        first.left = producer.createSingleNameReference(localField);

        ReturnStatement returnStatement = (ReturnStatement) getterMethod.statements[1];

        getValueCall = (MessageSend) returnStatement.expression;
        getFieldCall = (MessageSend) getValueCall.receiver;

        methodName = GET + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);
        receiver = producer.createSingleNameReference(localField);
        javaFactory.getJavaAstMethodCall().setMethodReceiver(receiver, getFieldCall);
        javaFactory.getJavaAstMethodCall().setMethodName(methodName, getFieldCall);
    }

    public void changeGetterDatatype(MethodDeclaration getterMethod, String mappedField)
            throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        String[] accessPath = mappedField.split(FIELD_SEPARATOR);
        String localField = accessPath[0];
        String referencedFieldTypeKey = accessPath[1];
        String basetype = accessPath[2];

        getterMethod.selector = (GET
                + NabuccoTransformationUtility.firstToUpper(localField)
                + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey) + NabuccoTransformationUtility
                .firstToUpper(basetype)).toCharArray();

        IfStatement ifStatement = (IfStatement) getterMethod.statements[0];
        OR_OR_Expression condition = (OR_OR_Expression) ifStatement.condition;
        OR_OR_Expression leftOfCondition = (OR_OR_Expression) condition.left;
        OR_OR_Expression leftOfLeftOfCondition = (OR_OR_Expression) leftOfCondition.left;

        EqualExpression firstExpression = (EqualExpression) leftOfLeftOfCondition.left;
        EqualExpression secondExpression = (EqualExpression) leftOfLeftOfCondition.right;
        EqualExpression thirdExpression = (EqualExpression) leftOfCondition.right;
        EqualExpression fourthExpression = (EqualExpression) condition.right;

        SingleNameReference datatypeReference = jamp.createSingleNameReference(localField);
        MessageSend second = jamp.createMessageSend(
                GET + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey),
                datatypeReference, null);
        MessageSend third = jamp.createMessageSend(
                GET + NabuccoTransformationUtility.firstToUpper(basetype), second, null);
        MessageSend fourth = jamp.createMessageSend(GET_VALUE, third, null);

        // change first expression
        firstExpression.left = datatypeReference;

        // change second expression
        secondExpression.left = second;

        // change third expression
        thirdExpression.left = third;

        // change fourth expression
        fourthExpression.left = fourth;

        // change returnType
        ReturnStatement returnStatement = (ReturnStatement) getterMethod.statements[1];
        returnStatement.expression = fourth;
    }

    /**
     * Manipulates a given getter to return a given field
     * 
     * @param getterMethod
     *            a getter method definition
     * @param mappedField
     *            a mapped field
     */
    public void changeGetterCombo(MethodDeclaration getterMethod, String mappedField) {
        String[] accessPath = mappedField.split(FIELD_SEPARATOR);
        String localField = accessPath[0];
        String referencedFieldTypeKey = accessPath[1];

        getterMethod.selector = (GET + NabuccoTransformationUtility.firstToUpper(localField) + NabuccoTransformationUtility
                .firstToUpper(referencedFieldTypeKey)).toCharArray();

        ((SingleNameReference) ((EqualExpression) ((OR_OR_Expression) ((IfStatement) getterMethod.statements[0]).condition).left).left).token = localField
                .toCharArray();
        ((SingleNameReference) ((MessageSend) ((EqualExpression) ((OR_OR_Expression) ((IfStatement) getterMethod.statements[0]).condition).right).left).receiver).token = localField
                .toCharArray();
        ((MessageSend) ((EqualExpression) ((OR_OR_Expression) ((IfStatement) getterMethod.statements[0]).condition).right).left).selector = (GET + NabuccoTransformationUtility
                .firstToUpper(referencedFieldTypeKey)).toCharArray();

        // return value
        MessageSend expression = ((MessageSend) ((ReturnStatement) getterMethod.statements[1]).expression);
        MessageSend receiver = ((MessageSend) expression.receiver);
        ((SingleNameReference) receiver.receiver).token = localField.toCharArray();
        receiver.selector = (GET + NabuccoTransformationUtility
                .firstToUpper(referencedFieldTypeKey)).toCharArray();
    }

    /**
     * Manipulates a given setter method by adding a setter call for a given field
     * 
     * @param setter
     *            setter method to manipulate
     * @param mappedField
     *            field to be set by setter
     * @throws NabuccoVisitorException
     *             if the manipulation fails
     * @throws JavaModelException
     */
    public void changeSetterCombo(MethodDeclaration setter, String mappedField)
            throws NabuccoVisitorException, JavaModelException {
        String[] accessPath = mappedField.split(FIELD_SEPARATOR);
        String localField = accessPath[0];
        String referencedFieldTypeKey = accessPath[1];

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String parameterName = NEW
                + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);

        // Signature
        StringBuilder setterName = new StringBuilder();
        setterName.append(PREFIX_SETTER);
        setterName.append(NabuccoTransformationUtility.firstToUpper(localField));
        setterName.append(NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey));

        javaFactory.getJavaAstArgument().setName(setter.arguments[0], parameterName);
        javaFactory.getJavaAstMethod().setMethodName(setter, setterName.toString());

        String enumName = NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);

        // 1. Statement
        LocalDeclaration localDeclaration = (LocalDeclaration) setter.statements[0];
        MessageSend callName = (MessageSend) localDeclaration.initialization;
        MessageSend callGetEnum = ((MessageSend) callName.receiver);
        javaFactory.getJavaAstMethodCall().setMethodName(PREFIX_GETTER + enumName, callGetEnum);
        callGetEnum.receiver = producer.createFieldThisReference(localField);

        // 2. Statement
        MessageSend callSetEnum = (MessageSend) setter.statements[1];
        javaFactory.getJavaAstMethodCall().setMethodName(PREFIX_SETTER + enumName, callSetEnum);
        callSetEnum.receiver = producer.createFieldThisReference(localField);
        callSetEnum.arguments[0] = producer.createSingleNameReference(parameterName);

        // 3. Statement
        MessageSend callUpdateProperty = (MessageSend) setter.statements[2];
        StringBuilder property = new StringBuilder();
        property.append(PROPERTY);
        property.append(UNDERSCORE);
        property.append(localField.toUpperCase());
        property.append(UNDERSCORE);
        property.append(referencedFieldTypeKey.toUpperCase());

        callUpdateProperty.arguments[0] = producer.createSingleNameReference(property.toString());
        callUpdateProperty.arguments[2] = producer.createSingleNameReference(parameterName);

        // if-Statement
        IfStatement ifStatement = (IfStatement) setter.statements[3];
        AND_AND_Expression condition = (AND_AND_Expression) ifStatement.condition;

        MessageSend right = (MessageSend) condition.right;
        UnaryExpression left = (UnaryExpression) condition.left;

        MessageSend callGetState = (MessageSend) right.receiver;
        callGetState.receiver = producer.createSingleNameReference(localField);

        MessageSend leftEquals = (MessageSend) left.expression;
        leftEquals.arguments[0] = producer.createSingleNameReference(parameterName);

        Block then = (Block) ifStatement.thenStatement;
        MessageSend callSetState = (MessageSend) then.statements[0];

        callSetState.receiver = producer.createSingleNameReference(localField);

    }

    /**
     * Visits a field defined for a ViewModel and extracts all necessary type information.
     * 
     * @param datatypeDeclaration
     *            a basetype datatype declaration
     * @param context
     *            current visitors context
     */
    public void createMappingInformation(DatatypeDeclaration datatypeDeclaration,
            NabuccoToJavaVisitorContext context) {

        String fieldName = datatypeDeclaration.nodeToken2.tokenImage;
        String fieldType = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
        String importString = super.resolveImport(fieldType);

        this.fieldTypeMap.put(fieldName, fieldType);

        // TODO: Create clone() method in visitor context!
        NabuccoToJavaVisitorContext iterationContext = new NabuccoToJavaVisitorContext();
        iterationContext.setRootDir(context.getRootDir());
        iterationContext.setOutDir(context.getOutDir());

        NabuccoToJavaDatatypeFieldCollectionVisitor visitor = new NabuccoToJavaDatatypeFieldCollectionVisitor(
                iterationContext);

        try {
            String pkg = super.getVisitorContext().getPackage();
            NabuccoDependencyResolver.getInstance().resolveDependency(context, pkg, importString)
                    .getModel().getUnit().accept(visitor, this.getFieldNameToTypeReference());
            this.getFieldNameToFieldTypeProperties().put(datatypeDeclaration.nodeToken2.tokenImage,
                    this.getFieldNameToTypeReference());
            context.getContainerList().addAll(iterationContext.getContainerList());
        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException(e);
        }

    }

    /**
     * Creates getter and setter method for a given field and adds them to the getContainerList of
     * the given visitorContext.
     * 
     * @param datatypeDeclaration
     *            the datatypedeclaration of the field.
     * @param visitorContext
     *            the NabuccoToJavaVisitorContext.
     */
    public void createGetterSetterForModelField(DatatypeDeclaration datatypeDeclaration,
            NabuccoToJavaVisitorContext visitorContext) {

        String fieldName = datatypeDeclaration.nodeToken2.tokenImage;
        String fieldType = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;

        try {

            createGetterSetterForFieldHelper(visitorContext, fieldName, fieldType);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating getter and setter for field "
                    + fieldName + ".", e);
        } catch (JavaTemplateException e) {
            throw new NabuccoVisitorException("Error creating getter and setter for field "
                    + fieldName + ".", e);
        }
    }

    private void createGetterSetterForFieldHelper(NabuccoToJavaVisitorContext visitorContext,
            String fieldName, String fieldType) throws JavaTemplateException, JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        JavaCompilationUnit dataTypeUnit = super
                .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
        TypeDeclaration dataTypeType = dataTypeUnit
                .getType(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
        MethodDeclaration setterMethod = (MethodDeclaration) javaFactory.getJavaAstType()
                .getMethod(dataTypeType, new JavaAstMethodSignature(SET_DATATYPE, DATATYPE));
        setterMethod.selector = (PREFIX_SETTER + NabuccoTransformationUtility
                .firstToUpper(fieldName)).toCharArray();
        ((SingleTypeReference) setterMethod.arguments[0].type).token = fieldType.toCharArray();
        ((SingleTypeReference) ((LocalDeclaration) setterMethod.statements[0]).type).token = fieldType
                .toCharArray();
        ((FieldReference) ((LocalDeclaration) setterMethod.statements[0]).initialization).token = fieldName
                .toCharArray();
        ((FieldReference) ((Assignment) setterMethod.statements[1]).lhs).token = fieldName
                .toCharArray();
        // add foreach property one update-statement

        // FIXME: Potential null-pointer!!!
        Map<String, JavaAstContainter<TypeReference>> usedSubSet = getOnlyUsedElements(fieldName);
        int newSize = usedSubSet.size() + setterMethod.statements.length;
        Statement[] newArray = Arrays.copyOf(setterMethod.statements, newSize);
        int position = setterMethod.statements.length;
        for (String property : usedSubSet.keySet()) {
            JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
            String prop = PROPERTY
                    + UNDERSCORE + fieldName.toUpperCase() + UNDERSCORE + property.toUpperCase();

            List<Expression> args = new LinkedList<Expression>();

            SingleNameReference snr = jamp.createSingleNameReference(prop);
            // second param
            SingleNameReference left = jamp.createSingleNameReference(OLD_VALUE);
            Literal right = jamp.createLiteral(NULL, LiteralType.NULL_LITERAL);
            Expression condition = jamp.createBinaryExpression(
                    BinaryExpressionType.EQUAL_EXPRESSION, left, right, EqualExpression.NOT_EQUAL);
            SingleNameReference receiver = jamp.createSingleNameReference(OLD_VALUE);
            Expression valueIfTrue = jamp.createMessageSend(
                    GET + NabuccoTransformationUtility.firstToUpper(property), receiver, null);
            Expression valueIfFalse = jamp.createLiteral(EMPTY_STRING, LiteralType.STRING_LITERAL);
            ConditionalExpression ceOld = jamp.createConditionalExpression(condition, valueIfTrue,
                    valueIfFalse);
            // third param
            left = jamp.createSingleNameReference(NEW_VALUE);
            condition = jamp.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION, left,
                    right, EqualExpression.NOT_EQUAL);
            receiver = jamp.createSingleNameReference(NEW_VALUE);
            valueIfTrue = jamp.createMessageSend(
                    GET + NabuccoTransformationUtility.firstToUpper(property), receiver, null);
            valueIfFalse = jamp.createLiteral(EMPTY_STRING, LiteralType.STRING_LITERAL);
            ConditionalExpression ceNew = jamp.createConditionalExpression(condition, valueIfTrue,
                    valueIfFalse);

            args.add(snr);
            args.add(ceOld);
            args.add(ceNew);

            Expression newStatementExpression = jamp.createSingleNameReference(THIS);
            Statement newStatement = jamp.createMessageSend(UPDATE_PROPERTY,
                    newStatementExpression, args);
            newArray[position] = newStatement;

            position++;
        }
        setterMethod.statements = newArray;
        JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                setterMethod, JavaAstType.METHOD);

        visitorContext.getContainerList().add(container);
    }

    /**
     * Creates the setter for the set string representation in view model
     * 
     * @param field
     *            the field to create the setter
     * @param constantName
     *            name of the update property constant
     * 
     * @return the setter container
     */
    public JavaAstContainter<MethodDeclaration> createSetterForModelSet(FieldDeclaration field,
            String constantName) {

        try {
            MethodDeclaration setter = JavaAstGetterSetterProducer.getInstance().produceSetter(
                    field);

            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            String fieldName = javaFactory.getJavaAstField().getFieldName(field);
            FieldReference fieldReference = producer.createFieldThisReference(fieldName);
            SingleNameReference nameReference = producer.createSingleNameReference(fieldName);

            SingleNameReference propertyReference = producer.createSingleNameReference(constantName
                    .toUpperCase());

            Assignment assignment = producer.createAssignment(fieldReference, nameReference);

            SuperReference receiver = producer.createSuperReference();
            MessageSend updatePropertyCall = producer.createMessageSend(UPDATE_PROPERTY, receiver,
                    Arrays.asList(propertyReference, fieldReference, assignment));

            setter.statements[0] = updatePropertyCall;

            // Container

            JavaAstContainter<MethodDeclaration> containter = new JavaAstContainter<MethodDeclaration>(
                    setter, JavaAstType.METHOD);

            return containter;

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error during Java model commonview processing.", me);
        }
    }

    public void createSetterForFieldDatatype(NabuccoToJavaVisitorContext visitorContext,
            String localField, String fieldName, String fieldType, String property)
            throws JavaTemplateException, JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        JavaCompilationUnit dataTypeUnit = super
                .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
        TypeDeclaration dataTypeType = dataTypeUnit
                .getType(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);

        // select the method
        MethodDeclaration setterMethod = (MethodDeclaration) javaFactory.getJavaAstType()
                .getMethod(dataTypeType,
                        new JavaAstMethodSignature(SET_DATATYPE_DATATYPE, DATATYPE));
        // change the name
        setterMethod.selector = (PREFIX_SETTER + NabuccoTransformationUtility
                .firstToUpper(fieldName)).toCharArray();

        TypeReference datatypeReference = jamp.createTypeReference(fieldType, false);
        FieldReference localFieldReference = jamp.createFieldThisReference(localField);
        MessageSend localFieldGetProperty = jamp.createMessageSend(GET
                + NabuccoTransformationUtility.firstToUpper(fieldName), localFieldReference, null);
        ThisReference thisReference = jamp.createThisReference();
        MessageSend getLocalField = jamp.createMessageSend(
                GET + NabuccoTransformationUtility.firstToUpper(localField), thisReference, null);
        SingleNameReference newValue = jamp.createSingleNameReference(NEW_VALUE);
        List<Expression> arguments = new ArrayList<Expression>();
        arguments.add(newValue);
        MessageSend getLocalFieldSetProperty = jamp.createMessageSend(PREFIX_SETTER
                + NabuccoTransformationUtility.firstToUpper(fieldName), getLocalField, arguments);

        MessageSend localFieldSetProperty = jamp.createMessageSend(PREFIX_SETTER
                + NabuccoTransformationUtility.firstToUpper(fieldName), localFieldReference,
                arguments);

        SingleNameReference oldValue = jamp.createSingleNameReference(OLD_VALUE);
        MessageSend oldValueGetProperty = jamp.createMessageSend(
                GET + NabuccoTransformationUtility.firstToUpper(property), oldValue, null);
        MessageSend oldValueGetPropertyGetValue = jamp.createMessageSend(GET_VALUE,
                oldValueGetProperty, null);

        MessageSend newValueGetProperty = jamp.createMessageSend(
                GET + NabuccoTransformationUtility.firstToUpper(property), newValue, null);
        MessageSend newValueGetPropertyGetValue = jamp.createMessageSend(GET_VALUE,
                newValueGetProperty, null);

        arguments.clear();
        String propertyString = PROPERTY
                + UNDERSCORE + localField.toUpperCase() + UNDERSCORE + fieldName.toUpperCase()
                + UNDERSCORE + property.toUpperCase();

        SingleNameReference propertyReference = jamp.createSingleNameReference(propertyString);
        SingleNameReference oldValueStringReference = jamp.createSingleNameReference(OLD_VALUE
                + STRING);
        SingleNameReference newValueStringReference = jamp.createSingleNameReference(NEW_VALUE
                + STRING);
        arguments.add(propertyReference);
        arguments.add(oldValueStringReference);
        arguments.add(newValueStringReference);
        MessageSend updateProperty = jamp.createMessageSend(UPDATE_PROPERTY, thisReference,
                arguments);

        // change argument type
        Argument methodArgument = setterMethod.arguments[0];
        methodArgument.type = datatypeReference;

        // change statements
        LocalDeclaration firstStatement = (LocalDeclaration) setterMethod.statements[0];
        IfStatement fifthStatement = (IfStatement) setterMethod.statements[4];
        IfStatement seventhStatement = (IfStatement) setterMethod.statements[6];

        // change first statement
        firstStatement.type = datatypeReference;
        firstStatement.initialization = localFieldGetProperty;

        // change second statement
        setterMethod.statements[1] = getLocalFieldSetProperty;

        // change third statement
        setterMethod.statements[2] = localFieldSetProperty;

        // change fifth statement
        // change condition
        AND_AND_Expression fifthCondition = (AND_AND_Expression) fifthStatement.condition;
        EqualExpression fifthConditionRight = (EqualExpression) fifthCondition.right;
        fifthConditionRight.left = oldValueGetProperty;

        // change then statement
        Block fifthThen = (Block) fifthStatement.thenStatement;
        Assignment fifthThenStatement = (Assignment) fifthThen.statements[0];
        fifthThenStatement.expression = oldValueGetPropertyGetValue;

        // change seventh statement
        // change condition
        AND_AND_Expression seventhCondition = (AND_AND_Expression) seventhStatement.condition;
        EqualExpression seventhConditionRight = (EqualExpression) seventhCondition.right;
        seventhConditionRight.left = newValueGetProperty;

        // change then statement
        Block seventhThen = (Block) seventhStatement.thenStatement;
        Assignment seventhThenStatement = (Assignment) seventhThen.statements[0];
        seventhThenStatement.expression = newValueGetPropertyGetValue;

        // change eighth statement
        setterMethod.statements[7] = updateProperty;

        JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                setterMethod, JavaAstType.METHOD);

        visitorContext.getContainerList().add(container);
    }

    /**
     * Returns a map where only all used elements are put into.
     * 
     * @param entry
     *            the string from where to start from
     * @return map of all used elements
     */
    private Map<String, JavaAstContainter<TypeReference>> getOnlyUsedElements(String entry) {
        Set<String> itemsToRemove = new HashSet<String>();
        for (Map.Entry<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> current : this
                .getFieldNameToFieldTypeProperties().entrySet()) {
            // assure right entry
            if (current.getKey().compareTo(entry) == 0) {
                Map<String, JavaAstContainter<TypeReference>> currentMap = new HashMap<String, JavaAstContainter<TypeReference>>();
                if (current.getValue() != null && current.getValue().get(BASETYPE) != null) {
                    currentMap.putAll(current.getValue().get(BASETYPE));
                }
                if (current.getValue() != null && current.getValue().get(ENUMERATION) != null) {
                    currentMap.putAll(current.getValue().get(ENUMERATION));
                }
                for (String currentKey : currentMap.keySet()) {
                    if (!this.getMappedFieldsInUse().contains(entry + PKG_SEPARATOR + currentKey)) {
                        itemsToRemove.add(currentKey);
                    }
                }
                for (String itemToRemove : itemsToRemove) {
                    currentMap.remove(itemToRemove);
                }
                return currentMap;
            }
            for (Map.Entry<String, JavaAstContainter<TypeReference>> currentDatatype : current
                    .getValue().get(DATATYPE).entrySet()) {
                if (currentDatatype.getKey().compareTo(entry) == 0) {
                    Map<String, JavaAstContainter<TypeReference>> currentMap = new HashMap<String, JavaAstContainter<TypeReference>>();
                    currentMap.put(currentDatatype.getKey(),
                            current.getValue().get(DATATYPE).get(currentDatatype.getKey()));
                    return currentMap;
                }
            }
        }

        return null;
    }

    public void changeSetterSet(MethodDeclaration setter, String mappedField)
            throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String[] accessPath = mappedField.split(FIELD_SEPARATOR);
        String localField = accessPath[0];
        String childName = accessPath[1];
        String childGetterName = PREFIX_GETTER
                + NabuccoTransformationUtility.firstToUpper(childName);

        String setterName = PREFIX_SETTER + NabuccoTransformationUtility.firstToUpper(localField);

        StringBuilder delegateSetterName = new StringBuilder();
        delegateSetterName.append(PREFIX_SETTER);

        for (int i = 0; i < accessPath.length; i++) {
            String name = NabuccoTransformationUtility.firstToUpper(accessPath[i]);
            delegateSetterName.append(name);
        }

        TypeReference typeReference = producer.createTypeReference(
                this.fieldTypeMap.get(localField), false);

        FieldReference fieldReference = producer.createFieldThisReference(localField);

        // Signature

        javaFactory.getJavaAstMethod().setMethodName(setter, setterName);

        ParameterizedSingleTypeReference typeRef = (ParameterizedSingleTypeReference) javaFactory
                .getJavaAstArgument().getType(setter.arguments[0]);

        typeRef.typeArguments[0] = typeReference;

        javaFactory.getJavaAstArgument().setType(setter.arguments[0], typeRef);

        // Body

        // 1. Statement
        IfStatement ifStatement = (IfStatement) setter.statements[0];
        Assignment assignment = (Assignment) ((Block) ifStatement.thenStatement).statements[0];
        AllocationExpression allocation = (AllocationExpression) assignment.expression;
        ((ParameterizedSingleTypeReference) allocation.type).typeArguments[0] = typeReference;

        // 2. Statement
        Assignment fieldAssignment = (Assignment) setter.statements[1];
        fieldAssignment.lhs = fieldReference;

        // 4. Statement
        LocalDeclaration iteratorDeclaration = (LocalDeclaration) setter.statements[3];
        ((ParameterizedSingleTypeReference) iteratorDeclaration.type).typeArguments[0] = typeReference;

        // 5. Statement
        WhileStatement loop = (WhileStatement) setter.statements[4];
        Block loopBlock = (Block) loop.action;

        LocalDeclaration datatype = (LocalDeclaration) loopBlock.statements[0];
        datatype.type = typeReference;

        IfStatement loopIf = (IfStatement) loopBlock.statements[1];
        OR_OR_Expression condition = (OR_OR_Expression) loopIf.condition;
        MessageSend callGetChild = (MessageSend) ((EqualExpression) condition.right).left;
        javaFactory.getJavaAstMethodCall().setMethodName(childGetterName, callGetChild);

        Block elseLoop = (Block) loopIf.elseStatement;
        MessageSend callAppend = (MessageSend) elseLoop.statements[0];
        MessageSend callGetValue = (MessageSend) callAppend.arguments[0];
        callGetChild = (MessageSend) callGetValue.receiver;
        javaFactory.getJavaAstMethodCall().setMethodName(childGetterName, callGetChild);

        // 6. Statement
        MessageSend callSetString = (MessageSend) setter.statements[5];
        javaFactory.getJavaAstMethodCall().setMethodName(delegateSetterName.toString(),
                callSetString);

    }

    /**
     * @param getValues
     * @param currentMappedField
     * @throws JavaModelException
     */
    public static void addGetValuesEntry(MethodDeclaration getValues, String currentMappedField)
            throws JavaModelException {

        String[] tokens = currentMappedField.split("\\.");

        StringBuilder propertyToken = new StringBuilder(PROPERTY);
        StringBuilder methodName = new StringBuilder(GET);
        for (String token : tokens) {
            propertyToken.append(UNDERSCORE).append(token.toUpperCase());
            methodName.append(NabuccoTransformationUtility.firstToUpper(token));
        }

        // inflat the array with a new message send
        Statement[] newStatements = new Statement[getValues.statements.length + 1];

        int index = 0;
        for (; index < getValues.statements.length - 1; index++) {
            newStatements[index] = getValues.statements[index];
        }

        JavaAstModelProducer modelProducer = JavaAstModelProducer.getInstance();
        ThisReference thisReference = modelProducer.createThisReference();
        SingleNameReference methodResult = modelProducer.createSingleNameReference(RESULT);
        SingleNameReference propertyName = modelProducer.createSingleNameReference(propertyToken
                .toString());

        MessageSend operationCall = modelProducer.createMessageSend(methodName.toString(),
                thisReference, Collections.<Expression> emptyList());
        newStatements[index] = modelProducer.createMessageSend("put", methodResult,
                Arrays.asList(new Expression[] { propertyName, operationCall }));

        // shift the last statement to the end
        newStatements[++index] = getValues.statements[index - 1];

        getValues.statements = newStatements;
    }
}
