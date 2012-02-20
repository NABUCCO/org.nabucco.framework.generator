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
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.browsersupport.BrowserElementSupport;
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
 * NabuccoToJavaRcpViewListBrowserElementVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
class NabuccoToJavaRcpViewListBrowserElementVisitor extends NabuccoToJavaVisitorSupport implements ViewConstants {

    String datatypePkg;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewListBrowserElementVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewListBrowserElementVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ListViewStatement nabuccoListView, MdaModel<JavaModel> target) {
        datatypePkg = super.getVisitorContext().getPackage();

        // Visit sub-nodes!
        super.visit(nabuccoListView, target);
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {
        String datatypePkg = super.getVisitorContext().getPackage();
        if (!NabuccoCompilerSupport.isBase(datatypePkg)) {
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            String datatype = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
            String name = datatype + LIST_VIEW_BROWSER_ELEMENT;
            String pkg = datatypePkg.replace(UI, UI_RCP).replace(PKG_LIST, PKG_BROWSER);

            String projectName = super.getComponentName(NabuccoClientType.RCP);

            try {
                JavaCompilationUnit unit = super
                        .extractAst(NabuccoJavaTemplateConstants.BROWSER_VIEW_LIST_ELEMENT_TEMPLATE);
                TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.BROWSER_VIEW_LIST_ELEMENT_TEMPLATE);

                javaFactory.getJavaAstType().setTypeName(type, name);
                javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

                changeGenericOfSuperclass(datatype, type);
                changeFieldHandler(datatype, type);
                changeConstructorList(name, datatype, type);
                changeConstructorArray(name, datatype, type);

                // add import of datatype
                BrowserElementSupport.addImport(super.resolveImport(datatype), unit);

                // add import of ListViewModel
                ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                        datatypePkg.replace(UI, UI_RCP)
                                + PKG_SEPARATOR + MODEL_PACKAGE + PKG_SEPARATOR + datatype + LIST_VIEW_MODEL);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);

                // JavaDocAnnotations
                JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration, type);

                JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                        .getImportList());

                unit.setProjectName(projectName);
                unit.setSourceFolder(super.getSourceFolder());

                target.getModel().getUnitList().add(unit);

            } catch (JavaModelException jme) {
                throw new NabuccoVisitorException("Error during Java AST list browser element modification.", jme);
            } catch (JavaTemplateException te) {
                throw new NabuccoVisitorException("Error during Java template list browser element processing.", te);
            }
        }
    }

    /**
     * Change generic of BrowserListElement in superclass.
     * 
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeGenericOfSuperclass(String datatype, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        TypeReference model = jamp.createTypeReference(datatype + LIST_VIEW_MODEL, false);
        List<TypeReference> parameters = new ArrayList<TypeReference>();
        parameters.add(model);
        TypeReference superclass = jamp.createParameterizedTypeReference(BROWSER_LIST_ELEMENT, false, parameters);
        type.superclass = superclass;
    }

    /**
     * Changes the local field datatype.
     * 
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeFieldHandler(String datatype, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // select field datatype
        FieldDeclaration datatypeField = javaFactory.getJavaAstType().getField(type,
                LIST_VIEW_BROWSER_ELEMENT_HANDLER_FIELD);

        TypeReference handler = jamp.createTypeReference(datatype + LIST_VIEW_BROWSER_ELEMENT_HANDLER, false);

        // change its type
        datatypeField.type = handler;
    }

    /**
     * Changes the constructor.
     * 
     * @param name
     *            the name of the constructor.
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeConstructorArray(String name, String datatype, TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        ClassLiteralAccess listViewBrowserElement = jamp.createClassLiteralAccess(datatype + LIST_VIEW_BROWSER_ELEMENT);
        ClassLiteralAccess listViewBrowserElementHandler = jamp.createClassLiteralAccess(datatype
                + LIST_VIEW_BROWSER_ELEMENT_HANDLER);
        TypeReference modelType = jamp.createTypeReference(datatype + LIST_VIEW_MODEL, false);
        AllocationExpression liestViewModel = jamp.createAllocationExpression(modelType, null);

        JavaAstMethodSignature signature = new JavaAstMethodSignature(name, DATATYPE);
        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type, signature);

        // change type of parameter
        Argument parameter = constructor.arguments[0];
        ArrayTypeReference array = new ArrayTypeReference(datatype.toCharArray(), 1, 0);
        parameter.type = array;

        // change first statement
        LocalDeclaration firstStatement = (LocalDeclaration) constructor.statements[0];
        MessageSend initialization = (MessageSend) firstStatement.initialization;
        initialization.arguments[0] = listViewBrowserElement;

        // change second statement
        Assignment secondStatement = (Assignment) constructor.statements[1];
        MessageSend expression = (MessageSend) secondStatement.expression;
        expression.arguments[0] = listViewBrowserElementHandler;

        // change third statement
        Assignment thirdStatement = (Assignment) constructor.statements[2];
        thirdStatement.expression = liestViewModel;
    }

    /**
     * Changes the constructor.
     * 
     * @param name
     *            the name of the constructor.
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private void changeConstructorList(String name, String datatype, TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        TypeReference reference = jamp.createTypeReference(datatype, false);

        JavaAstMethodSignature signature = new JavaAstMethodSignature(name, LIST);
        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type, signature);

        // change generic of parameter
        Argument parameter = constructor.arguments[0];
        ParameterizedSingleTypeReference typeReference = (ParameterizedSingleTypeReference) parameter.type;
        typeReference.typeArguments[0] = reference;

        // change constructorCall
        ExplicitConstructorCall constructorCall = constructor.constructorCall;
        MessageSend argument = (MessageSend) constructorCall.arguments[0];
        ArrayAllocationExpression allocation = (ArrayAllocationExpression) argument.arguments[0];
        allocation.type = reference;
    }

}
