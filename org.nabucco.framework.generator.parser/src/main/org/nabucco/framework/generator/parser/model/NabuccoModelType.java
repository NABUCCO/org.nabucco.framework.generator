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
 * NabuccoModelType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum NabuccoModelType {

    APPLICATION("Application", ModelLocation.APPLICATION),

    ADAPTER("Adapter", ModelLocation.COMPONENT),
    
    BASETYPE("Basetype", ModelLocation.COMPONENT),

    COMPONENT("Component", ModelLocation.COMPONENT),

    CONNECTOR("Connector", ModelLocation.APPLICATION),

    DATATYPE("Datatype", ModelLocation.COMPONENT),

    ENUMERATION("Enumeration", ModelLocation.COMPONENT),

    EXCEPTION("Exception", ModelLocation.COMPONENT),

    MESSAGE("Message", ModelLocation.COMPONENT),

    SERVICE("Service", ModelLocation.COMPONENT),

    EDIT_VIEW("EditView", ModelLocation.COMPONENT),

    LIST_VIEW("ListView", ModelLocation.COMPONENT),

    SEARCH_VIEW("SearchView", ModelLocation.COMPONENT),

    COMMAND("Command", ModelLocation.COMPONENT);

    private String id;

    private ModelLocation location;

    /**
     * Creates a new {@link NabuccoModelType} instance.
     * 
     * @param id
     *            the model id
     * @param location
     *            the model location
     */
    private NabuccoModelType(String id, ModelLocation location) {
        this.id = id;
        this.location = location;
    }

    /**
     * Location of the model (Component, Application or both).
     */
    private enum ModelLocation {
        APPLICATION, COMPONENT, BOTH,
    }

    /**
     * Getter for the model ID.
     * 
     * @return Returns the id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Checks whether the model type is usable in applications.
     * 
     * @return <b>true</b> if the model type is usable in applications, <b>false</b> if not
     */
    public boolean isApplication() {
        return this.location != ModelLocation.COMPONENT;
    }

    /**
     * Checks whether the model type is usable in components.
     * 
     * @return <b>true</b> if the model type is usable in components, <b>false</b> if not
     */
    public boolean isComponent() {
        return this.location != ModelLocation.APPLICATION;
    }

}
