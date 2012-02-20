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
package org.nabucco.framework.generator.compiler.transformation.java.common.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationGroupType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.constraint.util.BasetypeConstraintResolver;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitor;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaConstraintMapper
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaConstraintMapper implements NabuccoToJavaConstraintConstants {

    private static final String CONSTRAINTS_FIELD = "PROPERTY_CONSTRAINTS";

    private static final int DEFAULT_MIN_LENGTH = 0;

    private static final int DEFAULT_MAX_LENGTH = 255;

    private static final int DEFAULT_MIN_VALUE = 0;

    private static final int DEFAULT_MAX_VALUE = Integer.MAX_VALUE;

    /**
     * Singleton instance.
     */
    private static NabuccoToJavaConstraintMapper instance = new NabuccoToJavaConstraintMapper();

    /**
     * Private constructor.
     */
    private NabuccoToJavaConstraintMapper() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoToJavaConstraintMapper instance.
     */
    public static NabuccoToJavaConstraintMapper getInstance() {
        return instance;
    }

    /**
     * Converts all Constraint annotations of a Basetype Declaration.
     * 
     * @param declaration
     *            the annotated basetype
     * @param context
     *            the visitor context holding the type information (package, import, etc.)
     * 
     * @return the string literal containing the constraint
     * 
     * @throws JavaModelException
     */
    public Literal convertFieldConstraints(BasetypeDeclaration declaration, NabuccoToJavaVisitorContext context) {

        NabuccoToJavaConstraint constraint = new NabuccoToJavaBasetypeConstraint(true);

        try {

            List<NabuccoAnnotation> annotations = this.extractConstraints(declaration.annotationDeclaration);

            List<NabuccoAnnotation> basetypeAnnotations = this.resolveBasetypeAnnotations(declaration, context);

            this.appendAnnotations(annotations, basetypeAnnotations, constraint);

            NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                    declaration.nodeToken2.tokenImage);

            constraint.appendMultiplicityConstraint(multiplicity);

            return JavaAstModelProducer.getInstance().createLiteral(constraint.getValue(), LiteralType.STRING_LITERAL);

        } catch (NabuccoTransformationException ne) {
            throw new NabuccoVisitorException("Error resolving basetype constraints.", ne);
        } catch (JavaModelException je) {
            throw new NabuccoVisitorException("Error creating constraint literals.", je);
        }

    }

    /**
     * Resolves the annotations of a referenced Basetype.
     * 
     * @param declaration
     *            the basetype declaration
     * @param context
     *            the visitor context
     * 
     * @return the annotations defined in the given basetype, or null if none exist
     * 
     * @throws NabuccoTransformationException
     */
    private List<NabuccoAnnotation> resolveBasetypeAnnotations(BasetypeDeclaration declaration,
            NabuccoToJavaVisitorContext context) throws NabuccoTransformationException {
        String type = declaration.nodeToken1.tokenImage;
        String importString = NabuccoVisitor.resolveImport(type, context);

        // Resolve constraints defined in the referenced basetype!
        MdaModel<NabuccoModel> target = NabuccoDependencyResolver.getInstance().resolveDependency(context,
                context.getPackage(), importString);

        List<NabuccoAnnotation> basetypeAnnotations = new ArrayList<NabuccoAnnotation>();
        BasetypeConstraintResolver visitor = new BasetypeConstraintResolver();
        target.getModel().getUnit().accept(visitor, basetypeAnnotations);

        // Ignore constraints for large text (CLOB)
        if (visitor.isLarge()) {
            basetypeAnnotations = null;
        }

        return basetypeAnnotations;
    }

    /**
     * Converts all Constraint annotations of a Enumeration Declaration.
     * 
     * @param declaration
     *            the annotated enumeration
     * @param context
     *            the visitor context holding the type information (package, import, etc.)
     * 
     * @return the string literal containing the constraint
     * 
     * @throws JavaModelException
     */
    public Literal convertFieldConstraints(EnumerationDeclaration declaration, NabuccoToJavaVisitorContext context) {

        NabuccoToJavaConstraint constraint = new NabuccoToJavaDatatypeConstraint(true);

        try {
            List<NabuccoAnnotation> annotations = this.extractConstraints(declaration.annotationDeclaration);
            this.appendAnnotations(annotations, null, constraint);

            NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                    declaration.nodeToken1.tokenImage);

            constraint.appendMultiplicityConstraint(multiplicity);

            return JavaAstModelProducer.getInstance().createLiteral(constraint.getValue(), LiteralType.STRING_LITERAL);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating constraint literals.", e);
        }
    }

    /**
     * Converts all Constraint annotations of a Datatype Declaration.
     * 
     * @param nabuccoDatatype
     *            the annotated datatype
     * @param context
     *            the visitor context holding the type information (package, import, etc.)
     * 
     * @return the string literal containing the constraint
     * 
     * @throws JavaModelException
     */
    public Literal convertFieldConstraints(DatatypeDeclaration nabuccoDatatype, NabuccoToJavaVisitorContext context) {

        NabuccoToJavaConstraint constraint = new NabuccoToJavaDatatypeConstraint(true);

        try {
            List<NabuccoAnnotation> annotations = this.extractConstraints(nabuccoDatatype.annotationDeclaration);
            this.appendAnnotations(annotations, null, constraint);

            NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                    nabuccoDatatype.nodeToken1.tokenImage);

            constraint.appendMultiplicityConstraint(multiplicity);

            return JavaAstModelProducer.getInstance().createLiteral(constraint.getValue(), LiteralType.STRING_LITERAL);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating constraint literals.", e);
        }
    }

    /**
     * Converts all Constraint annotations of a Basetype Statement.
     * 
     * @param nabuccoBasetype
     *            the annotated basetype
     * @param type
     *            the java type to add the constraints
     * 
     * @throws JavaModelException
     */
    public void convertStatementConstraints(BasetypeStatement nabuccoBasetype, TypeDeclaration type)
            throws JavaModelException {

        List<NabuccoAnnotation> annotations = this.extractConstraints(nabuccoBasetype.annotationDeclaration);

        NabuccoToJavaConstraint constraint = new NabuccoToJavaBasetypeConstraint(false);

        this.appendAnnotations(annotations, null, constraint);

        // Append String Literals to the CONSTRAINTS array initializer.
        this.placeLiteral(type,
                JavaAstModelProducer.getInstance().createLiteral(constraint.getValue(), LiteralType.STRING_LITERAL));
    }

    /**
     * Appends an array literal to the constraint array (necessary for a field declaration).
     * 
     * @param constraint
     *            the constraint literal
     * @param type
     *            the type to add the literal
     * 
     * @throws JavaModelException
     */
    public void appendArrayLiterals(List<Literal> constraints, TypeDeclaration type) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration field = javaFactory.getJavaAstType().getField(type, CONSTRAINTS_FIELD);

        ((ArrayInitializer) field.initialization).expressions = constraints.toArray(new Literal[constraints.size()]);
    }

    /**
     * Extracts all constraint annotations of the annotation declaration.
     * 
     * @param annotationDeclaration
     *            the annotation declaration
     * 
     * @return the constraint annotations
     */
    private List<NabuccoAnnotation> extractConstraints(AnnotationDeclaration annotationDeclaration)
            throws NabuccoVisitorException {
        return NabuccoAnnotationMapper.getInstance().mapToAnnotationList(annotationDeclaration,
                NabuccoAnnotationGroupType.CONSTRAINT);
    }

    /**
     * Appends all constraint annotations to the given constraint object.
     * 
     * @param referenceAnnotations
     *            the annotations defined by the referencing type
     * @param basetypeAnnotations
     *            the annotations defined by the referenced type
     * @param constraint
     *            the constraint
     * 
     * @throws JavaModelException
     */
    private void appendAnnotations(List<NabuccoAnnotation> referenceAnnotations,
            List<NabuccoAnnotation> basetypeAnnotations, NabuccoToJavaConstraint constraint) throws JavaModelException {

        String referenceMinLength = this.getAnnotationValue(referenceAnnotations, NabuccoAnnotationType.MIN_LENGTH);
        String basetypeMinLength = this.getAnnotationValue(basetypeAnnotations, NabuccoAnnotationType.MIN_LENGTH);

        String referenceMaxLength = this.getAnnotationValue(referenceAnnotations, NabuccoAnnotationType.MAX_LENGTH);
        String basetypeMaxLength = this.getAnnotationValue(basetypeAnnotations, NabuccoAnnotationType.MAX_LENGTH);

        String referenceMinValue = this.getAnnotationValue(referenceAnnotations, NabuccoAnnotationType.MIN_VALUE);
        String basetypeMinValue = this.getAnnotationValue(basetypeAnnotations, NabuccoAnnotationType.MIN_VALUE);

        String referenceMaxValue = this.getAnnotationValue(referenceAnnotations, NabuccoAnnotationType.MAX_VALUE);
        String basetypeMaxValue = this.getAnnotationValue(basetypeAnnotations, NabuccoAnnotationType.MAX_VALUE);

        String referencePattern = this.getAnnotationValue(referenceAnnotations, NabuccoAnnotationType.PATTERN);
        String basetypePattern = this.getAnnotationValue(basetypeAnnotations, NabuccoAnnotationType.PATTERN);

        String minLength = this.compareMinLength(referenceMinLength, referenceMaxLength, basetypeMinLength,
                basetypeMaxLength);

        if (minLength != null) {
            constraint.appendMinLengthConstraint(minLength);
        }

        String maxLength = this.compareMaxLength(referenceMinLength, referenceMaxLength, basetypeMinLength,
                basetypeMaxLength);

        if (maxLength != null) {
            constraint.appendMaxLengthConstraint(maxLength);
        }

        String minValue = this
                .compareMinValue(referenceMinValue, referenceMaxValue, basetypeMinValue, basetypeMaxValue);

        if (minValue != null) {
            constraint.appendMinValueConstraint(minValue);
        }

        String maxValue = this
                .compareMaxValue(referenceMinValue, referenceMaxValue, basetypeMinValue, basetypeMaxValue);

        if (maxValue != null) {
            constraint.appendMaxValueConstraint(maxValue);
        }

        String pattern = this.comparePattern(referencePattern, basetypePattern);

        if (pattern != null) {

            try {
                Pattern.compile(pattern);
            } catch (PatternSyntaxException pe) {
                throw new NabuccoVisitorException("Pattern '" + pattern + "' is not valid.", pe);
            }

            constraint.appendPatternConstraint(pattern);
        }
    }

    /**
     * Compares the min lengths.
     * 
     * @param referenceMinLength
     *            the min length annotation of the reference
     * @param referenceMaxLength
     *            the max length annotation of the reference
     * @param basetypeMinLength
     *            the min length annotation of the basetype
     * @param basetypeMaxLength
     *            the max length annotation of the basetype
     * 
     * @return the merged annotation value
     */
    private String compareMinLength(String referenceMinLength, String referenceMaxLength, String basetypeMinLength,
            String basetypeMaxLength) {

        if (referenceMinLength == null) {
            if (basetypeMinLength == null) {
                return null;
            }
            return basetypeMinLength;
        }

        if (basetypeMinLength == null) {
            return referenceMinLength;
        }

        try {
            Integer referenceMin = Integer.valueOf(referenceMinLength);
            Integer referenceMax = referenceMaxLength != null ? Integer.valueOf(referenceMaxLength)
                    : DEFAULT_MAX_LENGTH;
            Integer basetypeMin = Integer.valueOf(basetypeMinLength);
            Integer basetypeMax = basetypeMaxLength != null ? Integer.valueOf(basetypeMaxLength) : DEFAULT_MAX_LENGTH;

            if (referenceMin > referenceMax) {
                throw new NabuccoVisitorException("@MinLength larger @MaxLength.");
            }
            if (referenceMin < basetypeMin) {
                throw new NabuccoVisitorException("@MinLength smaller origin @MinLength.");
            }
            if (referenceMin > basetypeMax) {
                throw new NabuccoVisitorException("@MinLength larger origin @MaxLength.");
            }

            return referenceMinLength;

        } catch (NumberFormatException nfe) {
            throw new NabuccoVisitorException("Length Constraint Annotation is not valid.", nfe);
        }
    }

    /**
     * Compares the max lengths.
     * 
     * @param referenceMinLength
     *            the min length annotation of the reference
     * @param referenceMaxLength
     *            the max length annotation of the reference
     * @param basetypeMinLength
     *            the min length annotation of the basetype
     * @param basetypeMaxLength
     *            the max length annotation of the basetype
     * 
     * @return the merged annotation value
     */
    private String compareMaxLength(String referenceMinLength, String referenceMaxLength, String basetypeMinLength,
            String basetypeMaxLength) {
        if (referenceMaxLength == null) {
            if (basetypeMaxLength == null) {
                return null;
            }
            return basetypeMaxLength;
        }

        if (basetypeMaxLength == null) {
            return referenceMaxLength;
        }

        try {
            Integer referenceMin = referenceMinLength != null ? Integer.valueOf(referenceMinLength)
                    : DEFAULT_MIN_LENGTH;
            Integer referenceMax = Integer.valueOf(referenceMaxLength);
            Integer basetypeMin = basetypeMinLength != null ? Integer.valueOf(basetypeMinLength) : DEFAULT_MIN_LENGTH;
            Integer basetypeMax = Integer.valueOf(basetypeMaxLength);

            if (referenceMax < referenceMin) {
                throw new NabuccoVisitorException("@MaxLength smaller @MinLength.");
            }
            if (referenceMax > basetypeMax) {
                throw new NabuccoVisitorException("@MaxLength larger origin @MaxLength.");
            }
            if (referenceMax < basetypeMin) {
                throw new NabuccoVisitorException("@MaxLength smaller origin @MinLength.");
            }

            return referenceMaxLength;

        } catch (NumberFormatException nfe) {
            throw new NabuccoVisitorException("Length Constraint Annotation is not valid.", nfe);
        }
    }

    /**
     * Compares the min values.
     * 
     * @param referenceMinValue
     *            the min value annotation of the reference
     * @param referenceMaxValue
     *            the max value annotation of the reference
     * @param basetypeMinValue
     *            the min value annotation of the basetype
     * @param basetypeMaxValue
     *            the max value annotation of the basetype
     * 
     * @return the merged annotation value
     */
    private String compareMinValue(String referenceMinValue, String referenceMaxValue, String basetypeMinValue,
            String basetypeMaxValue) {

        if (referenceMinValue == null) {
            if (basetypeMinValue == null) {
                return null;
            }
            return basetypeMinValue;
        }

        if (basetypeMinValue == null) {
            return referenceMinValue;
        }

        try {
            Integer referenceMin = Integer.valueOf(referenceMinValue);
            Integer referenceMax = referenceMaxValue != null ? Integer.valueOf(referenceMaxValue) : DEFAULT_MAX_VALUE;
            Integer basetypeMin = Integer.valueOf(basetypeMinValue);
            Integer basetypeMax = basetypeMaxValue != null ? Integer.valueOf(basetypeMaxValue) : DEFAULT_MAX_VALUE;

            if (referenceMin > referenceMax) {
                throw new NabuccoVisitorException("@MinValue larger @MaxValue.");
            }
            if (referenceMin < basetypeMin) {
                throw new NabuccoVisitorException("@MinValue smaller origin @MinValue.");
            }
            if (referenceMin > basetypeMax) {
                throw new NabuccoVisitorException("@MinValue larger origin @MaxValue.");
            }

            return referenceMinValue;

        } catch (NumberFormatException nfe) {
            throw new NabuccoVisitorException("Value Constraint Annotation is not valid.", nfe);
        }
    }

    /**
     * Compares the max values.
     * 
     * @param referenceMinValue
     *            the min value annotation of the reference
     * @param referenceMaxValue
     *            the max value annotation of the reference
     * @param basetypeMinValue
     *            the min valueannotation of the basetype
     * @param basetypeMaxValue
     *            the max valueannotation of the basetype
     * 
     * @return the merged annotation value
     */
    private String compareMaxValue(String referenceMinValue, String referenceMaxValue, String basetypeMinValue,
            String basetypeMaxValue) {
        if (referenceMaxValue == null) {
            if (basetypeMaxValue == null) {
                return null;
            }
            return basetypeMaxValue;
        }

        if (basetypeMaxValue == null) {
            return referenceMaxValue;
        }

        try {
            Integer referenceMin = referenceMinValue != null ? Integer.valueOf(referenceMinValue) : DEFAULT_MIN_VALUE;
            Integer referenceMax = Integer.valueOf(referenceMaxValue);
            Integer basetypeMin = basetypeMinValue != null ? Integer.valueOf(basetypeMinValue) : DEFAULT_MIN_VALUE;
            Integer basetypeMax = Integer.valueOf(basetypeMaxValue);

            if (referenceMax < referenceMin) {
                throw new NabuccoVisitorException("@MaxValue smaller @MinValue.");
            }
            if (referenceMax > basetypeMax) {
                throw new NabuccoVisitorException("@MaxValue larger origin @MaxValue.");
            }
            if (referenceMax < basetypeMin) {
                throw new NabuccoVisitorException("@MaxValue smaller origin @MinValue.");
            }

            return referenceMaxValue;

        } catch (NumberFormatException nfe) {
            throw new NabuccoVisitorException("Value Constraint Annotation is not valid.", nfe);
        }
    }

    /**
     * Compares the pattern values.
     * 
     * @param referencePattern
     *            the pattern annotation of the reference
     * @param basetypePattern
     *            the pattern annotation of the basetype
     * 
     * @return the merged annotation value
     */
    private String comparePattern(String referencePattern, String basetypePattern) {
        if (referencePattern == null) {
            return basetypePattern;
        }
        return referencePattern;
    }

    /**
     * Resolve the given annotation from the list of constraint annotations.
     * 
     * @param annotations
     *            the annotation list
     * @param type
     *            the type to resolve
     * 
     * @return the value of the resolved annotation, or null if it does not exist
     */
    private String getAnnotationValue(List<NabuccoAnnotation> annotations, NabuccoAnnotationType type) {

        if (annotations == null) {
            return null;
        }

        for (NabuccoAnnotation annotation : annotations) {
            if (annotation.getType() == type) {
                return annotation.getValue();
            }
        }

        return null;
    }

    /**
     * Places a single string literal (necessary for basetype statement constraints).
     * 
     * @param type
     *            the type to add the literal
     * @param constraint
     *            the constraint literal
     * 
     * @throws JavaModelException
     */
    private void placeLiteral(TypeDeclaration type, Literal constraint) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration field = javaFactory.getJavaAstType().getField(type, CONSTRAINTS_FIELD);

        field.initialization = constraint;
    }
}
