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

import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
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
import org.nabucco.framework.generator.parser.syntaxtree.LabeledListPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ListPickerDeclaration;
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
public class NabuccoToJavaRcpViewPickerLabelProviderVisitor extends NabuccoToJavaVisitorSupport
        implements ViewConstants {

    /**
     * the annotation declaration of the view.
     */
    private AnnotationDeclaration viewAnnotationDeclaration;

    /**
     * Mapping from field name to field type properties of all datatypes.
     */
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
    public NabuccoToJavaRcpViewPickerLabelProviderVisitor(
            NabuccoToJavaVisitorContext visitorContext,
            AnnotationDeclaration annotationDeclaration,
            Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> fieldNameToFieldTypeProperties) {
        super(visitorContext);
        this.viewAnnotationDeclaration = annotationDeclaration;
        this.fieldNameToFieldTypeProperties = fieldNameToFieldTypeProperties;
    }

    @Override
    public void visit(PickerDeclaration picker, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotations = picker.annotationDeclaration;
        String name = picker.nodeToken2.tokenImage;

        this.createPickerLabelProvider(name, annotations,target);
    }

    @Override
    public void visit(LabeledPickerDeclaration picker, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotations = picker.annotationDeclaration;
        String name = picker.nodeToken2.tokenImage;

        this.createPickerLabelProvider(name, annotations,target);
    }

    @Override
    public void visit(ListPickerDeclaration picker, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotations = picker.annotationDeclaration;
        String name = picker.nodeToken2.tokenImage;

        this.createPickerLabelProvider(name, annotations,target);
    }

    @Override
    public void visit(LabeledListPickerDeclaration picker, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotations = picker.annotationDeclaration;
        String name = picker.nodeToken2.tokenImage;

        this.createPickerLabelProvider(name, annotations,target);
    }

    /**
     * Creates the picker label provider for the given picker attributes.
     * 
     * @param name
     *            the picker name
     * @param annotations
     *            the pickers annotations
     * @param target
     *            the java target model
     */
    private void createPickerLabelProvider(String name, AnnotationDeclaration annotations,
            MdaModel<JavaModel> target) {
        
        name = NabuccoTransformationUtility.firstToUpper(name) + ViewConstants.LABEL_PROVIDER;

        try {
            NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotation(annotations, NabuccoAnnotationType.MAPPED_FIELD);

            if (mappedFieldAnn != null
                    && mappedFieldAnn.getValue() != null
                    && mappedFieldAnn.getValue().split(FIELD_SEPARATOR).length == 3) {

                JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
                JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
                String pkg = super.getVisitorContext().getPackage()
                        .replace(ViewConstants.UI, ViewConstants.UI_RCP)
                        + ViewConstants.PKG_SEPARATOR
                        + ViewConstants.VIEW_PACKAGE
                        + ViewConstants.PKG_SEPARATOR + ViewConstants.LABEL_PACKAGE;

                String projectName = super.getComponentName(NabuccoClientType.RCP);

                JavaCompilationUnit unit = super
                        .extractAst(NabuccoJavaTemplateConstants.LIST_VIEW_LABEL_PROVIDER_TEMPLATE);
                TypeDeclaration type = unit
                        .getType(NabuccoJavaTemplateConstants.LIST_VIEW_LABEL_PROVIDER_TEMPLATE);

                javaFactory.getJavaAstType().setTypeName(type, name);
                javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

                // JavaDocAnnotations
                JavaAstSupport.convertJavadocAnnotations(annotations, type);

                JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(),
                        getVisitorContext().getImportList());

                String mappedDatatype = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[0];
                String mappedType = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[1];
                String mappedProperty = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[2];

                Map<String, Map<String, JavaAstContainter<TypeReference>>> datatypeMap = this.fieldNameToFieldTypeProperties
                        .get(mappedDatatype);
                Map<String, JavaAstContainter<TypeReference>> datatypeSubMap = datatypeMap
                        .get(DATATYPE);

                JavaAstContainter<TypeReference> javacontainer = datatypeSubMap.get(mappedType);
                TypeReference ref = javacontainer.getAstNode();
                String newDatatype = ref.toString();

                Set<String> imports = javacontainer.getImports();
                // Add the imports
                for (String current : imports) {
                    String importString = current;
                    ImportReference importReference = JavaAstModelProducer.getInstance()
                            .createImportReference(importString);
                    javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(),
                            importReference);

                }

                TypeReference newDatatypeReference = jamp.createTypeReference(newDatatype, false);
                SingleNameReference datatypeReference = jamp.createSingleNameReference("dt");
                MessageSend dtGetAttribute = jamp.createMessageSend(GET
                        + NabuccoTransformationUtility.firstToUpper(mappedProperty),
                        datatypeReference, null);
                MessageSend dtGetAttributeToString = jamp.createMessageSend(GET_VALUE,
                        dtGetAttribute, null);

                // select the method getText
                JavaAstMethodSignature signature = new JavaAstMethodSignature(
                        ViewConstants.GET_TEXT, ViewConstants.OBJECT);

                MethodDeclaration getText = (MethodDeclaration) javaFactory.getJavaAstType()
                        .getMethod(type, signature);

                // change first statement
                IfStatement ifStatement = (IfStatement) getText.statements[1];

                // change condition
                InstanceOfExpression condition = (InstanceOfExpression) ifStatement.condition;
                condition.type = newDatatypeReference;

                // change thenStatement
                Block thenStatement = (Block) ifStatement.thenStatement;

                // change first statement
                LocalDeclaration firstStatement = (LocalDeclaration) thenStatement.statements[0];
                firstStatement.type = newDatatypeReference;
                CastExpression initialization = (CastExpression) firstStatement.initialization;
                initialization.type = newDatatypeReference;

                // change second statement
                Assignment secondStatement = (Assignment) thenStatement.statements[1];
                ConditionalExpression expression = (ConditionalExpression) secondStatement.expression;
                EqualExpression equalExpression = (EqualExpression) expression.condition;
                equalExpression.left = dtGetAttribute;

                expression.valueIfTrue = dtGetAttributeToString;

                // Annotations
                JavaAstSupport.convertJavadocAnnotations(viewAnnotationDeclaration, type);

                unit.setProjectName(projectName);
                unit.setSourceFolder(super.getSourceFolder());

                target.getModel().getUnitList().add(unit);

            }
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException(
                    "Error during Java AST editview picker content provider modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template editview picker content provider processing.", te);
        }
    }

}
