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
package org.nabucco.framework.generator.compiler.transformation.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nabucco.framework.generator.compiler.constants.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.JBossConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.service.comparator.EjbAssemblyDescriptorComparator;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelException;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelLoader;
import org.nabucco.framework.mda.template.MdaTemplateException;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;
import org.nabucco.framework.mda.template.xml.XmlTemplateLoader;
import org.nabucco.framework.mda.transformation.TransformationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * NabuccoXmlFragmentMerger
 * <p/>
 * Utility class to merge fragment files.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoXmlFragmentMerger implements EjbJarConstants, JBossConstants, PersistenceConstants {

    private static final String XML_SUFFIX = PKG_SEPARATOR + XML;

    /**
     * Singleton instance.
     */
    private static NabuccoXmlFragmentMerger instance = new NabuccoXmlFragmentMerger();

    /**
     * Private constructor.
     */
    private NabuccoXmlFragmentMerger() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoXmlFragmentMerger instance.
     */
    public static NabuccoXmlFragmentMerger getInstance() {
        return instance;
    }

    /**
     * Merges a list of fragment files into complete files.
     * 
     * @param rootDir
     *            the root directory
     * 
     * @return the model
     * 
     * @throws TransformationException
     */
    public MdaModel<XmlModel> mergeFragments(String rootDir, String componentName)
            throws NabuccoTransformationException {

        try {
            XmlModel model = new XmlModel();

            StringBuilder path = new StringBuilder();
            path.append(rootDir);
            path.append(File.separatorChar);
            path.append(componentName);
            path.append(File.separatorChar);
            path.append(CONF);
            path.append(File.separatorChar);
            path.append(EJB);
            path.append(File.separatorChar);
            path.append(FRAGMENT);

            File dir = new File(path.toString());

            if (dir.exists()) {
                List<XmlModel> modelList = this.collectFragments(dir);
                List<XmlDocument> mergedFragments = this.merge(modelList, componentName);
                model.getDocuments().addAll(mergedFragments);
            }

            return new MdaModel<XmlModel>(model);

        } catch (ModelException me) {
            throw new NabuccoTransformationException(me);
        } catch (MdaTemplateException te) {
            throw new NabuccoTransformationException(te);
        } catch (IOException ioe) {
            throw new NabuccoTransformationException(ioe);
        }
    }

    /**
     * Loads all XML fragment models.
     * 
     * @param dir
     *            the fragment dir
     * 
     * @return the loaded models
     * 
     * @throws ModelException
     * @throws IOException
     */
    private List<XmlModel> collectFragments(File dir) throws ModelException, IOException {

        List<XmlModel> modelList = new ArrayList<XmlModel>();
        XmlModelLoader loader = new XmlModelLoader();

        for (String fragmentName : dir.list()) {

            if (fragmentName.endsWith(XML_SUFFIX)) {
                File fragmentFile = new File(dir, fragmentName);
                FileInputStream in = new FileInputStream(fragmentFile);
                modelList.add(loader.loadModel(in));
            }
        }

        Collections.sort(modelList, NabuccoXmlFragmentComparator.getInstance());
        return modelList;
    }

    /**
     * Merges the XML files.
     * 
     * @param modelList
     *            the list of fragments
     * @param path
     *            path of the target
     * @param component
     *            the component name
     * 
     * @return the merged documents
     * 
     * @throws XmlTemplateException
     */
    private List<XmlDocument> merge(List<XmlModel> modelList, String path) throws MdaTemplateException {

        List<XmlDocument> documentList = new ArrayList<XmlDocument>();

        XmlDocument ejbJarDocument = XmlTemplateLoader.getInstance()
                .loadTemplate(NabuccoXmlTemplateConstants.EJB_JAR_EMPTY_TEMPLATE).extractModel().getDocuments().get(0);

        String ejbFolder = CONF + File.separatorChar + EJB + File.separatorChar;

        ejbJarDocument.setProjectName(path);
        ejbJarDocument.setConfFolder(ejbFolder);

        XmlDocument ormDocument = XmlTemplateLoader.getInstance()
                .loadTemplate(NabuccoXmlTemplateConstants.ORM_EMPTY_TEMPLATE).extractModel().getDocuments().get(0);

        ormDocument.setProjectName(path);
        ormDocument.setConfFolder(ejbFolder);

        XmlDocument jbossDocument = XmlTemplateLoader.getInstance()
                .loadTemplate(NabuccoXmlTemplateConstants.JBOSS_EMPTY_TEMPLATE).extractModel().getDocuments().get(0);

        jbossDocument.setProjectName(path);
        jbossDocument.setConfFolder(ejbFolder + JBOSS + File.separatorChar);

        for (XmlModel xmlModel : modelList) {

            Element fragmentElement = xmlModel.getDocuments().get(0).getDocument().getDocumentElement();

            if (fragmentElement.getAttribute(TYPE).startsWith(EJB_JAR)) {
                this.createEjbJar(ejbJarDocument, fragmentElement);
            } else if (fragmentElement.getAttribute(TYPE).startsWith(ORM)) {
                this.createOrm(ormDocument, fragmentElement);
            } else if (fragmentElement.getAttribute(TYPE).startsWith(JBOSS)) {
                this.createJBoss(jbossDocument, fragmentElement);
            }
        }

        documentList.add(ejbJarDocument);
        documentList.add(ormDocument);
        documentList.add(jbossDocument);

        return documentList;
    }

    /**
     * Merges the ejb-jar fragments into the document.
     * 
     * @param ejbJarDocument
     *            the empty ejb-jar document
     * @param fragmentElement
     *            the fragment element
     */
    private void createEjbJar(XmlDocument ejbJarDocument, Element fragmentElement) {

        Element ejb = (Element) ejbJarDocument.getDocument().getDocumentElement()
                .getElementsByTagName(ENTERPRISE_BEANS).item(0);

        Element assembly = (Element) ejbJarDocument.getDocument().getDocumentElement()
                .getElementsByTagName(ASSEMBlY_DESCRIPTOR).item(0);

        NodeList tags = fragmentElement.getElementsByTagName(SESSION);

        for (int i = 0; i < tags.getLength(); i++) {
            ejb.appendChild(ejbJarDocument.getDocument().importNode(tags.item(i), true));
        }

        tags = fragmentElement.getElementsByTagName(CONTAINER_TRANSACTION);

        List<Node> attributeList = new ArrayList<Node>();

        for (int i = 0; i < tags.getLength(); i++) {
            attributeList.add(ejbJarDocument.getDocument().importNode(tags.item(i), true));
        }

        tags = fragmentElement.getElementsByTagName(APPLICATION_EXCEPTION);

        for (int i = 0; i < tags.getLength(); i++) {
            attributeList.add(ejbJarDocument.getDocument().importNode(tags.item(i), true));
        }

        this.mergeAssemblyDescriptors(assembly, attributeList);
    }

    /**
     * Merges the assebly descriptors in the appropriate schema order.
     * 
     * @param assembly
     *            the assembly element
     * @param attributeList
     *            the attributes
     */
    private void mergeAssemblyDescriptors(Element assembly, List<Node> attributeList) {

        while (assembly.hasChildNodes()) {
            Node oldChild = assembly.getFirstChild();
            attributeList.add(assembly.removeChild(oldChild));
        }

        Collections.sort(attributeList, EjbAssemblyDescriptorComparator.getInstance());

        // Add new nodes
        for (Node attribute : attributeList) {
            assembly.appendChild(attribute);
        }
    }

    /**
     * Merges the orm fragments into the document.
     * 
     * @param ormDocument
     *            the empty orm document
     * @param fragmentElement
     *            the fragment element
     */
    private void createOrm(XmlDocument ormDocument, Element fragmentElement) {

        Element orm = ormDocument.getDocument().getDocumentElement();

        NodeList entities = fragmentElement.getElementsByTagName(ENTITY);

        for (int i = 0; i < entities.getLength(); i++) {
            orm.appendChild(ormDocument.getDocument().importNode(entities.item(i), true));
        }

        NodeList supperClasses = fragmentElement.getElementsByTagName(MAPPED_SUPERCLASS);

        for (int i = 0; i < supperClasses.getLength(); i++) {
            orm.appendChild(ormDocument.getDocument().importNode(supperClasses.item(i), true));
        }

        NodeList embeddables = fragmentElement.getElementsByTagName(EMBEDDABLE);

        for (int i = 0; i < embeddables.getLength(); i++) {
            orm.appendChild(ormDocument.getDocument().importNode(embeddables.item(i), true));
        }
    }

    /**
     * Merges the jboss fragments into the document.
     * 
     * @param jbossDocument
     *            the empty jboss document
     * @param fragmentElement
     *            the fragment element
     */
    private void createJBoss(XmlDocument jbossDocument, Element fragmentElement) {

        Element jboss = (Element) jbossDocument.getDocument().getDocumentElement()
                .getElementsByTagName(ENTERPRISE_BEANS).item(0);

        NodeList tags = fragmentElement.getElementsByTagName(SESSION);

        for (int i = 0; i < tags.getLength(); i++) {
            jboss.appendChild(jbossDocument.getDocument().importNode(tags.item(i), true));
        }
    }

}
