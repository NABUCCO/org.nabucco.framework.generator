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
package org.nabucco.framework.generator.compiler.transformation.java.common.extension;

import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;

/**
 * NabuccoToJavaExtensionEvaluator
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaExtensionEvaluator {

    /**
     * Singleton instance.
     */
    private static NabuccoToJavaExtensionEvaluator instance = new NabuccoToJavaExtensionEvaluator();

    /**
     * Private constructor.
     */
    private NabuccoToJavaExtensionEvaluator() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoToJavaExtensionEvaluator instance.
     */
    public static NabuccoToJavaExtensionEvaluator getInstance() {
        return instance;
    }

    /**
     * Extracts the extension declaration of a {@link NabuccoUnit} instance.
     * 
     * @param unit
     *            the unit to evaluate
     * 
     * @return the extension declaration string
     */
    public String getExtension(NabuccoUnit unit) {
        NabuccoToJavaExtensionVisitor visitor = new NabuccoToJavaExtensionVisitor();
        unit.accept(visitor);

        return visitor.getExtension();
    }

}
