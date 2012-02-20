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
package org.nabucco.framework.generator.compiler.transformation.confluence.utils;

/**
 * NabuccoConfluenceStringManipulator
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoConfluenceStringManipulator {

    public static final String CONF_EMPTY = " ";

    public static final String EMPTY = "";

    public static final String THROWS = "throws";

    /**
     * Standard formatting
     * 
     * @param token
     * @return
     */
    public static String normalizeString(String token) {
        return normalizeString(token, NabuccoConfluenceTextFormat.TEXT);
    }

    /**
     * Normalizes the string depending on the formattype e.g. delete square braces by multiplicity
     * 
     * @param token
     *            String token to normalize
     * @return normalized string token
     */
    public static String normalizeString(String token, NabuccoConfluenceTextFormat format) {
        String retVal = token;

        if (retVal.equals(EMPTY)) {
            retVal = CONF_EMPTY;
        }

        switch (format) {
        case MULTIPLICITY:
            retVal = retVal.replace("[", EMPTY).replace("]", EMPTY);
            break;
        case EXCEPTION:
            retVal = retVal.replace(THROWS, EMPTY).trim();
            break;
        default:
            break;
        }

        return retVal;
    }
}
