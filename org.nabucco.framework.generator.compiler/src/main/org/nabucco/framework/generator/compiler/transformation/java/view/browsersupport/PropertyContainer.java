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
package org.nabucco.framework.generator.compiler.transformation.java.view.browsersupport;

/**
 * PropertyHandler
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class PropertyContainer implements Comparable<PropertyContainer> {

    /**
     * Name of a Property.
     */
    private String name;

    /**
     * Multiplicity of a Property.
     */
    private String multiplicity;

    /**
     * Type of a Property.
     */
    private String type;

    /**
     * ImportString of a Property.
     */
    private String importString;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the multiplicity.
     */
    public String getMultiplicity() {
        return multiplicity;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @return Returns the importString.
     */
    public String getImportString() {
        return importString;
    }

    /**
     * Creates a new {@link PropertyContainer} instance.
     * 
     * @param name
     *            name of the property.
     * @param multiplicity
     *            multiplicity of the property.
     * @param type
     *            type of the property.
     * @param importString
     *            importString of the property.
     */
    public PropertyContainer(String name, String multiplicity, String type, String importString) {
        super();
        this.name = name;
        this.multiplicity = multiplicity;
        this.type = type;
        this.importString = importString;
    }

    @Override
    public int compareTo(PropertyContainer propertyContainer) {

        int result = this.getName().compareTo(propertyContainer.getName());

        if (result != 0) {
            return result;
        }

        result = this.getType().compareTo(propertyContainer.getType());

        if (result != 0) {
            return result;
        }

        return this.getImportString().compareTo(propertyContainer.getImportString());
    }

}
