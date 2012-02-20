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
package org.nabucco.framework.generator.compiler.transformation.common.annotation.constraint;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.visitor.DepthFirstVisitor;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;

/**
 * NabuccoConstraintAnnotationExtractor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoConstraintAnnotationExtractor extends DepthFirstVisitor {

    private static final Integer DEFAULT_MIN_LENGTH = 0;

    private static final Integer DEFAULT_MAX_LENGTH = 255;

    private String minLength;

    private String maxLength;

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoConstraintAnnotationExtractor.class);

    @Override
    public void visit(BasetypeStatement nabuccoBasetype) {

        String name = nabuccoBasetype.nodeToken2.tokenImage;

        AnnotationDeclaration annotations = nabuccoBasetype.annotationDeclaration;

        this.minLength = extractLength(name, annotations, NabuccoAnnotationType.MIN_LENGTH, DEFAULT_MIN_LENGTH);

        this.maxLength = extractLength(name, annotations, NabuccoAnnotationType.MAX_LENGTH, DEFAULT_MAX_LENGTH);

        super.visit(nabuccoBasetype);
    }

    /**
     * Extracts the length of the given annotation.
     * 
     * @param name
     *            name of the basetype
     * @param annotations
     *            the annotation to extract
     * @param type
     *            the annotation type
     * @param defaultValue
     *            the default value (when min/max cannot be figured out)
     * 
     * @return the appropriate length for the given annotation
     */
    private String extractLength(String name, AnnotationDeclaration annotations, NabuccoAnnotationType type,
            final int defaultValue) {

        NabuccoAnnotation annotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotations, type);

        if (annotation != null && annotation.getValue() != null) {

            String annotationName = annotation.getName();

            try {
                int length = Integer.parseInt(annotation.getValue());

                if (type == NabuccoAnnotationType.MIN_LENGTH && length < defaultValue) {
                    logger.warning("Value for @", annotationName, " annotation at Basetype ", name,
                            " is smaller than default " + defaultValue, ".");
                }

                if (type == NabuccoAnnotationType.MAX_LENGTH && length > defaultValue) {
                    logger.warning("Value for @", annotationName, " annotation at Basetype ", name,
                            " is larger than default " + defaultValue, ".");
                }

                return String.valueOf(length);

            } catch (NumberFormatException e) {
                logger.warning("Cannot parse @", annotationName, " [", annotation.getValue(), "]. ", e.getMessage());
            }
        }

        return String.valueOf(defaultValue);
    }

    /**
     * Getter for the minLength.
     * 
     * @return Returns the minLength.
     */
    public String getMinLength() {
        return this.minLength;
    }

    /**
     * Getter for the maxLength.
     * 
     * @return Returns the maxLength.
     */
    public String getMaxLength() {
        return this.maxLength;
    }

}
