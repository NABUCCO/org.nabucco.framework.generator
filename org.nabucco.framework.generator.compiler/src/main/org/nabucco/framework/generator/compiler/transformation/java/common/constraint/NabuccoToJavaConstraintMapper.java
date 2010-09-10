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
package org.nabucco.framework.generator.compiler.transformation.java.common.constraint;

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationGroupType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;

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

    private static final String CONSTRAINTS_FIELD = "CONSTRAINTS";

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
     * @param nabuccoBasetype
     *            the annotated basetype
     * 
     * @return the string literal containing the constraint
     * 
     * @throws JavaModelException
     */
    public Literal convertFieldConstraints(BasetypeDeclaration nabuccoBasetype) {

        NabuccoToJavaConstraint constraint = new NabuccoToJavaBasetypeConstraint(true);

        try {
            List<NabuccoAnnotation> annotations = this
                    .extractConstraintAnnotations(nabuccoBasetype.annotationDeclaration);
            this.appendAnnotations(annotations, constraint);

            NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                    .mapToMultiplicity(nabuccoBasetype.nodeToken2.tokenImage);

            constraint.appendMultiplicityConstraint(multiplicity);

            return JavaAstModelProducer.getInstance().createLiteral(constraint.getValue(),
                    LiteralType.STRING_LITERAL);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating constraint literals.", e);
        }

    }

    /**
     * Converts all Constraint annotations of a Enumeration Declaration.
     * 
     * @param nabuccoEnumeration
     *            the annotated enumeration
     * 
     * @return the string literal containing the constraint
     * 
     * @throws JavaModelException
     */
    public Literal convertFieldConstraints(EnumerationDeclaration nabuccoEnumeration) {

        NabuccoToJavaConstraint constraint = new NabuccoToJavaDatatypeConstraint(true);

        try {
            List<NabuccoAnnotation> annotations = this
                    .extractConstraintAnnotations(nabuccoEnumeration.annotationDeclaration);
            this.appendAnnotations(annotations, constraint);

            NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                    .mapToMultiplicity(nabuccoEnumeration.nodeToken1.tokenImage);

            constraint.appendMultiplicityConstraint(multiplicity);

            return JavaAstModelProducer.getInstance().createLiteral(constraint.getValue(),
                    LiteralType.STRING_LITERAL);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error creating constraint literals.", e);
        }
    }


    /**
     * Converts all Constraint annotations of a Datatype Declaration.
     * 
     * @param nabuccoDatatype
     *            the annotated datatype
     * 
     * @return the string literal containing the constraint
     * 
     * @throws JavaModelException
     */
    public Literal convertFieldConstraints(DatatypeDeclaration nabuccoDatatype) {

        NabuccoToJavaConstraint constraint = new NabuccoToJavaDatatypeConstraint(true);

        try {
            List<NabuccoAnnotation> annotations = this
                    .extractConstraintAnnotations(nabuccoDatatype.annotationDeclaration);
            this.appendAnnotations(annotations, constraint);

            NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                    .mapToMultiplicity(nabuccoDatatype.nodeToken1.tokenImage);

            constraint.appendMultiplicityConstraint(multiplicity);

            return JavaAstModelProducer.getInstance().createLiteral(constraint.getValue(),
                    LiteralType.STRING_LITERAL);

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

        List<NabuccoAnnotation> annotations = this
                .extractConstraintAnnotations(nabuccoBasetype.annotationDeclaration);

        NabuccoToJavaConstraint constraint = new NabuccoToJavaBasetypeConstraint(false);

        this.appendAnnotations(annotations, constraint);

        // Append String Literals to the CONSTRAINTS array initializer.
        this.placeLiteral(type, JavaAstModelProducer.getInstance().createLiteral(
                constraint.getValue(), LiteralType.STRING_LITERAL));
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
    public void appendArrayLiterals(List<Literal> constraints, TypeDeclaration type)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        FieldDeclaration field = javaFactory.getJavaAstType().getField(type, CONSTRAINTS_FIELD);

        ((ArrayInitializer) field.initialization).expressions = constraints
                .toArray(new Literal[constraints.size()]);
    }

    /**
     * Extracts all constraint annotations of the annotation declaration.
     * 
     * @param annotationDeclaration
     *            the annotation declaration
     * 
     * @return the constraint annotations
     */
    private List<NabuccoAnnotation> extractConstraintAnnotations(
            AnnotationDeclaration annotationDeclaration) throws NabuccoVisitorException {
        return NabuccoAnnotationMapper.getInstance().mapToAnnotations(annotationDeclaration,
                NabuccoAnnotationGroupType.CONSTRAINT);
    }

    /**
     * Appends all constraint annotations to the given constraint object.
     * 
     * @param annotations
     *            the annotations
     * @param constraint
     *            the constraint
     * 
     * @throws JavaModelException
     */
    private void appendAnnotations(List<NabuccoAnnotation> annotations,
            NabuccoToJavaConstraint constraint) throws JavaModelException {
        
        for (NabuccoAnnotation annotation : annotations) {

            this.validateAnnotation(annotation);

            switch (annotation.getType()) {
            case MIN_LENGTH:
                constraint.appendMinLengthConstraint(annotation.getValue());
                break;

            case MAX_LENGTH:
                constraint.appendMaxLengthConstraint(annotation.getValue());
                break;
                
            case PATTERN:
                constraint.appendPatternConstraint(annotation.getValue());
                break;
            }
        }
    }

    /**
     * Validate the NABUCCO annotation.
     * 
     * @param annotation
     *            the annotation to validate.
     * 
     * @throws JavaModelException
     */
    private void validateAnnotation(NabuccoAnnotation annotation) throws JavaModelException {
        String name = annotation.getName();

        if (name == null) {
            throw new IllegalArgumentException("Annotation [null] is not valid.");
        }

        if (annotation.getGroupType() != NabuccoAnnotationGroupType.CONSTRAINT) {
            throw new IllegalArgumentException("Annotation "
                    + String.valueOf(name) + "] is not of type CONSTRAINT.");
        }
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
