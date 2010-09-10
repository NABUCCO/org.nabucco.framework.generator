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

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;


/**
 * NoCollectionObjectMethodStrategy
 * <p/>
 * Object Method Strategy without creating collection statements.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NoCollectionObjectMethodStrategy extends DefaultObjectMethodStrategy implements
        ObjectMethodStrategy, CollectionConstants {

    @Override
    boolean isValid(FieldDeclaration field) {
        return super.isValid(field) && !this.isCollection(field);
    }

    /**
     * Checks whether a field is of type List, Set, Map or not.
     * 
     * @param field
     *            the field declaration to check
     * 
     * @return <b>true</b> if a field is a collection, <b>false</b> if not
     */
    private boolean isCollection(FieldDeclaration field) {

        if (Arrays.equals(field.type.getLastToken(), LIST.toCharArray())) {
            return true;
        }
        if (Arrays.equals(field.type.getLastToken(), SET.toCharArray())) {
            return true;
        }
        if (Arrays.equals(field.type.getLastToken(), MAP.toCharArray())) {
            return true;
        }

        return false;
    }
    
}
