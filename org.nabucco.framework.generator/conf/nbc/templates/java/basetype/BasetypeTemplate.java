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

import java.util.List;
import org.nabucco.framework.base.facade.datatype.Basetype;
import org.nabucco.framework.base.facade.datatype.property.SimpleProperty;
import org.nabucco.framework.base.facade.datatype.property.NabuccoProperty;

/**
 * BasetypeTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class BasetypeTemplate implements Basetype {

    private static final long serialVersionUID = 1L;

    private static final String PROPERTY_NAME = "value";

    private static final String PROPERTY_CONSTRAINTS = null;

    /**
     * Creates a new {@link BasetypeTemplate} instance.
     */
    public BasetypeTemplate() {
        super();
    }

    public BasetypeTemplate(Basetype value) {
        super(value);
    }

    @Override
    public List<NabuccoProperty<?>> getProperties() {
        List<NabuccoProperty<?>> properties = super.getProperties();
        properties.add(new SimpleProperty<String>(PROPERTY_NAME, String.class,
                PROPERTY_CONSTRAINTS, super.getValue()));
        return properties;
    }

    @Override
    public BasetypeTemplate cloneObject() {
        BasetypeTemplate clone = new BasetypeTemplate();
        super.cloneObject(clone);
        return clone;
    }

}
