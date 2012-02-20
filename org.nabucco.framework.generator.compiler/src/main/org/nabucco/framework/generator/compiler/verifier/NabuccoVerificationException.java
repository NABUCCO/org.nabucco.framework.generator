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
package org.nabucco.framework.generator.compiler.verifier;

import org.nabucco.framework.generator.compiler.NabuccoCompilerException;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;

/**
 * NabuccoVerificationException
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoVerificationException extends NabuccoCompilerException {

    private static final long serialVersionUID = 1L;

    private VerificationResult result;

    /**
     * Creates a new {@link NabuccoVerificationException} instance.
     * 
     * @param result
     *            the verification result
     */
    public NabuccoVerificationException(VerificationResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Cannot create NabuccoVerificationException with result [null].");
        }
        this.result = result;
    }

    @Override
    public String getMessage() {
        return this.result.toString();
    }

    /**
     * Getter for the result.
     * 
     * @return Returns the result.
     */
    public VerificationResult getResult() {
        return this.result;
    }

}
