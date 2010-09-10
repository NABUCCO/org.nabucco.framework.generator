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
package org.nabucco.framework.generator.compiler.transformation.java.view.search;

import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.view.common.combobox.NabuccoToJavaRcpViewComboBoxVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaModelVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplateException;
import org.nabucco.framework.mda.template.java.JavaTemplateLoader;

/**
 * NabuccoToJavaRcpViewSearchTransformation
 * 
 * @author Silas Schwarz, Stefanie Feld PRODYNA AG
 */
public class NabuccoToJavaRcpViewSearchTransformation extends NabuccoToJavaTransformation implements
        NabuccoJavaTemplateConstants {

    /**
     * @param source
     * @param target
     * @param context
     */
    public NabuccoToJavaRcpViewSearchTransformation(MdaModel<NabuccoModel> source,
            MdaModel<JavaModel> target, NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        // visitor for search view generation
        NabuccoToJavaVisitorContext visitorContext = super.createVisitorContext(context);
        NabuccoToJavaModelVisitor visitor = new NabuccoToJavaRcpViewSearchVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        // visitor for widget factory generation
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewSearchWidgetFactoryVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        // visitor for layouter generation
        // visitorContext = super.createVisitorContext(context);
        // visitor = new NabuccoToJavaRcpViewSearchLayouterVisitor(visitorContext);
        // source.getModel().getUnit().accept(visitor, target);

        // visitor for model generation
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewSearchViewModelVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        // visitor for comboBox generation
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewComboBoxVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext)
            throws NabuccoTransformationException {
        try {
            // template for Search View
            JavaTemplate template = JavaTemplateLoader.getInstance().loadTemplate(
                    SEARCH_VIEW_TEMPLATE);
            visitorContext.putTemplate(SEARCH_VIEW_TEMPLATE, template);

            // template for Widget factory
            template = JavaTemplateLoader.getInstance().loadTemplate(
                    SEARCH_VIEW_WIDGET_FACTORY_TEMPLATE);
            visitorContext.putTemplate(SEARCH_VIEW_WIDGET_FACTORY_TEMPLATE, template);
            // template for Widget factory - reusing functionality from edit view widget factory
            template = JavaTemplateLoader.getInstance().loadTemplate(
                    EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE);
            visitorContext.putTemplate(EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE, template);
            // template for layouter
            template = JavaTemplateLoader.getInstance().loadTemplate(SEARCH_VIEW_LAYOUTER_TEMPLATE);
            visitorContext.putTemplate(SEARCH_VIEW_LAYOUTER_TEMPLATE, template);

            // template for model
            template = JavaTemplateLoader.getInstance().loadTemplate(SEARCH_VIEW_MODEL_TEMPLATE);
            visitorContext.putTemplate(SEARCH_VIEW_MODEL_TEMPLATE, template);

            // template for common model parts
            template = JavaTemplateLoader.getInstance().loadTemplate(COMMON_VIEW_MODEL_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_MODEL_TEMPLATE, template);

            // template for common model method parts
            template = JavaTemplateLoader.getInstance().loadTemplate(
                    COMMON_VIEW_MODEL_METHOD_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_MODEL_METHOD_TEMPLATE, template);

            // template for common ui method parts
            template = JavaTemplateLoader.getInstance().loadTemplate(COMMON_VIEW_VIEW_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_VIEW_TEMPLATE, template);

            // templates for comboBox generation
            template = JavaTemplateLoader.getInstance().loadTemplate(
                    COMMON_VIEW_COMBO_BOX_CONTENT_PROVIDER_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_COMBO_BOX_CONTENT_PROVIDER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    COMMON_VIEW_COMBO_BOX_HANDLER_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_COMBO_BOX_HANDLER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    COMMON_VIEW_COMBO_BOX_LABEL_PROVIDER_TEMPLATE);
            visitorContext.putTemplate(COMMON_VIEW_COMBO_BOX_LABEL_PROVIDER_TEMPLATE, template);
            
        } catch (JavaTemplateException e) {
            throw new NabuccoTransformationException("Error loading java edit view templates.", e);
        }

    }
}
