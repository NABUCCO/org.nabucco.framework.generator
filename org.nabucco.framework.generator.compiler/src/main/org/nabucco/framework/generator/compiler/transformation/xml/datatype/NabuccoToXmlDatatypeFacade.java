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
package org.nabucco.framework.generator.compiler.transformation.xml.datatype;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;

/**
 * NabuccoToXmlDatatypeFacade
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToXmlDatatypeFacade {

    /**
     * Singleton instance.
     */
    private static NabuccoToXmlDatatypeFacade instance = new NabuccoToXmlDatatypeFacade();

    /**
     * Private constructor.
     */
    private NabuccoToXmlDatatypeFacade() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoToXmlDatatypeFacade instance.
     */
    public static NabuccoToXmlDatatypeFacade getInstance() {
        return instance;
    }

    /**
     * Creates the orm fragments file by visiting all datatype declarations of a NABUCCO unit and
     * the related statements recursively.
     * 
     * @param source
     *            the NABUCCO model
     * @param target
     *            the XML model
     * @param visitorContext
     *            the visitor context
     * 
     * @throws NabuccoTransformationException
     */
    public void createOrmFragments(MdaModel<NabuccoModel> source, MdaModel<XmlModel> target,
            NabuccoToXmlVisitorContext visitorContext) throws NabuccoTransformationException {

        NabuccoToXmlDatatypeCollector collector = new NabuccoToXmlDatatypeCollector();
        NabuccoToXmlDatatypeVisitor visitor = new NabuccoToXmlDatatypeVisitor(visitorContext, collector, null);

        source.getModel().getUnit().accept(visitor, target);
        target.getModel().getDocuments().addAll(collector.getDocuments());
    }

}
