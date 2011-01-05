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
package org.nabucco.framework.generator.compiler.transformation.util.dependency;

import java.util.HashMap;
import java.util.Map;

import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoDependencyContainer
 * <p/>
 * Container for all already visited NABUCCO dependencies.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoDependencyContainer {

    private Map<String, MdaModel<NabuccoModel>> modelMap = new HashMap<String, MdaModel<NabuccoModel>>();

    /**
     * Singleton instance.
     */
    private static NabuccoDependencyContainer instance;

    /**
     * Private constructor.
     */
    private NabuccoDependencyContainer() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoDependencyContainer instance.
     */
    public static synchronized NabuccoDependencyContainer getInstance() {
        if (instance == null) {
            instance = new NabuccoDependencyContainer();
        }
        return instance;
    }

    /**
     * Add a dependency into the map.
     * 
     * @param key
     *            the full qualified name
     * @param model
     *            the dependency
     */
    public synchronized void putModel(String key, MdaModel<NabuccoModel> model) {
        this.modelMap.put(key, model);
    }

    /**
     * Retrieves a dependency of the map.
     * 
     * @param key
     *            the full qualified name
     * 
     * @return the dependency
     */
    public synchronized MdaModel<NabuccoModel> getModel(String key) {
        return this.modelMap.get(key);
    }

    /**
     * Checks whether an import dependency is contained in the map.
     * 
     * @param importString
     *            the full qualified import key
     * 
     * @return <b>true</b> if the import is contained <b>false</b> if not.
     */
    public synchronized Boolean containsImport(String importString) {
        return this.modelMap.containsKey(importString);
    }

    /**
     * Checks whether a type dependency is contained in the map.
     * 
     * @param type
     *            the unqualified type key
     * 
     * @return <b>true</b> if the import is contained <b>false</b> if not.
     */
    public synchronized Boolean containsType(String type) {
        for (String key : this.modelMap.keySet()) {
            if (key.endsWith(type)) {
                String[] importToken = key.split("\\.");
                if (importToken[importToken.length - 1].equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Clears the dependency cache.
     */
    public synchronized void clear() {
        this.modelMap.clear();
    }
}
