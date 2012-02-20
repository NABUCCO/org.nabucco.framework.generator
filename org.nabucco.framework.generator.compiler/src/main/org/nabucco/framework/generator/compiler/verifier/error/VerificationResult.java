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
package org.nabucco.framework.generator.compiler.verifier.error;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nabucco.framework.generator.parser.model.NabuccoModel;

/**
 * VerificationResult
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class VerificationResult {

    private NabuccoModel model;

    private Map<VerificationErrorCriticality, Set<VerificationError>> errorMap;

    /**
     * Creates a new {@link VerificationResult} instance.
     * 
     * @param model
     *            the NABUCCO model in focus
     */
    public VerificationResult(NabuccoModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Cannot verify model [null].");
        }
        this.model = model;
        this.errorMap = new EnumMap<VerificationErrorCriticality, Set<VerificationError>>(
                VerificationErrorCriticality.class);
    }

    /**
     * Getter for the model.
     * 
     * @return Returns the model.
     */
    public NabuccoModel getModel() {
        return this.model;
    }

    /**
     * Adds a verification error to the verification result.
     * 
     * @param error
     *            the verification error
     */
    public void addError(VerificationError error) {
        VerificationErrorCriticality criticality = error.getCriticality();
        if (this.errorMap.containsKey(criticality)) {
            this.errorMap.get(criticality).add(error);
        } else {
            Set<VerificationError> errorList = new HashSet<VerificationError>();
            errorList.add(error);
            this.errorMap.put(criticality, errorList);
        }
    }

    /**
     * Checks whether the verification result contains errors with criticality WARNING or not.
     * 
     * @return <b>true</b> if the result containts verification errors, <b>false</b> if not
     */
    public boolean hasWarnings() {
        return this.errorMap.containsKey(VerificationErrorCriticality.WARNING);
    }

    /**
     * Checks whether the verification result contains errors with criticality ERROR or not.
     * 
     * @return <b>true</b> if the result containts verification errors, <b>false</b> if not
     */
    public boolean hasErrors() {
        return this.errorMap.containsKey(VerificationErrorCriticality.ERROR);
    }

    /**
     * Checks whether the verification result contains errors with criticality FATAL or not.
     * 
     * @return <b>true</b> if the result containts verification errors, <b>false</b> if not
     */
    public boolean hasFatals() {
        return this.errorMap.containsKey(VerificationErrorCriticality.FATAL);
    }

    /**
     * Creates a new verification error and adds it to the verification result.
     * 
     * @param criticality
     *            the error criticality
     * @param message
     *            the error message
     */
    public void addError(VerificationErrorCriticality criticality, String... message) {
        this.addError(new VerificationError(criticality, message));
    }

    /**
     * Creates a new verification error and adds it to the verification result.
     * 
     * @param criticality
     *            the error criticality
     * @param beginLine
     *            the starting error row
     * @param endLine
     *            the ending error row
     * @param beginColumn
     *            the starting error column
     * @param endColumn
     *            the ending error column
     * @param message
     *            the error message
     */
    public void addError(VerificationErrorCriticality criticality, int beginLine, int endLine, int beginColumn,
            int endColumn, String... message) {
        this.addError(new VerificationError(criticality, beginLine, endLine, beginColumn, endColumn, message));
    }

    /**
     * Returns the set of errors with ciriticality WARNING.
     * 
     * @return Returns the set of warnings.
     */
    public Set<VerificationError> getWarnings() {
        if (!this.hasWarnings()) {
            return Collections.emptySet();
        }
        return new HashSet<VerificationError>(this.errorMap.get(VerificationErrorCriticality.WARNING));
    }

    /**
     * Returns the set of errors with ciriticality ERROR.
     * 
     * @return Returns the set of errors.
     */
    public Set<VerificationError> getErrors() {
        if (!this.hasErrors()) {
            return Collections.emptySet();
        }
        return new HashSet<VerificationError>(this.errorMap.get(VerificationErrorCriticality.ERROR));
    }

    /**
     * Returns the set of errors with ciriticality FATAL.
     * 
     * @return Returns the set of fatals.
     */
    public Set<VerificationError> getFatals() {
        if (!this.hasFatals()) {
            return Collections.emptySet();
        }
        return new HashSet<VerificationError>(this.errorMap.get(VerificationErrorCriticality.FATAL));
    }

    /**
     * Returns true if the result does not contain any warnings, errors or fatals.
     * 
     * @return <b>true</b> if the result does not contain any verification errors, <b>false</b>
     *         otherwise
     */
    public boolean isEmpty() {
        return this.errorMap.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("Error verifying NABUCCO model (");
        result.append(this.model.getName());
        result.append(").\n");

        if (this.hasWarnings()) {
            result.append("Warnings:");
            for (VerificationError error : this.errorMap.get(VerificationErrorCriticality.WARNING)) {
                result.append("\n\t- ");
                result.append(error);
            }
        }
        if (this.hasErrors()) {
            result.append("Errors:");
            for (VerificationError error : this.errorMap.get(VerificationErrorCriticality.ERROR)) {
                result.append("\n\t- ");
                result.append(error);
            }
        }
        if (this.hasFatals()) {
            result.append("Fatals:");
            for (VerificationError error : this.errorMap.get(VerificationErrorCriticality.FATAL)) {
                result.append("\n\t- ");
                result.append(error);
            }
        }
        return result.toString();
    }

}
