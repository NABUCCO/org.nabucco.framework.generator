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
package org.nabucco.framework.generator.compiler.transformation.xml.visitor;

import java.io.File;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;

import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;

/**
 * NabuccoToXmlVisitorSupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoToXmlVisitorSupport extends NabuccoToXmlModelVisitor {

    private static final String FOLDER_CONF = "conf";

    private static final String FOLDER_EJB = "ejb";

    /**
     * Creates a new {@link NabuccoToXmlVisitorSupport} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlVisitorSupport(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    protected String getConfFolder() {
        StringBuilder confFolder = new StringBuilder();

        confFolder.append(FOLDER_CONF);
        confFolder.append(File.separatorChar);
        confFolder.append(FOLDER_EJB);
        confFolder.append(File.separatorChar);

        return confFolder.toString();
    }

    /**
     * Extracts and copies the XML DOM from the template map the visitor context and inserts it into
     * a {@link XmlDocument}.
     * 
     * @param name
     *            the template name
     * 
     * @return the extracted XML DOM
     * 
     * @throws XmlTemplateException
     */
    protected XmlDocument extractDocument(String name) throws XmlTemplateException {

        XmlModel model = super.getVisitorContext().getTemplate(name).extractModel();

        if (model.getDocuments().size() != 1) {
            throw new IllegalArgumentException("Template must contain exactly one document.");
        }

        return model.getDocuments().get(0);
    }

    /**
     * Generates the datasource name out of import name.
     * 
     * @return the datasource name
     */
    protected String getDatasourceName() {
        
        String pkg = super.getVisitorContext().getPackage();
        String componentName = NabuccoCompilerSupport.getParentComponentName(pkg);

        return componentName.replace("org.nabucco.framework", "NABUCCO_DS").replace(".", "_")
                .toUpperCase();
    }
    
}
