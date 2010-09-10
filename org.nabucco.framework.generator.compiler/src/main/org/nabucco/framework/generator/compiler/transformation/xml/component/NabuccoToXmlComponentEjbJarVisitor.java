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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.template.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
import org.w3c.dom.Element;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;

/**
 * NabuccoToXmlComponentApplicationVisitor
 * <p/>
 * Visitor to create fragments of ejb-jar.xml for components.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlComponentEjbJarVisitor extends NabuccoToXmlVisitorSupport implements
        EjbJarConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToXmlComponentEjbJarVisitor.class);

    /** List of service references */
    private List<Element> ejbReferenceList = new ArrayList<Element>();

    /** Name of the component interface */
    private String interfaceName;

    /**
     * Creates a new {@link NabuccoToXmlComponentEjbJarVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlComponentEjbJarVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<XmlModel> target) {

        this.interfaceName = nabuccoComponent.nodeToken2.tokenImage;

        // Visit sub-nodes first!
        super.visit(nabuccoComponent, target);

        String interfacePackage = this.getVisitorContext().getPackage();
        String name = this.interfaceName + IMPLEMENTATION;
        String componentName = super.getComponentName(null, null);

        try {
            XmlDocument document = super
                    .extractDocument(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE);

            String ejbName = interfacePackage + PKG_SEPARATOR + this.interfaceName;
            document.getDocument().getDocumentElement().setAttribute(NAME, this.interfaceName);

            Element sessionElement = (Element) document.getElementsByXPath(XPATH_FRAGMENT_SESSION)
                    .get(0);

            sessionElement.getElementsByTagName(EJB_NAME).item(0).setTextContent(ejbName);
            sessionElement.getElementsByTagName(EJB_REMOTE).item(0).setTextContent(ejbName);
            sessionElement.getElementsByTagName(EJB_CLASS).item(0).setTextContent(
                    NabuccoTransformationUtility.toImpl(interfacePackage) + PKG_SEPARATOR + name);

            ((Element) document.getElementsByXPath(XPATH_FRAGMENT_EJB_NAME).get(0))
                    .setTextContent(ejbName);

            for (Element ejbReference : this.ejbReferenceList) {
                sessionElement.appendChild(document.getDocument().importNode(ejbReference, true));
            }

            document.getDocument().importNode(sessionElement, true);

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            logger.error(me, "Error during XML DOM component modification.");
            throw new NabuccoVisitorException("Error during XML DOM component modification.", me);
        } catch (XmlTemplateException te) {
            logger.error(te, "Error during XML template component processing.");
            throw new NabuccoVisitorException("Error during XML template component processing.", te);
        }
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, MdaModel<XmlModel> target) {

        // Visit service declarations to create ejb-ref tags.

        String serviceName = nabuccoService.nodeToken1.tokenImage;
        String ejbName = super.resolveImport(serviceName);

        try {
            XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(
                    NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

            Element ejbRefElement = (Element) ejbTemplate.copyNodesByXPath(XPATH_EJB_REF).get(0);

            ejbRefElement.getElementsByTagName(EJB_REF_NAME).item(0).setTextContent(ejbName);
            ejbRefElement.getElementsByTagName(EJB_REF_REMOTE).item(0).setTextContent(ejbName);

            Element injectionElement = (Element) ejbRefElement.getElementsByTagName(
                    INJECTION_TARGET).item(0);

            String pkg = NabuccoTransformationUtility.toImpl(this.getVisitorContext().getPackage());
            injectionElement.getElementsByTagName(INJECTION_TARGET_CLASS).item(0).setTextContent(
                    pkg + PKG_SEPARATOR + this.interfaceName + IMPLEMENTATION);
            injectionElement.getElementsByTagName(INJECTION_TARGET_NAME).item(0).setTextContent(
                    serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1));

            this.ejbReferenceList.add(ejbRefElement);

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error creating service reference " + serviceName, te);
        }
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, MdaModel<XmlModel> target) {

        // Visit service declarations to create ejb-ref tags.

        String componentName = nabuccoComponent.nodeToken1.tokenImage;
        String ejbName = super.resolveImport(componentName);

        try {
            XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(
                    NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

            Element ejbRefElement = (Element) ejbTemplate.copyNodesByXPath(XPATH_EJB_REF).get(0);

            ejbRefElement.getElementsByTagName(EJB_REF_NAME).item(0).setTextContent(ejbName);
            ejbRefElement.getElementsByTagName(EJB_REF_REMOTE).item(0).setTextContent(ejbName);

            Element injectionElement = (Element) ejbRefElement.getElementsByTagName(
                    INJECTION_TARGET).item(0);

            String pkg = NabuccoTransformationUtility.toImpl(this.getVisitorContext().getPackage());
            injectionElement.getElementsByTagName(INJECTION_TARGET_CLASS).item(0).setTextContent(
                    pkg + PKG_SEPARATOR + this.interfaceName + IMPLEMENTATION);
            injectionElement.getElementsByTagName(INJECTION_TARGET_NAME).item(0).setTextContent(
                    componentName.substring(0, 1).toLowerCase() + componentName.substring(1));

            this.ejbReferenceList.add(ejbRefElement);

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException(
                    "Error creating component reference " + componentName, te);
        }
    }

}
