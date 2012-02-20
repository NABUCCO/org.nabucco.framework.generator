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
package org.nabucco.framework.generator.compiler.transformation.xml.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoTransactionType;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.AdapterStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * NabuccoToXmlAdapterApplicationVisitor
 * <p/>
 * Visitor to create fragments of ejb-jar.xml for adapters.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlAdapterEjbJarVisitor extends NabuccoToXmlVisitorSupport implements EjbJarConstants {

    /** List of remote service references */
    private List<Element> ejbRemoteReferenceList = new ArrayList<Element>();

    /** List of local service references */
    private List<Element> ejbLocalReferenceList = new ArrayList<Element>();

    /** Name of the adapter interface */
    private String interfaceName;

    private static final String TAG_METHOD = "method";

    private static final String TAG_METHOD_NAME = "method-name";

    private static final String TAG_EJB_NAME = "ejb-name";

    private static final String TAG_TRANS_ATTRIBUTE = "trans-attribute";

    /**
     * Creates a new {@link NabuccoToXmlAdapterEjbJarVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlAdapterEjbJarVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(AdapterStatement nabuccoAdapter, MdaModel<XmlModel> target) {

        this.interfaceName = nabuccoAdapter.nodeToken2.tokenImage;

        // Visit sub-nodes first!
        super.visit(nabuccoAdapter, target);

        String interfacePackage = this.getVisitorContext().getPackage();
        String name = this.interfaceName + IMPLEMENTATION;
        String adapterName = super.getProjectName(null, null);

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

            Element element = (Element) document.getElementsByXPath(XPATH_FRAGMENT_TRANSACTION_ATTRIBUTE).get(0);
            element.setTextContent(NabuccoTransactionType.NOT_SUPPORTED.getValue());

            // TransactionAttribute for ping()
            this.addPingTransactionAttribute(document, ejbName);

            // PostConstruct, PreDestroy
            this.addLifecycleMethods(document, sessionElement);

            document.getDocument().importNode(sessionElement, true);

            // File creation
            document.setProjectName(adapterName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            throw new NabuccoVisitorException("Error during XML DOM adapter modification.", me);
        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during XML template adapter processing.", te);
        }
    }

    /**
     * Add the transaction attribute NOT_SUPPORTED to the adapter ping() method.
     * 
     * @param document
     *            the xml document
     * @param adapterName
     *            the adapter ejb name
     * 
     * @throws XmlTemplateException
     *             when the ejb template is not valid
     * @throws XmlModelException
     *             when the xml document cannot be modified
     */
    private void addPingTransactionAttribute(XmlDocument document, String adapterName) throws XmlTemplateException,
            XmlModelException {

        // Template
        XmlTemplate ejbTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);
        Element containerTransaction = (Element) ejbTemplate.copyNodesByXPath(XPATH_CONTAINER_TRANSACTION).get(0);

        Element methodElement = this.getElementByTagName(containerTransaction, TAG_METHOD);

        Element ejbNameElement = this.getElementByTagName(methodElement, TAG_EJB_NAME);
        ejbNameElement.setTextContent(adapterName);

        Element methodNameElement = this.getElementByTagName(methodElement, TAG_METHOD_NAME);
        methodNameElement.setTextContent("ping");

        Element transAttribute = this.getElementByTagName(containerTransaction, TAG_TRANS_ATTRIBUTE);
        transAttribute.setTextContent(NabuccoTransactionType.NOT_SUPPORTED.getValue());

        Node fragment = document.getElementsByXPath(XPATH_FRAGMENT).get(0);
        fragment.appendChild(document.getDocument().importNode(containerTransaction, true));
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

}
