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
package org.nabucco.framework.generator.compiler.precompiler;

import java.util.EnumMap;
import java.util.Map;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;

/**
 * NabuccoPreCompilerInstance
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoPreCompilerInstance implements NabuccoPreCompiler {

    /** The pre-compiler delegates. */
    private Map<NabuccoModelType, NabuccoPreCompiler> preCompilerMap;

    /**
     * Singleton instance.
     */
    private static NabuccoPreCompilerInstance instance = new NabuccoPreCompilerInstance();

    /**
     * Private constructor.
     */
    private NabuccoPreCompilerInstance() {
        this.init();
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoPreCompilerInstance instance.
     */
    public static NabuccoPreCompilerInstance getInstance() {
        return instance;
    }

    /**
     * Initializes the pre-compiler delegates.
     */
    private void init() {
        this.preCompilerMap = new EnumMap<NabuccoModelType, NabuccoPreCompiler>(
                NabuccoModelType.class);
    }

    @Override
    public void preCompile(NabuccoModel model) throws NabuccoTransformationException {
        NabuccoPreCompiler compiler = this.preCompilerMap.get(model.getNabuccoType());

        if (compiler != null) {
            compiler.preCompile(model);
        }
    }
}
