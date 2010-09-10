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
package org.nabucco.framework.generator.compiler.transformation.java.view.edit;

import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.view.common.combobox.NabuccoToJavaRcpViewComboBoxVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.view.common.picker.NabuccoToJavaRcpViewPickerVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaModelVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplateException;
import org.nabucco.framework.mda.template.java.JavaTemplateLoader;

/**
 * NabuccoToJavaViewEditTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaViewEditTransformation extends NabuccoToJavaTransformation implements
        NabuccoJavaTemplateConstants {

    /**
     * Creates a new {@link NabuccoToJavaViewEditTransformation} instance.
     * 
     * @param source
     *            the source model.
     * @param target
     *            the target model.
     * @param context
     *            the context of the visitor.
     */
    public NabuccoToJavaViewEditTransformation(MdaModel<NabuccoModel> source,
            MdaModel<JavaModel> target, NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoToJavaVisitorContext visitorContext = super.createVisitorContext(context);
        NabuccoToJavaModelVisitor visitor = new NabuccoToJavaRcpViewEditVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewEditWidgetFactoryVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewModelEditVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewPickerVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewComboBoxVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewEditBrowserElementVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewEditBrowserElementHandlerVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext)
            throws NabuccoTransformationException {

        try {
            JavaTemplate template = JavaTemplateLoader.getInstance().loadTemplate(
                    EDIT_VIEW_TEMPLATE);
            visitorContext.putTemplate(EDIT_VIEW_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(EDIT_WIDGET_FACTORY_TEMPLATE);
            visitorContext.putTemplate(EDIT_WIDGET_FACTORY_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);
            visitorContext.putTemplate(EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    EDIT_VIEW_PICKER_CONTENT_PROVIDER_TEMPLATE);
            visitorContext.putTemplate(EDIT_VIEW_PICKER_CONTENT_PROVIDER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    EDIT_VIEW_PICKER_CONTENT_PROVIDER_HANDLER_TEMPLATE);
            visitorContext
                    .putTemplate(EDIT_VIEW_PICKER_CONTENT_PROVIDER_HANDLER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(COMMON_VIEW_MODEL_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_MODEL_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_MODEL_METHOD_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(COMMON_VIEW_VIEW_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_VIEW_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    EDIT_VIEW_PICKER_HANDLER_TEMPLATE);
            visitorContext.putTemplate(EDIT_VIEW_PICKER_HANDLER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    COMMON_VIEW_COMBO_BOX_CONTENT_PROVIDER_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_COMBO_BOX_CONTENT_PROVIDER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    COMMON_VIEW_COMBO_BOX_HANDLER_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_COMBO_BOX_HANDLER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    COMMON_VIEW_COMBO_BOX_LABEL_PROVIDER_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_COMBO_BOX_LABEL_PROVIDER_TEMPLATE, template);

            // only needed for datatype-picker
            template = JavaTemplateLoader.getInstance().loadTemplate(
                    EDIT_VIEW_PICKER_COMPARATOR_TEMPLATE);
            visitorContext.putTemplate(EDIT_VIEW_PICKER_COMPARATOR_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    LIST_VIEW_LABEL_PROVIDER_TEMPLATE);
            visitorContext.putTemplate(LIST_VIEW_LABEL_PROVIDER_TEMPLATE, template);

            // browser element
            template = JavaTemplateLoader.getInstance().loadTemplate(BROWSER_VIEW_ELEMENT_TEMPLATE);
            visitorContext.putTemplate(BROWSER_VIEW_ELEMENT_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(BROWSER_VIEW_HELPER_TEMPLATE);
            visitorContext.putTemplate(BROWSER_VIEW_HELPER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    BROWSER_VIEW_ELEMENT_HANDLER_TEMPLATE);
            visitorContext.putTemplate(BROWSER_VIEW_ELEMENT_HANDLER_TEMPLATE, template);

        } catch (JavaTemplateException e) {
            throw new NabuccoTransformationException("Error loading java edit view templates.", e);
        }
    }

}
