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
package org.nabucco.framework.generator.compiler.verifier.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoPath;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoPathEntry;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NbcPathParser;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.file.NabuccoFile;
import org.nabucco.framework.generator.parser.file.NabuccoFileConstants;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelLoader;
import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;
import org.nabucco.framework.generator.parser.model.serializer.NabuccoModelSerializer;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.mda.model.ModelException;

/**
 * NabuccoImportVerification
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoImportVerification extends NabuccoModelVerificationVisitor implements
        NabuccoTransformationConstants {

    private String pkg;

    private String rootDirectory;

    private String outDirectory;

    /**
     * Creates a new {@link NabuccoImportVerification} instance.
     * 
     * @param rootDirectory
     *            the project root directory
     * @param outDirectory
     *            the target directory
     */
    public NabuccoImportVerification(String rootDirectory, String outDirectory) {
        this.rootDirectory = rootDirectory;
        this.outDirectory = outDirectory;
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, VerificationResult result) {
        this.pkg = nabuccoPackage.nodeToken1.tokenImage;

        super.visit(nabuccoPackage, result);
    }

    @Override
    public void visit(ImportDeclaration nabuccoImport, VerificationResult result) {

        if (this.pkg == null) {
            return;
        }

        String importString = nabuccoImport.nodeToken1.tokenImage;

        try {
            this.validateImport(importString, result);
        } catch (NabuccoTransformationException e) {
            int line = nabuccoImport.nodeToken1.beginLine;
            int col = nabuccoImport.nodeToken1.beginColumn;
            VerificationErrorCriticality criticality = VerificationErrorCriticality.ERROR;
            result.addError(criticality, line, col, e.getOriginalMessage());
        }
    }

    /**
     * Validate the import strings.
     * 
     * @param importString
     *            the import to validate
     * @param result
     *            the verification result
     * 
     * @throws NabuccoTransformationException
     */
    private void validateImport(String importString, VerificationResult result)
            throws NabuccoTransformationException {

        String component = NabuccoCompilerSupport.getParentComponentName(pkg);

        StringBuilder componentPath = new StringBuilder();
        componentPath.append(this.rootDirectory);
        componentPath.append(File.separatorChar);
        componentPath.append(component);
        componentPath.append(File.separatorChar);

        String nbcPath = componentPath + NabuccoFileConstants.NBCPATH_XML;
        NabuccoPath nabuccoPath = NbcPathParser.getElementsByXPath(nbcPath);

        NabuccoModel model = null;

        boolean otherComponent = NabuccoCompilerSupport.isOtherComponent(component, importString);

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
                    model = readFromProject(this.rootDirectory, importString, this.outDirectory,
                            location);
                    break;

                default:
                    throw new IllegalStateException("NbcPath Entry is not supported: "
                            + entryType + ".");
                }

                if (model != null) {
                    return;
                }
            }
        } else {
            model = readFromProject(this.rootDirectory, importString, this.outDirectory, null);
        }

        if (model == null) {
            if (otherComponent) {
                throw new NabuccoTransformationException("Cannot resolve import '"
                        + importString + "'. Missing entry in nbcPath.xml.");
            }
            throw new NabuccoTransformationException("Cannot resolve import '"
                    + importString + "' in project " + component + ".");
        }
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
    private static NabuccoModel readFromArchive(String importString, String location)
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

                return nabuccoModel;
            }
        } catch (IOException e) {
            throw new NabuccoTransformationException("Cannot read from NAR '" + location + "'.", e);
        } catch (ModelException e) {
            throw new NabuccoTransformationException("Cannot read from NAR '" + location + "'.", e);
        }

        return null;
    }

    /**
     * Converts an import string to a nar-entry-file path.
     * 
     * @param importString
     *            the import string
     * 
     * @return the file path
     */
    private static String convertImportString(String importString) {
        String nbccPath = importString.replace(PKG_SEPARATOR, JAR_SEPARATOR)
                .replace("\\", JAR_SEPARATOR).concat(NabuccoFileConstants.NBCC_SUFFIX);
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
    private static NabuccoModel readFromProject(String rootDir, String importString, String outDir,
            String location) throws NabuccoTransformationException {
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

            return referencedModel;

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
