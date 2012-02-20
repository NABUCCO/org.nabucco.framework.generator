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

import java.util.HashSet;

import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoServiceType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceImplementationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceImplementationVisitor extends NabuccoToJavaVisitorSupport implements ServerConstants {

    /**
     * Creates a new {@link NabuccoToJavaServiceImplementationVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceImplementationVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<JavaModel> target) {

        AnnotationDeclaration annotations = nabuccoService.annotationDeclaration;

        NabuccoToJavaServiceImplementationOperationVisitor operationVisitor = new NabuccoToJavaServiceImplementationOperationVisitor(
                super.getVisitorContext(), new HashSet<NabuccoServiceType>());

        nabuccoService.accept(operationVisitor, target);

        // Service Extension
        if (nabuccoService.nodeOptional1.present()) {
            NabuccoModel parent = super.getParent();

            if (parent != null) {
                NabuccoToJavaVisitorContext context = new NabuccoToJavaVisitorContext(super.getVisitorContext());
                operationVisitor = new NabuccoToJavaServiceImplementationOperationVisitor(context,
                        operationVisitor.getServiceTypes());

                parent.getUnit().accept(operationVisitor, target);

                super.getVisitorContext().getContainerList().addAll(context.getContainerList());
                super.getVisitorContext().getImportList().addAll(context.getImportList());
            }
        }

        String interfaceName = nabuccoService.nodeToken2.tokenImage;

        String interfacePackage = this.getVisitorContext().getPackage();
        String name = interfaceName + IMPLEMENTATION;
        String projectName = super.getProjectName(NabuccoModelType.SERVICE, NabuccoModifierType.PRIVATE);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SERVICE_IMPLEMENTATION_TEMPLATE);
            TypeDeclaration type = unit.getType();

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    interfacePackage.replace(PKG_FACADE, PKG_IMPL));

            // Interfaces
            super.createInterface(interfaceName);

            // PostConstruct
            NabuccoToJavaServiceVisitorSupport.preparePostConstruct(nabuccoService, unit);

            // PreDestroy
            NabuccoToJavaServiceVisitorSupport.preparePreDestroy(type);

            // Join Points
            NabuccoToJavaServiceJoinPointVisitor joinPointVisitor = new NabuccoToJavaServiceJoinPointVisitor(unit,
                    super.getVisitorContext());

            nabuccoService.accept(joinPointVisitor, target);

            // Javadoc
            JavaAstSupport.convertJavadocAnnotations(annotations, type);

            // Collected child elements.
            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(), super
                    .getVisitorContext().getImportList());

            this.changeInjectionId(annotations, interfaceName, type);

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST service modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template service processing.", te);
        }
    }

    /**
     * Change the injection id of the service implementation.
     * 
     * @param annotations
     *            the annotations
     * @param name
     *            the service name
     * @param type
     *            the type to change
     */
    private void changeInjectionId(AnnotationDeclaration annotations, String name, TypeDeclaration type) {

        String id = name;

        try {

            NabuccoAnnotation injectionId;
            injectionId = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations,
                    NabuccoAnnotationType.INJECTION_ID);

            if (injectionId != null) {
                id = injectionId.getValue();
            }

            FieldDeclaration field = JavaAstElementFactory.getInstance().getJavaAstType().getField(type, INJECTION_ID);

            field.initialization = JavaAstModelProducer.getInstance().createLiteral(id, LiteralType.STRING_LITERAL);

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error changing injection ID.", me);
        }
    }

}
