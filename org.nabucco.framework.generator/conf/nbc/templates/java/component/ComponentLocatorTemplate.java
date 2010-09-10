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

import org.nabucco.framework.base.facade.component.locator.ComponentLocator;
import org.nabucco.framework.base.facade.component.locator.ComponentLocatorSupport;

/**
 * ComponentLocatorTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ComponentLocatorTemplate extends ComponentLocatorSupport<ComponentTemplate> implements
        ComponentLocator<ComponentTemplate> {

    private static final String JNDI_NAME = ComponentLocator.COMPONENTS
            + "/" + ComponentTemplate.COMPONENT_NAME + "/"
            + "org.nabucco.framework.ComponentTemplate";

    private static ComponentLocatorTemplate instance;

    /**
     * Private constructor.
     */
    private ComponentLocatorTemplate(String jndiName, Class<ComponentTemplate> component) {
        super(jndiName, component);
    }

    /**
     * Singleton access.
     * 
     * @return the ComponentLocatorTemplate instance.
     */
    public static ComponentLocatorTemplate getInstance() {
        if (instance == null) {
            instance = new ComponentLocatorTemplate(JNDI_NAME, ComponentTemplate.class);
        }
        return instance;
    }

}