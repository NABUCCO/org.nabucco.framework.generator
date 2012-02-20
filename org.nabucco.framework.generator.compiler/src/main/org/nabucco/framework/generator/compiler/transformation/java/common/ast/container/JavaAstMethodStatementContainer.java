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
package org.nabucco.framework.generator.compiler.transformation.java.common.ast.container;

import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;

/**
 * JavaAstMethodStatementContainer
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class JavaAstMethodStatementContainer<S extends Statement> extends JavaAstContainter<S> {

    private JavaAstMethodSignature signature;

    private boolean isConstructor;

    /**
     * Creates a new {@link JavaAstMethodStatementContainer} instance for a non-constructor method.
     * 
     * @param statement
     *            the statement defined by the type.
     * @param signature
     *            the method signature to add the statement
     */
    public JavaAstMethodStatementContainer(S statement, JavaAstMethodSignature signature) {
        this(statement, signature, false);
    }

    /**
     * Creates a new {@link JavaAstMethodStatementContainer} instance.
     * 
     * @param statement
     *            the statement defined by the type.
     * @param signature
     *            the method signature to add the statement
     * @param whether
     *            the method is a constructor or not
     */
    public JavaAstMethodStatementContainer(S statement, JavaAstMethodSignature signature, boolean isConstructor) {
        super(statement, JavaAstType.METHOD_STATEMENT);
        this.signature = signature;
    }

    /**
     * Getter for the method signature.
     * 
     * @return Returns the signature.
     */
    public JavaAstMethodSignature getSignature() {
        return this.signature;
    }

    /**
     * Checks whether the method is a constructor or not.
     * 
     * @return <b>true</b> if it is a constructor, <b>false</b> if not
     */
    public boolean isConstructor() {
        return this.isConstructor;
    }

}
