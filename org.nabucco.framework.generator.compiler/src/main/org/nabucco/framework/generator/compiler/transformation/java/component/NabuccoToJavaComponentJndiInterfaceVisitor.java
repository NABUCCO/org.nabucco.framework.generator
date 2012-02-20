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

import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstField;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaComponentRemoteInterfaceVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaComponentJndiInterfaceVisitor extends NabuccoToJavaVisitorSupport {

    private static final String CLASS_SUFFIX = "JndiNames";

    private static final String JNDI_SUFFIX_LOCAL = "local";

    private static final String JNDI_SUFFIX_REMOTE = "remote";

    private static final String JNDI_PREFIX = "nabucco";

    private static final String COMPONENT_RELATION_SERVICE = "ComponentRelationService";
    
    private static final String QUERY_FILTER_SERVICE = "QueryFilterService";

    private String pkg;

    /**
     * Creates a new {@link NabuccoToJavaComponentJndiInterfaceVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaComponentJndiInterfaceVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<JavaModel> target) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String interfaceName = nabuccoComponent.nodeToken2.tokenImage;
        String className = interfaceName + CLASS_SUFFIX;
        this.pkg = this.getVisitorContext().getPackage();
        String projectName = super.getProjectName(NabuccoModelType.COMPONENT, NabuccoModifierType.PRIVATE);

        this.createComponentRelationJndi();
        this.createQueryFilterJndi();

        // Visit sub-nodes first!
        super.visit(nabuccoComponent, target);

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMPONENT_JNDI_INTERFACE_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.COMPONENT_JNDI_INTERFACE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, className);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), this.pkg.replace(PKG_FACADE, PKG_IMPL));

            // Javadoc
            JavaAstSupport.convertJavadocAnnotations(nabuccoComponent.annotationDeclaration, type);

            // Collected nodes
            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error during Java AST component modification.", me);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template component processing.", te);
        }
    }

    /**
     * Create the constants for the component relation service.
     */
    private void createComponentRelationJndi() {
        this.createConstants(COMPONENT_RELATION_SERVICE, this.pkg + PKG_SEPARATOR + COMPONENT_RELATION_SERVICE);
    }
    
    /**
     * Create the constants for the query filter service.
     */
    private void createQueryFilterJndi() {
        this.createConstants(QUERY_FILTER_SERVICE, this.pkg + PKG_SEPARATOR + QUERY_FILTER_SERVICE);
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<JavaModel> target) {
        String type = nabuccoService.nodeToken1.tokenImage;
        String canonicalName = super.resolveImport(type);

        this.createConstants(type, canonicalName);
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, MdaModel<JavaModel> target) {
        String type = nabuccoComponent.nodeToken1.tokenImage;
        String canonicalName = super.resolveImport(type);

        this.createConstants(type, canonicalName);
    }

    /**
     * Create the JNDI constants for the given service type.
     * 
     * @param type
     *            the service type
     */
    private void createConstants(String type, String typeJndi) {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstField fieldFactory = JavaAstElementFactory.getInstance().getJavaAstField();

        String name = NabuccoTransformationUtility.firstToLower(type);
        try {

            // Common Component JNDI Prefix
            String jndi = JNDI_PREFIX + PATH_SEPARATOR + this.pkg.replace(".facade.component", "") + PATH_SEPARATOR;

            // java.lang.String
            TypeReference constantType = producer.createTypeReference("String", false);

            // Local Constant
            String localName = NabuccoTransformationUtility.toConstantName(name + ServerConstants.LOCAL);
            FieldDeclaration localConstant = producer.createFieldDeclaration(localName, ClassFileConstants.AccFinal);
            localConstant.type = constantType;

            // Local String Literal
            String localJndi = jndi + typeJndi + PATH_SEPARATOR + JNDI_SUFFIX_LOCAL;
            Literal localJndiLiteral = producer.createLiteral(localJndi, LiteralType.STRING_LITERAL);
            fieldFactory.setFieldInitializer(localConstant, localJndiLiteral);

            // Remote Constant
            String remoteName = NabuccoTransformationUtility.toConstantName(name + ServerConstants.REMOTE);
            FieldDeclaration remoteConstant = producer.createFieldDeclaration(remoteName, ClassFileConstants.AccFinal);
            remoteConstant.type = constantType;

            // Remote String Literal
            String remoteJndi = jndi + typeJndi + PATH_SEPARATOR + JNDI_SUFFIX_REMOTE;
            Literal remoteJndiLiteral = producer.createLiteral(remoteJndi, LiteralType.STRING_LITERAL);
            fieldFactory.setFieldInitializer(remoteConstant, remoteJndiLiteral);

            super.getVisitorContext().getContainerList()
                    .add(new JavaAstContainter<FieldDeclaration>(localConstant, JavaAstType.FIELD));

            super.getVisitorContext().getContainerList()
                    .add(new JavaAstContainter<FieldDeclaration>(remoteConstant, JavaAstType.FIELD));

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error creating JNDI Constant.", me);
        }
    }
}
