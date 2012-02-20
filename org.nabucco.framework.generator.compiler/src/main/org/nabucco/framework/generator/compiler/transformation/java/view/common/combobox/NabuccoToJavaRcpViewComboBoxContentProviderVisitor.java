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
package org.nabucco.framework.generator.compiler.transformation.java.view.common.combobox;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.java.view.util.MappedFieldVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewComboBoxContentProviderVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewComboBoxContentProviderVisitor extends NabuccoToJavaVisitorSupport implements
        NabuccoJavaTemplateConstants, ViewConstants {

    /**
     * the annotation declaration of the view.
     */
    private AnnotationDeclaration viewAnnotationDeclaration;

    /**
     * the name of the view.
     */
    private String viewName;

    /**
     * The reference of the NabuccoToJavaRcpViewVisitorSupport.
     */
    private NabuccoToJavaRcpViewVisitorSupport util;

    /**
     * The key for the referenced field of the combo box.
     */
    private String referencedFieldTypeKey;

    /**
     * The method signature for "getElements".
     */
    private final JavaAstMethodSignature signature = new JavaAstMethodSignature(ViewConstants.GET_ELEMENTS,
            ViewConstants.OBJECT);

    /**
     * Creates a new {@link NabuccoToJavaRcpViewComboBoxContentProviderVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     * @param viewName
     *            the name of the view from which the constructor is called.
     * @param annotationDeclaration
     *            the annotationDeclaration of the view from which the constructor is called.
     * @param nabuccoEditView
     *            the edit view from which the constructor is called.
     */
    public NabuccoToJavaRcpViewComboBoxContentProviderVisitor(NabuccoToJavaVisitorContext visitorContext,
            String viewName, AnnotationDeclaration annotationDeclaration, EditViewStatement nabuccoEditView) {
        super(visitorContext);
        this.viewName = viewName;
        this.viewAnnotationDeclaration = annotationDeclaration;

        this.util = new NabuccoToJavaRcpViewVisitorSupport(this.getVisitorContext());

        MappedFieldVisitor mappedFieldVisitor = new MappedFieldVisitor();
        nabuccoEditView.accept(mappedFieldVisitor);

        this.util.setMappedFieldsInUse((mappedFieldVisitor.getMappedFields()));
    }

    /**
     * Constructor to create a new instance of NabuccoToJavaRcpViewComboBoxContentProviderVisitor.
     * 
     * @param visitorContext
     *            the context of the visitor.
     * @param viewName
     *            the name of the view from which the constructor is called.
     * @param annotationDeclaration
     *            the annotationDeclaration of the view from which the constructor is called.
     * @param nabuccoSearchView
     *            the search view from which the constructor is called.
     */
    public NabuccoToJavaRcpViewComboBoxContentProviderVisitor(NabuccoToJavaVisitorContext visitorContext,
            String viewName, AnnotationDeclaration annotationDeclaration, SearchViewStatement nabuccoSearchView) {
        super(visitorContext);
        this.viewName = viewName;
        this.viewAnnotationDeclaration = annotationDeclaration;

        this.util = new NabuccoToJavaRcpViewVisitorSupport(this.getVisitorContext());

        MappedFieldVisitor mappedFieldVisitor = new MappedFieldVisitor();
        nabuccoSearchView.accept(mappedFieldVisitor);

        this.util.setMappedFieldsInUse((mappedFieldVisitor.getMappedFields()));
    }

    @Override
    public void visit(DatatypeDeclaration datatypeDeclaration, MdaModel<JavaModel> target) {

        util.createMappingInformation(datatypeDeclaration, getVisitorContext());
    }

    @Override
    public void visit(LabeledComboBoxDeclaration labeledComboBox, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotationDeclaration = labeledComboBox.annotationDeclaration;
        String comboBoxName = labeledComboBox.nodeToken2.tokenImage;

        this.createComboBoxContentProvider(target, annotationDeclaration, comboBoxName);
    }

    @Override
    public void visit(ComboBoxDeclaration comboBox, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotationDeclaration = comboBox.annotationDeclaration;
        String comboBoxName = comboBox.nodeToken2.tokenImage;

        this.createComboBoxContentProvider(target, annotationDeclaration, comboBoxName);
    }

    /**
     * Creates a content provider for a combo box.
     * 
     * @param target
     *            the target mda model.
     * @param annotationDeclaration
     *            the annotation declaration of the combo box.
     * @param comboBoxName
     *            the name of the combo box.
     */
    private void createComboBoxContentProvider(MdaModel<JavaModel> target, AnnotationDeclaration annotationDeclaration,
            String comboBoxName) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = viewName
                + NabuccoTransformationUtility.firstToUpper(comboBoxName) + ViewConstants.CONTENT_PROVIDER;
        String pkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE;
        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            JavaCompilationUnit unit = super.extractAst(COMMON_VIEW_COMBO_BOX_CONTENT_PROVIDER_TEMPLATE);
            TypeDeclaration type = unit.getType(COMMON_VIEW_COMBO_BOX_CONTENT_PROVIDER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // change method getElements(Object arg0)
            this.changeGetElements(annotationDeclaration, type);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(annotationDeclaration, type);

            // addImport
            this.addImports(unit);

            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                    .getImportList());

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(viewAnnotationDeclaration, type);

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException(
                    "Error during Java AST editview combo box content provider modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template editview combo box content provider processing.", te);
        }
    }

    /**
     * Adds all imports to the unit.
     * 
     * @param unit
     *            the java compilation unit where all imports are added to.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void addImports(JavaCompilationUnit unit) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        if (this.util.getFieldNameToTypeReference().get(BASETYPE) != null
                && this.util.getFieldNameToTypeReference().get(BASETYPE).containsKey(referencedFieldTypeKey)) {
            for (String importReferenceString : this.util.getFieldNameToTypeReference().get(BASETYPE)
                    .get(referencedFieldTypeKey).getImports()) {
                ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                        importReferenceString);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
            }
        } else if (this.util.getFieldNameToTypeReference().get(ENUMERATION) != null
                && this.util.getFieldNameToTypeReference().get(ENUMERATION).containsKey(referencedFieldTypeKey)) {
            for (String importReferenceString : this.util.getFieldNameToTypeReference().get(ENUMERATION)
                    .get(referencedFieldTypeKey).getImports()) {
                ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                        importReferenceString);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
            }
        }
    }

    /**
     * Changes the method getElements().
     * 
     * @param annotationDeclaration
     *            the annotation declaration of the combo box.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeGetElements(AnnotationDeclaration annotationDeclaration, TypeDeclaration type)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // mappedField
        NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotationDeclaration,
                NabuccoAnnotationType.MAPPED_FIELD);
        String mappedField = mappedFieldAnn == null ? null : mappedFieldAnn.getValue();
        String[] accessPath = mappedField.split(ViewConstants.FIELD_SEPARATOR);
        String localField = accessPath[0];
        this.referencedFieldTypeKey = accessPath[1];

        SingleTypeReference typeReference;
        if (this.util.getFieldNameToFieldTypeProperties().get(localField).get(BASETYPE) != null
                && this.util.getFieldNameToFieldTypeProperties().get(localField).get(BASETYPE)
                        .containsKey(this.referencedFieldTypeKey)) {
            typeReference = (SingleTypeReference) this.util.getFieldNameToFieldTypeProperties().get(localField)
                    .get(BASETYPE).get(this.referencedFieldTypeKey).getAstNode();
        } else if (this.util.getFieldNameToFieldTypeProperties().get(localField).get(ENUMERATION) != null
                && this.util.getFieldNameToFieldTypeProperties().get(localField).get(ENUMERATION)
                        .containsKey(this.referencedFieldTypeKey)) {
            typeReference = (SingleTypeReference) this.util.getFieldNameToFieldTypeProperties().get(localField)
                    .get(ENUMERATION).get(this.referencedFieldTypeKey).getAstNode();
        } else {
            throw new NabuccoVisitorException("Used MappedField \""
                    + referencedFieldTypeKey + "\" is no Basetype or Enumeration.");
        }

        // select the method getElements(Object arg0)
        MethodDeclaration methodGetElements = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                this.signature);

        ((ParameterizedSingleTypeReference) ((LocalDeclaration) methodGetElements.statements[0]).type).typeArguments[0] = typeReference;
        ((ParameterizedSingleTypeReference) ((AllocationExpression) ((LocalDeclaration) methodGetElements.statements[0]).initialization).type).typeArguments[0] = typeReference;
        ((ForeachStatement) methodGetElements.statements[1]).elementVariable.type = typeReference;
        ((ForeachStatement) methodGetElements.statements[1]).collection = JavaAstModelProducer.getInstance()
                .createMessageSend(ViewConstants.VALUES, typeReference, null);
    }

}
