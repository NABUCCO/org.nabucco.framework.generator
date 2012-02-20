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
/*
 * Copyright 2011 PRODYNA AG
 *
 * Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/eclipse-1.0.php or
 * http://nabuccosource.org/License.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.nabucco.framework.base.facade.component.connection.ConnectionException;
import org.nabucco.framework.base.facade.component.locator.AdapterLocator;
import org.nabucco.framework.base.facade.component.locator.AdapterLocatorSupport;

/**
 * AdapterLocatorTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class AdapterLocatorTemplate extends AdapterLocatorSupport<AdapterTemplate> implements
        AdapterLocator<AdapterTemplate> {

    private static AdapterLocatorTemplate instance;

    /**
     * Private constructor.
     */
    private AdapterLocatorTemplate(String jndiName, Class<AdapterTemplate> adapter) {
        super(jndiName, adapter);
    }

    /**
     * Singleton access.
     * 
     * @return the AdapterLocatorTemplate instance.
     */
    public static AdapterLocatorTemplate getInstance() {
        if (instance == null) {
            instance = new AdapterLocatorTemplate(AdapterInterface.JNDI_NAME, AdapterTemplate.class);
        }
        return instance;
    }

    @Override
    public AdapterInterface getAdapter() throws ConnectionException {
        AdapterInterface adapter = super.getAdapter();

        if (adapter instanceof AdapterInterfaceLocal) {
            return new AdapterAdapterLocalProxy((AdapterInterfaceLocal) adapter);
        }

        return adapter;
    }

}
