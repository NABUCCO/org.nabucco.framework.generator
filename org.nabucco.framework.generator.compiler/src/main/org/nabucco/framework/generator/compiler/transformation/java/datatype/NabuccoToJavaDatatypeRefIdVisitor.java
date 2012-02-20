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
package org.nabucco.framework.generator.compiler.transformation.java.datatype;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierTypeMapper;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaDatatypeRefIdVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaDatatypeRefIdVisitor extends NabuccoToJavaVisitorSupport {

    /**
     * Creates a new {@link NabuccoToJavaDatatypeRefIdVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaDatatypeRefIdVisitor(NabuccoToJavaVisitorContext context) {
        super(createContext(context));
    }

    /**
     * Prepare the visitor context for the current traversion.
     * 
     * @param context
     *            the existing context
     * 
     * @return the new context
     */
    private static NabuccoToJavaVisitorContext createContext(NabuccoToJavaVisitorContext context) {
        context = new NabuccoToJavaVisitorContext(context);
        context.setNabuccoExtension(null);
        return context;
    }

    @Override
    public void visit(DatatypeStatement nabuccoDatatype, MdaModel<JavaModel> target) {
        super.visit(nabuccoDatatype, target);

        this.createParentRefIds(target);
    }

    /**
     * Create the reference IDs for the datatypes parent datatypes.
     * 
     * @param target
     *            the java target
     */
    private void createParentRefIds(MdaModel<JavaModel> target) {
        NabuccoModel parent = super.getParent();

        if (parent != null) {
            super.getVisitorContext().setNabuccoExtension(null);
            parent.getUnit().accept(this, target);
        }
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {

        // Transient fields must not have a ref id.
        if (nabuccoDatatype.nodeOptional.present()) {
            return;
        }

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                nabuccoDatatype.nodeToken1.tokenImage);

        if (!multiplicity.isMultiple()) {
            this.createRefId(nabuccoDatatype);
            this.createOverwritingSetter(nabuccoDatatype);
        }
    }

    /**
     * Checks whether a type is of another component and creates a reference ID.
     * 
     * @param nabuccoDatatype
     *            the datatype declaration
     * 
     */
    private void createRefId(DatatypeDeclaration nabuccoDatatype) {
        String name = nabuccoDatatype.nodeToken2.tokenImage;

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(REF_ID_TYPE, name + REF_ID,
                NabuccoModifierType.PRIVATE);
        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field.getAstNode());
        JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field.getAstNode());

        this.getVisitorContext().getContainerList().add(field);
        this.getVisitorContext().getContainerList().add(getter);
        this.getVisitorContext().getContainerList().add(setter);
    }

    /**
     * Creates the overwriting setter which is setting the ref ID appropriate to the setters ID.
     * 
     * @param nabuccoDatatype
     *            the datatype declaration
     */
    private void createOverwritingSetter(DatatypeDeclaration nabuccoDatatype) {

        String type = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        String name = nabuccoDatatype.nodeToken2.tokenImage;

        NabuccoModifierType modifier = NabuccoModifierTypeMapper.getInstance().mapToModifier(
                ((NodeToken) nabuccoDatatype.nodeChoice.choice).tokenImage);

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name, modifier);

        JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field.getAstNode());

        setter.getImports().add(super.resolveImport(type));

        this.getVisitorContext().getContainerList().add(setter);

        try {

            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            MethodDeclaration method = setter.getAstNode();
            String setterName = javaFactory.getJavaAstMethod().getMethodName(method);
            SuperReference superReference = producer.createSuperReference();

            List<Expression> arguments = new ArrayList<Expression>();
            for (Argument argument : method.arguments) {
                arguments.add(producer.createSingleNameReference(javaFactory.getJavaAstArgument().getName(argument)));
            }

            method.statements = new Statement[] { producer.createMessageSend(setterName, superReference, arguments) };

            NabuccoToJavaDatatypeVisitorSupport.prepareSetterForRefId(setter);

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error creating Reference ID for " + name + ".", me);
        }
    }

    /**
     * Getter for the field and method container.
     * 
     * @return the list of container
     */
    List<JavaAstContainter<? extends ASTNode>> getContainerList() {
        return this.getVisitorContext().getContainerList();
    }

}
