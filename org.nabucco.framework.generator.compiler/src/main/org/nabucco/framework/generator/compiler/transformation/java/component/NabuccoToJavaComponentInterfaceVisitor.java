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
package org.nabucco.framework.generator.compiler.transformation.java.component;

import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaComponentInterfaceVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaComponentInterfaceVisitor extends NabuccoToJavaVisitorSupport {

    /**
     * Creates a new {@link NabuccoToJavaComponentInterfaceVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaComponentInterfaceVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoComponent, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoComponent.nodeToken2.tokenImage;
        String pkg = this.getVisitorContext().getPackage();
        String projectName = super.getComponentName(NabuccoModelType.COMPONENT,
                NabuccoModifierComponentMapper
                        .getModifierType(nabuccoComponent.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMPONENT_INTERFACE_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.COMPONENT_INTERFACE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            this.setComponentName(pkg.replace(".facade.component", ""), type);

            // Super-classes
            super.createSuperClass();

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoComponent.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST component modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template component processing.", te);
        }
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<JavaModel> target) {

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);

            String name = nabuccoService.nodeToken1.tokenImage;

            JavaAstContainter<MethodDeclaration> operation = NabuccoToJavaComponentVisitorSupport
                    .createComponentInterfaceOperation(name, type);
            this.getVisitorContext().getContainerList().add(operation);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST component modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template component processing.", te);
        }
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, MdaModel<JavaModel> target) {

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);

            String name = nabuccoComponent.nodeToken1.tokenImage;

            this.getVisitorContext().getContainerList().add(
                    NabuccoToJavaComponentVisitorSupport.createComponentInterfaceOperation(name,
                            type));

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST component modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template component processing.",
                    te);
        }
    }

    /**
     * Sets the constant COMPONENT_NAME in the component interface.
     * 
     * @param name
     *            name of the component
     * @param type
     *            component interface
     * @throws JavaModelException
     */
    private void setComponentName(String name, TypeDeclaration type) throws JavaModelException {
        FieldDeclaration field = JavaAstElementFactory.getInstance().getJavaAstType().getField(
                type, "COMPONENT_NAME");

        Literal literal = JavaAstModelProducer.getInstance().createLiteral(name,
                LiteralType.STRING_LITERAL);
        field.initialization = literal;
    }

}
