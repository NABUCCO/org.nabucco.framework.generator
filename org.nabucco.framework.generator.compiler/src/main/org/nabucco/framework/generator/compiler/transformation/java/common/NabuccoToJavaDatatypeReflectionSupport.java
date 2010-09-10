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
package org.nabucco.framework.generator.compiler.transformation.java.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;

import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstType;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaDatatypeReflectionSupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaDatatypeReflectionSupport {

    private static final String GETTER_PREFIX = "get";

    private static final JavaAstMethodSignature GET_PROPERTIES = new JavaAstMethodSignature(
            "getProperties");

    private static final JavaAstMethodSignature GET_PROPERTY_NAMES = new JavaAstMethodSignature(
            "getPropertyNames");

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoToJavaDatatypeReflectionSupport() {
    }

    /**
     * Adjusts the getProperties method for datatype visitation.
     * 
     * @param fieldList
     *            list of all fields
     * @param type
     *            the type containing the validate method.
     * 
     * @throws JavaModelException
     */
    public static void adjustPropertiesMethods(List<String> fieldList, TypeDeclaration type)
            throws JavaModelException {

        adjustGetProperties(fieldList, type);
        adjustGetPropertyNames(fieldList, type);
    }

    /**
     * Adjust the getProperties() method.
     * 
     * @param fieldList
     *            list of field names
     * @param type
     *            the type to modify
     * 
     * @throws JavaModelException
     */
    private static void adjustGetProperties(List<String> fieldList, TypeDeclaration type)
            throws JavaModelException {

        JavaAstType javaType = JavaAstElementFactory.getInstance().getJavaAstType();
        AbstractMethodDeclaration getProperties = javaType.getMethod(type, GET_PROPERTIES);

        ReturnStatement returnStatement = (ReturnStatement) getProperties.statements[0];
        MessageSend mergeCall = (MessageSend) returnStatement.expression;
        ArrayInitializer initializer = ((ArrayAllocationExpression) mergeCall.arguments[1]).initializer;

        List<MessageSend> getterCalls = createGetterCalls(fieldList);
        initializer.expressions = getterCalls.toArray(new MessageSend[getterCalls.size()]);
    }

    /**
     * Create getter calls for all fields.
     * 
     * @param fieldList
     *            the list of field names.
     * 
     * @return the list of getters
     * 
     * @throws JavaModelException
     */
    private static List<MessageSend> createGetterCalls(List<String> fieldList)
            throws JavaModelException {
        List<MessageSend> methodCalls = new ArrayList<MessageSend>();
        for (String fieldName : fieldList) {
            methodCalls.add(createGetterCall(NabuccoTransformationUtility.firstToUpper(fieldName)));
        }
        return methodCalls;
    }

    /**
     * Creates a getter method call for the given name.
     * 
     * @param fieldName
     *            name of the field
     * 
     * @return the method call
     * 
     * @throws JavaModelException
     */
    private static MessageSend createGetterCall(String fieldName) throws JavaModelException {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        String methodName = GETTER_PREFIX + fieldName;
        ThisReference receiver = producer.createThisReference();

        return producer.createMessageSend(methodName, receiver, null);
    }

    /**
     * Adjust the getPropertyNames() method.
     * 
     * @param fieldList
     *            list of field names
     * @param type
     *            the type to modify
     * 
     * @throws JavaModelException
     */
    private static void adjustGetPropertyNames(List<String> fieldList, TypeDeclaration type)
            throws JavaModelException {

        JavaAstType javaType = JavaAstElementFactory.getInstance().getJavaAstType();
        AbstractMethodDeclaration getPropertyNames = javaType.getMethod(type, GET_PROPERTY_NAMES);

        ReturnStatement returnStatement = (ReturnStatement) getPropertyNames.statements[0];
        MessageSend mergeCall = (MessageSend) returnStatement.expression;
        ArrayInitializer initializer = ((ArrayAllocationExpression) mergeCall.arguments[1]).initializer;

        List<Literal> literals = createStringLiterals(fieldList);
        initializer.expressions = literals.toArray(new Literal[literals.size()]);
    }

    /**
     * Create string literals for all fields.
     * 
     * @param fieldList
     *            the list of fields
     * 
     * @return the list of string literals
     * 
     * @throws JavaModelException
     */
    private static List<Literal> createStringLiterals(List<String> fieldList)
            throws JavaModelException {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        List<Literal> literals = new ArrayList<Literal>();

        for (String fieldName : fieldList) {
            Literal literal = producer.createLiteral(fieldName, LiteralType.STRING_LITERAL);
            literals.add(literal);
        }
        return literals;
    }
}
