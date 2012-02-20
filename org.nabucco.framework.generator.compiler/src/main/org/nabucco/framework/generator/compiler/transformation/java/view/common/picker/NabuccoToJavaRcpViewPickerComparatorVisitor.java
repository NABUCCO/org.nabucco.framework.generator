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
package org.nabucco.framework.generator.compiler.transformation.java.view.common.picker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
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
public class NabuccoToJavaRcpViewPickerComparatorVisitor extends NabuccoToJavaVisitorSupport implements ViewConstants {

    /**
     * the annotation declaration of the view.
     */
    private AnnotationDeclaration viewAnnotationDeclaration;

    /**
     * Mapping from field name to field type properties of all datatypes.
     */
    private Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> fieldNameToFieldTypeProperties;

    /**
     * Constructor to create a new instance of NabuccoToJavaRcpViewPickerComparatorVisitor.
     * 
     * @param visitorContext
     *            the context of the visitor.
     * @param annotationDeclaration
     *            the annotationDeclaration of the view from which the constructor is called.
     * @param fieldNameToFieldTypeProperties
     */
    public NabuccoToJavaRcpViewPickerComparatorVisitor(NabuccoToJavaVisitorContext visitorContext,
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

        this.createPickerComparator(name, annotations, target);
    }

    @Override
    public void visit(LabeledPickerDeclaration picker, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotations = picker.annotationDeclaration;
        String name = picker.nodeToken2.tokenImage;

        this.createPickerComparator(name, annotations, target);
    }

    @Override
    public void visit(ListPickerDeclaration picker, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotations = picker.annotationDeclaration;
        String name = picker.nodeToken2.tokenImage;

        this.createPickerComparator(name, annotations, target);
    }

    @Override
    public void visit(LabeledListPickerDeclaration picker, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotations = picker.annotationDeclaration;
        String name = picker.nodeToken2.tokenImage;

        this.createPickerComparator(name, annotations, target);
    }

    /**
     * Creates the picker comparator for the given picker attributes.
     * 
     * @param name
     *            name of the picker
     * @param annotations
     *            picker annotations
     * @param target
     *            the java target model
     */
    private void createPickerComparator(String name, AnnotationDeclaration annotations, MdaModel<JavaModel> target) {
        name = NabuccoTransformationUtility.firstToUpper(name) + ViewConstants.COMPARATOR;

        try {
            NabuccoAnnotation mappedFieldAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations,
                    NabuccoAnnotationType.MAPPED_FIELD);

            if (mappedFieldAnn != null
                    && mappedFieldAnn.getValue() != null
                    && mappedFieldAnn.getValue().split(FIELD_SEPARATOR).length == 3) {

                JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
                JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

                String pkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP)
                        + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE + ViewConstants.PKG_SEPARATOR
                        + ViewConstants.COMPARATOR_PACKAGE;

                String projectName = super.getComponentName(NabuccoClientType.RCP);

                JavaCompilationUnit unit = super
                        .extractAst(NabuccoJavaTemplateConstants.EDIT_VIEW_PICKER_COMPARATOR_TEMPLATE);
                TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.EDIT_VIEW_PICKER_COMPARATOR_TEMPLATE);

                javaFactory.getJavaAstType().setTypeName(type, name);
                javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

                // JavaDocAnnotations
                JavaAstSupport.convertJavadocAnnotations(annotations, type);

                JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                        .getImportList());

                String mappedDatatype = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[0];
                String mappedType = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[1];
                String mappedProperty = mappedFieldAnn.getValue().split(FIELD_SEPARATOR)[2];

                Map<String, Map<String, JavaAstContainter<TypeReference>>> datatypeMap = this.fieldNameToFieldTypeProperties
                        .get(mappedDatatype);
                Map<String, JavaAstContainter<TypeReference>> datatypeSubMap = datatypeMap.get(DATATYPE);

                JavaAstContainter<TypeReference> javacontainer = datatypeSubMap.get(mappedType);
                TypeReference ref = javacontainer.getAstNode();
                String newDatatype = ref.toString();

                Set<String> imports = javacontainer.getImports();
                // Add the imports
                for (String current : imports) {
                    String importString = current;
                    ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                            importString);
                    javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

                }

                TypeReference newDatatypeReference = jamp.createTypeReference(newDatatype, false);

                // change generic of superclass
                List<TypeReference> interfaces = javaFactory.getJavaAstType().getInterfaces(type);
                ((ParameterizedSingleTypeReference) interfaces.get(0)).typeArguments[0] = newDatatypeReference;

                // select the method elementSelected(TypedEvent aTypedEvent)
                JavaAstMethodSignature signature = new JavaAstMethodSignature(ViewConstants.COMPARE,
                        ViewConstants.DATATYPE, ViewConstants.DATATYPE);

                MethodDeclaration compare = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

                Argument arg = jamp.createArgument(ARGUMENT_FIRST, newDatatypeReference);
                Argument[] arguments = compare.arguments;
                arguments[0] = arg;
                arg = jamp.createArgument(ARGUMENT_SECOND, newDatatypeReference);
                arguments[1] = arg;
                // change type of arguments
                compare.arguments = arguments;

                SingleNameReference secondReference = jamp.createSingleNameReference(ARGUMENT_SECOND);
                MessageSend secondMS = jamp.createMessageSend(
                        GET + NabuccoTransformationUtility.firstToUpper(mappedProperty), secondReference, null);
                List<Expression> argumentList = new ArrayList<Expression>();
                argumentList.add(secondMS);
                SingleNameReference firstReference = jamp.createSingleNameReference(ARGUMENT_FIRST);
                MessageSend firstMS = jamp.createMessageSend(
                        GET + NabuccoTransformationUtility.firstToUpper(mappedProperty), firstReference, null);
                MessageSend expression = jamp.createMessageSend(COMPARE_TO, firstMS, argumentList);
                ReturnStatement returnStatement = jamp.createReturnStatement(expression);

                compare.statements[0] = returnStatement;

                // Annotations
                JavaAstSupport.convertJavadocAnnotations(viewAnnotationDeclaration, type);

                unit.setProjectName(projectName);
                unit.setSourceFolder(super.getSourceFolder());

                target.getModel().getUnitList().add(unit);

            }
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST editview picker content provider modification.",
                    jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template editview picker content provider processing.", te);
        }
    }
}
