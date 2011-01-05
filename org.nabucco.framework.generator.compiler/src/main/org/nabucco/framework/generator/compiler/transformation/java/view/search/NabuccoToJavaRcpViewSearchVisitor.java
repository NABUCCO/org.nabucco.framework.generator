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

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewVisitorSupportUtil;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.WidgetDeclaration;
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
 * NabuccoToJavaRcpViewSearchVistitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
class NabuccoToJavaRcpViewSearchVisitor extends NabuccoToJavaVisitorSupport {

    String name;

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaRcpViewSearchVisitor.class);

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewSearchVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(SearchViewStatement searchViewStatement, MdaModel<JavaModel> target) {
        String projectName = super.getComponentName(NabuccoClientType.RCP);
        name = searchViewStatement.nodeToken2.tokenImage;
        String pkg = super.getVisitorContext().getPackage()
                .replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR
                + ViewConstants.VIEW_PACKAGE;

        String modelPackage = super.getVisitorContext().getPackage()
                .replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR
                + ViewConstants.MODEL_PACKAGE;
        String modelType = name.replace(NabuccoJavaTemplateConstants.VIEW,
                NabuccoJavaTemplateConstants.VIEW + NabuccoJavaTemplateConstants.MODEL);
        String modelTypeFQN = modelPackage + ViewConstants.PKG_SEPARATOR + modelType;

        try {

            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.SEARCH_VIEW_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SEARCH_VIEW_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            TypeReference modelTypeRef = JavaAstModelProducer.getInstance().createTypeReference(
                    modelType, false);

            handleViewParameter(type, modelTypeRef);

            handleModelFieldType(type, modelTypeRef);

            handleGetterMethod(type, modelTypeRef);

            handleConstructor(type, modelTypeRef);

            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(),
                    JavaAstModelProducer.getInstance().createImportReference(modelTypeFQN));

            // Adding common ui elements
            JavaCompilationUnit uIUnit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            TypeDeclaration uIType = uIUnit
                    .getType(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewVisitorSupportUtil.getUiCommonElements(uIType,
                            type, searchViewStatement.annotationDeclaration));

            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(),
                    getVisitorContext().getImportList());

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(searchViewStatement.annotationDeclaration,
                    type);

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST datatype modification.");
            throw new NabuccoVisitorException("Error during Java AST searchview modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template datatype processing.");
            throw new NabuccoVisitorException("Error during Java template searchview processing.",
                    te);
        }
        super.visit(searchViewStatement, target);
    }

    /**
     * @param type
     * @param modelTypeRef
     * @throws JavaModelException
     */
    private void handleViewParameter(TypeDeclaration type, TypeReference modelTypeRef)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        TypeReference superClass = javaFactory.getJavaAstType().getSuperClass(type);
        superClass = javaFactory.getJavaAstReference().getAsParameterized(superClass,
                new TypeReference[] { modelTypeRef });
        javaFactory.getJavaAstType().setSuperClass(type, superClass);
    }

    /**
     * @param type
     * @throws JavaModelException
     */
    private void handleConstructor(TypeDeclaration type, TypeReference modelTypeRef)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        AbstractMethodDeclaration constructor = javaFactory.getJavaAstType().getConstructors(type)
                .get(0);
        ((AllocationExpression) ((Assignment) constructor.statements[0]).expression).type = modelTypeRef;

    }

    /**
     * @param type
     * @param modelTypeRef
     * @throws JavaModelException
     */
    private void handleGetterMethod(TypeDeclaration type, TypeReference modelTypeRef)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                new JavaAstMethodSignature(ViewConstants.GET_MODEL, new String[] {}));
        javaFactory.getJavaAstMethod().setReturnType(method, modelTypeRef);

    }

    /**
     * @param type
     * @param modelTypeRef
     * @throws JavaModelException
     */
    private void handleModelFieldType(TypeDeclaration type, TypeReference modelTypeRef)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration field = javaFactory.getJavaAstType().getField(type,
                ViewConstants.MODEL_FIELD);
        javaFactory.getJavaAstField().setFieldType(field, modelTypeRef);

    }

    @Override
    public void visit(WidgetDeclaration nabuccoWidgetDeclartion, MdaModel<JavaModel> target) {
        super.visit(nabuccoWidgetDeclartion, target);
    }

}
