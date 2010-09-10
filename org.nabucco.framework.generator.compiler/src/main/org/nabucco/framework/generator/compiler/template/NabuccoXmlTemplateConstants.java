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
package org.nabucco.framework.generator.compiler.template;

import org.nabucco.framework.mda.template.xml.XmlTemplateConstants;

/**
 * NabuccoXmlTemplateConstants
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface NabuccoXmlTemplateConstants extends XmlTemplateConstants {

    final String APPLICATION_TEMPLATE = "Application" + TEMPLATE;

    final String BUILDFILE_TEMPLATE = "Buildfile" + TEMPLATE;

    final String DATASOURCE_TEMPLATE = "Datasource" + TEMPLATE;

    final String EJB_JAR_TEMPLATE = EJB + TEMPLATE;

    final String EJB_JAR_FRAGMENT_TEMPLATE = EJB + FRAGMENT + TEMPLATE;

    final String EJB_JAR_EMPTY_TEMPLATE = EJB + EMPTY + TEMPLATE;

    final String EXCEPTION_FRAGMENT_TEMPLATE = EXCEPTION + FRAGMENT + TEMPLATE;

    final String ORM_TEMPLATE = ORM + TEMPLATE;

    final String ORM_FRAGMENT_TEMPLATE = ORM + FRAGMENT + TEMPLATE;

    final String ORM_EMPTY_TEMPLATE = ORM + EMPTY + TEMPLATE;

    final String PERSISTENCE_TEMPLATE = "Persistence" + TEMPLATE;

    /* JBoss Templates */

    final String JBOSS_APPLICATION_TEMPLATE = JBOSS + "Application" + TEMPLATE;

    final String JBOSS_TEMPLATE = JBOSS + "Application" + TEMPLATE;

    final String JBOSS_FRAGMENT_TEMPLATE = JBOSS + FRAGMENT + TEMPLATE;

    final String JBOSS_EMPTY_TEMPLATE = JBOSS + EMPTY + TEMPLATE;
    
}
