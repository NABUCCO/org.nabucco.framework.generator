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
package org.nabucco.framework.generator.compiler.transformation.java.view.edit;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
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
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
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
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaRcpViewEditVisitor extends NabuccoToJavaVisitorSupport {

    /**
     * Creates a new {@link NabuccoToJavaRcpViewEditVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewEditVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EditViewStatement nabuccoEditView, MdaModel<JavaModel> target) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoEditView.nodeToken2.tokenImage;
        String modelName = nabuccoEditView.nodeToken2.tokenImage.replace(NabuccoJavaTemplateConstants.VIEW,
                NabuccoJavaTemplateConstants.VIEW + NabuccoJavaTemplateConstants.MODEL);
        String pkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE;
        String modelPkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR + ViewConstants.MODEL_PACKAGE;

        String projectName = super.getComponentName(NabuccoClientType.RCP);
        try {

            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.EDIT_VIEW_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.EDIT_VIEW_TEMPLATE);

            // create type reference for view model
            TypeReference modelTypeReference = JavaAstModelProducer.getInstance().createTypeReference(modelName, false);

            javaFactory.getJavaAstType().setTypeName(type, name);

            TypeReference superClass = javaFactory.getJavaAstType().getSuperClass(type);

            superClass = javaFactory.getJavaAstReference().getAsParameterized(superClass,
                    new TypeReference[] { modelTypeReference });

            javaFactory.getJavaAstType().setSuperClass(type, superClass);

            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // common ui element handling
            JavaCompilationUnit uIUnit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            TypeDeclaration uIType = uIUnit.getType(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            getVisitorContext().getContainerList().addAll(
                    NabuccoToJavaRcpViewVisitorSupportUtil.getUiCommonElements(uIType, type,
                            nabuccoEditView.annotationDeclaration));

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoEditView.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                    .getImportList());

            // swap order of public static final fields ID and TITLE
            NabuccoToJavaRcpViewVisitorSupportUtil.swapFieldOrder(type);

            // add the model import
            ImportReference modelImportRef = JavaAstModelProducer.getInstance().createImportReference(
                    modelPkg + ViewConstants.PKG_SEPARATOR + modelName);
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), modelImportRef);

            // handle createFormControl method
            handleCreateFormControl(type, modelName);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoEditView.annotationDeclaration, type);

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST editview modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template editview processing.", te);
        }
    }

    /**
     * Selects the method createFormControl() and changes the modelType.
     * 
     * @param type
     *            the type declaration.
     * @param modelName
     *            the name of the type of the model.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void handleCreateFormControl(TypeDeclaration type, String modelName) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                new JavaAstMethodSignature(ViewConstants.CREATE_FORM_CONTROL, new String[] { ViewConstants.FORM }));
        TypeReference modelType = JavaAstModelProducer.getInstance().createTypeReference(modelName, false);
        ((AllocationExpression) ((Assignment) method.statements[2]).expression).type = modelType;
    }

}
