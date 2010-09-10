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

/**
 * NabuccoToJavaBasetypeReferences
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class NabuccoToJavaBasetypeReferences {

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoToJavaBasetypeReferences() {
    }

    /**
     * Returns the basetype import string for the specified basetype;
     * 
     * @param name
     *            name of the basetype
     * 
     * @return the import string
     */
    public static String getBasetypeReference(String name) {
        JavaBasetypeMapping mappedType = NabuccoToJavaBasetypeMapping
                .getByName(name);
        if (mappedType != null) {
            return mappedType.getWrapperClass();
        }
        return null;
    }

    /**
     * Checks whether a type is a basetype.
     * 
     * @param name
     *            name of the basetype
     * 
     * @return true if it is a basetype, false otherwise
     */
    public static boolean isBasetypeReference(String name) {
        return NabuccoToJavaBasetypeMapping.getByName(name) != null;
    }

    /**
     * Returns the java type for a specified basetype.
     * 
     * @param basetype
     *            name of the basetype
     * 
     * @return the java type for the basetype (non java.lang.* basetype are returned qualified).
     */
    public static String mapToJavaType(String basetype) {
        JavaBasetypeMapping type = NabuccoToJavaBasetypeMapping
                .getByName(basetype);
        if (type != null) {
            return type.getJavaClass();
        }
        return null;
    }
}
