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
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.constants.ComponentRelationConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
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
 * NabuccoToXmlComponentApplicationVisitor
 * <p/>
 * Visitor to create fragments of ejb-jar.xml for components.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlComponentEjbJarVisitor extends NabuccoToXmlVisitorSupport implements EjbJarConstants,
        ComponentRelationConstants {

    /** List of remote service references */
    private List<Element> ejbRemoteReferenceList = new ArrayList<Element>();

    /** List of local service references */
    private List<Element> ejbLocalReferenceList = new ArrayList<Element>();

    /** Name of the component interface */
    private String interfaceName;

    /**
     * Creates a new {@link NabuccoToXmlAdapterEjbJarVisitor} instance.
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
        String componentName = super.getProjectName(null, null);

        // Component Relation Service
        // String componentRelation = interfacePackage + PKG_SEPARATOR + COMPONENT_RELATION_SERVICE;
        // this.createEjbReference(COMPONENT_RELATION_SERVICE, componentRelation, CR_INTERFACE);

        try {
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE);

            String ejbName = interfacePackage + PKG_SEPARATOR + this.interfaceName;
            document.getDocument().getDocumentElement().setAttribute(NAME, this.interfaceName);

            Element sessionElement = (Element) document.getElementsByXPath(XPATH_FRAGMENT_SESSION).get(0);

            String implName = NabuccoTransformationUtility.toImpl(interfacePackage) + PKG_SEPARATOR + name;

            sessionElement.getElementsByTagName(EJB_NAME).item(0).setTextContent(ejbName);
            sessionElement.getElementsByTagName(EJB_REMOTE).item(0).setTextContent(ejbName + REMOTE);
            sessionElement.getElementsByTagName(EJB_LOCAL).item(0).setTextContent(ejbName + LOCAL);
            sessionElement.getElementsByTagName(EJB_CLASS).item(0).setTextContent(implName);

            ((Element) document.getElementsByXPath(XPATH_FRAGMENT_EJB_NAME).get(0)).setTextContent(ejbName);

            for (Element ejbReference : this.ejbRemoteReferenceList) {
                sessionElement.appendChild(document.getDocument().importNode(ejbReference, true));
            }

            for (Element ejbReference : this.ejbLocalReferenceList) {
                sessionElement.appendChild(document.getDocument().importNode(ejbReference, true));
            }

            // PostConstruct, PreDestroy
            this.addLifecycleMethods(document, sessionElement);

            document.getDocument().importNode(sessionElement, true);

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            throw new NabuccoVisitorException("Error during XML DOM component modification.", me);
        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during XML template component processing.", te);
        }
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
     * Create the EJB injection reference.
     * 
     * @param fieldName
     *            name of the reference
     * @param name
     *            jndi name of the EJB reference
     * @param name
     *            fully qualified name of the remote interface
     */
    @SuppressWarnings("unused")
    private void createEjbReference(String fieldName, String ejbName, String interfaceName) {

        try {
            XmlTemplate ejbTemplate = this.getVisitorContext()
                    .getTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

            Element ejbLocalRefElement = (Element) ejbTemplate.copyNodesByXPath(XPATH_EJB_REF).get(0);

            ejbLocalRefElement.getElementsByTagName(EJB_REF_NAME).item(0).setTextContent(ejbName);
            ejbLocalRefElement.getElementsByTagName(EJB_REF_REMOTE).item(0).setTextContent(interfaceName + REMOTE);

            Element injectionElement = (Element) ejbLocalRefElement.getElementsByTagName(INJECTION_TARGET).item(0);

            String pkg = NabuccoTransformationUtility.toImpl(this.getVisitorContext().getPackage());

            StringBuilder injectionTarget = new StringBuilder();
            injectionTarget.append(pkg);
            injectionTarget.append(PKG_SEPARATOR);
            injectionTarget.append(this.interfaceName);
            injectionTarget.append(IMPLEMENTATION);

            injectionElement.getElementsByTagName(INJECTION_TARGET_CLASS).item(0)
                    .setTextContent(injectionTarget.toString());

            injectionElement.getElementsByTagName(INJECTION_TARGET_NAME).item(0)
                    .setTextContent(NabuccoTransformationUtility.firstToLower(fieldName));

            this.ejbRemoteReferenceList.add(ejbLocalRefElement);

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error creating service reference " + fieldName, te);
        }
    }

    /**
     * Create the EJB injection reference.
     * 
     * @param fieldName
     *            name of the reference
     * @param name
     *            jndi name of the EJB reference
     * @param name
     *            fully qualified name of the remote interface
     */
    @SuppressWarnings("unused")
    private void createEjbLocalReference(String fieldName, String ejbName, String interfaceName) {

        try {
            XmlTemplate ejbTemplate = this.getVisitorContext()
                    .getTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);

            Element ejbRefElement = (Element) ejbTemplate.copyNodesByXPath(XPATH_EJB_LOCAL_REF).get(0);

            ejbRefElement.getElementsByTagName(EJB_REF_NAME).item(0).setTextContent(ejbName);
            ejbRefElement.getElementsByTagName(EJB_REF_LOCAL).item(0).setTextContent(interfaceName + LOCAL);

            Element injectionElement = (Element) ejbRefElement.getElementsByTagName(INJECTION_TARGET).item(0);

            String pkg = NabuccoTransformationUtility.toImpl(this.getVisitorContext().getPackage());

            StringBuilder injectionTarget = new StringBuilder();
            injectionTarget.append(pkg);
            injectionTarget.append(PKG_SEPARATOR);
            injectionTarget.append(this.interfaceName);
            injectionTarget.append(IMPLEMENTATION);

            injectionElement.getElementsByTagName(INJECTION_TARGET_CLASS).item(0)
                    .setTextContent(injectionTarget.toString());

            injectionElement.getElementsByTagName(INJECTION_TARGET_NAME).item(0)
                    .setTextContent(NabuccoTransformationUtility.firstToLower(fieldName));

            this.ejbLocalReferenceList.add(ejbRefElement);

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error creating service reference " + fieldName, te);
        }
    }

}
