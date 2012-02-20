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
package org.nabucco.framework.generator.compiler.visitor.util;

/**
 * NabuccoPropertyKey
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoPropertyKey {

    private String qualifiedType;

    private String name;

    /**
     * Creates a new {@link NabuccoPropertyKey} instance.
     * 
     * @param qualifiedType
     *            the qualified type name
     * @param name
     *            the property name
     */
    NabuccoPropertyKey(String qualifiedType, String name) {
        if (qualifiedType == null) {
            throw new IllegalArgumentException("Cannot create property key for type [null].");
        }
        if (name == null) {
            throw new IllegalArgumentException("Cannot create property key for name [null].");
        }
        this.qualifiedType = qualifiedType;
        this.name = name;
    }

    /**
     * Getter for the qualifiedType.
     * 
     * @return Returns the qualifiedType.
     */
    public String getQualifiedType() {
        return this.qualifiedType;
    }

    /**
     * Getter for the name.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.qualifiedType == null) ? 0 : this.qualifiedType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NabuccoPropertyKey other = (NabuccoPropertyKey) obj;
        if (this.name == null) {
            if (other.name != null)
                return false;
        } else if (!this.name.equals(other.name))
            return false;
        if (this.qualifiedType == null) {
            if (other.qualifiedType != null)
                return false;
        } else if (!this.qualifiedType.equals(other.qualifiedType))
            return false;
        return true;
    }

}
