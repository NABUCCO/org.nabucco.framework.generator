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
package org.nabucco.framework.generator.compiler.transformation.java.common.reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.visitor.DepthFirstVisitor;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstType;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaDatatypeReflectionVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaDatatypeReflectionVisitor extends DepthFirstVisitor {

    /* Constants */

    private static final String METHOD_ADD = "add";

    private static final String VARIABLE_PROPERTIES = "properties";

    private static final String FIELD_NAMES = "PROPERTY_NAMES";

    private static final String FIELD_CONSTRAINTS = "PROPERTY_CONSTRAINTS";

    /* Fields */

    private SingleNameReference propertyList;

    private List<MessageSend> propertyAllocations = new ArrayList<MessageSend>();

    private Set<PropertyType> propertyTypes = new HashSet<PropertyType>();

    private List<Literal> propertyNames = new ArrayList<Literal>();

    @Override
    public void visit(BasetypeDeclaration basetype) {

        if (isRedefinition(basetype)) {
            return;
        }

        String name = basetype.nodeToken3.tokenImage;
        String type = basetype.nodeToken1.tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(basetype.nodeToken2.tokenImage);

        try {
            if (multiplicity.isMultiple()) {
                this.createListProperty(name, type);
            } else {
                this.createBasetypeProperty(name, type);
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

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(enumeration.nodeToken1.tokenImage);

        try {
            if (multiplicity.isMultiple()) {
                this.createListProperty(name, type);
            } else {
                this.createEnumProperty(name, type);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating datatype reflection.", e);
        }
    }

    @Override
    public void visit(DatatypeDeclaration datatype) {

        if (isRedefinition(datatype)) {
            return;
        }
        
        String name = datatype.nodeToken2.tokenImage;
        String type = ((NodeToken) datatype.nodeChoice1.choice).tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(datatype.nodeToken1.tokenImage);

        try {
            if (multiplicity.isMultiple()) {
                this.createListProperty(name, type);
            } else {
                this.createDatatypeProperty(name, type);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating datatype reflection.", e);
        }
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
            if (NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                    basetype.annotationDeclaration, NabuccoAnnotationType.REDEFINED) == null) {
                return false;
            }
            return true;
        } else if (node instanceof EnumerationDeclaration) {
            EnumerationDeclaration nabuccoEnum = (EnumerationDeclaration) node;
            if (NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                    nabuccoEnum.annotationDeclaration, NabuccoAnnotationType.REDEFINED) == null) {
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * Create the BasetypeProperty allocation.
     * 
     * @param name
     *            name of the property
     * @param type
     *            type of the property
     * 
     * @throws JavaModelException
     */
    private void createBasetypeProperty(String name, String type) throws JavaModelException {
        this.createProperty(name, type, PropertyType.BASETYPE);
    }

    /**
     * Create the EnumProperty allocation.
     * 
     * @param name
     *            name of the property
     * @param type
     *            type of the property
     * 
     * @throws JavaModelException
     */
    private void createEnumProperty(String name, String type) throws JavaModelException {
        this.createProperty(name, type, PropertyType.ENUM);
    }

    /**
     * Create the DatatypeProperty allocation.
     * 
     * @param name
     *            name of the property
     * @param type
     *            type of the property
     * 
     * @throws JavaModelException
     */
    private void createDatatypeProperty(String name, String type) throws JavaModelException {
        this.createProperty(name, type, PropertyType.DATATYPE);
    }

    /**
     * Create the ListProperty allocation.
     * 
     * @param name
     *            name of the property
     * @param type
     *            type of the property
     * 
     * @throws JavaModelException
     */
    private void createListProperty(String name, String type) throws JavaModelException {
        this.createProperty(name, type, PropertyType.LIST);
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
     * 
     * @return the 'add()' method call including the property allocation
     * 
     * @throws JavaModelException
     */
    private void createProperty(String fieldName, String fieldType, PropertyType propertyType)
            throws JavaModelException {

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        if (this.propertyList == null) {
            this.propertyList = producer.createSingleNameReference(VARIABLE_PROPERTIES);
        }

        // Add name to constant array 'PROPERTY_NAME'
        this.propertyNames.add(producer.createLiteral(fieldName, LiteralType.STRING_LITERAL));

        // Method 'getProperties'
        String stringIndex = String.valueOf(this.propertyAllocations.size());
        Literal index = producer.createLiteral(stringIndex, LiteralType.INT_LITERAL);

        SingleNameReference names = producer.createSingleNameReference(FIELD_NAMES);
        SingleNameReference constraints = producer.createSingleNameReference(FIELD_CONSTRAINTS);

        List<Expression> constructorArguments = new ArrayList<Expression>();
        constructorArguments.add(new ArrayReference(names, index));
        constructorArguments.add(producer.createClassLiteralAccess(fieldType));
        constructorArguments.add(new ArrayReference(constraints, index));
        constructorArguments.add(producer.createFieldThisReference(fieldName));

        TypeReference fieldTypeRef = producer.createTypeReference(fieldType, false);

        TypeReference propertyTypeRef = producer.createParameterizedTypeReference(
                propertyType.getName(), false, Arrays.asList(fieldTypeRef));

        AllocationExpression allocation = producer.createAllocationExpression(propertyTypeRef,
                constructorArguments);

        MessageSend addMethod = producer.createMessageSend(METHOD_ADD, this.propertyList,
                Arrays.asList(allocation));

        this.propertyAllocations.add(addMethod);

        this.propertyTypes.add(propertyType);
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
     */
    public void finish(JavaCompilationUnit unit) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        if (this.propertyAllocations.isEmpty()) {
            JavaAstType typeFactory = javaFactory.getJavaAstType();

            FieldDeclaration names = typeFactory.getField(unit.getType(), FIELD_NAMES);
            FieldDeclaration constraints = typeFactory.getField(unit.getType(), FIELD_CONSTRAINTS);

            typeFactory.removeField(unit.getType(), names);
            typeFactory.removeField(unit.getType(), constraints);

            return;
        }

        MethodDeclaration getProperties = (MethodDeclaration) javaFactory.getJavaAstType()
                .getMethod(unit.getType(), NabuccoToJavaReflectionFacade.GET_PROPERTIES);

        List<Statement> statements = new ArrayList<Statement>();
        statements.add(getProperties.statements[0]);
        statements.addAll(this.propertyAllocations);
        statements.add(getProperties.statements[1]);

        getProperties.statements = statements.toArray(new Statement[statements.size()]);

        for (PropertyType propertyType : this.propertyTypes) {
            ImportReference importRef = JavaAstModelProducer.getInstance().createImportReference(
                    propertyType.getImport());
            javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importRef);
        }

        this.createPropertyNames(unit.getType());
    }

    /**
     * Create the 'PROPERTY_NAME' array initializer.
     * 
     * @param type
     *            the class holding the field.
     * 
     * @throws JavaModelException
     */
    private void createPropertyNames(TypeDeclaration type) throws JavaModelException {
        FieldDeclaration field = JavaAstElementFactory.getInstance().getJavaAstType()
                .getField(type, FIELD_NAMES);

        ((ArrayInitializer) field.initialization).expressions = this.propertyNames
                .toArray(new Literal[this.propertyNames.size()]);
    }

}
