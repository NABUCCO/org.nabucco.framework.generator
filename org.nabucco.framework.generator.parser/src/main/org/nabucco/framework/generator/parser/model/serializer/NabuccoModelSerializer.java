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
package org.nabucco.framework.generator.parser.model.serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileLock;

import org.nabucco.framework.generator.parser.file.NabuccoFileConstants;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelException;

/**
 * NabuccoModelSerializer
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoModelSerializer implements NabuccoFileConstants {

    /**
     * Singleton instance.
     */
    private static NabuccoModelSerializer instance = new NabuccoModelSerializer();

    /**
     * Private constructor.
     */
    private NabuccoModelSerializer() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoModelSerializer instance.
     */
    public static NabuccoModelSerializer getInstance() {
        return instance;
    }

    /**
     * Serializes a NABUCCO model into an appropriate <b>.nbcc</b> file in the out/ directory.
     * 
     * @param model
     *            the NABUCCO model
     * 
     * @throws NabuccoModelException
     */
    public synchronized void serializeNabucco(NabuccoModel model, File outFile)
            throws NabuccoModelException {
        FileOutputStream fileOutput = null;
        FileLock lock = null;
        BufferedOutputStream bufferedOutput = null;
        ObjectOutputStream objectOutput = null;

        try {
            if (!outFile.exists()) {
                outFile.getParentFile().mkdirs();
                outFile.createNewFile();
            }

            fileOutput = new FileOutputStream(outFile);
            lock = fileOutput.getChannel().lock();

            bufferedOutput = new BufferedOutputStream(fileOutput);
            objectOutput = new ObjectOutputStream(bufferedOutput);

            objectOutput.writeObject(model);
            objectOutput.flush();
        } catch (IOException e) {
            throw new NabuccoSerializationException(model.getName(), e);
        } finally {
            try {
                if (lock != null) {
                    lock.release();
                }
                if (fileOutput != null) {
                    fileOutput.close();
                }
                if (bufferedOutput != null) {
                    bufferedOutput.close();
                }
                if (objectOutput != null) {
                    objectOutput.close();
                }
            } catch (IOException e) {
                throw new NabuccoSerializationException(model.getName(), e);
            }
        }
    }

    /**
     * Deserializes a <b>.nbcc</b> file into an appropriate NABUCCO model file.
     * 
     * @param file
     *            the <b>.nbcc</b> file
     * 
     * @return the deserialized NABUCCO model
     * 
     * @throws NabuccoModelException
     */
    public synchronized NabuccoModel deserializeNabucco(File file) throws NabuccoModelException {

        FileInputStream fileInput = null;

        String fileName = file.getName().replace(NBC_SUFFIX, "");

        try {
            fileInput = new FileInputStream(file);
            return deserializeNabucco(fileName, fileInput);

        } catch (IOException e) {
            throw new NabuccoDeserializationException(fileName, e);
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
            } catch (IOException e) {
                throw new NabuccoDeserializationException(fileName, e);
            }
        }
    }

    /**
     * Deserializes a InputStream into an appropriate NABUCCO model file.
     * 
     * @param fileName
     *            name of the file
     * @param inputStream
     *            inputStream of the <b>.nbcc</b> file
     * 
     * @return the deserialized NABUCCO model
     * 
     * @throws NabuccoModelException
     */
    public synchronized NabuccoModel deserializeNabucco(String fileName, InputStream inputStream)
            throws NabuccoModelException {

        BufferedInputStream bufferedInput = null;
        ObjectInputStream objectInput = null;

        try {
            bufferedInput = new BufferedInputStream(inputStream);
            objectInput = new ObjectInputStream(bufferedInput);

            NabuccoModel model = (NabuccoModel) objectInput.readObject();
            return model;

        } catch (ClassNotFoundException e) {
            throw new NabuccoDeserializationException(fileName, e);
        } catch (IOException e) {
            throw new NabuccoDeserializationException(fileName, e);
        } finally {
            try {
                if (bufferedInput != null) {
                    bufferedInput.close();
                }
                if (objectInput != null) {
                    objectInput.close();
                }
            } catch (IOException e) {
                throw new NabuccoDeserializationException(fileName, e);
            }
        }
    }

}
