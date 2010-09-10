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

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
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
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewComboBoxLabelProviderVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewComboBoxLabelProviderVisitor extends NabuccoToJavaVisitorSupport
        implements ViewConstants, NabuccoJavaTemplateConstants {

    /**
     * the annotation declaration of the view.
     */
    private AnnotationDeclaration viewAnnotationDeclaration;

    /**
     * the name of the view.
     */
    private String viewName;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewComboBoxLabelProviderVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     * @param viewName
     *            the name of the view from which the constructor is called.
     * @param annotationDeclaration
     *            the name of the view from which the constructor is called.
     */
    public NabuccoToJavaRcpViewComboBoxLabelProviderVisitor(
            NabuccoToJavaVisitorContext visitorContext, String viewName,
            AnnotationDeclaration annotationDeclaration) {
        super(visitorContext);
        this.viewName = viewName;
        this.viewAnnotationDeclaration = annotationDeclaration;
    }

    @Override
    public void visit(LabeledComboBoxDeclaration labeledComboBox, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotationDeclaration = labeledComboBox.annotationDeclaration;
        String comboBoxName = labeledComboBox.nodeToken2.tokenImage;

        this.createComboBoxLabelProvider(target, annotationDeclaration, comboBoxName);
    }

    @Override
    public void visit(ComboBoxDeclaration comboBox, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotationDeclaration = comboBox.annotationDeclaration;
        String comboBoxName = comboBox.nodeToken2.tokenImage;

        this.createComboBoxLabelProvider(target, annotationDeclaration, comboBoxName);
    }

    /**
     * Creates a label provider for a combo box.
     * 
     * @param target
     *            the target mda model.
     * @param annotationDeclaration
     *            the annotation declaration of the combo box.
     * @param comboBoxName
     *            the name of the combo box.
     */
    private void createComboBoxLabelProvider(MdaModel<JavaModel> target,
            AnnotationDeclaration annotationDeclaration, String comboBoxName) {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        String name = viewName + NabuccoTransformationUtility.firstToUpper(comboBoxName)
                + ViewConstants.LABEL_PROVIDER;
        String pkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI,
                ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR
                + ViewConstants.VIEW_PACKAGE
                + ViewConstants.PKG_SEPARATOR + ViewConstants.LABEL_PACKAGE;
        String projectName = super.getComponentName(NabuccoClientType.RCP);
        try {
            JavaCompilationUnit unit = super
                    .extractAst(COMMON_VIEW_COMBO_BOX_LABEL_PROVIDER_TEMPLATE);
            TypeDeclaration type = unit.getType(COMMON_VIEW_COMBO_BOX_LABEL_PROVIDER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(),
                    getVisitorContext().getImportList());

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(viewAnnotationDeclaration, type);

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException(
                    "Error during Java AST combo box label provider modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template combo box label provider processing.", te);
        }
    }

}
