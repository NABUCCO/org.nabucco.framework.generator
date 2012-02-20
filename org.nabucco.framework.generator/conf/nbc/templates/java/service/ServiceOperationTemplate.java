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

import org.nabucco.framework.base.facade.datatype.NabuccoSystem;

/**
 * ServiceOperationTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ServiceOperationTemplate {

    ServiceResponse<ServiceMsgTemplate> serviceInterfaceOperation(ServiceRequest<ServiceMsgTemplate> rq)
            throws ServiceException;

    @Override
    public ServiceResponse<ServiceMsgTemplate> serviceImplementationOperation(ServiceRequest<ServiceMsgTemplate> rq)
            throws ServiceException {

        if (this.handler == null) {
            super.getLogger().error("No service implementation configured for {0}().");
            throw new InjectionException("No service implementation configured for {0}().");
        }

        ServiceResponse<ServiceMsgTemplate> rs;

        this.handler.init();
        rs = this.handler.invoke(rq);
        this.handler.finish();

        return rs;
    }

    public ServiceMsgTemplate serviceDelegateOperationRcpTemplate(ServiceMsgTemplate message,
            ServiceSubContext... subContexts) throws ClientException {

        ServiceRequest<ServiceMsgTemplate> request = new ServiceRequest<ServiceMsgTemplate>(
                super.createServiceContext(subContexts));

        request.setRequestMessage(message);

        ServiceResponse<ServiceMsgTemplate> response = null;
        Exception exception = null;

        if (service != null) {
            super.handleRequest(request);

            long start = NabuccoSystem.getCurrentTimeMillis();

            try {
                response = service.serviceOperationTemplate(request);
            } catch (Exception e) {
                exception = e;
            } finally {
                long end = NabuccoSystem.getCurrentTimeMillis();
                long duration = end - start;
                super.monitorResult(ServiceTemplate.class, "serviceDelegateOperationWebTemplate", duration, exception);
            }

            if (response != null) {
                super.handleResponse(response);
                return response.getResponseMessage();
            }
        }

        throw new ClientException("Cannot execute service operation: ServiceTemplate.serviceOperationTemplate");
    }

    public ServiceMsgTemplate serviceDelegateOperationWebTemplate(ServiceMsgTemplate message, NabuccoSession session,
            ServiceSubContext... subContexts) throws ServiceException {

        ServiceRequest<ServiceMsgTemplate> request = new ServiceRequest<ServiceMsgTemplate>(super.createServiceContext(
                session, subContexts));

        request.setRequestMessage(message);

        ServiceResponse<ServiceMsgTemplate> response = null;
        Exception exception = null;

        if (this.service != null) {
            super.handleRequest(request, session);

            long start = NabuccoSystem.getCurrentTimeMillis();

            try {
                response = service.serviceOperationTemplate(request);
            } catch (Exception e) {
                exception = e;
            } finally {
                long end = NabuccoSystem.getCurrentTimeMillis();
                long duration = end - start;
                super.monitorResult(ServiceTemplate.class, "serviceDelegateOperationWebTemplate", duration, exception);
            }

            if (response != null) {
                super.handleResponse(response, session);
                return response.getResponseMessage();
            }
        }

        throw new ServiceException("Cannot execute service operation: ServiceTemplate.serviceOperationTemplate");
    }
}
