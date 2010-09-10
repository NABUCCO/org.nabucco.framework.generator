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
package org.nabucco.framework.generator.compiler.transformation.java.common.clone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaCloneVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaCloneVisitor extends NabuccoToJavaVisitorSupport implements
        CollectionConstants {

    private static final String CLONE = "clone";

    private static final String CLONE_OBJECT_METHOD = CLONE + "Object";

    private static final String CLONE_COLLECTION_METHOD = CLONE + "Collection";

    private static final JavaAstMethodSignature CLONE_OBJECT = new JavaAstMethodSignature(
            CLONE_OBJECT_METHOD);

    private static final JavaAstMethodSignature CLONE_OBJECT_PARAM = new JavaAstMethodSignature(
            CLONE_OBJECT_METHOD, "Template");

    /** Collected method statements for cloneObject() */
    private List<Statement> cloneStatements = new ArrayList<Statement>();

    /** Type containing the cloneObject() method */
    private TypeDeclaration javaType;

    private JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

    /**
     * Creates a new {@link NabuccoToJavaCloneVisitor} instance.
     * 
     * @param javaType
     *            the java javaType
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaCloneVisitor(TypeDeclaration type,
            NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
        this.javaType = type;
    }

    @Override
    public void visit(DatatypeStatement nabuccoDatatype, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoDatatype, target);

        String name = nabuccoDatatype.nodeToken2.tokenImage;
        boolean isAbstract = nabuccoDatatype.nodeOptional.present();

        this.modifyCloneObjectMethod(name, isAbstract);
    }

    @Override
    public void visit(MessageStatement nabuccoMessage, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoMessage, target);

        String name = nabuccoMessage.nodeToken2.tokenImage;

        this.modifyCloneObjectMethod(name, false);
    }

    /**
     * Modifies the cloneObject() method of datatypes and messages.
     * 
     * @param name
     *            name of the statement
     * @param isAbstract
     *            abstract types cannot be cloned
     */
    private void modifyCloneObjectMethod(String name, boolean isAbstract) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        try {
            MethodDeclaration cloneObject = (MethodDeclaration) javaFactory.getJavaAstType()
                    .getMethod(javaType, CLONE_OBJECT);

            MethodDeclaration cloneObjectParam = (MethodDeclaration) javaFactory.getJavaAstType()
                    .getMethod(javaType, CLONE_OBJECT_PARAM);

            TypeReference type = producer.createTypeReference(name, false);

            // Abstract classes cannot be cloned directly!
            if (isAbstract) {

                javaFactory.getJavaAstMethod().addModifier(cloneObject,
                        ClassFileConstants.AccAbstract);
                cloneObject.statements = null;

            } else {
                LocalDeclaration local = (LocalDeclaration) cloneObject.statements[0];
                AllocationExpression allocation = (AllocationExpression) local.initialization;
                local.type = type;
                allocation.type = type;
            }

            javaFactory.getJavaAstMethod().setReturnType(cloneObject, type);

            Argument argument = cloneObjectParam.arguments[0];
            javaFactory.getJavaAstArgument().setType(argument, type);

            for (Statement statement : this.cloneStatements) {
                javaFactory.getJavaAstMethod().addStatement(cloneObjectParam, statement);
            }

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Cannot find cloneObject() method.", e);
        }
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBasetype, MdaModel<JavaModel> argu) {

        String name = nabuccoBasetype.nodeToken3.tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoBasetype.nodeToken2.tokenImage);

        try {
            if (!multiplicity.isMultiple()) {
                this.addAssignment(name);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating cloneObject() assignment.", e);
        }
    }

    @Override
    public void visit(EnumerationDeclaration nabuccoEnum, MdaModel<JavaModel> argu) {

        String name = nabuccoEnum.nodeToken2.tokenImage;

        try {
            QualifiedNameReference left = producer.createQualifiedNameReference("clone", name);
            FieldReference right = producer.createFieldThisReference(name);

            Assignment assignment = producer.createAssignment(left, right);

            cloneStatements.add(assignment);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating cloneObject() assignment.", e);
        }
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {

        String name = nabuccoDatatype.nodeToken2.tokenImage;
        String type = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoDatatype.nodeToken1.tokenImage);

        try {
            if (multiplicity.isMultiple()) {
                this.addListAssignment(name, type);
            } else {
                this.addAssignment(name);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating cloneObject() assignment.", e);
        }
    }

    /**
     * Adds an assignment to the collected list.
     * 
     * @param fieldName
     *            the field to create the assignment for.
     * 
     * @throws JavaModelException
     */
    private void addAssignment(String fieldName) throws JavaModelException {

        FieldReference field = producer.createFieldThisReference(fieldName);
        QualifiedNameReference clone = producer.createQualifiedNameReference(CLONE, fieldName);

        Literal literal = producer.createLiteral(null, LiteralType.NULL_LITERAL);
        BinaryExpression condition = producer.createBinaryExpression(
                BinaryExpressionType.EQUAL_EXPRESSION, field, literal, BinaryExpression.NOT_EQUAL);

        MessageSend right = producer.createMessageSend(CLONE_OBJECT_METHOD, field, null);
        Assignment assignment = producer.createAssignment(clone, right);

        Block then = producer.createBlock(assignment);
        IfStatement ifStatement = producer.createIfStatement(condition, then);

        this.cloneStatements.add(ifStatement);
    }

    /**
     * Adds an assignment to the collected list.
     * 
     * @param fieldName
     *            the field to create the assignment for.
     * 
     * @throws JavaModelException
     */
    private void addListAssignment(String fieldName, String type) throws JavaModelException {

        // Common
        FieldReference field = producer.createFieldThisReference(fieldName);
        QualifiedNameReference clone = producer.createQualifiedNameReference(CLONE, fieldName);

        TypeReference wildcard = producer.createWildcard();
        TypeReference list = producer.createParameterizedTypeReference(NABUCCO_LIST, false, Arrays
                .asList(wildcard));

        TypeReference typeRef = producer.createTypeReference(type, false);
        TypeReference listType = producer.createParameterizedTypeReference(NABUCCO_LIST, false,
                Arrays.asList(typeRef));

        // Condition
        InstanceOfExpression condition = producer.createInstanceOfExpression(field, list);

        // List Assignment
        CastExpression cast = producer.createCastExpression(field, listType);
        MessageSend right = producer.createMessageSend(CLONE_COLLECTION_METHOD, cast, null);
        Assignment assignment = producer.createAssignment(clone, right);

        // Then
        Block then = producer.createBlock(assignment);

        IfStatement listAssignment = producer.createIfStatement(condition, then);

        this.cloneStatements.add(listAssignment);
    }
}
