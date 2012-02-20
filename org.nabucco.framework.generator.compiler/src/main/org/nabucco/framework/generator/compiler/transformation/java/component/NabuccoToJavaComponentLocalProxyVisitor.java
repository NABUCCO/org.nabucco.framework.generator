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

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethod;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaComponentLocalProxyVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaComponentLocalProxyVisitor extends NabuccoToJavaVisitorSupport {

    private static final String ANNOTATION_OVERRIDE = "Override";

    private static final String DELEGATE_FIELD = "delegate";

    /**
     * Creates a new {@link NabuccoToJavaComponentLocalProxyVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaComponentLocalProxyVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoComponent, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String interfaceName = nabuccoComponent.nodeToken2.tokenImage;
        String localInterface = interfaceName + ServerConstants.LOCAL;

        String pkg = this.getVisitorContext().getPackage();
        String name = localInterface + ServerConstants.PROXY;

        String projectName = super.getProjectName(NabuccoModelType.COMPONENT,
                NabuccoModifierComponentMapper.getModifierType(nabuccoComponent.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMPONENT_LOCAL_PROXY_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.COMPONENT_LOCAL_PROXY_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // Super-classes
            super.createInterface(interfaceName);

            this.prepareDelegate(localInterface, type);

            // Javadoc
            JavaAstSupport.convertJavadocAnnotations(nabuccoComponent.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

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
     * Modify the delegate type.
     * 
     * @param interfaceName
     *            name of the local interface
     * @param type
     *            the type declaration
     * 
     * @throws JavaModelException
     */
    private void prepareDelegate(String interfaceName, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        FieldDeclaration delegate = javaFactory.getJavaAstType().getField(type, DELEGATE_FIELD);
        delegate.type = producer.createTypeReference(interfaceName, false);

        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructors(type).get(0);
        Argument argument = constructor.arguments[0];
        javaFactory.getJavaAstArgument().setType(argument, producer.createTypeReference(interfaceName, false));
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<JavaModel> target) {

        String type = nabuccoService.nodeToken1.tokenImage;
        String name = NabuccoTransformationUtility.firstToLower(type);

        JavaAstContainter<MethodDeclaration> getter = this.createGetter(name, type);
        super.getVisitorContext().getContainerList().add(getter);
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, MdaModel<JavaModel> target) {

        String type = nabuccoComponent.nodeToken1.tokenImage;
        String name = NabuccoTransformationUtility.firstToLower(type);

        JavaAstContainter<MethodDeclaration> getter = this.createGetter(name, type);
        super.getVisitorContext().getContainerList().add(getter);
    }

    /**
     * Create the delegating getter.
     * 
     * @param serviceName
     *            name of the service
     * @param serviceType
     *            type of the service
     * 
     * @return the getter method
     */
    private JavaAstContainter<MethodDeclaration> createGetter(String serviceName, String serviceType) {

        try {
            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
            JavaAstMethod methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethod();

            String getterName = NabuccoTransformationUtility.toGetter(serviceName);

            MethodDeclaration getter = producer.createMethodDeclaration(getterName, null, false);
            methodFactory.setReturnType(getter, producer.createTypeReference(serviceType, false));

            methodFactory.addException(getter, producer.createTypeReference(SERVICE + EXCEPTION, false));

            FieldReference fieldReference = producer.createFieldThisReference(DELEGATE_FIELD);
            MessageSend delegate = producer.createMessageSend(getterName + ServerConstants.LOCAL, fieldReference, null);

            ReturnStatement returnStatement = producer.createReturnStatement(delegate);
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
