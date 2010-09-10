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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * CommonViewElementSupport
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class CommonOperationSupport {

    private static JavaAstModelProducer JAMP = JavaAstModelProducer.getInstance();

    static Literal NULL_LITERAL;

    static Literal EMPTY_STRING_LITERAL;

    static synchronized Literal getNullLiteral() throws JavaModelException {
        if (NULL_LITERAL == null) {
            NULL_LITERAL = JAMP.createLiteral("", LiteralType.NULL_LITERAL);
        }
        return NULL_LITERAL;
    }

    static synchronized Literal getEmptyStringLiteral() throws JavaModelException {
        if (EMPTY_STRING_LITERAL == null) {
            EMPTY_STRING_LITERAL = JAMP.createLiteral("", LiteralType.STRING_LITERAL);
        }
        return EMPTY_STRING_LITERAL;
    }

    static MessageSend createOperation(OperationType type, Expression reciever, String property,
            List<Expression> params) throws JavaModelException {
        return JAMP.createMessageSend(type.format(property), reciever, params);
    }

    static MessageSend createGetter(Expression reciever, String property) throws JavaModelException {
        return createOperation(OperationType.GETTER, reciever, property, Collections
                .<Expression> emptyList());
    }

    static MessageSend createGetterChain(Expression reciever, String propertyChain)
            throws JavaModelException {
        String[] tokens = propertyChain.split("\\.");
        while (tokens.length > 0) {
            reciever = createGetter(reciever, tokens[0]);
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        }
        return (MessageSend) reciever;
    }

    static MessageSend createSetterChain(Expression reciever, String propertyChain,
            List<Expression> arguments) throws JavaModelException {
        String[] tokens = propertyChain.split("\\.");
        if (tokens.length > 1) {
            // leave a last one for setter call
            reciever = createGetterChain(reciever, propertyChain.substring(0, propertyChain
                    .lastIndexOf('.')));
        }
        reciever = createOperation(OperationType.SETTER, reciever, tokens[tokens.length - 1],
                arguments);
        return (MessageSend) reciever;
    }

    static ConditionalExpression createNullSaveGetterChain(Expression reciever, String propertyChain)
            throws JavaModelException {
        return getNullChecks(createGetterChain(reciever, propertyChain), OperationType.GETTER);
    }

    static BinaryExpression createNullCheck(Expression expression) throws JavaModelException {
        return JAMP.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION, expression,
                getNullLiteral(), BinaryExpression.NOT_EQUAL);
    }

    static ConditionalExpression getNullChecks(MessageSend input, OperationType operationType) {
        return new ConditionalExpressionBuilder(input, operationType).getResult();
    }

    static MessageSend createUpdateCall(String propertyPath) throws JavaModelException {
        ThisReference reciever = JAMP.createThisReference();
        Expression[] arguments = new Expression[] {
                JAMP.createSingleNameReference(formatAsPropertyIdentifier(propertyPath)),
                JAMP.createSingleNameReference("oldValue"),
                JAMP.createSingleNameReference("newValue") };
        return JavaAstModelProducer.getInstance().createMessageSend("update", reciever,
                Arrays.asList(arguments));
    }

    static void addOldValueNullChecks(MessageSend updateCall, String propertyChain)
            throws JavaModelException {
        updateCall.arguments[1] = createNullSaveGetterChain(updateCall.arguments[1], propertyChain);
    }

    static void addNewValueNullChecks(MessageSend updateCall, String propertyChain)
            throws JavaModelException {
        updateCall.arguments[2] = createNullSaveGetterChain(updateCall.arguments[2], propertyChain);
    }

    static String formatAsPropertyIdentifier(String input) {
        StringBuilder result = new StringBuilder("PROPERTY_");
        StringTokenizer stringTokenizer = new StringTokenizer(input,
                StructuredPropertyPathEntry.SEPARATOR.toString());
        while (stringTokenizer.hasMoreTokens()) {
            result.append(stringTokenizer.nextToken().toUpperCase());
            if (stringTokenizer.hasMoreTokens()) {
                result.append("_");
            }
        }
        return result.toString();
    }
}
