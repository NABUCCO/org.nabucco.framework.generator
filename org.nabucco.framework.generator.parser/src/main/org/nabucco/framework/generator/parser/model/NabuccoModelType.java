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
 * NabuccoModelType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum NabuccoModelType {

    BASETYPE("Basetype"),

    COMPONENT("Component"),

    CONNECTOR("Connector"),

    DATATYPE("Datatype"),

    ENUMERATION("Enumeration"),

    EXCEPTION("Exception"),

    MESSAGE("Message"),

    SERVICE("Service"),
    
    EDIT_VIEW("EditView"),
    
    LIST_VIEW("ListView"),
    
    SEARCH_VIEW("SearchView"),
    
    COMMAND("Command");
    
    private String id;
    
    private NabuccoModelType(String id) {
        this.id = id;
    }
    
    /**
     * Getter for the model ID.
     * 
     * @return Returns the id.
     */
    public String getId() {
        return this.id;
    }

}
