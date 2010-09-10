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
package org.nabucco.framework.generator.compiler.transformation.java.common.constraint;

/**
 * NabuccoToJavaBasetypeConstraint
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
final class NabuccoToJavaBasetypeConstraint extends NabuccoToJavaConstraint {

    private static String DEFAULT_MIN_LENGTH = "0";

    private static String DEFAULT_MAX_LENGTH = "n";

    private String minLength;

    private String maxLength;

    private String pattern;

    /**
     * Constructs a new {@link NabuccoToJavaBasetypeConstraint} instance.
     * 
     * @param isFieldConstraint
     *            flag for indicating the constraint is for a field or for a statement
     */
    public NabuccoToJavaBasetypeConstraint(boolean isFieldConstraint) {
        super(isFieldConstraint);
        this.minLength = DEFAULT_MIN_LENGTH;
        this.maxLength = DEFAULT_MAX_LENGTH;
    }

    @Override
    public String getValue() {

        StringBuilder value = new StringBuilder();

        /* Length Constraints */
        value.append(LENGTH_CONSTRAINT);
        value.append(this.minLength);
        value.append(",");
        value.append(this.maxLength);
        value.append(SEPARATOR);

        /* Pattern Constraints */
        if (this.pattern != null) {
            value.append(PATTERN_CONSTRAINT);
            value.append(pattern);
            value.append(SEPARATOR);
        }

        value.append(super.getValue());

        return value.toString();
    }

    @Override
    public void appendMinLengthConstraint(String minLength) {
        if (minLength != null) {
            this.minLength = minLength;
        } else {
            minLength = DEFAULT_MIN_LENGTH;
        }
    }

    @Override
    public void appendMaxLengthConstraint(String maxLength) {
        if (maxLength != null) {
            this.maxLength = maxLength;
        } else {
            maxLength = DEFAULT_MAX_LENGTH;
        }
    }
    
    @Override
    public void appendPatternConstraint(String pattern) {
        this.pattern = pattern;
    }

}
