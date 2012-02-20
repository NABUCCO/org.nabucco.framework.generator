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

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.xml.NabuccoToXmlTransformation;
import org.nabucco.framework.generator.compiler.transformation.xml.adapter.NabuccoToXmlAdapterTransformation;
import org.nabucco.framework.generator.compiler.transformation.xml.datatype.NabuccoToXmlDatatypeFacade;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitor;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.MdaExeception;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateLoader;

/**
 * NabuccoToXmlAdapterTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToXmlComponentTransformation extends NabuccoToXmlTransformation {

    /**
     * Creates a new {@link NabuccoToXmlAdapterTransformation} instance.
     * 
     * @param source
     *            the nabucco source
     * @param target
     *            the xml target
     * @param context
     *            the transformation context
     */
    public NabuccoToXmlComponentTransformation(MdaModel<NabuccoModel> source, MdaModel<XmlModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<XmlModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoToXmlVisitorContext visitorContext;
        NabuccoToXmlVisitor visitor;

        // ejb-jar.xml (component fragment)
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToXmlComponentEjbJarVisitor(visitorContext);

        source.getModel().getUnit().accept(visitor, target);
        
        // jboss.xml (component fragment)
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToXmlComponentJBossVisitor(visitorContext);
        
        source.getModel().getUnit().accept(visitor, target);

        // ejb-jar.xml (component relation service fragment)
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToXmlComponentRelationEjbJarVisitor(visitorContext);

        source.getModel().getUnit().accept(visitor, target);

        // jboss.xml (component relation service fragment)
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToXmlComponentRelationJBossVisitor(visitorContext);

        source.getModel().getUnit().accept(visitor, target);
        
        // ejb-jar.xml (query filter service fragment)
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToXmlQueryFilterEjbJarVisitor(visitorContext);
        
        source.getModel().getUnit().accept(visitor, target);
        
        // jboss.xml (query filter service fragment)
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToXmlQueryFilterJBossVisitor(visitorContext);
        
        source.getModel().getUnit().accept(visitor, target);

        // orm.xml (datatype fragments)
        visitorContext = super.createVisitorContext(context);
        NabuccoToXmlDatatypeFacade.getInstance().createOrmFragments(source, target, visitorContext);
    }

    @Override
    protected void loadTemplates(NabuccoToXmlVisitorContext visitorContext) throws NabuccoTransformationException {

        try {
            XmlTemplateLoader loader = XmlTemplateLoader.getInstance();

            XmlTemplate template = loader.loadTemplate(NabuccoXmlTemplateConstants.APPLICATION_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.APPLICATION_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.EJB_JAR_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.EJB_JAR_FRAGMENT_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoXmlTemplateConstants.PERSISTENCE_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.PERSISTENCE_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoXmlTemplateConstants.ORM_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.ORM_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoXmlTemplateConstants.ORM_FRAGMENT_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.ORM_FRAGMENT_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoXmlTemplateConstants.JBOSS_FRAGMENT_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.JBOSS_FRAGMENT_TEMPLATE, template);
            
            template = loader.loadTemplate(NabuccoXmlTemplateConstants.JBOSS_TEMPLATE);
            visitorContext.putTemplate(NabuccoXmlTemplateConstants.JBOSS_TEMPLATE, template);

        } catch (MdaExeception e) {
            throw new NabuccoVisitorException("Error loading XML component templates.", e);
        }
    }

}
