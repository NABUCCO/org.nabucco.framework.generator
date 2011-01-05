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
package org.nabucco.framework.generator.compiler.transformation.java.constants;

/**
 * CollectionConstants
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface CollectionConstants extends JavaConstants {

    final String COLLECTION = "Collection";
    
    final String LIST = "List";

    final String SET = "Set";

    final String MAP = "Map";
    
    final String NABUCCO_COLLECTION = "NabuccoCollection";
    
    final String NABUCCO_LIST = "NabuccoList";

    final String NABUCCO_SET = "NabuccoSet";

    final String NABUCCO_MAP = "NabuccoMap";

    final String DEFAULT_LIST = "ArrayList";

    final String DEFAULT_SET = "HashSet";

    final String DEFAULT_MAP = "HashMap";

    final String COLLECTION_STATE = "NabuccoCollectionState";

    final String COLLECTION_STATE_INIT = "INITIALIZED";
    
    final String COLLECTION_STATE_LAZY = "LAZY";
    
    final String COLLECTION_STATE_EAGER = "EAGER";
    
    final String GET_DELEGATE = "getDelegate";
    
    final String SET_DELEGATE = "setDelegate";
    
    final String SUFFIX_JPA = "JPA";

    final String IMPORT_LIST = "java.util.List";

    final String IMPORT_SET = "java.util.Set";

    final String IMPORT_MAP = "java.util.Map";
    
    final String IMPORT_ITERATOR = "java.util.Iterator";

    final String IMPORT_NABUCCO_LIST = "org.nabucco.framework.base.facade.datatype.collection.NabuccoList";

    final String IMPORT_NABUCCO_SET = "org.nabucco.framework.base.facade.datatype.collection.NabuccoSet";

    final String IMPORT_NABUCCO_MAP = "org.nabucco.framework.base.facade.datatype.collection.NabuccoMap";

    final String IMPORT_DEFAULT_LIST = "java.util.ArrayList";

    final String IMPORT_DEFAULT_SET = "java.util.HashSet";

    final String IMPORT_DEFAULT_MAP = "java.util.HashMap";
    
    final String IMPORT_NABUCCO_COLLECTION_STATE = "org.nabucco.framework.base.facade.datatype.collection.NabuccoCollectionState";

}
