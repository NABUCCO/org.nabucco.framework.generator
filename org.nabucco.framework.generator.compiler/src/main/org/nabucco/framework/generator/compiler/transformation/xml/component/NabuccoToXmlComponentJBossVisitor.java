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

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.template.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.JBossConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.model.xml.util.XmlModelToolkit;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;

/**
 * NabuccoToXmlComponentApplicationVisitor
 * <p/>
 * Visitor to create the jboss-app.xml and fragments of jboss.xml for components.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlComponentJBossVisitor extends NabuccoToXmlVisitorSupport implements
        JBossConstants {

    /**
     * Creates a new {@link NabuccoToXmlComponentJBossVisitor} instance.
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
            XmlDocument document = this.createJBossAppXml(componentName);

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + JBOSS + File.separatorChar);
            target.getModel().getDocuments().add(document);

            document = this.createJBossFragment(interfaceName, componentName);

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
     * Creates the jboss-app XML element.
     * 
     * @param componentName
     *            name of the component.
     * 
     * @return the jboss-app XML element
     * 
     * @throws XmlTemplateException
     * @throws XmlModelException
     */
    private XmlDocument createJBossAppXml(String componentName) throws XmlTemplateException,
            XmlModelException {

        XmlDocument document = super
                .extractDocument(NabuccoXmlTemplateConstants.JBOSS_APPLICATION_TEMPLATE);

        Element element = XmlModelToolkit.getElementsByTagName(
                document.getDocument().getDocumentElement(), LOADER_REPOSITORY).get(0);

        String loaderRepository = element.getTextContent();
        String component = NabuccoCompilerSupport.getParentComponentName(this.getVisitorContext()
                .getPackage());

        element.setTextContent(loaderRepository.replace(COMPONENT_NAME, component));

        return document;
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
    private XmlDocument createJBossFragment(String interfaceName, String componentName)
            throws XmlTemplateException, XmlModelException {

        String interfacePackage = this.getVisitorContext().getPackage();

        XmlDocument document = super
                .extractDocument(NabuccoXmlTemplateConstants.JBOSS_FRAGMENT_TEMPLATE);

        document.getDocument().getDocumentElement().setAttribute(NAME, interfaceName);

        String ejbName = interfacePackage + PKG_SEPARATOR + interfaceName;
        String component = NabuccoCompilerSupport.getParentComponentName(this.getVisitorContext()
                .getPackage());

        StringBuilder jndiName = new StringBuilder();
        jndiName.append(JNDI_ROOT);
        jndiName.append(component);
        jndiName.append(XPATH_SEPARATOR);
        jndiName.append(interfacePackage);
        jndiName.append(PKG_SEPARATOR);
        jndiName.append(interfaceName);

        document.getElementsByXPath(XPATH_JBOSS_EJB_NAME).get(0).setTextContent(ejbName);
        document.getElementsByXPath(XPATH_JBOSS_JNDI_NAME).get(0)
                .setTextContent(jndiName.toString());

        return document;
    }
    
}
