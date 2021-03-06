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

import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.mda.transformation.TransformationContext;

/**
 * NabuccoTransformationContext
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class NabuccoTransformationContext extends TransformationContext {

    private NabuccoCompilerOptions compilerOptions;

    /**
     * Creates a new {@link NabuccoTransformationContext} instance.
     * 
     * @param rootDir
     *            the root directory
     * @param compilerOptions
     *            the compiler options
     */
    public NabuccoTransformationContext(String rootDir, NabuccoCompilerOptions compilerOptions) {
        super(rootDir);
        this.compilerOptions = compilerOptions;
    }

    /**
     * Getter for the compiler options.
     * 
     * @return Returns the compilerOptions.
     */
    public NabuccoCompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

}
