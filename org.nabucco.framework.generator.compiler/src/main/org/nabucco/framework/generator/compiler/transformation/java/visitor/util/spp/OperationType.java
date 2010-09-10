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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp;

import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;

/**
 * OperationType
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public enum OperationType {

    /**
     * For getter Operations
     */
    GETTER("get"),
    /**
     * For setter Operations
     */
    SETTER("set");

    private String prefix;

    private OperationType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Formats a given String in a Operation typical manor.
     * 
     * @param property
     *            name of the accessed property
     * @return newly formated method name
     */
    public String format(String property) {
        return getPrefix() + NabuccoTransformationUtility.firstToUpper(property);
    }

    /**
     * @return Returns the prefix.
     */
    private String getPrefix() {
        return prefix;
    }

}
