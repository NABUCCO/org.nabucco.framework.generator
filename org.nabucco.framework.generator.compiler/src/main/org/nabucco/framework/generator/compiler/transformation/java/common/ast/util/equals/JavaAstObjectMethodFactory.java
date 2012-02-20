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
package org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.equals;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaAstObjectMethodFactory
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class JavaAstObjectMethodFactory {

    /**
     * Singleton instance.
     */
    private static JavaAstObjectMethodFactory instance = new JavaAstObjectMethodFactory();

    /** The Map of ObjectMethod Strategies */
    private Map<Class<? extends ObjectMethodStrategy>, ObjectMethodStrategy> objectStrategies = new HashMap<Class<? extends ObjectMethodStrategy>, ObjectMethodStrategy>();

    /**
     * Private constructor.
     */
    private JavaAstObjectMethodFactory() {
    }

    /**
     * Singleton access.
     * 
     * @return the JavaAstObjectMethodFactory instance.
     */
    public static JavaAstObjectMethodFactory getInstance() {
        return instance;
    }

    /**
     * Creates the default {@link ObjectMethodStrategy} implementation instance.
     * 
     * @return the default strategy.
     */
    public ObjectMethodStrategy getDefaultStrategy() {
        Class<DefaultObjectMethodStrategy> key = DefaultObjectMethodStrategy.class;

        if (objectStrategies.containsKey(key)) {
            return objectStrategies.get(key);
        }

        ObjectMethodStrategy strategy = new DefaultObjectMethodStrategy();
        objectStrategies.put(key, strategy);
        return strategy;
    }

    /**
     * Creates {@link ObjectMethodStrategy} implementation instance creating object methods without
     * collections.
     * 
     * @return the no collection object method strategy.
     */
    public ObjectMethodStrategy getNoCollectionStrategy() {
        Class<NoCollectionObjectMethodStrategy> key = NoCollectionObjectMethodStrategy.class;

        if (objectStrategies.containsKey(key)) {
            return objectStrategies.get(key);
        }

        ObjectMethodStrategy strategy = new NoCollectionObjectMethodStrategy();
        objectStrategies.put(key, strategy);
        return strategy;
    }

}
