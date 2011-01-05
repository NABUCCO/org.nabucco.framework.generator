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
package org.nabucco.framework.generator.compiler.verifier.error;

/**
 * VerificationError
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class VerificationError {

    private String message;

    private VerificationErrorCriticality criticality;

    private int rowNumber;

    private int columnNumber;

    /**
     * Creates a new {@link VerificationError} instance.
     * 
     * @param criticality
     *            the error criticality
     * @param message
     *            a specific error message
     */
    public VerificationError(VerificationErrorCriticality criticality, String... message) {
        this(criticality, 0, 0, message);
    }

    /**
     * Creates a new {@link VerificationError} instance.
     * 
     * @param criticality
     *            the error criticality
     * @param row
     *            the row number
     * @param column
     *            the column number
     * @param message
     *            a specific error message
     */
    public VerificationError(VerificationErrorCriticality criticality, int row,
            int column, String... message) {
        this.message = this.formatMessage(message);
        this.criticality = criticality;
        this.rowNumber = row;
        this.columnNumber = column;
    }

    /**
     * Getter for the message.
     * 
     * @return Returns the message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Getter for the criticality.
     * 
     * @return Returns the criticality.
     */
    public VerificationErrorCriticality getCriticality() {
        if (this.criticality == null) {
            this.criticality = VerificationErrorCriticality.ERROR;
        }
        return this.criticality;
    }

    /**
     * Getter for the rowNumber.
     * 
     * @return Returns the rowNumber.
     */
    public int getRowNumber() {
        return this.rowNumber;
    }

    /**
     * Getter for the columnNumber.
     * 
     * @return Returns the columnNumber.
     */
    public int getColumnNumber() {
        return this.columnNumber;
    }

    /**
     * Format the message tokens.
     * 
     * @param message
     *            the message tokens to merge
     * 
     * @return the formatted message
     */
    private String formatMessage(String... message) {
        StringBuilder result = new StringBuilder();
        for (String token : message) {
            result.append(token);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getMessage());
        result.append(" In row ");
        result.append(this.getRowNumber());
        result.append(", column ");
        result.append(this.getColumnNumber());
        result.append(".");
        return result.toString();
    }
}
