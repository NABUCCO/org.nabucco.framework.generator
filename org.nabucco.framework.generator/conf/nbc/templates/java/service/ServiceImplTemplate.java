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

import org.nabucco.framework.base.impl.service.ServiceSupport;
import org.nabucco.framework.base.facade.message.ServiceResponse;
import org.nabucco.framework.base.facade.message.ServiceRequest;
import org.nabucco.framework.base.facade.service.injection.InjectionException;
import org.nabucco.framework.base.facade.service.injection.InjectionProvider;

/**
 * ServiceImplTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ServiceImplTemplate extends ServiceSupport {

    private static final long serialVersionUID = 1L;
 
    private static final String ID = "ServiceImplTemplate";
    
    public void postConstruct() {
        InjectionProvider injector = InjectionProvider.getInstance(ID);
    }

    public void preDestroy() {

    }
    
}
