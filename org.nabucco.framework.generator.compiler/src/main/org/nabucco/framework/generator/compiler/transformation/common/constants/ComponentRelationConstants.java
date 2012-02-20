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
package org.nabucco.framework.generator.compiler.transformation.common.constants;

/**
 * ComponentRelationConstants
 * <p/>
 * Constants for component relation transformations.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface ComponentRelationConstants {

    final String COMPONENT_RELATION = "ComponentRelation";

    final String COMPONENT_RELATION_SERVICE = COMPONENT_RELATION + "Service";

    final String COMPONENT_RELATION_TYPE = COMPONENT_RELATION + "Type";

    final String CR_PACKAGE = "componentrelation";

    final String CR_INTERFACE = "org.nabucco.framework.base.facade.service.componentrelation.ComponentRelationService";

    final String CR_IMPLEMENTATION = "org.nabucco.framework.base.impl.service.componentrelation.ComponentRelationServiceImpl";

    final String CR_ENTITY_MANAGER = "entityManager";

    final String CR_TARGET = "target";

    final String CR_TARGET_ID = "target_id";

}
