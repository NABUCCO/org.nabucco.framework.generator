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
package org.nabucco.framework.generator.compiler.transformation.java.application;

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.application.connector.NabuccoToJavaConnectorVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.ApplicationStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaApplicationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaApplicationVisitor extends NabuccoToJavaVisitorSupport implements
        NabuccoJavaTemplateConstants {

    /**
     * Creates a new {@link NabuccoToJavaApplicationVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaApplicationVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ApplicationStatement nabuccoApplication, MdaModel<JavaModel> target) {
        super.visit(nabuccoApplication, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoApplication.nodeToken2.tokenImage;
        NabuccoModifierType modifier = NabuccoModifierComponentMapper
                .getModifierType(nabuccoApplication.nodeToken.tokenImage);

        String projectName = super.getProjectName(NabuccoModelType.APPLICATION, modifier);

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(APPLICATION_TEMPLATE);
            TypeDeclaration type = unit.getType(APPLICATION_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    super.getVisitorContext().getPackage());

            // Annotations
            JavaAstSupport
                    .convertJavadocAnnotations(nabuccoApplication.annotationDeclaration, type);

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

            NabuccoToJavaConnectorVisitor visitor = new NabuccoToJavaConnectorVisitor(
                    super.getVisitorContext(), nabuccoApplication);

            nabuccoApplication.accept(visitor, target);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating Java Application.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error creating Java Application.", te);
        }
    }

}
