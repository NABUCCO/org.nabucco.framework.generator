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
package org.nabucco.framework.generator.compiler.transformation.java.common.javadoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationGroupType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.extension.javadoc.JavadocParameter;
import org.nabucco.framework.mda.model.java.ast.extension.javadoc.JavadocSupport;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaJavadocCreator
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaJavadocCreator {

    private static final String NABUCCO_GENERATOR = "NABUCCO Generator, PRODYNA AG";

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoToJavaJavadocCreator.class);

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoToJavaJavadocCreator() {
        throw new IllegalStateException("Private constructor must not be invoked.");
    }

    /**
     * Creates javadoc for a {@link TypeDeclaration} instance.
     * 
     * @param annotationList
     *            the necessary NABUCCo javadoc annotations
     * @param typeDeclaration
     *            the type declaration to modify
     * 
     * @throws NabuccoJavadocTransformationException
     */
    public static void createJavadoc(List<NabuccoAnnotation> annotationList, TypeDeclaration typeDeclaration)
            throws NabuccoJavadocTransformationException {
        if (annotationList == null) {
            throw new IllegalArgumentException("Annotation list must be defined.");
        }
        if (typeDeclaration == null) {
            throw new IllegalArgumentException("TypeDeclaration must be defined.");
        }

        try {
            StringBuilder description = new StringBuilder();
            description.append(JavaAstElementFactory.getInstance().getJavaAstType().getTypeName(typeDeclaration));

            String name = null;
            String company = null;
            String date = null;

            List<JavadocParameter> parameterList = new ArrayList<JavadocParameter>();

            for (NabuccoAnnotation annotation : annotationList) {

                if (annotation.getGroupType() != NabuccoAnnotationGroupType.DOCUMENTATION) {
                    logger.warning("NabuccoAnnotation is not of type Javadoc: ", annotation.getName());
                    continue;
                }

                if (annotation.getType() == NabuccoAnnotationType.DESCRIPTION) {
                    description.append("<p/>");
                    description.append(annotation.getValue());
                    description.append("<p/>");

                } else if (annotation.getType() == NabuccoAnnotationType.AUTHOR) {
                    name = annotation.getValue();

                } else if (annotation.getType() == NabuccoAnnotationType.COMPANY) {
                    company = annotation.getValue();

                } else if (annotation.getType() == NabuccoAnnotationType.DATE) {
                    date = annotation.getValue();

                } else {
                    parameterList.add(JavaAstModelProducer.getInstance().createJavadocParameter(annotation.getName(),
                            annotation.getValue()));
                }
            }

            // Author
            StringBuilder author = new StringBuilder();
            author.append((name != null) ? name : "Undefined");
            author.append((company != null) ? ", " + company : "");
            author.append((date != null) ? ", " + date : "");

            JavadocParameter parameter = JavaAstModelProducer.getInstance().createJavadocParameter(
                    NabuccoAnnotationType.AUTHOR.getName(), author.toString());

            parameterList.add(parameter);

            Javadoc javadoc = JavaAstModelProducer.getInstance().createJavadoc(description.toString(), parameterList);

            typeDeclaration.javadoc = javadoc;

        } catch (JavaModelException e) {
            throw new NabuccoJavadocTransformationException("Error creating TypeDeclaration javadoc.", e);
        }
    }

    /**
     * Creates javadoc for a {@link AbstractMethodDeclaration} instance.
     * 
     * @param annotationList
     *            the necessary NABUCCo javadoc annotations
     * @param method
     *            the method declaration to modify
     * 
     * @throws NabuccoJavadocTransformationException
     */
    public static void createJavadoc(List<NabuccoAnnotation> annotationList, AbstractMethodDeclaration method)
            throws NabuccoJavadocTransformationException {
        if (annotationList == null) {
            throw new IllegalArgumentException("Annotation list must be defined.");
        }
        if (method == null) {
            throw new IllegalArgumentException("MethodDeclaration must be defined.");
        }

        String description = null;
        for (NabuccoAnnotation annotation : annotationList) {
            if (annotation.getGroupType() != NabuccoAnnotationGroupType.DOCUMENTATION) {
                logger.warning("NabuccoAnnotation is not of type Javadoc: ", annotation.getName());
                continue;
            }
            if (annotation.getType() == NabuccoAnnotationType.DESCRIPTION) {
                description = annotation.getValue();
            }
        }

        try {
            NabuccoToJavaJavadocCreator.createJavadoc(description, method);
        } catch (JavaModelException e) {
            throw new NabuccoJavadocTransformationException("Cannot create javadoc for: " + description, e);
        }

    }

    /**
     * Creates javadoc for a {@link FieldDeclaration} instance.
     * 
     * @param annotationList
     *            the necessary NABUCCo javadoc annotations
     * @param method
     *            the field declaration to modify
     * 
     * @throws NabuccoJavadocTransformationException
     */
    public static void createJavadoc(List<NabuccoAnnotation> annotationList, FieldDeclaration field)
            throws NabuccoJavadocTransformationException {
        if (annotationList == null) {
            throw new IllegalArgumentException("Annotation list must be defined.");
        }
        if (field == null) {
            throw new IllegalArgumentException("FieldDeclaration must be defined.");
        }

        for (NabuccoAnnotation annotation : annotationList) {

            if (annotation.getGroupType() != NabuccoAnnotationGroupType.DOCUMENTATION) {
                logger.warning("NabuccoAnnotation is not of type Javadoc: ", annotation.getName());
                continue;
            }

            String description = null;
            if (annotation.getType() == NabuccoAnnotationType.DESCRIPTION) {
                description = annotation.getValue();
            }

            if (description != null) {
                try {
                    Javadoc javadoc = JavaAstModelProducer.getInstance().createJavadoc(description.toString(), null);
                    field.javadoc = javadoc;
                } catch (JavaModelException e) {
                    throw new NabuccoJavadocTransformationException("Error creating FieldDeclaration javadoc.", e);
                }
            }
        }
    }

    /**
     * Creates a default javadoc for a given type declaration.
     * 
     * @param description
     *            the description text
     * @param type
     *            the type to add the javadoc
     * 
     * @throws JavaModelException
     */
    public static void createJavadoc(String description, TypeDeclaration type) throws JavaModelException {

        JavadocParameter parameter = JavaAstModelProducer.getInstance().createJavadocParameter(
                NabuccoAnnotationType.AUTHOR.getName(), NABUCCO_GENERATOR);

        Javadoc javadoc = JavaAstModelProducer.getInstance().createJavadoc(description, Arrays.asList(parameter));

        type.javadoc = javadoc;
    }

    /**
     * Creates a default javadoc for a given method declaration
     * 
     * @param description
     *            the description text
     * @param method
     *            the method to add the javadoc
     * 
     * @throws JavaModelException
     */
    public static void createJavadoc(String description, AbstractMethodDeclaration method) throws JavaModelException {

        if (description == null) {
            description = "Missing description at method " + String.valueOf(method.selector) + ".";
        }
        // creates automatically @param, @return and @throws
        List<JavadocParameter> parameterList = new ArrayList<JavadocParameter>();
        List<JavadocParameter> result = JavadocSupport.defaultArgumentsJavadocParameter(method);
        if (result != null) {
            parameterList.addAll(result);
        }
        JavadocParameter returnValue = JavadocSupport.defaultReturnJavadocParameter(method);
        if (returnValue != null) {
            parameterList.add(returnValue);
        }
        result = JavadocSupport.defaultExceptionsJavadocParameter(method);
        if (result != null) {
            parameterList.addAll(result);
        }

        Javadoc javadoc = JavaAstModelProducer.getInstance().createJavadoc(description.toString(), parameterList);
        method.javadoc = javadoc;
    }
}
