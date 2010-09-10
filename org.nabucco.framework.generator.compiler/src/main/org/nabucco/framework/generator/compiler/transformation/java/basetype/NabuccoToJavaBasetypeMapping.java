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
package org.nabucco.framework.generator.compiler.transformation.java.basetype;

import java.util.HashSet;
import java.util.Set;

/**
 * BaseTypeMapping
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public enum NabuccoToJavaBasetypeMapping implements JavaBasetypeMapping {

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

    private static final Set<JavaBasetypeMapping> EXTENSIONS = new HashSet<JavaBasetypeMapping>();

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
    private String javaClass;

    NabuccoToJavaBasetypeMapping(String name, String javaClass, String javaType) {
        this.name = name;
        this.javaClass = javaType;
        this.wrapperClass = javaClass;
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
     * @return Returns the javaClass.
     */
    public String getJavaClass() {
        return javaClass;
    }

    /**
     * Maps a simple basetype name to a {@link NabuccoToJavaBasetypeMapping}.
     * 
     * @param name
     *            name of the mapping
     * 
     * @return the mapping
     */
    public static JavaBasetypeMapping getByName(String name) {
        // extensions first use-case -> overrule existing entry
        for (JavaBasetypeMapping current : EXTENSIONS) {
            if (name.compareTo(current.getName()) == 0) {
                return current;
            }
        }
        for (JavaBasetypeMapping current : values()) {
            if (name.compareTo(current.getName()) == 0) {
                return current;
            }
        }
        return null;
    }

    /**
     * Outside access to add additional or overwrite existing entries
     * 
     * @param contribution
     */
    public static void addExtension(JavaBasetypeMapping contribution) {
        EXTENSIONS.add(contribution);
    }

}
