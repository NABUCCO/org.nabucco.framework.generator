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

import org.nabucco.framework.base.facade.message.ServiceMessage;
import org.nabucco.framework.base.facade.message.ServiceMessageSupport;
import org.nabucco.framework.base.facade.datatype.visitor.VisitorUtility;

/**
 * MessageTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class MessageTemplate extends ServiceMessageSupport implements ServiceMessage {

    private static final long serialVersionUID = 1L;

    private static final String[] CONSTRAINTS = {};

    @Override
    public String[] getConstraints() {
        return VisitorUtility.merge(super.getConstraints(), CONSTRAINTS);
    }

    @Override
    public Object[] getProperties() {
        return VisitorUtility.merge(super.getProperties(), new Object[] {});
    }

    @Override
    public String[] getPropertyNames() {
        return VisitorUtility.merge(super.getPropertyNames(), new String[] {});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final DatatypeTemplate other = (DatatypeTemplate) obj;

        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder appendable = new StringBuilder();
        appendable.append(super.toString());
        return appendable.toString();
    }

    @Override
    public ServiceMessage cloneObject() {
        return this;
    }

}