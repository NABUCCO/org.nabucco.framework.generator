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
package org.nabucco.framework.generator.compiler.transformation.java.service;

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceLocalInterfaceVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceLocalInterfaceVisitor extends NabuccoToJavaVisitorSupport implements ServerConstants {

    /**
     * Creates a new {@link NabuccoToJavaServiceLocalInterfaceVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceLocalInterfaceVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoService, target);

        String interfaceName = nabuccoService.nodeToken2.tokenImage;
        String localName = interfaceName + LOCAL;
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String componentName = super.getProjectName(NabuccoModelType.SERVICE,
                NabuccoModifierComponentMapper.getModifierType(nabuccoService.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SERVICE_LOCAL_INTERFACE_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SERVICE_LOCAL_INTERFACE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, localName);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), super.getVisitorContext().getPackage());

            this.getVisitorContext().getContainerList().add(JavaAstSupport.createSuperClass(interfaceName));

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoService.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

            // File creation
            unit.setProjectName(componentName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST service modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template service processing.", te);
        }
    }

}
