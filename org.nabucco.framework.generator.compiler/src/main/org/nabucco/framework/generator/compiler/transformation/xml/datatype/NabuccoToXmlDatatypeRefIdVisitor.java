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

import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * NabuccoToXmlDatatypeRefIdVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlDatatypeRefIdVisitor extends NabuccoToXmlVisitorSupport implements PersistenceConstants {

    private List<Element> elementList = new ArrayList<Element>();

    /**
     * Creates a new {@link NabuccoToXmlDatatypeRefIdVisitor} instance.
     * 
     * @param context
     *            the visitor context
     */
    public NabuccoToXmlDatatypeRefIdVisitor(NabuccoToXmlVisitorContext context) {
        super(createContext(context));
    }

    /**
     * Prepare the visitor context for the current traversion.
     * 
     * @param context
     *            the existing context
     * 
     * @return the new context
     */
    private static NabuccoToXmlVisitorContext createContext(NabuccoToXmlVisitorContext context) {
        context = new NabuccoToXmlVisitorContext(context);
        context.setNabuccoExtension(null);
        return context;
    }

    /**
     * Getter for the elementList.
     * 
     * @return Returns the elementList.
     */
    public List<Element> getElementList() {
        return this.elementList;
    }

    @Override
    public void visit(DatatypeStatement nabuccoDatatype, MdaModel<XmlModel> target) {
        super.visit(nabuccoDatatype, target);

        this.createParentRefIds(target);
    }

    /**
     * Create the reference IDs for the datatypes parent datatypes.
     * 
     * @param target
     *            the java target
     */
    private void createParentRefIds(MdaModel<XmlModel> target) {
        NabuccoModel parent = super.getParent();

        if (parent != null) {
            super.getVisitorContext().setNabuccoExtension(null);
            parent.getUnit().accept(this, target);
        }
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<XmlModel> target) {

        // Transient fields must not have a ref ID.
        if (nabuccoDatatype.nodeOptional.present()) {
            return;
        }

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                nabuccoDatatype.nodeToken1.tokenImage);

        if (multiplicity.isMultiple()) {
            throw new NabuccoVisitorException(
                    "Cannot extend a datatype of another component which has references on another datatype with multiplicity larger 1.");
        }

        String name = nabuccoDatatype.nodeToken2.tokenImage;
        this.createRefId(name, multiplicity);

    }

    /**
     * Checks whether a type is of another componentName and creates a reference ID.
     * 
     * @param name
     *            the element name
     * @param multiplicity
     *            the multiplicity
     */
    private void createRefId(String name, NabuccoMultiplicityType multiplicity) {

        try {

            XmlTemplate ormTemplate = this.getVisitorContext().getTemplate(NabuccoXmlTemplateConstants.ORM_TEMPLATE);

            List<Node> nodes = ormTemplate.copyNodesByXPath(XPATH_BASIC);

            if (nodes.size() == 1 && nodes.get(0) instanceof Element) {
                Element basic = (Element) nodes.get(0);
                basic.setAttribute(NAME, name + REF_ID);

                NodeList childNodes = basic.getChildNodes();
                if (childNodes.getLength() > 1 && childNodes.item(1) instanceof Element) {
                    Element column = (Element) childNodes.item(1);

                    column.setAttribute(NAME, NabuccoTransformationUtility.toTableName(name)
                            + TABLE_SEPARATOR + REF_ID_COLUMN);

                    if (!multiplicity.isOptional()) {
                        column.setAttribute(NULLABLE, Boolean.FALSE.toString());
                    }
                }

                this.elementList.add(basic);
            }

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error creating Reference ID for " + name + ".", te);
        }
    }

}
