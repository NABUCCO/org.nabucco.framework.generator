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

import org.nabucco.framework.base.facade.component.application.connector.ConnectorException;
import org.nabucco.framework.base.facade.component.application.connector.ConnectorStrategy;
import org.nabucco.framework.base.facade.component.application.connector.ServiceConnectorSupport;
import org.nabucco.framework.base.facade.exception.NabuccoException;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.message.ServiceResponse;
import org.nabucco.framework.base.facade.service.signature.ServiceOperationSignature;

/**
 * ConnectorTemplate
 * <p/>
 * The empty connector template holding the connector blueprint.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class ServiceConnectorTemplate extends ServiceConnectorSupport {

    private static final long serialVersionUID = 1L;

    private static final String SOURCE_SERVICE = "Service";

    private static final String SOURCE_OPERATION = "Operation";

    private static final String SOURCE_MESSAGE = "Message";

    private static final ServiceOperationSignature SOURCE_SIGNATURE = new ServiceOperationSignature(
            SOURCE_SERVICE, SOURCE_OPERATION, SOURCE_MESSAGE);
    
    /**
     * Creates a new {@link ServiceConnectorTemplate} instance.
     */
    protected ServiceConnectorTemplate() {
        super(ConnectorStrategy.AFTER);
    }

    @Override
    public final ServiceOperationSignature getSourceOperationSignature() {
        return SOURCE_SIGNATURE;
    }

    @Override
    protected void internalConnect() throws NabuccoException {
    }
}
