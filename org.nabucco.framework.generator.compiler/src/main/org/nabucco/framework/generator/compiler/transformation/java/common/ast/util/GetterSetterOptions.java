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
package org.nabucco.framework.generator.compiler.transformation.java.common.ast.util;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.FetchStrategyType;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionImplementationType;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionType;

/**
 * GetterSetterOptions
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class GetterSetterOptions {

    /** The default collection type (LIST). */
    private static final CollectionType DEFAULT_COLLECTION_TYPE = CollectionType.LIST;

    /** The default collection implementation type (NABUCCO). */
    private static final CollectionImplementationType DEFAULT_COLLECTION_IMPL_TYPE = CollectionImplementationType.NABUCCO;

    /** The default fetch type (LAZY). */
    private static final FetchStrategyType DEFAULT_FETCH_TYPE = FetchStrategyType.LAZY;
    
    private CollectionType collectionType;

    private CollectionImplementationType collectionImplType;

    private FetchStrategyType fetchType;

    /**
     * Getter for the collection type. Default is LIST
     * 
     * @return Returns the collectionType.
     */
    public CollectionType getCollectionType() {
        if (this.collectionType == null) {
            this.collectionType = DEFAULT_COLLECTION_TYPE;
        }
        return this.collectionType;
    }

    /**
     * Setter for the collection type. Default is LIST.
     * 
     * @param collectionType
     *            The collectionType to set.
     */
    public void setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
    }

    /**
     * Getter for the collection implementation type. Default is NABUCCO.
     * 
     * @return Returns the collectionImplType.
     */
    public CollectionImplementationType getCollectionImplementationType() {
        if (this.collectionImplType == null) {
            this.collectionImplType = DEFAULT_COLLECTION_IMPL_TYPE;
        }
        return this.collectionImplType;
    }

    /**
     * setter for the collection implementation type. Default is NABUCCO.
     * 
     * @param collectionImplType
     *            The collectionImplType to set.
     */
    public void setCollectionImplementationType(CollectionImplementationType collectionImplType) {
        this.collectionImplType = collectionImplType;
    }

    /**
     * Getter for the fetch type. Default is LAZY.
     * 
     * @return Returns the fetchType.
     */
    public FetchStrategyType getFetchType() {
        if (this.fetchType == null) {
            this.fetchType = DEFAULT_FETCH_TYPE;
        }
        return this.fetchType;
    }

    /**
     * setter for the fetch type. Default is LAZY.
     * 
     * @param fetchType
     *            The fetchType to set.
     */
    public void setFetchType(FetchStrategyType fetchType) {
        this.fetchType = fetchType;
    }

    /**
     * Returns an initialized {@link GetterSetterOptions} instance. Depending on the argument the
     * object is initialized with a value.
     * 
     * @param parameter
     *            optional properties to set (must be an instance of {@link CollectionType},
     *            {@link CollectionImplementationType}, {@link FetchStrategyType}
     * 
     * @return the initialized instance
     */
    public static <P extends Enum<?>> GetterSetterOptions valueOf(P... parameters) {
        GetterSetterOptions options = new GetterSetterOptions();

        if (parameters == null) {
            return options;
        }
        for (P parameter : parameters) {
            if (parameter instanceof FetchStrategyType) {
                options.setFetchType((FetchStrategyType) parameter);
            } else if (parameter instanceof CollectionType) {
                options.setCollectionType((CollectionType) parameter);
            } else if (parameter instanceof CollectionImplementationType) {
                options.setCollectionImplementationType((CollectionImplementationType) parameter);
            }
        }

        return options;
    }
}
