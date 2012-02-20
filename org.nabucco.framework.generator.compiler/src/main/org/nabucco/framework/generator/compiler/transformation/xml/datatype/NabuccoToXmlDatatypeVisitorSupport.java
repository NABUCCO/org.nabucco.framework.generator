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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.AssociationStrategyType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.constraint.NabuccoConstraintAnnotationExtractor;
import org.nabucco.framework.generator.compiler.transformation.java.constants.CollectionConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.datatype.comparator.OrmAttributeComparator;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * NabuccoToXmlDatatypeVisitorSupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlDatatypeVisitorSupport implements PersistenceConstants {

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoToXmlDatatypeVisitorSupport() {
    }

    /** The MDA Logger */
    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToXmlDatatypeVisitorSupport.class);

    /**
     * Copies <code>one-to-one</code>, <code>one-to-many</code>, <code>many-to-one</code> or
     * <code>many-to-many</code> tags from the template depending on multiplicity and association
     * type.</p>
     * 
     * <table border=true>
     * <col width="10%"/> <col width="30%"/> <col width="30%"/> <thead>
     * <tr>
     * <th>&nbsp;</th>
     * <th>COMPOSITION</th>
     * <th>AGGREGATION</th>
     * </tr>
     * </thead> <tbody>
     * <tr>
     * <td>1</td>
     * <td>1:1</td>
     * <td>M:1</td>
     * </tr>
     * <tr>
     * <td>*</td>
     * <td>1:N</td>
     * <td>M:N</td>
     * </tr>
     * <tr>
     * </tr>
     * </tbody>
     * </table>
     * <p/>
     * 
     * @param multiplicity
     *            the multiplicity
     * @param association
     *            the association strategy defining
     * @param template
     *            the template containing the tags
     * 
     * @return the extracted {@link Element} instance.
     * 
     * @throws XmlTemplateException
     */
    public static Element resolveMapping(NabuccoMultiplicityType multiplicity, AssociationStrategyType association,
            XmlTemplate template) throws XmlTemplateException {

        if (multiplicity == null) {
            throw new NabuccoVisitorException("Multiplicity is not valid " + multiplicity + ".");
        }

        if (association == AssociationStrategyType.AGGREGATION) {

            if (multiplicity.isMultiple()) {
                if (multiplicity.isOptional()) {
                    // Optional M:M Relation Template (ManyToMany)
                    return (Element) template.copyNodesByXPath(XPATH_MANY_TO_MANY).get(0);
                }
                // Mandatory M:M Relation Template (ManyToMany)
                return (Element) template.copyNodesByXPath(XPATH_MANY_TO_MANY).get(1);
            }
            if (multiplicity.isOptional()) {
                // Optional M:1 Relation Template (ManyToOne)
                return (Element) template.copyNodesByXPath(XPATH_MANY_TO_ONE).get(0);
            }
            // Mandatory M:1 Relation Template (ManyToOne)
            return (Element) template.copyNodesByXPath(XPATH_MANY_TO_ONE).get(1);
        }

        if (multiplicity.isMultiple()) {
            if (multiplicity.isOptional()) {
                // Optional 1:M Relation Template (OneToMany)
                return (Element) template.copyNodesByXPath(XPATH_ONE_TO_MANY).get(0);
            }
            // Mandatory 1:M Relation Template (OneToMany)
            return (Element) template.copyNodesByXPath(XPATH_ONE_TO_MANY).get(0);
        }
        if (multiplicity.isOptional()) {
            // Optional 1:1 Relation Template (OneToOne)
            return (Element) template.copyNodesByXPath(XPATH_ONE_TO_ONE).get(0);
        }
        // Mandatory 1:1 Relation Template (OneToOne)
        return (Element) template.copyNodesByXPath(XPATH_ONE_TO_ONE).get(1);
    }

    /**
     * Resolves element tags (id, version, etc.) by attached NABUCCO annotations.
     * 
     * @param annotationDeclaration
     *            the basetype declaration
     * @param ormTemplate
     *            the template containing the tags
     * @param name
     *            the column declaration name
     * @param length
     *            the column length
     * @param nullable
     *            whether the basetype is nullable or not
     * @param addJpaSuffix
     *            whether the basetype needs the 'JPA' suffix or not
     * 
     * @return the extracted {@link Element} instance.
     */
    public static Element resolveElementType(AnnotationDeclaration annotationDeclaration, XmlTemplate ormTemplate,
            String name, String length, boolean nullable, boolean addJpaSuffix) {

        try {
            Element element = null;
            String tableName = NabuccoTransformationUtility.toTableName(name);

            List<NabuccoAnnotation> annotationList = NabuccoAnnotationMapper.getInstance().mapToAnnotationList(
                    annotationDeclaration);

            for (NabuccoAnnotation annotation : annotationList) {

                if (annotation.getType() == NabuccoAnnotationType.PRIMARY) {

                    element = (Element) ormTemplate.copyNodesByXPath(XPATH_ID).get(0);
                    ((Element) element.getElementsByTagName(COLUMN).item(0)).setAttribute(NAME, tableName);

                } else if (annotation.getType() == NabuccoAnnotationType.OPTIMISTIC_LOCK) {
                    element = (Element) ormTemplate.copyNodesByXPath(XPATH_VERSION).get(0);
                }
            }

            if (element == null) {

                element = (Element) ormTemplate.copyNodesByXPath(XPATH_EMBEDDED).get(0);

                Element attributeOverride = (Element) element.getElementsByTagName(ATTRIBUTE_OVERRIDE).item(0);

                if (length == null || length.isEmpty()) {
                    length = DEFAULT_COLUMN_LENGTH;
                }

                if (addJpaSuffix) {
                    String nameValue = attributeOverride.getAttribute(NAME);
                    attributeOverride.setAttribute(NAME, nameValue + CollectionConstants.SUFFIX_JPA);
                }

                Element column = (Element) attributeOverride.getElementsByTagName(COLUMN).item(0);
                column.setAttribute(NAME, tableName);
                column.setAttribute(LENGTH, length);
                column.setAttribute(NULLABLE, String.valueOf(nullable));

            }

            element.setAttribute(NAME, name);
            return element;

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error copying XML tags by XPath of '" + name + "'.", te);
        }
    }

    /**
     * Extracts the column length of the NABUCCO type.
     * 
     * @param basetypeReference
     *            the NABUCCO basetype declaration to
     * @param the
     *            root package
     * @param context
     *            the visitor context
     * 
     * @return the column length
     */
    public static String extractColumnLength(BasetypeDeclaration basetypeReference, NabuccoVisitorContext context,
            String pkg, String importString) {

        AnnotationDeclaration annotationDeclaration = basetypeReference.annotationDeclaration;

        Integer length = Integer.parseInt(DEFAULT_COLUMN_LENGTH);

        NabuccoAnnotation maxLength = NabuccoAnnotationMapper.getInstance().mapToAnnotation(annotationDeclaration,
                NabuccoAnnotationType.MAX_LENGTH);

        if (maxLength != null && maxLength.getValue() != null) {

            try {
                int tempLength = Integer.parseInt(maxLength.getValue());

                if (tempLength <= length) {
                    length = tempLength;
                } else {
                    logger.warning("@MaxLength larger default " + DEFAULT_COLUMN_LENGTH);
                }

            } catch (NumberFormatException e) {
                logger.error("Cannot parse @MaxLength [", maxLength.getValue(), "]. ", e.getMessage());
            }

            return length.toString();
        }

        try {
            NabuccoConstraintAnnotationExtractor extractor = new NabuccoConstraintAnnotationExtractor();

            MdaModel<NabuccoModel> model = NabuccoDependencyResolver.getInstance().resolveDependency(
                    new NabuccoVisitorContext(context), pkg, importString);

            model.getModel().getUnit().accept(extractor);

            return extractor.getMaxLength();

        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException(e);
        }

    }

    /**
     * Removes all existing nodes of the element and inserts them into the attribute list. Then the
     * list is sorted for XSD compliance. Finally all attributes are inserted into the XML attibute.
     * 
     * @param attributes
     *            the XML element
     * @param the
     *            attribute list of new attributes
     */
    public static void mergeAttributeNodes(Element attributes, List<Node> attributeList) {
        mergeNodes(attributes, attributeList, OrmAttributeComparator.getInstance());
    }

    /**
     * Load all child nodes of an xml element.
     * 
     * @param entity
     *            the child nodes of an element as list
     * 
     * @return the children as list
     */
    public static List<Node> loadChildren(Element entity) {
        List<Node> entityNodes = new ArrayList<Node>();
        NodeList nodes = entity.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            entityNodes.add(node);
        }
        return entityNodes;
    }

    /**
     * Merge the list of nodes by the given comparator.
     * 
     * @param parent
     *            the parent element
     * @param comparator
     *            the comparator to sort
     */
    public static void sortNodes(Element parent, Comparator<Node> comparator) {
        mergeNodes(parent, loadChildren(parent), comparator);
    }

    /**
     * Merge the list of nodes by the given comparator.
     * 
     * @param parent
     *            the parent element
     * @param children
     *            the list of children
     * @param comparator
     *            the comparator to sort
     */
    public static void mergeNodes(Element parent, List<Node> children, Comparator<Node> comparator) {
        while (parent.hasChildNodes()) {
            Node oldChild = parent.getFirstChild();
            children.add(parent.removeChild(oldChild));
        }

        Collections.sort(children, comparator);

        // Add new nodes
        for (Node attribute : children) {
            parent.appendChild(attribute);
        }
    }

}
