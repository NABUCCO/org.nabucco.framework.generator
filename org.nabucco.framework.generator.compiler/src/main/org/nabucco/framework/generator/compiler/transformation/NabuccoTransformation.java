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
package org.nabucco.framework.generator.compiler.transformation;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.nabucco.framework.generator.compiler.transformation.java.NabuccoToJavaTransformation;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelImplementation;
import org.nabucco.framework.mda.transformation.TransformationComponent;

/**
 * NabuccoTransformation
 * <p/>
 * Transformation of a NABUCCO model to a specific target model.
 * <p/>
 * For threading issues this class implements {@link Callable}.
 * 
 * @see ExecutorService
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoTransformation<T extends ModelImplementation> extends
        TransformationComponent<NabuccoModel, T, NabuccoTransformationContext> implements
        Callable<NabuccoTransformationResult>, NabuccoTransformationConstants {

    /** The transformation ID. */
    private long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

    /** The source model. */
    private MdaModel<NabuccoModel> source;

    /** The target model. */
    private MdaModel<T> target;

    /** The transformation context. */
    private NabuccoTransformationContext context;

    /** The logger. */
    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoTransformation.class);

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
     * 
     * @throws NabuccoTransformationThreadException
     */
    public NabuccoTransformation(MdaModel<NabuccoModel> source, MdaModel<T> target, NabuccoTransformationContext context) {

        if (source == null) {
            throw new IllegalArgumentException("Source model must be defined.");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target model must be defined.");
        }
        if (context == null) {
            throw new IllegalArgumentException("Transformation context must be defined.");
        }

        this.source = source;
        this.target = target;
        this.context = context;
    }

    @Override
    public final NabuccoTransformationResult call() throws Exception {

        String name = this.source.getModel().getName();
        String type = this.source.getModel().getNabuccoType().name();
        String sourceType = this.source.getModel().getType().name();
        String targetType = this.target.getModel().getType().name();

        logger.debug("Transformation [" + this.id + "]: Start transforming ", type, " '", name, "' from '", sourceType,
                "' to '", targetType, "'.");

        try {
            this.transformModel(this.source, this.target, this.context);

        } catch (NabuccoTransformationException te) {
            raiseException(te.getMessage(), this.source, te);
        } catch (NabuccoVisitorException ve) {
            raiseException(ve.getMessage(), this.source, ve.getCause());
        } catch (Exception e) {
            raiseException("Unexpected error during NABUCCO transformation.", this.source, e);
        } finally {
            logger.debug("Transformation [" + this.id + "]: Finish transforming ", type, " '", name, "' from '",
                    sourceType, "' to '", targetType, "'.");
        }

        return null;
    }

    @Override
    public abstract void transformModel(MdaModel<NabuccoModel> source, MdaModel<T> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException;

    /**
     * Raises the {@link NabuccoTransformationException}.
     * 
     * @param msg
     *            the original message
     * @param currentModel
     *            the current nabucco model
     * @param cause
     *            the cause
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
