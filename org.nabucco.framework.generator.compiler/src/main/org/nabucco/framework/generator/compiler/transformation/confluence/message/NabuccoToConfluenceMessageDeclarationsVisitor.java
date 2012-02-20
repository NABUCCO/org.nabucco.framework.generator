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

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceConstants;
import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceDeclarationVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.datatype.NabuccoToConfluenceDatatypeDeclarationsVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableContainer;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableRow;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableSpaltenEnum;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;
import org.nabucco.framework.mda.model.text.confluence.ast.ConfluenceComposite;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceLocalAnchor;
import org.nabucco.framework.mda.model.text.confluence.ast.page.ConfluencePagePart;

/**
 * NabuccoToConfluenceMessageDeclarationsVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceMessageDeclarationsVisitor extends NabuccoToConfluenceDeclarationVisitor {

    private ConfluenceComposite element;

    private NabuccoToConfluenceTableContainer tableContainer;

    private NabuccoAnnotationMapper mapper = NabuccoAnnotationMapper.getInstance();

    /**
     * Creates a new {@link NabuccoToConfluenceDatatypeDeclarationsVisitor} instance.
     * 
     * @param visitorContext
     */
    public NabuccoToConfluenceMessageDeclarationsVisitor(NabuccoToConfluenceVisitorContext visitorContext,
            ConfluenceComposite element) {
        super(visitorContext);
        this.element = element;

        this.memberParts = new ConfluencePagePart();
        this.tableContainer = new NabuccoToConfluenceTableContainer();
    }

    /**
     * Completes visit: creates a table
     */
    public void completeVisit() {
        super.createTable(this.element, this.tableContainer);

        this.element.addElement(this.memberParts);
    }

    @Override
    public void visit(BasetypeDeclaration n, MdaModel<ConfluenceModel> argu) {
        String description = EMPTY;
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION)) {

            description = mapper.mapToAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION).getValue();
        }

        // Set Values
        String icon = NabuccoToConfluenceConstants.BASETYPE;
        String name = n.nodeToken3.tokenImage;
        String type = n.nodeToken1.tokenImage;
        String multiplicity = n.nodeToken2.tokenImage;

        ConfluenceLocalAnchor nameAnchor = generateInfoPart(name, n, argu, true);

        // Fill the row
        NabuccoToConfluenceTableRow dataRow = new NabuccoToConfluenceTableRow(this.tableContainer,this);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ICON, icon);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.NAME, nameAnchor);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.TYPE, type);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.MULTIPLICITY, multiplicity);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.DESCRIPTION, description);
        this.tableContainer.addNewRow(dataRow);

    }

    @Override
    public void visit(DatatypeDeclaration n, MdaModel<ConfluenceModel> argu) {
        String description = EMPTY;
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION)) {

            description = mapper.mapToAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION).getValue();
        }

        // Set Values
        String icon = NabuccoToConfluenceConstants.DATATYPE;
        String name = n.nodeToken2.tokenImage;
        String type = n.nodeChoice1.choice.toString();
        String multiplicity = n.nodeToken1.tokenImage;

        ConfluenceLocalAnchor nameAnchor = generateInfoPart(name, n, argu, true);

        // Fill the row
        NabuccoToConfluenceTableRow dataRow = new NabuccoToConfluenceTableRow(this.tableContainer,this);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ICON, icon);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.NAME, nameAnchor);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.TYPE, type);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.MULTIPLICITY, multiplicity);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.DESCRIPTION, description);
        this.tableContainer.addNewRow(dataRow);
    }

    @Override
    public void visit(EnumerationDeclaration n, MdaModel<ConfluenceModel> argu) {

        String description = EMPTY;
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION)) {

            description = mapper.mapToAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION).getValue();
        }

        // Set Values
        String icon = NabuccoToConfluenceConstants.ENUM;
        String name = n.nodeToken2.tokenImage;
        String type = n.nodeChoice1.choice.toString();
        String multiplicity = n.nodeToken1.tokenImage;

        ConfluenceLocalAnchor nameAnchor = generateInfoPart(name, n, argu, true);

        // Fill the row
        NabuccoToConfluenceTableRow dataRow = new NabuccoToConfluenceTableRow(this.tableContainer,this);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ICON, icon);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.NAME, nameAnchor);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.TYPE, type);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.MULTIPLICITY, multiplicity);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.DESCRIPTION, description);
        this.tableContainer.addNewRow(dataRow);
    }

}
