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
package org.nabucco.framework.generator.compiler.transformation.util.dependency;

import java.io.File;
import java.util.List;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.path.NabuccoPath;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.path.NabuccoPathEntry;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.path.NbcPathParser;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.reader.NabuccoArchiveReader;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.reader.NabuccoProjectReader;
import org.nabucco.framework.generator.parser.file.NabuccoFileConstants;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoDependencySupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
final class NabuccoDependencySupport implements NabuccoTransformationConstants {

    /** Package of the initial generated component. */
    private static String componentInFocus = null;

    /** NabuccoPath of the current transformation (must be cleared after). */
    private static NabuccoPath nabuccoPath = null;

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoDependencySupport() {
    }

    /**
     * Load a model by an import string.
     * 
     * @param rootDir
     *            the root directory
     * @param pkg
     *            package of the owning datatype
     * @param importString
     *            the import string
     * @param outDir
     *            the out directory
     * 
     * @return the loaded model
     * 
     * @throws NabuccoTransformationException
     *             if the model cannot be loaded
     */
    public static synchronized MdaModel<NabuccoModel> loadModel(String rootDir, String pkg, String importString,
            String outDir) throws NabuccoTransformationException {

        MdaModel<NabuccoModel> model = NabuccoDependencyContainer.getInstance().getModel(importString);

        if (model != null) {
            return model;
        }

        if (componentInFocus == null) {
            componentInFocus = NabuccoCompilerSupport.getParentComponentName(pkg);
        }

        StringBuilder componentPath = NabuccoDependencySupport.resolveComponentPath(rootDir);

        if (nabuccoPath == null) {
            String nbcPath = componentPath + NabuccoFileConstants.NBCPATH_XML;
            nabuccoPath = NbcPathParser.getElementsByXPath(nbcPath);
        }

        boolean otherComponent = NabuccoCompilerSupport.isOtherComponent(componentInFocus, importString);

        if (otherComponent) {

            List<NabuccoPathEntry> pathEntries = nabuccoPath.getPathEntries();

            for (NabuccoPathEntry pathEntry : pathEntries) {

                NabuccoModelResourceType entryType = pathEntry.getType();
                String location = componentPath + pathEntry.getLocation();

                switch (entryType) {

                case ARCHIVE: {
                    NabuccoArchiveReader reader = new NabuccoArchiveReader(location);
                    model = reader.read(importString);
                    break;
                }

                case PROJECT: {
                    NabuccoProjectReader reader = new NabuccoProjectReader(location, outDir);
                    model = reader.read(importString);
                    break;
                }

                default:
                    throw new IllegalStateException("NbcPath Entry is not supported: " + entryType + ".");
                }

                if (model != null) {
                    return model;
                }
            }

        } else {
            StringBuilder path = new StringBuilder();
            path.append(rootDir);
            path.append(File.separatorChar);
            path.append(NabuccoCompilerSupport.getParentComponentName(importString));

            NabuccoProjectReader reader = new NabuccoProjectReader(path.toString(), outDir);
            model = reader.read(importString);
        }

        if (model != null) {
            return model;
        }

        if (otherComponent) {
            throw new NabuccoTransformationException("Cannot resolve import '"
                    + importString + "'. Missing entry in nbcPath.xml.");
        }

        throw new NabuccoTransformationException("Cannot resolve import '"
                + importString + "' in project " + rootDir + ".");
    }

    /**
     * Resolves the component path depending on the root directory.
     * 
     * @param rootDir
     *            the root direcotry
     * 
     * @return the component path
     */
    private static StringBuilder resolveComponentPath(String rootDir) {
        StringBuilder componentPath = new StringBuilder();
        componentPath.append(rootDir);
        componentPath.append(File.separatorChar);
        componentPath.append(componentInFocus);
        componentPath.append(File.separatorChar);
        return componentPath;
    }

    /**
     * Clears the dependency cache and NBC Path.
     */
    public static synchronized void clearCache() {
        NabuccoDependencyContainer.getInstance().clear();
        componentInFocus = null;
        nabuccoPath = null;
    }

}
