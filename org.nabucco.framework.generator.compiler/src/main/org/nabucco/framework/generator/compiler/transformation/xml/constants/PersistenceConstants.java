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
package org.nabucco.framework.generator.compiler.transformation.xml.constants;

/**
 * PersistenceConstants
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface PersistenceConstants extends XmlConstants {

    final String ORM = "orm";
    
    final String ID = "id";
    
    final String TABLE = "table";

    final String COLUMN = "column";
    
    final String FETCH = "fetch";
    
    final String ENTITY = "entity";

    final String ATTRIBUTES = "attributes";

    final String PERSISTENCE_UNIT = "persistence-unit";
    
    final String MAPPED_SUPERCLASS = "mapped-superclass";
    
    final String EMBEDDABLE = "embeddable";

    final String JOIN_COLUMN = "join-column";

    final String INVERSE_JOIN_COLUMN = "inverse-join-column";

    final String JOIN_TABLE = "join-table";
    
    final String ATTRIBUTE_OVERRIDE = "attribute-override";

    final String TARGET_ENTITY = "target-entity";

    final String XPATH_ENTITY = "/entity-mappings/entity";

    final String XPATH_SUPERCLASS = "/entity-mappings/mapped-superclass";

    final String XPATH_EMBEDDABLE = "/entity-mappings/embeddable";
    
    final String XPATH_ID = "/entity-mappings/id";

    final String XPATH_TRANSIENT = "/entity-mappings/transient";
    
    final String XPATH_BASIC = "/entity-mappings/basic";
    
    final String XPATH_LOB = "/entity-mappings/lob";
    
    final String XPATH_ENUM = "/entity-mappings/enumerated";
    
    final String XPATH_VERSION = "/entity-mappings/version";
    
    final String XPATH_EMBEDDED = "/entity-mappings/embedded";

    final String XPATH_ONE_TO_ONE = "/entity-mappings/one-to-one";

    final String XPATH_MANY_TO_ONE = "/entity-mappings/many-to-one";

    final String XPATH_ONE_TO_MANY = "/entity-mappings/one-to-many";

    final String XPATH_MANY_TO_MANY = "/entity-mappings/many-to-many";

    final String FRAGMENT_ORDER_ENTITY = "2";
    
    final String FRAGMENT_ORDER_SUPERCLASS = "1";
    
    final String FRAGMENT_ORDER_EMBEDDABLE = "3";
    
    final String DEFAULT_COLUMN_LENGTH = "255";
    
}
