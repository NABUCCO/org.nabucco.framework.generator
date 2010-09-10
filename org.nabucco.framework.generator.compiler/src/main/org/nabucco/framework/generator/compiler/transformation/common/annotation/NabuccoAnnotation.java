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
package org.nabucco.framework.generator.compiler.transformation.common.annotation;

/**
 * NabuccoAnnotation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoAnnotation {

    private NabuccoAnnotationType type;

    private String value;

    /**
     * Creates a new {@link NabuccoAnnotation} instance.
     * 
     * @param type
     *            type of the annotation
     * @param value
     *            the annotation value
     */
    public NabuccoAnnotation(NabuccoAnnotationType type, String value) {
        if (type == null) {
            throw new IllegalArgumentException("Annotation type must be defined.");
        }
        this.type = type;
        this.value = value;
    }
    
    /**
     * Getter for the annotation type.
     * 
     * @return Returns the type.
     */
    public NabuccoAnnotationType getType() {
        return this.type;
    }

    /**
     * Getter for the annotation name.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return this.type.getName();
    }

    /**
     * Getter for the annotation value.
     * 
     * @return Returns the value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Getter for the annotation group type.
     * 
     * @return Returns the group type.
     */
    public NabuccoAnnotationGroupType getGroupType() {
        return this.type.getType();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof NabuccoAnnotation))
            return false;
        NabuccoAnnotation other = (NabuccoAnnotation) obj;
        if (this.type == null) {
            if (other.type != null)
                return false;
        } else if (!this.type.equals(other.type))
            return false;
        if (this.value == null) {
            if (other.value != null)
                return false;
        } else if (!this.value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("@");
        builder.append(this.getName());
        builder.append("\t");
        builder.append(this.getValue());
        builder.append("\n[");
        builder.append(this.getGroupType());
        builder.append("]");
        return builder.toString();
    }
}
