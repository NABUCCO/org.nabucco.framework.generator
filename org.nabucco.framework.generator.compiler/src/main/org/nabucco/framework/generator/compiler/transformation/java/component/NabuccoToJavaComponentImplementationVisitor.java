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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
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
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethod;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaComponentImplementationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaComponentImplementationVisitor extends NabuccoToJavaVisitorSupport implements ServerConstants {

    private static final String JNDI_CLASS_SUFFIX = "JndiNames";

    private static final String LOOKUP = "lookup";

    private static final String ANNOTATION_OVERRIDE = "Override";

    private static final String COMPONENT_RELATION_SERVICE = "COMPONENT_RELATION_SERVICE";

    private static final String QUERY_FILTER_SERVICE = "QUERY_FILTER_SERVICE";

    private static final JavaAstMethodSignature METHOD_COMPONENT_RELATION_SERVICE_REMOTE = new JavaAstMethodSignature(
            "getComponentRelationService");

    private static final JavaAstMethodSignature METHOD_COMPONENT_RELATION_SERVICE_LOCAL = new JavaAstMethodSignature(
            "getComponentRelationServiceLocal");

    private static final JavaAstMethodSignature METHOD_QUERY_FILTER_SERVICE_REMOTE = new JavaAstMethodSignature(
            "getQueryFilterService");

    private static final JavaAstMethodSignature METHOD_QUERY_FILTER_SERVICE_LOCAL = new JavaAstMethodSignature(
            "getQueryFilterServiceLocal");

    private String interfaceName;

    /**
     * Creates a new {@link NabuccoToJavaComponentImplementationVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaComponentImplementationVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<JavaModel> target) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        this.interfaceName = nabuccoComponent.nodeToken2.tokenImage;
        String interfacePackage = this.getVisitorContext().getPackage();
        String name = this.interfaceName + IMPLEMENTATION;
        String projectName = super.getProjectName(NabuccoModelType.COMPONENT, NabuccoModifierType.PRIVATE);

        // Visit sub-nodes first!
        super.visit(nabuccoComponent, target);

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMPONENT_IMPLEMENTATION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.COMPONENT_IMPLEMENTATION_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    interfacePackage.replace(PKG_FACADE, PKG_IMPL));

            // Super-classes
            super.createSuperClass();
            super.createInterface(this.interfaceName + LOCAL);
            super.createInterface(this.interfaceName + REMOTE);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoComponent.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

            // Injection ID
            this.injectionId(nabuccoComponent.annotationDeclaration, this.interfaceName, type);

            // Component Relation Service Lookup
            this.componentRelationService(this.interfaceName, type);
            
            // Query Filter Service Lookup
            this.queryFilterService(this.interfaceName, type);

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST component modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template component processing.", te);
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
     *            the java type to change
     */
    private void injectionId(AnnotationDeclaration annotations, String name, TypeDeclaration type) {

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

    /**
     * Change the template component relation service lookup.
     * 
     * @param componentName
     *            the name of the component
     * @param type
     *            the java type to change
     */
    private void componentRelationService(String componentName, TypeDeclaration type) {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            MethodDeclaration remote = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                    METHOD_COMPONENT_RELATION_SERVICE_REMOTE);
            MethodDeclaration local = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                    METHOD_COMPONENT_RELATION_SERVICE_LOCAL);

            String jndiNames = componentName + JNDI_CLASS_SUFFIX;

            {
                ReturnStatement returnStatement = (ReturnStatement) remote.statements[0];
                MessageSend lookup = (MessageSend) returnStatement.expression;
                lookup.arguments[0] = producer.createQualifiedNameReference(jndiNames, COMPONENT_RELATION_SERVICE
                        + CONSTANT_SEPARATOR + REMOTE.toUpperCase());
            }

            {
                ReturnStatement returnStatement = (ReturnStatement) local.statements[0];
                MessageSend lookup = (MessageSend) returnStatement.expression;
                lookup.arguments[0] = producer.createQualifiedNameReference(jndiNames, COMPONENT_RELATION_SERVICE
                        + CONSTANT_SEPARATOR + LOCAL.toUpperCase());
            }

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error changing component relation service lookup.", me);
        }
    }
    
    /**
     * Change the template query filter service lookup.
     * 
     * @param componentName
     *            the name of the component
     * @param type
     *            the java type to change
     */
    private void queryFilterService(String componentName, TypeDeclaration type) {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        
        try {
            MethodDeclaration remote = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                    METHOD_QUERY_FILTER_SERVICE_REMOTE);
            MethodDeclaration local = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                    METHOD_QUERY_FILTER_SERVICE_LOCAL);
            
            String jndiNames = componentName + JNDI_CLASS_SUFFIX;
            
            {
                ReturnStatement returnStatement = (ReturnStatement) remote.statements[0];
                MessageSend lookup = (MessageSend) returnStatement.expression;
                lookup.arguments[0] = producer.createQualifiedNameReference(jndiNames, QUERY_FILTER_SERVICE
                        + CONSTANT_SEPARATOR + REMOTE.toUpperCase());
            }
            
            {
                ReturnStatement returnStatement = (ReturnStatement) local.statements[0];
                MessageSend lookup = (MessageSend) returnStatement.expression;
                lookup.arguments[0] = producer.createQualifiedNameReference(jndiNames, QUERY_FILTER_SERVICE
                        + CONSTANT_SEPARATOR + LOCAL.toUpperCase());
            }
            
        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error changing query filter service lookup.", me);
        }
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<JavaModel> target) {

        String type = nabuccoService.nodeToken1.tokenImage;

        JavaAstContainter<MethodDeclaration> localGetter = this.createGetter(type + LOCAL, type, LOCAL);
        super.getVisitorContext().getContainerList().add(localGetter);

        JavaAstContainter<MethodDeclaration> remoteGetter = this.createGetter(type, type, REMOTE);
        super.getVisitorContext().getContainerList().add(remoteGetter);
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, MdaModel<JavaModel> target) {

        String type = nabuccoComponent.nodeToken1.tokenImage;

        JavaAstContainter<MethodDeclaration> localGetter = this.createGetter(type + LOCAL, type, LOCAL);
        super.getVisitorContext().getContainerList().add(localGetter);

        JavaAstContainter<MethodDeclaration> remoteGetter = this.createGetter(type, type, REMOTE);
        super.getVisitorContext().getContainerList().add(remoteGetter);
    }

    /**
     * Create the delegating getter.
     * 
     * @param serviceName
     *            name of the service
     * @param serviceType
     *            type of the service
     * @param constant
     *            either remote or local suffix
     * 
     * @return the getter method
     */
    private JavaAstContainter<MethodDeclaration> createGetter(String serviceName, String serviceType, String constant) {

        try {
            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
            JavaAstMethod methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethod();

            String getterName = NabuccoTransformationUtility.toGetter(serviceName);

            MethodDeclaration getter = producer.createMethodDeclaration(getterName, null, false);
            methodFactory.setReturnType(getter, producer.createTypeReference(serviceType, false));

            methodFactory.addException(getter, producer.createTypeReference(SERVICE + EXCEPTION, false));

            String jndiNames = this.interfaceName + JNDI_CLASS_SUFFIX;
            constant = NabuccoTransformationUtility.toConstantName(serviceType + constant);

            List<Expression> arguments = new ArrayList<Expression>();
            arguments.add(producer.createQualifiedNameReference(jndiNames, constant));
            arguments.add(producer.createClassLiteralAccess(serviceType));

            SuperReference superReference = producer.createSuperReference();
            MessageSend lookup = producer.createMessageSend(LOOKUP, superReference, arguments);

            ReturnStatement returnStatement = producer.createReturnStatement(lookup);
            getter.statements = new Statement[] { returnStatement };

            Annotation override = producer.createAnnotation(ANNOTATION_OVERRIDE, null);
            methodFactory.addAnnotation(getter, override);

            JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(getter,
                    JavaAstType.METHOD);

            container.getImports().add(super.resolveImport(serviceType));

            return container;

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating local proxy getter.", jme);
        }
    }

}
