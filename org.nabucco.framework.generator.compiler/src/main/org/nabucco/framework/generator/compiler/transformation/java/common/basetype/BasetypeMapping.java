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
package org.nabucco.framework.generator.compiler.transformation.java.common.basetype;

import org.nabucco.framework.generator.compiler.transformation.java.basetype.JavaBasetypeMapping;

/**
 * BaseTypeMapping
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public enum BasetypeMapping implements JavaBasetypeMapping {

    N_BOOLEAN("NBoolean", "org.nabucco.framework.base.facade.datatype.NBoolean", "Boolean"),

    N_BYTE("NByte", "org.nabucco.framework.base.facade.datatype.NByte", "Byte"),

    N_BYTE_ARRAY("NByteArray", "org.nabucco.framework.base.facade.datatype.NByteArray", "byte[]"),

    N_CHAR("NChar", "org.nabucco.framework.base.facade.datatype.NChar", "Character"),

    N_DATE("NDate", "org.nabucco.framework.base.facade.datatype.NDate", "java.util.Date"),

    N_DOUBLE("NDouble", "org.nabucco.framework.base.facade.datatype.NDouble", "Double"),

    N_FLOAT("NFloat", "org.nabucco.framework.base.facade.datatype.NFloat", "Float"),

    N_INTEGER("NInteger", "org.nabucco.framework.base.facade.datatype.NInteger", "Integer"),

    N_LONG("NLong", "org.nabucco.framework.base.facade.datatype.NLong", "Long"),

    N_STRING("NString", "org.nabucco.framework.base.facade.datatype.NString", "String"),

    N_TEXT("NText", "org.nabucco.framework.base.facade.datatype.NText", "String"),

    N_TYPE(
            "NType",
            "org.nabucco.framework.base.facade.datatype.NType",
            "org.nabucco.framework.base.facade.datatype.NType");

    /**
     * Name of the type
     */
    private String name;

    /**
     * The mapped java class wrapper
     */
    private String wrapperClass;

    /**
     * The java class to use
     */
    private String primitiveType;

    /**
     * Creates a new {@link BasetypeMapping} instance.
     * 
     * @param name
     *            the type name
     * @param wrapperClass
     *            the fully qualified wrapper class
     * @param primitiveType
     *            the primitive java type
     */
    private BasetypeMapping(String name, String wrapperClass, String primitiveType) {
        this.name = name;
        this.wrapperClass = wrapperClass;
        this.primitiveType = primitiveType;
    }

    /**
     * Getter for the type's name.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the type's java class.
     * 
     * @return Returns the wrapperClass.
     */
    public String getWrapperClass() {
        return wrapperClass;
    }

    /**
     * Getter for the type's java type.
     * 
     * @return Returns the primitiveType.
     */
    public String getPrimitiveType() {
        return primitiveType;
    }

    /**
     * Maps a simple basetype name to a {@link BasetypeMapping}.
     * 
     * @param name
     *            name of the mapping
     * 
     * @return the mapping
     */
    public static JavaBasetypeMapping getByName(String name) {
        for (JavaBasetypeMapping current : values()) {
            if (name.compareTo(current.getName()) == 0) {
                return current;
            }
        }
        return null;
    }

}
