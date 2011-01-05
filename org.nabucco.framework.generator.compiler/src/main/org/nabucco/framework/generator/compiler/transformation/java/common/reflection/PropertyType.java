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
package org.nabucco.framework.generator.compiler.transformation.java.common.reflection;

/**
 * PropertyType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
enum PropertyType {

    BASETYPE(
            "BasetypeProperty",
            "org.nabucco.framework.base.facade.datatype.property.BasetypeProperty"),

    DATATYPE(
            "DatatypeProperty",
            "org.nabucco.framework.base.facade.datatype.property.DatatypeProperty"),

    ENUM("EnumProperty", "org.nabucco.framework.base.facade.datatype.property.EnumProperty"),

    SIMPLE("SimpleProperty", "org.nabucco.framework.base.facade.datatype.property.SimpleProperty"),

    LIST("ListProperty", "org.nabucco.framework.base.facade.datatype.property.ListProperty");

    private String name;

    private String importName;

    /**
     * Creates a new {@link PropertyType} instance.
     * 
     * @param name
     *            the property name
     * @param importName
     *            the fully qualified import of the property
     */
    private PropertyType(String name, String importName) {
        this.name = name;
        this.importName = importName;
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
    public String getImport() {
        return this.importName;
    }

}
