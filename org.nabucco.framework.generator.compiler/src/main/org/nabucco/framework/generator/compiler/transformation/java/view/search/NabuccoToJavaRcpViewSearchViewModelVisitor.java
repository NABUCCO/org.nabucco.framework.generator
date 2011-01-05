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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewModelSupport;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewVisitorSupportUtil;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.ComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.InputFieldDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledInputFieldDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
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
 * NabuccoToJavaJavaRcpViewSearchViewModelVisitor
 * 
 * @author Silas Schwarz, Stefanie Feld PRODYNA AG
 */
class NabuccoToJavaRcpViewSearchViewModelVisitor extends NabuccoToJavaVisitorSupport implements
        ViewConstants {

    private NabuccoToJavaRcpViewVisitorSupport util;

    private List<Statement> constructorStatements = new ArrayList<Statement>();

    private String fieldType;

    private String fieldName;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewSearchViewModelVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaRcpViewSearchViewModelVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
        this.util = new NabuccoToJavaRcpViewVisitorSupport(visitorContext);
    }

    @Override
    public void visit(SearchViewStatement searchViewStatement, MdaModel<JavaModel> target) {

        // visit inner statements before this one
        super.visit(searchViewStatement, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String projectName = super.getComponentName(NabuccoClientType.RCP);
        String viewName = searchViewStatement.nodeToken2.tokenImage;
        String modelName = viewName.replace(NabuccoJavaTemplateConstants.VIEW,
                NabuccoJavaTemplateConstants.VIEW + NabuccoJavaTemplateConstants.MODEL);

        String originalPkg = super.getVisitorContext().getPackage();
        String pkg = originalPkg.replace(PKG_SEPARATOR + UI, PKG_SEPARATOR + UI_RCP)
                + PKG_SEPARATOR
                + MODEL_PACKAGE;

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SEARCH_VIEW_MODEL_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.SEARCH_VIEW_MODEL_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, modelName);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // TODO: fix this! taking the last found datatype is unacceptable
            TypeReference typeReference = JavaAstModelProducer.getInstance().createTypeReference(
                    fieldType, false);

            TypeReference superClass = javaFactory.getJavaAstType().getSuperClass(type);
            superClass = javaFactory.getJavaAstReference().getAsParameterized(superClass,
                    new TypeReference[] { typeReference });
            javaFactory.getJavaAstType().setSuperClass(type, superClass);

            for (ImportReference importRef : this.util.getCollectedImports()) {
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importRef);
            }

            // Adds the field initialization(s)
            this.addStatementsToConstructor(type);

            JavaCompilationUnit uIUnit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            TypeDeclaration uIType = uIUnit
                    .getType(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);

            List<JavaAstContainter<? extends ASTNode>> uiCommonElements = NabuccoToJavaRcpViewVisitorSupportUtil
                    .getUiCommonElements(uIType, type, searchViewStatement.annotationDeclaration);

            FieldDeclaration idField = (FieldDeclaration) uiCommonElements.get(0).getAstNode();
            idField.initialization = JavaAstModelProducer.getInstance().createLiteral(
                    new String(((StringLiteral) idField.initialization).source()) + MODEL,
                    LiteralType.STRING_LITERAL);
            super.getVisitorContext().getContainerList().addAll(uiCommonElements);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(),
                    super.getVisitorContext().getImportList());

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(searchViewStatement.annotationDeclaration,
                    type);

            NabuccoToJavaRcpViewVisitorSupportUtil.swapFieldOrder(type);
            unit.setProjectName(projectName);

            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error during Java Search View modification.", me);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java Template search view processing.",
                    te);
        }
    }

    @Override
    public void visit(DatatypeDeclaration datatypeDeclaration, MdaModel<JavaModel> target) {
        fieldType = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
        fieldName = datatypeDeclaration.nodeToken2.tokenImage;
        Boolean isMultiple = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(datatypeDeclaration.nodeToken1.tokenImage).isMultiple();

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(fieldType,
                fieldName, NabuccoModifierType.PRIVATE, isMultiple);

        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field
                .getAstNode());

        NabuccoToJavaVisitorContext context = new NabuccoToJavaVisitorContext();
        context.setRootDir(super.getVisitorContext().getRootDir());
        context.setOutDir(super.getVisitorContext().getOutDir());

        this.util.createMappingInformation(datatypeDeclaration, super.getVisitorContext());

        List<JavaAstContainter<? extends ASTNode>> containerList = super.getVisitorContext()
                .getContainerList();

        containerList.add(field);
        containerList.add(getter);

        this.addConstructorStatement();
    }

    /**
     * Adds the field allocation to the constructor statements.
     * 
     * @param name
     *            the field name
     * @param type
     *            the field type
     */
    private void addConstructorStatement() {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        try {
            FieldReference fieldRef = producer.createFieldThisReference(fieldName);
            TypeReference typeRef = producer.createTypeReference(fieldType, false);

            AllocationExpression allocation = producer.createAllocationExpression(typeRef, null);
            Assignment assignment = producer.createAssignment(fieldRef, allocation);

            this.constructorStatements.add(assignment);
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating search view model constructor.", e);
        }
    }

    /**
     * Adds the collected constructor statements to the constructor.
     * 
     * @param type
     *            the type holding the constructor
     * 
     * @throws JavaModelException
     */
    private void addStatementsToConstructor(TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = javaFactory.getJavaAstType().getTypeName(type);
        JavaAstMethodSignature constructorSignature = new JavaAstMethodSignature(name, "String");

        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type,
                constructorSignature);

        for (Statement statement : this.constructorStatements) {
            javaFactory.getJavaAstMethod().addStatement(constructor, statement);
        }
    }

    @Override
    public void visit(InputFieldDeclaration inputField, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(inputField.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElement(
                            annotationDeclarationList, unit, util, getVisitorContext()));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template search view processing.",
                    te);
        }
        super.visit(inputField, target);
    }

    @Override
    public void visit(LabeledInputFieldDeclaration labeledInputField, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(labeledInputField.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElement(
                            annotationDeclarationList, unit, util, getVisitorContext()));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template search view processing.",
                    te);
        }
        super.visit(labeledInputField, target);
    }

    @Override
    public void visit(LabeledPickerDeclaration labeledPicker, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(labeledPicker.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElement(
                            annotationDeclarationList, unit, util, getVisitorContext()));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template search view processing.",
                    te);
        }
        super.visit(labeledPicker, target);
    }

    @Override
    public void visit(PickerDeclaration picker, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(picker.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElement(
                            annotationDeclarationList, unit, util, getVisitorContext()));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template search view processing.",
                    te);
        }
        super.visit(picker, target);
    }

    @Override
    public void visit(LabeledComboBoxDeclaration labeledComboBox, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(labeledComboBox.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElementComboBox(
                            annotationDeclarationList, unit, util));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template searchview processing.",
                    te);
        }
        super.visit(labeledComboBox, target);
    }

    @Override
    public void visit(ComboBoxDeclaration comboBox, MdaModel<JavaModel> target) {
        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            List<NabuccoAnnotation> annotationDeclarationList = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotations(comboBox.annotationDeclaration);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewModelSupport.createModelElementComboBox(
                            annotationDeclarationList, unit, util));
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template searchview processing.",
                    te);
        }
        super.visit(comboBox, target);
    }

}
