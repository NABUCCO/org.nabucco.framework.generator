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
package org.nabucco.framework.generator.compiler.transformation.java.service;

import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.common.service.NabuccoAbstractServiceVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.service.delegate.NabuccoToJavaServiceDelegateRcpVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.service.delegate.NabuccoToJavaServiceDelegateWebVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.MdaExeception;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplateLoader;

/**
 * NabuccoToJavaServiceTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaServiceTransformation extends NabuccoToJavaTransformation {

    public NabuccoToJavaServiceTransformation(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoModel nabuccoModel = source.getModel();

        NabuccoToJavaVisitorContext visitorContext = super.createVisitorContext(context);
        NabuccoToJavaVisitor visitor = new NabuccoToJavaServiceInterfaceVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);

        NabuccoAbstractServiceVisitor isAbstractVisitor = new NabuccoAbstractServiceVisitor();
        nabuccoModel.getUnit().accept(isAbstractVisitor, null);

        if (isAbstractVisitor.isAbstract()) {
            
            visitorContext = super.createVisitorContext(context);
            visitor = new NabuccoToJavaServiceLocatorVisitor(visitorContext);
            nabuccoModel.getUnit().accept(visitor, target);
            
            visitorContext = super.createVisitorContext(context);
            visitor = new NabuccoToJavaServiceDefaultVisitor(visitorContext);
            nabuccoModel.getUnit().accept(visitor, target);
            
        } else {
            
            visitorContext = super.createVisitorContext(context);
            visitor = new NabuccoToJavaServiceLocalInterfaceVisitor(visitorContext);
            nabuccoModel.getUnit().accept(visitor, target);

            visitorContext = super.createVisitorContext(context);
            visitor = new NabuccoToJavaServiceRemoteInterfaceVisitor(visitorContext);
            nabuccoModel.getUnit().accept(visitor, target);

            visitorContext = super.createVisitorContext(context);
            visitor = new NabuccoToJavaServiceImplementationVisitor(visitorContext);
            nabuccoModel.getUnit().accept(visitor, target);

            visitorContext = super.createVisitorContext(context);
            visitor = new NabuccoToJavaServiceDelegateRcpVisitor(visitorContext);
            nabuccoModel.getUnit().accept(visitor, target);

            visitorContext = super.createVisitorContext(context);
            visitor = new NabuccoToJavaServiceDelegateWebVisitor(visitorContext);
            nabuccoModel.getUnit().accept(visitor, target);
        }
    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext) throws NabuccoTransformationException {

        try {
            JavaTemplateLoader loader = JavaTemplateLoader.getInstance();

            JavaTemplate template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_INTERFACE_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_INTERFACE_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_LOCAL_INTERFACE_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_LOCAL_INTERFACE_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_REMOTE_INTERFACE_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_REMOTE_INTERFACE_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_IMPLEMENTATION_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_IMPLEMENTATION_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_OPERATION_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_HANDLER_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_HANDLER_TEMPLATE, template);

            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_DELEGATE_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_DELEGATE_TEMPLATE, template);
            
            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_LOCATOR_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_LOCATOR_TEMPLATE, template);
            
            template = loader.loadTemplate(NabuccoJavaTemplateConstants.SERVICE_DEFAULT_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.SERVICE_DEFAULT_TEMPLATE, template);

        } catch (MdaExeception e) {
            throw new NabuccoVisitorException("Error loading java service templates.", e);
        }
    }

}
