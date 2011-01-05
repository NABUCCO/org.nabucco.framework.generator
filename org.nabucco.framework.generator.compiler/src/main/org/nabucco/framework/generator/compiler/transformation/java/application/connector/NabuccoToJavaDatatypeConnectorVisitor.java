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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.constants.ComponentRelationConstants;
import org.nabucco.framework.generator.compiler.transformation.java.application.connector.util.DatatypeCollector;
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
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaDatatypeConnectorVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaDatatypeConnectorVisitor extends NabuccoToJavaVisitorSupport implements
        ServerConstants {

    private JavaCompilationUnit unit;

    private ApplicationStatement nabuccoApplication;

    private static final String COMPONENT_RELATION_TYPE = "ComponentRelationType";

    private static final JavaAstMethodSignature SIGNATURE_GET_CLASS = new JavaAstMethodSignature(
            "getSourceDatatypeClass");

    private static final JavaAstMethodSignature SIGNATURE_GET_RELATIONTYPE = new JavaAstMethodSignature(
            "getRelationTypes");

    private static final JavaAstMethodSignature SIGNATURE_LOOKUP_COMPONENT = new JavaAstMethodSignature(
            "lookupTargetComponent", "ComponentRelationType");

    private static final JavaAstMethodSignature SIGNATURE_INTERNAL_MAINTAIN = new JavaAstMethodSignature(
            "internalMaintain", "ComponentRelation");

    private static final JavaAstMethodSignature SIGNATURE_INTERNAL_RESOLVE = new JavaAstMethodSignature(
            "internalResolve", "ComponentRelation");

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaDatatypeConnectorVisitor.class);

    /**
     * Creates a new {@link NabuccoToJavaDatatypeConnectorVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param nabuccoApplication
     *            the application statement
     */
    public NabuccoToJavaDatatypeConnectorVisitor(NabuccoToJavaVisitorContext visitorContext,
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
            this.unit = super.extractAst(NabuccoJavaTemplateConstants.DATATYPE_CONNECTOR_TEMPLATE);
            TypeDeclaration type = this.unit
                    .getType(NabuccoJavaTemplateConstants.DATATYPE_CONNECTOR_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);

            // Package
            this.createPackage(this.unit);

            // Javadoc
            this.createJavadoc(nabuccoConnector, type);

            // getRelationTypes()
            this.createGetRelationTypes(nabuccoConnector);

            // lookupTargetComponent()
            this.createDatatypeMethods(nabuccoConnector);

            // internalMaintain() internalResolve()
            this.createInternalMethods(nabuccoConnector);

            NabuccoToJavaDatatypeTargetServiceLinkVisitor visitor = new NabuccoToJavaDatatypeTargetServiceLinkVisitor(
                    super.getVisitorContext(), this.nabuccoApplication, this.unit);

            nabuccoConnector.accept(visitor, target);
            
            // File creation
            this.unit.setProjectName(projectName);
            this.unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(this.unit);

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
     * Create the method getRelationTypes().
     * 
     * @param nabuccoConnector
     *            the connector statement
     * 
     * @throws JavaModelException
     */
    private void createGetRelationTypes(ConnectorStatement nabuccoConnector)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        String type = this.nabuccoApplication.nodeToken2.tokenImage + COMPONENT_RELATION_TYPE;
        TypeReference typeReference = producer.createTypeReference(type, false);

        MethodDeclaration getRelationType = (MethodDeclaration) javaFactory.getJavaAstType()
                .getMethod(this.unit.getType(), SIGNATURE_GET_RELATIONTYPE);

        TypeReference returnType = javaFactory.getJavaAstMethod().getReturnType(getRelationType);

        if (returnType instanceof ParameterizedSingleTypeReference) {
            ((ParameterizedSingleTypeReference) returnType).typeArguments[0] = typeReference;
        }

        if (getRelationType.statements != null && getRelationType.statements.length == 1) {
            ReturnStatement returnStatement = (ReturnStatement) getRelationType.statements[0];

            MessageSend valuesBySource = (MessageSend) returnStatement.expression;
            javaFactory.getJavaAstMethodCall().setMethodReceiver(typeReference, valuesBySource);
        }

        StringBuilder importString = new StringBuilder();
        importString.append(super.getVisitorContext().getPackage());
        importString.append(PKG_SEPARATOR);
        importString.append(ComponentRelationConstants.CR_PACKAGE);
        importString.append(PKG_SEPARATOR);
        importString.append(type);

        ImportReference importReference = producer.createImportReference(importString.toString());
        javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(), importReference);
    }

    /**
     * Create methods getClass() and lookupTargetComponent().
     * 
     * @param connector
     *            the connector statement
     */
    private void createDatatypeMethods(ConnectorStatement connector) {

        DatatypeCollector collector = new DatatypeCollector(super.getVisitorContext(),
                this.nabuccoApplication);

        collector.accept(connector);

        String connectorName = connector.nodeToken2.tokenImage;

        if (collector.getSourceType() == null) {
            throw new IllegalStateException("No source datatype defined in connector "
                    + connectorName + ". Missing @Source annotation.");
        }

        if (collector.getTargetMap().isEmpty()) {
            logger.warning("No @Target datatype defined in connector ", connectorName, ".");
        }

        try {
            this.createTarget(collector);
            this.createSource(collector.getSourceType());
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating Java Application.", jme);
        }
    }

    /**
     * Create the getClass() method depending on the source datatype.
     * 
     * @param type
     *            the source type
     * 
     * @throws JavaModelException
     */
    private void createSource(String type) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        MethodDeclaration getClass = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                this.unit.getType(), SIGNATURE_GET_CLASS);

        TypeReference typeReference = producer.createTypeReference(type, false);

        TypeReference returnType = javaFactory.getJavaAstMethod().getReturnType(getClass);

        if (returnType instanceof ParameterizedSingleTypeReference) {
            ((ParameterizedSingleTypeReference) returnType).typeArguments[0] = typeReference;
        }

        if (getClass.statements != null && getClass.statements.length == 1) {
            ReturnStatement returnStatement = (ReturnStatement) getClass.statements[0];
            ClassLiteralAccess classAccess = (ClassLiteralAccess) returnStatement.expression;
            classAccess.type = typeReference;
        }

        ImportReference importReference = producer.createImportReference(super.resolveImport(type));
        javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(), importReference);
    }

    /**
     * Create the lookupTargetComponent() method depending on the target datatypes.
     * 
     * @param collector
     *            the collector holding the datatype information
     * 
     * @throws JavaModelException
     */
    private void createTarget(DatatypeCollector collector) throws JavaModelException {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        MethodDeclaration lookupMethod = (MethodDeclaration) javaFactory.getJavaAstType()
                .getMethod(this.unit.getType(), SIGNATURE_LOOKUP_COMPONENT);

        String applicationType = this.nabuccoApplication.nodeToken2.tokenImage
                + COMPONENT_RELATION_TYPE;
        TypeReference applicationTypeReference = producer.createTypeReference(applicationType,
                false);

        // If Condition
        IfStatement ifStatement = (IfStatement) lookupMethod.statements[0];
        InstanceOfExpression instanceofExpression = (InstanceOfExpression) ifStatement.condition;
        instanceofExpression.type = applicationTypeReference;

        // Switch
        Block thenStatement = (Block) ifStatement.thenStatement;
        SwitchStatement switchStatement = (SwitchStatement) thenStatement.statements[0];
        CastExpression cast = (CastExpression) switchStatement.expression;
        cast.type = applicationTypeReference;

        String source = collector.getSourceType().toUpperCase();

        List<CaseStatement> cases = new ArrayList<CaseStatement>();
        List<Statement> statements = new ArrayList<Statement>();

        for (String targetName : collector.getTargetMap().keySet()) {
            String targetType = collector.getTargetMap().get(targetName);

            CaseStatement caseStatement = producer.createCaseStatement(source
                    + CONSTANT_SEPARATOR + targetType.toUpperCase());

            cases.add(caseStatement);
            statements.add(caseStatement);

            ServiceLinkResolver resolver = collector.getMaintainServices().get(targetName);

            if (resolver == null) {
                resolver = collector.getResolveServices().get(targetName);

                if (resolver == null) {
                    throw new IllegalStateException("No ServiceLink defined for target '"
                            + targetName + "'.");
                }
            }
            
            String component = resolver.getComponent();

            component = component.substring(component.lastIndexOf(PKG_SEPARATOR) + 1);

            SingleNameReference componentLocator = producer.createSingleNameReference(component
                    + LOCATOR);

            MessageSend getInstance = producer.createMessageSend("getInstance", componentLocator,
                    null);
            MessageSend getComponent = producer
                    .createMessageSend("getComponent", getInstance, null);

            ReturnStatement returnStatement = producer.createReturnStatement(getComponent);
            statements.add(returnStatement);
        }

        switchStatement.cases = cases.toArray(new CaseStatement[cases.size()]);
        switchStatement.statements = statements.toArray(new Statement[statements.size()]);
    }

    /**
     * Create the internalMaintain() and internalResolve methods.
     * 
     * @param nabuccoConnector
     *            the connector
     * 
     * @throws JavaModelException
     */
    private void createInternalMethods(ConnectorStatement nabuccoConnector)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        MethodDeclaration maintain = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                this.unit.getType(), SIGNATURE_INTERNAL_MAINTAIN);

        MethodDeclaration resolve = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                this.unit.getType(), SIGNATURE_INTERNAL_RESOLVE);

        this.createMethod(maintain);
        this.createMethod(resolve);
    }

    /**
     * Prepare the method by application component relation type.
     * 
     * @param method
     *            the method to prepare
     * 
     * @throws JavaModelException
     */
    private void createMethod(MethodDeclaration method) throws JavaModelException {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        String applicationType = this.nabuccoApplication.nodeToken2.tokenImage
                + COMPONENT_RELATION_TYPE;
        TypeReference applicationTypeReference = producer.createTypeReference(applicationType,
                false);

        // If Condition
        IfStatement ifStatement = (IfStatement) method.statements[0];
        InstanceOfExpression instanceofExpression = (InstanceOfExpression) ifStatement.condition;
        instanceofExpression.type = applicationTypeReference;

        // Switch
        Block thenStatement = (Block) ifStatement.thenStatement;
        SwitchStatement switchStatement = (SwitchStatement) thenStatement.statements[0];
        CastExpression cast = (CastExpression) switchStatement.expression;
        cast.type = applicationTypeReference;
    }

}
