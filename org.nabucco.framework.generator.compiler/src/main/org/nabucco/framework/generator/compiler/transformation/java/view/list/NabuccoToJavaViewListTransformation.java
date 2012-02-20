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
package org.nabucco.framework.generator.compiler.transformation.java.view.list;

import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplateException;
import org.nabucco.framework.mda.template.java.JavaTemplateLoader;

/**
 * NabuccoToJavaViewListTransformation
 * 
 * @author Stefanie Feld, Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaViewListTransformation extends NabuccoToJavaTransformation {

    public NabuccoToJavaViewListTransformation(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoToJavaVisitorContext visitorContext = super.createVisitorContext(context);
        NabuccoToJavaVisitor visitor = new NabuccoToJavaRcpViewListVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewListWidgetFactoryVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewListLabelProviderVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewComparatorVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewTableFilterVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewListBrowserElementVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewListBrowserElementHandlerVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaRcpViewModelListVisitor(visitorContext);
        source.getModel().getUnit().accept(visitor, target);
    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext) throws NabuccoTransformationException {

        try {
            JavaTemplate template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.LIST_VIEW_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.LIST_VIEW_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.LIST_VIEW_WIDGET_FACTORY_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.LIST_VIEW_WIDGET_FACTORY_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.LIST_VIEW_LABEL_PROVIDER_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.LIST_VIEW_LABEL_PROVIDER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.COMMON_VIEW_VIEW_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.LIST_VIEW_COMPARATOR_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.LIST_VIEW_COMPARATOR_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.LIST_VIEW_TABLE_FILTER_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.LIST_VIEW_TABLE_FILTER_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.BROWSER_VIEW_LIST_ELEMENT_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.BROWSER_VIEW_LIST_ELEMENT_TEMPLATE, template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.BROWSER_VIEW_LIST_ELEMENT_HANDLER_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.BROWSER_VIEW_LIST_ELEMENT_HANDLER_TEMPLATE,
                    template);

            template = JavaTemplateLoader.getInstance().loadTemplate(
                    NabuccoJavaTemplateConstants.LIST_VIEW_MODEL_TEMPLATE);
            visitorContext.putTemplate(NabuccoJavaTemplateConstants.LIST_VIEW_MODEL_TEMPLATE, template);

        } catch (JavaTemplateException e) {
            throw new NabuccoTransformationException("Error loading java list view templates.", e);
        }
    }

}
