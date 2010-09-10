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
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaComponentImplementationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaComponentImplementationVisitor extends NabuccoToJavaVisitorSupport {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaComponentImplementationVisitor.class);

    /**
     * Creates a new {@link NabuccoToJavaComponentImplementationVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaComponentImplementationVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoComponent, target);
        
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String interfaceName = nabuccoComponent.nodeToken2.tokenImage;
        String interfacePackage = this.getVisitorContext().getPackage();
        String name = interfaceName + IMPLEMENTATION;
        String componentName = super.getComponentName(NabuccoModelType.COMPONENT, NabuccoModifierType.PRIVATE);

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMPONENT_IMPLEMENTATION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.COMPONENT_IMPLEMENTATION_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    interfacePackage.replace(PKG_FACADE, PKG_IMPL));

            // Super-classes
            super.createSuperClass();
            super.createInterface(interfaceName);
            
            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoComponent.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());
            
            // File creation
            unit.setProjectName(componentName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST component modification.");
            throw new NabuccoVisitorException("Error during Java AST component modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template component processing.");
            throw new NabuccoVisitorException("Error during Java template component processing.", te);
        }
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<JavaModel> target) {

        String type = nabuccoService.nodeToken1.tokenImage;
        String name = NabuccoTransformationUtility.firstToLower(type);

        NabuccoModifierType modifier = NabuccoModifierType.PRIVATE;

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name,
                modifier, false);

        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field
                .getAstNode());
        
        super.getVisitorContext().getContainerList().add(field);
        super.getVisitorContext().getContainerList().add(getter);
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, MdaModel<JavaModel> target) {

        String type = nabuccoComponent.nodeToken1.tokenImage;
        String name = NabuccoTransformationUtility.firstToLower(type);

        NabuccoModifierType modifier = NabuccoModifierType.PRIVATE;

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name,
                modifier, false);

        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field
                .getAstNode());
        
        super.getVisitorContext().getContainerList().add(field);
        super.getVisitorContext().getContainerList().add(getter);
    }

}
