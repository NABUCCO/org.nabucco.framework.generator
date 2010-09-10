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
package org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.equals;

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * ObjectMethodStrategy
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface ObjectMethodStrategy {

    /**
     * Creates <code>toString()</code>, <code>hashCode()</code> and <code>equals()</code> methods
     * for a {@link TypeDeclaration}. Depending on the type's fields the methods differ from each
     * other.
     * 
     * @param type
     *            the {@link TypeDeclaration} to create <code>toString()</code>,
     *            <code>hashCode()</code> and <code>equals()</code> for.
     * 
     * @throws JavaTemplateException
     */
    public void createAllObjectMethods(TypeDeclaration type) throws JavaTemplateException;
}
