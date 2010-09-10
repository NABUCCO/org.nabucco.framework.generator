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
package org.nabucco.framework.generator.compiler.transformation.xml.exception;

import java.io.File;
import java.util.List;

import org.nabucco.framework.generator.compiler.template.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionStatement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlDocument;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.XmlModelException;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;

/**
 * NabuccoToXmlExceptionVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlExceptionVisitor extends NabuccoToXmlVisitorSupport implements EjbJarConstants {

    private String componentName;

    /**
     * Creates a new {@link NabuccoToXmlExceptionVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param componentName
     *            the component name where this exception is used
     */
    public NabuccoToXmlExceptionVisitor(NabuccoToXmlVisitorContext visitorContext,
            String componentName) {
        super(visitorContext);
        if (componentName == null) {
            throw new IllegalArgumentException(
                    "Component Name is not defined for service exception.");
        }
        this.componentName = componentName;
    }

    @Override
    public void visit(ExceptionStatement nabuccoException, MdaModel<XmlModel> target) {

        String name = nabuccoException.nodeToken2.tokenImage;

        try {
            // Final document
            XmlDocument document = super
                    .extractDocument(NabuccoXmlTemplateConstants.EXCEPTION_FRAGMENT_TEMPLATE);

            boolean rollback = isRollback(nabuccoException.annotationDeclaration);

            this.modifyFragment(document, name, rollback);

            // File creation
            document.setProjectName(this.componentName);
            document.setConfFolder(super.getConfFolder() + FRAGMENT + File.separator);

            target.getModel().getDocuments().add(document);

        } catch (XmlModelException me) {
            throw new NabuccoVisitorException("Error during XML DOM exception modification.", me);
        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error during XML template exception processing.", te);
        }
    }

    /**
     * Checks whether an exception is marked for rollback or not.
     * 
     * @param annotation
     *            the annotation list
     * 
     * @return <b>true</b> if the rollback annotation does exist, <b>false</b> if not
     */
    private boolean isRollback(AnnotationDeclaration annotation) {

        NabuccoAnnotation rollbackAnnotation = NabuccoAnnotationMapper.getInstance()
                .mapToAnnotation(annotation, NabuccoAnnotationType.ROLLBACK);

        // Default behaviour
        if (rollbackAnnotation == null) {
            return false;
        }
        return true;
    }

    /**
     * Prepares the XML fragment for the exception.
     * 
     * @param document
     *            the XML document
     * @param name
     *            the exception name
     * @param isRollback
     *            the rollback flag
     * 
     * @throws XmlModelException
     */
    private void modifyFragment(XmlDocument document, String name, boolean isRollback)
            throws XmlModelException {

        document.getDocument().getDocumentElement().setAttribute(NAME, name);

        List<Node> classNodes = document.getElementsByXPath(XPATH_FRAGMENT_EXCEPTION_CLASS);
        List<Node> rollbackNodes = document.getElementsByXPath(XPATH_FRAGMENT_EXCEPTION_ROLLBACK);

        if (classNodes.size() != 1 || rollbackNodes.size() != 1) {
            throw new IllegalArgumentException("XML exception fragment is not consistent.");
        }

        String pkg = super.getVisitorContext().getPackage();
        String exception = pkg + PKG_SEPARATOR + name;

        Element element = (Element) classNodes.get(0);
        element.setTextContent(exception);

        element = (Element) rollbackNodes.get(0);
        element.setTextContent(String.valueOf(isRollback));
    }
}
