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

import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.service.NabuccoTransactionType;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * NabuccoToXmlServiceOperationEjbJarVisitor
 * <p/>
 * Visitor for service operation transaction attributes.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlServiceOperationEjbJarVisitor extends GJVoidDepthFirst<List<Element>> implements EjbJarConstants {

    private static final String TAG_METHOD = "method";

    private static final String TAG_METHOD_NAME = "method-name";

    private static final String TAG_EJB_NAME = "ejb-name";

    private static final String TAG_TRANS_ATTRIBUTE = "trans-attribute";

    private String ejbName;

    private XmlTemplate template;

    /**
     * Creates a new {@link NabuccoToXmlServiceOperationEjbJarVisitor} instance.
     * 
     * @param ejbName
     *            the ejb name
     * @param template
     *            the xml template
     */
    public NabuccoToXmlServiceOperationEjbJarVisitor(String ejbName, XmlTemplate template) {
        this.ejbName = ejbName;
        this.template = template;
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, List<Element> containerTransactions) {

        try {

            Element containerTransaction = this.createOperationTransactionAttribute(nabuccoMethod);
            if (containerTransaction != null) {
                containerTransactions.add(containerTransaction);
            }

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error creating transaction attribute for service operation.", te);
        }
    }

    /**
     * Create the transaction attribute of the service operation.
     * 
     * @param nabuccoService
     *            the nbc service
     * 
     * @return the container-transaction xml element
     * 
     * @throws XmlTemplateException
     *             when the XML template is not correct
     */
    private Element createOperationTransactionAttribute(MethodDeclaration nabuccoMethod) throws XmlTemplateException {

        NabuccoTransactionType transactionType = NabuccoTransactionType.valueOf(nabuccoMethod);

        String methodName = nabuccoMethod.nodeToken1.tokenImage;

        // REQUIRED is defined on service level by default!
        if (transactionType == NabuccoTransactionType.REQUIRED) {
            return null;
        }

        // Template

        Element containerTransaction = (Element) this.template.copyNodesByXPath(XPATH_CONTAINER_TRANSACTION).get(0);

        Element methodElement = this.getElementByTagName(containerTransaction, TAG_METHOD);

        Element ejbNameElement = this.getElementByTagName(methodElement, TAG_EJB_NAME);
        ejbNameElement.setTextContent(this.ejbName);

        Element methodNameElement = this.getElementByTagName(methodElement, TAG_METHOD_NAME);
        methodNameElement.setTextContent(methodName);

        Element transAttribute = this.getElementByTagName(containerTransaction, TAG_TRANS_ATTRIBUTE);
        transAttribute.setTextContent(transactionType.getValue());

        return containerTransaction;
    }

    /**
     * Get the first child elements of the given parent node by its name.
     * 
     * @param parent
     *            the parent node
     * @param name
     *            name of the child element, or null for all children
     * 
     * @return the first found child element, or null if none is found
     */
    protected Element getElementByTagName(Node parent, String name) {
        List<Element> elements = this.getElementsByTagName(parent, name);

        if (elements.isEmpty()) {
            return null;
        }

        return elements.get(0);
    }

    /**
     * Get all child elements of the given node by their name.
     * 
     * @param parent
     *            the parent node
     * @param name
     *            name of the child elements, or null for all children
     * 
     * @return the child elements
     */
    protected List<Element> getElementsByTagName(Node parent, String name) {
        List<Element> elements = new ArrayList<Element>();

        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {

            Node child = childNodes.item(i);

            if (child instanceof Element) {
                Element element = (Element) child;
                if (name == null || element.getNodeName().equals(name)) {
                    elements.add(element);
                }
            }
        }

        return elements;
    }

}
