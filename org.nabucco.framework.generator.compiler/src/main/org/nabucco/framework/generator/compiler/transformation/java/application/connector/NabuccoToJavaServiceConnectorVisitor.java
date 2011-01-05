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
package org.nabucco.framework.generator.compiler.transformation.java.application.connector;

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.application.connector.util.ServiceLinkResolver;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.ApplicationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ConnectorStatement;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceLinkDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceConnectorVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceConnectorVisitor extends NabuccoToJavaVisitorSupport implements
        NabuccoJavaTemplateConstants, ServerConstants {

    private JavaCompilationUnit unit;

    private ApplicationStatement nabuccoApplication;

    private static final String FIELD_SOURCE_SERVICE = "SOURCE_SERVICE";

    private static final String FIELD_SOURCE_OPERATION = "SOURCE_OPERATION";

    private static final String FIELD_SOURCE_MESSAGE = "SOURCE_MESSAGE";

    private static final String STRATEGY_BEFORE = "STRATEGY_BEFORE";

    /**
     * Creates a new {@link NabuccoToJavaServiceConnectorVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param nabuccoApplication
     *            the application statement
     */
    public NabuccoToJavaServiceConnectorVisitor(NabuccoToJavaVisitorContext visitorContext,
            ApplicationStatement nabuccoApplication) {
        super(visitorContext);
        this.nabuccoApplication = nabuccoApplication;
    }

    @Override
    public void visit(ConnectorStatement nabuccoConnector, MdaModel<JavaModel> target) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoConnector.nodeToken2.tokenImage;
        NabuccoModifierType modifier = NabuccoModifierComponentMapper
                .getModifierType(nabuccoConnector.nodeToken.tokenImage);

        String projectName = super.getProjectName(NabuccoModelType.CONNECTOR, modifier);

        try {
            // Load Template
            this.unit = super.extractAst(SERVICE_CONNECTOR_TEMPLATE);
            TypeDeclaration type = this.unit.getType(SERVICE_CONNECTOR_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);

            // Package
            this.createPackage(this.unit);

            // Javadoc
            this.createJavadoc(nabuccoConnector, type);

            // Constructor
            this.createConstructor(nabuccoConnector);

            // File creation
            this.unit.setProjectName(projectName);
            this.unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating Java Application.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error creating Java Application.", te);
        }

        super.visit(nabuccoConnector, target);
    }

    /**
     * Create the class javadoc depending on the application information.
     * 
     * @param nabuccoConnector
     *            the connector holding the annotations
     * @param type
     *            the java type to create the javadoc for
     */
    private void createJavadoc(ConnectorStatement nabuccoConnector, TypeDeclaration type) {
        Node application = nabuccoConnector.getParent().getParent().getParent().getParent();
        if (application instanceof ApplicationStatement) {
            JavaAstSupport.convertJavadocAnnotations(
                    ((ApplicationStatement) application).annotationDeclaration, type);
        }
    }

    /**
     * Create the package of the connector.
     * 
     * @param unit
     *            the java compilation unit
     * 
     * @throws JavaModelException
     */
    private void createPackage(JavaCompilationUnit unit) throws JavaModelException {
        StringBuilder pkg = new StringBuilder();
        pkg.append(super.getVisitorContext().getPackage());
        pkg.append(PKG_SEPARATOR);
        pkg.append(PKG_CONNECTOR);

        JavaAstElementFactory.getInstance().getJavaAstUnit()
                .setPackage(unit.getUnitDeclaration(), pkg.toString());
    }

    /**
     * Modify the strategy type in the connector constructor.
     * 
     * @param nabuccoConnector
     * 
     * @throws JavaModelException
     */
    private void createConstructor(ConnectorStatement nabuccoConnector) throws JavaModelException {

        NabuccoAnnotation connectorStrategy = NabuccoAnnotationMapper.getInstance()
                .mapToAnnotation(nabuccoConnector.annotationDeclaration,
                        NabuccoAnnotationType.CONNECTOR_STRATEGY);

        String value = connectorStrategy.getValue();

        // Skip because template defaults to AFTER
        if (!value.equalsIgnoreCase(STRATEGY_BEFORE)) {
            return;
        }

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        TypeDeclaration type = this.unit.getType();

        List<ConstructorDeclaration> constructors = javaFactory.getJavaAstType().getConstructors(
                type);

        if (constructors.size() != 1) {
            throw new IllegalStateException(
                    "Connector Template not valid. Multiple constructors found.");
        }

        ConstructorDeclaration constructor = constructors.get(0);

        ExplicitConstructorCall superCall = constructor.constructorCall;

        if (superCall == null || superCall.arguments == null || superCall.arguments.length != 1) {
            throw new IllegalStateException("Connector Template not valid. No super call defined.");
        }

        Expression strategy = superCall.arguments[0];

        if (!(strategy instanceof QualifiedNameReference)) {
            throw new IllegalStateException("Connector Template not valid.");
        }

        QualifiedNameReference reference = (QualifiedNameReference) strategy;

        if (reference.tokens == null || reference.tokens.length != 2) {
            throw new IllegalStateException("Connector Template not valid.");
        }

        reference.tokens[reference.tokens.length - 1] = value.toUpperCase().toCharArray();
    }

    @Override
    public void visit(ServiceLinkDeclaration serviceLink, MdaModel<JavaModel> target) {

        boolean isSource = JavaAstSupport.hasAnnotation(serviceLink.annotationDeclaration,
                NabuccoAnnotationType.SOURCE);

        if (isSource) {
            this.createSourceConstants(serviceLink);
        } else {
            this.createTargetCallbacks(serviceLink, target);
        }

        super.visit(serviceLink, target);
    }

    /**
     * Modifies the constants for the source operation signature.
     * 
     * @param serviceLink
     *            the service link for the source service
     */
    private void createSourceConstants(ServiceLinkDeclaration serviceLink) {

        ServiceLinkResolver resolver = new ServiceLinkResolver(this.nabuccoApplication,
                super.getVisitorContext());

        resolver.resolve(serviceLink);

        String service = resolver.getService();
        String operation = resolver.getServiceOperation();
        String message = resolver.getRequestMessage();

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            FieldDeclaration field;
            TypeDeclaration type = this.unit.getType();

            field = javaFactory.getJavaAstType().getField(type, FIELD_SOURCE_SERVICE);
            field.initialization = producer.createLiteral(service, LiteralType.STRING_LITERAL);

            field = javaFactory.getJavaAstType().getField(type, FIELD_SOURCE_OPERATION);
            field.initialization = producer.createLiteral(operation, LiteralType.STRING_LITERAL);

            field = javaFactory.getJavaAstType().getField(type, FIELD_SOURCE_MESSAGE);
            field.initialization = producer.createLiteral(message, LiteralType.STRING_LITERAL);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating service link signature.", e);
        }

    }

    /**
     * Create the callback methods for each target servicelink.
     * 
     * @param serviceLink
     *            the target servicelink
     * @param target
     *            the target model
     */
    private void createTargetCallbacks(ServiceLinkDeclaration serviceLink,
            MdaModel<JavaModel> target) {
        NabuccoToJavaServiceTargetServiceLinkVisitor visitor = new NabuccoToJavaServiceTargetServiceLinkVisitor(
                super.getVisitorContext(), this.nabuccoApplication, this.unit);

        serviceLink.accept(visitor, target);
    }

}
