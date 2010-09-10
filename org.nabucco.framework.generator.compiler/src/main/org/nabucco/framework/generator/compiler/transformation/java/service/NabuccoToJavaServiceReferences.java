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
package org.nabucco.framework.generator.compiler.transformation.java.service;

import java.util.HashMap;
import java.util.Map;

/**
 * NabuccoToJavaServiceReferences
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class NabuccoToJavaServiceReferences {

    private static Map<String, String> importMap;

    private static final String ENTITY_MANAGER = "EntityManager";

    private NabuccoToJavaServiceReferences() {
    }

    static {
        importMap = new HashMap<String, String>();
        importMap.put(ENTITY_MANAGER, "javax.persistence.EntityManager");
    }

    public static String getServiceReference(String name) {
        return importMap.get(name);
    }

    public static boolean isServiceReference(String name) {
        return importMap.containsKey(name);
    }

}
