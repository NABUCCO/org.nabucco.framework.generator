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
package org.nabucco.framework.generator.compiler.transformation.util.dependency.reader;

import java.io.File;
import java.io.IOException;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.parser.file.NabuccoFile;
import org.nabucco.framework.generator.parser.file.NabuccoFileConstants;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelLoader;
import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelException;

/**
 * NabuccoProjectReader
 * <p/>
 * Reader for reading a {@link NabuccoModel} from a NABUCCO project.
 * 
 * @see NabuccoDependencyReader
 * @see NabuccoArchiveReader
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoProjectReader implements NabuccoDependencyReader, NabuccoTransformationConstants {

    private String location;

    private String outDir;

    /**
     * Creates a new {@link NabuccoArchiveReader} instance.
     * 
     * @param location
     *            the archive location
     */
    public NabuccoProjectReader(String location, String outDir) {
        if (location == null) {
            throw new IllegalArgumentException("Cannot load from project [null].");
        }
        if (outDir == null) {
            throw new IllegalArgumentException("Cannot load from project [" + location + "] with out directory [null].");
        }

        this.location = location;
        this.outDir = outDir;
    }

    @Override
    public MdaModel<NabuccoModel> read(String importString) throws NabuccoTransformationException {
        MdaModel<NabuccoModel> model;

        NabuccoFile referencedFile = getReferencedFile(importString, this.location);

        try {
            NabuccoModelLoader nabuccoModelLoader = new NabuccoModelLoader(this.outDir);
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
            throw new NabuccoTransformationException("Cannot resolve import '" + importString + "'.");
        }

        try {
            return new NabuccoFile(file);
        } catch (IOException e) {
            throw new NabuccoTransformationException("Cannot resolve import '" + importString + "'.", e);
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
            String msg = "NABUCCO path does not point to a valid project '" + path + "' (target does not exist).";
            throw new NabuccoTransformationException(msg);
        }

        if (!file.isDirectory()) {
            String msg = "NABUCCO path does not point to a valid project '" + path + "' (target is no directory).";
            throw new NabuccoTransformationException(msg);
        }

        File sourceFolder = new File(file, NBC_SOURCE_FOLDER);

        if (!sourceFolder.exists()) {
            String msg = "NABUCCO path does not point to a valid NABUCCO project '" + path + "' (src/nbc not found).";
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
