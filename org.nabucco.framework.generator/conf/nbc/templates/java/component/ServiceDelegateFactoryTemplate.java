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

import org.nabucco.framework.base.facade.component.connection.Connection;
import org.nabucco.framework.base.facade.component.connection.ConnectionException;
import org.nabucco.framework.base.facade.component.connection.ConnectionFactory;
import org.nabucco.framework.base.facade.component.connection.ConnectionSpecification;
import org.nabucco.framework.base.facade.exception.service.ServiceException;
import org.nabucco.framework.base.facade.exception.client.ClientException;

/**
 * ServiceDelegateFactoryTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ServiceDelegateFactoryTemplate {

    private static ServiceDelegateTemplate instance = new ServiceDelegateTemplate();

    private ComponentTemplate component;

    private ServiceDelegateFactoryTemplate() {
    }

    public static ServiceDelegateTemplate getInstance() {
        return instance;
    }

    private ComponentTemplate getComponent() throws ConnectionException {
        if (this.component == null) {
            this.initComponent();
        }
        return this.component;
    }

    private void initComponent() throws ConnectionException {
        ConnectionSpecification specification = ConnectionSpecification.getCurrentSpecification();
        Connection connection = ConnectionFactory.getInstance().createConnection(specification);
        this.component = ComponentLocatorTemplate.getInstance().getComponent(connection);
    }

}