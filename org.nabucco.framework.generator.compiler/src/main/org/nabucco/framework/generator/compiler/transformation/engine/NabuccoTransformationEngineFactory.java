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
package org.nabucco.framework.generator.compiler.transformation.engine;

import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.generator.compiler.NabuccoCompilerOptionType;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;

/**
 * NabuccoTransformationEngineFactory
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoTransformationEngineFactory {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoTransformationEngineFactory.class);

    /**
     * Singleton instance.
     */
    private static NabuccoTransformationEngineFactory instance = new NabuccoTransformationEngineFactory();

    /**
     * Private constructor.
     */
    private NabuccoTransformationEngineFactory() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoTransformationEngineFactory instance.
     */
    public static NabuccoTransformationEngineFactory getInstance() {
        return instance;
    }

    /**
     * Retrieves the appropriate {@link NabuccoTransformationEngine} for generation.
     * 
     * @param rootDir
     *            the project root directory
     * @param options
     *            the compiler options
     * 
     * @return the specified engine type.
     * 
     * @throws NabuccoTransformationException
     */
    public NabuccoTransformationEngine retrieveEngine(String rootDir, NabuccoCompilerOptions options)
            throws NabuccoTransformationException {

        String engineType = options.getOption(NabuccoCompilerOptionType.ENGINE);

        logger.debug("Configured Transformation Engine: " + engineType);

        if (engineType.equals(NabuccoCompilerOptionType.ENGINE.getDefaultValue())) {
            return new DefaultTransformationEngine(rootDir, options);
        }

        logger.warning("Engine type is not specified. Using NABUCCO engine.");
        return new DefaultTransformationEngine(rootDir, options);
    }

    /**
     * Retrieves the default {@link NabuccoTransformationEngine} for generation.
     * 
     * @param rootDir
     *            the project root directory
     * @param options
     *            the compiler options
     * 
     * @return the default transformation engine
     * 
     * @throws NabuccoTransformationException
     */
    public NabuccoTransformationEngine retrieveDefaultEngine(String rootDir, NabuccoCompilerOptions options)
            throws NabuccoTransformationException {
        return new DefaultTransformationEngine(rootDir, options);
    }

}
