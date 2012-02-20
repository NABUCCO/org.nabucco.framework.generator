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
package org.nabucco.framework.generator.compiler.transformation.confluence.enumeration;

import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceConstants;
import org.nabucco.framework.generator.compiler.transformation.confluence.annotation.NabuccoToConfluenceAnnotationVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;
import org.nabucco.framework.mda.model.text.confluence.ast.format.ConfluenceLine;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeading;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeadingSize;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceText;

/**
 * NabuccoToConfluenceEnumerationVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceEnumerationVisitor extends NabuccoToConfluenceVisitor {


    /**
     * Creates a new {@link NabuccoToConfluenceDatatypeVisitor} instance.
     * 
     * @param visitorContext
     *            the confluence visitor context
     */
    public NabuccoToConfluenceEnumerationVisitor(NabuccoToConfluenceVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EnumerationStatement nabuccoEnumeration, MdaModel<ConfluenceModel> target) {

        super.visit(nabuccoEnumeration, target);

        String enumerationName = nabuccoEnumeration.nodeToken2.tokenImage;

        // Create and init a new page
        super.initPage(enumerationName,DATATYPE, target);

        // Print page header with package
        super.printPageHeader(NabuccoModelType.ENUMERATION, enumerationName);

        // Add description part
        ConfluenceLine descLine = new ConfluenceLine();
        this.page.addElement(descLine);

        // Print the original signature
        super.printOriginalSignature(nabuccoEnumeration, target);

        // Print description and annotation
        NabuccoToConfluenceAnnotationVisitor annotationVisitor = new NabuccoToConfluenceAnnotationVisitor(
                this.getVisitorContext(), this.page, true);
        nabuccoEnumeration.annotationDeclaration.accept(annotationVisitor, target);

        // Add members part
        this.page.addElement(descLine);
        ConfluenceHeading membersPart = new ConfluenceHeading(ConfluenceHeadingSize.H3);
        membersPart.addElement(new ConfluenceText(NabuccoToConfluenceConstants.LITERALS));
        this.page.addElement(membersPart);

        NabuccoToConfluenceEnumerationsDeclarationsVisitor declarationsVisitor = new NabuccoToConfluenceEnumerationsDeclarationsVisitor(
                this.getVisitorContext(), this.page);

        nabuccoEnumeration.accept(declarationsVisitor, target);
        declarationsVisitor.completeVisit();
    }
}
