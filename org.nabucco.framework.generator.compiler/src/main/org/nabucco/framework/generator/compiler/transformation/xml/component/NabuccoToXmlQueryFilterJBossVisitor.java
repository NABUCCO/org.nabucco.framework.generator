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
import org.nabucco.framework.generator.compiler.transformation.common.constants.QueryFilterConstants;
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

/**
 * NabuccoToXmlQueryFilterJBossVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlQueryFilterJBossVisitor extends NabuccoToXmlVisitorSupport implements JBossConstants,
        QueryFilterConstants {

    /**
     * Creates a new {@link NabuccoToXmlAdapterEjbJarVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlQueryFilterJBossVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentStatement nabuccoComponent, MdaModel<XmlModel> target) {

        String interfacePackage = this.getVisitorContext().getPackage();
        String componentName = super.getProjectName(null, null);

        try {
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.JBOSS_FRAGMENT_TEMPLATE);

            String ejbName = interfacePackage + PKG_SEPARATOR + QUERY_FILTER_SERVICE;
            document.getDocument().getDocumentElement().setAttribute(NAME, QUERY_FILTER_SERVICE);

            String component = NabuccoCompilerSupport.getParentComponentName(this.getVisitorContext().getPackage());

            StringBuilder jndiName = new StringBuilder();
            jndiName.append(JNDI_PREFIX);
            jndiName.append(component);
            jndiName.append(XPATH_SEPARATOR);
            jndiName.append(interfacePackage);
            jndiName.append(PKG_SEPARATOR);
            jndiName.append(QUERY_FILTER_SERVICE);
            jndiName.append(XPATH_SEPARATOR);

            document.getElementsByXPath(XPATH_JBOSS_EJB_NAME).get(0).setTextContent(ejbName);
            document.getElementsByXPath(XPATH_JBOSS_REMOTE_JNDI_NAME).get(0)
                    .setTextContent(jndiName.toString() + REMOTE);
            document.getElementsByXPath(XPATH_JBOSS_LOCAL_JNDI_NAME).get(0).setTextContent(jndiName.toString() + LOCAL);

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

}
