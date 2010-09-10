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
package org.nabucco.framework.generator.compiler.transformation.xml.component;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.template.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.w3c.dom.Element;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.model.xml.util.XmlModelToolkit;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;

/**
 * NabuccoToXmlComponentApplicationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlComponentPersistenceVisitor extends NabuccoToXmlVisitorSupport implements
        PersistenceConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToXmlComponentPersistenceVisitor.class);

    /**
     * Creates a new {@link NabuccoToXmlComponentPersistenceVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlComponentPersistenceVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<XmlModel> target) {

        String componentName = super.getComponentName(null, null);

        try {
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.PERSISTENCE_TEMPLATE);

            Element element = XmlModelToolkit.getElementsByTagName(
                    document.getDocument().getDocumentElement(), PERSISTENCE_UNIT).get(0);

            element.setAttribute(NAME, super.getDatasourceName());

            Element mappedFile = XmlModelToolkit.getElementsByTagName(element, "mapping-file").get(
                    0);
            String textContent = mappedFile.getTextContent();

            String fileName = NabuccoCompilerSupport.getParentComponentName(super
                    .getVisitorContext().getPackage()) + "_orm";

            mappedFile.setTextContent(textContent.replace("orm", fileName));

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder());

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            logger.error(me, "Error during XML document component modification.");
            throw new NabuccoVisitorException("Error during XML document component modification.",
                    me);
        } catch (XmlTemplateException te) {
            logger.error(te, "Error during XML template component processing.");
            throw new NabuccoVisitorException("Error during XML template component processing.", te);
        }

    }
}
