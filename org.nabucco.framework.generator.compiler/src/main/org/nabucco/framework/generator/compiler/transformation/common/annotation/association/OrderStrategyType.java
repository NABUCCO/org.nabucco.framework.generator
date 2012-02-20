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
package org.nabucco.framework.generator.compiler.transformation.common.annotation.association;

import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionType;

/**
 * OrderStrategyType
 * <p/>
 * The order strategy defines whether a collection between datatypes is ordered (list), unordered
 * (set) or mapped (map).
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum OrderStrategyType {

    ORDERED("ORDERED", "ordered", CollectionType.LIST),

    UNORDERED("UNORDERED", "unordered", CollectionType.SET),

    MAPPED("MAPPED", "mapped", CollectionType.MAP);

    private String id;

    private String name;

    private CollectionType collectionType;

    /**
     * Creates a new {@link OrderStrategyType} instance.
     * 
     * @param id
     *            the id of the strategy
     * @param name
     *            the name of the strategy
     * @param collectionType
     *            the type of the associated collection
     */
    private OrderStrategyType(String id, String name, CollectionType collectionType) {
        this.id = id;
        this.name = name;
        this.collectionType = collectionType;
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
     * Getter for the collectionType.
     * 
     * @return Returns the collectionType.
     */
    public CollectionType getCollectionType() {
        return this.collectionType;
    }

    /**
     * Maps the specified string to the {@link OrderStrategyType} enum.
     * 
     * @param value
     *            the value as string
     * 
     * @return the type, or null if it cannot be mapped
     */
    public static OrderStrategyType getType(String value) {
        if (MAPPED.id.equalsIgnoreCase(value)) {
            return MAPPED;
        } else if (UNORDERED.id.equalsIgnoreCase(value)) {
            return UNORDERED;
        }
        return ORDERED;
    }
}
