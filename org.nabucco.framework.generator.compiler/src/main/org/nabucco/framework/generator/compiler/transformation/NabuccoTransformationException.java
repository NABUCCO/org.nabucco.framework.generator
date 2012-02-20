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
package org.nabucco.framework.generator.compiler.transformation;

import java.text.MessageFormat;

import org.nabucco.framework.mda.transformation.TransformationException;

/**
 * NabuccoTransformationException
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoTransformationException extends TransformationException {

    private static final long serialVersionUID = 1L;

    private static final String ROOT_MESSAGE = "Error transforming NABUCCO {0} [{1}].";

    private String type;

    private String source;

    /**
     * Creates a new {@link NabuccoTransformationException} instance.
     */
    public NabuccoTransformationException() {
        super();
    }

    /**
     * Creates a new {@link NabuccoTransformationException} instance.
     * 
     * @param message
     *            the error message
     * @param cause
     *            the causing exception
     */
    public NabuccoTransformationException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@link NabuccoTransformationException} instance.
     * 
     * @param message
     *            the error message
     */
    public NabuccoTransformationException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link NabuccoTransformationException} instance.
     * 
     * @param cause
     *            the causing throwable
     */
    public NabuccoTransformationException(Exception cause) {
        super(cause);
    }

    /**
     * Set the transformation parameters.
     * 
     * @param type
     *            the NABUCCO type
     * @param source
     *            the NABUCCO source file
     */
    public void setParameters(String type, String source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public String getMessage() {
        return format(super.getMessage());
    }

    /**
     * Getter for the original error message.
     * 
     * @return the original message
     */
    public String getOriginalMessage() {
        return super.getMessage();
    }

    /**
     * Formats the exception message.
     * 
     * @param error
     *            the concrete error message
     * 
     * @return the formatted message
     */
    private String format(String error) {
        StringBuilder message = new StringBuilder();
        message.append(MessageFormat.format(ROOT_MESSAGE, this.type, this.source));
        if (error != null && !error.isEmpty()) {
            message.append(" ");
            message.append(error);
        }
        return message.toString();
    }

}
