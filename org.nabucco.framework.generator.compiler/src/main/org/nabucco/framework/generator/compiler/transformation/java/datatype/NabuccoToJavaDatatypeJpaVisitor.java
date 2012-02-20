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

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.FetchStrategyType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.OrderStrategyType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaDatatypeJpaVisitor
 * <p/>
 * Visits datatype declarations to create SUFFIX_JPA persistence provider specific getter and setter
 * according to Java Beans Conventions.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaDatatypeJpaVisitor extends NabuccoToJavaVisitorSupport implements CollectionConstants {

    private JavaCompilationUnit unit;

    /**
     * Creates a new {@link NabuccoToJavaDatatypeJpaVisitor} instance.
     * 
     * @param context
     *            the visitor context
     * @param unit
     *            the Java compilation unit
     */
    public NabuccoToJavaDatatypeJpaVisitor(NabuccoToJavaVisitorContext context, JavaCompilationUnit unit) {
        super(context);

        this.unit = unit;
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {
        super.visit(nabuccoDatatype, target);

        // Transient Datatypes must not have JPA getters/setter!
        if (nabuccoDatatype.nodeOptional.present()) {
            return;
        }

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                nabuccoDatatype.nodeToken1.tokenImage);

        // Only multiplicities larget 1 must be defined!
        if (!multiplicity.isMultiple()) {
            return;
        }

        String name = nabuccoDatatype.nodeToken2.tokenImage;
        String type = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;

        FetchStrategyType fetchStrategy = this.getFetchStrategy(nabuccoDatatype);
        OrderStrategyType orderStrategy = this.getOrderStrategy(nabuccoDatatype);

        // Persistence of maps is currently not supported!
        if (orderStrategy == OrderStrategyType.MAPPED) {
            return;
        }

        try {
            MethodDeclaration getter = this.createGetter(type, name, fetchStrategy, orderStrategy);
            MethodDeclaration setter = this.createSetter(type, name, fetchStrategy, orderStrategy);

            TypeDeclaration datatypeClass = this.unit.getType();

            JavaAstElementFactory.getInstance().getJavaAstType().addMethod(datatypeClass, getter);
            JavaAstElementFactory.getInstance().getJavaAstType().addMethod(datatypeClass, setter);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating JPA Getter/Setter methods.", e);
        }

    }

    /**
     * Extract the fetch strategy of the datatype declaration annotations.
     * 
     * @param nabuccoDatatype
     *            the datatype declaration
     * @return the fetch strategy
     */
    private FetchStrategyType getFetchStrategy(DatatypeDeclaration nabuccoDatatype) {
        NabuccoAnnotation fetchStrategy = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoDatatype.annotationDeclaration, NabuccoAnnotationType.FETCH_STRATEGY);

        FetchStrategyType fetchStrategyType;
        if (fetchStrategy != null) {
            fetchStrategyType = FetchStrategyType.getType(fetchStrategy.getValue());
        } else {
            fetchStrategyType = FetchStrategyType.LAZY;
        }
        return fetchStrategyType;
    }

    /**
     * Extract the order strategy of the datatype declaration annotations.
     * 
     * @param nabuccoDatatype
     *            the datatype declaration
     * @return the order strategy
     */
    private OrderStrategyType getOrderStrategy(DatatypeDeclaration nabuccoDatatype) {
        NabuccoAnnotation orderStrategy = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoDatatype.annotationDeclaration, NabuccoAnnotationType.ORDER_STRATEGY);

        OrderStrategyType orderStrategyType;
        if (orderStrategy != null) {
            orderStrategyType = OrderStrategyType.getType(orderStrategy.getValue());
        } else {
            orderStrategyType = OrderStrategyType.ORDERED;
        }
        return orderStrategyType;
    }

    /**
     * Create the SUFFIX_JPA getter method.
     * 
     * @param type
     *            the datatype type
     * @param name
     *            the datatype name
     * @param fetchStrategy
     *            the fetch strategy
     * @param orderStrategy
     *            the order strategy
     * 
     * @return the getter method
     * 
     * @throws JavaModelException
     */
    private MethodDeclaration createGetter(String type, String name, FetchStrategyType fetchStrategy,
            OrderStrategyType orderStrategy) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String originalGetterName = PREFIX_GETTER + NabuccoTransformationUtility.firstToUpper(name);
        String getterName = originalGetterName + SUFFIX_JPA;

        MethodDeclaration getter = producer.createMethodDeclaration(getterName,
                this.unit.getUnitDeclaration().compilationResult, false);

        javaFactory.getJavaAstMethod().setModifier(getter, ClassFileConstants.AccDefault);

        TypeReference nbcCollection = null;
        TypeReference javaCollection = null;
        TypeReference typeReference = producer.createTypeReference(type, false);

        switch (orderStrategy) {

        case ORDERED:
            nbcCollection = producer.createParameterizedTypeReference(NABUCCO_LIST_IMPL, false,
                    Arrays.asList(typeReference));
            javaCollection = producer.createParameterizedTypeReference(LIST, false, Arrays.asList(typeReference));
            break;

        case UNORDERED:
            nbcCollection = producer.createParameterizedTypeReference(NABUCCO_SET_IMPL, false,
                    Arrays.asList(typeReference));
            javaCollection = producer.createParameterizedTypeReference(SET, false, Arrays.asList(typeReference));
            break;

        case MAPPED:
            throw new IllegalStateException("Persistence of maps is not supported!");
        }

        javaFactory.getJavaAstMethod().setReturnType(getter, javaCollection);

        FieldReference field = producer.createFieldThisReference(name);
        CastExpression castExpression = producer.createCastExpression(field, nbcCollection);
        MessageSend getDelegate = producer.createMessageSend(GET_DELEGATE, castExpression, null);
        ReturnStatement returnStatement = producer.createReturnStatement(getDelegate);

        IfStatement listInitialization = this.createCollectionInitialization(type, name, fetchStrategy, orderStrategy);

        getter.statements = new Statement[] { listInitialization, returnStatement };

        return getter;
    }

    /**
     * Create the SUFFIX_JPA setter method.
     * 
     * @param type
     *            the datatype type
     * @param name
     *            the datatype name
     * @param fetchStrategy
     *            the fetch strategy
     * @param orderStrategy
     *            the order strategy
     * 
     * @return the setter method
     * 
     * @throws JavaModelException
     */
    private MethodDeclaration createSetter(String type, String name, FetchStrategyType fetchStrategy,
            OrderStrategyType orderStrategy) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String setterName = PREFIX_SETTER + NabuccoTransformationUtility.firstToUpper(name) + SUFFIX_JPA;

        MethodDeclaration setter = producer.createMethodDeclaration(setterName,
                this.unit.getUnitDeclaration().compilationResult, false);

        javaFactory.getJavaAstMethod().setModifier(setter, ClassFileConstants.AccDefault);

        TypeReference nbcCollection = null;
        TypeReference javaCollection = null;
        TypeReference typeReference = producer.createTypeReference(type, false);

        switch (orderStrategy) {

        case ORDERED:
            nbcCollection = producer.createParameterizedTypeReference(NABUCCO_LIST_IMPL, false,
                    Arrays.asList(typeReference));
            javaCollection = producer.createParameterizedTypeReference(LIST, false, Arrays.asList(typeReference));
            break;

        case UNORDERED:
            nbcCollection = producer.createParameterizedTypeReference(NABUCCO_SET_IMPL, false,
                    Arrays.asList(typeReference));
            javaCollection = producer.createParameterizedTypeReference(SET, false, Arrays.asList(typeReference));
            break;

        case MAPPED:
            throw new IllegalStateException("Persistence of maps is not supported!");
        }

        Argument argument = producer.createArgument(name, javaCollection);
        javaFactory.getJavaAstMethod().addArgument(setter, argument);

        FieldReference field = producer.createFieldThisReference(name);

        CastExpression castExpression = producer.createCastExpression(field, nbcCollection);
        SingleNameReference parameter = producer.createSingleNameReference(name);
        MessageSend setDelegate = producer.createMessageSend(SET_DELEGATE, castExpression, Arrays.asList(parameter));

        IfStatement listInitialization = this.createCollectionInitialization(type, name, fetchStrategy, orderStrategy);

        setter.statements = new Statement[] { listInitialization, setDelegate };

        return setter;
    }

    /**
     * Create the collection initializer for the nabucco collection.
     * 
     * @param type
     *            the datatype type
     * @param name
     *            the list name
     * @param fetchStrategy
     *            the fetch strategy
     * 
     * @return the new if statement
     * 
     * @throws JavaModelException
     */
    private IfStatement createCollectionInitialization(String type, String name, FetchStrategyType fetchStrategy,
            OrderStrategyType orderStrategy) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        FieldReference field = producer.createFieldThisReference(name);

        TypeReference typeReference = producer.createTypeReference(type, false);

        String collectionType = null;

        switch (orderStrategy) {
        case ORDERED:
            collectionType = NABUCCO_LIST_IMPL;
            break;
        case UNORDERED:
            collectionType = NABUCCO_SET_IMPL;
            break;
        case MAPPED:
            collectionType = NABUCCO_MAP_IMPL;
            break;
        }

        TypeReference nbcCollection = producer.createParameterizedTypeReference(collectionType, false,
                Arrays.asList(typeReference));

        // Condition
        Literal nullLiteral = producer.createLiteral(null, LiteralType.NULL_LITERAL);
        BinaryExpression condition = producer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION, field,
                nullLiteral, BinaryExpression.EQUAL_EQUAL);

        // Then
        QualifiedNameReference collectionState = producer.createQualifiedNameReference(COLLECTION_STATE,
                fetchStrategy.getId());

        AllocationExpression listAllocation = producer.createAllocationExpression(nbcCollection,
                Arrays.asList(collectionState));

        Assignment listAssignment = producer.createAssignment(field, listAllocation);
        Block thenStatement = producer.createBlock(listAssignment);

        return producer.createIfStatement(condition, thenStatement);
    }

}
