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
package org.nabucco.framework.generator.compiler.transformation.common.annotation;

import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;

/**
 * NabuccoAnnotationMapper
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoAnnotationMapper {

    /** The logger */
    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoAnnotationMapper.class);

    /** Singleton instance. */
    private static NabuccoAnnotationMapper instance = new NabuccoAnnotationMapper();

    /** Private constructor. */
    private NabuccoAnnotationMapper() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoAnnotationMapper instance.
     */
    public static NabuccoAnnotationMapper getInstance() {
        return instance;
    }

    /**
     * Mapps the annotation declaration to a list of {@link NabuccoAnnotation}.
     * 
     * @param annotationDeclaration
     *            the annotation declaration
     * 
     * @param types
     *            optional list of {@link NabuccoAnnotationGroupType} to filter by
     * 
     * @return the list of annotations, probably filtered by the types
     */
    public List<NabuccoAnnotation> mapToAnnotations(AnnotationDeclaration annotationDeclaration,
            NabuccoAnnotationGroupType... types) {

        if (annotationDeclaration == null) {
            throw new IllegalArgumentException("Annotation Declaration is not valid.");
        }

        List<NabuccoAnnotation> annotationList = new ArrayList<NabuccoAnnotation>();

        for (Node node : annotationDeclaration.nodeListOptional.nodes) {
            if (node instanceof NodeToken) {
                String annotationName = ((NodeToken) node).tokenImage;
                NabuccoAnnotation annotation = this.mapStringToAnnotation(annotationName);

                if (annotation == null) {
                    logger.error("Cannot resolve annotation [", annotationName, "].");
                }

                if (annotation != null) {
                    if (types.length == 0) {
                        annotationList.add(annotation);
                    } else {
                        for (NabuccoAnnotationGroupType type : types) {
                            if (annotation.getGroupType() == type) {
                                annotationList.add(annotation);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return annotationList;
    }

    /**
     * Mapps the annotation declaration to a {@link NabuccoAnnotation}.
     * 
     * @param annotationDeclaration
     *            the annotation declaration
     * @param type
     *            the annotation type
     * 
     * @return the annotation, filtered by the constant name, or null
     */
    public NabuccoAnnotation mapToAnnotation(AnnotationDeclaration annotationDeclaration,
            NabuccoAnnotationType type) {

        for (Node node : annotationDeclaration.nodeListOptional.nodes) {

            String annotationName = ((NodeToken) node).tokenImage;

            if (node instanceof NodeToken && annotationName.startsWith(type.getName())) {
                NabuccoAnnotation annotation = this.mapStringToAnnotation(annotationName);

                if (annotation == null) {
                    logger.error("Cannot resolve annotation [", annotationName, "].");
                }

                return annotation;
            }
        }
        return null;
    }

    /**
     * Mapps the list of annotation declarations to a {@link NabuccoAnnotation}.
     * 
     * @param annotationDeclarationList
     *            the list of annotation declaration
     * 
     * @param constant
     *            {@link NabuccoAnnotationConstants} to filter by
     * 
     * @return the annotation, filtered by the constant name
     */
    public NabuccoAnnotation mapToAnnotation(List<NabuccoAnnotation> annotationDeclarationList,
            NabuccoAnnotationType type) {

        for (NabuccoAnnotation nabuccoAnnotation : annotationDeclarationList) {
            if (nabuccoAnnotation.getType() == type) {
                return nabuccoAnnotation;
            }
        }
        return null;
    }

    /**
     * Checks if a given annotation is present in a given annotation declaration.
     * 
     * @param annotationDeclaration
     *            a annotation declaration
     * @param types
     *            a list of types to filter for
     * 
     * @return <code>true</code> only if the annotation is present, else <code>false</code>
     */
    public boolean hasAnnotation(AnnotationDeclaration annotationDeclaration,
            NabuccoAnnotationType... types) {
        for (NabuccoAnnotationType type : types) {
            if (mapToAnnotation(annotationDeclaration, type) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a string representation is a valid annotation.
     * 
     * @param annotationName
     *            name of the annotation
     * 
     * @return <b>true</b> if the name is a defined annotation, <b>false</b> if not
     */
    public boolean isAnnotation(String annotationName) {
        return this.mapStringToAnnotation(annotationName) != null;
    }

    /**
     * Mapps the annotation string to a {@link NabuccoAnnotation} instance.
     * 
     * @param annotationString
     *            the annotation string
     * 
     * @return the annotation instance containing key, value and type of the annotation
     */
    private NabuccoAnnotation mapStringToAnnotation(String annotationString) {

        for (NabuccoAnnotationType type : NabuccoAnnotationType.values()) {

            String annotation = type.getName();

            if (isAnnotation(annotationString, annotation)) {
                return new NabuccoAnnotation(type, trim(annotationString, annotation));
            }
        }

        return null;
    }

    /**
     * Check whether the given string string is of the given annotation type.
     * 
     * @param current
     *            the string to check
     * @param annotation
     *            the annotation key
     * 
     * @return <b>true</b> if it is the annotation, <b>false</b> if not
     */
    private boolean isAnnotation(String current, String annotation) {
        if (!(current.startsWith(annotation))) {
            return false;
        }
        if (current.length() == annotation.length()) {
            return true;
        }
        if (Character.isWhitespace(current.charAt(annotation.length()))) {
            return true;
        }
        return false;
    }

    /**
     * Removes the leading annotation name, leading and trailing whitespace or tabs and enclosing
     * quotes.
     * 
     * @param annotation
     *            the annotation string to trim
     * @param key
     *            the annotation name to remove
     * 
     * @return the trimmed value of the annotation
     */
    private String trim(String annotation, String key) {
        return annotation.replace(key, "").replace("\t", "").trim().replace("\"", "");
    }
}
