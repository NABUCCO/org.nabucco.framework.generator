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
package org.nabucco.framework.generator.compiler.transformation.java.application;

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
import org.nabucco.framework.mda.template.java.JavaTemplateLoader;

/**
 * NabuccoToJavaApplicationTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaApplicationTransformation extends NabuccoToJavaTransformation {

    /**
     * Creates a new {@link NabuccoToJavaApplicationTransformation} instance.
     * 
     * @param source
     *            the source model
     * @param target
     *            the target model
     * @param context
     *            the transformation context
     */
    public NabuccoToJavaApplicationTransformation(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoToJavaVisitor visitor;
        NabuccoToJavaVisitorContext visitorContext;

        NabuccoModel nabuccoModel = source.getModel();

        // Application
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaApplicationVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);

        // Component Relation Type
        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaComponentRelationTypeVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext) throws NabuccoTransformationException {

        try {

            JavaTemplateLoader loader = JavaTemplateLoader.getInstance();

            String name = NabuccoJavaTemplateConstants.APPLICATION_TEMPLATE;
            visitorContext.putTemplate(name, loader.loadTemplate(name));

            name = NabuccoJavaTemplateConstants.DATATYPE_CONNECTOR_TEMPLATE;
            visitorContext.putTemplate(name, loader.loadTemplate(name));

            name = NabuccoJavaTemplateConstants.SERVICE_CONNECTOR_TEMPLATE;
            visitorContext.putTemplate(name, loader.loadTemplate(name));

            name = NabuccoJavaTemplateConstants.CONNECTOR_CALLBACK_TEMPLATE;
            visitorContext.putTemplate(name, loader.loadTemplate(name));

            name = NabuccoJavaTemplateConstants.COMPONENT_RELATION_TYPE_TEMPLATE;
            visitorContext.putTemplate(name, loader.loadTemplate(name));

        } catch (MdaExeception e) {
            throw new NabuccoVisitorException("Error loading java application templates.", e);
        }
    }
}
