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
package org.nabucco.framework.generator.compiler.transformation.java.adapter;

import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.AdapterStatement;
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
 * NabuccoToJavaAdapterInterfaceVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaAdapterInterfaceVisitor extends NabuccoToJavaVisitorSupport {

    /**
     * Creates a new {@link NabuccoToJavaAdapterInterfaceVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaAdapterInterfaceVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(AdapterStatement nabuccoAdapter, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoAdapter, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoAdapter.nodeToken2.tokenImage;
        String pkg = this.getVisitorContext().getPackage();
        String projectName = super.getProjectName(NabuccoModelType.ADAPTER,
                NabuccoModifierComponentMapper.getModifierType(nabuccoAdapter.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.ADAPTER_INTERFACE_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.ADAPTER_INTERFACE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            this.setAdapterName(pkg.replace(".facade.adapter", ""), type);

            this.configureJNDIName(name, pkg + PKG_SEPARATOR + name, type);

            // Super-classes
            super.createSuperClass();

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoAdapter.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST adapter modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template adapter processing.", te);
        }
    }

    /**
     * Configures the JNDI name of the adapter.
     * 
     * @param adapterName
     *            name of the adapter interface
     * @param pkg
     *            package of the adapter
     * @param type
     *            the type to modify
     * 
     * @throws JavaModelException
     */
    private void configureJNDIName(String adapterName, String pkg, TypeDeclaration type) throws JavaModelException {

        FieldDeclaration field = JavaAstElementFactory.getInstance().getJavaAstType()
                .getField(type, ServerConstants.JNDI_NAME);

        BinaryExpression init = (BinaryExpression) field.initialization;

        Literal literal = JavaAstModelProducer.getInstance().createLiteral(pkg, LiteralType.STRING_LITERAL);
        init.right = literal;
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<JavaModel> target) {

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.ADAPTER_OPERATION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.ADAPTER_OPERATION_TEMPLATE);

            String name = nabuccoService.nodeToken1.tokenImage;
            String operationName = PREFIX_GETTER + name;

            JavaAstContainter<MethodDeclaration> operation = NabuccoToJavaAdapterVisitorSupport
                    .createAdapterInterfaceOperation(name, type, operationName);
            this.getVisitorContext().getContainerList().add(operation);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST adapter modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template adapter processing.", te);
        }
    }

    /**
     * Sets the constant ADAPTER_NAME in the adapter interface.
     * 
     * @param name
     *            name of the adapter
     * @param type
     *            adapter interface
     * @throws JavaModelException
     */
    private void setAdapterName(String name, TypeDeclaration type) throws JavaModelException {
        FieldDeclaration field = JavaAstElementFactory.getInstance().getJavaAstType().getField(type, "ADAPTER_NAME");

        Literal literal = JavaAstModelProducer.getInstance().createLiteral(name, LiteralType.STRING_LITERAL);
        field.initialization = literal;
    }

}
