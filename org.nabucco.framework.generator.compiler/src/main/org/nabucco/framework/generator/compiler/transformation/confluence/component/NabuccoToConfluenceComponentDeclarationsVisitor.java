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
package org.nabucco.framework.generator.compiler.transformation.confluence.component;

import org.nabucco.framework.generator.compiler.transformation.NabuccoConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceConstants;
import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceDeclarationVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.datatype.NabuccoToConfluenceDatatypeDeclarationsVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableContainer;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableRow;
import org.nabucco.framework.generator.compiler.transformation.confluence.utils.NabuccoToConfluenceTableSpaltenEnum;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;
import org.nabucco.framework.mda.model.text.confluence.ast.ConfluenceComposite;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceLocalAnchor;
import org.nabucco.framework.mda.model.text.confluence.ast.page.ConfluencePagePart;

/**
 * NabuccoToConfluenceComponentDeclarationVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceComponentDeclarationsVisitor extends NabuccoToConfluenceDeclarationVisitor {

    private ConfluenceComposite element;

    private NabuccoToConfluenceTableContainer tableContainerDatatypes;

    private NabuccoToConfluenceTableContainer tableContainerComponents;

    private NabuccoToConfluenceTableContainer tableContainerServices;

    private NabuccoAnnotationMapper mapper = NabuccoAnnotationMapper.getInstance();

    /**
     * Creates a new {@link NabuccoToConfluenceDatatypeDeclarationsVisitor} instance.
     * 
     * @param visitorContext
     */
    public NabuccoToConfluenceComponentDeclarationsVisitor(NabuccoToConfluenceVisitorContext visitorContext,
            ConfluenceComposite element) {
        super(visitorContext);
        this.element = element;

        this.memberParts = new ConfluencePagePart();

        this.tableContainerDatatypes = new NabuccoToConfluenceTableContainer();
        this.tableContainerComponents = new NabuccoToConfluenceTableContainer();
        this.tableContainerServices = new NabuccoToConfluenceTableContainer();
    }

    /**
     * Completes visit: creates a table
     */
    public void completeVisit() {

        super.createTable(this.element, this.tableContainerDatatypes, NabuccoConstants.DATATYPE);

        super.createTable(this.element, this.tableContainerComponents, NabuccoConstants.COMPONENT);

        super.createTable(this.element, this.tableContainerServices, NabuccoConstants.SERVICE);

        this.element.addElement(this.memberParts);
    }

    @Override
    public void visit(ComponentDatatypeDeclaration n, MdaModel<ConfluenceModel> argu) {

        String description = EMPTY;
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION)) {

            description = mapper.mapToAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION).getValue();
        }
        String persistence = null;

        if (n.nodeOptional.present()) {
            persistence = n.nodeOptional.node.toString();
        }

        // Set Values
        String icon = NabuccoToConfluenceConstants.DATATYPE;
        String access = n.nodeChoice.choice.toString();
        String multiplicity = n.nodeToken1.tokenImage;
        String name = n.nodeToken2.tokenImage;
        String type = n.nodeChoice1.choice.toString();

        ConfluenceLocalAnchor nameAnchor = generateInfoPart(name, n, argu, true);

        // Fill the row
        NabuccoToConfluenceTableRow dataRow = new NabuccoToConfluenceTableRow(this.tableContainerDatatypes, this);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ICON, icon);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ACCESS, access);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.TYPE, type);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.NAME, nameAnchor);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.PERSISTENCE, persistence);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.MULTIPLICITY, multiplicity);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.DESCRIPTION, description);
        this.tableContainerDatatypes.addNewRow(dataRow);
    }

    @Override
    public void visit(ServiceDeclaration n, MdaModel<ConfluenceModel> argu) {

        String description = EMPTY;
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION)) {

            description = mapper.mapToAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION).getValue();
        }
        // Set Values
        String icon = NabuccoToConfluenceConstants.SERVICE;
        String access = n.nodeChoice.choice.toString();
        String type = n.nodeToken1.tokenImage;

        generateInfoPart(type, n, argu, true);

        // Fill the row
        NabuccoToConfluenceTableRow dataRow = new NabuccoToConfluenceTableRow(this.tableContainerServices, this);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ICON, icon);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ACCESS, access);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.TYPE, type);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.DESCRIPTION, description);
        this.tableContainerServices.addNewRow(dataRow);
    }

    @Override
    public void visit(ComponentDeclaration n, MdaModel<ConfluenceModel> argu) {

        String description = EMPTY;
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION)) {

            description = mapper.mapToAnnotation(n.annotationDeclaration, NabuccoAnnotationType.DESCRIPTION).getValue();
        }
        // Set Values
        String icon = NabuccoToConfluenceConstants.COMPONENT;
        String access = n.nodeChoice.choice.toString();
        String type = n.nodeToken1.tokenImage;
        String name = n.nodeToken2.tokenImage;

        ConfluenceLocalAnchor nameAnchor = generateInfoPart(name, n, argu, true);

        // Fill the row
        NabuccoToConfluenceTableRow dataRow = new NabuccoToConfluenceTableRow(this.tableContainerComponents, this);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ICON, icon);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.ACCESS, access);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.TYPE, type);
        dataRow.addLink(NabuccoToConfluenceTableSpaltenEnum.NAME, nameAnchor);
        dataRow.addText(NabuccoToConfluenceTableSpaltenEnum.DESCRIPTION, description);
        this.tableContainerComponents.addNewRow(dataRow);
    }

}
