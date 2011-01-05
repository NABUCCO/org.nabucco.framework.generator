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
package org.nabucco.framework.showcase.componentrelation;

import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.base.facade.datatype.Enumeration;
import org.nabucco.framework.base.facade.datatype.NabuccoDatatype;
import org.nabucco.framework.base.facade.datatype.componentrelation.ComponentRelation;
import org.nabucco.framework.base.facade.datatype.componentrelation.ComponentRelationType;

/**
 * ComponentRelationTypeTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ComponentRelationTypeTemplate implements ComponentRelationType {

    private final Class<? extends NabuccoDatatype> source;

    private final Class<? extends ComponentRelation<?>> relation;

    private final Class<? extends NabuccoDatatype> target;

    private ComponentRelationTypeTemplate(Class<? extends NabuccoDatatype> source,
            Class<? extends ComponentRelation<?>> relation, Class<? extends NabuccoDatatype> target) {
        this.source = source;
        this.relation = relation;
        this.target = target;
    }

    @Override
    public Class<? extends NabuccoDatatype> getSource() {
        return this.source;
    }

    @Override
    public Class<? extends NabuccoDatatype> getTarget() {
        return this.target;
    }

    @Override
    public Class<? extends ComponentRelation<?>> getRelation() {
        return this.relation;
    }

    @Override
    public Enumeration cloneObject() {
        return this;
    }

    @Override
    public String getId() {
        return super.name();
    }

    @Override
    public int getOrdinal() {
        return super.ordinal();
    }

    public static List<ComponentRelationTypeTemplate> valuesBySource(
            Class<? extends NabuccoDatatype> source) {
        List<ComponentRelationTypeTemplate> types = new ArrayList<ComponentRelationTypeTemplate>();
        for (ComponentRelationTypeTemplate type : values()) {
            if (type.source == source) {
                types.add(type);
            }
        }
        return types;
    }

    public static List<ComponentRelationTypeTemplate> valuesByTarget(
            Class<? extends NabuccoDatatype> target) {
        List<ComponentRelationTypeTemplate> types = new ArrayList<ComponentRelationTypeTemplate>();
        for (ComponentRelationTypeTemplate type : ComponentRelationTypeTemplate.values()) {
            if (type.target == target) {
                types.add(type);
            }
        }
        return types;
    }

}
