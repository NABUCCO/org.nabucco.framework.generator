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

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.JBossConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Node;

/**
 * NabuccoToXmlComponentApplicationVisitor
 * <p/>
 * Visitor to create the fragments of jboss.xml for components.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlComponentJBossVisitor extends NabuccoToXmlVisitorSupport implements JBossConstants {

    private static final String XPATH_POOL_CONFIG = "/enterprise-beans/pool-config";

    /**
     * Creates a new {@link NabuccoToXmlAdapterJBossVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlComponentJBossVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<XmlModel> target) {

        String interfaceName = nabuccoComponent.nodeToken2.tokenImage;

        String componentName = super.getProjectName(null, null);

        try {
            XmlDocument document = this.createJBossFragment(interfaceName, componentName);

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separatorChar);
            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            throw new NabuccoVisitorException("Error during jboss.xml modification.", me);
        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during jboss.xml template processing.", te);
        }
    }

    /**
     * Creates the JBoss fragment for a component.
     * 
     * @param interfaceName
     *            the interface name
     * @param componentName
     *            the component name
     * 
     * @return the jboss framgment XML document
     * 
     * @throws XmlTemplateException
     * @throws XmlModelException
     */
    private XmlDocument createJBossFragment(String interfaceName, String componentName) throws XmlTemplateException,
            XmlModelException {

        String interfacePackage = this.getVisitorContext().getPackage();

        XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.JBOSS_FRAGMENT_TEMPLATE);

        document.getDocument().getDocumentElement().setAttribute(NAME, interfaceName);

        String ejbName = interfacePackage + PKG_SEPARATOR + interfaceName;
        String component = NabuccoCompilerSupport.getParentComponentName(this.getVisitorContext().getPackage());

        StringBuilder jndiName = new StringBuilder();
        jndiName.append(JNDI_PREFIX);
        jndiName.append(component);
        jndiName.append(XPATH_SEPARATOR);
        jndiName.append(interfacePackage);
        jndiName.append(PKG_SEPARATOR);
        jndiName.append(interfaceName);
        jndiName.append(XPATH_SEPARATOR);

        document.getElementsByXPath(XPATH_JBOSS_EJB_NAME).get(0).setTextContent(ejbName);
        document.getElementsByXPath(XPATH_JBOSS_REMOTE_JNDI_NAME).get(0).setTextContent(jndiName.toString() + REMOTE);
        document.getElementsByXPath(XPATH_JBOSS_LOCAL_JNDI_NAME).get(0).setTextContent(jndiName.toString() + LOCAL);

        this.createPoolConfig(document);

        return document;
    }

    /**
     * Create the JBoss Pool Size Config.
     * 
     * @param document
     *            the XML document
     * 
     * @throws XmlTemplateException
     * @throws XmlModelException
     */
    private void createPoolConfig(XmlDocument document) throws XmlTemplateException, XmlModelException {
        XmlDocument template = super.extractDocument(NabuccoXmlTemplateConstants.JBOSS_TEMPLATE);
        Node poolConfig = template.getElementsByXPath(XPATH_POOL_CONFIG).get(0);

        document.getElementsByXPath(XPATH_JBOSS_SESSION).get(0)
                .appendChild(document.getDocument().importNode(poolConfig, true));
    }

}
