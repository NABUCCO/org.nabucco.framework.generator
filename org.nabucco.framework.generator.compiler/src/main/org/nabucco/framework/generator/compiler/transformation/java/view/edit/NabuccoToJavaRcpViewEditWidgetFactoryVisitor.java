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
package org.nabucco.framework.generator.compiler.transformation.java.view.edit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewWidgetFactory;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.ComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.InputFieldDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledInputFieldDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledListPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ListPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PickerDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewEditWidgetFactoryVisitor
 * 
 * @author Nicolas Moser, Stefanie Feld, PRODYNA AG
 */
class NabuccoToJavaRcpViewEditWidgetFactoryVisitor extends NabuccoToJavaVisitorSupport {

    /**
     * the type of the model.
     */
    private String modelType;

    /**
     * The name of the view.
     */
    private String view;

    /**
     * The map with the unqualified type of all visited DatatypeDeclarations.
     */
    private Map<String, String> typeRefMap = new HashMap<String, String>();

    /**
     * Creates a new {@link NabuccoToJavaRcpViewEditWidgetFactoryVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewEditWidgetFactoryVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EditViewStatement nabuccoEditView, MdaModel<JavaModel> target) {

        modelType = nabuccoEditView.nodeToken2.tokenImage.replace(NabuccoJavaTemplateConstants.VIEW,
                NabuccoJavaTemplateConstants.VIEW + NabuccoJavaTemplateConstants.MODEL);
        view = nabuccoEditView.nodeToken2.tokenImage;

        // Visit sub-nodes first!
        super.visit(nabuccoEditView, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoEditView.nodeToken2.tokenImage + NabuccoJavaTemplateConstants.WIDGET_FACTORY;
        String mainPath = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP);
        String pkg = mainPath + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE;

        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            target.getModel().getUnitList().add(unit);

            changeModelFieldType(type);
            addImportModel(mainPath, unit);
            changeConstructor(name, type);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoEditView.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(), super
                    .getVisitorContext().getImportList());

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST editview modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
    }

    @Override
    public void visit(DatatypeDeclaration datatypeDeclaration, MdaModel<JavaModel> argu) {
        String unqualifiedType = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
        this.typeRefMap.put(datatypeDeclaration.nodeToken2.tokenImage, unqualifiedType);
    }

    @Override
    public void visit(LabeledInputFieldDeclaration labeledInputField, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(labeledInputField.annotationDeclaration);
            String name = labeledInputField.nodeToken2.tokenImage;
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewWidgetFactory.createInputField(annotationDeclarationList, name, true,
                            unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(labeledInputField, target);
    }

    @Override
    public void visit(InputFieldDeclaration inputField, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(inputField.annotationDeclaration);
            String name = inputField.nodeToken2.tokenImage;
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewWidgetFactory.createInputField(annotationDeclarationList, name, false,
                            unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(inputField, target);
    }

    @Override
    public void visit(LabeledPickerDeclaration labeledPickerDeclaration, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(labeledPickerDeclaration.annotationDeclaration);
            String name = labeledPickerDeclaration.nodeToken2.tokenImage;
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewWidgetFactory.createPicker(annotationDeclarationList, name, true, unit,
                            typeRefMap, view));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(labeledPickerDeclaration, target);
    }

    @Override
    public void visit(PickerDeclaration pickerDeclaration, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(pickerDeclaration.annotationDeclaration);
            String name = pickerDeclaration.nodeToken2.tokenImage;
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewWidgetFactory.createPicker(annotationDeclarationList, name, false,
                            unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(pickerDeclaration, target);
    }

    @Override
    public void visit(LabeledListPickerDeclaration pickerDeclaration, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(pickerDeclaration.annotationDeclaration);
            String name = pickerDeclaration.nodeToken2.tokenImage;
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewWidgetFactory.createPicker(annotationDeclarationList, name, true, unit,
                            typeRefMap, view, true));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(pickerDeclaration, target);
    }

    @Override
    public void visit(ListPickerDeclaration pickerDeclaration, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(pickerDeclaration.annotationDeclaration);
            String name = pickerDeclaration.nodeToken2.tokenImage;
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewWidgetFactory.createPicker(annotationDeclarationList, name, false,
                            unit, typeRefMap, view, true));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(pickerDeclaration, target);
    }

    @Override
    public void visit(LabeledComboBoxDeclaration labeledComboBoxDeclaration, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(labeledComboBoxDeclaration.annotationDeclaration);
            String name = labeledComboBoxDeclaration.nodeToken2.tokenImage;
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewWidgetFactory.createComboBox(annotationDeclarationList, name, true,
                            unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(labeledComboBoxDeclaration, target);
    }

    @Override
    public void visit(ComboBoxDeclaration comboBoxDeclaration, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(comboBoxDeclaration.annotationDeclaration);
            String name = comboBoxDeclaration.nodeToken2.tokenImage;
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewWidgetFactory.createComboBox(annotationDeclarationList, name, false,
                            unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(comboBoxDeclaration, target);
    }

    /**
     * Changes the type of the field model.
     * 
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeModelFieldType(TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // select the field model
        FieldDeclaration modelField = javaFactory.getJavaAstType().getField(type, ViewConstants.MODEL_FIELD);

        modelField.type = JavaAstModelProducer.getInstance().createTypeReference(modelType, false);

        javaFactory.getJavaAstField().setFieldType(modelField, modelField.type);
    }

    /**
     * Adds the import of the model.
     * 
     * @param mainPath
     *            ui.rcp path.
     * @param unit
     *            the java compilation unit.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void addImportModel(String mainPath, JavaCompilationUnit unit) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        String modelPath = mainPath + ViewConstants.PKG_SEPARATOR + ViewConstants.MODEL_PACKAGE;
        ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                modelPath + ViewConstants.PKG_SEPARATOR + modelType);

        javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
    }

    /**
     * Selects the constructor and changes the second param.
     * 
     * @param name
     *            the name of the constructor.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeConstructor(String name, TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        JavaAstMethodSignature signature = new JavaAstMethodSignature(name, ViewConstants.NABUCCO_FORM_TOOLKIT,
                ViewConstants.SEARCH_MODEL);
        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type, signature);

        ((SingleTypeReference) (constructor.arguments[1]).type).token = modelType.toCharArray();
    }
}
