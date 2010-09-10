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
package org.nabucco.framework.generator.compiler.transformation.java.view.common.combobox;

import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledComboBoxDeclaration;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewComboBoxHandlerVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
class NabuccoToJavaRcpViewComboBoxHandlerVisitor extends NabuccoToJavaVisitorSupport implements
        ViewConstants {

    /**
     * The method signature for "changeWidgetDefaultSelected".
     */
    private static final JavaAstMethodSignature CHANGE_WIDGET_DEFAULT_SELECTED = new JavaAstMethodSignature(
            WIDGET_DEFAULT_SELECTED, SELECTION_EVENT);

    /**
     * The method signature for "changeWidgetSelected".
     */
    private static final JavaAstMethodSignature CHANGE_WIDGET_SELECTED = new JavaAstMethodSignature(
            WIDGET_SELECTED, SELECTION_EVENT);

    /**
     * the name of the view.
     */
    private String viewName;

    /**
     * the type of the model.
     */
    private String modelType;

    /**
     * the annotation declaration of the view.
     */
    private AnnotationDeclaration viewAnnotationDeclaration;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewComboBoxHandlerVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     * @param viewName
     *            the name of the view from which the constructor is called.
     * @param annotationDeclaration
     *            the name of the view from which the constructor is called.
     */
    public NabuccoToJavaRcpViewComboBoxHandlerVisitor(NabuccoToJavaVisitorContext visitorContext,
            String viewName, AnnotationDeclaration annotationDeclaration) {
        super(visitorContext);
        this.viewName = viewName;
        this.viewAnnotationDeclaration = annotationDeclaration;
        this.modelType = viewName.replace(VIEW, VIEW + MODEL);
    }

    @Override
    public void visit(LabeledComboBoxDeclaration labeledComboBox, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotationDeclaration = labeledComboBox.annotationDeclaration;
        String comboBoxName = labeledComboBox.nodeToken2.tokenImage;

        this.createComboBoxHandler(target, annotationDeclaration, comboBoxName);
    }

    @Override
    public void visit(ComboBoxDeclaration comboBox, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotationDeclaration = comboBox.annotationDeclaration;
        String comboBoxName = comboBox.nodeToken2.tokenImage;

        this.createComboBoxHandler(target, annotationDeclaration, comboBoxName);
    }

    /**
     * Creates a handler for a combo box.
     * 
     * @param target
     *            the target mda model.
     * @param annotationDeclaration
     *            the annotation declaration of the combo box.
     * @param comboBoxName
     *            the name of the combo box.
     */
    private void createComboBoxHandler(MdaModel<JavaModel> target,
            AnnotationDeclaration annotationDeclaration, String comboBoxName) {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = this.viewName
                + NabuccoTransformationUtility.firstToUpper(comboBoxName) + COMBO_BOX_HANDLER;
        String rootPath = super.getVisitorContext().getPackage().replace(UI, UI_RCP);

        String pkg = rootPath + PKG_SEPARATOR + VIEW_PACKAGE;
        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {

            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_COMBO_BOX_HANDLER_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.COMMON_VIEW_COMBO_BOX_HANDLER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // change type of the attribute model
            this.changeModelFieldType(type);

            // change param of the constructor
            this.changeConstructor(type, name);

            // change model in WidgetSelected-Methods
            String methodName = this.createSetterMethodName(annotationDeclaration);
            this.changeMethod(methodName, type, CHANGE_WIDGET_SELECTED);
            this.changeMethod(methodName, type, CHANGE_WIDGET_DEFAULT_SELECTED);

            // add import of the model
            ImportReference modelImport = createModelImport(rootPath);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), modelImport);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(annotationDeclaration, type);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(viewAnnotationDeclaration, type);

            // JavaDocAnnotations
            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(),
                    getVisitorContext().getImportList());

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException(
                    "Error during Java AST combo box handler modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template combo box handler processing.", te);
        }
    }

    /**
     * Changes the method name from "setPropertie" in the given method.
     * 
     * @param methodName
     *            the new name of the method.
     * @param type
     *            the type declaration.
     * @param signature
     *            the signture of the method to be changed.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeMethod(String methodName, TypeDeclaration type,
            JavaAstMethodSignature signature) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                signature);

        IfStatement ifStatement = (IfStatement) method.statements[0];
        Block thenStatement = (Block) ifStatement.thenStatement;
        ((MessageSend) thenStatement.statements[1]).selector = methodName.toCharArray();
    }

    /**
     * Returns the name for the setterMethod.
     * 
     * @param annotationDeclaration
     *            the annotation declaration of the combo box.
     * @return the name for the setterMethod
     */
    private String createSetterMethodName(AnnotationDeclaration annotationDeclaration) {

        NabuccoAnnotation annotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);

        String mappedField = annotation == null ? null : annotation.getValue();
        String[] accessPath = mappedField.split(FIELD_SEPARATOR);
        String localField = accessPath[0];
        String referencedFieldTypeKey = accessPath[1];

        String setProperty = PREFIX_SETTER
                + NabuccoTransformationUtility.firstToUpper(localField)
                + NabuccoTransformationUtility.firstToUpper(referencedFieldTypeKey);

        return setProperty;
    }

    /**
     * Changes the model type in the arguments of the constructor.
     * 
     * @param type
     *            the type declaration.
     * @param name
     *            the name of the constructor.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeConstructor(TypeDeclaration type, String name) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstMethodSignature signature = new JavaAstMethodSignature(name, EDIT_VIEW_MODEL);
        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type,
                signature);
        ((SingleTypeReference) (constructor.arguments[0]).type).token = modelType.toCharArray();
    }

    /**
     * Creates the import of the model.
     * 
     * @param rootPath
     *            the root path of the view.
     * @return the import reference of the model.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private ImportReference createModelImport(String rootPath) throws JavaModelException {
        String modelPath = rootPath + PKG_SEPARATOR + MODEL_PACKAGE;
        ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                modelPath + PKG_SEPARATOR + modelType);
        return importReference;
    }

    /**
     * Changes the model type of the model field.
     * 
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeModelFieldType(TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration modelField = javaFactory.getJavaAstType().getField(type, MODEL_FIELD);
        modelField.type = JavaAstModelProducer.getInstance().createTypeReference(modelType, false);
        javaFactory.getJavaAstField().setFieldType(modelField, modelField.type);
    }
}
