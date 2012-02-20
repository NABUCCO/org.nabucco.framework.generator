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
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionImplementationType;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.FieldOptions;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewModelSupport;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.java.view.util.MappedFieldVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
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
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewModelEditVisitor
 * 
 * @author Silas Schwarz, Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewModelEditVisitor extends NabuccoToJavaVisitorSupport implements ViewConstants {

    private NabuccoToJavaRcpViewVisitorSupport util;

    private Map<String, String> typeRefMap = new HashMap<String, String>();

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewModelEditVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EditViewStatement editViewStatement, MdaModel<JavaModel> target) {
        this.util = new NabuccoToJavaRcpViewVisitorSupport(this.getVisitorContext());

        MappedFieldVisitor mappedFieldVisitor = new MappedFieldVisitor();
        editViewStatement.accept(mappedFieldVisitor);

        this.util.setMappedFieldsInUse((mappedFieldVisitor.getMappedFields()));
        // children first, we need the datatypes
        super.visit(editViewStatement, target);

        String pkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR + ViewConstants.MODEL_PACKAGE;
        String name = editViewStatement.nodeToken2.tokenImage.replace(NabuccoJavaTemplateConstants.VIEW,
                NabuccoJavaTemplateConstants.VIEW + NabuccoJavaTemplateConstants.MODEL);
        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.EDIT_VIEW_MODEL_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.EDIT_VIEW_MODEL_TEMPLATE);
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            modifyIdMethod(type, pkg, name);

            modifyGetValues(type, mappedFieldVisitor.getMappedFields());

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(editViewStatement.annotationDeclaration, type);

            for (ImportReference importReference : this.util.getCollectedImports()) {
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
            }

            // append AST nodes
            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(), super
                    .getVisitorContext().getImportList());

            unit.setProjectName(projectName);

            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST edit view modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template edit view processing.", te);
        }
    }

    /**
     * @param type
     * @param mappedFields
     * @throws JavaModelException
     */
    private void modifyGetValues(TypeDeclaration type, Set<String> mappedFields) throws JavaModelException {
        JavaAstMethodSignature signature = new JavaAstMethodSignature(GET
                + NabuccoTransformationUtility.firstToUpper(VALUES), new String[] {});
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        MethodDeclaration getValuesMethod = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);
        for (String field : mappedFields) {
            NabuccoToJavaRcpViewVisitorSupport.addGetValuesEntry(getValuesMethod, field);
        }

    }

    private void modifyIdMethod(TypeDeclaration type, String pkg, String name) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        MethodDeclaration getIdMethod = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                new JavaAstMethodSignature(ViewConstants.GET_ID_UPPERCASE, new String[] {}));
        ((ReturnStatement) getIdMethod.statements[0]).expression = JavaAstModelProducer.getInstance().createLiteral(
                pkg + ViewConstants.PKG_SEPARATOR + name, LiteralType.STRING_LITERAL);

    }

    @Override
    public void visit(DatatypeDeclaration datatypeDeclaration, MdaModel<JavaModel> target) {

        String unqualifiedType = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
        this.typeRefMap.put(datatypeDeclaration.nodeToken2.tokenImage, unqualifiedType);

        String fieldType = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
        String fieldName = datatypeDeclaration.nodeToken2.tokenImage;
        Boolean isMultiple = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(datatypeDeclaration.nodeToken1.tokenImage).isMultiple();

        List<JavaAstContainter<? extends ASTNode>> containerList = super.getVisitorContext().getContainerList();

        this.util.createMappingInformation(datatypeDeclaration, super.getVisitorContext());

        JavaAstContainter<FieldDeclaration> field;

        if (isMultiple) {
            field = JavaAstSupport.createField(fieldType, fieldName, NabuccoModifierType.PRIVATE, CollectionType.SET,
                    CollectionImplementationType.DEFAULT);
        } else {
            field = JavaAstSupport.createField(fieldType, fieldName, NabuccoModifierType.PRIVATE);
            this.util.createGetterSetterForModelField(datatypeDeclaration, super.getVisitorContext());
        }

        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field.getAstNode(),
                FieldOptions.valueOf(CollectionImplementationType.DEFAULT));

        containerList.add(field);
        containerList.add(getter);
    }

    @Override
    public void visit(InputFieldDeclaration inputField, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(inputField.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElement(annotationDeclarationList, unit, util,
                            getVisitorContext()));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(inputField, target);
    }

    @Override
    public void visit(LabeledInputFieldDeclaration labeledInputField, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(labeledInputField.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElement(annotationDeclarationList, unit, util,
                            getVisitorContext()));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(labeledInputField, target);
    }

    @Override
    public void visit(LabeledPickerDeclaration labeledPicker, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(labeledPicker.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElement(annotationDeclarationList, unit, util,
                            getVisitorContext()));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(labeledPicker, target);
    }

    @Override
    public void visit(PickerDeclaration picker, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(picker.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElement(annotationDeclarationList, unit, util,
                            getVisitorContext()));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(picker, target);
    }

    @Override
    public void visit(LabeledListPickerDeclaration picker, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(picker.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElementListPicker(annotationDeclarationList,
                            unit, util));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(picker, target);
    }

    @Override
    public void visit(ListPickerDeclaration picker, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(picker.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElementListPicker(annotationDeclarationList,
                            unit, util));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(picker, target);
    }

    @Override
    public void visit(LabeledComboBoxDeclaration labeledComboBox, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(labeledComboBox.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElementComboBox(annotationDeclarationList,
                            unit, util));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(labeledComboBox, target);
    }

    @Override
    public void visit(ComboBoxDeclaration comboBox, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotationList(comboBox.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElementComboBox(annotationDeclarationList,
                            unit, util));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
        super.visit(comboBox, target);
    }
}
