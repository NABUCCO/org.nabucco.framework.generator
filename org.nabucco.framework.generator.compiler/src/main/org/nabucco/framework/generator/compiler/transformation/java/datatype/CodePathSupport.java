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
package org.nabucco.framework.generator.compiler.transformation.java.datatype;

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * CodePathSupport
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class CodePathSupport implements ServerConstants {

    private static final String IMPORT_CODEPATH = "org.nabucco.framework.base.facade.datatype.code.CodePath";

    static final String IMPORT_CODE = "org.nabucco.framework.base.facade.datatype.code.Code";

    /**
     * Create for the constant name for the given field name.
     * 
     * @param fieldName
     *            the field name to create the code path constant for
     * 
     * @return the code path constant
     */
    public static String createCodePathConstant(String fieldName) {
        return (fieldName + CONSTANT_SEPARATOR + CODEPATH).toUpperCase();
    }

    /**
     * Create for the getter name for the given field name.
     * 
     * @param fieldName
     *            the field name to create the code path getter for
     * 
     * @return the code path constant
     */
    public static String createCodePathGetter(String name) {
        return PREFIX_GETTER + NabuccoTransformationUtility.firstToUpper(name) + CODEPATH;
    }

    /**
     * Creates and appends a getter method and a static field for CodePath field to a given
     * {@link NabuccoToJavaVisitorContext}.
     * 
     * @param path
     *            the code path to use
     * @param name
     *            the name of the field containing the redefinition.
     * @param context
     *            the context to append the field and method to.
     */
    public static void createCodePath(String name, String path, NabuccoToJavaVisitorContext context) {

        String fieldName = createCodePathConstant(name);
        String getterName = createCodePathGetter(name);

        JavaAstContainter<FieldDeclaration> fieldContainer = JavaAstSupport.createField(STRING, fieldName,
                NabuccoModifierType.PROTECTED);

        FieldDeclaration field = fieldContainer.getAstNode();
        field.modifiers |= ClassFileConstants.AccFinal;
        field.modifiers |= ClassFileConstants.AccStatic;

        try {
            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
            Literal string = producer.createLiteral(path, LiteralType.STRING_LITERAL);

            field.initialization = string;

            TypeReference type = producer.createTypeReference(CODEPATH, false);
            SingleNameReference reference = producer.createSingleNameReference(fieldName);

            Expression constructor = producer.createAllocationExpression(type, Arrays.asList(reference));

            JavaAstContainter<MethodDeclaration> getterContainer = JavaAstSupport.createGetter(field);
            MethodDeclaration getter = getterContainer.getAstNode();
            getter.modifiers |= ClassFileConstants.AccStatic;

            JavaAstElementFactory.getInstance().getJavaAstMethod().setReturnType(getter, type);
            JavaAstElementFactory.getInstance().getJavaAstMethod().setMethodName(getter, getterName);

            ReturnStatement returnStatement = (ReturnStatement) getter.statements[0];
            returnStatement.expression = constructor;

            fieldContainer.getImports().add(IMPORT_CODEPATH);
            context.getContainerList().add(fieldContainer);
            context.getContainerList().add(getterContainer);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating CodePath for datatype " + name + ".", jme);
        }
    }

}
