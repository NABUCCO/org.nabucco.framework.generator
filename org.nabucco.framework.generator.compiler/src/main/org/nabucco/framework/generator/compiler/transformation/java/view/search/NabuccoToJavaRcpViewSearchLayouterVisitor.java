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
package org.nabucco.framework.generator.compiler.transformation.java.view.search;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewVisitorSupportUtil;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaSearchViewLayouterVisitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
class NabuccoToJavaRcpViewSearchLayouterVisitor extends NabuccoToJavaVisitorSupport {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaRcpViewSearchLayouterVisitor.class);

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewSearchLayouterVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(SearchViewStatement searchViewStatement, MdaModel<JavaModel> target) {
        super.visit(searchViewStatement, target);

        String pkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE;
        String name = searchViewStatement.nodeToken2.tokenImage.replace(NabuccoJavaTemplateConstants.VIEW,
                ViewConstants.LAYOUTER);
        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SEARCH_VIEW_LAYOUTER_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SEARCH_VIEW_LAYOUTER_TEMPLATE);
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            handleWidgetFactoryField(type, name);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), layoutMethod(type, searchViewStatement));

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(searchViewStatement.annotationDeclaration, type);

            unit.setProjectName(projectName);

            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST datatype modification.");
            throw new NabuccoVisitorException("Error during Java AST search view layouter modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template datatype processing.");
            throw new NabuccoVisitorException("Error during Java template search view layouter processing.", te);
        }
    }

    /**
     * @param type
     * @param name
     * @throws JavaModelException
     */
    private ImportReference layoutMethod(TypeDeclaration type, SearchViewStatement searchViewStatement)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        // get method
        MethodDeclaration layoutMethod = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                type,
                new JavaAstMethodSignature(ViewConstants.LAYOUT, new String[] { ViewConstants.COMPOSITE,
                        ViewConstants.NABUCCO_MESSAGE_MANAGER, ViewConstants.TEMPLATE_SEARCH_MODEL }));
        // change parameter 3
        String name = searchViewStatement.nodeToken2.tokenImage.replace(NabuccoJavaTemplateConstants.VIEW,
                ViewConstants.LAYOUTER);
        layoutMethod.arguments[2].type = JavaAstModelProducer.getInstance().createTypeReference(
                name.replace(ViewConstants.LAYOUTER, NabuccoJavaTemplateConstants.MODEL), true);
        TypeReference widgetFactoryType = JavaAstModelProducer.getInstance().createTypeReference(
                name.replace(ViewConstants.LAYOUTER, NabuccoJavaTemplateConstants.VIEW
                        + NabuccoJavaTemplateConstants.WIDGET_FACTORY), false);
        ((AllocationExpression) ((Assignment) layoutMethod.statements[1]).expression).type = widgetFactoryType;

        return NabuccoToJavaRcpViewVisitorSupportUtil.getModelTypeImport(getVisitorContext(), searchViewStatement);

    }

    /**
     * @param type
     * @param name
     * @throws JavaModelException
     */
    private void handleWidgetFactoryField(TypeDeclaration type, String name) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration widgetFactoryField = javaFactory.getJavaAstType().getField(type, ViewConstants.WIDGET_FACTORY);
        TypeReference fieldTypeRef = JavaAstModelProducer.getInstance().createTypeReference(
                name.replace(ViewConstants.LAYOUTER, NabuccoJavaTemplateConstants.VIEW
                        + NabuccoJavaTemplateConstants.WIDGET_FACTORY), false);
        javaFactory.getJavaAstField().setFieldType(widgetFactoryField, fieldTypeRef);
    }

}
