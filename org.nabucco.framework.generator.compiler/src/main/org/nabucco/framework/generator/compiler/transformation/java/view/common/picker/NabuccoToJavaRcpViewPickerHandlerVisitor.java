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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
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
 * NabuccoToJavaRcpViewEditPickerHandlerVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewPickerHandlerVisitor extends NabuccoToJavaVisitorSupport implements
        ViewConstants, CollectionConstants {

    /** The <code>@SupressWarnings</code> annotation */
    private static final String ANNOTATION_SUPPRESS_WARNINGS = "SuppressWarnings";

    /** The <code>"unchecked"</code> value for the <code>@SupressWarnings</code> annotation */
    private static final String ANNOTATION_VALUE_UNCHECKED = "unchecked";
    
    /** Template method <code>elementSelected(TypedEvent event)</code> */
    private static final JavaAstMethodSignature SIGNATURE_ELEMENTS_SELECTED = new JavaAstMethodSignature(
            ELEMENT_SELECTED, TYPED_EVENT);
    
    /** The name declaration of the view. */
    private String view;

    /** The annotation declaration of the view. */
    private AnnotationDeclaration viewAnnotationDeclaration;

    /** Map of all unqualified types. */
    private Map<String, String> typeUnqualifiedMap = new HashMap<String, String>();

    /** Map of all unqualified types. */
    private Map<String, String> typeQualifiedMap = new HashMap<String, String>();

    /** Mapping from field name to field type properties of all datatypes. */
    private Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> fieldNameToFieldTypeProperties;

    /**
     * Constructor to create a new instance of NabuccoToJavaRcpViewPickerHandlerVisitor.
     * 
     * @param visitorContext
     *            the context of the visitor.
     * @param name
     *            the name of the view from which the constructor is called.
     * @param annotationDeclaration
     *            the annotationDeclaration of the view from which the constructor is called.
     */
    public NabuccoToJavaRcpViewPickerHandlerVisitor(
            NabuccoToJavaVisitorContext visitorContext,
            String name,
            AnnotationDeclaration annotationDeclaration,
            Map<String, Map<String, JavaAstContainter<TypeReference>>> fieldNameToTypeReference,
            Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> fieldNameToFieldTypeProperties) {
        super(visitorContext);
        this.viewAnnotationDeclaration = annotationDeclaration;
        this.view = name;
        this.fieldNameToFieldTypeProperties = fieldNameToFieldTypeProperties;
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {

        String unqualifiedType = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        String qualifiedType = super.resolveImport(unqualifiedType);
        this.typeUnqualifiedMap.put(nabuccoDatatype.nodeToken2.tokenImage, unqualifiedType);
        this.typeQualifiedMap.put(nabuccoDatatype.nodeToken2.tokenImage, qualifiedType);

        super.visit(nabuccoDatatype, target);
    }
    
    @Override
    public void visit(PickerDeclaration nabuccoWidget, MdaModel<JavaModel> target) {
        
        String pickerName = nabuccoWidget.nodeToken2.tokenImage;
        AnnotationDeclaration annotations = nabuccoWidget.annotationDeclaration;

        this.createPickerHandler(pickerName, annotations, false, target);
    }

    @Override
    public void visit(LabeledPickerDeclaration nabuccoWidget, MdaModel<JavaModel> target) {

        String pickerName = nabuccoWidget.nodeToken2.tokenImage;
        AnnotationDeclaration annotations = nabuccoWidget.annotationDeclaration;

        this.createPickerHandler(pickerName, annotations, false, target);
    }
    
    @Override
    public void visit(ListPickerDeclaration nabuccoWidget, MdaModel<JavaModel> target) {
        
        String pickerName = nabuccoWidget.nodeToken2.tokenImage;
        AnnotationDeclaration annotations = nabuccoWidget.annotationDeclaration;
        
        this.createPickerHandler(pickerName, annotations, true, target);
    }
    
    @Override
    public void visit(LabeledListPickerDeclaration nabuccoWidget, MdaModel<JavaModel> target) {
        
        String pickerName = nabuccoWidget.nodeToken2.tokenImage;
        AnnotationDeclaration annotations = nabuccoWidget.annotationDeclaration;
        
        this.createPickerHandler(pickerName, annotations, true, target);
    }

    /**
     * Craetes the picker handler java model.
     * 
     * @param pickerName
     *            name of the picker
     * @param annotations
     *            the annotations
     * @param isList
     *            whether it is a list picker or not
     * @param target
     *            the target model
     */
    private void createPickerHandler(String pickerName, AnnotationDeclaration annotations,
            boolean isList, MdaModel<JavaModel> target) {

        String handlerName = NabuccoTransformationUtility.firstToUpper(pickerName) + HANDLER;
        String pkg = super.getVisitorContext().getPackage().replace(UI, UI_RCP)
                + PKG_SEPARATOR + VIEW_PACKAGE;

        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            
            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EDIT_VIEW_PICKER_HANDLER_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.EDIT_VIEW_PICKER_HANDLER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, handlerName);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(),
                    getVisitorContext().getImportList());

            // define the right Datatype
            NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotation(annotations, NabuccoAnnotationType.MAPPED_FIELD);

            if (mappedFieldAnn != null && mappedFieldAnn.getValue() != null) {
                
                String newDatatype;
                String importString = "";
                String mappedType = "";
                
                if (mappedFieldAnn.getValue().split(FIELD_SEPARATOR).length == 2) {
                    mappedType = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[0];
                    newDatatype = this.typeUnqualifiedMap.get(mappedType);
                    importString = this.typeQualifiedMap.get(mappedType);

                } else if (mappedFieldAnn.getValue().split(FIELD_SEPARATOR).length == 3) {

                    String mappedDatatype = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[0];
                    mappedType = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[1];

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
                    // TODO: throw new exception!!!!
                    throw new NabuccoVisitorException("Mapped Field not valid for picker.");
                }

                String model = view + MODEL;

                // select field "model"
                FieldDeclaration field = javaFactory.getJavaAstType().getField(type, MODEL_FIELD);
                TypeReference modelType = producer.createTypeReference(model, false);
                field.type = modelType;
                
                // Constructor
                JavaAstMethodSignature constructorSignature = new JavaAstMethodSignature(
                        handlerName, DATATYPE_EDIT_VIEW_MODEL);
                ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(
                        type, constructorSignature);
                constructor.arguments[0].type = modelType;

                // select the method elementSelected(TypedEvent aTypedEvent)
                MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType()
                        .getMethod(type, SIGNATURE_ELEMENTS_SELECTED);

                if (isList) {
                    this.updateForListPicker(newDatatype, mappedType, method);
                    ImportReference setImport = producer.createImportReference(IMPORT_SET);
                    javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), setImport);
                } else {
                    this.updateForElementPicker(newDatatype, mappedType, method);
                }
                
                // Add the imports
                ImportReference importReference = producer.createImportReference(importString);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

                // import of the model
                String modelImport = pkg.replace(VIEW_PACKAGE, MODEL_PACKAGE)
                        + PKG_SEPARATOR + model;
                importReference = producer.createImportReference(
                        modelImport);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

            } else {
                // TODO: throw new exception!!!!
                throw new NabuccoVisitorException("Mapped field not defined for picker.");
            }

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(annotations, type);

            // Javadoc Annotations
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

    private void updateForElementPicker(String type, String mappedType,
            MethodDeclaration method) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        
        // replace "setMappedDatatype" by the real datatype
        TypeReference typeReference = producer.createTypeReference(type, false);

        IfStatement ifStatement = (IfStatement) method.statements[0];
        InstanceOfExpression condition = (InstanceOfExpression) ifStatement.condition;
        condition.type = typeReference;

        String setterName = PREFIX_SETTER + NabuccoTransformationUtility
                .firstToUpper(mappedType);

        Block then = (Block) ifStatement.thenStatement;
        MessageSend callSetter = (MessageSend) then.statements[0];
        javaFactory.getJavaAstMethodCall().setMethodName(setterName, callSetter);
        ((CastExpression)callSetter.arguments[0]).type = typeReference;

        IfStatement ifElse = (IfStatement) ifStatement.elseStatement;
        callSetter = (MessageSend) ((Block) ifElse.thenStatement).statements[0];
        javaFactory.getJavaAstMethodCall().setMethodName(setterName, callSetter);
    }

    private void updateForListPicker(String type, String mappedType, MethodDeclaration method)
            throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // replace "setMappedDatatype" by the real datatype

        TypeReference wildcard = producer.createWildcard();
        TypeReference innerType = producer.createTypeReference(type, false);
        TypeReference typeReference = producer.createParameterizedTypeReference(SET, false, Arrays
                .asList(wildcard));

        IfStatement ifStatement = (IfStatement) method.statements[0];
        InstanceOfExpression condition = (InstanceOfExpression) ifStatement.condition;
        condition.type = typeReference;

        typeReference = producer.createParameterizedTypeReference(SET, false, Arrays
                .asList(innerType));

        String setterName = PREFIX_SETTER + NabuccoTransformationUtility.firstToUpper(mappedType);

        Block then = (Block) ifStatement.thenStatement;
        MessageSend callSetter = (MessageSend) then.statements[0];
        javaFactory.getJavaAstMethodCall().setMethodName(setterName, callSetter);
        ((CastExpression) callSetter.arguments[0]).type = typeReference;

        IfStatement ifElse = (IfStatement) ifStatement.elseStatement;
        callSetter = (MessageSend) ((Block) ifElse.thenStatement).statements[0];
        javaFactory.getJavaAstMethodCall().setMethodName(setterName, callSetter);
        
        Annotation annotation = producer.createAnnotation(ANNOTATION_SUPPRESS_WARNINGS,
                ANNOTATION_VALUE_UNCHECKED);
        javaFactory.getJavaAstMethod().addAnnotation(method, annotation);
    }

}
