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
package org.nabucco.framework.generator.compiler.transformation.java;

import java.util.Map.Entry;

import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformation;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaModelVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.template.MdaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * Transformation between NABUCCO and Java models.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoToJavaTransformation extends NabuccoTransformation<JavaModel> {

    /**
     * Creates a new {@link NabuccoToJavaTransformation} instance to transform from source to
     * target.
     * 
     * @param source
     *            the source model
     * @param target
     *            the target model
     * @param context
     *            the transformation context.
     */
    public NabuccoToJavaTransformation(MdaModel<NabuccoModel> source, MdaModel<JavaModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    /**
     * Creates the {@link NabuccoToJavaVisitorContext} visitor context for all
     * {@link NabuccoToJavaTransformation} depending on target model. All {@link JavaTemplate} in
     * {@link NabuccoTransformationContext} are put into the visitor context.
     * 
     * @param context
     *            the transformation context
     * 
     * @return the visitor context.
     * 
     * @throws NabuccoTransformationException
     */
    protected NabuccoToJavaVisitorContext createVisitorContext(NabuccoTransformationContext context)
            throws NabuccoTransformationException {

        NabuccoToJavaVisitorContext visitorContext = new NabuccoToJavaVisitorContext();

        this.copyTemplates(context, visitorContext);

        String outDir = context.getCompilerOptions().getOption(NabuccoCompilerOptions.OUT_DIR);

        visitorContext.setRootDir(context.getRootDir());
        visitorContext.setOutDir(outDir);
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
    private void copyTemplates(NabuccoTransformationContext context,
            NabuccoToJavaVisitorContext visitorContext) throws NabuccoTransformationException {
        for (Entry<String, MdaTemplate<?>> entry : context.getTemplateMap().entrySet()) {
            try {
                if (entry.getValue() instanceof JavaTemplate) {
                    visitorContext.putTemplate(entry.getKey(), (JavaTemplate) entry.getValue());
                }
            } catch (JavaTemplateException e) {
                throw new NabuccoTransformationException("Java template not valid: "
                        + entry.getKey(), e);
            }
        }
    }

    /**
     * Callback method to load templates for the appropriate {@link NabuccoToJavaModelVisitor}. The
     * template are inserted into the {@link NabuccoToJavaVisitorContext} instance.
     * 
     * @throws NabuccoVisitorException
     */
    protected abstract void loadTemplates(NabuccoToJavaVisitorContext visitorContext)
            throws NabuccoTransformationException;

}
