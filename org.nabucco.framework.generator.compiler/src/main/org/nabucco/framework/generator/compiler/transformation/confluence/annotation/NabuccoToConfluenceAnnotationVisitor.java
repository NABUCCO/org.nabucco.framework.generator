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
package org.nabucco.framework.generator.compiler.transformation.confluence.annotation;

import java.util.List;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;
import org.nabucco.framework.mda.model.text.confluence.ast.ConfluenceComposite;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeading;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeadingSize;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceExternalLink;
import org.nabucco.framework.mda.model.text.confluence.ast.metadatalist.ConfluenceMetadataList;
import org.nabucco.framework.mda.model.text.confluence.ast.metadatalist.ConfluenceMetadataListContent;
import org.nabucco.framework.mda.model.text.confluence.ast.metadatalist.ConfluenceMetadataListElement;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceText;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceTextEffect;

/**
 * NabuccoToConfluenceAnnotationVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceAnnotationVisitor extends NabuccoToConfluenceVisitor {

    /**
     * Comment for <code>NO_DESCRIPTION</code>
     */
    private static final String NO_DESCRIPTION = "No description";

    private ConfluenceHeadingSize headingSize = ConfluenceHeadingSize.H3;

    /**
     * The element where the annotation should be appended
     */
    private ConfluenceComposite element;

    private static final String METADATA = "Metadata";

    private final static String annotationUrl = "http://www.nabucco-source.org/confluence/display/NBCF/";

    /**
     * Shows if the description in the annotation should become except mark
     */
    private boolean excerpt;

    /**
     * Creates a new {@link NabuccoToConfluenceAnnotationVisitor} instance.
     * 
     * @param visitorContext
     * @param element
     *            The element where the annotation should be appended
     * @param excerpt
     *            the anotation should have exceprt part
     */
    public NabuccoToConfluenceAnnotationVisitor(NabuccoToConfluenceVisitorContext visitorContext,
            ConfluenceComposite element, boolean excerpt) {
        super(visitorContext);
        this.element = element;
        this.excerpt = excerpt;

    }

    @Override
    public void visit(AnnotationDeclaration annotations, MdaModel<ConfluenceModel> argu) {
        List<NabuccoAnnotation> javaAnnotations = NabuccoAnnotationMapper.getInstance()
                .mapToAnnotationList(annotations);

        ConfluenceMetadataList confluenceMetadataList = new ConfluenceMetadataList();

        ConfluenceText description;
        if (this.excerpt) {
            description = new ConfluenceText(NO_DESCRIPTION, ConfluenceTextEffect.EXCERPT);
        } else {
            description = new ConfluenceText(NO_DESCRIPTION);
        }

        for (NabuccoAnnotation annotation : javaAnnotations) {
            if (annotation.getType() == NabuccoAnnotationType.DESCRIPTION) {
                description.setValue(annotation.getValue());
            } else {
                ConfluenceMetadataListElement confluenceMetadataListElement = new ConfluenceMetadataListElement();

                String annotantionName = annotation.getName();
                String annotantionLink = this.getAnnotationLinkURL(annotantionName);
                ConfluenceMetadataListContent annotationName = new ConfluenceMetadataListContent(
                        new ConfluenceExternalLink(annotantionLink, annotantionName));
                confluenceMetadataListElement.addElement(annotationName);

                ConfluenceMetadataListContent annotationValue = new ConfluenceMetadataListContent(new ConfluenceText(
                        annotation.getValue()));
                confluenceMetadataListElement.addElement(annotationValue);

                confluenceMetadataList.addElement(confluenceMetadataListElement);
            }
        }

        this.element.addElement(description);

        if (!confluenceMetadataList.getChildren().isEmpty()) {
            ConfluenceHeading membersHeading = new ConfluenceHeading(this.headingSize);
            membersHeading.addElement(new ConfluenceText(METADATA));
            this.element.addElement(membersHeading);

            this.element.addElement(confluenceMetadataList);
        }
    }

    /**
     * Initialize map with urls to the annotation information
     * 
     * Returns the url to be shown for the given url
     */
    private String getAnnotationLinkURL(String annotation) {
        return annotationUrl + annotation;
    }

    /**
     * Setter for the headingSize.
     * 
     * @param headingSize
     *            The headingSize to set.
     */
    public void setHeadingSize(ConfluenceHeadingSize headingSize) {
        this.headingSize = headingSize;
    }

    /**
     * Getter for the headingSize.
     * 
     * @return Returns the headingSize.
     */
    public ConfluenceHeadingSize getHeadingSize() {
        return this.headingSize;
    }

}
