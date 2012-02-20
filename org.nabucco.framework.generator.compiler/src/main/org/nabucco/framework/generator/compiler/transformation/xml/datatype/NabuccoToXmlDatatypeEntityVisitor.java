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
package org.nabucco.framework.generator.compiler.transformation.xml.datatype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.datatype.comparator.OrmEntityComparator;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * NabuccoToXmlDatatypeEntityVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlDatatypeEntityVisitor extends NabuccoToXmlDatatypeVisitor implements PersistenceConstants {

    private String entityName;

    private List<Node> elementList = new ArrayList<Node>();

    private String componentPrefix;

    /**
     * Creates a new {@link NabuccoToXmlDatatypeEntityVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param collector
     *            the visitor collector (must be closed after visitation)
     * @param rootPackage
     *            the root package of the XML transformation (the starting element)
     * @param componentPrefix
     *            the component prefix
     */
    public NabuccoToXmlDatatypeEntityVisitor(NabuccoToXmlVisitorContext visitorContext,
            NabuccoToXmlDatatypeCollector collector, String rootPackage, String componentPrefix) {
        super(visitorContext, collector, rootPackage);
        this.componentPrefix = componentPrefix;
    }

    @Override
    public void visit(ExtensionDeclaration nabuccoExtension, MdaModel<XmlModel> target) {

        // Set the component name to the current component.
        String currentComponent = super.getProjectName(null, null);
        super.setComponentName(currentComponent);

        super.visit(nabuccoExtension, target);
    }

    @Override
    public void visit(DatatypeStatement nabuccoDatatype, MdaModel<XmlModel> target) {

        this.entityName = nabuccoDatatype.nodeToken2.tokenImage;

        // Visit sub-nodes first!
        super.visit(nabuccoDatatype, target);

        String componentName = super.getProjectName(null, null);

        try {
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.ORM_FRAGMENT_TEMPLATE);

            document.getDocument().getDocumentElement().setAttribute(NAME, this.entityName);
            document.getDocument().getDocumentElement().setAttribute(ORDER, FRAGMENT_ORDER_ENTITY);

            // Ref IDs
            this.createParentRefIds(target);

            Element datatypeEntity = this.createEntity(nabuccoDatatype);

            document.getDocument().getDocumentElement()
                    .appendChild(document.getDocument().importNode(datatypeEntity, true));

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            String entityImport = super.getVisitorContext().getPackage() + PKG_SEPARATOR + this.entityName;

            super.collector.addEntity(entityImport, document);

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during XML template datatype processing.", te);
        }
    }

    /**
     * Create the reference IDs for the datatypes parent datatypes.
     * 
     * @param target
     *            the java target
     */
    private void createParentRefIds(MdaModel<XmlModel> target) {
        NabuccoModel parent = super.getParent();

        if (parent == null) {
            return;
        }

        String pkg = super.getVisitorContext().getPackage();
        if (!NabuccoCompilerSupport.isOtherComponent(pkg, parent.getPackage())) {
            return;
        }

        NabuccoToXmlDatatypeRefIdVisitor visitor = new NabuccoToXmlDatatypeRefIdVisitor(super.getVisitorContext());
        parent.getUnit().accept(visitor, target);

        this.elementList.addAll(visitor.getElementList());
    }

    /**
     * Create an entity XML tag for the currently visited datatype.
     * 
     * @param nabuccoDatatype
     *            the datatype statement
     * 
     * @return the XML element
     * 
     * @throws XmlTemplateException
     */
    private Element createEntity(DatatypeStatement nabuccoDatatype) throws XmlTemplateException {

        XmlTemplate ormTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.ORM_TEMPLATE);

        String pkg = this.getVisitorContext().getPackage();
        Element entity = (Element) ormTemplate.copyNodesByXPath(XPATH_ENTITY).get(0);
        entity.setAttribute(CLASS, pkg + PKG_SEPARATOR + this.entityName);

        String tableName = this.componentPrefix
                + TABLE_SEPARATOR + NabuccoTransformationUtility.toTableName(this.entityName);

        // Discriminator Column
        NabuccoToXmlDiscriminatorVisitor discriminatorVisitor = new NabuccoToXmlDiscriminatorVisitor(ormTemplate);
        nabuccoDatatype.accept(discriminatorVisitor, entity);

        ((Element) entity.getElementsByTagName(TABLE).item(0)).setAttribute(NAME, tableName);

        Element attributes = (Element) entity.getElementsByTagName(ATTRIBUTES).item(0);
        NabuccoToXmlDatatypeVisitorSupport.mergeAttributeNodes(attributes, this.elementList);

        NabuccoToXmlDatatypeVisitorSupport.sortNodes(entity, OrmEntityComparator.getInstance());

        return entity;
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<XmlModel> target) {
        super.createEntityRelation(nabuccoDatatype, this.elementList, this.entityName, this.componentPrefix);
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBasetype, MdaModel<XmlModel> target) {
        super.createBasetypeRelation(nabuccoBasetype, this.elementList);
    }

    @Override
    public void visit(EnumerationDeclaration nabuccoEnum, MdaModel<XmlModel> target) {
        super.createEnumRelation(nabuccoEnum, this.elementList);
    }

}
