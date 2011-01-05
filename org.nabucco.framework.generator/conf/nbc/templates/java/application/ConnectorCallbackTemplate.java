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
package org.nabucco.framework.showcase.server.connector;

/**
 * ConnectorCallbackTemplate
 * <p/>
 * Holding connector callback methods that must be generated per target service operation.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class ConnectorCallbackTemplate {

    @Override
    public void internalConnect() throws NabuccoException {
        ServiceMessageTemplate requestMessage = new ServiceMessageTemplate();
        this.preServiceOperation(requestMessage);

        ServiceMessageTemplate responseMessage = this.invoke(requestMessage);
        this.postServiceOperation(responseMessage);
    }

    /**
     * Invoke the {@link ServiceTemplate#serviceOperation(ServiceRequest)} service.
     * 
     * @param requestMessage
     *            the request message
     * @return the response message
     * 
     * @throws NabuccoException
     *             if the invocation was unsuccessful
     */
    private ServiceMessageTemplate invoke(ServiceMessageTemplate requestMessage)
            throws NabuccoException {

        ComponentTemplate component = ComponentLocatorTemplate.getInstance().getComponent();

        ServiceTemplate service = component.getService();

        ServiceRequest<ServiceMessageTemplate> request = new ServiceRequest<ServiceMessageTemplate>(
                super.getServiceContext());

        request.setRequestMessage(requestMessage);

        ServiceResponse<ServiceMessageTemplate> response = service.serviceOperation(request);

        return response.getResponseMessage();
    }

    /**
     * Called before invocation of {@link ServiceTemplate}.
     * 
     * @param requestMessage
     *            the empty request message
     * 
     * @throws ConnectorException
     */
    protected abstract void preServiceOperation(ServiceMessageTemplate requestMessage)
            throws ConnectorException;

    /**
     * Called after invocation of {@link ServiceTemplate}.
     * 
     * @param responseMessage
     *            the response message
     * 
     * @throws ConnectorException
     */
    protected abstract void postServiceOperation(ServiceMessageTemplate responseMessage)
            throws ConnectorException;

}
