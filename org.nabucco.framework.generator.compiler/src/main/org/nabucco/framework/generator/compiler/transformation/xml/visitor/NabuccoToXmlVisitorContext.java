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

import java.util.HashMap;
import java.util.Map;

import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorContext;

import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;

/**
 * NabuccoToXmlVisitorContext
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToXmlVisitorContext extends NabuccoVisitorContext {

    private Map<String, XmlTemplate> templateMap = new HashMap<String, XmlTemplate>();

    /**
     * Creates an empty {@link NabuccoToXmlVisitorContext} instance.
     */
    public NabuccoToXmlVisitorContext() {
    }

    /**
     * Creates a visitor context with all existing templates of another context.
     * 
     * @param context
     *            another context.
     */
    public NabuccoToXmlVisitorContext(NabuccoToXmlVisitorContext context) {
        super(context);
        this.templateMap.putAll(context.templateMap);
    }

    /**
     * Inserts a template into the context.
     * 
     * @param name
     *            name of the template
     * @param template
     *            the template
     * 
     * @throws XmlTemplateException
     */
    public void putTemplate(String name, XmlTemplate template) throws XmlTemplateException {

        if (template.getModel() == null) {
            throw new XmlTemplateException("Template does not contain an XML targetModel: " + name);
        }

        this.templateMap.put(name, template);
    }

    /**
     * Get an XML template from the context.
     * 
     * @param name
     *            the template name
     * 
     * @return the template
     * 
     * @throws XmlTemplateException
     */
    public XmlTemplate getTemplate(String name) throws XmlTemplateException {
        XmlTemplate template = this.templateMap.get(name);

        if (template == null) {
            throw new XmlTemplateException("Template not found: " + name);
        }

        return template;
    }

    /**
     * Copies all templates of the current context into the other.
     * 
     * @param context
     *            the context to copy the templates into
     */
    public void copyTemplates(NabuccoToXmlVisitorContext context) {
        if (context == null) {
            throw new IllegalArgumentException(
                    "Cannot copy XML templates. VisitorContext not valid.");
        }
        context.templateMap.putAll(templateMap);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Xml Visitor Context:");
        builder.append(super.toString());
        builder.append("\n - Templates=");
        builder.append(this.templateMap.size());
        return builder.toString();
    }

}
