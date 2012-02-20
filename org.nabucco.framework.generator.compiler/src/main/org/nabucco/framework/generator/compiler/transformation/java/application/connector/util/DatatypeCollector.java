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
package org.nabucco.framework.generator.compiler.transformation.java.application.connector.util;

import java.util.HashMap;
import java.util.Map;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.TraversingNabuccoToJavaVisitor;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.ApplicationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ConnectorStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceLinkDeclaration;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;

/**
 * DatatypeCollector
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class DatatypeCollector extends TraversingNabuccoToJavaVisitor<ConnectorStatement> {

    private ApplicationStatement application;

    private String sourceName;

    private String sourceType;

    private enum ServiceLinkType {
        MAINTAIN, RESOLVE
    }

    /** Map of datatype name to type. */
    private Map<String, String> targetMap = new HashMap<String, String>();

    /** Map of datatype name to component type. */
    private Map<ServiceLinkType, Map<String, ServiceLinkResolver>> targetServiceMap = new HashMap<ServiceLinkType, Map<String, ServiceLinkResolver>>();

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(DatatypeCollector.class);

    /**
     * Creates a new {@link DatatypeCollector} instance.
     * 
     * @param context
     *            the visitor context
     */
    public DatatypeCollector(NabuccoToJavaVisitorContext context, ApplicationStatement application) {
        super(context);

        this.application = application;
    }

    /**
     * Accepts the connector statement and starts the visitation.
     * 
     * @param connector
     *            the connector statement
     */
    public void accept(ConnectorStatement connector) {
        connector.accept(this, null);
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, ConnectorStatement connector) {

        boolean isSource = NabuccoAnnotationMapper.getInstance().hasAnnotation(nabuccoDatatype.annotationDeclaration,
                NabuccoAnnotationType.SOURCE);

        boolean isTarget = NabuccoAnnotationMapper.getInstance().hasAnnotation(nabuccoDatatype.annotationDeclaration,
                NabuccoAnnotationType.TARGET);

        if (isSource) {
            if (this.sourceType != null) {
                throw new NabuccoVisitorException("Only one sourceName datatype allowed!");
            }

            this.sourceName = nabuccoDatatype.nodeToken2.tokenImage;
            this.sourceType = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;

        } else if (isTarget) {

            String targetName = nabuccoDatatype.nodeToken2.tokenImage;
            String targetType = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
            this.targetMap.put(targetName, targetType);

        } else {
            String connectorName = connector.nodeToken2.tokenImage;
            String datatypeName = nabuccoDatatype.nodeToken2.tokenImage;
            logger.warning("Datatype '", datatypeName, "' cannot be mapped in connector '", connectorName,
                    "'. No @Source or @Target annotation defined.");
        }
    }

    @Override
    public void visit(ServiceLinkDeclaration nabuccoServiceLink, ConnectorStatement connector) {
        ServiceLinkResolver resolver = new ServiceLinkResolver(this.application, super.getContext());

        resolver.resolve(nabuccoServiceLink);

        NabuccoAnnotation maintain = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoServiceLink.annotationDeclaration, NabuccoAnnotationType.MAINTAIN);

        NabuccoAnnotation resolve = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoServiceLink.annotationDeclaration, NabuccoAnnotationType.RESOLVE);

        String name = null;
        ServiceLinkType type = null;

        if (maintain != null && maintain.getValue() != null) {
            name = maintain.getValue();
            type = ServiceLinkType.MAINTAIN;
        }

        if (resolve != null && resolve.getValue() != null) {
            name = resolve.getValue();
            type = ServiceLinkType.RESOLVE;
        }

        if (type == null || name == null) {
            throw new IllegalStateException("ServiceLink does not define a valid @Maintain or @Resolve annotation.");
        }

        Map<String, ServiceLinkResolver> map = this.targetServiceMap.get(type);
        if (map == null) {
            map = new HashMap<String, ServiceLinkResolver>();
            this.targetServiceMap.put(type, map);
        }
        map.put(name, resolver);
    }

    /**
     * Getter for the sourceType.
     * 
     * @return Returns the sourceType.
     */
    public String getSourceType() {
        return this.sourceType;
    }

    /**
     * Getter for the sourceName.
     * 
     * @return Returns the sourceName.
     */
    public String getSourceName() {
        return this.sourceName;
    }

    /**
     * Getter for the Map<Name, Type> of target datatypes.
     * 
     * @return Returns the targetMap.
     */
    public Map<String, String> getTargetMap() {
        return this.targetMap;
    }

    /**
     * Getter for the resolve service link resolvers.
     * 
     * @return Returns the resolve services.
     */
    public Map<String, ServiceLinkResolver> getResolveServices() {
        return this.targetServiceMap.get(ServiceLinkType.RESOLVE);
    }

    /**
     * Getter for the maintain service link resolvers.
     * 
     * @return Returns the maintain services.
     */
    public Map<String, ServiceLinkResolver> getMaintainServices() {
        return this.targetServiceMap.get(ServiceLinkType.MAINTAIN);
    }

}
