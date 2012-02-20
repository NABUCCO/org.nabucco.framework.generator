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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionImplementationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeFacade;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.util.NabuccoPropertyKey;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstType;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaReflectionFacade
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaReflectionFacade {

    /** Signature for getProperties() method. */
    static final JavaAstMethodSignature GET_PROPERTIES = new JavaAstMethodSignature("getProperties");

    /** Signature for createPropertyContainer() method. */
    static final JavaAstMethodSignature GET_PROPERTY_CONTAINER = new JavaAstMethodSignature("createPropertyContainer");

    /** Signature for setProperty() method. */
    static final JavaAstMethodSignature SET_PROPERTY = new JavaAstMethodSignature("setProperty", "NabuccoProperty");

    /** Signature for setProperty() method. */
    static final JavaAstMethodSignature GET_PROPERTY_DESCRIPTOR = new JavaAstMethodSignature("getPropertyDescriptor",
            "String");

    /** Signature for setProperty() method. */
    static final JavaAstMethodSignature GET_PROPERTY_DESCRIPTOR_LIST = new JavaAstMethodSignature(
            "getPropertyDescriptorList");

    /**
     * Singleton instance.
     */
    private static NabuccoToJavaReflectionFacade instance = new NabuccoToJavaReflectionFacade();

    /**
     * Private constructor.
     */
    private NabuccoToJavaReflectionFacade() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoToJavaReflectionFacade instance.
     */
    public static NabuccoToJavaReflectionFacade getInstance() {
        return instance;
    }

    /**
     * Create get/set properties methods for basetypes.
     * 
     * @param basetype
     *            nabucco basetype statement
     * @param unit
     *            java compilation unit
     * 
     * @throws JavaModelException
     *             when the reflection methods cannot be created
     */
    public void createReflectionMethods(BasetypeStatement basetype, JavaCompilationUnit unit) throws JavaModelException {

        if (!basetype.nodeOptional.present()) {
            return;
        }

        Node node = basetype.nodeOptional.node;
        if (!(node instanceof ExtensionDeclaration)) {
            return;
        }

        ExtensionDeclaration extension = (ExtensionDeclaration) node;
        String superType = ((NodeToken) extension.nodeChoice.choice).tokenImage;
        String simpleType = BasetypeFacade.mapToPrimitiveType(superType);

        this.handleSimpleGetProperties(unit);
        this.handleCreatePropertyContainer(simpleType, unit);
        this.handleStaticMethods(unit);
    }

    /**
     * Mofiy the static getPropertyDescriptor() methods.
     * 
     * @param unit
     *            the java unit
     * 
     * @throws JavaModelException
     */
    private void handleStaticMethods(JavaCompilationUnit unit) throws JavaModelException {
        JavaAstType factory = JavaAstElementFactory.getInstance().getJavaAstType();

        TypeDeclaration type = unit.getType();

        final TypeReference classTypeReference = JavaAstModelProducer.getInstance().createTypeReference(
                factory.getTypeName(type), false);

        AbstractMethodDeclaration[] methods = new AbstractMethodDeclaration[] {
                factory.getMethod(type, GET_PROPERTY_DESCRIPTOR), factory.getMethod(type, GET_PROPERTY_DESCRIPTOR_LIST) };

        ASTVisitor alterClassLiteral = new ASTVisitor() {

            @Override
            public boolean visit(ClassLiteralAccess classLiteral, BlockScope scope) {
                classLiteral.type = classTypeReference;
                return super.visit(classLiteral, scope);
            }
        };

        for (AbstractMethodDeclaration currentMethodDeclaration : methods) {
            for (Statement statement : currentMethodDeclaration.statements) {
                statement.traverse(alterClassLiteral, null);
            }
        }

    }

    /**
     * Modify the getPropertyContainer method.
     * 
     * @param simpleType
     *            the simple type name
     * @param unit
     *            the java unit
     * 
     * @throws JavaModelException
     */
    private void handleCreatePropertyContainer(String simpleType, JavaCompilationUnit unit) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        TypeDeclaration type = unit.getType();

        AbstractMethodDeclaration method = javaFactory.getJavaAstType().getMethod(type, GET_PROPERTY_CONTAINER);
        MessageSend putToMap = (MessageSend) method.statements[1];
        MessageSend createSimpletypeCall = (MessageSend) putToMap.arguments[1];
        createSimpletypeCall.arguments[1] = producer.createClassLiteralAccess(simpleType);
    }

    /**
     * Modify the getProperties method for simple types.
     * 
     * @param unit
     *            the java unit
     * 
     * @throws JavaModelException
     */
    private void handleSimpleGetProperties(JavaCompilationUnit unit) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        MethodDeclaration getProperties = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(unit.getType(),
                GET_PROPERTIES);

        MessageSend addProperty = (MessageSend) getProperties.statements[1];
        MessageSend allocation = (MessageSend) addProperty.arguments[0];
        MessageSend innerCall = (MessageSend) allocation.receiver;
        innerCall.receiver = producer.createSingleNameReference(javaFactory.getJavaAstType()
                .getTypeName(unit.getType()));
    }

    /**
     * Create get/set properties methods for datatypes.
     * 
     * @param datatype
     *            nabucco datatype statement
     * @param qualifiedName
     *            the qualified name
     * @param imports
     *            the type imports
     * @param unit
     *            java compilation unit
     * @param nabuccoExtension
     *            the extended type if any or <code>null</code>.
     * @param properties
     *            the parent properties
     * 
     * @throws JavaModelException
     *             when the reflection methods cannot be created
     */
    public void createReflectionMethods(DatatypeStatement datatype, String qualifiedName, Set<String> imports,
            JavaCompilationUnit unit, String nabuccoExtension, Map<NabuccoPropertyKey, Node> properties)
            throws JavaModelException {

        NabuccoToJavaPropertiesVisitor visitor = new NabuccoToJavaPropertiesVisitor(qualifiedName, imports,
                nabuccoExtension, properties, CollectionImplementationType.NABUCCO);

        datatype.accept(visitor);

        // Skip if super-class is of same component!
        if (datatype.nodeOptional1.present()) {
            Node extension = datatype.nodeOptional1.node;

            if (extension instanceof ExtensionDeclaration) {
                ExtensionDeclaration extensionDeclaration = (ExtensionDeclaration) extension;
                String extensionType = ((NodeToken) extensionDeclaration.nodeChoice.choice).tokenImage;
                String pkg = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
                String importString = NabuccoToJavaVisitorSupport.resolveImport(extensionType, pkg, imports);

                if (!NabuccoCompilerSupport.isOtherComponent(qualifiedName, importString)) {
                    visitor.finish(unit);
                    return;
                }
            }
        }

        // Inherited Datatype Declarations
        for (Entry<NabuccoPropertyKey, Node> entry : properties.entrySet()) {

            Node property = entry.getValue();

            if (property instanceof DatatypeDeclaration) {

                DatatypeDeclaration datatypeProperty = (DatatypeDeclaration) property;

                if (datatypeProperty.nodeOptional.present()) {
                    continue;
                }

                NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                        datatypeProperty.nodeToken1.tokenImage);

                if (multiplicity.isMultiple()) {
                    continue;
                }

                String importString = entry.getKey().getQualifiedType();

                if (!NabuccoCompilerSupport.isOtherComponent(qualifiedName, importString)) {
                    continue;
                }

                imports.add(importString);
                property.accept(visitor);
            }
        }

        visitor.finish(unit);
    }

    /**
     * Create get/set properties methods for messages.
     * 
     * @param message
     *            nabucco message statement
     * @param qualifiedName
     *            the qualified name
     * @param imports
     *            the type imports
     * @param unit
     *            java compilation unit
     * 
     * @throws JavaModelException
     *             when the reflection methods cannot be created
     */
    public void createReflectionMethods(MessageStatement message, String qualifiedName, Set<String> imports,
            JavaCompilationUnit unit) throws JavaModelException {

        NabuccoToJavaPropertiesVisitor visitor = new NabuccoToJavaPropertiesVisitor(qualifiedName, imports, null, null,
                CollectionImplementationType.NABUCCO);
        message.accept(visitor);

        handleStaticMethods(unit);

        visitor.finish(unit);
    }

}
