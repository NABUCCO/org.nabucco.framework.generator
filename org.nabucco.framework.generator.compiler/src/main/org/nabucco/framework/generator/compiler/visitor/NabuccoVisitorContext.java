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
package org.nabucco.framework.generator.compiler.visitor;

import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;

import org.nabucco.framework.mda.model.visitor.MdaModelVisitorContext;

/**
 * NabuccoVisitorContext
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoVisitorContext extends MdaModelVisitorContext {

    private String rootDir;
    
    private String outDir;

    private String pkgString;

    private String nabuccoExtension;

    private NabuccoModifierType nabuccoModifier;

    private List<String> nabuccoImports = new ArrayList<String>();

    /**
     * Creates an empty {@link NabuccoVisitorContext} instance.
     */
    public NabuccoVisitorContext() {
    }

    /**
     * Creates a visitor context with all existing NABUCCO data of another context.
     * 
     * @param context
     *            another context.
     */
    public NabuccoVisitorContext(NabuccoVisitorContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Cannot clone visitor context [null].");
        }
        this.rootDir = context.getRootDir();
        this.outDir = context.getOutDir();
        this.pkgString = context.getPackage();
        this.nabuccoExtension = context.getNabuccoExtension();
        this.nabuccoModifier = context.getNabuccoModifier();
        this.nabuccoImports.addAll(context.getImportList());
    }

    /**
     * Getter for the root directory.
     * 
     * @return Returns the rootDir.
     */
    public String getRootDir() {
        return this.rootDir;
    }

    /**
     * Setter for the root directory.
     * 
     * @param rootDir
     *            The rootDir to set.
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }
    
    /**
     * Getter for the out directory.
     * 
     * @return Returns the outDir.
     */
    public String getOutDir() {
        return this.outDir;
    }
    
    /**
     * Setter for the out directory.
     * 
     * @param outDir The outDir to set.
     */
    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    /**
     * Getter for the package string.
     * 
     * @return Returns the package as string.
     */
    public String getPackage() {
        return this.pkgString;
    }

    /**
     * Getter for the nabuccoModifier.
     * 
     * @return Returns the nabuccoModifier.
     */
    public NabuccoModifierType getNabuccoModifier() {
        return this.nabuccoModifier;
    }

    /**
     * Setter for the nabuccoModifier.
     * 
     * @param nabuccoModifier
     *            The nabuccoModifier to set.
     */
    public void setNabuccoModifier(NabuccoModifierType modifier) {
        this.nabuccoModifier = modifier;
    }

    /**
     * Getter for the NABUCCO extension.
     * 
     * @return Returns the nabuccoExtension.
     */
    public String getNabuccoExtension() {
        return this.nabuccoExtension;
    }

    /**
     * Setter for the NABUCCO extension.
     * 
     * @param nabuccoExtension
     *            The nabuccoExtension to set.
     */
    public void setNabuccoExtension(String nabuccoExtension) {
        this.nabuccoExtension = nabuccoExtension;
    }

    /**
     * Setter for the package as string.
     * 
     * @param pkg
     *            the package as string
     */
    public void setPackage(String pkg) {
        this.pkgString = pkg;
    }

    /**
     * Getter for the import list.
     * 
     * @return Returns the nabuccoImports.
     */
    public List<String> getImportList() {
        return this.nabuccoImports;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n - Package=");
        builder.append(this.pkgString);
        builder.append("\n - Extends=");
        builder.append(this.nabuccoExtension);
        builder.append("\n - Imports=");
        builder.append(this.nabuccoImports.size());
        return builder.toString();
    }

}
