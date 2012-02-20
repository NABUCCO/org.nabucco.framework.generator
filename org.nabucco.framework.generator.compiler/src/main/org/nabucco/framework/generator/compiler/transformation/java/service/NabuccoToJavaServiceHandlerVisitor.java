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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoServiceType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaServiceHandlerVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceHandlerVisitor extends NabuccoToJavaVisitorSupport implements ServerConstants {

    private static final String DEFAULT_SERVICE_EXCEPTION = "org.nabucco.framework.base.facade.exception.service.ServiceException";

    private AnnotationDeclaration typeAnnotations;

    /**
     * Creates a new {@link NabuccoToJavaServiceHandlerVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param statementAnnotations
     *            the typeAnnotations
     */
    public NabuccoToJavaServiceHandlerVisitor(NabuccoToJavaVisitorContext visitorContext,
            AnnotationDeclaration statementAnnotations) {
        super(visitorContext);
        this.typeAnnotations = statementAnnotations;
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, MdaModel<JavaModel> target) {

        String methodName = nabuccoMethod.nodeToken1.tokenImage;
        String servicePackage = this.getVisitorContext().getPackage().replace(PKG_FACADE, PKG_IMPL);
        String handlerName = NabuccoToJavaServiceVisitorSupport.convertMethodToHandler(methodName);
        String componentName = super.getProjectName(NabuccoModelType.SERVICE, NabuccoModifierType.PRIVATE);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.SERVICE_HANDLER_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.SERVICE_HANDLER_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, handlerName);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), servicePackage);

            String exception = null;

            if (nabuccoMethod.nodeOptional.present()) {
                NodeSequence exceptionNode = (NodeSequence) nabuccoMethod.nodeOptional.node;
                exception = ((NodeToken) exceptionNode.nodes.get(1)).tokenImage;
                super.removeImport(unit.getUnitDeclaration(), DEFAULT_SERVICE_EXCEPTION);
            }

            String rq = this.getRequest(nabuccoMethod, unit.getUnitDeclaration());
            String rs = this.getResponse(nabuccoMethod, unit.getUnitDeclaration());

            NabuccoToJavaServiceVisitorSupport.prepareInvokeMethod(methodName, rq, rs, exception, type);

            JavaAstContainter<org.eclipse.jdt.internal.compiler.ast.MethodDeclaration> handlerMethod;
            handlerMethod = NabuccoToJavaServiceVisitorSupport.createServiceHandlerMethod(methodName, rq, rs,
                    exception, type);

            org.eclipse.jdt.internal.compiler.ast.MethodDeclaration method = handlerMethod.getAstNode();

            super.getVisitorContext().getContainerList().add(handlerMethod);

            // JavaDocAnnotations
            JavaAstSupport.convertJavadocAnnotations(this.typeAnnotations, type);
            JavaAstSupport.convertJavadocAnnotations(nabuccoMethod.annotationDeclaration, method);

            JavaAstSupport.convertAstNodes(unit, super.getVisitorContext().getContainerList(), this.getVisitorContext()
                    .getImportList());

            this.changeInjectionId(nabuccoMethod.annotationDeclaration, servicePackage, handlerName, type);

            List<String> imports = this.changeInheritance(type);
            for (String newImport : imports) {
                ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(newImport);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
            }
            
            if (!imports.isEmpty()) {
                super.removeImport(unit.getUnitDeclaration(), IMPORT_SERVICE_HANDLER_SUPPORT);
            }

            // File creation
            unit.setProjectName(componentName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST service modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template service processing.", te);
        }
    }

    /**
     * Create the service handler super class depending on the statement annotations.
     * 
     * @param type
     *            the java type
     * 
     * @return the list of imports to add
     * 
     * @throws JavaModelException
     *             when an error in java AST modification
     */
    private List<String> changeInheritance(TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        NabuccoServiceType serviceType = NabuccoServiceType.valueOf(this.typeAnnotations);

        List<String> importList = new ArrayList<String>();

        switch (serviceType) {

        case PERSISTENCE: {
            TypeReference superInterface = producer.createTypeReference(PERSISTENCE_SERVICE_HANDLER, false);
            TypeReference superType = producer.createTypeReference(PERSISTENCE_SERVICE_HANDLER_SUPPORT, false);
            javaFactory.getJavaAstType().addInterface(type, superInterface);
            javaFactory.getJavaAstType().setSuperClass(type, superType);

            importList.add(IMPORT_PERSISTENCE_SERVICE_HANDLER);
            importList.add(IMPORT_PERSISTENCE_SERVICE_HANDLER_SUPPORT);

            break;
        }

        case RESOURCE: {
            TypeReference superInterface = producer.createTypeReference(RESOURCE_SERVICE_HANDLER, false);
            TypeReference superType = producer.createTypeReference(RESOURCE_SERVICE_HANDLER_SUPPORT, false);
            javaFactory.getJavaAstType().addInterface(type, superInterface);
            javaFactory.getJavaAstType().setSuperClass(type, superType);

            importList.add(IMPORT_RESOURCE_SERVICE_HANDLER);
            importList.add(IMPORT_RESOURCE_SERVICE_HANDLER_SUPPORT);

            break;
        }

        }

        return importList;
    }

    /**
     * Resolves the service request message
     * 
     * @param method
     *            the service operation
     * @param unit
     *            compilation unit to add missing imports
     * 
     * @return the request as string
     * 
     * @throws JavaModelException
     *             if the import cannot be created
     */
    private String getRequest(MethodDeclaration method, CompilationUnitDeclaration unit) throws JavaModelException {

        if (method.parameterList.nodeListOptional.nodes.isEmpty()) {
            ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                    IMPORT_EMPTY_SERVICE_MESSAGE);

            JavaAstElementFactory.getInstance().getJavaAstUnit().addImport(unit, importReference);
            return EMPTY_SERVICE_MESSAGE;
        }
        Parameter param = (Parameter) method.parameterList.nodeListOptional.nodes.get(0);
        return param.nodeToken.tokenImage;
    }

    /**
     * Resolves the service response message.
     * 
     * @param method
     *            the service operation
     * @param unit
     *            compilation unit to add missing imports
     * 
     * @return the response as string
     * 
     * @throws JavaModelException
     *             if the import cannot be created
     */
    private String getResponse(MethodDeclaration method, CompilationUnitDeclaration unit) throws JavaModelException {
        String rs = ((NodeToken) method.nodeChoice.choice).tokenImage;

        if (rs == null || rs.equalsIgnoreCase(VOID)) {
            ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(
                    IMPORT_EMPTY_SERVICE_MESSAGE);

            JavaAstElementFactory.getInstance().getJavaAstUnit().addImport(unit, importReference);
            return EMPTY_SERVICE_MESSAGE;
        }

        return rs;
    }

    /**
     * Changes the static ID field
     * 
     * @param annotations
     *            the method annotations
     * @param pkg
     *            the package
     * @param name
     *            the name
     * @param type
     *            the type
     */
    private void changeInjectionId(AnnotationDeclaration annotations, String pkg, String name, TypeDeclaration type) {

        String id = pkg + PKG_SEPARATOR + name;

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
