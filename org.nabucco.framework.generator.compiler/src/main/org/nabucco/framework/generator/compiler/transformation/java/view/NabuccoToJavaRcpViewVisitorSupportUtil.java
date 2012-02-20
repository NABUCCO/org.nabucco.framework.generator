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
package org.nabucco.framework.generator.compiler.transformation.java.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaRcpViewVisitorSupportUtil
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public final class NabuccoToJavaRcpViewVisitorSupportUtil implements ViewConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaRcpViewVisitorSupportUtil.class);

    private NabuccoToJavaRcpViewVisitorSupportUtil() {
    }

    public static final ImportReference getModelTypeImport(NabuccoToJavaVisitorContext visitorContext,
            SearchViewStatement searchViewStatement) throws JavaModelException {
        String pkg = visitorContext.getPackage().replace(UI, UI_RCP) + PKG_SEPARATOR + MODEL_PACKAGE;
        String name = searchViewStatement.nodeToken2.tokenImage.replace(NabuccoJavaTemplateConstants.VIEW,
                NabuccoJavaTemplateConstants.MODEL);
        return JavaAstModelProducer.getInstance().createImportReference(pkg + PKG_SEPARATOR + name);
    }

    /**
     * Swaps the field order so that the title field is defined after the id field since it depends
     * on it
     * 
     */
    public static void swapFieldOrder(TypeDeclaration type) {
        int idPos = -1;
        for (int i = 0; i < type.fields.length; i++) {
            if (Arrays.equals(type.fields[i].name, ID.toCharArray())) {
                idPos = i;
            }
        }
        if (idPos != 0) {
            FieldDeclaration currentFirst = type.fields[0];
            type.fields[0] = type.fields[idPos];
            type.fields[idPos] = currentFirst;
        }
    }

    public static List<JavaAstContainter<? extends ASTNode>> getUiCommonElements(TypeDeclaration uIType,
            TypeDeclaration type, AnnotationDeclaration annotations) throws JavaModelException {
        List<JavaAstContainter<? extends ASTNode>> result = new ArrayList<JavaAstContainter<? extends ASTNode>>();
        NabuccoAnnotation elementId = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations,
                NabuccoAnnotationType.CLIENT_ELEMENT_ID);

        // Add the static final id field if we find a Id client element
        // annotation (@id)
        if (elementId != null && elementId.getValue() != null && !elementId.getValue().isEmpty()) {

            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            // create static field id

            FieldDeclaration fieldID = javaFactory.getJavaAstType().getField(uIType, ID);
            fieldID.initialization = JavaAstModelProducer.getInstance().createLiteral(elementId.getValue(),
                    LiteralType.STRING_LITERAL);
            JavaAstContainter<FieldDeclaration> fieldContainer = new JavaAstContainter<FieldDeclaration>(fieldID,
                    JavaAstType.FIELD);
            result.add(fieldContainer);
            // select the method "getId()"
            JavaAstMethodSignature signature = new JavaAstMethodSignature(GET_ID);
            MethodDeclaration getIdMethod = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(uIType,
                    signature);
            JavaAstContainter<MethodDeclaration> methodContainer = new JavaAstContainter<MethodDeclaration>(
                    getIdMethod, JavaAstType.METHOD);

            // set the returnStatement
            ((QualifiedNameReference) ((ReturnStatement) getIdMethod.statements[0]).expression).tokens[0] = type.name;

            result.add(methodContainer);

        } else {
            logger.warning("@Id annotation is missing or could not be analized");
        }

        return result;
    }

    public static JavaAstContainter<FieldDeclaration> createStaticFinalField(String mappedFieldName)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String mappedField = PROPERTY + UNDERSCORE + mappedFieldName.toUpperCase().replace(PKG_SEPARATOR, UNDERSCORE);

        FieldDeclaration resultingField = JavaAstModelProducer.getInstance().createFieldDeclaration(mappedField);

        javaFactory.getJavaAstField().setFieldType(resultingField,
                JavaAstModelProducer.getInstance().createTypeReference(STRING, false));

        String[] mappedFieldInit = mappedFieldName.split(FIELD_SEPARATOR);

        // only if mapped field is a Basetype or Enumeration
        if (mappedFieldName.split(FIELD_SEPARATOR).length <= 2) {
            resultingField.initialization = JavaAstModelProducer.getInstance().createLiteral(
                    mappedFieldInit[0] + NabuccoTransformationUtility.firstToUpper(mappedFieldInit[1]),
                    LiteralType.STRING_LITERAL);
        } else {
            resultingField.initialization = JavaAstModelProducer.getInstance()
                    .createLiteral(
                            mappedFieldInit[0]
                                    + NabuccoTransformationUtility.firstToUpper(mappedFieldInit[1])
                                    + NabuccoTransformationUtility.firstToUpper(mappedFieldInit[2]),
                            LiteralType.STRING_LITERAL);
        }
        // make static final
        javaFactory.getJavaAstField().addModifier(resultingField,
                ClassFileConstants.AccStatic | ClassFileConstants.AccFinal);
        // remove private
        javaFactory.getJavaAstField().removeModifier(resultingField, ClassFileConstants.AccPrivate);
        // add public
        javaFactory.getJavaAstField().addModifier(resultingField, ClassFileConstants.AccPublic);

        JavaAstContainter<FieldDeclaration> result = new JavaAstContainter<FieldDeclaration>(resultingField,
                JavaAstType.FIELD);

        return result;
    }

    public static JavaAstContainter<FieldDeclaration> createConstant(String name, String value) {
        try {

            name = name.toUpperCase();
            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

            int modifier = ClassFileConstants.AccStatic | ClassFileConstants.AccFinal | ClassFileConstants.AccPublic;

            FieldDeclaration constant = producer.createFieldDeclaration(name, modifier);
            constant.type = producer.createTypeReference(STRING, false);

            constant.initialization = producer.createLiteral(value, LiteralType.STRING_LITERAL);

            return new JavaAstContainter<FieldDeclaration>(constant, JavaAstType.FIELD);
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Cannot create constant [" + name + "].", e);
        }
    }

}
