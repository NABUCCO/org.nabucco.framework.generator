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

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * ConditionalExpressionBuilder
 * 
 * @author Silas Schwarz PRODYNA AG
 */
class ConditionalExpressionBuilder extends ASTVisitor {

    private static final JavaAstModelProducer JAMP = JavaAstModelProducer.getInstance();

    static Literal NULL_LITERAL;

    static Literal EMPTY_STRING_LITERAL;

    private ConditionalExpression result = null;

    private OperationType operationType;

    public ConditionalExpressionBuilder(MessageSend messageSend, OperationType operationType) {
        init(operationType);
        messageSend.traverse(this, (BlockScope) null);
    }

    /**
     * @return Returns the result.
     */
    public ConditionalExpression getResult() {
        return result;
    }

    /**
     * @return Returns the operationType.
     */
    private OperationType getOperationType() {
        return operationType;
    }

    /**
     * @param operationType
     * 
     */
    private void init(OperationType operationType) {
        try {
            NULL_LITERAL = CommonOperationSupport.getNullLiteral();
            EMPTY_STRING_LITERAL = CommonOperationSupport.getEmptyStringLiteral();
            this.operationType = operationType;
        } catch (JavaModelException e) {
            throw new IllegalStateException("unable to create model elements");
        }
    }

    @Override
    public boolean visit(MessageSend messageSend, BlockScope scope) {
        switch (this.getOperationType()) {
        case GETTER: {
            this.getterMode(messageSend);
            break;
        }
        case SETTER: {
            this.setterMode(messageSend);
            break;
        }

        default:
            break;
        }

        return super.visit(messageSend, scope);
    }

    public void getterMode(MessageSend messageSend) {
        if (result == null) {
            BinaryExpression createBinaryExpression = JAMP.createBinaryExpression(
                    BinaryExpressionType.EQUAL_EXPRESSION, messageSend, NULL_LITERAL,
                    EqualExpression.NOT_EQUAL);
            result = JAMP.createConditionalExpression(createBinaryExpression, messageSend,
                    EMPTY_STRING_LITERAL);
        } else {
            BinaryExpression createBinaryExpression = JAMP.createBinaryExpression(
                    BinaryExpressionType.EQUAL_EXPRESSION, messageSend, NULL_LITERAL,
                    EqualExpression.NOT_EQUAL);
            result = JAMP.createConditionalExpression(createBinaryExpression, result,
                    EMPTY_STRING_LITERAL);

        }
    }

    public void setterMode(MessageSend messageSend) {
        if (result == null) {
            BinaryExpression createBinaryExpression = JAMP.createBinaryExpression(
                    BinaryExpressionType.EQUAL_EXPRESSION, messageSend, NULL_LITERAL,
                    EqualExpression.NOT_EQUAL);
            result = JAMP.createConditionalExpression(createBinaryExpression, messageSend,
                    EMPTY_STRING_LITERAL);
        } else {
            BinaryExpression createBinaryExpression = JAMP.createBinaryExpression(
                    BinaryExpressionType.EQUAL_EXPRESSION, messageSend, NULL_LITERAL,
                    EqualExpression.NOT_EQUAL);
            result = JAMP.createConditionalExpression(createBinaryExpression, result,
                    EMPTY_STRING_LITERAL);

        }

    }
}
