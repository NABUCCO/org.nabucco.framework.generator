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
package org.nabucco.framework.generator.compiler.transformation.xml.component;

import java.io.File;

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.constants.ComponentRelationConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;

/**
 * NabuccoToXmlComponentRelationEjbJarVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlComponentRelationEjbJarVisitor extends NabuccoToXmlVisitorSupport implements EjbJarConstants,
        ComponentRelationConstants {

    /**
     * Creates a new {@link NabuccoToXmlAdapterEjbJarVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlComponentRelationEjbJarVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<XmlModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoComponent, target);

        String componentName = super.getProjectName(null, null);

        try {
            // Final document
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE);

            this.modifyFragment(document);

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            throw new NabuccoVisitorException("Error during XML DOM service modification.", me);
        } catch (XmlTemplateException te) {
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
    private void modifyFragment(XmlDocument document) throws XmlModelException, XmlTemplateException {

        document.getDocument().getDocumentElement().setAttribute(NAME, COMPONENT_RELATION_SERVICE);

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
    private Element createSessionElement(XmlDocument document) throws XmlModelException, XmlTemplateException {

        String interfacePackage = this.getVisitorContext().getPackage();
        String ejbName = interfacePackage + PKG_SEPARATOR + COMPONENT_RELATION_SERVICE;

        Element session = (Element) document.getElementsByXPath(XPATH_FRAGMENT_SESSION).get(0);

        session.getElementsByTagName(EJB_NAME).item(0).setTextContent(ejbName);
        session.getElementsByTagName(EJB_LOCAL).item(0).setTextContent(CR_INTERFACE + LOCAL);
        session.getElementsByTagName(EJB_REMOTE).item(0).setTextContent(CR_INTERFACE + REMOTE);
        session.getElementsByTagName(EJB_CLASS).item(0).setTextContent(CR_IMPLEMENTATION);

        ((Element) document.getElementsByXPath(XPATH_FRAGMENT_EJB_NAME).get(0)).setTextContent(ejbName);

        Element entityManager = this.createPersistenceRefElement();
        session.appendChild(document.getDocument().importNode(entityManager, true));

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
     * Creates a persistence-ref-element tag for the service. This represents the entity-manager
     * reference.
     * 
     * @return the persistence-ref XML element
     * 
     * @throws XmlTemplateException
     */
    private Element createPersistenceRefElement() throws XmlTemplateException {

        // Template
        XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

        Element persistenceRefElement = (Element) ejbTemplate.copyNodesByXPath(XPATH_PERSISTENCE_REF).get(0);

        // Persistence Context
        persistenceRefElement.getElementsByTagName(PERSISTENCE_REF_NAME).item(0)
                .setTextContent(PERSISTENCE + XPATH_SEPARATOR + super.getDatasourceName());

        // Persistence Unit
        persistenceRefElement.getElementsByTagName(PERSISTENCE_UNIT_NAME).item(0)
                .setTextContent(super.getDatasourceName());

        Element injectionElement = (Element) persistenceRefElement.getElementsByTagName(INJECTION_TARGET).item(0);

        injectionElement.getElementsByTagName(INJECTION_TARGET_CLASS).item(0).setTextContent(CR_IMPLEMENTATION);

        injectionElement.getElementsByTagName(INJECTION_TARGET_NAME).item(0).setTextContent(CR_ENTITY_MANAGER);

        return persistenceRefElement;
    }

}
