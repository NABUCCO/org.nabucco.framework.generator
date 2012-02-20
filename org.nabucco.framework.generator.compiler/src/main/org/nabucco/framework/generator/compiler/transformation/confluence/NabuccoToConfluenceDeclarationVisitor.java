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
package org.nabucco.framework.generator.compiler.transformation.confluence;

import org.nabucco.framework.generator.compiler.transformation.confluence.annotation.NabuccoToConfluenceAnnotationVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.signature.NabuccoSignatureDeclarationVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableContainer;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableRow;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableSpaltenEnum;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;
import org.nabucco.framework.mda.model.text.confluence.ast.ConfluenceComposite;
import org.nabucco.framework.mda.model.text.confluence.ast.ConfluenceElement;
import org.nabucco.framework.mda.model.text.confluence.ast.format.ConfluenceLine;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeading;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeadingSize;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceLocalAnchor;
import org.nabucco.framework.mda.model.text.confluence.ast.page.ConfluencePagePart;
import org.nabucco.framework.mda.model.text.confluence.ast.table.ConfluenceTable;
import org.nabucco.framework.mda.model.text.confluence.ast.table.ConfluenceTableRow;
import org.nabucco.framework.mda.model.text.confluence.ast.table.ConfluenceTableRowContent;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceText;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceTextEffect;

/**
 * NabuccoToConfluenceDeclarationVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public abstract class NabuccoToConfluenceDeclarationVisitor extends NabuccoToConfluenceVisitor {

    protected ConfluencePagePart memberParts;

    /**
     * Creates a new {@link NabuccoToConfluenceDeclarationVisitor} instance.
     * 
     * @param visitorContext
     */
    public NabuccoToConfluenceDeclarationVisitor(NabuccoToConfluenceVisitorContext visitorContext) {
        super(visitorContext);

    }



    /**
     * Generates a local info part for a Node
     * 
     * @param name
     *            Name of a node
     * @param n
     *            Node
     * @param argu
     *            Target
     * @return Anchor for the local link
     */
    protected ConfluenceLocalAnchor generateInfoPart(String name, Node n, MdaModel<ConfluenceModel> argu,
            boolean printSignature) {
        // generate informational part
        ConfluencePagePart part = new ConfluencePagePart();
        part.addElement(new ConfluenceLine());
        ConfluenceHeading header = new ConfluenceHeading(ConfluenceHeadingSize.H4);
        header.addElement(new ConfluenceText(name));
        part.addElement(header);
        ConfluenceLocalAnchor anchor = new ConfluenceLocalAnchor(name);
        part.addElement(anchor);

        // Signature
        if (printSignature) {
            StringBuilder signatureBuilder = new StringBuilder();
            NabuccoSignatureDeclarationVisitor signatureVisitor = new NabuccoSignatureDeclarationVisitor(
                    this.getVisitorContext(), signatureBuilder);
            n.accept(signatureVisitor, argu);
            ConfluenceText signatureCode = new ConfluenceText(signatureBuilder.toString().trim(),
                    ConfluenceTextEffect.CODE);
            part.addElement(signatureCode);
            part.addElement(new ConfluenceText(ConfluenceTextEffect.NEWLINE));
        }

        // Annotations
        NabuccoToConfluenceAnnotationVisitor declarationVisitor = new NabuccoToConfluenceAnnotationVisitor(
                this.getVisitorContext(), part, false);
        declarationVisitor.setHeadingSize(ConfluenceHeadingSize.H5);

        n.accept(declarationVisitor, argu);
        this.memberParts.addElement(part);

        return anchor;
    }

    /**
     * Returns the content of the Element
     * 
     * @param n
     *            Node to print
     * @param argu
     *            target
     * @return
     */
    protected String readElementContent(Node n, MdaModel<ConfluenceModel> argu) {
        StringBuilder retVal = new StringBuilder();
        NabuccoSignatureDeclarationVisitor parameterVisitor = new NabuccoSignatureDeclarationVisitor(
                this.getVisitorContext(), retVal);
        n.accept(parameterVisitor, argu);

        return retVal.toString();
    }

    /**
     * writes all rows to a table
     * 
     * @param element
     *            element to write table
     * @param datatContainer
     *            datat to write
     */
    protected void createTable(ConfluenceComposite element, NabuccoToConfluenceTableContainer dataContainer) {
        this.createTable(element, dataContainer, null);
    }

    /**
     * writes all rows to a table
     * 
     * @param element
     *            element to write table
     * @param datatContainer
     *            datat to write
     */
    protected void createTable(ConfluenceComposite element, NabuccoToConfluenceTableContainer dataContainer,
            String header) {
        // Print header
        if (!dataContainer.getColumns().isEmpty()) {
            if (header != null) {
                ConfluenceHeading tableHeader = new ConfluenceHeading(ConfluenceHeadingSize.H4);
                tableHeader.addElement(new ConfluenceText(header));
                element.addElement(tableHeader);
            }

            ConfluenceTable table = new ConfluenceTable();
            ConfluenceTableRow headerRow = new ConfluenceTableRow(true);

            for (NabuccoToConfluenceTableSpaltenEnum column : dataContainer.getColumns()) {
                headerRow.addElement(new ConfluenceTableRowContent(new ConfluenceText(column.getValue())));
            }
            table.addElement(headerRow);

            // Iterate rows of data
            for (NabuccoToConfluenceTableRow row : dataContainer.getRows()) {
                ConfluenceTableRow tableRow = new ConfluenceTableRow(false);

                // Iterate columns
                for (NabuccoToConfluenceTableSpaltenEnum column : dataContainer.getColumns()) {
                    ConfluenceElement confElement = row.getValue(column);
                    tableRow.addElement(new ConfluenceTableRowContent(confElement));
                }

                table.addElement(tableRow);
            }

            element.addElement(table);
        }
    }

}
