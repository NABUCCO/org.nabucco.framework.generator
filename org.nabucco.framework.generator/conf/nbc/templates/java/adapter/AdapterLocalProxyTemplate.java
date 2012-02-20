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

import org.nabucco.framework.base.facade.exception.service.ServiceException;
import org.nabucco.framework.base.facade.message.ping.PingRequest;
import org.nabucco.framework.base.facade.message.ping.PingResponse;

/**
 * AdapterLocalProxyTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class AdapterLocalProxyTemplate {

    private static final long serialVersionUID = 1L;

    private final AdapterLocalTemplate delegate;

    /**
     * Creates a new {@link AdapterLocalProxyTemplate} instance.
     * 
     * @param delegate
     *            the local adapter
     */
    public AdapterLocalProxyTemplate(AdapterLocalTemplate delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Cannot create local proxy for adapter [null].");
        }
        this.delegate = delegate;
    }
    
    @Override
    public PingResponse ping(PingRequest request) {
        return this.delegate.ping(request);
    }

    @Override
    public String getId() {
        return this.delegate.getId();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public String getJndiName() {
        return this.delegate.getJndiName();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }
}
