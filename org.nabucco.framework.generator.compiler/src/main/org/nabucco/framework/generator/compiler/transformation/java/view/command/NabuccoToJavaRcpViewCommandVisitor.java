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
package org.nabucco.framework.generator.compiler.transformation.java.view.command;

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewVisitorSupportUtil;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.CommandStatement;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
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
 * NabuccoToJavaRcpViewCommandVisitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class NabuccoToJavaRcpViewCommandVisitor extends NabuccoToJavaVisitorSupport implements ViewConstants {

    private static String INJECTOR_FQN = "org.nabucco.framework.base.facade.component.injector.NabuccoInjector";

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaRcpViewCommandVisitor.class);

    private String methodName = null;

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewCommandVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(CommandStatement commandStatement, MdaModel<JavaModel> target) {
        // children first
        super.visit(commandStatement, target);
        String pkg = super.getVisitorContext().getPackage().replace("ui", "ui.rcp");
        String name = commandStatement.nodeToken2.tokenImage;
        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.VIEW_COMMAND_TEMAPLTE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.VIEW_COMMAND_TEMAPLTE);
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(commandStatement.annotationDeclaration, type);

            changeRunMethod(type);

            JavaCompilationUnit uIUnit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            TypeDeclaration uIType = uIUnit.getType(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            getVisitorContext().getContainerList().addAll(
                    NabuccoToJavaRcpViewVisitorSupportUtil.getUiCommonElements(uIType, type,
                            commandStatement.annotationDeclaration));
            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                    .getImportList());
            javaFactory.getJavaAstUnit().addImport(
                    unit.getUnitDeclaration(),
                    addFieldInit((FieldDeclaration) super.getVisitorContext().getContainerList().get(0).getAstNode(),
                            name));

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST datatype modification.");
            throw new NabuccoVisitorException("Error during Java AST View Command modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template datatype processing.");
            throw new NabuccoVisitorException("Error during Java template View Command processing.", te);
        }

    }

    /**
     * @param astNode
     * @throws JavaModelException
     */
    private ImportReference addFieldInit(FieldDeclaration fieldDeclaration, String typeName) throws JavaModelException {
        String injectorType = INJECTOR_FQN.substring(INJECTOR_FQN.lastIndexOf('.') + 1);
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        ClassLiteralAccess classType = producer.createClassLiteralAccess(typeName);
        ClassLiteralAccess fieldType = producer.createClassLiteralAccess(fieldDeclaration.type);
        SingleNameReference injector = producer.createSingleNameReference(injectorType);
        MessageSend firstCall = producer.createMessageSend("getInstance", injector, Arrays.asList(classType));
        MessageSend secondCall = producer.createMessageSend("inject", firstCall, Arrays.asList(fieldType));
        fieldDeclaration.initialization = secondCall;

        return producer.createImportReference(INJECTOR_FQN);
    }

    /**
     * @param type
     * @throws JavaModelException
     */
    private void changeRunMethod(TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        org.eclipse.jdt.internal.compiler.ast.MethodDeclaration runMethod = (org.eclipse.jdt.internal.compiler.ast.MethodDeclaration) javaFactory
                .getJavaAstType().getMethod(type, new JavaAstMethodSignature("run", new String[] {}));
        ((SingleNameReference) ((MessageSend) runMethod.statements[0]).receiver).token = (methodName + HANDLER)
                .toCharArray();
        ((MessageSend) runMethod.statements[0]).selector = methodName.toCharArray();

    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, MdaModel<JavaModel> target) {

        super.visit(methodDeclaration, target);

        this.methodName = methodDeclaration.nodeToken1.tokenImage;

        String name = this.methodName + HANDLER;
        String type = NabuccoTransformationUtility.firstToUpper(name);

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name, NabuccoModifierType.PRIVATE);

        super.getVisitorContext().getContainerList().add(field);
    }

}
