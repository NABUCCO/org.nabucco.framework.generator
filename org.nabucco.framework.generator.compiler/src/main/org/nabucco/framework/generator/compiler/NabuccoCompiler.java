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
package org.nabucco.framework.generator.compiler;

import java.util.List;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformation;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.engine.NabuccoTransformationEngine;
import org.nabucco.framework.generator.compiler.transformation.engine.NabuccoTransformationEngineFactory;
import org.nabucco.framework.generator.compiler.transformation.util.file.NabuccoTargetFileCreator;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitor;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;

/**
 * NabuccoCompiler
 * <p/>
 * Compiles a {@link NabuccoModel} to multiple target models. This class is not tended to be
 * sub-classed.
 * 
 * @see NabuccoCompilerOptions
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class NabuccoCompiler {

    private NabuccoCompilerOptions options;

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoCompiler.class);

    /**
     * Creates a new {@link NabuccoCompiler} instance with appropriate compiler options.
     * 
     * @param options
     *            the compiler options
     * 
     * @see NabuccoCompilerOptions
     */
    public NabuccoCompiler(NabuccoCompilerOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("Compiler Options must be set.");
        }
        this.options = options;
    }

    /**
     * Compiles a NABUCCO model to target models (Java, XML, etc.).
     * 
     * @param target
     *            the target containing the model to compile.
     * 
     * @see NabuccoTransformation
     * @see NabuccoVisitor
     * 
     * @throws NabuccoCompilerException
     */
    public void compile(NabuccoCompilationTarget target) throws NabuccoCompilerException {

        List<MdaModel<NabuccoModel>> modelList = target.getModelList();
        String rootDir = target.getRootDirectory();
        String component = target.getComponent();

        try {
            this.transformModel(modelList, rootDir, component);
        } catch (NabuccoTransformationException e) {
            throw new NabuccoCompilerException("Error transforming NABUCCO model.", e);
        } catch (Exception e) {
            throw new NabuccoCompilerException("Error compiling NABUCCO model.", e);
        }
    }

    /**
     * Transforms the NABUCCO model.
     * 
     * @param modelList
     *            the NABUCCO model to transform
     * @param rootDir
     *            the project root directory
     * @param component
     *            the component name
     * 
     * @see NabuccoTransformationEngine
     * @see NabuccoTransformationEngineFactory
     * 
     * @throws NabuccoTransformationException
     */
    private void transformModel(List<MdaModel<NabuccoModel>> modelList, String rootDir,
            String component) throws NabuccoTransformationException {

        NabuccoTransformationEngine engine = NabuccoTransformationEngineFactory.getInstance()
                .retrieveEngine(rootDir, this.options);

        engine.process(modelList);

        this.generateJava(rootDir, engine.getJavaTarget());
        this.generateXml(rootDir, engine.getXmlTarget());
        this.mergeFragments(rootDir, component);
    }

    /**
     * Create Java files depending on the compiler option GEN_JAVA.
     * 
     * @param rootDir
     *            the root directory
     * @param mdaModel
     *            the transformed java model
     * 
     * @throws NabuccoTransformationException
     */
    private void generateJava(String rootDir, MdaModel<JavaModel> mdaModel)
            throws NabuccoTransformationException {

        String formatter = this.options.getOption(NabuccoCompilerOptions.JAVA_FORMATTER_CONFIG);
        Boolean genJava = Boolean.valueOf(this.options.getOption(NabuccoCompilerOptions.GEN_JAVA));

        if (genJava) {
            logger.debug("Compiler option 'GENERATE_JAVA' is 'ENABLED'.");
            NabuccoTargetFileCreator.getInstance().createJavaFiles(mdaModel, rootDir, formatter);
        } else {
            logger.warning("Compiler option 'GENERATE_JAVA' is 'DISABLED'.");
        }
    }

    /**
     * Create XML files depending on the compiler option GEN_XML.
     * 
     * @param rootDir
     *            the root directory
     * @param mdaModel
     *            the transformed xml model
     * 
     * @throws NabuccoTransformationException
     */
    private void generateXml(String rootDir, MdaModel<XmlModel> mdaModel)
            throws NabuccoTransformationException {

        Boolean genXml = Boolean.valueOf(this.options.getOption(NabuccoCompilerOptions.GEN_XML));

        if (genXml) {
            logger.debug("Compiler option 'GENERATE_XML' is 'ENABLED'.");
            NabuccoTargetFileCreator.getInstance().createXmlFiles(mdaModel, rootDir);
        } else {
            logger.warning("Compiler option 'GENERATE_XML' is 'DISABLED'.");
        }
    }

    /**
     * Merges the generated XML fragments to single files depending on the compiler option
     * MERGE_FRAGMENTS.
     * 
     * @param rootDir
     *            the root directry
     * @param the
     *            component name
     * 
     * @throws NabuccoTransformationException
     */
    private void mergeFragments(String rootDir, String component)
            throws NabuccoTransformationException {

        Boolean mergeXml = Boolean.valueOf(this.options
                .getOption(NabuccoCompilerOptions.MERGE_FRAGMENTS));

        if (mergeXml) {
            logger.debug("Compiler option 'MERGE_FRAGMENTS' is 'ENABLED'.");
            NabuccoTargetFileCreator.getInstance().mergeFragments(rootDir, component);
        } else {
            logger.warning("Compiler option 'MERGE_FRAGMENTS' is 'DISABLED'.");
        }
    }
}
