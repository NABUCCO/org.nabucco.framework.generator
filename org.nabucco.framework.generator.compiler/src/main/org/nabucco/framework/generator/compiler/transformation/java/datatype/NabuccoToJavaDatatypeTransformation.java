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
package org.nabucco.framework.generator.compiler.transformation.java.datatype;

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
 * NabuccoToJavaDatatypeTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaDatatypeTransformation extends NabuccoToJavaTransformation {

    public NabuccoToJavaDatatypeTransformation(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoModel nabuccoModel = source.getModel();

        NabuccoToJavaVisitor visitor;
        NabuccoToJavaVisitorContext visitorContext;

        visitorContext = super.createVisitorContext(context);
        visitor = new NabuccoToJavaDatatypeVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
    }

    @Override
    protected void loadTemplates(NabuccoToJavaVisitorContext visitorContext) throws NabuccoVisitorException {

        try {
            JavaTemplateLoader loader = JavaTemplateLoader.getInstance();

            String name = NabuccoJavaTemplateConstants.DATATYPE_TEMPLATE;
            visitorContext.putTemplate(name, loader.loadTemplate(name));

            name = NabuccoJavaTemplateConstants.COMPONENT_RELATION_TEMPLATE;
            visitorContext.putTemplate(name, loader.loadTemplate(name));

        } catch (MdaExeception e) {
            throw new NabuccoVisitorException("Error loading java datatype templates.", e);
        }
    }
}
