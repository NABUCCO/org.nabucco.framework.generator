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
package org.nabucco.framework.generator.compiler.transformation.java.component;

import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaModelVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.MdaExeception;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplateLoader;

/**
 * NabuccoToJavaComponentDatatypeVisitorTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaComponentTransformation extends NabuccoToJavaTransformation {

    public NabuccoToJavaComponentTransformation(MdaModel<NabuccoModel> source,
            MdaModel<JavaModel> target, NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoModel nabuccoModel = source.getModel();

        NabuccoToJavaVisitorContext visitorContext = super.createVisitorContext(context);
        NabuccoToJavaModelVisitor visitor = new NabuccoToJavaComponentInterfaceVisitor(
                visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaComponentImplementationVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaComponentLocatorVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaComponentServiceDelegateFactoryRcpVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaComponentServiceDelegateFactoryWebVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext)
            throws NabuccoVisitorException {

        try {
            JavaTemplateLoader loader = JavaTemplateLoader.getInstance();

            JavaTemplate template = loader.loadTemplate(NabuccoJavaTemplateConstants.COMPONENT_INTERFACE_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.COMPONENT_INTERFACE_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.COMPONENT_IMPLEMENTATION_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.COMPONENT_IMPLEMENTATION_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.COMPONENT_OPERATION_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.COMPONENT_LOCATOR_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.COMPONENT_LOCATOR_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_COMPONENT_DELEGATE_FACTORY_PROVIDER_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_COMPONENT_DELEGATE_FACTORY_PROVIDER_TEMPLATE, template);

        } catch (MdaExeception e) {
            throw new NabuccoVisitorException("Error loading java component templates.", e);
        }
    }
}
