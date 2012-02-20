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
package org.nabucco.framework.generator.compiler.transformation.confluence.message;

import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceConstants;
import org.nabucco.framework.generator.compiler.transformation.confluence.annotation.NabuccoToConfluenceAnnotationVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;
import org.nabucco.framework.mda.model.text.confluence.ast.format.ConfluenceLine;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeading;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeadingSize;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceText;

/**
 * NabuccoToConfluenceMessageVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceMessageVisitor extends NabuccoToConfluenceVisitor {

    /**
     * Creates a new {@link NabuccoToConfluenceMessageVisitor} instance.
     * 
     * @param visitorContext
     *            the confluence visitor context
     */
    public NabuccoToConfluenceMessageVisitor(NabuccoToConfluenceVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(MessageStatement nabuccoMessage, MdaModel<ConfluenceModel> target) {

        super.visit(nabuccoMessage, target);

        String messageName = nabuccoMessage.nodeToken2.tokenImage;

        // Create and init a new page
        super.initPage(messageName, MESSAGE, target);

        // Print page header with package
        super.printPageHeader(NabuccoModelType.MESSAGE, messageName);

        // Add description part
        ConfluenceLine descLine = new ConfluenceLine();
        this.page.addElement(descLine);

        // Print the original signature
        super.printOriginalSignature(nabuccoMessage, target);

        // Print description and annotation
        NabuccoToConfluenceAnnotationVisitor annotationVisitor = new NabuccoToConfluenceAnnotationVisitor(
                this.getVisitorContext(), this.page, true);
        nabuccoMessage.annotationDeclaration.accept(annotationVisitor, target);

        // Add members part
        this.page.addElement(descLine);
        ConfluenceHeading membersPart = new ConfluenceHeading(ConfluenceHeadingSize.H3);
        membersPart.addElement(new ConfluenceText(NabuccoToConfluenceConstants.MEMBERS));
        this.page.addElement(membersPart);

        NabuccoToConfluenceMessageDeclarationsVisitor declarationsVisitor = new NabuccoToConfluenceMessageDeclarationsVisitor(
                this.getVisitorContext(), this.page);

        nabuccoMessage.accept(declarationsVisitor, target);
        declarationsVisitor.completeVisit();
    }
}
