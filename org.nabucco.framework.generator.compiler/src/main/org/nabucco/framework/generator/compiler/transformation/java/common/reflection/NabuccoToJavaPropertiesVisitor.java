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
package org.nabucco.framework.generator.compiler.transformation.java.common.reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.AssociationStrategyType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.OrderStrategyType;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionImplementationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.constants.JavaConstants;
import org.nabucco.framework.generator.compiler.transformation.java.datatype.CodePathSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.compiler.visitor.util.NabuccoPropertyKey;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.visitor.DepthFirstVisitor;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethodCall;
import org.nabucco.framework.mda.model.java.ast.JavaAstType;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.BinaryExpressionType;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaPropertiesVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaPropertiesVisitor extends DepthFirstVisitor {

    /* Constants */

    private static final String DESCRIPTOR_SUPPORT = "org.nabucco.framework.base.facade.datatype.property.PropertyDescriptorSupport";

    private static final String ASSOCIATION_TYPE = "PropertyAssociationType";

    private static final String METHOD_GET_TYPE = "getType";

    private static final String METHOD_GET_PROPERTY_DESCRIPTOR = "getPropertyDescriptor";

    private static final String METHOD_GET_NAME = "getName";

    private static final String METHOD_CREATE = "create";

    private static final String METHOD_CREATE_PROPERTY = METHOD_CREATE + "Property";

    private static final String METHOD_CREATE_BASETYPE = METHOD_CREATE + "Basetype";

    private static final String METHOD_CREATE_ENUMERATION = METHOD_CREATE + "Enumeration";

    private static final String METHOD_CREATE_DATATYPE = METHOD_CREATE + "Datatype";

    private static final String METHOD_CREATE_COLLECTION = METHOD_CREATE + "Collection";

    private static final String METHOD_ADD = "add";

    private static final String METHOD_PUT = "put";

    private static final String VARIABLE_PROPERTY_MAP = "propertyMap";

    private static final String VARIABLE_PROPERTIES = "properties";

    private static final String VARIABLE_PROPERTY = "property";

    private static final String FIELD_CONSTRAINTS = "PROPERTY_CONSTRAINTS";

    private static final String CLASS_SUPPORT = "PropertyDescriptorSupport";

    private static final JavaAstMethodSignature SIGNATURE_CREATEPROPERTYCONTAINER = new JavaAstMethodSignature(
            "createPropertyContainer");

    private static final JavaAstMethodSignature SIGNATURE_SETPROPERTY = new JavaAstMethodSignature("setProperty",
            new String[] { "NabuccoProperty" });

    /* Instance Variables */

    private String qualifiedName;

    private Set<String> imports;

    private SingleNameReference propertyList;

    private IfStatement setPropertyStatementRoot;

    private List<MessageSend> getPropertyStatements = new ArrayList<MessageSend>();

    private Set<PropertyType> propertyTypes = new HashSet<PropertyType>();

    private List<SingleNameReference> propertyNames = new ArrayList<SingleNameReference>();

    private List<FieldDeclaration> propertyNameFields = new ArrayList<FieldDeclaration>();

    private List<Statement> propertyContainerStatements = new ArrayList<Statement>();

    private String currentTypeName;

    private String extention;

    private CollectionImplementationType collectionImplementation;

    private Map<NabuccoPropertyKey, Node> parentProperties;

    /**
     * Creates a new {@link NabuccoToJavaPropertiesVisitor} instance.
     * 
     * @param qualifiedName
     *            the qualified nabucco type name to create properties for
     * @param imports
     *            the nabucco imports of the nabucco type
     * @param nabuccoExtension
     *            the nabucco extentsion definition if there is any, <code>null</code> otherwise
     * @param properties
     *            the parent properties
     */
    public NabuccoToJavaPropertiesVisitor(String qualifiedName, Set<String> imports, String nabuccoExtension,
            Map<NabuccoPropertyKey, Node> properties, CollectionImplementationType collectionImplementation) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("Cannot create NabuccoToJavaPropertiesVisitor for name [null].");
        }

        this.qualifiedName = qualifiedName;
        this.imports = imports;
        this.extention = nabuccoExtension;
        this.collectionImplementation = collectionImplementation;
        this.parentProperties = properties;
    }

    @Override
    public void visit(DatatypeStatement datatype) {
        this.currentTypeName = datatype.nodeToken2.tokenImage;
        super.visit(datatype);
    }

    @Override
    public void visit(MessageStatement message) {
        this.currentTypeName = message.nodeToken2.tokenImage;
        super.visit(message);
    }

    @Override
    public void visit(BasetypeDeclaration basetype) {

        if (isRedefinition(basetype)) {
            return;
        }

        String name = basetype.nodeToken3.tokenImage;
        String type = basetype.nodeToken1.tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                basetype.nodeToken2.tokenImage);

        boolean isTechnical = this.isTechnical(basetype.annotationDeclaration);
        boolean isTransient = basetype.nodeOptional.present();

        try {
            if (multiplicity.isMultiple()) {
                OrderStrategyType ordered = this.getOrderStrategy(basetype.annotationDeclaration);
                this.createCollectionProperty(name, type, null, ordered, isTechnical, isTransient);
            } else {
                this.createBasetypeProperty(name, type, isTechnical, isTransient);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating datatype reflection.", e);
        }
    }

    @Override
    public void visit(EnumerationDeclaration enumeration) {

        if (isRedefinition(enumeration)) {
            return;
        }

        String name = enumeration.nodeToken2.tokenImage;
        String type = ((NodeToken) enumeration.nodeChoice1.choice).tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                enumeration.nodeToken1.tokenImage);

        boolean isTechnical = this.isTechnical(enumeration.annotationDeclaration);
        boolean isTransient = enumeration.nodeOptional.present();

        try {
            if (multiplicity.isMultiple()) {
                OrderStrategyType ordered = this.getOrderStrategy(enumeration.annotationDeclaration);
                this.createCollectionProperty(name, type, null, ordered, isTechnical, isTransient);
            } else {
                this.createEnumProperty(name, type, isTechnical, isTransient);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating datatype reflection.", e);
        }
    }

    @Override
    public void visit(DatatypeDeclaration datatype) {

        String name = datatype.nodeToken2.tokenImage;
        String type = ((NodeToken) datatype.nodeChoice1.choice).tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                datatype.nodeToken1.tokenImage);

        AssociationStrategyType strategy = this.getAssociationStrategy(datatype.annotationDeclaration);

        boolean isTransient = datatype.nodeOptional.present();
        boolean isTechnical = this.isTechnical(datatype.annotationDeclaration);
        boolean isCode = this.isCode(datatype.annotationDeclaration);

        try {
            if (multiplicity.isMultiple()) {
                OrderStrategyType ordered = this.getOrderStrategy(datatype.annotationDeclaration);
                this.createCollectionProperty(name, type, strategy, ordered, isTechnical, isTransient);
            } else {
                this.createDatatypeProperty(name, type, strategy, isTechnical, isTransient, isCode);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating datatype reflection.", e);
        }
    }

    /**
     * Extracts the association strategy type of the given annotations.
     * 
     * @param annotations
     *            the datatype annotations
     * 
     * @return the association strategy
     */
    private AssociationStrategyType getAssociationStrategy(AnnotationDeclaration annotations) {
        NabuccoAnnotation strategyAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations,
                NabuccoAnnotationType.ASSOCIATION_STRATEGY);

        AssociationStrategyType strategy = (strategyAnnotation != null) ? AssociationStrategyType
                .getType(strategyAnnotation.getValue()) : AssociationStrategyType.COMPOSITION;
        return strategy;
    }

    /**
     * Extracts the order strategy type of the given annotations.
     * 
     * @param annotations
     *            the datatype annotations
     * 
     * @return the order strategy
     */
    private OrderStrategyType getOrderStrategy(AnnotationDeclaration annotations) {
        NabuccoAnnotation orderedAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations,
                NabuccoAnnotationType.ORDER_STRATEGY);

        OrderStrategyType ordered = (orderedAnnotation != null) ? OrderStrategyType.getType(orderedAnnotation
                .getValue()) : OrderStrategyType.ORDERED;
        return ordered;
    }

    /**
     * Check whether a declaration is @Redefined or not.
     * 
     * @param node
     *            the AST node to check.
     * 
     * @return <b>true</b> if the node is redefined, <b>false</b> if not
     */
    private static boolean isRedefinition(Node node) {
        if (node instanceof BasetypeDeclaration) {
            BasetypeDeclaration basetype = (BasetypeDeclaration) node;
            if (NabuccoAnnotationMapper.getInstance().mapToAnnotation(basetype.annotationDeclaration,
                    NabuccoAnnotationType.REDEFINED) == null) {
                return false;
            }
            return true;
        } else if (node instanceof EnumerationDeclaration) {
            EnumerationDeclaration nabuccoEnum = (EnumerationDeclaration) node;
            if (NabuccoAnnotationMapper.getInstance().mapToAnnotation(nabuccoEnum.annotationDeclaration,
                    NabuccoAnnotationType.REDEFINED) == null) {
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * Check whether a declaration is @Technical or not.
     * 
     * @param annotations
     *            the annotation declaration
     * 
     * @return <b>true</b> if the declaration is technical, <b>false</b> if not
     */
    private boolean isTechnical(AnnotationDeclaration annotations) {
        NabuccoAnnotation technical = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations,
                NabuccoAnnotationType.TECHNICAL_PROPERTY);

        return technical != null;
    }

    /**
     * Check whether a code path is defined at the datatype declaration.
     * 
     * @param annotations
     *            the annotations to check
     * 
     * @return <b>true</b> if the declaration holds a code path, <b>false</b> if not
     */
    private boolean isCode(AnnotationDeclaration annotations) {
        NabuccoAnnotation codePath = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations,
                NabuccoAnnotationType.CODE_PATH);

        if (codePath == null) {
            return false;
        }

        return codePath.getValue() != null;
    }

    /**
     * Create the BasetypeProperty allocation.
     * 
     * @param name
     *            name of the property
     * @param type
     *            type of the property
     * @param isTechnical
     *            whether the property is technical or not
     * 
     * @throws JavaModelException
     */
    private void createBasetypeProperty(String name, String type, boolean isTechnical, boolean isTransient)
            throws JavaModelException {
        this.createProperty(name, type, PropertyType.BASETYPE, null, isTechnical, isTransient, null);
    }

    /**
     * Create the EnumProperty allocation.
     * 
     * @param name
     *            name of the property
     * @param type
     *            type of the property
     * @param isTechnical
     *            whether the property is technical or not
     * 
     * @throws JavaModelException
     */
    private void createEnumProperty(String name, String type, boolean isTechnical, boolean isTransient)
            throws JavaModelException {
        this.createProperty(name, type, PropertyType.ENUMERATION, null, isTechnical, isTransient, null);
    }

    /**
     * Create the DatatypeProperty allocation.
     * 
     * @param name
     *            name of the property
     * @param type
     *            type of the property
     * @param strategy
     *            the association strategy
     * @param isTechnical
     *            the flag indicating whether the property is technical or not
     * 
     * @throws JavaModelException
     */
    private void createDatatypeProperty(String name, String type, AssociationStrategyType strategy,
            boolean isTechnical, boolean isTransient, boolean isCode) throws JavaModelException {
        QualifiedNameReference associationType = this.createStrategyType(type, strategy);

        List<Expression> additionalArguments = new ArrayList<Expression>();
        additionalArguments.add(associationType);

        if (isCode) {
            additionalArguments.add(this.createCodePath(name));
        }

        this.createProperty(name, type, PropertyType.DATATYPE, null, isTechnical, isTransient, additionalArguments);
    }

    /**
     * Create the code path string literal.
     * 
     * @param constant
     *            the constant holding the code path
     * 
     * @return the java ast string literal
     * 
     * @throws JavaModelException
     *             when the literal cannot be created
     */
    private SingleNameReference createCodePath(String fieldName) throws JavaModelException {
        String constant = CodePathSupport.createCodePathConstant(fieldName);
        return JavaAstModelProducer.getInstance().createSingleNameReference(constant);
    }

    /**
     * Create the ListProperty allocation.
     * 
     * @param name
     *            name of the property
     * @param type
     *            type of the property
     * @param strategy
     *            the association strategy
     * @param orderStrategy
     *            the order strategy
     * @param isTechnical
     *            whether the property is technical or not
     * @param isTransient
     *            whether the property is transient or not
     * 
     * @throws JavaModelException
     *             when the creation fails
     */
    private void createCollectionProperty(String name, String type, AssociationStrategyType strategy,
            OrderStrategyType orderStrategy, boolean isTechnical, boolean isTransient) throws JavaModelException {

        QualifiedNameReference associationType = this.createStrategyType(type, strategy);

        this.createProperty(name, type, PropertyType.COLLECTION, orderStrategy, isTechnical, isTransient,
                Arrays.<Expression> asList(associationType));
    }

    /**
     * Create the strategy type for the related type and strategy annotation.
     * 
     * @param type
     *            field type
     * @param strategy
     *            the association strategy
     * 
     * @return the name reference for the given strategy type
     * 
     * @throws JavaModelException
     */
    private QualifiedNameReference createStrategyType(String type, AssociationStrategyType strategy)
            throws JavaModelException {

        String importString = NabuccoCompilerSupport.resolveImport(type, this.imports);
        if (NabuccoCompilerSupport.isOtherComponent(this.qualifiedName, importString)) {
            return JavaAstModelProducer.getInstance().createQualifiedNameReference(ASSOCIATION_TYPE,
                    AssociationStrategyType.COMPONENT.getId());
        }

        if (strategy != null) {
            return JavaAstModelProducer.getInstance().createQualifiedNameReference(ASSOCIATION_TYPE, strategy.getId());
        }

        return JavaAstModelProducer.getInstance().createQualifiedNameReference(ASSOCIATION_TYPE,
                AssociationStrategyType.COMPOSITION.getId());
    }

    /**
     * Create a single property allocation.
     * 
     * @param fieldName
     *            the field name
     * @param fieldType
     *            the field type
     * @param propertyType
     *            the property type
     * @param orderStrategy
     *            the order strategy
     * @param isTechnical
     *            whether the property is technical or not
     * @param additionalArguments
     *            additional constructor arguments
     * 
     * @return the 'add()' method call including the property allocation
     * 
     * @throws JavaModelException
     */
    private void createProperty(String fieldName, String fieldType, PropertyType propertyType,
            OrderStrategyType orderStrategy, boolean isTechnical, boolean isTransient,
            List<Expression> additionalArguments) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        if (this.propertyList == null) {
            this.propertyList = producer.createSingleNameReference(VARIABLE_PROPERTIES);
        }

        boolean isInherited = false;

        if (this.parentProperties != null) {
            for (NabuccoPropertyKey key : this.parentProperties.keySet()) {
                if (key.getName().equals(fieldName)) {
                    isInherited = true;
                }
            }
        }

        if (isInherited) {
            // Fragment for Method 'getProperties'
            MessageSend addProperty = this.createGetPropertyStatement(fieldName, fieldType, isTransient, propertyType);
            this.getPropertyStatements.add(addProperty);
            return;
        }

        /** STATIC PROPERTIES */

        // Create a static final field for each property to identifier the name
        String staticFieldName = fieldName.toUpperCase();
        FieldDeclaration field = JavaAstSupport.createField(JavaConstants.STRING, staticFieldName,
                NabuccoModifierType.PUBLIC).getAstNode();
        field.initialization = producer.createLiteral(fieldName, LiteralType.STRING_LITERAL);
        JavaAstElementFactory.getInstance().getJavaAstField()
                .addModifier(field, ClassFileConstants.AccFinal | ClassFileConstants.AccStatic);
        this.propertyNameFields.add(field);

        // Add name to constant array 'PROPERTY_NAME'
        this.propertyNames.add(producer.createSingleNameReference(staticFieldName));

        // create fragment for createPropertyContainer
        Statement statement = this.createPropertyContainerFragment(fieldName, fieldType, propertyType, isTechnical,
                additionalArguments);

        /** DYNAMIC PROPERTIES */

        // Fragment for Method 'getProperties'
        MessageSend addProperty = this.createGetPropertyStatement(fieldName, fieldType, isTransient, propertyType);

        // Fragment for Method 'setProperty'
        IfStatement ifStatement = this.createSetPropertyStatement(fieldName, fieldType, propertyType, orderStrategy);

        if (this.setPropertyStatementRoot == null) {
            this.setPropertyStatementRoot = ifStatement;
        } else {
            IfStatement previousIf = this.setPropertyStatementRoot;
            while (previousIf.elseStatement != null) {
                previousIf = (IfStatement) previousIf.elseStatement;
            }
            previousIf.elseStatement = ifStatement;
        }

        this.getPropertyStatements.add(addProperty);
        this.propertyTypes.add(propertyType);
        this.propertyContainerStatements.add(statement);
    }

    /**
     * Create an if statement for a single property in the setProperty() method.
     * 
     * @param fieldName
     *            the field name
     * @param fieldType
     *            the field type
     * @param propertyType
     *            the property type
     * @param orderStrategy
     *            the order strategy type
     * 
     * @return the if statement
     * 
     * @throws JavaModelException
     */
    private IfStatement createSetPropertyStatement(String fieldName, String fieldType, PropertyType propertyType,
            OrderStrategyType orderStrategy) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        // Common
        SingleNameReference property = producer.createSingleNameReference(VARIABLE_PROPERTY);
        TypeReference type = producer.createTypeReference(fieldType, false);

        // Call 'property.getInstance()'
        MessageSend getInstance = producer.createMessageSend(JavaConstants.SINGLETON_GETTER, property, null);

        // TYPE or List<TYPE>?
        TypeReference propertyTypeRef;
        if (propertyType == PropertyType.COLLECTION) {

            String collectionType = null;
            switch (orderStrategy) {

            case ORDERED:
                collectionType = this.collectionImplementation.getList();
                break;

            case UNORDERED:
                collectionType = this.collectionImplementation.getSet();
                break;

            case MAPPED:
                collectionType = this.collectionImplementation.getMap();
                break;
            }

            propertyTypeRef = producer.createParameterizedTypeReference(collectionType, false, Arrays.asList(type));
        } else {
            propertyTypeRef = type;
        }

        CastExpression cast = producer.createCastExpression(getInstance, propertyTypeRef);

        Expression assignment;

        // Alternative via field!
        if (propertyType == PropertyType.COLLECTION) {
            FieldReference field = producer.createFieldThisReference(fieldName);
            assignment = producer.createAssignment(field, cast);

            // Alternative via setter!
        } else {
            ThisReference thisReference = producer.createThisReference();
            assignment = producer.createMessageSend(NabuccoTransformationUtility.toSetter(fieldName), thisReference,
                    Arrays.asList(cast));
        }

        ReturnStatement returnStatement = producer.createReturnStatement(producer.createLiteral(null,
                LiteralType.TRUE_LITERAL));

        // property.getName().equals(STATIC_PROPERTY_NAME_FIELD)
        MessageSend getName = producer.createMessageSend(METHOD_GET_NAME, property, null);

        MessageSend equals = producer.createMessageSend(JavaConstants.EQUALS, getName,
                Arrays.asList(producer.createSingleNameReference(fieldName.toUpperCase())));

        // property.getType() == XYZ.class
        MessageSend getType = producer.createMessageSend(METHOD_GET_TYPE, property, null);
        ClassLiteralAccess typeClass = producer.createClassLiteralAccess(fieldType);

        BinaryExpression sameClass = producer.createBinaryExpression(BinaryExpressionType.EQUAL_EXPRESSION, getType,
                typeClass, EqualExpression.EQUAL_EQUAL);

        BinaryExpression outerCondition = producer.createBinaryExpression(BinaryExpressionType.AND_AND_EXPRESSION,
                equals, sameClass, EqualExpression.AND_AND);

        return producer.createIfStatement(outerCondition, producer.createBlock(assignment, returnStatement));
    }

    /**
     * Create a property allocation for the getProperties() method.
     * 
     * @param fieldName
     *            the field name
     * @param fieldType
     *            the field type
     * @param isTransient
     *            whether the field is transient or not
     * @param propertyType
     *            the property type
     * 
     * @return the statement for the <code>getProperties()</code> method
     * 
     * @throws JavaModelException
     *             when a part of the java statement cannot be created
     */
    private MessageSend createGetPropertyStatement(String fieldName, String fieldType, boolean isTransient,
            PropertyType propertyType) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        SingleNameReference staticField = producer.createSingleNameReference(fieldName.toUpperCase());

        SingleNameReference typeNameReference = producer.createSingleNameReference(this.currentTypeName);

        MessageSend getPropertyDescriptorCall = producer.createMessageSend(METHOD_GET_PROPERTY_DESCRIPTOR,
                typeNameReference, Arrays.asList(new Expression[] { staticField }));

        List<Expression> arguments = new ArrayList<Expression>();
        arguments.add(getPropertyDescriptorCall);

        if (propertyType == PropertyType.COLLECTION || propertyType == PropertyType.BASETYPE) {
            // Alternative via Field
            arguments.add(producer.createFieldThisReference(fieldName));
        } else {
            // Alternative via Getter
            String getterName = NabuccoTransformationUtility.toGetter(fieldName);
            arguments.add(producer.createMessageSend(getterName, producer.createThisReference(), null));
        }

        Expression refId = this.createRefId(fieldName, fieldType, isTransient, propertyType);
        if (refId != null) {
            arguments.add(refId);
        }

        MessageSend containerInit = producer.createMessageSend(METHOD_CREATE_PROPERTY, producer.createSuperReference(),
                arguments);

        return producer.createMessageSend(METHOD_ADD, this.propertyList, Arrays.asList(containerInit));
    }

    /**
     * Create the reference ID literal for the given relation.
     * 
     * @param name
     *            the field name
     * @param type
     *            the field type
     * @param isTransient
     *            whether the property is transient or not
     * @param propertyType
     *            the property type
     * 
     * @return the refId field or a null literal if it is no component relation
     * 
     * @throws JavaModelException
     */
    private Expression createRefId(String name, String type, boolean isTransient, PropertyType propertyType)
            throws JavaModelException {

        // Messages do not have a propertyList and must not have ref IDs.
        if (this.parentProperties == null) {
            return null;
        }

        if (isTransient || propertyType != PropertyType.DATATYPE) {
            return JavaAstModelProducer.getInstance().createLiteral(null, LiteralType.NULL_LITERAL);
        }

        String importString = NabuccoCompilerSupport.resolveImport(type, this.imports);
        if (NabuccoCompilerSupport.isOtherComponent(this.qualifiedName, importString)) {
            return JavaAstModelProducer.getInstance().createFieldThisReference(
                    name + NabuccoTransformationConstants.REF_ID);
        }

        return JavaAstModelProducer.getInstance().createLiteral(null, LiteralType.NULL_LITERAL);
    }

    /**
     * Adjust the getProperties() method.
     * 
     * @param fieldList
     *            list of field names
     * @param type
     *            the type to modify
     * 
     * @throws JavaModelException
     *             when an unexpected error during compilation unit modification occurs
     */
    public void finish(JavaCompilationUnit unit) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        this.createCreatePropertyContainer(unit);

        if (this.getPropertyStatements.isEmpty()) {
            JavaAstType typeFactory = javaFactory.getJavaAstType();

            FieldDeclaration constraints = typeFactory.getField(unit.getType(), FIELD_CONSTRAINTS);

            typeFactory.removeField(unit.getType(), constraints);

            MethodDeclaration setProperty = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(unit.getType(),
                    NabuccoToJavaReflectionFacade.SET_PROPERTY);

            typeFactory.removeMethod(unit.getType(), setProperty);

            return;
        }

        this.createGetProperties(unit);
        this.createSetProperty(unit);

        this.createImports(unit);

        if (this.propertyTypes.contains(PropertyType.COLLECTION)) {
            // add suppress warnings to setPropertiy()
            MethodDeclaration method = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(unit.getType(),
                    SIGNATURE_SETPROPERTY);
            Annotation suppressWarnings = JavaAstModelProducer.getInstance().createAnnotation(
                    JavaConstants.ANNOTATION_SUPPRESS_WARNINGS, "unchecked");
            javaFactory.getJavaAstMethod().addAnnotation(method, suppressWarnings);
        }

        this.createPropertyNameFields(unit.getType());

    }

    /**
     * Create the getProperties() method.
     * 
     * @param unit
     *            the java compilation unit
     * 
     * @throws JavaModelException
     *             when an unexpected error during compilation unit modification occurs
     */
    private void createGetProperties(JavaCompilationUnit unit) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        MethodDeclaration getProperties = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(unit.getType(),
                NabuccoToJavaReflectionFacade.GET_PROPERTIES);

        List<Statement> statements = new ArrayList<Statement>();
        statements.add(getProperties.statements[0]);
        statements.addAll(this.getPropertyStatements);
        statements.add(getProperties.statements[1]);

        getProperties.statements = statements.toArray(new Statement[statements.size()]);
    }

    /**
     * Create the getProperties() method.
     * 
     * @param unit
     *            the java compilation unit
     * 
     * @throws JavaModelException
     *             when an unexpected error during compilation unit modification occurs
     */
    private void createSetProperty(JavaCompilationUnit unit) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        MethodDeclaration setProperty = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(unit.getType(),
                NabuccoToJavaReflectionFacade.SET_PROPERTY);

        if (this.setPropertyStatementRoot != null) {

            List<Statement> statements = new ArrayList<Statement>();
            statements.add(setProperty.statements[0]);
            statements.add(this.setPropertyStatementRoot);
            statements.add(setProperty.statements[1]);

            setProperty.statements = statements.toArray(new Statement[statements.size()]);
        }
    }

    /**
     * Add the missing imports.
     * 
     * @param unit
     *            the java compilation unit
     * 
     * @throws JavaModelException
     */
    private void createImports(JavaCompilationUnit unit) throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        if (this.propertyTypes.contains(PropertyType.COLLECTION)) {
            for (String importName : PropertyType.COLLECTION.getImports()) {
                ImportReference importRef = producer.createImportReference(importName);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importRef);
            }
        }

        if (this.propertyTypes.contains(PropertyType.DATATYPE)) {
            for (String importName : PropertyType.DATATYPE.getImports()) {
                ImportReference importRef = producer.createImportReference(importName);
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importRef);
            }
        }

        // added dynamically since only needed when we have actual properties
        javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(),
                producer.createImportReference(DESCRIPTOR_SUPPORT));

    }

    /**
     * Modifies the template 'createPropertyContainer' method by <li>changing the parent class
     * literal or removing it if there is no usable parent</li> <li>add all put statements
     * previously created by the
     * {@link NabuccoToJavaPropertiesVisitor#createPropertyContainerFragment(String, String, PropertyType, List)}
     * method</li>
     * 
     * @param unit
     *            resulting JavaUnit.
     * 
     * @throws JavaModelException
     *             if model creation/modifications fail.
     */
    private void createCreatePropertyContainer(JavaCompilationUnit unit) throws JavaModelException {
        AbstractMethodDeclaration method = JavaAstElementFactory.getInstance().getJavaAstType()
                .getMethod(unit.getType(), SIGNATURE_CREATEPROPERTYCONTAINER);

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        final TypeReference parentType = producer.createTypeReference(this.extention, false);

        // Replace parent class literal
        if (this.extention != null) {

            ASTVisitor vistior = new ASTVisitor() {

                @Override
                public boolean visit(ClassLiteralAccess classLiteral, BlockScope scope) {
                    classLiteral.type = parentType;
                    return super.visit(classLiteral, scope);
                }
            };

            for (Statement statement : method.statements) {
                statement.traverse(vistior, null);
            }

        } else {
            // remove message send for parent call
            method.statements[method.statements.length - 2] = method.statements[method.statements.length - 1];
            method.statements = Arrays.copyOf(method.statements, 2);
        }

        int returnStatementPos = method.statements.length - 1;

        method.statements = Arrays.copyOf(method.statements, method.statements.length
                + this.propertyContainerStatements.size());

        method.statements[method.statements.length - 1] = method.statements[returnStatementPos];

        for (int i = 0; i < this.propertyContainerStatements.size(); i++) {
            method.statements[returnStatementPos + i] = this.propertyContainerStatements.get(i);
        }

    }

    /**
     * Traverses the previously collected/created {@link FieldDeclaration}'s for each property there
     * should be a public static final String contant for it's name
     * 
     * @param type
     *            the resulting javatype.
     */
    private void createPropertyNameFields(TypeDeclaration type) throws JavaModelException {
        for (FieldDeclaration current : this.propertyNameFields) {
            JavaAstElementFactory.getInstance().getJavaAstType().addField(type, current);
        }
    }

    /**
     * Creates a 'put' statement for the static 'createPropertyContainer' method. Adding one
     * property of the given type to the map of NabuccoPropertyDescriptors utilizing the the
     * matching PropertyDescriptorSupport create method.
     * 
     * @param fieldName
     *            name of the property/field.
     * @param fieldType
     *            type name for the property
     * @param propertyType
     *            the type of property (Basetype,Datatype,List ...)
     * @param isTechnical
     *            whether the property is technical or not
     * @param additionalArguments
     *            additional arguments e.g. 'PropertyAssociationType'
     * 
     * @return an statement that add a PropertyDescriptor to a map.
     * 
     * @throws JavaModelException
     *             when model creation/modification fails.
     */
    private Statement createPropertyContainerFragment(String fieldName, String fieldType, PropertyType propertyType,
            boolean isTechnical, List<Expression> additionalArguments) throws JavaModelException {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        JavaAstMethodCall methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethodCall();

        SingleNameReference nameReference = producer.createSingleNameReference(fieldName.toUpperCase());

        SingleNameReference mapReference = producer.createSingleNameReference(VARIABLE_PROPERTY_MAP);

        MessageSend result = producer.createMessageSend(METHOD_PUT, mapReference,
                Arrays.asList(new Expression[] { nameReference }));

        SingleNameReference propertyDescriptorSupport = producer.createSingleNameReference(CLASS_SUPPORT);

        ClassLiteralAccess typeClassLiteralAccess = producer.createClassLiteralAccess(fieldType);

        SingleNameReference constraints = producer.createSingleNameReference(FIELD_CONSTRAINTS);

        Literal globalIndex = this.resolveIndex();

        String stringIndex = String.valueOf(this.getPropertyStatements.size());
        Literal localIndex = producer.createLiteral(stringIndex, LiteralType.INT_LITERAL);

        Literal technicalFlag;
        if (isTechnical) {
            technicalFlag = producer.createLiteral(null, LiteralType.TRUE_LITERAL);
        } else {
            technicalFlag = producer.createLiteral(null, LiteralType.FALSE_LITERAL);
        }

        ArrayReference arrayReference = new ArrayReference(constraints, localIndex);

        switch (propertyType) {

        case BASETYPE: {
            MessageSend createBasetype = producer.createMessageSend(METHOD_CREATE_BASETYPE, propertyDescriptorSupport,
                    Collections.<Expression> emptyList());
            methodFactory.addArgument(nameReference, createBasetype);
            methodFactory.addArgument(typeClassLiteralAccess, createBasetype);
            methodFactory.addArgument(globalIndex, createBasetype);
            methodFactory.addArgument(arrayReference, createBasetype);
            methodFactory.addArgument(technicalFlag, createBasetype);

            methodFactory.addArgument(createBasetype, result);
            break;
        }

        case ENUMERATION: {
            MessageSend createEnum = producer.createMessageSend(METHOD_CREATE_ENUMERATION, propertyDescriptorSupport,
                    Collections.<Expression> emptyList());
            methodFactory.addArgument(nameReference, createEnum);
            methodFactory.addArgument(typeClassLiteralAccess, createEnum);
            methodFactory.addArgument(globalIndex, createEnum);
            methodFactory.addArgument(arrayReference, createEnum);
            methodFactory.addArgument(technicalFlag, createEnum);

            methodFactory.addArgument(createEnum, result);
            break;
        }

        case DATATYPE: {
            MessageSend createDatatype = producer.createMessageSend(METHOD_CREATE_DATATYPE, propertyDescriptorSupport,
                    Collections.<Expression> emptyList());
            methodFactory.addArgument(nameReference, createDatatype);
            methodFactory.addArgument(typeClassLiteralAccess, createDatatype);
            methodFactory.addArgument(globalIndex, createDatatype);
            methodFactory.addArgument(arrayReference, createDatatype);
            methodFactory.addArgument(technicalFlag, createDatatype);
            for (Expression expression : additionalArguments) {
                methodFactory.addArgument(expression, createDatatype);
            }

            methodFactory.addArgument(createDatatype, result);
            break;
        }

        case COLLECTION: {
            MessageSend createCollection = producer.createMessageSend(METHOD_CREATE_COLLECTION,
                    propertyDescriptorSupport, Collections.<Expression> emptyList());
            methodFactory.addArgument(nameReference, createCollection);
            methodFactory.addArgument(typeClassLiteralAccess, createCollection);
            methodFactory.addArgument(globalIndex, createCollection);
            methodFactory.addArgument(arrayReference, createCollection);
            methodFactory.addArgument(technicalFlag, createCollection);
            for (Expression expression : additionalArguments) {
                methodFactory.addArgument(expression, createCollection);
            }

            methodFactory.addArgument(createCollection, result);

            break;
        }

        }
        return result;
    }

    /**
     * Resolves the current index number (including all parent properties) and creates an integer
     * literal for it.
     * 
     * @return the integer literal
     * 
     * @throws JavaModelException
     */
    private Literal resolveIndex() throws JavaModelException {
        int index = 0;
        index += (this.parentProperties != null) ? this.parentProperties.size() : 0;
        index += (this.getPropertyStatements != null) ? this.getPropertyStatements.size() : 0;

        return JavaAstModelProducer.getInstance().createLiteral(String.valueOf(index), LiteralType.INT_LITERAL);
    }
}
