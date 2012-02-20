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
package org.nabucco.framework.generator.compiler.transformation.common.counter;

import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.visitor.DepthFirstVisitor;

/**
 * ServiceOperationCounter
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ServiceOperationCounter extends DepthFirstVisitor {

    private int count = 0;

    @Override
    public void visit(MethodDeclaration method) {
        this.count++;
    }

    /**
     * Getter for the count.
     * 
     * @return Returns the count.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Returns whether the visitor has visited any method declaration or not.
     * 
     * @return <b>true</b> if no operations were found
     */
    public boolean isEmpty() {
        return this.count < 1;
    }

}
