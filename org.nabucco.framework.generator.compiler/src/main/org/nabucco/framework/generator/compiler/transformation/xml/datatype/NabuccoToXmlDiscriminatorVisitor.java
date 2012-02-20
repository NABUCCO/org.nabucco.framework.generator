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

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * NabuccoToXmlDiscriminatorVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToXmlDiscriminatorVisitor extends GJVoidDepthFirst<Element> implements PersistenceConstants {

    private XmlTemplate template;

    /**
     * Creates a new {@link NabuccoToXmlDiscriminatorVisitor} instance.
     * 
     * @param template
     *            the xml template
     */
    public NabuccoToXmlDiscriminatorVisitor(XmlTemplate template) {
        this.template = template;
    }

    @Override
    public void visit(EnumerationDeclaration enumeration, Element entity) {

        NabuccoAnnotation discriminatorAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                enumeration.annotationDeclaration, NabuccoAnnotationType.DISCRIMINATOR);

        if (discriminatorAnnotation != null) {

            Element discriminator = this.copyElementsByXPath(XPATH_DISCRIMINATOR_COLUMN).get(0);
            String name = enumeration.nodeToken2.tokenImage;
            discriminator.setAttribute(NAME, NabuccoTransformationUtility.toTableName(name));

            entity.appendChild(discriminator);
        }

        NabuccoAnnotation redefinedAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                enumeration.annotationDeclaration, NabuccoAnnotationType.REDEFINED);

        NabuccoAnnotation defaultAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                enumeration.annotationDeclaration, NabuccoAnnotationType.DEFAULT);

        if (redefinedAnnotation != null && defaultAnnotation != null && defaultAnnotation.getValue() != null) {
            Element discriminator = this.copyElementsByXPath(XPATH_DISCRIMINATOR_VALUE).get(0);
            discriminator.setTextContent(defaultAnnotation.getValue());

            entity.appendChild(discriminator);

        }
    }

    /**
     * Copies all elements for the given XPath expression.
     * 
     * @param xPath
     *            the xpath expression
     * 
     * @return the elements
     */
    private List<Element> copyElementsByXPath(String xPath) {
        List<Element> elements = new ArrayList<Element>();
        try {

            List<Node> nodes = this.template.copyNodesByXPath(xPath);
            for (Node node : nodes) {
                if (node instanceof Element) {
                    elements.add((Element) node);
                }
            }
        } catch (XmlTemplateException xte) {
            throw new NabuccoVisitorException("Error loading ORM Template.", xte);
        }

        if (elements.isEmpty()) {
            throw new NabuccoVisitorException("Error resolving XPATH '" + xPath + "' in ORM Template.");
        }

        return elements;
    }
}
