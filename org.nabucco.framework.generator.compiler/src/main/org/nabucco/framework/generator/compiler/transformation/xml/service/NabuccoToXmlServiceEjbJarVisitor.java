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
package org.nabucco.framework.generator.compiler.transformation.xml.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.template.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.CustomDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;

/**
 * NabuccoToXmlServiceEjbJarVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlServiceEjbJarVisitor extends NabuccoToXmlVisitorSupport implements
        EjbJarConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToXmlServiceEjbJarVisitor.class);

    private String interfaceName;

    private List<Element> persistenceList = new ArrayList<Element>();

    /**
     * Creates a new {@link NabuccoToXmlComponentEjbJarVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlServiceEjbJarVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<XmlModel> target) {

        this.interfaceName = nabuccoService.nodeToken2.tokenImage;

        // Visit sub-nodes first!
        super.visit(nabuccoService, target);

        String componentName = super.getComponentName(null, null);

        try {
            // Final document
            XmlDocument document = super
                    .extractDocument(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE);

            this.modifyFragment(document);

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            logger.error(me, "Error during XML DOM service modification.");
            throw new NabuccoVisitorException("Error during XML DOM service modification.", me);
        } catch (XmlTemplateException te) {
            logger.error(te, "Error during XML template service processing.");
            throw new NabuccoVisitorException("Error during XML template service processing.", te);
        }
    }

    /**
     * Modyfies the EJB-JAR document's name and attributes.
     * 
     * @param document
     *            the EJB-JAR XML document
     * 
     * @throws XmlModelException
     * @throws XmlTemplateException
     */
    private void modifyFragment(XmlDocument document) throws XmlModelException,
            XmlTemplateException {

        document.getDocument().getDocumentElement().setAttribute(NAME, this.interfaceName);

        Element session = this.createSessionElement(document);

        // PostConstruct, PreDestroy
        this.addLifecycleMethods(document, session);
    }

    /**
     * Creates the session tag for this fragment
     * 
     * @param document
     *            the xml document to extract the session
     * 
     * @return the session element
     * 
     * @throws XmlModelException
     */
    private Element createSessionElement(XmlDocument document) throws XmlModelException {

        String name = this.interfaceName + IMPLEMENTATION;
        String interfacePackage = this.getVisitorContext().getPackage();
        String ejbName = interfacePackage + PKG_SEPARATOR + this.interfaceName;

        Element session = (Element) document.getElementsByXPath(XPATH_FRAGMENT_SESSION).get(0);

        session.getElementsByTagName(EJB_NAME).item(0).setTextContent(ejbName);
        session.getElementsByTagName(EJB_REMOTE).item(0).setTextContent(ejbName);
        session.getElementsByTagName(EJB_CLASS).item(0).setTextContent(
                NabuccoTransformationUtility.toImpl(interfacePackage) + PKG_SEPARATOR + name);

        ((Element) document.getElementsByXPath(XPATH_FRAGMENT_EJB_NAME).get(0))
                .setTextContent(ejbName);

        for (Element ejbReference : this.persistenceList) {
            session.appendChild(document.getDocument().importNode(ejbReference, true));
        }

        // Add session to the document
        document.getDocument().importNode(session, true);

        return session;
    }

    /**
     * Adds postConstruct and preDestroy deployment descriptor elements to the session.
     * 
     * @param document
     *            the xml document
     * @param sessionElement
     *            the session element
     * 
     * @throws XmlTemplateException
     */
    private void addLifecycleMethods(XmlDocument document, Element sessionElement)
            throws XmlTemplateException {

        // Template
        XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(
                NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

        Element postConstruct = (Element) ejbTemplate.copyNodesByXPath(XPATH_POST_CONSTRUCT).get(0);
        Element preDestroy = (Element) ejbTemplate.copyNodesByXPath(XPATH_PRE_DESTROY).get(0);

        sessionElement.appendChild(document.getDocument().importNode(postConstruct, true));
        sessionElement.appendChild(document.getDocument().importNode(preDestroy, true));
    }

    @Override
    public void visit(CustomDeclaration customDeclaration, MdaModel<XmlModel> target) {

        String name = customDeclaration.nodeToken2.tokenImage;
        String type = customDeclaration.nodeToken.tokenImage;

        try {

            // @Inject annotation must be present.
            NabuccoAnnotation inject = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                    customDeclaration.annotationDeclaration, NabuccoAnnotationType.INJECT);

            if (type.equals(ENTITY_MANAGER) && inject != null) {
                Element persistenceRef = this.createPersistenceRefElement(name);
                this.persistenceList.add(persistenceRef);
            } else {
                logger.warning("Entity Manager cannot be injected.");
            }

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during XML template service processing.", te);
        }
    }

    /**
     * Creates a persistence-ref-element tag for the service. This represents the entity-manager
     * reference.
     * 
     * @param name
     *            name of the entity manager reference
     * 
     * @return the persistence-ref XML element
     * 
     * @throws XmlTemplateException
     */
    private Element createPersistenceRefElement(String name) throws XmlTemplateException {

        // Template
        XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(
                NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

        Element persistenceRefElement = (Element) ejbTemplate.copyNodesByXPath(
                XPATH_PERSISTENCE_CONTEXT).get(0);

        // Persistence Context
        persistenceRefElement.getElementsByTagName(PERSISTENCE_CONTEXT_NAME).item(0)
                .setTextContent(PERSISTENCE + XPATH_SEPARATOR + super.getDatasourceName());

        // Persistence Unit
        persistenceRefElement.getElementsByTagName(PERSISTENCE_UNIT_NAME).item(0).setTextContent(
                super.getDatasourceName());

        Element injectionElement = (Element) persistenceRefElement.getElementsByTagName(
                INJECTION_TARGET).item(0);

        String pkg = NabuccoTransformationUtility.toImpl(this.getVisitorContext().getPackage());

        injectionElement.getElementsByTagName(INJECTION_TARGET_CLASS).item(0).setTextContent(
                pkg + PKG_SEPARATOR + this.interfaceName + IMPLEMENTATION);

        injectionElement.getElementsByTagName(INJECTION_TARGET_NAME).item(0).setTextContent(name);
        return persistenceRefElement;
    }

}
