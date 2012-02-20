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
package org.nabucco.framework.generator.compiler.transformation.java.message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.OrderStrategyType;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionImplementationType;
import org.nabucco.framework.generator.compiler.transformation.common.collection.CollectionType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.util.FieldOptions;
import org.nabucco.framework.generator.compiler.transformation.java.common.constraint.NabuccoToJavaConstraintMapper;
import org.nabucco.framework.generator.compiler.transformation.java.common.reflection.NabuccoToJavaReflectionFacade;
import org.nabucco.framework.generator.compiler.transformation.java.datatype.CodePathSupport;
import org.nabucco.framework.generator.compiler.transformation.java.datatype.NabuccoToJavaDatatypeVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierTypeMapper;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaMessageVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaMessageVisitor extends NabuccoToJavaVisitorSupport {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaMessageVisitor.class);

    /** List for all field names */
    private List<String> fieldList = new ArrayList<String>();

    /** List for all field constraint literals */
    private List<Literal> constraintLiterals = new ArrayList<Literal>();

    /** Collected default value expressions */
    private List<Assignment> defaultValues = new ArrayList<Assignment>();

    public NabuccoToJavaMessageVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(MessageStatement nabuccoMessage, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoMessage, target);

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = nabuccoMessage.nodeToken2.tokenImage;
        String pkg = super.getVisitorContext().getPackage();

        NabuccoModifierType modifier = NabuccoModifierComponentMapper
                .getModifierType(nabuccoMessage.nodeToken.tokenImage);
        String projectName = super.getProjectName(NabuccoModelType.MESSAGE, modifier);

        try {
            // Load Template
            JavaCompilationUnit unit = super
                    .extractAst(NabuccoJavaTemplateConstants.MESSAGE_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.MESSAGE_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), pkg);

            // Super-class
            super.createSuperClass();

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoMessage.annotationDeclaration, type);

            // Constraints
            NabuccoToJavaConstraintMapper.getInstance().appendArrayLiterals(
                    this.constraintLiterals, type);

            // Adjust the getProperties() method of the message
            String qualifiedName = pkg + PKG_SEPARATOR + name;
            Set<String> imports = new HashSet<String>(super.getVisitorContext().getImportList());
            NabuccoToJavaReflectionFacade.getInstance().createReflectionMethods(nabuccoMessage,
                    qualifiedName, imports, unit);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());

            // Java methods (equals(), hashCode(), toString(),...)
            JavaAstSupport.createObjectMethods(type, true);

            // Init defaults
            NabuccoToJavaDatatypeVisitorSupport.handleInitDefaults(type, this.defaultValues);

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST message modification.");
            throw new NabuccoVisitorException("Error during Java AST message modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template message processing.");
            throw new NabuccoVisitorException("Error during Java template message processing.", te);
        }
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBasetype, MdaModel<JavaModel> target) {
        this.createBasetype(nabuccoBasetype);
    }

    @Override
    public void visit(EnumerationDeclaration nabuccoEnum, MdaModel<JavaModel> target) {
        this.createEnumeration(nabuccoEnum);
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {
        this.createDatatype(nabuccoDatatype);
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

        this.fieldList.add(name);
        this.constraintLiterals.add(NabuccoToJavaConstraintMapper.getInstance()
                .convertFieldConstraints(nabuccoBasetype, super.getVisitorContext()));

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoBasetype.nodeToken2.tokenImage);

        NabuccoModifierType modifier = NabuccoModifierTypeMapper.getInstance().mapToModifier(
                ((NodeToken) nabuccoBasetype.nodeChoice.choice).tokenImage);

        boolean isList = multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY;

        CollectionType collectionType;
        if (isList) {
            collectionType = this.getCollectionType(nabuccoBasetype.annotationDeclaration);
        } else {
            collectionType = CollectionType.NONE;
        }

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name,
                modifier, collectionType, CollectionImplementationType.NABUCCO);
        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(
                field.getAstNode(), FieldOptions.valueOf(CollectionImplementationType.NABUCCO));

        // Javadoc
        JavaAstSupport.convertJavadocAnnotations(nabuccoBasetype.annotationDeclaration,
                field.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoBasetype.annotationDeclaration,
                getter.getAstNode());

        this.getVisitorContext().getContainerList().add(field);
        this.getVisitorContext().getContainerList().add(getter);

        if (!isList) {
            JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field
                    .getAstNode());
            JavaAstSupport.convertJavadocAnnotations(nabuccoBasetype.annotationDeclaration,
                    setter.getAstNode());
            this.getVisitorContext().getContainerList().add(setter);

            String basetypeImport = super.resolveImport(nabuccoBasetype.nodeToken1.tokenImage);

            String basetypeDelegate = NabuccoToJavaDatatypeVisitorSupport.resolveBasetypeDelegate(
                    super.getVisitorContext().getRootDir(), super.getVisitorContext().getPackage(),
                    basetypeImport, super.getVisitorContext().getOutDir());

            Assignment basetypeInitializer = NabuccoToJavaDatatypeVisitorSupport
                    .createBasetypeInitializer(nabuccoBasetype, basetypeDelegate);

            if (basetypeInitializer != null) {
                this.defaultValues.add(basetypeInitializer);
            }
        }
    }

    /**
     * Creates a java enumeration field with getters and setters for the given enumeration
     * declaration.
     * 
     * @param nabuccoEnum
     *            the NABUCCO enumumeration
     */
    private void createEnumeration(EnumerationDeclaration nabuccoEnum) {
        String type = ((NodeToken) nabuccoEnum.nodeChoice1.choice).tokenImage;
        String name = nabuccoEnum.nodeToken2.tokenImage;

        this.fieldList.add(name);
        this.constraintLiterals.add(NabuccoToJavaConstraintMapper.getInstance()
                .convertFieldConstraints(nabuccoEnum, super.getVisitorContext()));

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoEnum.nodeToken1.tokenImage);

        NabuccoModifierType modifier = NabuccoModifierTypeMapper.getInstance().mapToModifier(
                ((NodeToken) nabuccoEnum.nodeChoice.choice).tokenImage);

        boolean isList = multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY;

        CollectionType collectionType;
        if (isList) {
            collectionType = this.getCollectionType(nabuccoEnum.annotationDeclaration);
        } else {
            collectionType = CollectionType.NONE;
        }

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name,
                modifier, collectionType, CollectionImplementationType.NABUCCO);
        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(
                field.getAstNode(), FieldOptions.valueOf(CollectionImplementationType.NABUCCO));

        // Javadoc
        JavaAstSupport.convertJavadocAnnotations(nabuccoEnum.annotationDeclaration,
                field.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoEnum.annotationDeclaration,
                getter.getAstNode());

        this.getVisitorContext().getContainerList().add(field);
        this.getVisitorContext().getContainerList().add(getter);

        if (!isList) {
            JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field
                    .getAstNode());
            JavaAstSupport.convertJavadocAnnotations(nabuccoEnum.annotationDeclaration,
                    setter.getAstNode());
            this.getVisitorContext().getContainerList().add(setter);

            // Default Value
            Assignment enumInitializer = NabuccoToJavaDatatypeVisitorSupport
                    .createEnumInitializer(nabuccoEnum);

            if (enumInitializer != null) {
                this.defaultValues.add(enumInitializer);
            }
        }
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
                .convertFieldConstraints(nabuccoDatatype, super.getVisitorContext()));

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoDatatype.nodeToken1.tokenImage);

        NabuccoModifierType modifier = NabuccoModifierTypeMapper.getInstance().mapToModifier(
                ((NodeToken) nabuccoDatatype.nodeChoice.choice).tokenImage);

        boolean isList = multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY;

        CollectionType collectionType;
        if (isList) {
            collectionType = this.getCollectionType(nabuccoDatatype.annotationDeclaration);
        } else {
            collectionType = CollectionType.NONE;
        }

        JavaAstContainter<FieldDeclaration> field = JavaAstSupport.createField(type, name,
                modifier, collectionType, CollectionImplementationType.NABUCCO);
        JavaAstContainter<MethodDeclaration> getter = JavaAstSupport.createGetter(
                field.getAstNode(), FieldOptions.valueOf(CollectionImplementationType.NABUCCO));

        // Javadoc
        JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration,
                field.getAstNode());
        JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration,
                getter.getAstNode());

        this.getVisitorContext().getContainerList().add(field);
        this.getVisitorContext().getContainerList().add(getter);

        if (!isList) {
            JavaAstContainter<MethodDeclaration> setter = JavaAstSupport.createSetter(field
                    .getAstNode());
            JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration,
                    setter.getAstNode());
            this.getVisitorContext().getContainerList().add(setter);
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
        CodePathSupport.createCodePath(name, path, getVisitorContext());
    }

    /**
     * Extract the order strategy of the datatype declaration annotations.
     * 
     * @param annotations
     *            the annotations
     * 
     * @return the order strategy
     */
    private CollectionType getCollectionType(AnnotationDeclaration annotations) {
        NabuccoAnnotation orderStrategy = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                annotations, NabuccoAnnotationType.ORDER_STRATEGY);

        if (orderStrategy != null) {
            return OrderStrategyType.getType(orderStrategy.getValue()).getCollectionType();
        }

        return CollectionType.LIST;
    }
}
