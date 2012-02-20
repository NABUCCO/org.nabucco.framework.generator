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
package org.nabucco.framework.generator.compiler.transformation.java.common.reflection;

/**
 * PropertyType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
enum PropertyType {

    BASETYPE("BasetypeProperty"),

    DATATYPE("DatatypeProperty", "org.nabucco.framework.base.facade.datatype.property.PropertyAssociationType"),

    ENUMERATION("EnumProperty"),

    SIMPLE("SimpleProperty"),

    COLLECTION("CollectionProperty", "org.nabucco.framework.base.facade.datatype.property.PropertyAssociationType");

    private String name;

    private String[] imports;

    private static final String IMPORT = "org.nabucco.framework.base.facade.datatype.property.NabuccoPropertyType";

    /**
     * Creates a new {@link PropertyType} instance.
     * 
     * @param name
     *            the property name
     * @param importName
     *            the fully qualified import of the property
     */
    private PropertyType(String name, String... importName) {
        this.name = name;
        this.imports = importName;
    }

    /**
     * Getter for the name.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the importName.
     * 
     * @return Returns the importName.
     */
    public String[] getImports() {
        return this.imports;
    }

    /**
     * Getter for the property type import.
     * 
     * @return the property type import
     */
    public static String getImport() {
        return IMPORT;
    }

}
