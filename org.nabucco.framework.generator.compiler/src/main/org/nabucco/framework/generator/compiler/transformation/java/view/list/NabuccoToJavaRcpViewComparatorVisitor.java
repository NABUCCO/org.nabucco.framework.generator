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

import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
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
 * NabuccoToJavaRcpViewComparatorVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewComparatorVisitor extends NabuccoToJavaVisitorSupport {

    DatatypeDeclaration datatype;

    String className;

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewComparatorVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ListViewStatement nabuccoListView, MdaModel<JavaModel> target) {
        className = nabuccoListView.nodeToken2.tokenImage;
        // Visit sub-nodes!
        super.visit(nabuccoListView, target);
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {
        datatype = nabuccoDatatype;
        super.visit(nabuccoDatatype, target);
    }

    @Override
    public void visit(ColumnDeclaration column, MdaModel<JavaModel> target) {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = className
                + NabuccoTransformationUtility.firstToUpper(column.nodeToken2.tokenImage)
                + NabuccoJavaTemplateConstants.COMPARATOR;
        String mainPath = super.getVisitorContext().getPackage()
                .replace(ViewConstants.UI, ViewConstants.UI_RCP);
        String pkg = mainPath
                + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE
                + ViewConstants.PKG_SEPARATOR + ViewConstants.COMPARATOR_PACKAGE;

        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.LIST_VIEW_COMPARATOR_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.LIST_VIEW_COMPARATOR_TEMPLATE);

            // import of the datatype
            String datatypeType = ((NodeToken) datatype.nodeChoice1.choice).tokenImage;
            String importString = super.resolveImport(datatypeType);
            ImportReference importReference = JavaAstModelProducer.getInstance()
                    .createImportReference(importString);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

            String currentMappedField = NabuccoAnnotationMapper
                    .getInstance()
                    .mapToAnnotation(column.annotationDeclaration,
                            NabuccoAnnotationType.MAPPED_FIELD).getValue();

            String getProperty = currentMappedField.split(ViewConstants.FIELD_SEPARATOR)[1];
            getProperty = ViewConstants.GET
                    + NabuccoTransformationUtility.firstToUpper(getProperty);

            // change method compare()
            JavaAstMethodSignature signature = new JavaAstMethodSignature(ViewConstants.COMPARE
                    + ViewConstants.CONCRETE, ViewConstants.DATATYPE, ViewConstants.DATATYPE);

            MethodDeclaration compareMethod = (MethodDeclaration) javaFactory.getJavaAstType()
                    .getMethod(type, signature);

            ((SingleTypeReference) compareMethod.arguments[0].type).token = datatypeType
                    .toCharArray();
            ((SingleTypeReference) compareMethod.arguments[1].type).token = datatypeType
                    .toCharArray();

            ReturnStatement returnStatement = (ReturnStatement) compareMethod.statements[0];

            MessageSend methodCall = (MessageSend) returnStatement.expression;

            MessageSend firstArgument = (MessageSend) methodCall.arguments[0];
            MessageSend secondArgument = (MessageSend) methodCall.arguments[1];

            firstArgument.selector = getProperty.toCharArray();
            secondArgument.selector = getProperty.toCharArray();

            // ((MessageSend) ((MessageSend) (returnStatement).expression).receiver).selector =
            // getProperty
            // .toCharArray();
            // ((MessageSend) ((MessageSend) (returnStatement).expression).arguments[0]).selector =
            // getProperty
            // .toCharArray();

            // change the generic datatype Comparator<Datatype>
            ParameterizedSingleTypeReference superclass = (ParameterizedSingleTypeReference) type.superclass;
            superclass.typeArguments[0] = JavaAstModelProducer.getInstance().createTypeReference(
                    datatypeType, false);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            target.getModel().getUnitList().add(unit);

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
