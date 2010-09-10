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
package org.nabucco.framework.generator.parser.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.nabucco.framework.generator.parser.NabuccoParser;
import org.nabucco.framework.generator.parser.ParseException;
import org.nabucco.framework.generator.parser.ParseExceptionMapper;
import org.nabucco.framework.generator.parser.file.NabuccoFile;
import org.nabucco.framework.generator.parser.model.serializer.NabuccoModelSerializer;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;

import org.nabucco.framework.mda.model.ModelLoader;

/**
 * NabuccoModelLoader
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoModelLoader extends ModelLoader<NabuccoModel, NabuccoFile> {

    /** Source folder */
    private static final String SOURCE_DIR = "src" + File.separator + "nbc";

    /** File suffix for NABUCCO output files (.nbcc) */
    private static final String NABUCCO_OUT_SUFFIX = "c";

    private String outDir;

    /**
     * Constructs a new NABUCCO model loader.
     * 
     * @param outDirectory
     *            target directory of the NABUCCO compilation
     */
    public NabuccoModelLoader(String outDirectory) {
        this.outDir = outDirectory;
    }

    @Override
    public NabuccoModel loadModel(NabuccoFile modelFile) throws NabuccoModelException {
        return loadModel(modelFile, this.outDir);
    }

    /**
     * Loads a NABUCCO model from a file and serializes it into a .nbcc file.
     * 
     * @param nbcFile
     *            the .nbc-file to generate.
     * @param outDir
     *            the relative target directory
     * 
     * @return the parsed NABUCCO model
     * 
     * @throws NabuccoModelException
     */
    private NabuccoModel loadModel(NabuccoFile nbcFile, String outDir)
            throws NabuccoModelException {

        try {
            if (outDir == null) {
                throw new IllegalArgumentException("Target directory is not defined.");
            }

            String srcPath = nbcFile.getCanonicalPath();
            String outPath = srcPath.replace(SOURCE_DIR, outDir).concat(NABUCCO_OUT_SUFFIX);

            File outFile = new File(outPath);

            NabuccoModelSerializer serializer = NabuccoModelSerializer.getInstance();

            if (outFile.exists() && outFile.lastModified() > nbcFile.lastModified()) {
                return serializer.deserializeNabucco(outFile);
            }

            FileReader reader = nbcFile.getFileReader();
            NabuccoParser parser = new NabuccoParser(reader);

            NabuccoUnit unit = parser.NabuccoUnit();
            NabuccoModelType modelType = this.resolveModelType(unit.nabuccoStatement);
            
            reader.close();

            NabuccoModel model = new NabuccoModel(unit, srcPath, modelType,
                    NabuccoPathEntryType.PROJECT);
            serializer.serializeNabucco(model, outFile);
            return model;

        } catch (FileNotFoundException fnfe) {
            // TODO: Logger
            return null;
        } catch (IOException ioe) {
            String name = (nbcFile == null) ? "null" : nbcFile.getFileName() + "'.";
            throw new NabuccoModelException("Cannot parse file '" + name + "'.");
        } catch (ParseException pe) {
            throw ParseExceptionMapper.mapParseException(pe, nbcFile);
        }
    }

    private NabuccoModelType resolveModelType(NabuccoStatement statement)
            throws NabuccoModelException {

        switch (statement.nodeChoice.which) {

        case 0:
            return NabuccoModelType.COMPONENT;
        case 1:
            return NabuccoModelType.DATATYPE;
        case 2:
            return NabuccoModelType.BASETYPE;
        case 3:
            return NabuccoModelType.ENUMERATION;
        case 4:
            return NabuccoModelType.EXCEPTION;
        case 5:
            return NabuccoModelType.SERVICE;
        case 6:
            return NabuccoModelType.MESSAGE;
        case 7:
            return NabuccoModelType.EDIT_VIEW;
        case 8:
            return NabuccoModelType.LIST_VIEW;
        case 9:
            return NabuccoModelType.SEARCH_VIEW;
        case 10:
            return NabuccoModelType.COMMAND;
        }

        // Other types

        throw new NabuccoModelException("NABUCCO model type is not supported yet.");
    }
    
}
