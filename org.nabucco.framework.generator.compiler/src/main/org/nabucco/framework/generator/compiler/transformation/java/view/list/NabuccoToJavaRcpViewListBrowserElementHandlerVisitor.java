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

import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
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
 * NabuccoToJavaRcpViewListBrowserElementHandlerVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewListBrowserElementHandlerVisitor extends NabuccoToJavaVisitorSupport implements
        ViewConstants {

    String datatypePkg;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewListBrowserElementHandlerVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewListBrowserElementHandlerVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ListViewStatement nabuccoListView, MdaModel<JavaModel> target) {
        datatypePkg = super.getVisitorContext().getPackage();

        // Visit sub-nodes!
        super.visit(nabuccoListView, target);
    }

    @Override
    public void visit(DatatypeDeclaration datatypeDeclaration, MdaModel<JavaModel> target) {
        try {
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            String datatype = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
            String name = datatype + LIST_VIEW_BROWSER_ELEMENT_HANDLER;

            String pkg = datatypePkg.replace(UI, UI_RCP).replace(PKG_LIST, PKG_BROWSER);

            String projectName = super.getComponentName(NabuccoClientType.RCP);
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.BROWSER_VIEW_LIST_ELEMENT_HANDLER_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.BROWSER_VIEW_LIST_ELEMENT_HANDLER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            changeMethodCreateChildren(datatypePkg, datatype, unit, type);

            changeMethodRemoveChild(datatypePkg, datatype, unit, type);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(datatypeDeclaration.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                    .getImportList());

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST browser element handler modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template browser element handler processing.", te);
        }
    }

    /**
     * @param datatypePkg2
     * @param datatype
     * @param unit
     * @param type
     * @throws JavaModelException
     */
    private void changeMethodRemoveChild(String datatypePkg2, String datatype, JavaCompilationUnit unit,
            TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        // select method createChildren(final EditViewModel viewModel, final EditViewBrowserElement
        // element)
        JavaAstMethodSignature signature = new JavaAstMethodSignature(REMOVE_CHIILD, BROWSER_ELEMENT,
                LIST_VIEW_BROWSER_ELEMENT);
        MethodDeclaration removeChild = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        // change type of first argument
        // TypeReference model = jamp.createTypeReference(datatype + LIST_VIEW_MODEL, false);
        // Argument firstArgument = removeChild.arguments[0];
        // firstArgument.type = model;

        // change type of second argument
        TypeReference element = jamp.createTypeReference(datatype + LIST_VIEW_BROWSER_ELEMENT, false);
        Argument secondArgument = removeChild.arguments[1];
        secondArgument.type = element;

    }

    private void changeMethodCreateChildren(String datatypePkg, String datatype, JavaCompilationUnit unit,
            TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        // select method createChildren(final EditViewModel viewModel, final EditViewBrowserElement
        // element)
        JavaAstMethodSignature signature = new JavaAstMethodSignature(CREATE_CHILDREN, LIST_VIEW_MODEL,
                LIST_VIEW_BROWSER_ELEMENT);
        MethodDeclaration createChildren = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        // change type of first argument
        TypeReference model = jamp.createTypeReference(datatype + LIST_VIEW_MODEL, false);
        Argument firstArgument = createChildren.arguments[0];
        firstArgument.type = model;

        // change type of second argument
        TypeReference element = jamp.createTypeReference(datatype + LIST_VIEW_BROWSER_ELEMENT, false);
        Argument secondArgument = createChildren.arguments[1];
        secondArgument.type = element;

        // add import
        // org.nabucco.framework.common.authorization.ui.rcp.edit.group.model.AuthorizationGroupEditViewModel
        ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                datatypePkg.replace(UI, UI_RCP)
                        + PKG_SEPARATOR + MODEL_PACKAGE + PKG_SEPARATOR + datatype + LIST_VIEW_MODEL);
        javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
    }
}
