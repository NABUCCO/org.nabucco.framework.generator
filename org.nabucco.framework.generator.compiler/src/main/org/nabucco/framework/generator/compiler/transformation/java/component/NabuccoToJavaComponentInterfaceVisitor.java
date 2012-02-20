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
package org.nabucco.framework.generator.compiler.transformation.java.component;

import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
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
        String projectName = super.getProjectName(NabuccoModelType.COMPONENT,
                NabuccoModifierComponentMapper.getModifierType(nabuccoComponent.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMPONENT_INTERFACE_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.COMPONENT_INTERFACE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            this.setComponentName(pkg.replace(".facade.component", ""), type);
            this.setComponentPrefix(nabuccoComponent, type);

            this.configureJNDIName(name, pkg + PKG_SEPARATOR + name, type);
            
            // Super-classes
            super.createSuperClass();

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoComponent.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

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

    /**
     * Configures the JNDI name of the component.
     * 
     * @param componentName
     *            name of the component interface
     * @param pkg
     *            package of the component
     * @param type
     *            the type to modify
     * 
     * @throws JavaModelException
     */
    private void configureJNDIName(String componentName, String pkg, TypeDeclaration type) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        FieldDeclaration field = javaFactory.getJavaAstType().getField(type, ServerConstants.JNDI_NAME);

        BinaryExpression init = (BinaryExpression) field.initialization;

        Literal suffix = producer.createLiteral(pkg, LiteralType.STRING_LITERAL);
        init.right = suffix;
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<JavaModel> target) {

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);

            String name = nabuccoService.nodeToken1.tokenImage;
            String operationName = PREFIX_GETTER + name;

            JavaAstContainter<MethodDeclaration> operation = NabuccoToJavaComponentVisitorSupport
                    .createComponentInterfaceOperation(name, type, operationName);
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
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);

            String name = nabuccoComponent.nodeToken1.tokenImage;
            String operationName = PREFIX_GETTER + name;

            JavaAstContainter<MethodDeclaration> operation = NabuccoToJavaComponentVisitorSupport
                    .createComponentInterfaceOperation(name, type, operationName);
            this.getVisitorContext().getContainerList().add(operation);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST component modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template component processing.", te);
        }
    }

    /**
     * Sets the constant COMPONENT_NAME in the component interface.
     * 
     * @param name
     *            name of the component
     * @param type
     *            component interface
     * 
     * @throws JavaModelException
     */
    private void setComponentName(String name, TypeDeclaration type) throws JavaModelException {
        FieldDeclaration field = JavaAstElementFactory.getInstance().getJavaAstType().getField(type, "COMPONENT_NAME");

        Literal literal = JavaAstModelProducer.getInstance().createLiteral(name, LiteralType.STRING_LITERAL);
        field.initialization = literal;
    }

    /**
     * Sets the constant COMPONENT_PREFIX in the component interface.
     * 
     * @param nabuccoStatement
     *            the component statement
     * @param type
     *            component interface
     * 
     * @throws JavaModelException
     */
    private void setComponentPrefix(ComponentStatement nabuccoStatement, TypeDeclaration type)
            throws JavaModelException {

        FieldDeclaration field = JavaAstElementFactory.getInstance().getJavaAstType()
                .getField(type, "COMPONENT_PREFIX");

        String prefix = this.createComponentId(nabuccoStatement).toLowerCase();

        Literal literal = JavaAstModelProducer.getInstance().createLiteral(prefix, LiteralType.STRING_LITERAL);
        field.initialization = literal;
    }

    /**
     * Extracts the component id from the ComponentId annotation.
     * 
     * @param nabuccoComponent
     *            the component
     * 
     * @return the component id
     */
    private String createComponentId(ComponentStatement nabuccoComponent) {

        NabuccoAnnotation annotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoComponent.annotationDeclaration, NabuccoAnnotationType.COMPONENT_PREFIX);

        if (annotation == null) {
            String componentName = nabuccoComponent.nodeToken2.tokenImage;

            if (componentName.length() < 4) {
                return COMPONENT.substring(0, componentName.length()) + componentName;
            }

            return componentName.substring(0, 4);
        }

        if (annotation.getValue() == null || annotation.getValue().length() != 4) {
            String componentName = nabuccoComponent.nodeToken2.tokenImage;

            if (componentName.length() < 4) {
                return COMPONENT.toLowerCase().substring(0, componentName.length()) + componentName;
            }

            return componentName.substring(0, 4);
        }

        return annotation.getValue();
    }

}
