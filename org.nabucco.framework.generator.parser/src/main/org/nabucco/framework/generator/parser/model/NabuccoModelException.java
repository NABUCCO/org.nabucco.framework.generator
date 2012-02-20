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
package org.nabucco.framework.generator.parser.model;

import java.text.MessageFormat;

import org.nabucco.framework.mda.model.ModelException;

/**
 * NabuccoModelException
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoModelException extends ModelException {

    private static final long serialVersionUID = 1L;

    private static final String MSG = "Problem in {0}";

    /**
     * Creates a new {@link NabuccoModelException} instance.
     * 
     * @param fileName
     *            name of the file
     */
    public NabuccoModelException(String fileName) {
        super(concat(MessageFormat.format(MSG, fileName), "."));
    }

    /**
     * Creates a new {@link NabuccoModelException} instance.
     * 
     * @param fileName
     *            name of the file
     * @param msg
     *            the exception message
     */
    protected NabuccoModelException(String fileName, String msg) {
        super(concat(MessageFormat.format(MSG, fileName), ": ", msg));
    }

    /**
     * Creates a new {@link NabuccoModelException} instance.
     * 
     * @param fileName
     *            name of the file
     * @param msg
     *            the exception message
     * @param cause
     *            the causing exception
     */
    protected NabuccoModelException(String fileName, String msg, Exception cause) {
        super(concat(MessageFormat.format(MSG, fileName), ": ", msg), cause);
    }

    /**
     * Creates a new {@link NabuccoModelException} instance.
     * 
     * @param fileName
     *            name of the file
     * @param cause
     *            the causing exception
     */
    protected NabuccoModelException(String fileName, Exception cause) {
        super(concat(MessageFormat.format(MSG, fileName), "."), cause);
    }

    /**
     * Format the exception message.
     * 
     * @param fileName
     *            name of the file
     * @param messages
     *            the messages to append
     * 
     * @return the formatted message
     */
    private static String concat(String... messages) {
        StringBuilder msg = new StringBuilder();
        for (String token : messages) {
            msg.append(token);
        }
        return msg.toString();
    }

}
