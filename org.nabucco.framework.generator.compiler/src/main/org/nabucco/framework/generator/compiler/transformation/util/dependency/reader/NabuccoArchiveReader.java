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

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.parser.file.NabuccoFileConstants;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;
import org.nabucco.framework.generator.parser.model.serializer.NabuccoModelSerializer;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelException;

/**
 * NabuccoArchiveReader
 * <p/>
 * Reader for reading a {@link NabuccoModel} from a NABUCCO Archive (.nar) file.
 * 
 * @see NabuccoDependencyReader
 * @see NabuccoProjectReader
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoArchiveReader implements NabuccoDependencyReader {

    /** The backslash character. */
    private static final String BACK_SLASH = "\\";

    private String location;

    /**
     * Creates a new {@link NabuccoArchiveReader} instance.
     * 
     * @param location
     *            the archive location
     */
    public NabuccoArchiveReader(String location) {
        if (location == null) {
            throw new IllegalArgumentException("Cannot load from NAR [null].");
        }
        this.location = location;
    }

    @Override
    public MdaModel<NabuccoModel> read(String importString) throws NabuccoTransformationException {

        if (importString == null) {
            throw new IllegalArgumentException("Cannot load from NAR '" + importString + "', location 'null'.");
        }

        try {
            JarFile jarFile = new JarFile(this.location);
            String nbccPath = convertImportString(importString);
            String modelName = importString.substring(importString.lastIndexOf('.'));
            JarEntry entry = jarFile.getJarEntry(nbccPath);

            if (entry != null) {
                InputStream in = jarFile.getInputStream(entry);

                NabuccoModel nabuccoModel = NabuccoModelSerializer.getInstance().deserializeNabucco(modelName, in);

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
        String nbccPath = importString.replace(PKG_SEPARATOR, JAR_SEPARATOR).replace(BACK_SLASH, JAR_SEPARATOR)
                .concat(NabuccoFileConstants.NBCC_SUFFIX);
        return nbccPath;
    }

}
