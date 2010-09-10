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
package org.nabucco.framework.generator.compiler.transformation.java.common.ast.container;

/**
 * JavaAstType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum JavaAstType {

    /**
     * Declares a package declaration.
     */
    PACKAGE,

    /**
     * Declares a super class reference.
     */
    SUPER_CLASS,

    /**
     * Declares an interface reference.
     */
    INTERFACE,

    /**
     * Declares a field declaration.
     */
    FIELD,

    /**
     * Declares a method declaration.
     */
    METHOD,

    /**
     * Declares a statement within a method.
     */
    METHOD_STATEMENT,

    /**
     * Declares an import statement
     */
    IMPORT,

    /**
     * Declares a type reference
     */
    TYPE_REFERENCE
}
