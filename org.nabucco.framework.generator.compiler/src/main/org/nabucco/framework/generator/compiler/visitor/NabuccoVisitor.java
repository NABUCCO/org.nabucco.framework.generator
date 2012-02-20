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
package org.nabucco.framework.generator.compiler.visitor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformation;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModelTypeComponentMapper;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.util.NabuccoPropertyKey;
import org.nabucco.framework.generator.compiler.visitor.util.NabuccoPropertyVisitor;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelImplementation;

/**
 * NabuccoVisitor
 * <p/>
 * Visitor for NABUCCO syntax-tree elements.
 * 
 * @see NabuccoTransformation
 * @see NabuccoVisitorContext
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoVisitor<M extends MdaModel<? extends ModelImplementation>, C extends NabuccoVisitorContext>
        extends GJVoidDepthFirst<M> implements NabuccoTransformationConstants {

    private final C visitorContext;

    private NabuccoUnit root;

    /**
     * Creates a new {@link NabuccoVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoVisitor(C visitorContext) {
        this.visitorContext = visitorContext;
    }

    /**
     * Getter for the visitor context.
     * 
     * @return Returns the visitorContext.
     */
    protected C getVisitorContext() {
        return visitorContext;
    }

    @Override
    public void visit(NabuccoUnit nabuccoUnit, M target) {
        this.root = nabuccoUnit;

        super.visit(nabuccoUnit, target);
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, M target) {
        String packageString = nabuccoPackage.nodeToken1.tokenImage;
        this.getVisitorContext().setPackage(packageString);
        super.visit(nabuccoPackage, target);
    }

    @Override
    public void visit(ImportDeclaration nabuccoImport, M target) {
        String importName = nabuccoImport.nodeToken1.tokenImage;
        this.getVisitorContext().getImportList().add(importName);
        super.visit(nabuccoImport, target);
    }

    @Override
    public void visit(ExtensionDeclaration nabuccoExtension, M target) {
        String extendedTypeName = ((NodeToken) nabuccoExtension.nodeChoice.choice).tokenImage;
        this.getVisitorContext().setNabuccoExtension(extendedTypeName);
        super.visit(nabuccoExtension, target);
    }

    /**
     * Getter for the nabucco unit.
     * 
     * @return Returns the root node.
     */
    public NabuccoUnit getRoot() {
        return this.root;
    }

    /**
     * Resolves an import for a related NABUCCO type.
     * 
     * @param type
     *            the NABUCCO type
     * 
     * @return the import string
     */
    protected String resolveImport(String type) {
        return resolveImport(type, this.getVisitorContext());
    }

    /**
     * Getter for the component project name defined by model type and modifier.
     * 
     * @param modelType
     *            the model type
     * @param modifier
     *            the modifier
     * 
     * @return the project name
     * 
     * @throws NabuccoVisitorException
     */
    protected String getProjectName(NabuccoModelType modelType, NabuccoModifierType modifier)
            throws NabuccoVisitorException {

        StringBuilder path = new StringBuilder();
        path.append(NabuccoCompilerSupport.getParentComponentName(this.getVisitorContext().getPackage()));

        if (modelType != null) {

            if (modifier != null && modelType.isComponent()) {
                path.append(PKG_SEPARATOR);
                path.append(NabuccoModifierComponentMapper.mapModifierToProjectString(modifier));
            }

            path.append(PKG_SEPARATOR);
            path.append(NabuccoModelTypeComponentMapper.mapModelToProjectString(modelType));
        }

        return path.toString();
    }

    /**
     * Getter for the parent model.
     * 
     * @return the parent model, or null if none exists
     * 
     * @throws NabuccoTransformationException
     *             when the parent cannot be resolved
     */
    public NabuccoModel getParent() throws NabuccoVisitorException {
        String extension = this.visitorContext.getNabuccoExtension();
        if (extension == null) {
            return null;
        }

        String extensionImport = this.resolveImport(extension);
        if (extensionImport == null) {
            return null;
        }

        try {
            MdaModel<NabuccoModel> parent = NabuccoDependencyResolver.getInstance().resolveDependency(
                    this.visitorContext, this.visitorContext.getPackage(), extensionImport);

            if (parent != null) {
                return parent.getModel();
            }

            return null;

        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException(e);
        }
    }

    /**
     * Collect all properties of the NABUCO source model. The result contains all
     * {@link BasetypeDeclaration}, {@link EnumerationDeclaration} and {@link DatatypeDeclaration}
     * instances of the model.
     * 
     * @return the map of nabucco properties by their qualified name
     */
    public Map<NabuccoPropertyKey, Node> getProperties() {
        Map<NabuccoPropertyKey, Node> properties = new LinkedHashMap<NabuccoPropertyKey, Node>();

        NabuccoModel parent = this.getParent();
        if (parent != null && parent.getUnit() != null) {
            NabuccoPropertyVisitor propertyVisitor = new NabuccoPropertyVisitor(this.visitorContext.getRootDir(),
                    this.visitorContext.getOutDir());
            parent.getUnit().accept(propertyVisitor, properties);
        }

        return properties;
    }

    /**
     * Resolves the component name of a client type
     * 
     * @param type
     *            type of the client
     * 
     * @return the client component (project) name
     */
    public String getComponentName(NabuccoClientType type) {

        StringBuilder path = new StringBuilder();
        path.append(NabuccoCompilerSupport.getParentComponentName(this.getVisitorContext().getPackage()));

        path.append(PKG_SEPARATOR);
        path.append(PKG_UI);
        path.append(PKG_SEPARATOR);

        if (type == NabuccoClientType.RCP) {
            path.append(PKG_RCP);
        } else if (type == NabuccoClientType.WEB) {
            path.append(PKG_WEB);
        }

        return path.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("NABUCCO Visitor:\n");
        result.append(this.getVisitorContext().toString());
        return result.toString();
    }

    /**
     * Resolves the import of type for a given visitor context.
     * 
     * @param <C>
     *            the visitor context type
     * @param type
     *            the type to resolve
     * @param context
     *            the visitor context instance
     * 
     * @return the resolved import
     */
    public static <C extends NabuccoVisitorContext> String resolveImport(String type, C context) {
        return resolveImport(type, context.getPackage(), context.getImportList());
    }

    /**
     * Resolves the import of type for a given package and imports.
     * 
     * @param type
     *            the type to resolve
     * @param pkg
     *            the current package
     * @param imports
     *            all existing imports
     * 
     * @return the resolved import
     */
    public static String resolveImport(String type, String pkg, Collection<String> imports) {

        String importString = null;

        for (String nabuccoImport : imports) {
            if (nabuccoImport.endsWith(type)) {
                String[] importToken = nabuccoImport.split("\\.");
                if (importToken[importToken.length - 1].equals(type)) {
                    importString = nabuccoImport;
                    break;
                }
            }
        }

        if (importString == null) {
            importString = pkg + PKG_SEPARATOR + type;
        }
        return importString;
    }

}
