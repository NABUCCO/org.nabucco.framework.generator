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
 * Copyright 2010 PRODYNA AG
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

import org.nabucco.framework.base.facade.exception.NabuccoException;
import org.nabucco.framework.base.impl.service.ServiceHandler;
import org.nabucco.framework.base.impl.service.ServiceHandlerSupport;
import org.nabucco.framework.base.facade.exception.service.ServiceException;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.message.ServiceResponse;

/**
 * ServiceHandlerInterfaceTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class AbstractServiceHandlerTemplate extends ServiceHandlerSupport implements
        ServiceHandler {

    private static final long serialVersionUID = 1L;

    private static final String ID = "AbstractServiceHandlerTemplate";
    
    /**
     * Static ID
     */
    protected static String getId() {
        return ID;
    }

    /**
     * Invoke method.
     */
    protected ServiceResponse<ServiceMessage> invoke(ServiceRequest<ServiceMessage> rq) throws ServiceException {
        ServiceResponse<ServiceMessage> rs;
        ServiceMessage msg;
        
        try {
            this.validateRequest(rq);
            this.setContext(rq.getContext());
            msg = this.serviceHandlerOperation(rq.getRequestMessage());
            
            if (msg == null) {
                super.getLogger().warning("No response message defined.");
            } else {
                super.cleanServiceMessage(msg);
            }

            rs = new ServiceResponse<ServiceMessage>(rq.getContext());
            rs.setResponseMessage(msg);
            return rs;
        } catch (ServiceException e) {
            super.getLogger().error(e);
            throw e;
        } catch (NabuccoException e) {
            super.getLogger().error(e);
            ServiceException wrappedException = new ServiceException(e);
            throw wrappedException;
        } catch (Exception e) {
            super.getLogger().error(e);
            throw new ServiceException("Error during service invocation.", e);
        }
    }
    
    /**
     * Abstract invoke method.
     */
    protected abstract ServiceMessage serviceHandlerOperation(ServiceMessage msg)
            throws ServiceException;
    
}
