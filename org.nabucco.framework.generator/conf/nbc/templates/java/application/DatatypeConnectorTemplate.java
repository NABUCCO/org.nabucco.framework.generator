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

import java.util.List;

import org.nabucco.framework.base.facade.component.Component;
import org.nabucco.framework.base.facade.component.application.connector.ConnectorException;
import org.nabucco.framework.base.facade.component.application.connector.DatatypeConnector;
import org.nabucco.framework.base.facade.component.application.connector.DatatypeConnectorSupport;
import org.nabucco.framework.base.facade.datatype.componentrelation.ComponentRelation;
import org.nabucco.framework.base.facade.datatype.componentrelation.ComponentRelationType;
import org.nabucco.framework.base.facade.exception.NabuccoException;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.message.ServiceResponse;

/**
 * ConnectorTemplate
 * <p/>
 * The empty connector template holding the connector blueprint.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class DatatypeConnectorTemplate extends DatatypeConnectorSupport implements
DatatypeConnector {

    private static final long serialVersionUID = 1L;

    @Override
    public Class<?> getSourceDatatypeClass() {
        return Datatype.class;
    }

    @Override
    protected List<ComponentRelationType> getRelationTypes() {
        return ComponentRelationType.valuesBySource(this.getSourceDatatypeClass());
    }

    @Override
    protected Component lookupTargetComponent(ComponentRelationType relationType) throws NabuccoException {
        if (relationType instanceof ComponentRelationType) {
            switch ((ComponentRelationType) relationType) {

            case CASE:
                return ComponentLocator.getInstance().getComponent();
            }
        }
        return null;
    }
    

    @Override
    protected void internalMaintain(ComponentRelation<?> relation) throws NabuccoException {
        if (relation.getRelationType() instanceof ComponentRelationType) {
            switch ((ComponentRelationType) relation.getRelationType()) {

            }
        }
    }


    @Override
    protected void internalResolve(ComponentRelation<?> relation) throws NabuccoException {
        if (relation.getRelationType() instanceof ComponentRelationType) {
            switch ((ComponentRelationType) relation.getRelationType()) {

            }
        }
    }
    
}
