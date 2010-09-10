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
package org.nabucco.framework.generator.compiler.transformation.util;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;

/**
 * NabuccoTransformationUtility
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class NabuccoTransformationUtility implements NabuccoTransformationConstants {

    private static final String FACADE = PKG_SEPARATOR + PKG_FACADE + PKG_SEPARATOR;

    private static final String IMPL = PKG_SEPARATOR + PKG_IMPL + PKG_SEPARATOR;

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoTransformationUtility() {
    }

    /**
     * Changes the first character of a string to upper case.
     * 
     * @param name
     *            the name to change
     * 
     * @return the changed name
     */
    public static String firstToUpper(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Changes the first character of a string to lower case.
     * 
     * @param name
     *            the name to change
     * 
     * @return the changed name
     */
    public static String firstToLower(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    /**
     * Changes the first character of a string to upper case.
     * 
     * @param name
     *            the name to change
     * 
     * @return the changed name
     */
    public static char[] firstToUpper(char[] name) {
        if (name == null || name.length == 0) {
            return name;
        }

        Character.toUpperCase(name[0]);
        return name;
    }

    /**
     * Changes the first character of a string to lower case.
     * 
     * @param name
     *            the name to change
     * 
     * @return the changed name
     */
    public static char[] firstToLower(char[] name) {
        if (name == null || name.length == 0) {
            return name;
        }

        Character.toLowerCase(name[0]);
        return name;
    }

    /**
     * Converts a camelCase string into a database_string.
     * 
     * @param name
     *            the name as camel case
     * 
     * @return the name as database representation
     */
    public static String toTableName(String name) {

        if (name == null || name.length() < 1) {
            return name;
        }

        char[] charName = name.toCharArray();
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(charName[0]));

        for (int i = 1; i < charName.length; i++) {
            if (Character.isUpperCase(charName[i])) {
                result.append(TABLE_SEPARATOR);
                result.append(Character.toLowerCase(charName[i]));
            } else {
                result.append(charName[i]);
            }
        }

        return result.toString();
    }

    /**
     * Converts a facade package to an implementation package.
     * 
     * @param pkg
     *            the facade package string
     * 
     * @return the implementation package string
     */
    public static String toImpl(String pkg) {
        return pkg.replace(FACADE, IMPL);
    }

    /**
     * Converts an implementation package to a facade package.
     * 
     * @param pkg
     *            the implementation package string
     * 
     * @return the facade package string
     */
    public static String toFacade(String pkg) {
        return pkg.replace(IMPL, FACADE);
    }
}
