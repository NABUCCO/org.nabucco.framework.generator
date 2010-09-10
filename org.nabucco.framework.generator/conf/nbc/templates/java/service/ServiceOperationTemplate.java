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

import org.nabucco.framework.base.facade.service.injection.InjectionException;

/**
 * ServiceOperationTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ServiceOperationTemplate {

    ServiceResponse<ServiceMsgTemplate> serviceInterfaceOperation(
            ServiceRequest<ServiceMsgTemplate> rq) throws ServiceException;

    @Override
    public ServiceResponse<ServiceMsgTemplate> serviceImplementationOperation(
            ServiceRequest<ServiceMsgTemplate> rq) throws ServiceException {

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

    public ServiceMsgTemplate serviceDelegateOperationRcpTemplate(ServiceMsgTemplate rq)
            throws ServiceException {

        ServiceRequest<ServiceMsgTemplate> request = new ServiceRequest<ServiceMsgTemplate>(
                super.createServiceContext());

        request.setRequestMessage(rq);

        ServiceResponse<ServiceMsgTemplate> rs;
        if (service != null) {
            long start = System.currentTimeMillis();
            rs = service.serviceOperationTemplate(request);
            long end = System.currentTimeMillis();

            Activator.getDefault().logDebug(
                    new NabuccoLogMessage(LoginServiceDelegate.class, "Service: ",
                            "ServiceTemplate.serviceOperationTemplate", " Time: ", String
                                    .valueOf(end - start), "ms."));
        } else {
            throw new ServiceException(
                    "Cannot execute service operation: ServiceTemplate.serviceOperationTemplate");
        }

        return rs.getResponseMessage();
    }

    public ServiceMsgTemplate serviceDelegateOperationWebTemplate(ServiceMsgTemplate rq)
            throws ServiceException {

        ServiceRequest<ServiceMsgTemplate> request = new ServiceRequest<ServiceMsgTemplate>(
                super.createServiceContext());

        request.setRequestMessage(rq);

        ServiceResponse<ServiceMsgTemplate> rs;
        if (service != null) {
            rs = service.serviceOperationTemplate(request);

        } else {
            throw new ServiceException(
                    "Cannot execute service operation: ServiceTemplate.serviceOperationTemplate");
        }

        return rs.getResponseMessage();
    }

    public ServiceMsgTemplate serviceDelegateOperationWebTemplate(ServiceMsgTemplate rq,
            Subject subject) throws ServiceException {

        ServiceRequest<ServiceMsgTemplate> request = new ServiceRequest<ServiceMsgTemplate>(
                super.createServiceContext(subject));

        request.setRequestMessage(rq);

        ServiceResponse<ServiceMsgTemplate> rs;
        if (service != null) {
            rs = service.serviceOperationTemplate(request);

        } else {
            throw new ServiceException(
                    "Cannot execute service operation: ServiceTemplate.serviceOperationTemplate");
        }

        return rs.getResponseMessage();
    }

}
