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

import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
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
 * NabuccoToJavaRcpViewBrowserElementHandlerVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewEditBrowserElementHandlerVisitor extends NabuccoToJavaVisitorSupport implements
        ViewConstants {

    String datatypePkg;

    String viewName;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewEditBrowserElementHandlerVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewEditBrowserElementHandlerVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EditViewStatement nabuccoEditView, MdaModel<JavaModel> target) {
        datatypePkg = super.getVisitorContext().getPackage();
        viewName = nabuccoEditView.nodeToken2.tokenImage;
        // Visit sub-nodes!
        super.visit(nabuccoEditView, target);
    }

    @Override
    public void visit(DatatypeDeclaration datatypeDeclaration, MdaModel<JavaModel> target) {
        // only for the leading datatype
        try {
            NabuccoAnnotation leadingAnn = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                    datatypeDeclaration.annotationDeclaration, NabuccoAnnotationType.LEADING);
            if (leadingAnn != null) {

                JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

                String datatype = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
                String name = datatype + EDIT_VIEW_BROWSER_ELEMENT_HANDLER;

                String pkg = datatypePkg.replace(UI, UI_RCP).replace(PKG_EDIT, PKG_BROWSER);

                String projectName = super.getComponentName(NabuccoClientType.RCP);

                JavaCompilationUnit unit = super
                        .extractAst(NabuccoJavaTemplateConstants.BROWSER_VIEW_ELEMENT_HANDLER_TEMPLATE);
                TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.BROWSER_VIEW_ELEMENT_HANDLER_TEMPLATE);

                javaFactory.getJavaAstType().setTypeName(type, name);
                javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

                changeMethodLoadFull(viewName, datatypePkg, datatype, unit, type);
                changeMethodCreateChildren(viewName, datatypePkg, datatype, unit, type);

                // JavaDocAnnotations
                JavaAstSupport.convertJavadocAnnotations(datatypeDeclaration.annotationDeclaration, type);

                JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                        .getImportList());

                unit.setProjectName(projectName);
                unit.setSourceFolder(super.getSourceFolder());

                target.getModel().getUnitList().add(unit);
            }
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST browser element handler modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template browser element handler processing.", te);
        }
    }

    /**
     * Changes the method loadFull.
     * 
     * @param datatypePkg
     *            the package of the datatype.
     * @param datatype
     *            the name of the datatype.
     * @param unit
     *            the java compilation unit where all imports are added to.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeMethodLoadFull(String viewName, String datatypePkg, String datatype, JavaCompilationUnit unit,
            TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        // select method loadFull(final Datatype datatype);
        JavaAstMethodSignature signature = new JavaAstMethodSignature(LOAD_FULL, DATATYPE);
        MethodDeclaration loadFull = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        TypeReference typeReference = jamp.createTypeReference(viewName + MODEL, false);

        // change returnType
        loadFull.returnType = typeReference;

        // change type of parameter
        loadFull.arguments[0].type = typeReference;

        // change name of parameter
        loadFull.arguments[0].name = NabuccoTransformationUtility.firstToLower(datatype).toCharArray();

    }

    private void changeMethodCreateChildren(String viewName, String datatypePkg, String datatype,
            JavaCompilationUnit unit, TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        // select method createChildren(final EditViewModel viewModel, final EditViewBrowserElement
        // element)
        JavaAstMethodSignature signature = new JavaAstMethodSignature(CREATE_CHILDREN, EDIT_VIEW_MODEL,
                EDIT_VIEW_BROWSER_ELEMENT);
        MethodDeclaration createChildren = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        // change type of first argument
        TypeReference model = jamp.createTypeReference(viewName + MODEL, false);
        Argument firstArgument = createChildren.arguments[0];
        firstArgument.type = model;

        // change type of first argument
        TypeReference element = jamp.createTypeReference(datatype + EDIT_VIEW_BROWSER_ELEMENT, false);
        Argument secondArgument = createChildren.arguments[1];
        secondArgument.type = element;

        // add import
        // org.nabucco.framework.common.authorization.ui.rcp.edit.group.model.AuthorizationGroupEditViewModel
        ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                datatypePkg.replace(UI, UI_RCP) + PKG_SEPARATOR + MODEL_PACKAGE + PKG_SEPARATOR + viewName + MODEL);
        javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
    }
}
