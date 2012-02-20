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

import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.TraversingNabuccoToJavaVisitor;
import org.nabucco.framework.generator.parser.syntaxtree.ApplicationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeListOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceLinkDeclaration;

/**
 * ServiceLinkResolver
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ServiceLinkResolver extends TraversingNabuccoToJavaVisitor<ServiceLinkDeclaration> {

    private String component;

    private String componentReference;

    private String service;

    private String operation;

    private String requestMessage;

    private String responseMessage;

    private ApplicationStatement application;

    private List<NabuccoAnnotation> annotations;

    private static final String EMPTY_SERVICE_MESSAGE = "org.nabucco.framework.base.facade.message.EmptyServiceMessage";

    /**
     * Creates a new {@link ServiceLinkResolver} instance.
     * 
     * @param nabuccoApplication
     * 
     * @param serviceLink
     *            the service link
     */
    public ServiceLinkResolver(ApplicationStatement nabuccoApplication, NabuccoToJavaVisitorContext context) {
        super(new NabuccoToJavaVisitorContext(context));

        this.application = nabuccoApplication;
    }

    /**
     * Resolve the service link.
     * 
     * @param serviceLink
     *            the service link to resolve
     */
    public void resolve(ServiceLinkDeclaration serviceLink) {
        String[] tokens = serviceLink.nodeToken2.tokenImage.split("\\.");

        if (tokens.length != 2) {
            throw new IllegalArgumentException(
                    "ServiceLink grammar is not correct, must be 'component.service.operation'.");
        }

        this.componentReference = tokens[0];
        this.service = tokens[1];
        this.operation = serviceLink.nodeToken4.tokenImage;

        this.application.accept(this, serviceLink);

        this.annotations = NabuccoAnnotationMapper.getInstance().mapToAnnotationList(serviceLink.annotationDeclaration);
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, ServiceLinkDeclaration serviceLink) {
        if (nabuccoComponent.nodeToken2.tokenImage.equals(this.componentReference)) {
            String component = nabuccoComponent.nodeToken1.tokenImage;
            this.component = super.resolveImport(component);
            super.subVisit(component, serviceLink);
        }
    }

    @Override
    public void visit(ServiceDeclaration nabuccoService, ServiceLinkDeclaration serviceLink) {
        if (nabuccoService.nodeToken1.tokenImage.equals(this.service)) {
            this.service = super.resolveImport(this.service);
            super.subVisit(nabuccoService.nodeToken1.tokenImage, serviceLink);
        }
    }

    @Override
    public void visit(MethodDeclaration nabuccoOperation, ServiceLinkDeclaration serviceLink) {
        if (nabuccoOperation.nodeToken1.tokenImage.equals(this.operation)) {
            NodeListOptional params = nabuccoOperation.parameterList.nodeListOptional;

            if (params.present() && params.nodes.size() > 0) {
                Parameter parameter = (Parameter) params.nodes.get(0);
                this.requestMessage = super.resolveImport(parameter.nodeToken.tokenImage);
            }

            if (nabuccoOperation.nodeChoice.which == 1) {
                String type = ((NodeToken) nabuccoOperation.nodeChoice.choice).tokenImage;
                this.responseMessage = super.resolveImport(type);
            }
        }
    }

    /**
     * Getter for the component.
     * 
     * @return Returns the component.
     */
    public String getComponent() {
        return this.component;
    }

    /**
     * Getter for the service.
     * 
     * @return Returns the service.
     */
    public String getService() {
        return this.service;
    }

    /**
     * Getter for the service operation.
     * 
     * @return Returns the operation.
     */
    public String getServiceOperation() {
        return this.operation;
    }

    /**
     * Getter for the requestMessage.
     * 
     * @return Returns the requestMessage.
     */
    public String getRequestMessage() {
        if (this.requestMessage == null) {
            return EMPTY_SERVICE_MESSAGE;
        }
        return this.requestMessage;
    }

    /**
     * Getter for the responseMessage.
     * 
     * @return Returns the responseMessage.
     */
    public String getResponseMessage() {
        if (this.responseMessage == null) {
            return EMPTY_SERVICE_MESSAGE;
        }
        return this.responseMessage;
    }

    /**
     * Getter for the annotations.
     * 
     * @return Returns the annotations.
     */
    public List<NabuccoAnnotation> getAnnotations() {
        if (this.annotations == null) {
            this.annotations = new ArrayList<NabuccoAnnotation>();
        }
        return this.annotations;
    }

}
