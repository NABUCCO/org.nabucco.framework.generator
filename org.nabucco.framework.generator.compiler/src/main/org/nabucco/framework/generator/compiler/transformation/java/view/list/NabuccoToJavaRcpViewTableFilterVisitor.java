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
package org.nabucco.framework.generator.compiler.transformation.java.view.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
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
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewTableFilterVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewTableFilterVisitor extends NabuccoToJavaVisitorSupport {

    private DatatypeDeclaration datatype;

    private List<String> mappedFieldList = new ArrayList<String>();

    public NabuccoToJavaRcpViewTableFilterVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ListViewStatement nabuccoListView, MdaModel<JavaModel> target) {
        // Visit sub-nodes!
        super.visit(nabuccoListView, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoListView.nodeToken2.tokenImage
                + NabuccoJavaTemplateConstants.TABLE + NabuccoJavaTemplateConstants.FILTER;
        String mainPath = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP);
        String pkg = mainPath + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE;

        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.LIST_VIEW_TABLE_FILTER_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.LIST_VIEW_TABLE_FILTER_TEMPLATE);

            // import of the datatype
            String datatypeType = ((NodeToken) datatype.nodeChoice1.choice).tokenImage;
            String importString = super.resolveImport(datatypeType);
            ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(importString);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

            // change Method select(...)
            JavaAstMethodSignature signature = new JavaAstMethodSignature(ViewConstants.SELECT, ViewConstants.VIEWER,
                    ViewConstants.OBJECT, ViewConstants.OBJECT);
            MethodDeclaration selectMethod = (MethodDeclaration) javaFactory.getJavaAstType()
                    .getMethod(type, signature);

            // im else if replace Datatype by the real datatype
            IfStatement ifStatement = (IfStatement) selectMethod.statements[1];
            IfStatement elseStatement = (IfStatement) ifStatement.elseStatement;
            Block then = (Block) elseStatement.thenStatement;
            InstanceOfExpression condition = (InstanceOfExpression) elseStatement.condition;

            TypeReference datatypeTypeRef = JavaAstModelProducer.getInstance().createTypeReference(datatypeType, false);
            condition.type = datatypeTypeRef;

            LocalDeclaration localDeclaration = (LocalDeclaration) then.statements[0];
            ((CastExpression) localDeclaration.initialization).type = datatypeTypeRef;
            localDeclaration.type = datatypeTypeRef;

            // insert a statement for each column
            Block thenStatement = then;
            int newSize = mappedFieldList.size() + thenStatement.statements.length;
            Statement[] newArray = Arrays.copyOf(thenStatement.statements, newSize);
            int position = thenStatement.statements.length;
            JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
            for (String mappedField : mappedFieldList) {
                String datatype = ViewConstants.DATATYPE.toLowerCase();
                String property = mappedField.split(ViewConstants.FIELD_SEPARATOR)[1];
                String getProperty = ViewConstants.GET + NabuccoTransformationUtility.firstToUpper(property);
                SingleNameReference leftOfAssignment = jamp.createSingleNameReference(ViewConstants.RESULT);
                SingleNameReference leftOfOrOrExpression = jamp.createSingleNameReference(ViewConstants.RESULT);

                ThisReference receiver = jamp.createThisReference();
                List<Expression> arguments = new ArrayList<Expression>();
                SingleNameReference receiverOfFirstArgument = jamp.createSingleNameReference(datatype);
                MessageSend firstArgument = jamp.createMessageSend(getProperty, receiverOfFirstArgument, null);
                SingleNameReference receiverOfSecondArgument = jamp
                        .createSingleNameReference(ViewConstants.SEARCH_FILTER);
                MessageSend secondArgument = jamp.createMessageSend(ViewConstants.GET_FILTER, receiverOfSecondArgument,
                        null);
                arguments.add(firstArgument);
                arguments.add(secondArgument);
                MessageSend rightOfOrOrExpression = jamp.createMessageSend(ViewConstants.CONTAINS, receiver, arguments);
                OR_OR_Expression rightOfAssignment = (OR_OR_Expression) jamp.createBinaryExpression(
                        BinaryExpressionType.OR_OR_EXPRESSION, leftOfOrOrExpression, rightOfOrOrExpression,
                        OR_OR_Expression.OR_OR);
                Statement newAssignment = jamp.createAssignment(leftOfAssignment, rightOfAssignment);
                newArray[position] = newAssignment;
                position++;
            }
            thenStatement.statements = newArray;

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            target.getModel().getUnitList().add(unit);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoListView.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(), super
                    .getVisitorContext().getImportList());

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST listviewlabelprovider modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template listviewlabelprovider processing.", te);
        }
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {
        datatype = nabuccoDatatype;
        super.visit(nabuccoDatatype, target);
    }

    @Override
    public void visit(ColumnDeclaration column, MdaModel<JavaModel> target) {
        // save all mappedFields in a map
        String currentMappedField = NabuccoAnnotationMapper.getInstance()
                .mapToAnnotation(column.annotationDeclaration, NabuccoAnnotationType.MAPPED_FIELD).getValue();
        mappedFieldList.add(currentMappedField);
        super.visit(column, target);
    }

}
