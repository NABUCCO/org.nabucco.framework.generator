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
package org.nabucco.framework.generator.compiler.transformation.common.collection;

import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;

/**
 * CollectionImplementationType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum CollectionImplementationType {

    /**
     * NABUCCO collection implementation.
     */
    NABUCCO(CollectionConstants.NABUCCO_LIST, CollectionConstants.NABUCCO_SET, CollectionConstants.NABUCCO_MAP),

    /**
     * Java default collection implementation.
     */
    DEFAULT(CollectionConstants.LIST, CollectionConstants.SET, CollectionConstants.MAP);

    private CollectionImplementationType(String list, String set, String map) {
        this.list = list;
        this.set = set;
        this.map = map;
    }

    private String list;

    private String set;

    private String map;

    /**
     * Getter for the list.
     * 
     * @return Returns the list.
     */
    public String getList() {
        return this.list;
    }

    /**
     * Getter for the set.
     * 
     * @return Returns the set.
     */
    public String getSet() {
        return this.set;
    }

    /**
     * Getter for the map.
     * 
     * @return Returns the map.
     */
    public String getMap() {
        return this.map;
    }

}
