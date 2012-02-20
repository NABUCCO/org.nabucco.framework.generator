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
package org.nabucco.framework.generator.compiler.transformation.xml.constants;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;

/**
 * XmlConstants
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface XmlConstants extends NabuccoTransformationConstants {

    final String XML = "xml";

    final String NAME = "name";

    final String LENGTH = "length";

    final String NULLABLE = "nullable";

    final String FRAGMENT = "fragment";

    final String TYPE = "type";

    final String ORDER = "order";

    final String PERSISTENCE = "persistence";

    final String ENTITY_MANAGER = "EntityManager";
    
    final String SESSION_CONTEXT = "SessionContext";

    final String EJB = "ejb";

    final String XPATH_SEPARATOR = "/";

    final String JNDI_PREFIX = "nabucco" + XPATH_SEPARATOR;
    
    final String COMPONENT_NAME = "ComponentName";
}
