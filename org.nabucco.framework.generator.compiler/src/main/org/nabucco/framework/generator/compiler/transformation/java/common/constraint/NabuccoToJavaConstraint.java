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
package org.nabucco.framework.generator.compiler.transformation.java.common.constraint;

import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;

/**
 * NabuccoToJavaConstraint
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
abstract class NabuccoToJavaConstraint implements NabuccoToJavaConstraintConstants {

    protected static String SEPARATOR = ";";

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoToJavaConstraint.class);

    private NabuccoMultiplicityType multiplicity;

    private boolean field;

    /**
     * Creates a new {@link NabuccoToJavaConstraint} instance.
     * 
     * @param isFieldConstraint
     *            flag for indicating the constraint is for a field or for a statement
     */
    public NabuccoToJavaConstraint(boolean isFieldConstraint) {
        this.field = isFieldConstraint;
    }

    /**
     * Getter for the constraint value.
     * 
     * @return Returns the constraint value.
     */
    public String getValue() {

        StringBuilder value = new StringBuilder();

        if (this.field) {

            if (this.multiplicity == null) {
                throw new IllegalStateException("Multiplicity must be defined for Field constraint.");
            }

            /* Multiplicity Constraints */
            value.append(MULTIPLICITY_CONSTRAINT);
            value.append(this.multiplicity.getConstraint());
            value.append(SEPARATOR);
        }

        return value.toString();
    }

    /**
     * Appends min length to the constraint.
     * 
     * @param minLength
     *            the min length
     */
    public void appendMinLengthConstraint(String minLength) {
    }

    /**
     * Appends max length to the constraint.
     * 
     * @param maxLength
     *            the max length
     */
    public void appendMaxLengthConstraint(String maxLength) {
    }

    /**
     * Appends min value to the constraint.
     * 
     * @param minValue
     *            the min value
     */
    public void appendMinValueConstraint(String minValue) {
    }

    /**
     * Appends max value to the constraint.
     * 
     * @param maxValue
     *            the max value
     */
    public void appendMaxValueConstraint(String maxValue) {
    }

    /**
     * Appends the multiplicity type to the constraint.
     * 
     * @param multiplicity
     *            the multiplicity type
     */
    public void appendMultiplicityConstraint(NabuccoMultiplicityType multiplicity) {
        if (multiplicity != null) {
            this.multiplicity = multiplicity;
        }
    }

    /**
     * Appends the pattern to the constraint.
     * 
     * @param pattern
     *            the pattern
     */
    public void appendPatternConstraint(String pattern) {
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    /**
     * Getter for the logger.
     * 
     * @return Returns the logger.
     */
    protected MdaLogger getLogger() {
        return logger;
    }
}
