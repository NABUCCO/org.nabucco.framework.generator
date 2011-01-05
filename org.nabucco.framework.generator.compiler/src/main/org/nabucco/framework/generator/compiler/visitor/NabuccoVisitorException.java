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
package org.nabucco.framework.generator.compiler.visitor;

/**
 * NabuccoVisitorException
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoVisitorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link NabuccoVisitorException} instance.
     */
    public NabuccoVisitorException() {
        super();
    }

    /**
     * Creates a new {@link NabuccoVisitorException} instance.
     * 
     * @param message
     *            the message
     * @param cause
     *            the causing exception
     */
    public NabuccoVisitorException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@link NabuccoVisitorException} instance.
     * 
     * @param messages
     *            the message
     */
    public NabuccoVisitorException(String messages) {
        super(messages);
    }

    /**
     * Creates a new {@link NabuccoVisitorException} instance.
     * 
     * @param cause
     *            the causing exception
     */
    public NabuccoVisitorException(Exception cause) {
        super(cause);
    }

    @Override
    public Exception getCause() {
        if (super.getCause() instanceof Exception) {
            return (Exception) super.getCause();
        }
        return null;
    }

}
