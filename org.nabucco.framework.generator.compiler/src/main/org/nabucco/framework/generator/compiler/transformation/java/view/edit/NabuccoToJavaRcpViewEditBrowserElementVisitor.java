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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.browsersupport.BrowserElementSupport;
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
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewEditBrowserElementVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewEditBrowserElementVisitor extends NabuccoToJavaVisitorSupport implements ViewConstants {

    DatatypeDeclaration datatypeDeclaration;

    List<NabuccoAnnotation> annotationList = new ArrayList<NabuccoAnnotation>();

    /**
     * Creates a new {@link NabuccoToJavaRcpViewEditBrowserElementVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewEditBrowserElementVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EditViewStatement nabuccoEditView, MdaModel<JavaModel> target) {
        String viewName = nabuccoEditView.nodeToken2.tokenImage;
        String datatypePkg = super.getVisitorContext().getPackage();

        // Visit sub-nodes!
        super.visit(nabuccoEditView, target);

        try {

            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
            if (this.datatypeDeclaration == null) {
                throw new NabuccoVisitorException("Missing Annotation @Leading.");
            }
            String datatype = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
            String name = datatype + EDIT_VIEW_BROWSER_ELEMENT;
            String pkg = datatypePkg.replace(UI, UI_RCP).replace(PKG_EDIT, PKG_BROWSER);

            String projectName = super.getComponentName(NabuccoClientType.RCP);

            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.BROWSER_VIEW_ELEMENT_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.BROWSER_VIEW_ELEMENT_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            modifyTemplate(viewName, datatype, unit, type);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(datatypeDeclaration.annotationDeclaration, type);
            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                    .getImportList());

            // add import of editViewModel
            String importString = datatypePkg.replace(UI, UI_RCP)
                    + PKG_SEPARATOR + MODEL_PACKAGE + PKG_SEPARATOR + viewName + MODEL;
            BrowserElementSupport.addImport(importString, unit);

            // add import of datatype
            BrowserElementSupport.addImport(super.resolveImport(datatype), unit);

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST browser element modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template browser element processing.", te);
        }
    }

    @Override
    public void visit(DatatypeDeclaration datatypeDeclaration, MdaModel<JavaModel> target) {
        // only for the leading datatype
        NabuccoAnnotation leadingAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                datatypeDeclaration.annotationDeclaration, NabuccoAnnotationType.LEADING);
        if (leadingAnn != null) {
            this.datatypeDeclaration = datatypeDeclaration;
        }
        super.visit(datatypeDeclaration, target);
    }

    @Override
    public void visit(InputFieldDeclaration inputField, MdaModel<JavaModel> target) {
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                inputField.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);
        this.annotationList.add(mappedFieldAnn);
        super.visit(inputField, target);
    }

    @Override
    public void visit(LabeledInputFieldDeclaration labeledInputField, MdaModel<JavaModel> target) {
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                labeledInputField.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);
        this.annotationList.add(mappedFieldAnn);
        super.visit(labeledInputField, target);
    }

    @Override
    public void visit(LabeledPickerDeclaration labeledPicker, MdaModel<JavaModel> target) {
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                labeledPicker.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);
        this.annotationList.add(mappedFieldAnn);
        super.visit(labeledPicker, target);
    }

    @Override
    public void visit(PickerDeclaration picker, MdaModel<JavaModel> target) {
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                picker.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);
        this.annotationList.add(mappedFieldAnn);
        super.visit(picker, target);
    }

    @Override
    public void visit(LabeledListPickerDeclaration picker, MdaModel<JavaModel> target) {
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                picker.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);
        this.annotationList.add(mappedFieldAnn);
        super.visit(picker, target);
    }

    @Override
    public void visit(ListPickerDeclaration picker, MdaModel<JavaModel> target) {
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                picker.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);
        this.annotationList.add(mappedFieldAnn);
        super.visit(picker, target);
    }

    @Override
    public void visit(LabeledComboBoxDeclaration labeledComboBox, MdaModel<JavaModel> target) {
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                labeledComboBox.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);
        this.annotationList.add(mappedFieldAnn);
        super.visit(labeledComboBox, target);
    }

    @Override
    public void visit(ComboBoxDeclaration comboBox, MdaModel<JavaModel> target) {
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                comboBox.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD);
        this.annotationList.add(mappedFieldAnn);
        super.visit(comboBox, target);
    }

    /**
     * Calls all needed methods to change the template.
     * 
     * @param viewName
     *            the name of the view.
     * @param datatype
     *            the name of the datatype.
     * @param unit
     *            the java compilation unit where all imports are added to.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     * @throws JavaTemplateException
     *             if an error occurred loading the template.
     */
    private void modifyTemplate(String viewName, String datatype, JavaCompilationUnit unit, TypeDeclaration type)
            throws JavaModelException, JavaTemplateException {
        String datatypeName = datatypeDeclaration.nodeToken2.tokenImage;
        String name = datatype + EDIT_VIEW_BROWSER_ELEMENT;

        super.extractAst(NabuccoJavaTemplateConstants.BROWSER_VIEW_HELPER_TEMPLATE);

        BrowserElementSupport.changeFieldViewModel(viewName, type);
        BrowserElementSupport.changeFieldBrowserHandler(datatype, type);
        BrowserElementSupport.changeConstructor(viewName, datatypeName, datatype, name, type);
        // BrowserElementSupport.addGetValuesStatements(datatypeName, viewName, this.annotationList,
        // type, helperUnit);
        BrowserElementSupport.changeGetViewModel(viewName, type);
        BrowserElementSupport.changeSetViewModel(viewName, type);
    }

}
