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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * StructuredPropertyPathBuilder
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class StructuredPropertyPath extends StructuredPropertyPathSupport {

    private StructuredPropertyPathEntry rootEntry;

    /**
     * @param context
     */
    public StructuredPropertyPath(NabuccoToJavaVisitorContext context) {
        super(context);
        rootEntry = new StructuredPropertyPathEntry();
    }

    @Override
    public void visit(ExtensionDeclaration n, StructuredPropertyPathEntry argu) {
        super.subVisit(((NodeToken) n.nodeChoice.choice).tokenImage, argu);
        super.visit(n, argu);
    }

    @Override
    public void visit(BasetypeDeclaration n, StructuredPropertyPathEntry argu) {
        argu.put(n.nodeToken3.tokenImage,
                new StructuredPropertyPathEntry(n, super.resolveImport(n.nodeToken1.tokenImage)));
        super.visit(n, argu);
    }

    @Override
    public void visit(DatatypeDeclaration n, StructuredPropertyPathEntry argu) {
        StructuredPropertyPathEntry structuredPropertyPathEntry = new StructuredPropertyPathEntry(n,
                super.resolveImport(((NodeToken) n.nodeChoice1.choice).tokenImage));
        argu.put(n.nodeToken2.tokenImage, structuredPropertyPathEntry);
        try {
            NabuccoDependencyResolver
                    .getInstance()
                    .resolveDependency(getContext(), getContext().getPackage(),
                            super.resolveImport(((NodeToken) n.nodeChoice1.choice).tokenImage)).getModel().getUnit()
                    .accept(this, structuredPropertyPathEntry);
        } catch (NabuccoTransformationException e) {

            e.printStackTrace();
        }
        super.visit(n, argu);
    }

    @Override
    public void visit(EnumerationDeclaration n, StructuredPropertyPathEntry argu) {
        argu.put(n.nodeToken2.tokenImage,
                new StructuredPropertyPathEntry(n, super.resolveImport(((NodeToken) n.nodeChoice1.choice).tokenImage)));
        super.visit(n, argu);
    }

    /**
     * Getter for the RootEntry in the StructuredPropertyPath.
     * 
     * @return the root element of the StructuredPropertyPath
     */
    public StructuredPropertyPathEntry getRootEntry() {
        return this.rootEntry;
    }

}
