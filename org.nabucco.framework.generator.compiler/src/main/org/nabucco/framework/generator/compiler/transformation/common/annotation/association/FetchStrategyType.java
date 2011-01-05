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
package org.nabucco.framework.generator.compiler.transformation.common.annotation.association;

/**
 * FetchStrategyType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum FetchStrategyType {

    LAZY("LAZY", "Lazy"),

    EAGER("EAGER", "Eager");

    private String id;

    private String name;

    /**
     * Creates a new {@link FetchStrategyType} instance.
     * 
     * @param id
     *            the id
     * @param name
     *            the name
     */
    private FetchStrategyType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Getter for the association ID.
     * 
     * @return Returns the id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for the association name.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Maps the specified string to the {@link FetchStrategyType} enum.
     * 
     * @param value
     *            the value as string
     * 
     * @return the type, or null if it cannot be mapped
     */
    public static FetchStrategyType getType(String value) {
        if (EAGER.id.equalsIgnoreCase(value)) {
            return EAGER;
        }
        return LAZY;
    }

}
