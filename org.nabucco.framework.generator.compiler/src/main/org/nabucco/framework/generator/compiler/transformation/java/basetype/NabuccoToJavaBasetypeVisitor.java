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
package org.nabucco.framework.generator.compiler.transformation.java.basetype;

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.constraint.NabuccoToJavaConstraintMapper;
import org.nabucco.framework.generator.compiler.transformation.java.common.reflection.NabuccoToJavaReflectionFacade;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaBasetypeVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaBasetypeVisitor extends NabuccoToJavaVisitorSupport {

    /**
     * Creates a new {@link NabuccoToJavaBasetypeVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaBasetypeVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(BasetypeStatement nabuccoBasetype, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoBasetype, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoBasetype.nodeToken2.tokenImage;
        String pkg = this.getVisitorContext().getPackage();
        NabuccoModifierType modifier = NabuccoModifierComponentMapper
                .getModifierType(nabuccoBasetype.nodeToken.tokenImage);
        String projectName = super.getProjectName(NabuccoModelType.BASETYPE, modifier);

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.BASETYPE_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.BASETYPE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // Super-classes
            String superType = super.getVisitorContext().getNabuccoExtension();
            JavaAstContainter<TypeReference> superClass = JavaAstSupport
                    .createSuperClass(superType);

            this.getVisitorContext().getContainerList().add(superClass);

            // Javadoc
            JavaAstSupport.convertJavadocAnnotations(nabuccoBasetype.annotationDeclaration, type);

            // Default Values
            NabuccoToJavaBasetypeVisitorSupport.createDefaultValues(
                    nabuccoBasetype.annotationDeclaration, superType, type);

            // Alternative constructor
            NabuccoToJavaBasetypeVisitorSupport.adjustAlternativeConstructor(name, superType, type);

            // Clone method
            NabuccoToJavaBasetypeCloneVisitor cloneVisitor = new NabuccoToJavaBasetypeCloneVisitor(
                    type, super.getVisitorContext());

            nabuccoBasetype.accept(cloneVisitor, target);

            // Convert constraints
            NabuccoToJavaConstraintMapper.getInstance().convertStatementConstraints(
                    nabuccoBasetype, type);

            // Adjust the getProperties() method of the basetype
            NabuccoToJavaReflectionFacade.getInstance().createReflection(nabuccoBasetype, unit);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST basetype modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template basetype processing.", te);
        }
    }
}
