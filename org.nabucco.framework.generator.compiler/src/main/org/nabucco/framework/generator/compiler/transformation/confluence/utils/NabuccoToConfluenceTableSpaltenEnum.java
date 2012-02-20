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

import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceConstants;

/**
 * NabuccoToConfluenceTableSpaltenEnum
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public enum NabuccoToConfluenceTableSpaltenEnum {

    MULTIPLICITY(NabuccoToConfluenceConstants.MULTIPLICITY),

    NAME(NabuccoToConfluenceConstants.NAME),

    TYPE(NabuccoToConfluenceConstants.TYPE),

    ICON(NabuccoConfluenceStringManipulator.CONF_EMPTY),

    EXCEPTION(NabuccoToConfluenceConstants.EXCEPTION),

    ASSOCIATION_STRATEGY(NabuccoToConfluenceConstants.ASSOCIATION_STRATEGY),

    TRANSIENT(NabuccoToConfluenceConstants.TRANSIENT),

    DESCRIPTION(NabuccoToConfluenceConstants.DESCRIPTION),

    REQUEST(NabuccoToConfluenceConstants.REQUEST),

    RESPONCE(NabuccoToConfluenceConstants.RESPONCE),

    MANUAL_IMPLEMENTATION("Implementation"),

    ACCESS(NabuccoToConfluenceConstants.ACCESS),

    PERSISTENCE(NabuccoToConfluenceConstants.PERSISTENCE);

    private String value;

    private NabuccoToConfluenceTableSpaltenEnum(String value) {
        this.value = value;
    }

    /**
     * Return a value of Enum to print
     * 
     * @return
     */
    public String getValue() {
        return this.value;
    }
}
