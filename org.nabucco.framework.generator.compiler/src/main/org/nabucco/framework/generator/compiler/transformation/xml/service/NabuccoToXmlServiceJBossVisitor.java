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

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.JBossConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;

/**
 * NabuccoToXmlComponentApplicationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlServiceJBossVisitor extends NabuccoToXmlVisitorSupport implements JBossConstants {

    /**
     * Creates a new {@link NabuccoToXmlAdapterEjbJarVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlServiceJBossVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ServiceStatement nabuccoService, MdaModel<XmlModel> target) {

        // Visit Sub-Nodes first!
        super.visit(nabuccoService, target);

        String serviceName = nabuccoService.nodeToken2.tokenImage;
        String componentPackage = this.getVisitorContext().getPackage();
        String componentName = NabuccoCompilerSupport.getParentComponentName(componentPackage);
        
        String projectName = super.getProjectName(null, null);

        try {
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.JBOSS_FRAGMENT_TEMPLATE);

            String ejbName = componentPackage + PKG_SEPARATOR + serviceName;
            document.getDocument().getDocumentElement().setAttribute(NAME, serviceName);


            NabuccoModel parent = super.getParent();
            if (parent != null) {
                serviceName = parent.getName();
                componentPackage = parent.getPackage();
                componentName = NabuccoCompilerSupport.getParentComponentName(componentPackage);
            }
            
            StringBuilder jndiName = new StringBuilder();
            jndiName.append(JNDI_PREFIX);
            jndiName.append(componentName);
            jndiName.append(XPATH_SEPARATOR);
            jndiName.append(componentPackage);
            jndiName.append(PKG_SEPARATOR);
            jndiName.append(serviceName);
            jndiName.append(XPATH_SEPARATOR);

            document.getElementsByXPath(XPATH_JBOSS_EJB_NAME).get(0).setTextContent(ejbName);
            document.getElementsByXPath(XPATH_JBOSS_REMOTE_JNDI_NAME).get(0)
                    .setTextContent(jndiName.toString() + REMOTE);
            document.getElementsByXPath(XPATH_JBOSS_LOCAL_JNDI_NAME).get(0).setTextContent(jndiName.toString() + LOCAL);

            // File creation
            document.setProjectName(projectName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            throw new NabuccoVisitorException("Error during XML DOM service modification.", me);
        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during XML template service processing.", te);
        }
    }
}
