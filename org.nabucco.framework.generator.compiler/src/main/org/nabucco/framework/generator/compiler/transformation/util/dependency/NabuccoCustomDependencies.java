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

/**
 * NabuccoCustomDependencies
 * <p/>
 * Singleton holding compiler specific dependencies.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class NabuccoCustomDependencies {

    private static Map<String, String> importMap;

    private static final String ENTITY_MANAGER = "EntityManager";

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoCustomDependencies() {
    }

    static {
        importMap = new HashMap<String, String>();
        importMap.put(ENTITY_MANAGER, "javax.persistence.EntityManager");
    }

    public static String getServiceReference(String name) {
        return importMap.get(name);
    }

    public static boolean isCustomDeclaration(String name) {
        return importMap.containsKey(name);
    }

}
