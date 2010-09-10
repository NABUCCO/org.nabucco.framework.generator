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
package org.nabucco.framework.generator.compiler.transformation.java.common.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationGroupType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionType;
import org.nabucco.framework.generator.compiler.transformation.java.basetype.NabuccoToJavaBasetypeMapping;
import org.nabucco.framework.generator.compiler.transformation.java.basetype.NabuccoToJavaBasetypeReferences;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstMethodStatementContainer;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.GetterSetterOptions;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.JavaAstGetterSetterProducer;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.equals.JavaAstObjectMethodFactory;
import org.nabucco.framework.generator.compiler.transformation.java.common.javadoc.NabuccoJavadocTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.common.javadoc.NabuccoToJavaJavadocCreator;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaModelVisitor;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ParameterList;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstField;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethod;
import org.nabucco.framework.mda.model.java.ast.JavaAstUnit;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * JavaAstSupport
 * <p/>
 * Support class for {@link NabuccoToJavaModelVisitor} instances. Creates and modifies Java AST
 * elements.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class JavaAstSupport implements CollectionConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            JavaAstSupport.class);

    /**
     * Private constructor must not be invoked
     */
    private JavaAstSupport() {
    }

    /**
     * Converts and adds a list of {@link JavaAstContainter} instances into java ASTNodes and
     * related ImportReferences.
     * 
     * @param unit
     *            the unit to add the nodes
     * @param containers
     *            the container holding the nodes
     * @param nabuccoImports
     *            the NABUCCO imports
     * 
     * @throws JavaModelException
     */
    public static void convertAstNodes(JavaCompilationUnit unit,
            List<JavaAstContainter<? extends ASTNode>> containers, List<String> nabuccoImports)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        TypeDeclaration type = javaFactory.getJavaAstUnit().getPublicJavaClass(
                unit.getUnitDeclaration());

        Set<String> convertedImports = new HashSet<String>();

        for (JavaAstContainter<? extends ASTNode> container : containers) {

            ASTNode astNode = container.getAstNode();

            switch (container.getType()) {

            case IMPORT:
                ImportReference importRef = (ImportReference) astNode;
                javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importRef);
                break;

            case INTERFACE:
                TypeReference intf = (TypeReference) astNode;
                javaFactory.getJavaAstType().addInterface(type, intf);
                break;

            case SUPER_CLASS:
                TypeReference superClass = (TypeReference) astNode;
                javaFactory.getJavaAstType().setSuperClass(type, superClass);
                break;

            case FIELD:
                FieldDeclaration field = (FieldDeclaration) astNode;
                javaFactory.getJavaAstType().addField(type, field);
                break;

            case METHOD:
                MethodDeclaration method = (MethodDeclaration) astNode;
                javaFactory.getJavaAstType().addMethod(type, method);
                break;

            case METHOD_STATEMENT:
                convertMethodStatement(container, type);
                break;

            default:
                throw new IllegalArgumentException("Java AST type '"
                        + container.getType() + "' is not supported.");
            }

            JavaAstSupport.createImports(unit.getUnitDeclaration(), container, nabuccoImports,
                    convertedImports);
        }
    }

    /**
     * Creates import references for a {@link JavaAstContainter} instance.
     * 
     * @param unit
     *            the compilation unit to add the imports
     * @param container
     *            the container for the import references
     * @param nabuccoImports
     *            the list of NABUCCO imports
     * @param convertedImports
     *            the already converted imports
     * 
     * @throws JavaModelException
     */
    private static void createImports(CompilationUnitDeclaration unit,
            JavaAstContainter<? extends ASTNode> container, List<String> nabuccoImports,
            Set<String> convertedImports) throws JavaModelException {

        JavaAstUnit unitFactory = JavaAstElementFactory.getInstance().getJavaAstUnit();

        for (String containerImport : container.getImports()) {

            // Final import string.
            String importName = null;
            
            if (containerImport.contains(PKG_SEPARATOR)) {
                // Import is already qualified.
                importName = containerImport;
            } else {
                // Import must be resolved.
                String resolvedImport = null;
                for (String nabuccoImport : nabuccoImports) {
                    if (nabuccoImport.endsWith(containerImport)) {
                        String[] importToken = nabuccoImport.split("\\.");
                        if (importToken[importToken.length - 1].equals(containerImport)) {
                            resolvedImport = nabuccoImport;
                            break;
                        }
                    }
                }
                importName = resolvedImport;
            }

            if (importName != null && !convertedImports.contains(importName)) {
                ImportReference importReference = JavaAstModelProducer.getInstance()
                        .createImportReference(importName);

                convertedImports.add(importName);
                unitFactory.addImport(unit, importReference);
            }
        }
    }

    /**
     * Converts a {@link JavaAstMethodStatementContainer} into a method statement of the particular
     * method.
     * 
     * @param container
     *            the container to add
     * 
     * @param type
     *            the type of the method
     * 
     * @throws JavaModelException
     */
    private static void convertMethodStatement(JavaAstContainter<? extends ASTNode> container,
            TypeDeclaration type) throws JavaModelException {

        if (container instanceof JavaAstMethodStatementContainer<?>) {
            JavaAstMethodStatementContainer<?> statementContainer = (JavaAstMethodStatementContainer<?>) container;
            JavaAstMethodSignature signature = statementContainer.getSignature();

            JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

            AbstractMethodDeclaration method;
            if (statementContainer.isConstructor()) {
                method = javaFactory.getJavaAstType().getConstructor(type, signature);
            } else {
                method = javaFactory.getJavaAstType().getMethod(type, signature);
            }

            javaFactory.getJavaAstMethod().addStatement(method, (Statement) container.getAstNode());
        }
    }

    /**
     * Creates and adds 'equals', 'hashCode' and 'toString' for the javaFields contained by the
     * type.
     * 
     * @param type
     *            the type to add the methods for
     * @param collectionsEnabled
     *            depending on the flag, collections are considered or not
     * 
     * @throws JavaTemplateException
     */
    public static void createObjectMethods(TypeDeclaration type, boolean collectionsEnabled)
            throws JavaTemplateException {
        if (collectionsEnabled) {
            JavaAstObjectMethodFactory.getInstance().getDefaultStrategy().createAllObjectMethods(
                    type);
        } else {
            JavaAstObjectMethodFactory.getInstance().getNoCollectionStrategy()
                    .createAllObjectMethods(type);
        }
    }

    /**
     * Converts NABUCCO documentation annotations (like @Description and @Author) to Javadoc.
     * 
     * @param nabuccoAnnotations
     *            the annotation declaration containing all NABUCCO annotations
     * @param type
     *            the java type to add the javadoc
     */
    public static void convertJavadocAnnotations(AnnotationDeclaration nabuccoAnnotations,
            TypeDeclaration type) throws NabuccoVisitorException {

        try {
            List<NabuccoAnnotation> javaAnnotations = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotations(nabuccoAnnotations, NabuccoAnnotationGroupType.DOCUMENTATION);
            NabuccoToJavaJavadocCreator.createJavadoc(javaAnnotations, type);
        } catch (NabuccoJavadocTransformationException je) {
            logger.error(je, "Error converting type annotations to javadoc.");
            throw new NabuccoVisitorException("Error converting type annotations to javadoc.", je);
        }
    }

    /**
     * Converts NABUCCO documentation annotations (like @Description and @Author) to Javadoc.
     * 
     * @param nabuccoAnnotations
     *            the annotation declaration containing all NABUCCO annotations
     * @param method
     *            the java method to add the javadoc
     */
    public static void convertJavadocAnnotations(AnnotationDeclaration nabuccoAnnotations,
            MethodDeclaration method) throws NabuccoVisitorException {

        try {
            List<NabuccoAnnotation> javaAnnotations = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotations(nabuccoAnnotations, NabuccoAnnotationGroupType.DOCUMENTATION);
            NabuccoToJavaJavadocCreator.createJavadoc(javaAnnotations, method);
        } catch (NabuccoJavadocTransformationException je) {
            logger.error(je, "Error converting method annotations to javadoc.");
            throw new NabuccoVisitorException("Error converting method annotations to javadoc.", je);
        }
    }

    /**
     * Converts NABUCCO documentation annotations (like @Description and @Author) to Javadoc.
     * 
     * @param nabuccoAnnotations
     *            the annotation declaration containing all NABUCCO annotations
     * @param field
     *            the java field to add the javadoc
     */
    public static void convertJavadocAnnotations(AnnotationDeclaration nabuccoAnnotations,
            FieldDeclaration field) throws NabuccoVisitorException {

        try {
            List<NabuccoAnnotation> javaAnnotations = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotations(nabuccoAnnotations, NabuccoAnnotationGroupType.DOCUMENTATION);
            NabuccoToJavaJavadocCreator.createJavadoc(javaAnnotations, field);
        } catch (NabuccoJavadocTransformationException je) {
            logger.error(je, "Error converting field annotations to javadoc.");
            throw new NabuccoVisitorException("Error converting field annotations to javadoc.", je);
        }
    }

    /**
     * Creates a new container for a Java AST Superclass.
     * 
     * @param className
     *            given name
     * @return new {@link JavaAstContainter} for an Superclass
     * 
     * @throws JavaModelException
     */
    public static JavaAstContainter<TypeReference> createSuperClass(String className)
            throws JavaModelException {

        TypeReference superClass = JavaAstModelProducer.getInstance().createTypeReference(
                className, false);

        JavaAstContainter<TypeReference> container = new JavaAstContainter<TypeReference>(
                superClass, JavaAstType.SUPER_CLASS);

        if (NabuccoToJavaBasetypeReferences.isBasetypeReference(className)) {
            container.getImports().add(
                    NabuccoToJavaBasetypeReferences.getBasetypeReference(className));
        } else {
            container.getImports().add(className);
        }

        return container;
    }

    /**
     * Creates a new container for a Java AST Interface.
     * 
     * @param interfaceName
     *            given name
     * @return new {@link JavaAstContainter} for an Interface
     * 
     * @throws JavaModelException
     */
    public static JavaAstContainter<TypeReference> createInterface(String interfaceName)
            throws JavaModelException {

        TypeReference superClass = JavaAstModelProducer.getInstance().createTypeReference(
                interfaceName, false);

        JavaAstContainter<TypeReference> container = new JavaAstContainter<TypeReference>(
                superClass, JavaAstType.INTERFACE);

        return container;
    }

    /**
     * Creates a {@link FieldDeclaration} and adds it into a JavaAstContainer.
     * <p/>
     * 
     * @param type
     *            type of the field
     * @param name
     *            name of the field
     * @param modifier
     *            modifier of the field
     * @param isList
     *            when true the type is wrapped in a list
     * 
     * @return a container holding the field declaration and related imports
     */
    public static JavaAstContainter<FieldDeclaration> createField(String type, String name,
            NabuccoModifierType modifier, boolean isList) {

        if (isList) {
            return createField(type, name, modifier, CollectionType.LIST);
        }

        return createField(type, name, modifier, null);
    }

    /**
     * Creates a {@link FieldDeclaration} and adds it into a JavaAstContainer.
     * <p/>
     * 
     * @param type
     *            type of the field
     * @param name
     *            name of the field
     * @param modifier
     *            modifier of the field
     * @param collectionType
     *            type of the collection, if null no collection is created
     * 
     * @return a container holding the field declaration and related imports
     */
    public static JavaAstContainter<FieldDeclaration> createField(String type, String name,
            NabuccoModifierType modifier, CollectionType collectionType) {

        JavaAstField fieldFactory = JavaAstElementFactory.getInstance().getJavaAstField();

        try {
            FieldDeclaration field = JavaAstModelProducer.getInstance()
                    .createFieldDeclaration(name);

            JavaAstContainter<FieldDeclaration> container = new JavaAstContainter<FieldDeclaration>(
                    field, JavaAstType.FIELD);

            TypeReference typeRef = JavaAstModelProducer.getInstance().createTypeReference(type,
                    false);

            if (NabuccoToJavaBasetypeMapping.N_TYPE.getName().equals(type)) {
                container.getImports().add(NabuccoToJavaBasetypeMapping.N_TYPE.getJavaClass());
            } else {
                container.getImports().add(type);
            }

            if (collectionType != null) {

                switch (collectionType) {

                case LIST:

                    typeRef = JavaAstModelProducer.getInstance().createParameterizedTypeReference(
                            LIST, false, Arrays.asList(typeRef));

                    container.getImports().add(IMPORT_LIST);
                    break;

                case SET:

                    typeRef = JavaAstModelProducer.getInstance().createParameterizedTypeReference(
                            SET, false, Arrays.asList(typeRef));

                    container.getImports().add(IMPORT_SET);
                    break;

                case MAP:
                    throw new UnsupportedOperationException(
                            "JavaAstSupport.createField() does not support the MAP implementation yet.");

                }
            }

            fieldFactory.setFieldType(field, typeRef);
            fieldFactory.setModifier(field, NabuccoModifierComponentMapper
                    .mapModifierToJava(modifier));

            return container;
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST field modification.", jme);
        }
    }

    /**
     * Creates a getter for a field declaration.
     * 
     * @param field
     *            the field declaration
     * 
     * @return the getter method declaration
     */
    public static JavaAstContainter<MethodDeclaration> createGetter(FieldDeclaration field) {
        return JavaAstSupport.createGetter(field, null);
    }

    /**
     * Creates a getter for a field declaration.
     * 
     * @param field
     *            the field declaration
     * @param options
     *            additional options
     * 
     * @return the getter method declaration
     */
    public static JavaAstContainter<MethodDeclaration> createGetter(FieldDeclaration field,
            GetterSetterOptions options) {

        if (options == null) {
            options = new GetterSetterOptions();
        }

        try {
            MethodDeclaration getter = JavaAstGetterSetterProducer.getInstance().produceGetter(
                    field, options);
            JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                    getter, JavaAstType.METHOD);

            addCollectionImports(field, container, options);

            return container;
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating Java AST Getter.", jme);
        }
    }

    /**
     * Adds the collection imports to the container.
     * 
     * @param field
     *            the field
     * @param container
     *            the method container
     * @param options
     *            the options
     */
    private static void addCollectionImports(FieldDeclaration field,
            JavaAstContainter<MethodDeclaration> container, GetterSetterOptions options) {

        char[] type = field.type.getLastToken();

        if (Arrays.equals(type, LIST.toCharArray())) {
            switch (options.getCollectionImplementationType()) {
            case NABUCCO:
                container.getImports().add(IMPORT_NABUCCO_LIST);
                container.getImports().add(IMPORT_NABUCCO_COLLECTION_STATE);
                break;
            default:
                container.getImports().add(IMPORT_DEFAULT_LIST);
            }
        }
        if (Arrays.equals(type, SET.toCharArray())) {
            switch (options.getCollectionImplementationType()) {
            case NABUCCO:
                container.getImports().add(IMPORT_NABUCCO_SET);
                container.getImports().add(IMPORT_NABUCCO_COLLECTION_STATE);
                break;
            default:
                container.getImports().add(IMPORT_DEFAULT_SET);
            }
        }
        if (Arrays.equals(type, MAP.toCharArray())) {
            switch (options.getCollectionImplementationType()) {
            case NABUCCO:
                container.getImports().add(IMPORT_NABUCCO_MAP);
                container.getImports().add(IMPORT_NABUCCO_COLLECTION_STATE);
                break;
            default:
                container.getImports().add(IMPORT_DEFAULT_MAP);
            }
        }
    }

    /**
     * Creates a setter for a field declaration with a fetch type LAZY.
     * 
     * @param field
     *            the field declaration
     * 
     * @return the setter method declaration
     */
    public static JavaAstContainter<MethodDeclaration> createSetter(FieldDeclaration field) {
        return JavaAstSupport.createSetter(field, new GetterSetterOptions());
    }

    /**
     * Creates a setter for a field declaration.
     * 
     * @param field
     *            the field declaration
     * @param options
     *            additional options
     * 
     * @return the setter method declaration
     */
    public static JavaAstContainter<MethodDeclaration> createSetter(FieldDeclaration field,
            GetterSetterOptions options) {
        try {
            return new JavaAstContainter<MethodDeclaration>(JavaAstGetterSetterProducer
                    .getInstance().produceSetter(field, options), JavaAstType.METHOD);
        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating Java AST Setter.", jme);
        }
    }

    /**
     * Creates a {@link MethodDeclaration} and adds it to the list of method declarations in the
     * visitor context.
     * 
     * @param returnType
     *            return type of the method
     * @param name
     *            name of the method
     * @param modifier
     *            modifier of the method
     * @param parameterList
     *            parameters of the method
     * @param isAbstract
     *            whether a method is abstract or not
     */
    public static JavaAstContainter<MethodDeclaration> createMethod(String returnType, String name,
            NabuccoModifierType modifier, ParameterList parameterList, boolean isAbstract) {

        try {
            JavaAstMethod methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethod();

            MethodDeclaration method = JavaAstModelProducer.getInstance().createMethodDeclaration(
                    name, null,
                    NabuccoModifierComponentMapper.mapModifierToJava(modifier, isAbstract));

            JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(
                    method, JavaAstType.METHOD);

            TypeReference typeRef = JavaAstModelProducer.getInstance().createTypeReference(
                    returnType, false);

            container.getImports().add(returnType);

            methodFactory.setReturnType(method, typeRef);

            if (parameterList != null) {
                for (Node node : parameterList.nodeListOptional.nodes) {

                    if (node instanceof Parameter) {
                        Parameter param = (Parameter) node;

                        String argumentType = param.nodeToken.tokenImage;
                        String argumentName = param.nodeToken1.tokenImage;

                        TypeReference type = JavaAstModelProducer.getInstance()
                                .createTypeReference(argumentType, false);
                        Argument argument = JavaAstModelProducer.getInstance().createArgument(
                                argumentName, type);

                        container.getImports().add(argumentType);

                        methodFactory.addArgument(method, argument);
                    }
                }
            }
            return container;
        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST method modification.");
            throw new NabuccoVisitorException("Error during Java AST method modification.", jme);
        }
    }

    /**
     * Creates a java.util.Map {@link FieldDeclaration} and adds it into a JavaAstContainer.
     * 
     * @param key
     *            the map's key type
     * @param value
     *            the map's value type
     * @param name
     *            name of the map field
     * @param modifier
     *            modifier of the map
     * 
     * @return the container holding the field
     */
    public static JavaAstContainter<FieldDeclaration> createMap(String key, String value,
            String name, NabuccoModifierType modifier) {

        JavaAstField fieldFactory = JavaAstElementFactory.getInstance().getJavaAstField();

        try {

            int javaModifier = NabuccoModifierComponentMapper.mapModifierToJava(modifier);

            // Transient fields cannot be serialized.
            // javaModifier |= ClassFileConstants.AccTransient;

            FieldDeclaration field = JavaAstModelProducer.getInstance().createFieldDeclaration(
                    name, javaModifier);

            JavaAstContainter<FieldDeclaration> fieldContainer = new JavaAstContainter<FieldDeclaration>(
                    field, JavaAstType.FIELD);

            TypeReference keyType = JavaAstModelProducer.getInstance().createTypeReference(key,
                    false);
            TypeReference valueType = JavaAstModelProducer.getInstance().createTypeReference(value,
                    false);

            fieldContainer.getImports().add(key);
            fieldContainer.getImports().add(value);

            TypeReference mapType = JavaAstModelProducer
                    .getInstance()
                    .createParameterizedTypeReference(MAP, false, Arrays.asList(keyType, valueType));

            fieldContainer.getImports().add(IMPORT_MAP);
            fieldContainer.getImports().add(IMPORT_NABUCCO_MAP);

            fieldFactory.setFieldType(field, mapType);

            return fieldContainer;
        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST field modification.");
            throw new NabuccoVisitorException("Error during Java AST field modification.", jme);
        }
    }

    /**
     * Converts all annotations of a basetype declaration to a list of {@link NabuccoAnnotation}.
     * 
     * @param nabuccoBasetype
     *            the basetype holding the annotations
     * @param types
     *            the optional annotation types to filter for
     * 
     * @return the converted list of annotations
     */
    public static List<NabuccoAnnotation> convertAnnotations(BasetypeDeclaration nabuccoBasetype,
            NabuccoAnnotationGroupType... types) {
        return convertAnnotations(nabuccoBasetype.annotationDeclaration, types);
    }

    /**
     * Converts all annotations of a {@link AnnotationDeclaration} to a list of
     * {@link NabuccoAnnotation}.
     * 
     * @param nabuccoAnnotations
     *            the annotation declaration
     * @param types
     *            the optional annotation types to filter for
     * 
     * @return the converted list of annotations
     */
    public static List<NabuccoAnnotation> convertAnnotations(
            AnnotationDeclaration nabuccoAnnotations, NabuccoAnnotationGroupType... types) {
        return NabuccoAnnotationMapper.getInstance().mapToAnnotations(nabuccoAnnotations, types);
    }

    /**
     * Checks whether an {@link AnnotationDeclaration} does contain one of the given annotations.
     * 
     * @see NabuccoAnnotationConstants
     * 
     * @param annotationDeclaration
     *            the annotation declaration
     * @param types
     *            the annotation types to check for
     * 
     * @return <b>true</b> if an annotation does exist, <b>false</b> if not
     */
    public static boolean hasAnnotation(AnnotationDeclaration annotationDeclaration,
            NabuccoAnnotationType... types) {
        return NabuccoAnnotationMapper.getInstance().hasAnnotation(annotationDeclaration, types);
    }

}
