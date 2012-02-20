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
package org.nabucco.framework.generator.compiler.transformation.java.view.list;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaRcpViewVisitorSupportUtil;
import org.nabucco.framework.generator.compiler.transformation.java.view.browsersupport.BrowserElementSupport;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.syntaxtree.ListViewStatement;
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
 * NabuccoToJavaRcpViewEditVisitor
 * 
 * @author Stefanie Feld, Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaRcpViewListVisitor extends NabuccoToJavaVisitorSupport implements ViewConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoToJavaRcpViewListVisitor.class);

    /**
     * @param visitorContext
     */
    public NabuccoToJavaRcpViewListVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ListViewStatement nabuccoListView, MdaModel<JavaModel> target) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoListView.nodeToken2.tokenImage;
        String pkg = super.getVisitorContext().getPackage().replace(ViewConstants.UI, ViewConstants.UI_RCP)
                + ViewConstants.PKG_SEPARATOR + ViewConstants.VIEW_PACKAGE;

        String projectName = super.getComponentName(NabuccoClientType.RCP);
        try {

            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.LIST_VIEW_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.LIST_VIEW_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // change model in constructor
            JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

            // select the constructor
            JavaAstMethodSignature signature = new JavaAstMethodSignature(name);
            ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type, signature);
            TypeReference nameReference = jamp.createTypeReference(name + MODEL, false);

            TypeReference superClass = javaFactory.getJavaAstType().getSuperClass(type);
            superClass = javaFactory.getJavaAstReference().getAsParameterized(superClass,
                    new TypeReference[] { nameReference });
            javaFactory.getJavaAstType().setSuperClass(type, superClass);

            AllocationExpression expression = jamp.createAllocationExpression(nameReference, null);

            Assignment statement = (Assignment) constructor.statements[0];
            statement.expression = expression;

            // add import of editViewModel
            String importString = pkg.replace(VIEW_PACKAGE, MODEL_PACKAGE) + PKG_SEPARATOR + name + MODEL;
            BrowserElementSupport.addImport(importString, unit);

            // add Id as static final field and the getter for it

            JavaCompilationUnit uIUnit = super.extractAst(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            TypeDeclaration uIType = uIUnit.getType(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            super.getVisitorContext()
                    .getContainerList()
                    .addAll(NabuccoToJavaRcpViewVisitorSupportUtil.getUiCommonElements(uIType, type,
                            nabuccoListView.annotationDeclaration));

            JavaAstSupport.convertAstNodes(unit, getVisitorContext().getContainerList(), getVisitorContext()
                    .getImportList());

            // swap order of public static final fields ID and TITLE
            NabuccoToJavaRcpViewVisitorSupportUtil.swapFieldOrder(type);

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoListView.annotationDeclaration, type);

            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST listview modification.");
            throw new NabuccoVisitorException("Error during Java AST listview modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template listview processing.");
            throw new NabuccoVisitorException("Error during Java template listview processing.", te);
        }
    }

}
