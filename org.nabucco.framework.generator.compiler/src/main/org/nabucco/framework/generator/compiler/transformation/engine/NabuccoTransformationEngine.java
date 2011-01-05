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
package org.nabucco.framework.generator.compiler.transformation.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.generator.compiler.precompiler.NabuccoPreCompilerInstance;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformation;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationResult;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyThread;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelException;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.MdaTemplateException;
import org.nabucco.framework.mda.template.java.JavaTemplateLoader;
import org.nabucco.framework.mda.template.java.provider.JavaTemplateFileProvider;
import org.nabucco.framework.mda.template.xml.XmlTemplateLoader;
import org.nabucco.framework.mda.template.xml.provider.XmlTemplateFileProvider;
import org.nabucco.framework.mda.transformation.Transformation;
import org.nabucco.framework.mda.transformation.TransformationEngine;

/**
 * NabuccoTransformationEngine
 * <p/>
 * Engine for processing transformations depending on a single NABUCCO source model. Resolves
 * dependencies and takes over threading issues of transformations.
 * 
 * @see ExecutorService
 * @see Transformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoTransformationEngine implements TransformationEngine<NabuccoModel> {

    private String rootDir;

    private String outDir;

    private MdaModel<JavaModel> javaTarget;

    private MdaModel<XmlModel> xmlTarget;

    private NabuccoCompilerOptions options;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * Creates a new {@link NabuccoTransformationEngine} instance by a root directory.
     * 
     * @param rootDir
     *            the component root directory
     * 
     * @throws NabuccoTransformationException
     */
    public NabuccoTransformationEngine(String rootDir, NabuccoCompilerOptions options)
            throws NabuccoTransformationException {

        this.rootDir = rootDir;
        this.options = options;

        this.outDir = this.options.getOption(NabuccoCompilerOptions.OUT_DIR);

        try {
            this.javaTarget = new MdaModel<JavaModel>(new JavaModel());
            this.xmlTarget = new MdaModel<XmlModel>(new XmlModel());

            String templateLocation = this.options.getOption(NabuccoCompilerOptions.TEMPLATE_DIR);

            this.init(templateLocation);

        } catch (ModelException e) {
            throw new NabuccoTransformationException("Error preparing target models.", e);
        }
    }

    /**
     * Initializes the generator properties.
     * 
     * @param templateLocation
     *            the template location
     * 
     * @throws NabuccoTransformationException
     */
    private void init(String templateLocation) throws NabuccoTransformationException {
        try {
            JavaTemplateLoader.getInstance().setTemplateProvider(
                    new JavaTemplateFileProvider(templateLocation));
            XmlTemplateLoader.getInstance().setTemplateProvider(
                    new XmlTemplateFileProvider(templateLocation));
        } catch (MdaTemplateException e) {
            throw new NabuccoTransformationException("Cannot configure generator templates.", e);
        }
    }

    /**
     * Processes the transformation of the appropriate NABUCCO source and its references.
     * 
     * @param sourceModel
     *            the root NABUCCO model
     * 
     * @return the generated models
     * 
     * @throws NabuccoTransformationException
     */
    @Override
    public final void process(MdaModel<NabuccoModel> sourceModel)
            throws NabuccoTransformationException {

        Queue<MdaModel<NabuccoModel>> modelQueue = new LinkedList<MdaModel<NabuccoModel>>();
        Queue<MdaModel<NabuccoModel>> dependencyQueue = new LinkedList<MdaModel<NabuccoModel>>();

        dependencyQueue.offer(sourceModel);
        modelQueue.offer(sourceModel);

        this.processQueues(modelQueue, dependencyQueue);
    }

    /**
     * Processes the transformation of the appropriate NABUCCO sources and their references.
     * 
     * @param sourceModel
     *            the root NABUCCO models
     * 
     * @return the generated models
     * 
     * @throws NabuccoTransformationException
     */
    @Override
    public final void process(List<MdaModel<NabuccoModel>> sourcemodels)
            throws NabuccoTransformationException {

        Queue<MdaModel<NabuccoModel>> modelQueue = new LinkedList<MdaModel<NabuccoModel>>();
        Queue<MdaModel<NabuccoModel>> dependencyQueue = new LinkedList<MdaModel<NabuccoModel>>();

        for (MdaModel<NabuccoModel> sourceModel : sourcemodels) {
            dependencyQueue.offer(sourceModel);
            modelQueue.offer(sourceModel);
        }

        this.processQueues(modelQueue, dependencyQueue);
    }

    /**
     * Processes the queues containing the NABUCCO models and starts the transformations.
     * 
     * @param modelQueue
     *            the queue of NABUCCO models
     * @param dependencyQueue
     *            the queue of NABUCCO dependencies
     * 
     * @throws NabuccoTransformationException
     */
    private final void processQueues(Queue<MdaModel<NabuccoModel>> modelQueue,
            Queue<MdaModel<NabuccoModel>> dependencyQueue) throws NabuccoTransformationException {

        this.startUp();

        try {
            List<Future<NabuccoTransformationResult>> transformationResults = new ArrayList<Future<NabuccoTransformationResult>>();
            List<NabuccoTransformation<?>> transformationList = new ArrayList<NabuccoTransformation<?>>();

            Set<String> alreadyTransformed = new HashSet<String>();

            while (!dependencyQueue.isEmpty() || !modelQueue.isEmpty()) {

                Future<List<MdaModel<NabuccoModel>>> dependencyResult = null;

                if (dependencyQueue.peek() != null) {

                    MdaModel<NabuccoModel> source = dependencyQueue.poll();

                    // Pre Compile
                    NabuccoPreCompilerInstance.getInstance().preCompile(source.getModel());

                    // Resolve Dependencies
                    NabuccoDependencyThread dependencyResolver = new NabuccoDependencyThread(
                            source, this.rootDir, this.outDir);
                    dependencyResult = EXECUTOR_SERVICE.submit(dependencyResolver);
                }

                while (modelQueue.peek() != null) {

                    MdaModel<NabuccoModel> source = modelQueue.poll();

                    this.produceTransformations(source, transformationList);

                    // Start transformation threads
                    transformationResults.addAll(EXECUTOR_SERVICE.invokeAll(transformationList));
                    transformationList.clear();

                    alreadyTransformed.add(source.getModel().getPath());
                }

                // Wait for dependencies and put them into the queues.
                if (dependencyResult != null) {
                    List<MdaModel<NabuccoModel>> dependencyList = dependencyResult.get();

                    for (MdaModel<NabuccoModel> dependency : dependencyList) {
                        NabuccoModel model = dependency.getModel();

                        if (model.getResourceType() == NabuccoModelResourceType.PROJECT
                                && !alreadyTransformed.contains(model.getPath())) {

                            modelQueue.add(dependency);
                            dependencyQueue.add(dependency);
                        }
                    }
                }

            }

            for (Future<NabuccoTransformationResult> transformationResult : transformationResults) {
                transformationResult.get();
            }

        } catch (ExecutionException ee) {
            raiseException(ee.getCause(), "NABUCCO transformation did not finish successful.");
        } catch (InterruptedException ie) {
            raiseException(ie, "NABUCCO transformation was interrupted.");
        } catch (Exception e) {
            raiseException(e, "Unexpected error during NABUCCO transformation.");
        } finally {
            this.shutDown();
        }
    }

    /**
     * Raises the {@link NabuccoTransformationException}.
     * 
     * @param cause
     *            the throwable to handle
     * @param message
     *            the exception message
     */
    private void raiseException(Throwable cause, String message) throws NabuccoTransformationException {
        if (cause instanceof NabuccoTransformationException) {
            throw (NabuccoTransformationException) cause;
        }
        if (cause instanceof Exception) {
            throw new NabuccoTransformationException(message, (Exception) cause);
        }
        throw new RuntimeException(cause);
    }

    /**
     * Starts up the necessary resources.
     */
    private void startUp() {
        // Clear all cached models for new generator process
        NabuccoDependencyResolver.getInstance().clearCache();
    }

    /**
     * Shutdown resources.
     */
    private void shutDown() {
        // Nothing to do here!
    }

    /**
     * Multiplexes the {@link NabuccoModelType} of the source model, creates appropriate
     * transformations and adds them to the transformation list.
     * 
     * @param source
     *            the NABUCCO source model
     * @param transformationList
     *            the list of transformations
     */
    protected abstract void produceTransformations(MdaModel<NabuccoModel> source,
            List<NabuccoTransformation<?>> transformationList);

    /**
     * Getter for the root directory.
     * 
     * @return Returns the rootDir.
     */
    protected String getRootDir() {
        return this.rootDir;
    }

    /**
     * Getter for the out directory.
     * 
     * @return Returns the outDir.
     */
    protected String getOutDir() {
        return this.outDir;
    }

    /**
     * Getter for the compiler options.
     * 
     * @return Returns the options.
     */
    protected NabuccoCompilerOptions getOptions() {
        return this.options;
    }

    /**
     * Returns the Java target model.
     * 
     * @return the Java target model
     */
    public MdaModel<JavaModel> getJavaTarget() {
        return this.javaTarget;
    }

    /**
     * Returns the XML target model.
     * 
     * @return the XML target model
     */
    public MdaModel<XmlModel> getXmlTarget() {
        return this.xmlTarget;
    }

}
