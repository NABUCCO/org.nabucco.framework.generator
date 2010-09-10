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
package org.nabucco.framework.generator.compiler.transformation.java.view.command;

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaRcpViewCommandHandlerVisitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class NabuccoToJavaRcpViewCommandHandlerVisitor extends NabuccoToJavaVisitorSupport {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaRcpViewCommandHandlerVisitor.class);

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewCommandHandlerVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, MdaModel<JavaModel> target) {
        super.visit(methodDeclaration, target);
        String pkg = super.getVisitorContext().getPackage().replace("ui", "ui.rcp");
        String name = NabuccoTransformationUtility
                .firstToUpper(methodDeclaration.nodeToken1.tokenImage)
                + NabuccoJavaTemplateConstants.HANDLER;
        String projectName = super.getComponentName(NabuccoClientType.RCP);

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.VIEW_COMMAND_HANDLER_TEMAPLTE);
            TypeDeclaration type = unit
                    .getType(NabuccoJavaTemplateConstants.VIEW_COMMAND_HANDLER_TEMAPLTE);
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            handleMethodRenaming(type, methodDeclaration);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(methodDeclaration.annotationDeclaration, type);

            unit.setProjectName(projectName);

            unit.setSourceFolder(super.getSourceFolder());
            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST datatype modification.");
            throw new NabuccoVisitorException("Error during Java AST Command method modification.",
                    jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template datatype processing.");
            throw new NabuccoVisitorException(
                    "Error during Java template Command method processing.", te);
        }
    }

    /**
     * @param type
     * @throws JavaModelException
     */
    private void handleMethodRenaming(TypeDeclaration type, MethodDeclaration methodDeclaration)
            throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        org.eclipse.jdt.internal.compiler.ast.MethodDeclaration commandMethod = (org.eclipse.jdt.internal.compiler.ast.MethodDeclaration) javaFactory
                .getJavaAstType().getMethod(type,
                        new JavaAstMethodSignature("templateMethod", new String[] {}));
        javaFactory.getJavaAstMethod().setMethodName(commandMethod,
                methodDeclaration.nodeToken1.tokenImage);
    }

}
