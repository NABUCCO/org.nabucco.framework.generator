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

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.FetchStrategyType;
import org.nabucco.framework.generator.compiler.transformation.common.constants.ComponentRelationConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;

/**
 * NabuccoToXmlDatatypeEntityVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlComponentRelationEntityVisitor extends NabuccoToXmlVisitorSupport implements PersistenceConstants,
        ComponentRelationConstants {

    private String componentPrefix;

    /**
     * Creates a new {@link NabuccoToXmlComponentRelationEntityVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlComponentRelationEntityVisitor(NabuccoToXmlVisitorContext visitorContext, String componentPrefix) {
        super(visitorContext);
        this.componentPrefix = componentPrefix;
    }

    @Override
    public void visit(DatatypeStatement nabuccoDatatype, MdaModel<XmlModel> target) {

        String targetName = nabuccoDatatype.nodeToken2.tokenImage;
        String relationName = targetName + COMPONENT_RELATION;

        // Visit sub-nodes first!
        super.visit(nabuccoDatatype, target);

        String componentName = super.getProjectName(null, null);

        try {
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.ORM_FRAGMENT_TEMPLATE);

            document.getDocument().getDocumentElement().setAttribute(NAME, relationName);
            document.getDocument().getDocumentElement().setAttribute(ORDER, FRAGMENT_ORDER_ENTITY);

            Element entity = this.createEntity(targetName);

            document.getDocument().getDocumentElement().appendChild(document.getDocument().importNode(entity, true));

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during XML template datatype processing.", te);
        }
    }

    /**
     * Create an component relation entity XML tag for the currently visited datatype.
     * 
     * @param targetName
     *            name of the entity
     * 
     * @return the XML entity element
     * 
     * @throws XmlTemplateException
     */
    private Element createEntity(String targetName) throws XmlTemplateException {

        XmlTemplate ormTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.ORM_TEMPLATE);

        String pkg = this.getVisitorContext().getPackage();
        String targetImport = pkg + PKG_SEPARATOR + targetName;
        String relationImport = targetImport + COMPONENT_RELATION;

        Element entity = (Element) ormTemplate.copyNodesByXPath(XPATH_ENTITY).get(0);
        entity.setAttribute(CLASS, relationImport);

        String tableName = this.componentPrefix
                + TABLE_SEPARATOR + NabuccoTransformationUtility.toTableName(targetName + COMPONENT_RELATION);

        Element table = (Element) entity.getElementsByTagName(TABLE).item(0);
        table.setAttribute(NAME, tableName);

        Element attributes = (Element) entity.getElementsByTagName(ATTRIBUTES).item(0);
        while (attributes.getChildNodes().getLength() > 0) {
            attributes.removeChild(attributes.getChildNodes().item(0));
        }

        Element relation = (Element) ormTemplate.copyNodesByXPath(XPATH_ONE_TO_ONE).get(1);

        relation.setAttribute(NAME, CR_TARGET);
        relation.setAttribute(TARGET_ENTITY, targetImport);
        relation.setAttribute(FETCH, FetchStrategyType.EAGER.getId());

        Element joinColumn = (Element) relation.getElementsByTagName(JOIN_COLUMN).item(0);
        joinColumn.setAttribute(NAME, CR_TARGET_ID);

        attributes.appendChild(relation);

        return entity;
    }

}
