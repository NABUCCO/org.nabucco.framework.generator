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
package org.nabucco.framework.generator.compiler.transformation.util.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.generator.compiler.NabuccoCompilerOptionType;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.verifier.NabuccoVerificationException;
import org.nabucco.framework.generator.compiler.verifier.NabuccoVerifier;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoDependencyThread
 * <p/>
 * Thread ({@link Callable}) for parallelizing dependency resolving.
 * 
 * @see Callable
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoDependencyThread implements Callable<List<MdaModel<NabuccoModel>>>, NabuccoTransformationConstants {

    private MdaModel<NabuccoModel> model;

    private String rootDir;

    private String outDir;

    private NabuccoCompilerOptions options;

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoDependencyThread.class);

    /**
     * Creates a new {@link NabuccoDependencyThread} instance.
     * 
     * @param model
     *            the NABUCCO model
     * @param options
     *            compiler options
     * @param rootDir
     *            the NABUCCO root directory
     * @param outDir
     *            the NABUCCO out directory
     */
    public NabuccoDependencyThread(MdaModel<NabuccoModel> model, NabuccoCompilerOptions options, String rootDir) {
        this.model = model;
        this.rootDir = rootDir;
        this.outDir = options.getOption(NabuccoCompilerOptionType.OUT_DIR);
        this.options = options;
    }

    @Override
    public List<MdaModel<NabuccoModel>> call() throws Exception {

        List<MdaModel<NabuccoModel>> dependencies = new ArrayList<MdaModel<NabuccoModel>>();

        try {
            this.verifyModel();

            dependencies.addAll(NabuccoDependencyResolver.getInstance().resolveDependencies(this.model, this.rootDir,
                    this.outDir));

            logger.trace("Found '" + dependencies.size(), "' dependencies to resolve for '", model.getModel()
                    .getNabuccoType().name(), "'.");

        } catch (NabuccoVerificationException ve) {
            raiseException(ve.getMessage(), this.model, ve);
        } catch (NabuccoVisitorException ve) {
            raiseException(ve.getMessage(), this.model, ve.getCause());
        } catch (Exception e) {
            raiseException("Unexpected error during NABUCCO dependency resolving.", this.model, e);
        }

        return dependencies;
    }

    /**
     * Verifies the NABUCCO model for semantic correctness.
     * 
     * @throws NabuccoVerificationException
     */
    private void verifyModel() throws NabuccoVerificationException {
        NabuccoVerifier.getInstance().verifyNabuccoModel(this.model, this.rootDir, this.options);
    }

    /**
     * Raises the {@link NabuccoTransformationException}.
     * 
     * @param msg
     *            the original message
     * @param currentModel
     *            the current nabucco model
     * @param cause
     *            the causing exception
     * 
     * @throws NabuccoTransformationException
     */
    private void raiseException(String msg, MdaModel<NabuccoModel> currentModel, Exception cause)
            throws NabuccoTransformationException {

        if (msg != null) {
            logger.error(msg);
        }

        logger.debug(cause);

        NabuccoTransformationException exception;
        if (cause instanceof NabuccoTransformationException) {
            exception = (NabuccoTransformationException) cause;
        } else {
            exception = new NabuccoTransformationException(msg, cause);
        }

        if (currentModel != null && currentModel.getModel() != null) {
            NabuccoModel model = currentModel.getModel();

            String path = model.getPath();
            String type = model.getNabuccoType() != null ? model.getNabuccoType().getId() : null;

            if (path.contains(NBC_SOURCE_FOLDER)) {
                path = path.substring(path.indexOf(NBC_SOURCE_FOLDER) + NBC_SOURCE_FOLDER.length())
                        .replace(File.separatorChar, '.').replace(".nbc", "");
            }

            exception.setParameters(type, path);
        }

        throw exception;
    }
}
