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
package org.nabucco.framework.generator.compiler.transformation.java.view.list;

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
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.ColumnDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ListViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

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
 * @author Stefanie Feld, Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaRcpViewListLabelProviderVisitor extends NabuccoToJavaVisitorSupport {

    String className;

    DatatypeDeclaration datatype;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewListLabelProviderVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaRcpViewListLabelProviderVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ListViewStatement nabuccoListView, MdaModel<JavaModel> target) {

        this.className = nabuccoListView.nodeToken2.tokenImage;

        // Visit sub-nodes last!
        super.visit(nabuccoListView, target);
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {

        this.datatype = nabuccoDatatype;

        // Visit sub-nodes last!
        super.visit(nabuccoDatatype, target);
    }

    @Override
    public void visit(ColumnDeclaration column, MdaModel<JavaModel> target) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        String name = className
                + NabuccoTransformationUtility.firstToUpper(column.nodeToken2.tokenImage)
                + NabuccoJavaTemplateConstants.LABEL_PROVIDER;

        String mainPath = super.getVisitorContext().getPackage()
                .replace(ViewConstants.UI, ViewConstants.UI_RCP);

        String pkg = mainPath
                + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE
                + ViewConstants.PKG_SEPARATOR + ViewConstants.LABEL_PACKAGE;

        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.LIST_VIEW_LABEL_PROVIDER_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.LIST_VIEW_LABEL_PROVIDER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            target.getModel().getUnitList().add(unit);

            // select the method "getText()"

            JavaAstMethodSignature signature = new JavaAstMethodSignature(ViewConstants.GET_TEXT,
                    ViewConstants.OBJECT);

            MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                    type, signature);

            // change in getText() the "Datatype"

            String datatypeName = datatype.nodeToken2.tokenImage;
            String datatypeType = ((NodeToken) datatype.nodeChoice1.choice).tokenImage;

            SingleNameReference datatypeNameRef = producer.createSingleNameReference(datatypeName);
            TypeReference datatypeTypeRef = producer.createTypeReference(datatypeType, false);

            String importString = super.resolveImport(datatypeType);
            ImportReference importReference = producer.createImportReference(importString);

            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

            IfStatement ifStatement = (IfStatement) (method.statements[1]);
            InstanceOfExpression condition = (InstanceOfExpression) ifStatement.condition;
            Block then = (Block) ifStatement.thenStatement;

            condition.type = datatypeTypeRef;

            LocalDeclaration localDeclaration = (LocalDeclaration) then.statements[0];
            CastExpression initialization = (CastExpression) localDeclaration.initialization;
            localDeclaration.name = datatypeName.toCharArray();
            localDeclaration.type = datatypeTypeRef;
            initialization.type = datatypeTypeRef;

            // change in getText() the "dt"

            Assignment assignment = (Assignment) then.statements[1];
            ConditionalExpression ternary = (ConditionalExpression) assignment.expression;
            EqualExpression equalExpression = (EqualExpression) ternary.condition;

            MessageSend left = (MessageSend) equalExpression.left;
            javaFactory.getJavaAstMethodCall().setMethodReceiver(datatypeNameRef, left);

            MessageSend ifTrue = (MessageSend) ((MessageSend) ternary.valueIfTrue).receiver;
            javaFactory.getJavaAstMethodCall().setMethodReceiver(datatypeNameRef, ifTrue);

            // change in getText() the "getAttribute"

            String currentMappedField = ViewConstants.EMPTY_STRING;
            currentMappedField = NabuccoAnnotationMapper
                    .getInstance().mapToAnnotation(column.annotationDeclaration,
                            NabuccoAnnotationType.MAPPED_FIELD).getValue();

            String getAttribute = currentMappedField.split(ViewConstants.FIELD_SEPARATOR)[1];
            getAttribute = ViewConstants.GET
                    + NabuccoTransformationUtility.firstToUpper(getAttribute);

            left.selector = getAttribute.toCharArray();
            ifTrue.selector = getAttribute.toCharArray();

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(column.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(),
                    super.getVisitorContext().getImportList());

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException(
                    "Error during Java AST listviewlabelprovider modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error during Java template listviewlabelprovider processing.", te);
        }

        super.visit(column, target);
    }

}
