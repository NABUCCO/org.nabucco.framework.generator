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

/**
 * NabuccoToJavaDatatypeConstraint
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
final class NabuccoToJavaDatatypeConstraint extends NabuccoToJavaConstraint {

    /**
     * Constructs a new {@link NabuccoToJavaDatatypeConstraint} instance.
     * 
     * @param isFieldConstraint
     *            flag for indicating the constraint is for a field or for a statement
     */
    public NabuccoToJavaDatatypeConstraint(boolean isFieldConstraint) {
        super(isFieldConstraint);
    }

    @Override
    public void appendMinLengthConstraint(String minLength) {
        super.getLogger().warning("LengthConstraints are not supported for Datatypes.");
    }

    @Override
    public void appendMaxLengthConstraint(String maxLength) {
        super.getLogger().warning("LengthConstraints are not supported for Datatypes.");
    }

    @Override
    public void appendMinValueConstraint(String minLength) {
        super.getLogger().warning("ValueConstraints are not supported for Datatypes.");
    }

    @Override
    public void appendMaxValueConstraint(String maxLength) {
        super.getLogger().warning("ValueConstraints are not supported for Datatypes.");
    }

    @Override
    public void appendPatternConstraint(String pattern) {
        super.getLogger().warning("PatternConstraints are not supported for Datatypes.");
    }
}
