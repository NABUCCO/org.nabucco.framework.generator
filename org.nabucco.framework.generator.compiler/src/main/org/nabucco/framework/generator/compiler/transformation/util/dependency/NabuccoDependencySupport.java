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
package org.nabucco.framework.generator.compiler.transformation.util.dependency;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.parser.file.NabuccoFile;
import org.nabucco.framework.generator.parser.file.NabuccoFileConstants;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelLoader;
import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;
import org.nabucco.framework.generator.parser.model.serializer.NabuccoModelSerializer;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelException;

/**
 * NabuccoDependencySupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
final class NabuccoDependencySupport implements NabuccoTransformationConstants {

    /** The backslash character. */
    private static final String BACK_SLASH = "\\";

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
    public static synchronized MdaModel<NabuccoModel> loadModel(String rootDir, String pkg,
            String importString, String outDir) throws NabuccoTransformationException {

        MdaModel<NabuccoModel> model = NabuccoDependencyContainer.getInstance().getModel(
                importString);

        if (model != null) {
            return model;
        }

        if (componentInFocus == null) {
            componentInFocus = NabuccoCompilerSupport.getParentComponentName(pkg);
        }

        StringBuilder componentPath = new StringBuilder();
        componentPath.append(rootDir);
        componentPath.append(File.separatorChar);
        componentPath.append(componentInFocus);
        componentPath.append(File.separatorChar);

        if (nabuccoPath == null) {
            String nbcPath = componentPath + NabuccoFileConstants.NBCPATH_XML;
            nabuccoPath = NbcPathParser.getElementsByXPath(nbcPath);
        }

        boolean otherComponent = NabuccoCompilerSupport.isOtherComponent(componentInFocus,
                importString);

        if (otherComponent) {

            List<NabuccoPathEntry> pathEntries = nabuccoPath.getPathEntries();

            for (NabuccoPathEntry pathEntry : pathEntries) {

                NabuccoModelResourceType entryType = pathEntry.getType();
                String location = componentPath + pathEntry.getLocation();

                switch (entryType) {

                case ARCHIVE:
                    model = readFromArchive(importString, location);
                    break;

                case PROJECT:
                    model = readFromProject(rootDir, importString, outDir, location);
                    break;

                default:
                    throw new IllegalStateException("NbcPath Entry is not supported: "
                            + entryType + ".");
                }

                if (model != null) {
                    return model;
                }

            }
        } else {
            model = readFromProject(rootDir, importString, outDir, null);
        }

        if (model == null) {
            if (otherComponent) {
                throw new NabuccoTransformationException("Cannot resolve import '"
                        + importString + "'. Missing entry in nbcPath.xml.");
            }
            throw new NabuccoTransformationException("Cannot resolve import '"
                    + importString + "' in project " + rootDir + ".");
        }
        return model;
    }

    /**
     * Clears the dependency cache and NBC Path.
     */
    public static synchronized void clearCache() {
        NabuccoDependencyContainer.getInstance().clear();
        componentInFocus = null;
        nabuccoPath = null;
    }

    /**
     * Read from NABUCCO Archive (NAR) file (for project use
     * {@link NabuccoDependencySupport#readFromProject(String, String, String)} instead).
     * 
     * @param importString
     *            the import string
     * @param location
     *            the location
     * 
     * @return the read NABUCCO model
     * 
     * @throws NabuccoTransformationException
     *             if the NABUCCO archive cannot be read
     */
    private static MdaModel<NabuccoModel> readFromArchive(String importString, String location)
            throws NabuccoTransformationException {

        try {
            JarFile jarFile = new JarFile(location);
            String nbccPath = convertImportString(importString);
            String modelName = importString.substring(importString.lastIndexOf('.'));
            JarEntry entry = jarFile.getJarEntry(nbccPath);

            if (entry != null) {
                InputStream in = jarFile.getInputStream(entry);

                NabuccoModel nabuccoModel = NabuccoModelSerializer.getInstance()
                        .deserializeNabucco(modelName, in);

                nabuccoModel.setResourceType(NabuccoModelResourceType.ARCHIVE);

                return new MdaModel<NabuccoModel>(nabuccoModel);
            }
        } catch (IOException e) {
            throw new NabuccoTransformationException("Cannot read from NAR '" + location + "'.", e);
        } catch (ModelException e) {
            throw new NabuccoTransformationException("Cannot read from NAR '" + location + "'.", e);
        }

        return null;
    }

    /**
     * Converts an import string to a file path.
     * 
     * @param importString
     *            the import string
     * 
     * @return the file path
     */
    private static String convertImportString(String importString) {
        String nbccPath = importString.replace(PKG_SEPARATOR, JAR_SEPARATOR)
                .replace(BACK_SLASH, JAR_SEPARATOR).concat(NabuccoFileConstants.NBCC_SUFFIX);
        return nbccPath;
    }

    /**
     * Reads from a NABUCCO project (for NAR use
     * {@link NabuccoDependencySupport#readFromArchive(String, String)} instead).
     * 
     * @param rootDir
     *            the root directory
     * @param importString
     *            the import string
     * @param outDir
     *            the out directory
     * 
     * @return the read NABUCCO model
     * 
     * @throws NabuccoTransformationException
     *             if the file cannot be found
     */
    private static MdaModel<NabuccoModel> readFromProject(String rootDir, String importString,
            String outDir, String location) throws NabuccoTransformationException {
        MdaModel<NabuccoModel> model;

        if (location == null) {
            StringBuilder path = new StringBuilder();
            path.append(rootDir);
            path.append(File.separatorChar);
            path.append(NabuccoCompilerSupport.getParentComponentName(importString));
            location = path.toString();
        }

        NabuccoFile referencedFile = getReferencedFile(importString, location);

        try {
            NabuccoModelLoader nabuccoModelLoader = new NabuccoModelLoader(outDir);
            NabuccoModel referencedModel = nabuccoModelLoader.loadModel(referencedFile);
            referencedModel.setResourceType(NabuccoModelResourceType.PROJECT);
            
            model = new MdaModel<NabuccoModel>(referencedModel);

            return model;

        } catch (ModelException e) {
            return null;
        }
    }

    /**
     * Retrieves the referenced file for tanhe import string.
     * 
     * @param importString
     *            the import to resolve
     * @param location
     *            the project location
     * 
     * @return the referenced NABUCCO file
     * 
     * @throws NabuccoTransformationException
     *             if the file cannot be found
     */
    private static NabuccoFile getReferencedFile(String importString, String location)
            throws NabuccoTransformationException {

        validatePath(location);

        String path = createFilePath(location, importString);
        File file = new File(path);

        if (!file.exists()) {
            throw new NabuccoTransformationException("Cannot resolve import '"
                    + importString + "'.");
        }

        try {
            return new NabuccoFile(file);
        } catch (IOException e) {
            throw new NabuccoTransformationException("Cannot resolve import '"
                    + importString + "'.", e);
        }
    }

    /**
     * Validates the path to the project directory.
     * 
     * @param path
     *            the path to the project
     * 
     * @throws NabuccoTransformationException
     */
    private static void validatePath(String path) throws NabuccoTransformationException {

        File file = new File(path);

        if (!file.exists()) {
            String msg = "NABUCCO path does not point to a valid project '"
                    + path + "' (target does not exist).";
            throw new NabuccoTransformationException(msg);
        }

        if (!file.isDirectory()) {
            String msg = "NABUCCO path does not point to a valid project '"
                    + path + "' (target is no directory).";
            throw new NabuccoTransformationException(msg);
        }

        File sourceFolder = new File(file, NBC_SOURCE_FOLDER);

        if (!sourceFolder.exists()) {
            String msg = "NABUCCO path does not point to a valid NABUCCO project '"
                    + path + "' (src/nbc not found).";
            throw new NabuccoTransformationException(msg);
        }
    }

    /**
     * Create a file path for the given location and import strings.
     * 
     * @param location
     *            the project location
     * @param importString
     *            the import to resolve
     * 
     * @return the absolute file path
     */
    private static String createFilePath(String location, String importString) {
        StringBuilder path = new StringBuilder(location);
        path.append(File.separatorChar);
        path.append(NabuccoFileConstants.SOURCE_SRC);
        path.append(File.separatorChar);
        path.append(NabuccoFileConstants.SOURCE_NBC);
        path.append(File.separatorChar);
        path.append(importString.replace('.', File.separatorChar));
        path.append(NabuccoFileConstants.NBC_SUFFIX);
        return path.toString();
    }

}
