/*
* Copyright 2010 PRODYNA AG
*
* Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.opensource.org/licenses/eclipse-1.0.php or
* http://www.nabucco-source.org/nabucco-license.html
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.nabucco.framework.generator.compiler.transformation.util.dependency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.parser.model.NabuccoPathEntryType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;

/**
 * NbcPathParser
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NbcPathParser {

    /** The NABUCCO logger. */
    private static MdaLogger logger = MdaLoggingFactory.getInstance()
            .getLogger(NbcPathParser.class);

    /** Constant for the path XML attribute. */
    private static final String PATH_VALUE = "path";

    /** Constant for the kind XML attribute. */
    private static final String KIND_VALUE = "kind";

    /** Constant for the nbcpath XPATH. */
    private static final String XPATH_NBCENTRY = "nbcpath/nbcentry";

    /**
     * Gets all {@link Element} of this XML document by an XPath.
     * 
     * @param filePath
     *            path to the nbcpath.xml
     * 
     * @return the NabuccoPath with all owned NabuccoPathEntries
     * 
     * @throws NabuccoTransformationException
     */
    public static NabuccoPath getElementsByXPath(String filePath)
            throws NabuccoTransformationException {

        Document document = getDocument(filePath);
        if (document == null) {
            return new NabuccoPath(null);
        }

        try {
            XPathExpression xpath = XPathFactory.newInstance().newXPath().compile(XPATH_NBCENTRY);
            NodeList xmlNodes = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);

            List<NabuccoPathEntry> entryList = new ArrayList<NabuccoPathEntry>();
            for (int i = 0; i < xmlNodes.getLength(); i++) {
                Node node = xmlNodes.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String nodePath = null;
                NabuccoPathEntryType entryType = null;

                if (attributes != null) {

                    Node kind = attributes.getNamedItem(KIND_VALUE);

                    if (kind != null && kind.getNodeValue() != null) {
                        String value = kind.getNodeValue();
                        if (value.equalsIgnoreCase(NabuccoPathEntryType.ARCHIVE.getId())) {
                            entryType = NabuccoPathEntryType.ARCHIVE;
                        } else if (value.equalsIgnoreCase(NabuccoPathEntryType.PROJECT.getId())) {
                            entryType = NabuccoPathEntryType.PROJECT;
                        }
                    }

                    Node path = attributes.getNamedItem(PATH_VALUE);
                    if (path != null) {
                        nodePath = path.getNodeValue();
                    }
                }

                if (nodePath != null && entryType != null) {
                    NabuccoPathEntry pathEntry = new NabuccoPathEntry(entryType, nodePath);
                    entryList.add(pathEntry);
                }
            }

            return new NabuccoPath(entryList);

        } catch (XPathExpressionException e) {
            throw new NabuccoTransformationException("Error parsing " + filePath + ".", e);
        }
    }

    /**
     * Parses the XML DOM for the given file path
     * 
     * @param path
     *            path to the xml file
     * 
     * @return the parsed XML document
     * 
     * @throws NabuccoTransformationException
     */
    private static Document getDocument(String path) throws NabuccoTransformationException {
        File file = new File(path);

        try {
            InputStream xmlStream = new FileInputStream(file);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlStream);

            return doc;

        } catch (FileNotFoundException e) {
            logger.warning("File " + path + " not found.");
            return null;
        } catch (ParserConfigurationException e) {
            throw new NabuccoTransformationException("Error parsing " + file + ".", e);
        } catch (SAXException e) {
            throw new NabuccoTransformationException("Error parsing " + path + ".", e);
        } catch (IOException e) {
            throw new NabuccoTransformationException("Error parsing " + path + ".", e);
        }
    }

}
