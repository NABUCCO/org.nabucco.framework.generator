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
package org.nabucco.framework.generator.compiler.transformation.common.annotation.service;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;

/**
 * NabuccoServiceType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum NabuccoServiceType {

    BUSINESS("business"),

    PERSISTENCE("persistence"),

    RESOURCE("resource");

    /**
     * Creates a new {@link NabuccoServiceType} instance.
     * 
     * @param value
     *            the enum value
     */
    private NabuccoServiceType(String value) {
        this.value = value;
    }

    private String value;

    /**
     * Getter for the value.
     * 
     * @return Returns the value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Resolves the service type of a service declaration.
     * 
     * @param service
     *            the service to check
     * 
     * @return the defined service type
     */
    public static NabuccoServiceType valueOf(ServiceStatement service) {
        return valueOf(service.annotationDeclaration);
    }

    /**
     * Resolves the service type of service declaration annotations.
     * 
     * @param serviceAnnotations
     *            the service annotations to check
     * 
     * @return the defined service type
     */
    public static NabuccoServiceType valueOf(AnnotationDeclaration serviceAnnotations) {
        NabuccoAnnotation serviceTypeAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                serviceAnnotations, NabuccoAnnotationType.SERVICE_TYPE);

        if (serviceTypeAnnotation == null || serviceTypeAnnotation.getValue() == null) {
            return PERSISTENCE;
        }

        String value = serviceTypeAnnotation.getValue();

        for (NabuccoServiceType serviceType : NabuccoServiceType.values()) {
            if (value.equalsIgnoreCase(serviceType.getValue())) {
                return serviceType;
            }
        }

        return PERSISTENCE;
    }

}
