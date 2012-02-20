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
package org.nabucco.framework.generator.compiler.transformation.java.basetype;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaBasetypeCloneVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaBasetypeCloneVisitor extends NabuccoToJavaVisitorSupport {

    private static final String CLONE_METHOD = "cloneObject";

    private static final JavaAstMethodSignature CLONE_OBJECT = new JavaAstMethodSignature(CLONE_METHOD);

    private TypeDeclaration type;

    /**
     * Creates a new {@link NabuccoToJavaBasetypeCloneVisitor} instance
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaBasetypeCloneVisitor(TypeDeclaration type, NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
        this.type = type;
    }

    @Override
    public void visit(BasetypeStatement nabuccoDatatype, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoDatatype, target);

        String name = nabuccoDatatype.nodeToken2.tokenImage;

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        try {
            MethodDeclaration cloneObject = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                    CLONE_OBJECT);

            TypeReference datatype = producer.createTypeReference(name, false);

            javaFactory.getJavaAstMethod().setReturnType(cloneObject, datatype);

            LocalDeclaration local = (LocalDeclaration) cloneObject.statements[0];
            AllocationExpression allocation = (AllocationExpression) local.initialization;
            local.type = datatype;
            allocation.type = datatype;

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Cannot find cloneObject() method.", e);
        }

    }

}
