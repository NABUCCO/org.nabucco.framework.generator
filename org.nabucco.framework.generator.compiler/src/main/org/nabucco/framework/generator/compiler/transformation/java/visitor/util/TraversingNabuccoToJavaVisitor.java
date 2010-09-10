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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;

import org.nabucco.framework.mda.model.MdaModel;

/**
 * TraversingNabuccoToJavaVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class TraversingNabuccoToJavaVisitor<A> extends GJVoidDepthFirst<A> {

    private NabuccoToJavaVisitorContext context;

    public TraversingNabuccoToJavaVisitor(NabuccoToJavaVisitorContext context) {
        this.context = context;
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, A argument) {
        String packageString = nabuccoPackage.nodeToken1.tokenImage;
        context.setPackage(packageString);
        super.visit(nabuccoPackage, argument);
    }

    @Override
    public void visit(ImportDeclaration nabuccoImport, A argument) {
        String importName = nabuccoImport.nodeToken1.tokenImage;
        context.getImportList().add(importName);
        super.visit(nabuccoImport, argument);
    }

    protected void subVisit(String type, A argument) {

        String importString = resolveImport(type);

        try {

            MdaModel<NabuccoModel> model = NabuccoDependencyResolver.getInstance().resolveDependency(context,
                    context.getPackage(), importString);
            model.getModel().getUnit().accept(this, argument);
        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException(e);
        }
    }

    protected String resolveImport(String type) {
        String importString = null;

        for (String nabuccoImport : this.context.getImportList()) {
            if (nabuccoImport.endsWith(type)) {
                String[] importToken = nabuccoImport.split("\\.");
                if (importToken[importToken.length - 1].equals(type)) {
                    importString = nabuccoImport;
                }
            }
        }

        if (importString == null) {
            importString = this.context.getPackage() + "." + type;
        }
        return importString;
    }

    protected NabuccoToJavaVisitorContext getContext() {
        return this.context;
    }

}
