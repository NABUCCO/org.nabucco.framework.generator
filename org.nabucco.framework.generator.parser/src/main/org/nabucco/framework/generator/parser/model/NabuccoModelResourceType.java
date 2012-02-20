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
package org.nabucco.framework.generator.parser.model;

/**
 * NabuccoModelResourceType
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public enum NabuccoModelResourceType {

    /**
     * A resource loaded from a NABUCCO Archive (NAR).
     */
    ARCHIVE("archive"),

    /**
     * A resources added dynamically by the pre-compiler.
     */
    COMPILER("compiler"),

    /**
     * A resource loaded from a project dependency.
     */
    PROJECT("project");

    /**
     * Creates a new {@link NabuccoModelResourceType} instance.
     * 
     * @param id
     *            the path entry id
     */
    private NabuccoModelResourceType(String id) {
        this.id = id;
    }

    /** The id of the path entry. */
    private String id;

    /**
     * Getter for the path entry ID.
     * 
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

}
