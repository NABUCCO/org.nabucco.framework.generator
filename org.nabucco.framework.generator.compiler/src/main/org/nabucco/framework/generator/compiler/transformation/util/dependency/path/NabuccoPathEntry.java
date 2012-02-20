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
package org.nabucco.framework.generator.compiler.transformation.util.dependency.path;

import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;

/**
 * NabuccoPathEntry
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoPathEntry {

    private NabuccoModelResourceType type;

    private String location;

    /**
     * Creates a new {@link NabuccoPathEntry} instance.
     * 
     * @param type
     *            type of the path entry
     * @param location
     *            concrete path of the entry
     */
    public NabuccoPathEntry(NabuccoModelResourceType type, String location) {
        this.type = type;
        this.location = location;
    }

    /**
     * Getter for the path entry type.
     * 
     * @return type of the entry.
     */
    public NabuccoModelResourceType getType() {
        return type;
    }

    /**
     * Getter for the path entry location.
     * 
     * @return the specified location.
     */
    public String getLocation() {
        return location;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.location == null) ? 0 : this.location.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
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
        NabuccoPathEntry other = (NabuccoPathEntry) obj;
        if (this.location == null) {
            if (other.location != null)
                return false;
        } else if (!this.location.equals(other.location))
            return false;
        if (this.type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append((type != null) ? type.getId() : "Undefined");
        result.append(" : ");
        result.append((location != null) ? location : "Undefined");
        return result.toString();
    }

}
