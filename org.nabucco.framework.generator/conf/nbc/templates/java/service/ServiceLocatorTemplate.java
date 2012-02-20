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

import javax.naming.Context;

import org.nabucco.framework.base.facade.datatype.logger.NabuccoLogger;
import org.nabucco.framework.base.facade.datatype.logger.NabuccoLoggingFactory;
import org.nabucco.framework.base.facade.service.ServiceLocator;

/**
 * ServiceLocatorTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ServiceLocatorTemplate extends ServiceLocator {

    /** The JNDI name. */
    private static final String JNDI_NAME = "nabucco/{0}/{1}/local";

    /** The Logger */
    private static final NabuccoLogger logger = NabuccoLoggingFactory.getInstance().getLogger(
            ServiceLocatorTemplate.class);

    /**
     * Locate the service via JNDI.
     * 
     * @param ctx
     *            the initial context, default context when null
     * 
     * @return the authorization service or a dummy implementation
     */
    public ServiceTemplate getService(Context ctx) {
        try {
            return (ServiceTemplate) super.locateService(JNDI_NAME, ctx);
        } catch (Exception e) {
            logger.warning(e, "Cannot locate Service from JNDI \'" + JNDI_NAME + "\', using a default implementation.");
            return new ServiceDefaultTemplate();
        }
    }
}
