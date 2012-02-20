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
package org.nabucco.framework.generator.compiler.transformation.confluence.basetype;

import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceConstants;
import org.nabucco.framework.generator.compiler.transformation.confluence.annotation.NabuccoToConfluenceAnnotationVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;
import org.nabucco.framework.mda.model.text.confluence.ast.format.ConfluenceLine;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeading;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeadingSize;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceLink;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceText;

/**
 * NabuccoToConfluenceBasetypeVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceBasetypeVisitor extends NabuccoToConfluenceVisitor {

    /**
     * Creates a new {@link NabuccoToConfluenceBasetypeVisitor} instance.
     * 
     * @param visitorContext
     */
    public NabuccoToConfluenceBasetypeVisitor(NabuccoToConfluenceVisitorContext visitorContext) {
        super(visitorContext);
    }


    @Override
    public void visit(BasetypeStatement nabuccoBasetype, MdaModel<ConfluenceModel> target) {
        super.visit(nabuccoBasetype, target);

        String basetypeName = nabuccoBasetype.nodeToken2.tokenImage;
        
        // Create and init a new page
        super.initPage(basetypeName,DATATYPE, target);

        // Print page header with package
        super.printPageHeader(NabuccoModelType.BASETYPE, basetypeName);

        // Add superclass Part
        ConfluenceHeading superclassPart = new ConfluenceHeading(ConfluenceHeadingSize.H3);
        superclassPart.addElement(new ConfluenceText(NabuccoToConfluenceConstants.INHERITED_FROM));
        String extentionString = this.getVisitorContext().getNabuccoExtension();
        this.page.addElement(superclassPart);

        if (extentionString != null && extentionString.isEmpty() == false) {
            String namespace = this.findComponentToElement(extentionString);
            ConfluenceLink extentionLink = new ConfluenceLink(extentionString, namespace, extentionString,
                    extentionString);
            this.page.addElement(extentionLink);
        }

        // Add description part
        ConfluenceLine descLine = new ConfluenceLine();
        this.page.addElement(descLine);

        // Print the original signature
        super.printOriginalSignature(nabuccoBasetype, target);
        
        // Print description and annotation
        NabuccoToConfluenceAnnotationVisitor annotationVisitor = new NabuccoToConfluenceAnnotationVisitor(
                this.getVisitorContext(), this.page, true);
        nabuccoBasetype.annotationDeclaration.accept(annotationVisitor, target);

    }

}
