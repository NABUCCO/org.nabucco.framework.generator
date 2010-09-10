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
package org.nabucco.framework.generator.compiler.transformation.java.view.command;

import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaModelVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplateException;
import org.nabucco.framework.mda.template.java.JavaTemplateLoader;

/**
 * NabuccoToJavaRcpViewCommandTransformation
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class NabuccoToJavaRcpViewCommandTransformation extends NabuccoToJavaTransformation {

    /**
     * @param source
     * @param target
     * @param context
     * @throws NabuccoTransformationException
     */
    public NabuccoToJavaRcpViewCommandTransformation(MdaModel<NabuccoModel> source,
            MdaModel<JavaModel> target, NabuccoTransformationContext context) {
        super(source, target, context);

        NabuccoToJavaVisitorContext visitorContext;
        try {
            visitorContext = super.createVisitorContext(context);
            NabuccoToJavaModelVisitor visitor = new NabuccoToJavaRcpViewCommandVisitor(
                    visitorContext);
            source.getModel().getUnit().accept(visitor, target);
            visitor = new NabuccoToJavaRcpViewCommandHandlerVisitor(visitorContext);
            source.getModel().getUnit().accept(visitor, target);
        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException("Exception during creation of VisitorContext", e);
        }

    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext)
            throws NabuccoTransformationException {

        try {
            JavaTemplate template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.VIEW_COMMAND_TEMAPLTE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.VIEW_COMMAND_TEMAPLTE, template);
            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.VIEW_COMMAND_HANDLER_TEMAPLTE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.VIEW_COMMAND_HANDLER_TEMAPLTE,
                    template);
            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE, template);
        } catch (JavaTemplateException e) {
            throw new NabuccoTransformationException("Error loading java edit view templates.", e);
        }
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

    }

}
