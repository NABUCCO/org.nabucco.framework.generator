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
package org.nabucco.framework.generator.compiler.transformation.xml.service;

import org.nabucco.framework.generator.compiler.template.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.xml.NabuccoToXmlTransformation;
import org.nabucco.framework.generator.compiler.transformation.xml.component.NabuccoToXmlComponentTransformation;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlModelVisitor;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.MdaExeception;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateLoader;

/**
 * NabuccoToXmlServiceTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToXmlServiceTransformation extends NabuccoToXmlTransformation {

    /**
     * Creates a new {@link NabuccoToXmlComponentTransformation} instance.
     * 
     * @param source
     *            the nabucco source
     * @param target
     *            the xml target
     * @param context
     *            the transformation context
     */
    public NabuccoToXmlServiceTransformation(MdaModel<NabuccoModel> source,
            MdaModel<XmlModel> target, NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<XmlModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoToXmlVisitorContext visitorContext = super.createVisitorContext(context);
        NabuccoToXmlModelVisitor visitor = new NabuccoToXmlServiceEjbJarVisitor(visitorContext);

        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToXmlServiceJBossVisitor(visitorContext);

        source.getModel().getUnit().accept(visitor, target);
    }

    @Override
    protected void loadTemplates(NabuccoToXmlVisitorContext visitorContext)
            throws NabuccoTransformationException {

        try {
            XmlTemplateLoader loader = XmlTemplateLoader.getInstance();

            XmlTemplate template = loader.loadTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoXmlTemplateConstants.JBOSS_FRAGMENT_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.JBOSS_FRAGMENT_TEMPLATE, template);

        } catch (MdaExeception e) {
            throw new NabuccoVisitorException("Error loading XML service templates.", e);
        }

    }

}
