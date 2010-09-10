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
package org.nabucco.framework.generator.parser.model;

/**
 * NabuccoPathEntryType
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public enum NabuccoPathEntryType {

    /**
     * A NABUCCO Archive (NAR) path dependency.
     */
    ARCHIVE("archive"),

    /**
     * A project path dependency.
     */
    PROJECT("project");

    /**
     * Creates a new {@link NabuccoPathEntryType} instance.
     * 
     * @param id
     *            the path entry id
     */
    private NabuccoPathEntryType(String id) {
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
