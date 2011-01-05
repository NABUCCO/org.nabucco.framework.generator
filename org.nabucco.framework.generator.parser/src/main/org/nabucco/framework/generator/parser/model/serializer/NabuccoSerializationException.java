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
package org.nabucco.framework.generator.parser.model.serializer;

import org.nabucco.framework.generator.parser.model.NabuccoModelException;

/**
 * NabuccoSerializationException
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoSerializationException extends NabuccoModelException {

    private static final long serialVersionUID = 1L;

    private static final String MSG = "Error serializing file.";

    /**
     * Creates a new {@link NabuccoSerializationException} instance.
     * 
     * @param fileName
     *            name of the file
     * @param cause
     *            the causing exception
     */
    public NabuccoSerializationException(String fileName, Exception cause) {
        super(fileName, MSG, cause);
    }

}