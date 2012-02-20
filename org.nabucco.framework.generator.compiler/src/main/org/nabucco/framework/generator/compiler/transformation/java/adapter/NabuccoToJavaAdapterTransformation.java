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
package org.nabucco.framework.generator.compiler.transformation.java.adapter;

import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
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
 * NabuccoToJavaADAPTERDatatypeVisitorTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaAdapterTransformation extends NabuccoToJavaTransformation implements
        NabuccoJavaTemplateConstants {

    /**
     * Creates a new {@link NabuccoToJavaAdapterTransformation} instance.
     * 
     * @param source
     *            the source model
     * @param target
     *            the target model
     * @param context
     *            the transformation context
     */
    public NabuccoToJavaAdapterTransformation(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoModel nabuccoModel = source.getModel();

        NabuccoToJavaVisitorContext visitorContext = super.createVisitorContext(context);
        NabuccoToJavaVisitor visitor = new NabuccoToJavaAdapterInterfaceVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
        
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaAdapterRemoteInterfaceVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
        
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaAdapterLocalInterfaceVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
        
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaAdapterLocalProxyVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaAdapterImplementationVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
        
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaAdapterJndiInterfaceVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaAdapterLocatorVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext) throws NabuccoVisitorException {

        try {
            JavaTemplateLoader loader = JavaTemplateLoader.getInstance();

            JavaTemplate template = loader.loadTemplate(ADAPTER_INTERFACE_TEMPLATE);
            visitorContext.putTemplate(ADAPTER_INTERFACE_TEMPLATE, template);
            
            template = loader.loadTemplate(ADAPTER_REMOTE_INTERFACE_TEMPLATE);
            visitorContext.putTemplate(ADAPTER_REMOTE_INTERFACE_TEMPLATE, template);
            
            template = loader.loadTemplate(ADAPTER_LOCAL_INTERFACE_TEMPLATE);
            visitorContext.putTemplate(ADAPTER_LOCAL_INTERFACE_TEMPLATE, template);
            
            template = loader.loadTemplate(ADAPTER_LOCAL_PROXY_TEMPLATE);
            visitorContext.putTemplate(ADAPTER_LOCAL_PROXY_TEMPLATE, template);

            template = loader.loadTemplate(ADAPTER_IMPLEMENTATION_TEMPLATE);
            visitorContext.putTemplate(ADAPTER_IMPLEMENTATION_TEMPLATE, template);
            
            template = loader.loadTemplate(ADAPTER_JNDI_INTERFACE_TEMPLATE);
            visitorContext.putTemplate(ADAPTER_JNDI_INTERFACE_TEMPLATE, template);

            template = loader.loadTemplate(ADAPTER_LOCATOR_TEMPLATE);
            visitorContext.putTemplate(ADAPTER_LOCATOR_TEMPLATE, template);

            template = loader.loadTemplate(ADAPTER_OPERATION_TEMPLATE);
            visitorContext.putTemplate(ADAPTER_OPERATION_TEMPLATE, template);

        } catch (MdaExeception e) {
            throw new NabuccoVisitorException("Error loading java ADAPTER templates.", e);
        }
    }
}
