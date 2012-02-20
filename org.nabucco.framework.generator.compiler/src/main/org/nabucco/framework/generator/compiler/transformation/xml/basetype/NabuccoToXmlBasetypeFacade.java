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
package org.nabucco.framework.generator.compiler.transformation.xml.basetype;

import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;

/**
 * NabuccoToXmlBasetypeFacade
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToXmlBasetypeFacade {

    /**
     * Singleton instance.
     */
    private static NabuccoToXmlBasetypeFacade instance = new NabuccoToXmlBasetypeFacade();

    /**
     * Private constructor.
     */
    private NabuccoToXmlBasetypeFacade() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoToXmlBasetypeFacade instance.
     */
    public static NabuccoToXmlBasetypeFacade getInstance() {
        return instance;
    }

    /**
     * Creates the basetype embeddable fragments for orm.xml file by visiting all basetype
     * declarations of a NABUCCO unit.
     * 
     * @param source
     *            the NABUCCO model
     * @param target
     *            the XML model
     * @param visitorContext
     *            the visitor context
     * @param componentName
     *            the component name
     */
    public void createOrmBasetypeFragments(MdaModel<NabuccoModel> source, MdaModel<XmlModel> target,
            NabuccoToXmlVisitorContext visitorContext, String componentName) {

        NabuccoToXmlBasetypeOrmVisitor visitor = new NabuccoToXmlBasetypeOrmVisitor(new NabuccoToXmlVisitorContext(
                visitorContext), componentName);
        source.getModel().getUnit().accept(visitor, target);
    }

}
