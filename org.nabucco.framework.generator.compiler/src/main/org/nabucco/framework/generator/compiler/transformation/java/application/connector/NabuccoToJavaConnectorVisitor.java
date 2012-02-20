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
package org.nabucco.framework.generator.compiler.transformation.java.application.connector;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.parser.syntaxtree.ApplicationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ConnectorStatement;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;

/**
 * NabuccoToJavaConnectorVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaConnectorVisitor extends NabuccoToJavaVisitorSupport {

    private static final Object CONNECTOR_TYPE_DATATYPE = "DATATYPE";

    private static final Object CONNECTOR_TYPE_SERVICE = "SERVICE";

    private ApplicationStatement nabuccoApplication;

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoToJavaConnectorVisitor.class);

    /**
     * Creates a new {@link NabuccoToJavaConnectorVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param nabuccoApplication
     */
    public NabuccoToJavaConnectorVisitor(NabuccoToJavaVisitorContext visitorContext,
            ApplicationStatement nabuccoApplication) {
        super(visitorContext);

        if (nabuccoApplication == null) {
            throw new IllegalArgumentException("Cannot create Connector for application [null].");
        }

        this.nabuccoApplication = nabuccoApplication;
    }

    @Override
    public void visit(ConnectorStatement nabuccoConnector, MdaModel<JavaModel> target) {

        NabuccoAnnotation connectorType = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoConnector.annotationDeclaration, NabuccoAnnotationType.CONNECTOR_TYPE);

        if (connectorType == null || connectorType.getValue() == null) {
            logger.error("Cannot create Connector for @ConnectorType [null].");
            return;
        }

        if (connectorType.getValue().equals(CONNECTOR_TYPE_DATATYPE)) {
            NabuccoToJavaDatatypeConnectorVisitor visitor = new NabuccoToJavaDatatypeConnectorVisitor(
                    super.getVisitorContext(), this.nabuccoApplication);

            nabuccoConnector.accept(visitor, target);

        } else if (connectorType.getValue().equals(CONNECTOR_TYPE_SERVICE)) {

            NabuccoToJavaServiceConnectorVisitor visitor = new NabuccoToJavaServiceConnectorVisitor(
                    super.getVisitorContext(), this.nabuccoApplication);

            nabuccoConnector.accept(visitor, target);
        } else {
            logger.error("Cannot create Connector for @ConnectorType [", connectorType.getValue(),
                    "]. Only DATATYPE and SERVICE allowed.");
        }
    }
}
