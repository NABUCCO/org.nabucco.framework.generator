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
package org.nabucco.framework.generator.compiler.transformation.java.view.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
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
import org.nabucco.framework.generator.parser.syntaxtree.InputFieldDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledInputFieldDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewSearchWidgetFactoryVisitor
 * 
 * @author Silas Schwarz, Stefanie Feld PRODYNA AG
 */
class NabuccoToJavaRcpViewSearchWidgetFactoryVisitor extends NabuccoToJavaVisitorSupport {

    private String modelType;

    private String view;

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaRcpViewSearchWidgetFactoryVisitor.class);

    private Map<String, String> typeRefMap = new HashMap<String, String>();

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewSearchWidgetFactoryVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);

    }

    @Override
    public void visit(SearchViewStatement searchViewStatement, MdaModel<JavaModel> target) {

        modelType = searchViewStatement.nodeToken2.tokenImage.replace(
                NabuccoJavaTemplateConstants.VIEW, NabuccoJavaTemplateConstants.VIEW
                        + ViewConstants.MODEL);
        view = searchViewStatement.nodeToken2.tokenImage;
        // visit children first
        super.visit(searchViewStatement, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String projectName = super.getComponentName(NabuccoClientType.RCP);
        String name = searchViewStatement.nodeToken2.tokenImage
                + NabuccoJavaTemplateConstants.WIDGET_FACTORY;

        String mainPath = super.getVisitorContext().getPackage().replace(ViewConstants.UI,
                ViewConstants.UI_RCP);
        String pkg = mainPath + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE;

        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SEARCH_VIEW_WIDGET_FACTORY_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SEARCH_VIEW_WIDGET_FACTORY_TEMPLATE);
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            target.getModel().getUnitList().add(unit);

            // change type of the attribute model

            FieldDeclaration modelField = javaFactory.getJavaAstType().getField(type,
                    ViewConstants.MODEL_FIELD);
            String modelPath = mainPath + ViewConstants.PKG_SEPARATOR + ViewConstants.MODEL_PACKAGE;

            modelField.type = JavaAstModelProducer.getInstance().createTypeReference(modelType,
                    false);

            javaFactory.getJavaAstField().setFieldType(modelField, modelField.type);

            ImportReference importReference = JavaAstModelProducer.getInstance()
                    .createImportReference(modelPath + ViewConstants.PKG_SEPARATOR + modelType);

            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

            // change second param of the constructor

            JavaAstMethodSignature signature = new JavaAstMethodSignature(name,
                    ViewConstants.NABUCCO_FORM_TOOLKIT, ViewConstants.SEARCH_MODEL);

            ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type,
                    signature);

            ((SingleTypeReference) (constructor.arguments[1]).type).token = modelType.toCharArray();

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(searchViewStatement.annotationDeclaration,
                    type);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(),
                    super.getVisitorContext().getImportList());

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST editview modification.");
            throw new NabuccoVisitorException("Error during Java AST editview modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template editview processing.");
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

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(labeledInputField.annotationDeclaration);
            String name = labeledInputField.nodeToken2.tokenImage;
            super.getVisitorContext().getContainerList().addAll(
                    NabuccoToJavaRcpViewWidgetFactory.createInputField(annotationDeclarationList,
                            name, true, unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template editview processing.");
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(labeledInputField, target);
    }

    @Override
    public void visit(InputFieldDeclaration inputField, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(inputField.annotationDeclaration);
            String name = inputField.nodeToken2.tokenImage;
            super.getVisitorContext().getContainerList().addAll(
                    NabuccoToJavaRcpViewWidgetFactory.createInputField(annotationDeclarationList,
                            name, false, unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template editview processing.");
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(inputField, target);
    }

    @Override
    public void visit(LabeledComboBoxDeclaration labeledComboBox, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(labeledComboBox.annotationDeclaration);
            String name = labeledComboBox.nodeToken2.tokenImage;
            super.getVisitorContext().getContainerList().addAll(
                    NabuccoToJavaRcpViewWidgetFactory.createComboBox(annotationDeclarationList,
                            name, true, unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template editview processing.");
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(labeledComboBox, target);
    }

    @Override
    public void visit(ComboBoxDeclaration comboBox, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);

            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(comboBox.annotationDeclaration);
            String name = comboBox.nodeToken2.tokenImage;
            super.getVisitorContext().getContainerList().addAll(
                    NabuccoToJavaRcpViewWidgetFactory.createComboBox(annotationDeclarationList,
                            name, false, unit, typeRefMap, view));
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template editview processing.");
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(comboBox, target);
    }

}
