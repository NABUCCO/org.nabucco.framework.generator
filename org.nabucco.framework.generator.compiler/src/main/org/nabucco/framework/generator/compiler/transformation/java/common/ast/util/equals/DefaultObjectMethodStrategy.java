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
package org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.equals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstType;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;
import org.nabucco.framework.mda.template.java.extract.JavaAstExtractorFactory;

/**
 * DefaultObjectMethodStrategy
 * <p/>
 * Creates the default object methods without any considerations.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class DefaultObjectMethodStrategy implements ObjectMethodStrategy {

    private static final JavaAstMethodSignature HASH_CODE_SIGNATURE = new JavaAstMethodSignature(
            "hashCode");

    private static final JavaAstMethodSignature EQUALS_SIGNATURE = new JavaAstMethodSignature(
            "equals", "Object");

    private static final JavaAstMethodSignature TO_STRING_SIGNATURE = new JavaAstMethodSignature(
            "toString");

    private static final String OTHER = "other";

    private static final String APPENDABLE = "appendable";

    private static final String APPEND = "append";

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            DefaultObjectMethodStrategy.class);

    private JavaAstModelProducer producer;

    /**
     * Private constructor.
     */
    protected DefaultObjectMethodStrategy() {
        this.producer = JavaAstModelProducer.getInstance();
    }

    /**
     * Creates <code>toString()</code>, <code>hashCode()</code> and <code>equals()</code> methods
     * for a {@link TypeDeclaration}. Depending on the type's fields the methods differ from each
     * other.
     * 
     * @param type
     *            the {@link TypeDeclaration} to create <code>toString()</code>,
     *            <code>hashCode()</code> and <code>equals()</code> for.
     * 
     * @throws JavaTemplateException
     */
    @Override
    public void createAllObjectMethods(TypeDeclaration type) throws JavaTemplateException {

        try {
            JavaAstType javaFactory = JavaAstElementFactory.getInstance().getJavaAstType();

            AbstractMethodDeclaration equals = javaFactory.getMethod(type, EQUALS_SIGNATURE);
            AbstractMethodDeclaration hashCode = javaFactory.getMethod(type, HASH_CODE_SIGNATURE);
            AbstractMethodDeclaration toString = javaFactory.getMethod(type, TO_STRING_SIGNATURE);

            if (javaFactory.getFields(type).size() < 1) {
                javaFactory.removeMethod(type, equals);
                javaFactory.removeMethod(type, hashCode);
                return;
            }
            
            boolean removeMethod = true;
            for (FieldDeclaration field : javaFactory.getFields(type)) {
                if (isValid(field)) {
                    removeMethod = false;
                    break;
                }
            }
            if (removeMethod) {
                javaFactory.removeMethod(type, equals);
                javaFactory.removeMethod(type, hashCode);
                return;
            }

            List<Statement> equalsStatementList = createEqualsStatementList(equals, type);
            List<Statement> hashCodeStatementList = createHashCodeStatementList(hashCode);
            List<Statement> toStringStatementList = createToStringStatementList(toString, type);

            for (FieldDeclaration field : javaFactory.getFields(type)) {

                if (isValid(field)) {

                    Statement equalsStatement = createEqualsStatement(field);
                    equalsStatementList.add(equalsStatement);

                    Statement hashCodeStatement = createHashCodeStatement(field,
                            (Assignment) hashCode.statements[2], type.initializerScope);
                    hashCodeStatementList.add(hashCodeStatement);

                    Statement toStringStatement = createToStringStatement(field);
                    toStringStatementList.add(toStringStatement);
                }
            }

            finishEquals(equals, equalsStatementList);
            finishHashCode(hashCode, hashCodeStatementList);
            finishToString(type, toString, toStringStatementList);

        } catch (JavaModelException e) {
            logger.error(e, "Template not valid.");
            throw new JavaTemplateException("Template not valid.", e);
        }
    }

    /**
     * Checks whether a field is generated or not.
     * 
     * @param field
     *            the field to check
     * 
     * @return <b>true</b> if the field should be generated, <b>false</b> if not
     */
    boolean isValid(FieldDeclaration field) {
        return !field.isStatic();
    }

    private List<Statement> createEqualsStatementList(AbstractMethodDeclaration equals,
            TypeDeclaration type) {

        List<Statement> statementList = new ArrayList<Statement>();
        statementList.add(equals.statements[0]);
        statementList.add(equals.statements[1]);
        statementList.add(equals.statements[2]);
        statementList.add(equals.statements[3]);

        SingleTypeReference typeReference = new SingleTypeReference(type.name, 0);
        LocalDeclaration cast = (LocalDeclaration) equals.statements[4];

        cast.type = typeReference;
        ((CastExpression) cast.initialization).type = typeReference;

        statementList.add(cast);

        return statementList;
    }

    private List<Statement> createHashCodeStatementList(AbstractMethodDeclaration hashCode) {

        List<Statement> statementList = new ArrayList<Statement>();
        statementList.add(hashCode.statements[0]);
        statementList.add(hashCode.statements[1]);

        return statementList;
    }

    private List<Statement> createToStringStatementList(AbstractMethodDeclaration toString,
            TypeDeclaration type) throws JavaModelException {

        List<Statement> statementList = new ArrayList<Statement>();
        statementList.add(toString.statements[0]);

        String name = new String(type.name);
        List<Expression> argumentList = new ArrayList<Expression>();

        Literal openXml = producer.createLiteral("<" + name + ">\n", LiteralType.STRING_LITERAL);
        argumentList.add(openXml);

        SingleNameReference receiver = producer.createSingleNameReference(APPENDABLE);
        statementList.add(producer.createMessageSend(APPEND, receiver, argumentList));
        statementList.add(toString.statements[1]);

        return statementList;
    }

    private IfStatement createEqualsStatement(FieldDeclaration field) throws JavaModelException {

        FieldReference thisObject = producer.createFieldThisReference(new String(field.name));
        QualifiedNameReference otherObject = producer.createQualifiedNameReference(OTHER,
                new String(field.name));

        // Common
        Expression innerCondition;
        ReturnStatement falseReturn = new ReturnStatement(new FalseLiteral(0, 0), 0, 0);

        // Condition
        Expression right = producer.createLiteral(null, LiteralType.NULL_LITERAL);
        Expression condition = producer.createBinaryExpression(
                BinaryExpressionType.EQUAL_EXPRESSION, thisObject, right,
                EqualExpression.EQUAL_EQUAL);

        // Then statement
        Block thenStatement = producer.createBlock();
        innerCondition = producer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION,
                otherObject, right, EqualExpression.NOT_EQUAL);

        IfStatement innerIfStatement = producer
                .createIfStatement(innerCondition, falseReturn, null);
        thenStatement.statements = new Statement[] { innerIfStatement };

        // Else statement
        MessageSend equalsCall = producer.createMessageSend(EQUALS_SIGNATURE.getMethodName(),
                thisObject, Arrays.asList(otherObject));

        innerCondition = producer.createUnaryExpression(equalsCall, UnaryExpression.NOT);
        IfStatement elseStatement = producer.createIfStatement(innerCondition, falseReturn, null);

        return producer.createIfStatement(condition, thenStatement, elseStatement);

    }

    private Assignment createHashCodeStatement(FieldDeclaration field, Assignment assignment,
            BlockScope scope) throws JavaTemplateException, JavaModelException {

        // Copy statement from template
        Assignment assignmentCopy = JavaAstExtractorFactory.getInstance().getStatementExtractor()
                .extractStatement(assignment, scope);

        // Shift existing expression to the left
        Expression left = assignmentCopy.expression;

        FieldReference fieldReference = producer.createFieldThisReference(new String(field.name));

        Expression nullLiteral = producer.createLiteral(null, LiteralType.NULL_LITERAL);

        // Ternary operator
        Expression condition = producer.createBinaryExpression(
                BinaryExpressionType.EQUAL_EXPRESSION, fieldReference, nullLiteral,
                EqualExpression.EQUAL_EQUAL);

        Literal valueIfTrue = producer.createLiteral("0", LiteralType.INT_LITERAL);

        MessageSend valueIfFalse = producer.createMessageSend(HASH_CODE_SIGNATURE.getMethodName(),
                fieldReference, null);

        Expression right = producer.createConditionalExpression(condition, valueIfTrue,
                valueIfFalse);

        assignmentCopy.expression = producer.createBinaryExpression(
                BinaryExpressionType.BINARY_EXPRESSION, left, right, BinaryExpression.PLUS);

        return assignmentCopy;

    }

    private Statement createToStringStatement(FieldDeclaration field) throws JavaModelException {

        List<Expression> argumentList = new ArrayList<Expression>();

        String name = new String(field.name);

        FieldReference fieldReference = producer.createFieldThisReference(name);
        Literal openXml = producer.createLiteral("<" + name + ">", LiteralType.STRING_LITERAL);
        Literal closeXml = producer.createLiteral("</" + name + ">\n", LiteralType.STRING_LITERAL);

        BinaryExpression argument = producer.createBinaryExpression(
                BinaryExpressionType.BINARY_EXPRESSION, openXml, fieldReference,
                BinaryExpression.PLUS);

        argument = producer.createBinaryExpression(BinaryExpressionType.BINARY_EXPRESSION,
                argument, closeXml, BinaryExpression.PLUS);
        argumentList.add(argument);

        SingleNameReference receiver = producer.createSingleNameReference(APPENDABLE);

        return producer.createMessageSend(APPEND, receiver, argumentList);

    }

    private void finishHashCode(AbstractMethodDeclaration hashCode,
            List<Statement> hashCodeStatementList) {
        finishMethod(hashCode, hashCodeStatementList);
    }

    private void finishEquals(AbstractMethodDeclaration equals, List<Statement> equalsStatementList) {
        finishMethod(equals, equalsStatementList);
    }

    private void finishToString(TypeDeclaration type, AbstractMethodDeclaration toString,
            List<Statement> toStringStatementList) throws JavaModelException {

        List<Expression> argumentList = new ArrayList<Expression>();

        String literal = "</" + new String(type.name) + ">\n";
        Literal closeXml = producer.createLiteral(literal, LiteralType.STRING_LITERAL);
        argumentList.add(closeXml);

        SingleNameReference receiver = producer.createSingleNameReference(APPENDABLE);
        toStringStatementList.add(producer.createMessageSend(APPEND, receiver, argumentList));
        finishMethod(toString, toStringStatementList);
    }

    private void finishMethod(AbstractMethodDeclaration method, List<Statement> statementList) {
        statementList.add(method.statements[method.statements.length - 1]);
        method.statements = statementList.toArray(new Statement[statementList.size()]);
    }

}
