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
package org.nabucco.framework.generator.compiler.transformation.common.annotation;

/**
 * NabuccoAnnotationType
 * <p/>
 * All available NABUCCO annotations.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum NabuccoAnnotationType {

    AUTHOR("Author", NabuccoAnnotationGroupType.DOCUMENTATION),

    DATE("Date", NabuccoAnnotationGroupType.DOCUMENTATION),

    VERSION("Version", NabuccoAnnotationGroupType.DOCUMENTATION),

    COMPANY("Company", NabuccoAnnotationGroupType.DOCUMENTATION),
    
    DEPRECATED("Deprecated", NabuccoAnnotationGroupType.DOCUMENTATION),

    DESCRIPTION("Description", NabuccoAnnotationGroupType.DOCUMENTATION),

    SERVICE_TYPE("ServiceType", NabuccoAnnotationGroupType.SERVICE),
    
    MANUAL_IMPLEMENTATION("ManualImplementation", NabuccoAnnotationGroupType.SERVICE),
    
    TRANSACTION("Transaction", NabuccoAnnotationGroupType.SERVICE),

    DEFAULT("Default", NabuccoAnnotationGroupType.INIT),

    REDEFINED("Redefined", NabuccoAnnotationGroupType.INIT),

    MIN_LENGTH("MinLength", NabuccoAnnotationGroupType.CONSTRAINT),

    MAX_LENGTH("MaxLength", NabuccoAnnotationGroupType.CONSTRAINT),

    MIN_VALUE("MinValue", NabuccoAnnotationGroupType.CONSTRAINT),

    MAX_VALUE("MaxValue", NabuccoAnnotationGroupType.CONSTRAINT),

    PATTERN("Pattern", NabuccoAnnotationGroupType.CONSTRAINT),

    ASSOCIATION_STRATEGY("AssociationStrategy", NabuccoAnnotationGroupType.RELATION),

    FETCH_STRATEGY("FetchStrategy", NabuccoAnnotationGroupType.RELATION),

    ORDER_STRATEGY("OrderStrategy", NabuccoAnnotationGroupType.RELATION),

    TECHNICAL_PROPERTY("TechnicalProperty", NabuccoAnnotationGroupType.RELATION),

    SOURCE("Source", NabuccoAnnotationGroupType.CONNECTOR),

    TARGET("Target", NabuccoAnnotationGroupType.CONNECTOR),

    MAINTAIN("Maintain", NabuccoAnnotationGroupType.CONNECTOR),

    RESOLVE("Resolve", NabuccoAnnotationGroupType.CONNECTOR),

    CONNECTOR_TYPE("ConnectorType", NabuccoAnnotationGroupType.CONNECTOR),

    CONNECTOR_STRATEGY("ConnectorStrategy", NabuccoAnnotationGroupType.CONNECTOR),

    REFERENCEABLE("Referenceable", NabuccoAnnotationGroupType.CONNECTOR),

    INJECT("Inject", NabuccoAnnotationGroupType.INJECTION),

    INJECTION_ID("InjectionId", NabuccoAnnotationGroupType.INJECTION),

    COMPONENT_PREFIX("ComponentPrefix", NabuccoAnnotationGroupType.DB),

    PRIMARY("Primary", NabuccoAnnotationGroupType.DB),

    OPTIMISTIC_LOCK("OptimisticLock", NabuccoAnnotationGroupType.DB),

    ROLLBACK("Rollback", NabuccoAnnotationGroupType.DB),

    DISCRIMINATOR("Discriminator", NabuccoAnnotationGroupType.DB),

    LITERAL_ID("LiteralId", NabuccoAnnotationGroupType.DB),

    USER_INTERFACE("UserInterface", NabuccoAnnotationGroupType.UI),

    MAPPED_FIELD("MappedField", NabuccoAnnotationGroupType.UI),

    LEADING("Leading", NabuccoAnnotationGroupType.UI),

    FIELD_LABEL_ID("FieldLabelId", NabuccoAnnotationGroupType.UI),

    FIELD_LABEL_DEFAULT("FieldLabelDefault", NabuccoAnnotationGroupType.UI),

    FIELD_EDIT_MODE("FieldEditMode", NabuccoAnnotationGroupType.UI),

    CLIENT_ELEMENT_ID("Id", NabuccoAnnotationGroupType.UI),

    COLUMN_LABEL_ID("ColumnLabelId", NabuccoAnnotationGroupType.UI),

    COLUMN_LABEL_DEFAULT("ColumnLabelDefault", NabuccoAnnotationGroupType.UI),

    CODE_PATH("CodePath", NabuccoAnnotationGroupType.EXTENSION),

    JOIN_POINT("JoinPoint", NabuccoAnnotationGroupType.ASPECT);

    private String name;

    private NabuccoAnnotationGroupType type;

    /**
     * Creates the {@link NabuccoAnnotationType} instance depending on its name and type.
     * 
     * @param name
     *            the annotation name
     * @param type
     *            the annotation group type
     */
    private NabuccoAnnotationType(String name, NabuccoAnnotationGroupType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Getter for the annotation name.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the annotation type.
     * 
     * @return Returns the type.
     */
    public NabuccoAnnotationGroupType getType() {
        return this.type;
    }

}
