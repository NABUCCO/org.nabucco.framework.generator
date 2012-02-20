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

import java.text.MessageFormat;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
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
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceLocatorVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceLocatorVisitor extends NabuccoToJavaVisitorSupport {

    private static final String DEFAULT_IMPL = "DefaultImpl";

    private static final String FIELD_JNDI = "JNDI_NAME";

    private static final String FIELD_LOGGER = "logger";

    private static final JavaAstMethodSignature METHOD_GETSERVICE = new JavaAstMethodSignature("getService", "Context");

    /**
     * Creates a new {@link NabuccoToJavaServiceLocatorVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceLocatorVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<JavaModel> target) {
        super.visit(nabuccoService, target);

        // Visit sub-nodes first!
        super.visit(nabuccoService, target);

        String interfaceName = nabuccoService.nodeToken2.tokenImage;
        String locatorName = interfaceName + LOCATOR;
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String projectName = super.getProjectName(NabuccoModelType.SERVICE,
                NabuccoModifierComponentMapper.getModifierType(nabuccoService.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SERVICE_LOCATOR_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SERVICE_LOCATOR_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, locatorName);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), super.getVisitorContext().getPackage());

            // Modifications
            this.jndi(interfaceName, type);
            this.logger(interfaceName, type);
            this.getService(interfaceName, type);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoService.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

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
     * Modify the logger field.
     * 
     * @param serviceName
     *            the service name
     * @param type
     *            the java type
     * 
     * @throws JavaModelException
     */
    private void jndi(String serviceName, TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        FieldDeclaration jndi = javaFactory.getJavaAstType().getField(type, FIELD_JNDI);
        StringLiteral literal = (StringLiteral) jndi.initialization;

        String serviceImport = super.resolveImport(serviceName);
        String componentName = NabuccoCompilerSupport.getParentComponentName(serviceImport);
        String jndiName = MessageFormat.format(new String(literal.source()), componentName, serviceImport);

        jndi.initialization = producer.createLiteral(jndiName, LiteralType.STRING_LITERAL);
    }

    /**
     * Modify the logger field.
     * 
     * @param serviceName
     *            the service name
     * @param type
     *            the java type
     * 
     * @throws JavaModelException
     */
    private void logger(String serviceName, TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        FieldDeclaration logger = javaFactory.getJavaAstType().getField(type, FIELD_LOGGER);

        MessageSend getLogger = (MessageSend) logger.initialization;
        ClassLiteralAccess classLiteral = (ClassLiteralAccess) getLogger.arguments[0];
        classLiteral.type = producer.createTypeReference(serviceName, false);
    }

    /**
     * Modify the getService method.
     * 
     * @param serviceName
     *            the service name
     * @param type
     *            the java type
     * 
     * @throws JavaModelException
     */
    private void getService(String serviceName, TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        MethodDeclaration getService = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                METHOD_GETSERVICE);

        TypeReference serviceType = producer.createTypeReference(serviceName, false);
        javaFactory.getJavaAstMethod().setReturnType(getService, serviceType);

        TryStatement tryStatement = (TryStatement) getService.statements[0];
        ReturnStatement returnStatement = (ReturnStatement) tryStatement.tryBlock.statements[0];

        CastExpression cast = (CastExpression) returnStatement.expression;
        cast.type = serviceType;

        returnStatement = (ReturnStatement) tryStatement.catchBlocks[0].statements[1];
        AllocationExpression allocation = (AllocationExpression) returnStatement.expression;

        allocation.type = producer.createTypeReference(serviceName + DEFAULT_IMPL, false);
    }

}
