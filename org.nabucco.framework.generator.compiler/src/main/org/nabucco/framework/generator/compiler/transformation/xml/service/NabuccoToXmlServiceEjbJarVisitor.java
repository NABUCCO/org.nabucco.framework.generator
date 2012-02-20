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
package org.nabucco.framework.generator.compiler.transformation.xml.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoServiceType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoTransactionType;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
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
import org.w3c.dom.Node;

/**
 * NabuccoToXmlComponentRelationEjbJarVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlServiceEjbJarVisitor extends NabuccoToXmlVisitorSupport implements EjbJarConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoToXmlServiceEjbJarVisitor.class);

    private String interfaceName;

    private List<Element> persistenceList = new ArrayList<Element>();

    /**
     * Creates a new {@link NabuccoToXmlAdapterEjbJarVisitor} instance.
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

        String componentName = super.getProjectName(null, null);

        try {
            // Final document
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE);

            this.createResourceReferences(nabuccoService);
            this.modifyFragment(document);

            this.createServiceTransactionAttribute(document, nabuccoService);
            this.createOperationTransactionAttributes(document, nabuccoService);

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            throw new NabuccoVisitorException("Error during XML DOM service modification.", me);
        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during XML template service processing.", te);
        }

        super.visit(nabuccoService, target);
    }

    /**
     * Create the transaction attribute of the service.
     * 
     * @param document
     *            the xml document
     * @param nabuccoService
     *            the nbc service
     * 
     * @throws XmlModelException
     *             when the DOM cannot be modified
     */
    private void createServiceTransactionAttribute(XmlDocument document, ServiceStatement nabuccoService)
            throws XmlModelException {

        NabuccoServiceType serviceType = NabuccoServiceType.valueOf(nabuccoService);

        switch (serviceType) {

        case RESOURCE: {
            Element element = (Element) document.getElementsByXPath(XPATH_FRAGMENT_TRANSACTION_ATTRIBUTE).get(0);
            element.setTextContent(NabuccoTransactionType.NOT_SUPPORTED.getValue());
            break;
        }
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
    private void modifyFragment(XmlDocument document) throws XmlModelException, XmlTemplateException {

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
        String implName = NabuccoTransformationUtility.toImpl(interfacePackage) + PKG_SEPARATOR + name;

        Element session = (Element) document.getElementsByXPath(XPATH_FRAGMENT_SESSION).get(0);

        session.getElementsByTagName(EJB_NAME).item(0).setTextContent(ejbName);
        session.getElementsByTagName(EJB_LOCAL).item(0).setTextContent(ejbName + LOCAL);
        session.getElementsByTagName(EJB_REMOTE).item(0).setTextContent(ejbName + REMOTE);
        session.getElementsByTagName(EJB_CLASS).item(0).setTextContent(implName);

        ((Element) document.getElementsByXPath(XPATH_FRAGMENT_EJB_NAME).get(0)).setTextContent(ejbName);

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
    private void addLifecycleMethods(XmlDocument document, Element sessionElement) throws XmlTemplateException {

        // Template
        XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

        Element postConstruct = (Element) ejbTemplate.copyNodesByXPath(XPATH_POST_CONSTRUCT).get(0);
        Element preDestroy = (Element) ejbTemplate.copyNodesByXPath(XPATH_PRE_DESTROY).get(0);

        sessionElement.appendChild(document.getDocument().importNode(postConstruct, true));
        sessionElement.appendChild(document.getDocument().importNode(preDestroy, true));
    }

    /**
     * Creates the reference to the entity manager.
     * 
     * @param nabuccoService
     *            the nabucco service to evalue
     * 
     * @throws XmlTemplateException
     *             when the DOM cannot be manipulated
     */
    private void createResourceReferences(ServiceStatement nabuccoService) throws XmlTemplateException {
        NabuccoServiceType serviceType = NabuccoServiceType.valueOf(nabuccoService);

        switch (serviceType) {

        case PERSISTENCE: {
            String name = NabuccoTransformationUtility.firstToLower(ENTITY_MANAGER);
            Element persistenceRef = this.createPersistenceRefElement(name);
            this.persistenceList.add(persistenceRef);

            break;
        }

        case RESOURCE: {
            Element resourceRef = this.createResourceRefElement(SESSION_CONTEXT, IMPORT_SESSION_CONTEXT,
                    JNDI_SESSION_CONTEXT);
            this.persistenceList.add(resourceRef);

            break;
        }

        case BUSINESS: {
            // Nothing to inject.
            break;
        }

        default:
            logger.warning("Service Type " + serviceType + " is not supported.");
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
        XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

        Element persistenceRef = (Element) ejbTemplate.copyNodesByXPath(XPATH_PERSISTENCE_REF).get(0);

        // Persistence Context
        persistenceRef.getElementsByTagName(PERSISTENCE_REF_NAME).item(0)
                .setTextContent(PERSISTENCE + XPATH_SEPARATOR + super.getDatasourceName());

        // Persistence Unit
        persistenceRef.getElementsByTagName(PERSISTENCE_UNIT_NAME).item(0).setTextContent(super.getDatasourceName());

        Element injectionElement = (Element) persistenceRef.getElementsByTagName(INJECTION_TARGET).item(0);

        String pkg = NabuccoTransformationUtility.toImpl(this.getVisitorContext().getPackage());

        injectionElement.getElementsByTagName(INJECTION_TARGET_CLASS).item(0)
                .setTextContent(pkg + PKG_SEPARATOR + this.interfaceName + IMPLEMENTATION);

        injectionElement.getElementsByTagName(INJECTION_TARGET_NAME).item(0).setTextContent(name);
        return persistenceRef;
    }

    /**
     * Creates a resource-ref-element tag for the service.
     * 
     * @param name
     *            name of the resource reference
     * 
     * @return the resource-ref XML element
     * 
     * @throws XmlTemplateException
     */
    private Element createResourceRefElement(String name, String type, String jndi) throws XmlTemplateException {

        // Template
        XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

        Element resourceRef = (Element) ejbTemplate.copyNodesByXPath(XPATH_RESOURCE_REF).get(0);

        // <res-ref-name>
        Element element = super.getElementByTagName(resourceRef, RESOURCE_REF_NAME);
        element.setTextContent(name);

        // <res-type>
        element = super.getElementByTagName(resourceRef, RESOURCE_TYPE);
        element.setTextContent(type);

        // <mapped-name>
        element = super.getElementByTagName(resourceRef, RESOURCE_MAPPED_NAME);
        element.setTextContent(jndi);

        Element injectionElement = super.getElementByTagName(resourceRef, INJECTION_TARGET);

        String pkg = NabuccoTransformationUtility.toImpl(this.getVisitorContext().getPackage());

        // <injection-target-class>
        element = super.getElementByTagName(injectionElement, INJECTION_TARGET_CLASS);
        element.setTextContent(pkg + PKG_SEPARATOR + this.interfaceName + IMPLEMENTATION);

        // <injection-target-name>
        element = super.getElementByTagName(injectionElement, INJECTION_TARGET_NAME);
        element.setTextContent(NabuccoTransformationUtility.firstToLower(name));

        return resourceRef;
    }

    /**
     * Create the transaction attribute of the service operation.
     * 
     * @param document
     *            the xml document
     * @param nabuccoService
     *            the nbc service
     * 
     * @throws XmlModelException
     *             when the DOM cannot be modified
     * @throws XmlTemplateException
     *             when the XML template is not correct
     */
    private void createOperationTransactionAttributes(XmlDocument document, ServiceStatement nabuccoService)
            throws XmlModelException, XmlTemplateException {

        String interfacePackage = this.getVisitorContext().getPackage();
        String ejbName = interfacePackage + PKG_SEPARATOR + this.interfaceName;

        // Template
        XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

        NabuccoToXmlServiceOperationEjbJarVisitor operationVisitor = new NabuccoToXmlServiceOperationEjbJarVisitor(
                ejbName, ejbTemplate);

        List<Element> containerTransactions = new ArrayList<Element>();
        nabuccoService.accept(operationVisitor, containerTransactions);

        Node fragment = document.getElementsByXPath(XPATH_FRAGMENT).get(0);
        for (Element containerTransaction : containerTransactions) {
            fragment.appendChild(document.getDocument().importNode(containerTransaction, true));
        }
    }
}
