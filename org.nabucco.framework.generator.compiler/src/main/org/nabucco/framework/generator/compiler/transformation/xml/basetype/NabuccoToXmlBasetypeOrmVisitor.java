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
package org.nabucco.framework.generator.compiler.transformation.xml.basetype;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;

/**
 * NabuccoToXmlBasetypeOrmVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlBasetypeOrmVisitor extends NabuccoToXmlVisitorSupport implements PersistenceConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoToXmlBasetypeOrmVisitor.class);

    /** List for already visited basetypes */
    private Set<String> visitedBasetypes = new HashSet<String>();

    private String componentName;

    /**
     * Creates a new {@link NabuccoToXmlBasetypeOrmVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param componentName
     *            name of the component
     */
    public NabuccoToXmlBasetypeOrmVisitor(NabuccoToXmlVisitorContext visitorContext, String componentName) {
        super(visitorContext);
        this.componentName = componentName;
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBasetype, MdaModel<XmlModel> target) {

        // BasetypeDeclarations do not have any sub-nodes.

        String type = nabuccoBasetype.nodeToken1.tokenImage;
        String qualifiedType = super.resolveImport(type);

        // Do not create multiple basetype embeddables
        if (this.visitedBasetypes.contains(qualifiedType)) {
            return;
        }

        String componentName = this.componentName;
        if (componentName == null) {
            componentName = super.getProjectName(null, null);
        }

        try {
            XmlDocument document = super.extractDocument(NabuccoXmlTemplateConstants.ORM_FRAGMENT_TEMPLATE);

            document.getDocument().getDocumentElement().setAttribute(NAME, type);
            document.getDocument().getDocumentElement().setAttribute(ORDER, FRAGMENT_ORDER_EMBEDDABLE);

            Element embeddableElement = this.createEmbeddable(qualifiedType);

            document.getDocument().getDocumentElement()
                    .appendChild(document.getDocument().importNode(embeddableElement, true));

            // File creation
            document.setProjectName(componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

            this.visitedBasetypes.add(qualifiedType);

        } catch (XmlTemplateException te) {
            logger.error(te, "Error during XML template datatype processing.");
            throw new NabuccoVisitorException("Error during XML template datatype processing.", te);
        }

    }

    /**
     * Create an embeddable XML tag for the current basetype.
     * 
     * @param qualifiedType
     *            the qualified basetype type
     * 
     * @return the XML element
     * 
     * @throws XmlTemplateException
     */
    private Element createEmbeddable(String qualifiedType) throws XmlTemplateException {

        XmlTemplate ormTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.ORM_TEMPLATE);
        Element embeddable = (Element) ormTemplate.copyNodesByXPath(XPATH_EMBEDDABLE).get(0);
        embeddable.setAttribute(CLASS, qualifiedType);

        return embeddable;
    }
}
