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
 * QueryFilterConstants
 * <p/>
 * Constants for query filter transformations.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface QueryFilterConstants {

    final String QUERY_FILTER = "QueryFilter";

    final String QUERY_FILTER_SERVICE = QUERY_FILTER + "Service";

    final String QF_PACKAGE = "queryfilter";

    final String QF_INTERFACE = "org.nabucco.framework.base.facade.service.queryfilter.QueryFilterService";

    final String QF_IMPLEMENTATION = "org.nabucco.framework.base.impl.service.queryfilter.QueryFilterServiceImpl";

    final String QF_ENTITY_MANAGER = "entityManager";

}
