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
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;

/**
 * NabuccoTransactionType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum NabuccoTransactionType {

    MANDATORY("Mandatory"),

    REQUIRED("Required"),

    REQUIRES_NEW("RequiresNew"),

    SUPPORTS("Supports"),

    NOT_SUPPORTED("NotSupported"),

    NEVER("Never");

    private String value;

    /**
     * Creates a new {@link NabuccoTransactionType} instance.
     * 
     * @param value
     *            the transaction attribute value
     */
    private NabuccoTransactionType(String name) {
        this.value = name;
    }

    /**
     * Getter for the value.
     * 
     * @return Returns the value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Resolves the service type of service declaration annotations.
     * 
     * @param nabuccoMethod
     *            the method declaration to check
     * 
     * @return the defined service type
     */
    public static NabuccoTransactionType valueOf(MethodDeclaration nabuccoMethod) {
        return NabuccoTransactionType.valueOf(nabuccoMethod.annotationDeclaration);
    }

    /**
     * Resolves the transaction type of service operation declaration annotations.
     * 
     * @param serviceOperationAnnotations
     *            the service operation annotations to check
     * 
     * @return the defined transaction type
     */
    public static NabuccoTransactionType valueOf(AnnotationDeclaration serviceOperationAnnotations) {
        NabuccoAnnotation transactionAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                serviceOperationAnnotations, NabuccoAnnotationType.TRANSACTION);

        if (transactionAnnotation == null || transactionAnnotation.getValue() == null) {
            return REQUIRED;
        }

        String value = transactionAnnotation.getValue();

        for (NabuccoTransactionType transactionType : NabuccoTransactionType.values()) {
            if (value.equalsIgnoreCase(transactionType.getValue())) {
                return transactionType;
            }
        }

        return REQUIRED;
    }

}
