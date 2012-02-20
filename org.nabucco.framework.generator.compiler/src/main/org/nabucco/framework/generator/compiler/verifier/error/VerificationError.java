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

/**
 * VerificationError
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class VerificationError {

    private String message;

    private VerificationErrorCriticality criticality;

    private int beginLine;

    private int endLine;

    private int beginColumn;

    private int endColumn;

    /**
     * Creates a new {@link VerificationError} instance.
     * 
     * @param criticality
     *            the error criticality
     * @param message
     *            a specific error message
     */
    public VerificationError(VerificationErrorCriticality criticality, String... message) {
        this(criticality, 0, 0, 0, 0, message);
    }

    /**
     * Creates a new {@link VerificationError} instance.
     * 
     * @param criticality
     *            the error criticality
     * @param beginLine
     *            the starting row number
     * @param endLine
     *            the ending row number
     * @param beginColumn
     *            the starting column number
     * @param endColumn
     *            the ending column number
     * @param message
     *            a specific error message
     */
    public VerificationError(VerificationErrorCriticality criticality, int beginLine, int endLine, int beginColumn,
            int endColumn, String... message) {
        this.message = this.formatMessage(message);
        this.criticality = criticality;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.beginColumn = beginColumn;
        this.endColumn = endColumn;
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
     * Getter for the beginLine.
     * 
     * @return Returns the beginLine.
     */
    public int getBeginLine() {
        return this.beginLine;
    }

    /**
     * Getter for the endLine.
     * 
     * @return Returns the endLine.
     */
    public int getEndLine() {
        return this.endLine;
    }

    /**
     * Getter for the beginColumn.
     * 
     * @return Returns the beginColumn.
     */
    public int getBeginColumn() {
        return this.beginColumn;
    }

    /**
     * Getter for the endColumn.
     * 
     * @return Returns the endColumn.
     */
    public int getEndColumn() {
        return this.endColumn;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.beginColumn;
        result = prime * result + this.beginLine;
        result = prime * result + ((this.criticality == null) ? 0 : this.criticality.hashCode());
        result = prime * result + this.endColumn;
        result = prime * result + this.endLine;
        result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof VerificationError))
            return false;
        VerificationError other = (VerificationError) obj;
        if (this.beginColumn != other.beginColumn)
            return false;
        if (this.beginLine != other.beginLine)
            return false;
        if (this.criticality != other.criticality)
            return false;
        if (this.endColumn != other.endColumn)
            return false;
        if (this.endLine != other.endLine)
            return false;
        if (this.message == null) {
            if (other.message != null)
                return false;
        } else if (!this.message.equals(other.message))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getMessage());
        result.append(" In row ");
        result.append(this.getBeginLine());
        result.append(", column ");
        result.append(this.getBeginColumn());
        result.append(".");
        return result.toString();
    }
}
