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
package org.nabucco.framework.generator.compiler.transformation.java.view.common.picker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledListPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ListPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PickerDeclaration;
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
 * NabuccoToJavaRcpViewEditVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
class NabuccoToJavaRcpViewPickerContentProviderVisitor extends NabuccoToJavaVisitorSupport
        implements NabuccoJavaTemplateConstants, ViewConstants {

    /**
     * the annotation declaration of the view.
     */
    private AnnotationDeclaration viewAnnotationDeclaration;

    /**
     * map of all unqualified types.
     */
    private Map<String, String> typeUnqualifiedMap = new HashMap<String, String>();

    /**
     * map of all unqualified types.
     */
    private Map<String, String> typeQualifiedMap = new HashMap<String, String>();

    /**
     * Mapping from field name to field type properties of all datatypes.
     */
    private Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> fieldNameToFieldTypeProperties;

    /**
     * Evaluated View model name.
     */
    private String modelName;

    /**
     * Evaluated View model package
     */
    private String modelPackage;

    /**
     * Constructor to create a new instance of NabuccoToJavaRcpViewPickerContentProviderVisitor.
     * 
     * @param visitorContext
     *            the context of the visitor.
     * @param annotationDeclaration
     *            the annotationDeclaration of the view from which the constructor is called.
     */
    public NabuccoToJavaRcpViewPickerContentProviderVisitor(
            NabuccoToJavaVisitorContext visitorContext,
            AnnotationDeclaration annotationDeclaration,
            Map<String, Map<String, JavaAstContainter<TypeReference>>> fieldNameToTypeReference,
            Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> fieldNameToFieldTypeProperties) {
        super(visitorContext);
        this.viewAnnotationDeclaration = annotationDeclaration;
        this.fieldNameToFieldTypeProperties = fieldNameToFieldTypeProperties;
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {

        String unqualifiedType = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        String qualifiedType = super.resolveImport(unqualifiedType);
        this.typeUnqualifiedMap.put(nabuccoDatatype.nodeToken2.tokenImage, unqualifiedType);
        this.typeQualifiedMap.put(nabuccoDatatype.nodeToken2.tokenImage, qualifiedType);

        // Visit sub-nodes first!
        super.visit(nabuccoDatatype, target);

    }

    @Override
    public void visit(PickerDeclaration picker, MdaModel<JavaModel> target) {

        String name = NabuccoTransformationUtility.firstToUpper(picker.nodeToken2.tokenImage)
                + CONTENT_PROVIDER;

        createContentProvider(name, picker.annotationDeclaration, target);
    }

    @Override
    public void visit(LabeledPickerDeclaration picker, MdaModel<JavaModel> target) {

        String name = NabuccoTransformationUtility.firstToUpper(picker.nodeToken2.tokenImage)
                + CONTENT_PROVIDER;

        createContentProvider(name, picker.annotationDeclaration, target);
    }

    @Override
    public void visit(ListPickerDeclaration picker, MdaModel<JavaModel> target) {

        String name = NabuccoTransformationUtility.firstToUpper(picker.nodeToken2.tokenImage)
                + CONTENT_PROVIDER;

        createContentProvider(name, picker.annotationDeclaration, target);
    }

    @Override
    public void visit(LabeledListPickerDeclaration picker, MdaModel<JavaModel> target) {

        String name = NabuccoTransformationUtility.firstToUpper(picker.nodeToken2.tokenImage)
                + CONTENT_PROVIDER;

        createContentProvider(name, picker.annotationDeclaration, target);
    }

    /**
     * Creates the content provider for the given picker type.
     * 
     * @param name
     *            name of the picker
     * @param annotation
     *            the picker's annotation declaration
     * @param target
     *            the target java model
     */
    private void createContentProvider(String name, AnnotationDeclaration annotation,
            MdaModel<JavaModel> target) {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String pkg = super.getVisitorContext().getPackage().replace(UI, UI_RCP)
                + PKG_SEPARATOR + VIEW_PACKAGE;

        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {

            JavaCompilationUnit unit = super.extractAst(EDIT_VIEW_PICKER_CONTENT_PROVIDER_TEMPLATE);
            TypeDeclaration type = unit.getType(EDIT_VIEW_PICKER_CONTENT_PROVIDER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(annotation, type);

            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(),
                    getVisitorContext().getImportList());

            JavaAstModelProducer modelProducer = JavaAstModelProducer.getInstance();

            TypeReference modelType = modelProducer.createTypeReference(this.modelName, false);
            // alter field type
            FieldDeclaration viewModelField = javaFactory.getJavaAstType().getField(type,
                    "viewModel");
            viewModelField.type = modelType;

            // alter constructor
            ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type,
                    new JavaAstMethodSignature(name, "ModelDataType"));
            List<Argument> allArguments = javaFactory.getJavaAstMethod().getAllArguments(
                    constructor);
            for (Argument current : allArguments) {
                current.type = modelType;
            }

            ImportReference viewModelImportRefernce = modelProducer
                    .createImportReference(this.modelPackage + '.' + this.modelName);

            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(),
                    viewModelImportRefernce);

            // select field "handler"
            FieldDeclaration handlerField = javaFactory.getJavaAstType().getField(type,
                    HANDLER_INSTANCE);

            ((SingleTypeReference) handlerField.type).token = (name + ViewConstants.HANDLER)
                    .toCharArray();

            ((SingleTypeReference) ((ClassLiteralAccess) ((MessageSend) handlerField.initialization).arguments[0]).type).token = (name + ViewConstants.HANDLER)
                    .toCharArray();

            ((SingleTypeReference) ((ClassLiteralAccess) ((MessageSend) ((MessageSend) handlerField.initialization).receiver).arguments[0]).type).token = name
                    .toCharArray();

            // define the right Datatype
            NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotation(annotation, NabuccoAnnotationType.MAPPED_FIELD);

            if (mappedFieldAnn != null && mappedFieldAnn.getValue() != null) {
                String newDatatype;
                String importString = "";
                if (mappedFieldAnn.getValue().split(FIELD_SEPARATOR).length == 2) {
                    String mappedType = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[0];
                    newDatatype = this.typeUnqualifiedMap.get(mappedType);
                    importString = this.typeQualifiedMap.get(mappedType);
                } else if (mappedFieldAnn.getValue().split(FIELD_SEPARATOR).length == 3) {

                    String mappedDatatype = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[0];
                    String mappedType = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[1];

                    Map<String, Map<String, JavaAstContainter<TypeReference>>> datatypeMap = this.fieldNameToFieldTypeProperties
                            .get(mappedDatatype);
                    Map<String, JavaAstContainter<TypeReference>> datatypeSubMap = datatypeMap
                            .get(DATATYPE);

                    JavaAstContainter<TypeReference> javacontainer = datatypeSubMap.get(mappedType);
                    TypeReference ref = javacontainer.getAstNode();
                    newDatatype = ref.toString();

                    Set<String> imports = javacontainer.getImports();
                    // import of the fieldType
                    for (String current : imports) {
                        importString = current;
                    }
                } else {
                    // FIXME: throw new exception!!!!
                    throw new NabuccoVisitorException();
                }

                // replace Datatype in
                // "private HashMap<String, Datatype[]> values;"
                FieldDeclaration field = javaFactory.getJavaAstType().getField(type, VALUES);

                ((ArrayTypeReference) ((ParameterizedSingleTypeReference) field.type).typeArguments[1]).token = newDatatype
                        .toCharArray();

                // select the method initValues()
                JavaAstMethodSignature signature = new JavaAstMethodSignature(INIT_VALUES);

                MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType()
                        .getMethod(type, signature);

                // replace "Datatype" within the method initValues() by the real datatype
                ((MessageSend) ((Assignment) method.statements[0]).expression).selector = (LOAD_ALL + newDatatype)
                        .toCharArray();

                // Add the imports
                ImportReference importReference = JavaAstModelProducer.getInstance()
                        .createImportReference(importString);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

            } else {
                // FIXME: throw new exception!!!!
                throw new NabuccoVisitorException();
            }

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(viewAnnotationDeclaration, type);

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException(
                    "Error during Java AST editview picker content provider modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template editview picker content provider processing.", te);
        }
    }

    @Override
    public void visit(EditViewStatement n, MdaModel<JavaModel> argu) {
        setPackage();
        setModelName(n.nodeToken2.tokenImage);
        super.visit(n, argu);
    }

    @Override
    public void visit(SearchViewStatement n, MdaModel<JavaModel> argu) {
        setPackage();
        setModelName(n.nodeToken2.tokenImage);
        super.visit(n, argu);
    }

    private void setModelName(String input) {
        this.modelName = input.replace(NabuccoJavaTemplateConstants.VIEW,
                NabuccoJavaTemplateConstants.VIEW + NabuccoJavaTemplateConstants.MODEL);
    }

    private void setPackage() {
        this.modelPackage = super.getVisitorContext().getPackage()
                .replace(PKG_SEPARATOR + UI, PKG_SEPARATOR + UI_RCP)
                + PKG_SEPARATOR + MODEL_PACKAGE;
    }
}
