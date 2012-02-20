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

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
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
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewModelListVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewModelListVisitor extends NabuccoToJavaVisitorSupport implements ViewConstants {

    String unqualifiedType;

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewModelListVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ListViewStatement listViewStatement, MdaModel<JavaModel> target) {
        // children first, we need the datatypes
        super.visit(listViewStatement, target);

        String pkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR + ViewConstants.MODEL_PACKAGE;
        String name = listViewStatement.nodeToken2.tokenImage.replace(NabuccoJavaTemplateConstants.VIEW,
                NabuccoJavaTemplateConstants.VIEW + NabuccoJavaTemplateConstants.MODEL);
        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.LIST_VIEW_MODEL_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.LIST_VIEW_MODEL_TEMPLATE);
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // change generic of superclass
            JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
            TypeReference model = jamp.createTypeReference(this.unqualifiedType, false);
            List<TypeReference> parameters = new ArrayList<TypeReference>();
            parameters.add(model);
            TypeReference superclass = jamp.createParameterizedTypeReference(LIST_VIEW_MODEL, false, parameters);
            type.superclass = superclass;

            // add import of datatype
            BrowserElementSupport.addImport(super.resolveImport(this.unqualifiedType), unit);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(listViewStatement.annotationDeclaration, type);

            // append AST nodes
            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(), super
                    .getVisitorContext().getImportList());

            unit.setProjectName(projectName);

            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST edit view modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template edit view processing.", te);
        }
    }

    @Override
    public void visit(DatatypeDeclaration datatypeDeclaration, MdaModel<JavaModel> target) {
        unqualifiedType = ((NodeToken) datatypeDeclaration.nodeChoice1.choice).tokenImage;
    }

}
