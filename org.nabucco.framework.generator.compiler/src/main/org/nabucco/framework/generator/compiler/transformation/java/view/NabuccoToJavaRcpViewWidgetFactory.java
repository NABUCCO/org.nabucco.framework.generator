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
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;

import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaRcpViewWidgetFactory
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewWidgetFactory implements ViewConstants {

    private static final JavaAstMethodSignature SIGNATURE_CREATE_PICKER = new JavaAstMethodSignature(
            CREATE_ELEMENT_PICKER, COMPOSITE, ELEMENT_PICKER_PARAMETER);

    /**
     * Creates a new InputField.
     * 
     * @param annotationDeclarationList
     *            annotaionDeclaration of the input field.
     * @param name
     *            name of the input field.
     * @param isLabeled
     *            if the input field is labeled or not.
     * @param template
     *            the java compilation unit.
     * @param typeRefMap
     *            map of all referenced fields.
     * @param view
     *            name of the view.
     * @return List of all elements that are needed for a input field.
     */
    public static List<JavaAstContainter<?>> createInputField(
            List<NabuccoAnnotation> annotationDeclarationList, String name, boolean isLabeled,
            JavaCompilationUnit template, Map<String, String> typeRefMap, String view) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        List<JavaAstContainter<?>> containerList = new ArrayList<JavaAstContainter<?>>();

        try {
            TypeDeclaration type = template
                    .getType(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            if (isLabeled) {
                containerList.addAll(createLabel(annotationDeclarationList, type, name));
            }

            NabuccoAnnotation fieldEditModeAnn = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotation(annotationDeclarationList,
                            NabuccoAnnotationType.FIELD_EDIT_MODE);

            String fieldEditMode = fieldEditModeAnn == null ? null : fieldEditModeAnn.getValue();

            boolean isReadOnly = false;

            if (!(fieldEditMode == null) && fieldEditMode.equals(READ_ONLY)) {
                isReadOnly = true;
            }

            if (isReadOnly) {
                // create method createInputFieldReadOnly()
                JavaAstMethodSignature signature = new JavaAstMethodSignature(
                        CREATE_INPUT_FIELD_READ_ONLY, COMPOSITE);
                MethodDeclaration createInputFieldMethod = (MethodDeclaration) javaFactory
                        .getJavaAstType().getMethod(type, signature);

                JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                        createInputFieldMethod, JavaAstType.METHOD);
                // rename method
                javaFactory.getJavaAstMethod().setMethodName(createInputFieldMethod,
                        CREATE_INPUT_FIELD + NabuccoTransformationUtility.firstToUpper(name));
                container.getImports().addAll(addImportsInputField(isReadOnly));
                containerList.add(container);
            } else {
                // create field MappedField
                NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance()
                        .mapToAnnotation(annotationDeclarationList,
                                NabuccoAnnotationType.MAPPED_FIELD);

                String searchViewModel = view + MODEL;

                String mappedField = mappedFieldAnn == null ? null : searchViewModel
                        + PKG_SEPARATOR
                        + PROPERTY
                        + UNDERSCORE
                        + mappedFieldAnn.getValue().toUpperCase()
                                .replace(PKG_SEPARATOR, UNDERSCORE);

                // create field OBSERVE_VALUE
                String observeValue = OBSERVE_VALUE + UNDERSCORE + name.toUpperCase();
                FieldDeclaration field = javaFactory.getJavaAstType().getField(type, OBSERVE_VALUE);
                javaFactory.getJavaAstField().setFieldName(field, observeValue);

                if (mappedField != null) {
                    String[] mappingInformation = mappedField.split(FIELD_SEPARATOR);
                    String modelTypeName = mappingInformation[0];
                    String modelTypeFieldConstant = mappingInformation[1];
                    field.initialization = JavaAstModelProducer.getInstance()
                            .createQualifiedNameReference(modelTypeName, modelTypeFieldConstant);
                }

                containerList
                        .add(new JavaAstContainter<FieldDeclaration>(field, JavaAstType.FIELD));

                // create method createInputField()
                JavaAstMethodSignature signature = new JavaAstMethodSignature(CREATE_INPUT_FIELD,
                        COMPOSITE);

                MethodDeclaration createInputFieldMethod = (MethodDeclaration) javaFactory
                        .getJavaAstType().getMethod(type, signature);
                // rename method
                javaFactory.getJavaAstMethod().setMethodName(createInputFieldMethod,
                        CREATE_INPUT_FIELD + NabuccoTransformationUtility.firstToUpper(name));

                JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                        createInputFieldMethod, JavaAstType.METHOD);
                // replace OBSERVE_VALUE by the right OBSERVE_VALUE
                ((SingleNameReference) ((MessageSend) ((LocalDeclaration) createInputFieldMethod.statements[3]).initialization).arguments[1]).token = observeValue
                        .toCharArray();

                container.getImports().addAll(addImportsInputField(isReadOnly));

                javaFactory.getJavaAstMethod().setMethodName(createInputFieldMethod,
                        CREATE_INPUT_FIELD + NabuccoTransformationUtility.firstToUpper(name));

                containerList.add(container);
            }

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST editview modification.", jme);
        }
        return containerList;
    }

    /**
     * Creates a new Picker.
     * 
     * @param annotations
     *            annotaionDeclaration of the picker.
     * @param name
     *            name of the picker.
     * @param isLabeled
     *            if the picker is labeled or not.
     * @param template
     *            the java compilation unit.
     * @param typeRefMap
     *            map of all referenced fields.
     * @param view
     *            name of the view.
     * @return List of all elements that are needed for a picker.
     */
    public static List<JavaAstContainter<?>> createPicker(List<NabuccoAnnotation> annotations,
            String name, boolean isLabeled, JavaCompilationUnit template,
            Map<String, String> typeRefMap, String view) {
        return createPicker(annotations, name, isLabeled, template, typeRefMap, view, false);
    }

    /**
     * Creates a new Picker.
     * 
     * @param annotations
     *            annotaionDeclaration of the picker.
     * @param name
     *            name of the picker.
     * @param isLabeled
     *            if the picker is labeled or not.
     * @param template
     *            the java compilation unit.
     * @param typeRefMap
     *            map of all referenced fields.
     * @param view
     *            name of the view.
     * @param isList
     *            <b>true</b> creates a list picker, <b>false</b> creates an element picker
     * 
     * @return List of all elements that are needed for a picker.
     */
    public static List<JavaAstContainter<?>> createPicker(List<NabuccoAnnotation> annotations,
            String name, boolean isLabeled, JavaCompilationUnit template,
            Map<String, String> typeRefMap, String view, boolean isList) {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        List<JavaAstContainter<?>> containerList = new ArrayList<JavaAstContainter<?>>();

        try {
            TypeDeclaration type = template
                    .getType(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            if (isLabeled) {
                containerList.addAll(createLabel(annotations, type, name));
            }

            // change value of all constants
            containerList.addAll(createStaticFieldsForPicker(annotations, name, type, typeRefMap,
                    view));

            // add method createElementPicker() to the container
            MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                    type, SIGNATURE_CREATE_PICKER);

            // set in createElementPicker() TITLE, MESSAGE,
            // SHELL_TITLE, MESSAGE_TABLE, MESSAGE_COMBO, PATH_LABEL
            LocalDeclaration picker = (LocalDeclaration) method.statements[0];
            AllocationExpression pickerAllocation = (AllocationExpression) picker.initialization;
            AllocationExpression labelAllocation = (AllocationExpression) pickerAllocation.arguments[4];

            String labelName = (TITLE + UNDERSCORE + name.toUpperCase());
            labelAllocation.arguments[0] = producer.createSingleNameReference(labelName);

            labelName = (MESSAGE_UPPERCASE + UNDERSCORE + name.toUpperCase());
            labelAllocation.arguments[1] = producer.createSingleNameReference(labelName);

            labelName = (SHELL_TITLE + UNDERSCORE + name.toUpperCase());
            labelAllocation.arguments[2] = producer.createSingleNameReference(labelName);

            labelName = (MESSAGE_TABLE + UNDERSCORE + name.toUpperCase());
            labelAllocation.arguments[3] = producer.createSingleNameReference(labelName);

            labelName = (MESSAGE_COMBO + UNDERSCORE + name.toUpperCase());
            labelAllocation.arguments[4] = producer.createSingleNameReference(labelName);

            labelName = (PATH_LABEL + UNDERSCORE + name.toUpperCase());
            labelAllocation.arguments[5] = producer.createSingleNameReference(labelName);

            // set in createElementPicker() the OBSERVE_VALUE
            String observeValue = OBSERVE_VALUE + UNDERSCORE + name.toUpperCase();
            Assignment modelElement = (Assignment) method.statements[5];
            MessageSend callObserveValue = (MessageSend) modelElement.expression;
            callObserveValue.arguments[1] = producer.createSingleNameReference(observeValue);

            // set in createElementPicker() the DatatypePickerHandler
            String handlerName = NabuccoTransformationUtility.firstToUpper(name) + HANDLER;
            MessageSend callAddElements = (MessageSend) method.statements[7];
            AllocationExpression handlerAllocation = (AllocationExpression) callAddElements.arguments[0];
            handlerAllocation.type = producer.createTypeReference(handlerName, false);

            JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                    method, JavaAstType.METHOD);

            if (isList) {
                javaFactory.getJavaAstMethod().setMethodName(method,
                        CREATE_LIST_PICKER + NabuccoTransformationUtility.firstToUpper(name));

                TypeReference listPickerType = producer.createTypeReference(LIST_PICKER_COMPOSITE,
                        false);

                picker.type = listPickerType;
                pickerAllocation.type = listPickerType;

            } else {
                javaFactory.getJavaAstMethod().setMethodName(method,
                        CREATE_ELEMENT_PICKER + NabuccoTransformationUtility.firstToUpper(name));
            }

            container.getImports().addAll(addImportsPicker(isList));

            containerList.add(container);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST editview modification.", jme);
        }
        return containerList;
    }

    /**
     * Creates a new ComboBox.
     * 
     * @param annotationDeclarationList
     *            annotaionDeclaration of the combo box.
     * @param name
     *            name of the combo box.
     * @param isLabeled
     *            if the combo box is labeled or not.
     * @param template
     *            the java compilation unit.
     * @param typeRefMap
     *            map of all referenced fields.
     * @param view
     *            name of the view.
     * @return List of all elements that are needed for a combo box.
     */
    public static List<JavaAstContainter<?>> createComboBox(
            List<NabuccoAnnotation> annotationDeclarationList, String name, boolean isLabeled,
            JavaCompilationUnit template, Map<String, String> typeRefMap, String view) {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        List<JavaAstContainter<?>> containerList = new ArrayList<JavaAstContainter<?>>();

        try {
            TypeDeclaration type = template
                    .getType(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            if (isLabeled) {
                containerList.addAll(createLabel(annotationDeclarationList, type, name));
            }

            // change value of all the constant OBSERVE_VALUE
            containerList.add(new JavaAstContainter<FieldDeclaration>(getObserveValueField(
                    annotationDeclarationList, name, type, typeRefMap, view), JavaAstType.FIELD));

            // add method createElementCombo() to the container
            JavaAstMethodSignature signature = new JavaAstMethodSignature(CREATE_ELEMENT_COMBO,
                    COMPOSITE, ELEMENT_PICKER_COMBO_PARAMETER);

            MethodDeclaration createElementPickerMethod = (MethodDeclaration) javaFactory
                    .getJavaAstType().getMethod(type, signature);
            JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                    createElementPickerMethod, JavaAstType.METHOD);

            container.getImports().addAll(addImportsCombo());

            javaFactory.getJavaAstMethod().setMethodName(createElementPickerMethod,
                    CREATE_ELEMENT_COMBO + NabuccoTransformationUtility.firstToUpper(name));

            // set in createElementCombo() the OBSERVE_VALUE
            String observeValue = OBSERVE_VALUE + UNDERSCORE + name.toUpperCase();
            ((SingleNameReference) ((MessageSend) ((Assignment) createElementPickerMethod.statements[5]).expression).arguments[1]).token = observeValue
                    .toCharArray();

            // set in createElementPicker() the DatatypePickerHandler
            String modelType = view
                    + NabuccoTransformationUtility.firstToUpper(name) + COMBO_BOX_HANDLER;
            ((SingleTypeReference) ((AllocationExpression) ((MessageSend) createElementPickerMethod.statements[7]).arguments[0]).type).token = modelType
                    .toCharArray();

            containerList.add(container);
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST editview modification.", jme);
        }
        return containerList;
    }

    /**
     * Creates a new Column.
     * 
     * @param annotationDeclarationList
     *            annotaionDeclaration of the column.
     * @param name
     *            name of the column.
     * @param isLabeled
     *            if the column is labeled or not.
     * @param template
     *            the java compilation unit.
     * @return List of all elements that are needed for a column.
     */
    public static List<JavaAstContainter<?>> createColumn(
            List<NabuccoAnnotation> annotationDeclarationList, String name, boolean isLabeled,
            JavaCompilationUnit template) {
        List<JavaAstContainter<?>> containerList = new ArrayList<JavaAstContainter<?>>();
        return containerList;
    }

    /**
     * Creates a new Label.
     * 
     * @param annotations
     *            annotaionDeclaration of the element that is labeled.
     * @param type
     *            the type declaration of the element that is labeled.
     * @param name
     *            name of the element that is labeled.
     * @return List of all elements that are needed for a label.
     */
    public static List<JavaAstContainter<?>> createLabel(List<NabuccoAnnotation> annotations,
            TypeDeclaration type, String name) {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        List<JavaAstContainter<?>> containerList = new ArrayList<JavaAstContainter<?>>();

        // create constant LABEL
        String labelName = LABEL + UNDERSCORE + name.toUpperCase();

        try {

            FieldDeclaration field = javaFactory.getJavaAstType().getField(type, LABEL);
            javaFactory.getJavaAstField().setFieldName(field, labelName);

            NabuccoAnnotation labelId = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                    annotations, NabuccoAnnotationType.FIELD_LABEL_ID);

            if (labelId != null) {
                String value = labelId.getValue();
                if (value != null) {
                    field.initialization = producer
                            .createLiteral(value, LiteralType.STRING_LITERAL);
                }
            }

            containerList.add(new JavaAstContainter<FieldDeclaration>(field, JavaAstType.FIELD));

            // create method createLabel()
            JavaAstMethodSignature signature = new JavaAstMethodSignature(CREATE_LABEL, COMPOSITE);

            MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                    type, signature);

            ReturnStatement returnStatement = (ReturnStatement) method.statements[0];
            MessageSend createLabelCall = (MessageSend) returnStatement.expression;

            createLabelCall.arguments[1] = producer.createSingleNameReference(labelName);

            javaFactory.getJavaAstMethod().setMethodName(method,
                    CREATE_LABEL + NabuccoTransformationUtility.firstToUpper(name));

            JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                    method, JavaAstType.METHOD);

            container.getImports().addAll(addImportsLabel());

            containerList.add(container);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST editview modification.", jme);
        }
        return containerList;
    }

    /**
     * Creates all static fields that are needed for a picker.
     * 
     * @param annotations
     *            annotaionDeclaration of the picker.
     * @param name
     *            name of the picker.
     * @param type
     *            the type declaration.
     * @param typeRefMap
     *            map of all referenced fields.
     * @param view
     *            name of the view.
     * 
     * @return List of all static fields that are needed for a picker.
     * 
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private static List<JavaAstContainter<FieldDeclaration>> createStaticFieldsForPicker(
            List<NabuccoAnnotation> annotations, String name, TypeDeclaration type,
            Map<String, String> typeRefMap, String view) throws JavaModelException {

        List<JavaAstContainter<FieldDeclaration>> containerList = new ArrayList<JavaAstContainter<FieldDeclaration>>();

        // TITLE
        String value = NEW + TITLE;
        String fieldName = TITLE + UNDERSCORE + name.toUpperCase();
        containerList.add(new JavaAstContainter<FieldDeclaration>(getStaticField(type, TITLE,
                fieldName, value), JavaAstType.FIELD));

        // MESSAGE
        value = NEW + MESSAGE_UPPERCASE;
        fieldName = MESSAGE_UPPERCASE + UNDERSCORE + name.toUpperCase();
        containerList.add(new JavaAstContainter<FieldDeclaration>(getStaticField(type,
                MESSAGE_UPPERCASE, fieldName, value), JavaAstType.FIELD));

        // SHELL_TITLE
        value = NEW + SHELL_TITLE;
        fieldName = SHELL_TITLE + UNDERSCORE + name.toUpperCase();
        containerList.add(new JavaAstContainter<FieldDeclaration>(getStaticField(type, SHELL_TITLE,
                fieldName, value), JavaAstType.FIELD));

        // MESSAGE_TABLE
        value = NEW + MESSAGE_TABLE;
        fieldName = MESSAGE_TABLE + UNDERSCORE + name.toUpperCase();
        containerList.add(new JavaAstContainter<FieldDeclaration>(getStaticField(type,
                MESSAGE_TABLE, fieldName, value), JavaAstType.FIELD));

        // MESSAGE_COMBO
        value = NEW + MESSAGE_COMBO;
        fieldName = MESSAGE_COMBO + UNDERSCORE + name.toUpperCase();
        containerList.add(new JavaAstContainter<FieldDeclaration>(getStaticField(type,
                MESSAGE_COMBO, fieldName, value), JavaAstType.FIELD));

        // PATH_LABEL
        value = NEW + PATH_LABEL;
        fieldName = PATH_LABEL + UNDERSCORE + name.toUpperCase();
        containerList.add(new JavaAstContainter<FieldDeclaration>(getStaticField(type, PATH_LABEL,
                fieldName, value), JavaAstType.FIELD));

        containerList.add(new JavaAstContainter<FieldDeclaration>(getObserveValueField(annotations,
                name, type, typeRefMap, view), JavaAstType.FIELD));

        return containerList;
    }

    /**
     * Returns the modified observe value field.
     * 
     * @param annotationDeclarationList
     *            annotaionDeclaration of element.
     * @param name
     *            name of the element.
     * @param type
     *            the type declaration.
     * @param typeRefMap
     *            map of all referenced fields.
     * @param view
     *            name of the view.
     * 
     * @return List of all static fields that are needed for a picker.
     * 
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private static FieldDeclaration getObserveValueField(
            List<NabuccoAnnotation> annotationDeclarationList, String name, TypeDeclaration type,
            Map<String, String> typeRefMap, String view) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // OBSERVE_VALUE
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                annotationDeclarationList, NabuccoAnnotationType.MAPPED_FIELD);

        String model = view + NabuccoJavaTemplateConstants.MODEL;

        if (mappedFieldAnn != null) {
            if (mappedFieldAnn.getValue() == null) {
                throw new NabuccoVisitorException("Value of mapped field is null.");
            }
        }
        String mappedField = mappedFieldAnn == null ? null : model
                + PKG_SEPARATOR + PROPERTY + UNDERSCORE
                + mappedFieldAnn.getValue().toUpperCase().replace(PKG_SEPARATOR, UNDERSCORE);

        String observeValue = OBSERVE_VALUE + UNDERSCORE + name.toUpperCase();
        FieldDeclaration field = javaFactory.getJavaAstType().getField(type, OBSERVE_VALUE);
        javaFactory.getJavaAstField().setFieldName(field, observeValue);

        if (mappedField != null) {
            String[] mappingInformation = mappedField.split(FIELD_SEPARATOR);
            String modelTypeName = mappingInformation[0];
            String modelTypeFieldConstant = mappingInformation[1];
            field.initialization = JavaAstModelProducer.getInstance().createQualifiedNameReference(
                    modelTypeName, modelTypeFieldConstant);
        }

        return field;
    }

    /**
     * Returns the modified static field.
     * 
     * @param type
     *            type of the static field.
     * @param oldFieldName
     *            name of the field in the template.
     * @param newFieldName
     *            name for this field.
     * @param value
     *            field is initialized with this value.
     * 
     * @return the static field.
     * 
     * @throws JavaModelException
     *             if the modification failed.
     */
    private static FieldDeclaration getStaticField(TypeDeclaration type, String oldFieldName,
            String newFieldName, String value) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration field = javaFactory.getJavaAstType().getField(type, oldFieldName);
        field.initialization = JavaAstModelProducer.getInstance().createLiteral(value,
                LiteralType.STRING_LITERAL);
        javaFactory.getJavaAstField().setFieldName(field, newFieldName);
        return field;
    }

    /**
     * Returns all needed imports for a label.
     * 
     * @return the list of strings with the needed imports for a label.
     */
    private static List<String> addImportsLabel() {
        return Arrays.asList("org.eclipse.swt.widgets.Label");
    }

    /**
     * Returns all needed imports for an input field.
     * 
     * @param isReadOnly
     *            boolean value if the field is read only or not.
     * 
     * @return the list of strings with the needed imports for an input field.
     */
    private static List<String> addImportsInputField(boolean isReadOnly) {
        List<String> importList = new ArrayList<String>();
        importList.add("org.eclipse.swt.widgets.Text");
        if (!isReadOnly) {
            importList.add("org.eclipse.swt.SWT");
            importList.add("org.eclipse.core.databinding.DataBindingContext");
            importList.add("org.eclipse.core.databinding.beans.BeansObservables");
            importList.add("org.eclipse.core.databinding.observable.value.IObservableValue");
            importList.add("org.eclipse.jface.databinding.swt.SWTObservables");
        }
        return importList;
    }

    /**
     * Returns all needed imports for a picker.
     * 
     * @param isList
     *            whether it is a list or an element picker
     * 
     * @return the list of strings with the needed imports for a picker.
     */
    private static List<String> addImportsPicker(boolean isList) {
        List<String> importList = new ArrayList<String>();
        importList.add("org.eclipse.core.databinding.DataBindingContext");
        importList.add("org.eclipse.core.databinding.beans.BeansObservables");
        importList.add("org.eclipse.core.databinding.observable.value.IObservableValue");
        importList.add("org.eclipse.jface.databinding.swt.SWTObservables");
        importList.add("org.eclipse.swt.SWT");

        if (isList) {
            importList.add("org.nabucco.framework.plugin.base.component.picker.dialog.ListPickerComposite");
        } else {
            importList.add("org.nabucco.framework.plugin.base.component.picker.dialog.ElementPickerComposite");
        }

        importList.add("org.nabucco.framework.plugin.base.component.picker.dialog.ElementPickerParameter");
        importList.add("org.nabucco.framework.plugin.base.component.picker.dialog.LabelForDialog");
        return importList;
    }

    /**
     * Returns all needed imports for a combo box.
     * 
     * @return the list of strings with the needed imports for a combo box.
     */
    private static List<String> addImportsCombo() {
        List<String> importList = new ArrayList<String>();
        importList.add("org.eclipse.core.databinding.DataBindingContext");
        importList.add("org.eclipse.core.databinding.beans.BeansObservables");
        importList.add("org.eclipse.core.databinding.observable.value.IObservableValue");
        importList.add("org.eclipse.jface.databinding.swt.SWTObservables");
        importList.add("org.eclipse.swt.SWT");
        importList.add("org.nabucco.framework.plugin.base.component.picker.combo.ElementPickerCombo");
        importList
                .add("org.nabucco.framework.plugin.base.component.picker.combo.ElementPickerComboParameter");
        return importList;
    }

}