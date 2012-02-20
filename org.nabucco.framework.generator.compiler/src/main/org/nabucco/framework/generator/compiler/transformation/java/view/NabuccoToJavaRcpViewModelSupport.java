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
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewModelSupport
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewModelSupport implements ViewConstants, CollectionConstants {

    /** Import String for the Activator. */
    private static final String ACTIVATOR_IMPORT = "org.nabucco.framework.plugin.base.Activator";

    /** Signature for getField() */
    private static final JavaAstMethodSignature SIGNATURE_GET_FIELD = new JavaAstMethodSignature(GET_FIELD);

    /** Signature for getFieldDatatype() */
    private static final JavaAstMethodSignature SIGNATURE_GET_FIELD_DATATYPE = new JavaAstMethodSignature(
            GET_FIELD_DATATYPE);

    /** Signature for getFieldCombo() */
    private static final JavaAstMethodSignature SIGNATURE_GET_FIELD_COMBO = new JavaAstMethodSignature(GET_FIELD_COMBO);

    /** Signature for setField(String str) */
    private static final JavaAstMethodSignature SIGNATURE_SET_FIELD = new JavaAstMethodSignature(SET_FIELD,
            new String[] { STRING });

    /** Signature for setFieldDatatype(String str) */
    private static final JavaAstMethodSignature SIGNATURE_SET_FIELD_DATATYPE = new JavaAstMethodSignature(
            SET_FIELD_DATATYPE, new String[] { STRING });

    /** Signature for setFieldCombo(String str) */
    private static final JavaAstMethodSignature SIGNATURE_SET_FIELD_COMBO = new JavaAstMethodSignature(SET_FIELD_COMBO,
            new String[] { STRING });

    private static final JavaAstMethodSignature SIGNATURE_SET_MODEL_LIST = new JavaAstMethodSignature(SET_DATATYPE_SET,
            "Set");

    /**
     * Creates a new model element for a input field.
     * 
     * @param annotationList
     *            the annotationDeclaration of the element.
     * @param unit
     *            the JavaCompilationUnit.
     * @param util
     *            the NabuccoToJavaRcpViewVisitorSupport.
     * 
     * @return the list of elements that needed to create a model element.
     */
    public static List<JavaAstContainter<?>> createModelElement(List<NabuccoAnnotation> annotationList,
            JavaCompilationUnit unit, NabuccoToJavaRcpViewVisitorSupport util,
            NabuccoToJavaVisitorContext visitorContext) {

        List<JavaAstContainter<?>> containerList = new ArrayList<JavaAstContainter<?>>();

        try {
            NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotationList,
                    NabuccoAnnotationType.MAPPED_FIELD);

            String currentMappedField = mappedFieldAnn == null ? null : mappedFieldAnn.getValue();

            // get the method template
            TypeDeclaration methodType = unit.getType(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            // TODO: Replace 2 by N

            // only if mapped field is a Basetype or Enumeration
            if (currentMappedField.split(FIELD_SEPARATOR).length <= 2) {

                MethodDeclaration getter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(methodType,
                        SIGNATURE_GET_FIELD);
                MethodDeclaration setter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(methodType,
                        SIGNATURE_SET_FIELD);

                util.changeGetter(getter, currentMappedField);
                util.changeSetter(setter, currentMappedField);

                containerList.add(new JavaAstContainter<ASTNode>(setter, JavaAstType.METHOD));
                containerList.add(new JavaAstContainter<ASTNode>(getter, JavaAstType.METHOD));

                containerList.add(NabuccoToJavaRcpViewVisitorSupportUtil.createStaticFinalField(currentMappedField));
            } else {

                // get the templated methods
                MethodDeclaration getter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(methodType,
                        SIGNATURE_GET_FIELD_DATATYPE);

                MethodDeclaration setter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(methodType,
                        SIGNATURE_SET_FIELD_DATATYPE);

                util.changeGetterDatatype(getter, currentMappedField);
                util.changeSetterDatatype(setter, currentMappedField);

                String localfield = currentMappedField.split(FIELD_SEPARATOR)[0];
                String fieldname = currentMappedField.split(FIELD_SEPARATOR)[1];
                JavaAstContainter<TypeReference> javacontainer = util.getFieldNameToTypeReference().get(DATATYPE)
                        .get(fieldname);
                TypeReference ref = javacontainer.getAstNode();
                String fieldType = ref.toString();
                String property = currentMappedField.split(FIELD_SEPARATOR)[2];
                util.createSetterForFieldDatatype(visitorContext, localfield, fieldname, fieldType, property);

                JavaAstContainter<ASTNode> container = new JavaAstContainter<ASTNode>(getter, JavaAstType.METHOD);

                Set<String> imports = javacontainer.getImports();
                // import of the fieldType
                for (String importString : imports) {
                    container.getImports().add(importString);
                }

                containerList.add(new JavaAstContainter<ASTNode>(setter, JavaAstType.METHOD));
                containerList.add(container);

                containerList.add(NabuccoToJavaRcpViewVisitorSupportUtil.createStaticFinalField(currentMappedField));
            }

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error creating model element.", me);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error creating model element.", te);
        }
        return containerList;
    }

    /**
     * Creates a new model element for a combo box.
     * 
     * @param annotationList
     *            the annotationDeclaration of the element.
     * @param unit
     *            the JavaCompilationUnit.
     * @param util
     *            the NabuccoToJavaRcpViewVisitorSupport.
     * 
     * @return the list of elements that needed to create a model element.
     */
    public static List<JavaAstContainter<?>> createModelElementComboBox(List<NabuccoAnnotation> annotationList,
            JavaCompilationUnit unit, NabuccoToJavaRcpViewVisitorSupport util) {

        List<JavaAstContainter<?>> containerList = new ArrayList<JavaAstContainter<?>>();

        try {
            NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotationList,
                    NabuccoAnnotationType.MAPPED_FIELD);

            String currentMappedField = mappedFieldAnn == null ? null : mappedFieldAnn.getValue();

            // get the method template
            TypeDeclaration methodType = unit.getType(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            // get the templated methods getFieldCombo and setFieldCombo
            MethodDeclaration getter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(methodType,
                    SIGNATURE_GET_FIELD_COMBO);
            MethodDeclaration setter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(methodType,
                    SIGNATURE_SET_FIELD_COMBO);

            util.changeGetterCombo(getter, currentMappedField);
            util.changeSetterCombo(setter, currentMappedField);

            JavaAstContainter<ASTNode> containerGetter = new JavaAstContainter<ASTNode>(getter, JavaAstType.METHOD);
            containerGetter.getImports().add(ACTIVATOR_IMPORT);
            containerList.add(containerGetter);

            JavaAstContainter<ASTNode> containerSetter = new JavaAstContainter<ASTNode>(setter, JavaAstType.METHOD);
            // add the enumtype as import
            String[] fieldAccess = currentMappedField.split(FIELD_SEPARATOR);
            containerSetter.getImports().addAll(
                    util.getFieldNameToFieldTypeProperties().get(fieldAccess[0]).get("Enumeration").get(fieldAccess[1])
                            .getImports());

            containerList.add(containerSetter);

            containerList.add(NabuccoToJavaRcpViewVisitorSupportUtil.createStaticFinalField(currentMappedField));

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error creating combo box element.", me);
        }

        return containerList;
    }

    public static List<JavaAstContainter<?>> createModelElementListPicker(List<NabuccoAnnotation> annotationList,
            JavaCompilationUnit unit, NabuccoToJavaRcpViewVisitorSupport util) {

        List<JavaAstContainter<?>> containerList = new ArrayList<JavaAstContainter<?>>();
        try {
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotationList,
                    NabuccoAnnotationType.MAPPED_FIELD);

            String mappedField = mappedFieldAnn == null ? null : mappedFieldAnn.getValue();

            TypeDeclaration javaType = unit.getType(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);

            MethodDeclaration setter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(javaType,
                    SIGNATURE_SET_MODEL_LIST);

            util.changeSetterSet(setter, mappedField);

            // Container

            JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(setter,
                    JavaAstType.METHOD);

            // Imports

            container.getImports().add(IMPORT_SET);
            container.getImports().add(IMPORT_ITERATOR);
            container.getImports().add(IMPORT_DEFAULT_SET);

            // Delegate Field

            String[] mappedFieldInit = mappedField.split(FIELD_SEPARATOR);

            StringBuilder fieldName = new StringBuilder();

            for (int i = 0; i < mappedFieldInit.length; i++) {
                String name;
                if (i == 0) {
                    name = mappedFieldInit[i];
                } else {
                    name = NabuccoTransformationUtility.firstToUpper(mappedFieldInit[i]);
                }
                fieldName.append(name);
            }

            JavaAstContainter<FieldDeclaration> delegateField = JavaAstSupport.createField(STRING,
                    fieldName.toString(), NabuccoModifierType.PRIVATE);

            JavaAstContainter<MethodDeclaration> delegateGetter = JavaAstSupport.createGetter(delegateField
                    .getAstNode());

            StringBuilder constantName = new StringBuilder();
            constantName.append(PROPERTY);
            constantName.append(UNDERSCORE);
            constantName.append(mappedField.toUpperCase().replace(PKG_SEPARATOR, UNDERSCORE));

            JavaAstContainter<MethodDeclaration> delegateSetter = util.createSetterForModelSet(
                    delegateField.getAstNode(), constantName.toString());

            containerList.add(delegateField);
            containerList.add(delegateGetter);
            containerList.add(delegateSetter);

            // Constant

            containerList.add(NabuccoToJavaRcpViewVisitorSupportUtil.createStaticFinalField(mappedField));

            containerList.add(container);

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error creating combo box element.", me);
        }

        return containerList;
    }

}
