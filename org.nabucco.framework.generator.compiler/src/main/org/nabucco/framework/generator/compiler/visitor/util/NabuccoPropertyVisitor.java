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
package org.nabucco.framework.generator.compiler.visitor.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitor;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoPropertyVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoPropertyVisitor extends GJVoidDepthFirst<Map<NabuccoPropertyKey, Node>> {

    private String rootDir;

    private String outDir;

    private String pkg;

    private Set<String> imports = new HashSet<String>();

    /**
     * Creates a new {@link NabuccoPropertyVisitor} instance.
     * 
     * @param rootDir
     *            the root directory
     * @param outDir
     *            the out directory
     */
    public NabuccoPropertyVisitor(String rootDir, String outDir) {
        this.rootDir = rootDir;
        this.outDir = outDir;
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, Map<NabuccoPropertyKey, Node> properties) {
        this.pkg = nabuccoPackage.nodeToken1.tokenImage;
        super.visit(nabuccoPackage, properties);
    }

    @Override
    public void visit(ImportDeclaration nabuccoImport, Map<NabuccoPropertyKey, Node> properties) {
        String importName = nabuccoImport.nodeToken1.tokenImage;
        this.imports.add(importName);
        super.visit(nabuccoImport, properties);
    }

    @Override
    public void visit(ExtensionDeclaration nabuccoExtension, Map<NabuccoPropertyKey, Node> properties) {
        super.visit(nabuccoExtension, properties);

        String extension = ((NodeToken) nabuccoExtension.nodeChoice.choice).tokenImage;

        try {
            String importString = NabuccoVisitor.resolveImport(extension, this.pkg, this.imports);
            MdaModel<NabuccoModel> model = NabuccoDependencyResolver.getInstance().resolveDependency(this.rootDir,
                    extension, importString, this.outDir);

            if (model != null && model.getModel() != null) {
                NabuccoPropertyVisitor visitor = new NabuccoPropertyVisitor(this.rootDir, this.outDir);
                model.getModel().getUnit().accept(visitor, properties);
            }

        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException("Error collecting NABUCCO properties.", e);
        }

    }

    @Override
    public void visit(BasetypeDeclaration basetype, Map<NabuccoPropertyKey, Node> properties) {
        String type = basetype.nodeToken1.tokenImage;
        String qualifiedType = NabuccoToJavaVisitorSupport.resolveImport(type, this.pkg, this.imports);
        String name = basetype.nodeToken3.tokenImage;

        properties.put(new NabuccoPropertyKey(qualifiedType, name), basetype);
    }

    @Override
    public void visit(EnumerationDeclaration enumeration, Map<NabuccoPropertyKey, Node> properties) {
        String type = ((NodeToken) enumeration.nodeChoice1.choice).tokenImage;
        String qualifiedType = NabuccoToJavaVisitorSupport.resolveImport(type, this.pkg, this.imports);
        String name = enumeration.nodeToken2.tokenImage;

        properties.put(new NabuccoPropertyKey(qualifiedType, name), enumeration);
    }

    @Override
    public void visit(DatatypeDeclaration datatype, Map<NabuccoPropertyKey, Node> properties) {
        String type = ((NodeToken) datatype.nodeChoice1.choice).tokenImage;
        String qualifiedType = NabuccoToJavaVisitorSupport.resolveImport(type, this.pkg, this.imports);
        String name = datatype.nodeToken2.tokenImage;

        properties.put(new NabuccoPropertyKey(qualifiedType, name), datatype);
    }

}
