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
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;

import org.nabucco.framework.mda.model.MdaModel;

/**
 * TraversingNabuccoToJavaVisitor
 * <p/>
 * Visitor for traversing over multiple NABUCCO models recursively.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class TraversingNabuccoToJavaVisitor<A> extends GJVoidDepthFirst<A> {

    private NabuccoToJavaVisitorContext context;

    /**
     * Creates a new {@link TraversingNabuccoToJavaVisitor} instance.
     * 
     * @param context
     *            the visitor context
     */
    public TraversingNabuccoToJavaVisitor(NabuccoToJavaVisitorContext context) {
        this.context = context;
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, A argument) {
        String packageString = nabuccoPackage.nodeToken1.tokenImage;
        this.context.setPackage(packageString);
        super.visit(nabuccoPackage, argument);
    }

    @Override
    public void visit(ImportDeclaration nabuccoImport, A argument) {
        String importName = nabuccoImport.nodeToken1.tokenImage;
        this.context.getImportList().add(importName);
        super.visit(nabuccoImport, argument);
    }

    /**
     * Starts visiting the sub elements.
     * 
     * @param type
     *            the type to visit into
     * @param argument
     *            the argument to pass
     */
    protected void subVisit(String type, A argument) {

        String importString = resolveImport(type);

        try {
            MdaModel<NabuccoModel> model = NabuccoDependencyResolver.getInstance()
                    .resolveDependency(this.context, this.context.getPackage(), importString);
            
            model.getModel().getUnit().accept(this, argument);
            
        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException(e);
        }
    }

    /**
     * Resolves the import of a given type.
     * 
     * @param type
     *            the type to resolve
     * 
     * @return the resolved import
     */
    protected String resolveImport(String type) {
        return NabuccoToJavaVisitorSupport.resolveImport(type, this.context);
    }

    /**
     * Getter for the visitor context.
     * 
     * @return the visitor context
     */
    protected NabuccoToJavaVisitorContext getContext() {
        return this.context;
    }

}
