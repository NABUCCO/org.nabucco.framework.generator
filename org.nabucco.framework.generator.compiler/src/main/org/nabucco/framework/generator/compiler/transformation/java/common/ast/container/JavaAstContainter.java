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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;

/**
 * JavaAstContainter
 * <p/>
 * Container for java {@link ASTNode} elements and related {@link ImportReference}.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class JavaAstContainter<A extends ASTNode> {

    private A astNode;

    private JavaAstType type;

    private Set<String> importList;

    /**
     * Creates a new {@link JavaAstContainter} instance for a given {@link JavaAstType}.
     * 
     * @param astNode
     *            the ast node defined by the type.
     * @param type
     *            type of the container
     */
    public JavaAstContainter(A astNode, JavaAstType type) {
        this.astNode = astNode;
        this.type = type;
    }

    /**
     * Getter for the current java AST node.
     * 
     * @return Returns the astNode.
     */
    public A getAstNode() {
        return this.astNode;
    }

    /**
     * Getter for the current java AST type.
     * 
     * @return Returns the type.
     */
    public JavaAstType getType() {
        return this.type;
    }

    /**
     * Getter for the import list. Necessary for this {@link ASTNode}.
     * 
     * @return Returns the importList.
     */
    public Set<String> getImports() {
        if (this.importList == null) {
            this.importList = new HashSet<String>();
        }
        return this.importList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.type);
        builder.append(" Container:\n\n");
        builder.append("Code:\n");
        if (this.astNode != null) {
            builder.append(this.astNode.print(2, new StringBuffer()));
        } else {
            builder.append("None");
        }
        
        builder.append("\n\nImports:\n");

        for (String javaImport : this.getImports()) {
            builder.append(" - ");
            builder.append(javaImport);
        }
        
        return builder.toString();
    }

}
