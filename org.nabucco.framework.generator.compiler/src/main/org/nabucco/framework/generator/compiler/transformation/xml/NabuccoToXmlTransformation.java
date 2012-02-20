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
package org.nabucco.framework.generator.compiler.transformation.xml;

import java.util.Map.Entry;

import org.nabucco.framework.generator.compiler.NabuccoCompilerOptionType;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformation;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitor;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.MdaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.nabucco.framework.mda.transformation.TransformationContext;

/**
 * NabuccoToXmlTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoToXmlTransformation extends NabuccoTransformation<XmlModel> {

    /**
     * Creates a new {@link NabuccoToXmlTransformation} instance to transform from source to target.
     * 
     * @param source
     *            the source model
     * @param target
     *            the target model
     * @param context
     *            the transformation context.
     */
    public NabuccoToXmlTransformation(MdaModel<NabuccoModel> source, MdaModel<XmlModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    /**
     * Creates the {@link NabuccoToJavaVisitorContext} visitor context for all
     * {@link NabuccoToJavaTransformation} depending on target model. All {@link JavaTemplate} in
     * {@link TransformationContext} are put into the visitor context.
     * 
     * @param context
     *            the transformation context
     * 
     * @return the visitor context.
     * 
     * @throws NabuccoTransformationException
     */
    protected NabuccoToXmlVisitorContext createVisitorContext(NabuccoTransformationContext context)
            throws NabuccoTransformationException {

        NabuccoToXmlVisitorContext visitorContext = new NabuccoToXmlVisitorContext();

        copyTemplates(context, visitorContext);

        String outDir = context.getCompilerOptions().getOption(NabuccoCompilerOptionType.OUT_DIR);

        visitorContext.setOutDir(outDir);
        visitorContext.setRootDir(context.getRootDir());

        this.loadTemplates(visitorContext);

        return visitorContext;
    }

    /**
     * Copies transformation templates to the visitor context.
     * 
     * @param context
     *            the transformation context
     * @param visitorContext
     *            the visitor context
     * 
     * @throws NabuccoTransformationException
     */
    private void copyTemplates(NabuccoTransformationContext context, NabuccoToXmlVisitorContext visitorContext)
            throws NabuccoTransformationException {
        for (Entry<String, MdaTemplate<?>> entry : context.getTemplateMap().entrySet()) {
            try {
                if (entry.getValue() instanceof XmlTemplate) {
                    visitorContext.putTemplate(entry.getKey(), (XmlTemplate) entry.getValue());
                }
            } catch (XmlTemplateException e) {
                throw new NabuccoTransformationException("XML template not valid: " + entry.getKey(), e);
            }
        }
    }

    /**
     * Callback method to load templates for the appropriate {@link NabuccoToXmlVisitor}. The
     * template are inserted into the {@link NabuccoToXmlVisitorContext} instance.
     * 
     * @throws NabuccoVisitorException
     */
    protected abstract void loadTemplates(NabuccoToXmlVisitorContext visitorContext)
            throws NabuccoTransformationException;

}
