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
package org.nabucco.framework.generator.compiler.transformation.java.exception;

import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
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
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionParameterDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaExceptionVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaExceptionVisitor extends NabuccoToJavaVisitorSupport {

    private static final String IMPORT_EXCEPTION = "org.nabucco.framework.base.facade.exception.ExceptionSupport";

    /**
     * Creates a new {@link NabuccoToJavaExceptionVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaExceptionVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ExceptionStatement nabuccoException, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoException, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoException.nodeToken2.tokenImage;
        String pkg = this.getVisitorContext().getPackage();
        String projectName = super.getProjectName(NabuccoModelType.EXCEPTION,
                NabuccoModifierComponentMapper
                        .getModifierType(nabuccoException.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.EXCEPTION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.EXCEPTION_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            if (super.getVisitorContext().getNabuccoExtension() != null) {
                super.createSuperClass();
                super.removeImport(unit.getUnitDeclaration(), IMPORT_EXCEPTION);
            }

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoException.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating Java Exception.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error loading Java Exception Template.", te);
        }
    }

    @Override
    public void visit(ExceptionParameterDeclaration nabuccoExceptionParam, MdaModel<JavaModel> argu) {

        String name = nabuccoExceptionParam.nodeToken2.tokenImage;

        // TODO: Implement Exception Parameters

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField("String", name,
                NabuccoModifierType.PRIVATE, false);

        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field
                .getAstNode());
        JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field
                .getAstNode());

        super.getVisitorContext().getContainerList().add(field);
        super.getVisitorContext().getContainerList().add(getter);
        super.getVisitorContext().getContainerList().add(setter);
    }

}
