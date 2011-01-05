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
package org.nabucco.framework.generator.compiler.transformation.java.datatype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.common.clone.NabuccoToJavaCloneVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.common.constraint.NabuccoToJavaConstraintMapper;
import org.nabucco.framework.generator.compiler.transformation.java.common.reflection.NabuccoToJavaReflectionFacade;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierTypeMapper;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaDatatypeVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaDatatypeVisitor extends NabuccoToJavaVisitorSupport implements
        ServerConstants {

    private static final String TYPE_PATH = "String";

    private static final String TYPE_CODEPATH = "CodePath";

    private static final String IMPORT_CODEPATH = "org.nabucco.framework.base.facade.datatype.code.CodePath";

    private static final String IMPORT_DATATYPESUPPORT = "org.nabucco.framework.base.facade.datatype.DatatypeSupport";

    /** Collected default value expressions */
    private List<Expression> defaultValues = new ArrayList<Expression>();

    /** List for all field names */
    private List<String> fieldList = new ArrayList<String>();

    /** List for all field constraint literals */
    private List<Literal> constraintLiterals = new ArrayList<Literal>();

    /**
     * Creates a new {@link NabuccoToJavaDatatypeVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaDatatypeVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(DatatypeStatement nabuccoDatatype, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoDatatype, target);

        String name = nabuccoDatatype.nodeToken2.tokenImage;
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        String projectName = super.getProjectName(NabuccoModelType.DATATYPE,
                NabuccoModifierComponentMapper
                        .getModifierType(nabuccoDatatype.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.DATATYPE_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.DATATYPE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    super.getVisitorContext().getPackage());

            // Abstract datatype
            if (nabuccoDatatype.nodeOptional.present()) {
                type.modifiers = type.modifiers | ClassFileConstants.AccAbstract;
            }

            // If super-class is available, remove default import.
            if (nabuccoDatatype.nodeOptional1.present()) {
                super.removeImport(unit.getUnitDeclaration(), IMPORT_DATATYPESUPPORT);
            }

            // Super-classes
            super.createSuperClass();

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration, type);

            // Constraints
            NabuccoToJavaConstraintMapper.getInstance().appendArrayLiterals(
                    this.constraintLiterals, type);

            // Adjust the getProperties() method of the datatype
            NabuccoToJavaReflectionFacade.getInstance().createReflection(nabuccoDatatype, unit);

            // Clone Method
            NabuccoToJavaCloneVisitor cloneVisitor = new NabuccoToJavaCloneVisitor(type,
                    super.getVisitorContext());
            nabuccoDatatype.accept(cloneVisitor, target);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());

            // Java methods (equals(), hashCode(), toString(),...)
            JavaAstSupport.createObjectMethods(type, false);

            // Init defaults
            NabuccoToJavaDatatypeVisitorSupport.handleInitDefaults(type, this.defaultValues);

            NabuccoToJavaDatatypeJpaVisitor jpaVisitor = new NabuccoToJavaDatatypeJpaVisitor(
                    super.getVisitorContext(), unit);

            nabuccoDatatype.accept(jpaVisitor, target);

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during Java AST datatype modification.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during Java template datatype processing.", te);
        }
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBasetype, MdaModel<JavaModel> target) {

        // Default Value
        String basetypeImport = super.resolveImport(nabuccoBasetype.nodeToken1.tokenImage);

        String pkg = super.getVisitorContext().getPackage();

        final NabuccoToJavaVisitorContext context = super.getVisitorContext();
        try {
            // this is no redefinition so create field
            if (!NabuccoToJavaDatatypeVisitorSupport.isRedefinition(nabuccoBasetype)) {
                this.createBasetype(nabuccoBasetype);
            } else {
                ImportReference importReference = JavaAstModelProducer.getInstance()
                        .createImportReference(basetypeImport);
                JavaAstContainter<ImportReference> container = new JavaAstContainter<ImportReference>(
                        importReference, JavaAstType.IMPORT);
                context.getContainerList().add(container);
            }

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Cannot create ImportReference for Basetype '"
                    + basetypeImport + "'.", e);
        }

        String basetypeDelegate = NabuccoToJavaDatatypeVisitorSupport.resolveBasetypeDelegate(
                context.getRootDir(), pkg, basetypeImport, context.getOutDir());

        Expression basetypeInitializer = NabuccoToJavaDatatypeVisitorSupport
                .createBasetypeInitializer(nabuccoBasetype, basetypeDelegate);

        if (basetypeInitializer != null) {
            this.defaultValues.add(basetypeInitializer);
        }
    }

    @Override
    public void visit(EnumerationDeclaration nabuccoEnum, MdaModel<JavaModel> target) {

        // Default Value
        String enumImport = super
                .resolveImport(((NodeToken) nabuccoEnum.nodeChoice1.choice).tokenImage);

        try {

            // Do not create Redefined enum fields
            if (!NabuccoToJavaDatatypeVisitorSupport.isRedefinition(nabuccoEnum)) {

                // Create Enumeration
                this.createEnumeration(nabuccoEnum);

            } else {
                ImportReference importReference = JavaAstModelProducer.getInstance()
                        .createImportReference(enumImport);
                JavaAstContainter<ImportReference> container = new JavaAstContainter<ImportReference>(
                        importReference, JavaAstType.IMPORT);
                super.getVisitorContext().getContainerList().add(container);
            }

            // Default Value
            Expression enumInitializer = NabuccoToJavaDatatypeVisitorSupport
                    .createEnumInitializer(nabuccoEnum);

            if (enumInitializer != null) {
                this.defaultValues.add(enumInitializer);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Cannot create ImportReference for Enumeration '"
                    + enumImport + "'.", e);
        }
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {
        this.createDatatype(nabuccoDatatype);
        this.createRefId(nabuccoDatatype);
        this.createCodePath(nabuccoDatatype);
    }

    /**
     * Creates a java basetype field with getters and setters for the given basetype declaration.
     * 
     * @param nabuccoBasetype
     *            the NABUCCO basetype
     */
    private void createBasetype(BasetypeDeclaration nabuccoBasetype) {

        String type = nabuccoBasetype.nodeToken1.tokenImage;
        String name = nabuccoBasetype.nodeToken3.tokenImage;

        String pkg = super.getVisitorContext().getPackage();

        NabuccoToJavaVisitorContext context = super.getVisitorContext();
        String basetypeType = NabuccoToJavaDatatypeVisitorSupport.resolveBasetypeDelegate(
                context.getRootDir(), pkg, super.resolveImport(type), context.getOutDir());

        this.fieldList.add(name);

        this.constraintLiterals.add(NabuccoToJavaConstraintMapper.getInstance()
                .convertFieldConstraints(nabuccoBasetype));

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoBasetype.nodeToken2.tokenImage);

        NabuccoModifierType modifier = NabuccoModifierTypeMapper.getInstance().mapToModifier(
                ((NodeToken) nabuccoBasetype.nodeChoice.choice).tokenImage);

        boolean isList = multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY;

        if (isList) {
            throw new NabuccoVisitorException(
                    "Basetype declarations may not have a multiplicity larger 1.");
        }

        // Extra setter for delegate set(String name)
        JavaAstContainter<MethodDeclaration> extraSetter = null;

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name,
                modifier, false);
        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field
                .getAstNode());
        JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field
                .getAstNode());

        // If basetype is Identifier or Version it must not be wrapped
        if (JavaAstSupport.hasAnnotation(nabuccoBasetype.annotationDeclaration,
                NabuccoAnnotationType.PRIMARY, NabuccoAnnotationType.OPTIMISTIC_LOCK)) {
            getter = NabuccoToJavaDatatypeVisitorSupport.createBasetypeWrapperGetter(name, type,
                    basetypeType);
            setter = NabuccoToJavaDatatypeVisitorSupport.createBasetypeWrapperSetter(name, type,
                    basetypeType);
        } else {
            extraSetter = NabuccoToJavaDatatypeVisitorSupport.createBasetypeWrapperSetter(name,
                    type, basetypeType);
        }

        // Javadoc
        JavaAstSupport.convertJavadocAnnotations(nabuccoBasetype.annotationDeclaration,
                field.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoBasetype.annotationDeclaration,
                getter.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoBasetype.annotationDeclaration,
                setter.getAstNode());

        this.getVisitorContext().getContainerList().add(field);
        this.getVisitorContext().getContainerList().add(getter);
        this.getVisitorContext().getContainerList().add(setter);

        if (extraSetter != null) {
            JavaAstSupport.convertJavadocAnnotations(nabuccoBasetype.annotationDeclaration,
                    extraSetter.getAstNode());
            this.getVisitorContext().getContainerList().add(extraSetter);
        }
    }

    /**
     * Creates a java enumeration field with getters and setters for the given enumeration
     * declaration.
     * 
     * @param nabuccoEnum
     *            the NABUCCO enumeration
     */
    private void createEnumeration(EnumerationDeclaration nabuccoEnum) {

        String type = ((NodeToken) nabuccoEnum.nodeChoice1.choice).tokenImage;
        String name = nabuccoEnum.nodeToken2.tokenImage;

        this.fieldList.add(name);

        this.constraintLiterals.add(NabuccoToJavaConstraintMapper.getInstance()
                .convertFieldConstraints(nabuccoEnum));

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoEnum.nodeToken1.tokenImage);

        NabuccoModifierType modifier = NabuccoModifierTypeMapper.getInstance().mapToModifier(
                ((NodeToken) nabuccoEnum.nodeChoice.choice).tokenImage);

        boolean isList = multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY;

        if (isList) {
            throw new NabuccoVisitorException(
                    "Enum declarations may not have a multiplicity larger 1.");
        }

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name,
                modifier, false);
        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field
                .getAstNode());
        JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field
                .getAstNode());
        JavaAstContainter<MethodDeclaration> enumIdSetter = NabuccoToJavaDatatypeVisitorSupport
                .createEnumIdSetter(name, type);

        // Javadoc
        JavaAstSupport.convertJavadocAnnotations(nabuccoEnum.annotationDeclaration,
                field.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoEnum.annotationDeclaration,
                getter.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoEnum.annotationDeclaration,
                setter.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoEnum.annotationDeclaration,
                enumIdSetter.getAstNode());

        this.getVisitorContext().getContainerList().add(field);
        this.getVisitorContext().getContainerList().add(getter);
        this.getVisitorContext().getContainerList().add(setter);
        this.getVisitorContext().getContainerList().add(enumIdSetter);
    }

    /**
     * Creates a java datatype field with getters and setters for the given datatype declaration.
     * 
     * @param nabuccoDatatype
     *            the NABUCCO datatype
     */
    private void createDatatype(DatatypeDeclaration nabuccoDatatype) {
        String type = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        String name = nabuccoDatatype.nodeToken2.tokenImage;

        this.fieldList.add(name);
        this.constraintLiterals.add(NabuccoToJavaConstraintMapper.getInstance()
                .convertFieldConstraints(nabuccoDatatype));

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoDatatype.nodeToken1.tokenImage);

        NabuccoModifierType modifier = NabuccoModifierTypeMapper.getInstance().mapToModifier(
                ((NodeToken) nabuccoDatatype.nodeChoice.choice).tokenImage);

        boolean isList = multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY;

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name,
                modifier, isList);
        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field
                .getAstNode());

        if (!isList) {
            JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field
                    .getAstNode());
            this.enhanceSetter(setter, type);
            JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration,
                    setter.getAstNode());
            this.getVisitorContext().getContainerList().add(setter);
        }

        // Javadoc
        JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration,
                field.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration,
                getter.getAstNode());

        this.getVisitorContext().getContainerList().add(field);
        this.getVisitorContext().getContainerList().add(getter);
    }

    /**
     * Enhance the datatype setter by
     * 
     * @param setter
     *            the container holding the setter
     * @param type
     *            the referenced datatypes type
     */
    private void enhanceSetter(JavaAstContainter<MethodDeclaration> setter, String type) {
        try {
            String typeImport = super.resolveImport(type);
            if (NabuccoCompilerSupport.isOtherComponent(super.getVisitorContext().getPackage(),
                    typeImport) || NabuccoCompilerSupport.isBase(typeImport)) {
                NabuccoToJavaDatatypeVisitorSupport.prepareSetterForRefId(setter);
            }
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Cannot modify datatype setter.", e);
        }
    }

    /**
     * Creates a static field and getter for the @CodePath annotation.
     * 
     * @param nabuccoDatatype
     *            the datatype declaration using codepath
     */
    private void createCodePath(DatatypeDeclaration nabuccoDatatype) {
        NabuccoAnnotation codePath = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoDatatype.annotationDeclaration, NabuccoAnnotationType.CODE_PATH);

        if (codePath == null || codePath.getValue() == null) {
            return;
        }

        String path = codePath.getValue();
        String name = nabuccoDatatype.nodeToken2.tokenImage;
        String fieldName = (name + CONSTANT_SEPARATOR + TYPE_CODEPATH).toUpperCase();
        String getterName = PREFIX_GETTER
                + NabuccoTransformationUtility.firstToUpper(name) + TYPE_CODEPATH;

        JavaAstContainter<FieldDeclaration> fieldContainer = JavaAstSupport.createField(TYPE_PATH,
                fieldName, NabuccoModifierType.PRIVATE, false);

        FieldDeclaration field = fieldContainer.getAstNode();
        field.modifiers |= ClassFileConstants.AccFinal;
        field.modifiers |= ClassFileConstants.AccStatic;

        try {
            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
            Literal string = producer.createLiteral(path, LiteralType.STRING_LITERAL);

            field.initialization = string;

            TypeReference type = producer.createTypeReference(TYPE_CODEPATH, false);
            SingleNameReference reference = producer.createSingleNameReference(fieldName);

            Expression constructor = producer.createAllocationExpression(type,
                    Arrays.asList(reference));

            JavaAstContainter<MethodDeclaration> getterContainer = JavaAstSupport
                    .createGetter(field);
            MethodDeclaration getter = getterContainer.getAstNode();
            getter.modifiers |= ClassFileConstants.AccStatic;

            JavaAstElementFactory.getInstance().getJavaAstMethod().setReturnType(getter, type);
            JavaAstElementFactory.getInstance().getJavaAstMethod()
                    .setMethodName(getter, getterName);

            ReturnStatement returnStatement = (ReturnStatement) getter.statements[0];
            returnStatement.expression = constructor;

            fieldContainer.getImports().add(IMPORT_CODEPATH);
            super.getVisitorContext().getContainerList().add(fieldContainer);
            super.getVisitorContext().getContainerList().add(getterContainer);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating CodePath for datatype " + name + ".",
                    jme);
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
        String type = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        String name = nabuccoDatatype.nodeToken2.tokenImage;

        String current = super.getVisitorContext().getPackage();
        String reference = super.resolveImport(type);

        // same component must be ignored
        if (!NabuccoCompilerSupport.isBase(reference)
                && !NabuccoCompilerSupport.isOtherComponent(current, reference)) {
            return;
        }

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(REF_ID_TYPE, name
                + REF_ID, NabuccoModifierType.PRIVATE, false);
        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(field
                .getAstNode());
        JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field
                .getAstNode());

        this.getVisitorContext().getContainerList().add(field);
        this.getVisitorContext().getContainerList().add(getter);
        this.getVisitorContext().getContainerList().add(setter);
    }

}
