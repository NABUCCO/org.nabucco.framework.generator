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
package org.nabucco.framework.generator.compiler.transformation.xml.datatype;

import java.util.List;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.template.NabuccoXmlTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.AssociationStrategyType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.association.FetchStrategyType;
import org.nabucco.framework.generator.compiler.transformation.java.basetype.NabuccoToJavaBasetypeMapping;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.transformation.xml.basetype.NabuccoToXmlBasetypeFacade;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.PersistenceConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoPathEntryType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityType;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.template.xml.XmlTemplate;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;

/**
 * NabuccoToXmlDatatypeVisitor
 * <p/>
 * Visitor for persistent datatypes. Do not use this class directly, use
 * {@link NabuccoToXmlDatatypeFacade} instead.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlDatatypeVisitor extends NabuccoToXmlVisitorSupport implements
        PersistenceConstants {

    private String componentName;
    
    private String rootPackage;

    /** ORM dependencies must be collected seperately */
    NabuccoToXmlDatatypeCollector collector;

    /** The logger */
    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToXmlDatatypeVisitor.class);

    /**
     * Creates a new {@link NabuccoToXmlDatatypeVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     * @param collector
     *            the visitor collector (must be closed after visitation)
     * @param rootPackage
     *            the root package of the XML transformation (the focused component)
     */
    public NabuccoToXmlDatatypeVisitor(NabuccoToXmlVisitorContext visitorContext,
            NabuccoToXmlDatatypeCollector collector, String rootPackage) {
        super(visitorContext);
        if (collector == null) {
            throw new IllegalArgumentException("Collector must be defined.");
        }
        this.collector = collector;
        this.rootPackage = rootPackage;
    }

    @Override
    public void visit(ExtensionDeclaration nabuccoExtension, MdaModel<XmlModel> target) {

        // Components do not have an extension declaration!

        super.visit(nabuccoExtension, target); // Extract super type

        if (this.rootPackage == null) {
            this.rootPackage = super.getVisitorContext().getPackage();
        }

        String superType = this.getVisitorContext().getNabuccoExtension();
        String superImport = super.resolveImport(superType);

        if (superImport == null) {
            logger.warning("Cannot resolve depdendency for Datatype '", superType, "'.");
            return;
        }

        if (this.collector.contains(superImport)) {
            logger.debug("Depdendency already visited '" + superImport + "'.");
            return;
        }

        this.createMappedSuperclass(superType, superImport, target);
    }

    @Override
    public void visit(ComponentDatatypeDeclaration nabuccoDatatype, MdaModel<XmlModel> target) {

        String type = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        boolean isPersistent = nabuccoDatatype.nodeOptional.present();

        if (isPersistent) {
            String datatypeImport = super.resolveImport(type);

            if (datatypeImport == null) {
                logger.warning("Cannot resolve depdendency for Datatype '", type, "'.");
                return;
            }

            this.createEntity(type, datatypeImport, target);
        }
    }

    /**
     * Creates an entity fragment for the particular type.
     * 
     * @param type
     *            the type
     * @param entityImport
     *            the import to resolve
     * @param target
     *            the target model
     */
    private void createEntity(String type, String entityImport, MdaModel<XmlModel> target) {

        if (this.collector.isEntity(entityImport)) {
            logger.debug("Entity already visited '" + entityImport + "'.");
            return;
        }

        try {
            MdaModel<NabuccoModel> model = this.resolveDependency(entityImport);

            // Do not create XML for archived entities (only mapped-superclasses).
            if (model.getModel().getResourceType() == NabuccoPathEntryType.ARCHIVE) {
                return;
            }
            
            NabuccoToXmlVisitorContext context = this.copyContext();

            NabuccoToXmlDatatypeEntityVisitor visitor = new NabuccoToXmlDatatypeEntityVisitor(
                    context, this.collector, this.rootPackage);

            model.getModel().getUnit().accept(visitor, target);

            // Create basetype embeddables
            NabuccoToXmlBasetypeFacade.getInstance().createOrmBasetypeFragments(model, target,
                    super.getVisitorContext(), this.componentName);

        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException("Error resolving XML dependency for Datatype '"
                    + type + "'.", e);
        }
    }

    /**
     * Creates an mapped superclass fragment for the particular type.
     * 
     * @param type
     *            the type
     * @param superclassImport
     *            the import to resolve
     * @param target
     *            the target model
     */
    private void createMappedSuperclass(String type, String superclassImport,
            MdaModel<XmlModel> target) {

        if (this.collector.contains(superclassImport)) {
            logger.debug("MappedSuperclass already visited '" + superclassImport + "'.");
            return;
        }

        try {
            MdaModel<NabuccoModel> model = this.resolveDependency(superclassImport);

            NabuccoToXmlVisitorContext context = this.copyContext();

            NabuccoToXmlDatatypeSuperclassVisitor visitor = new NabuccoToXmlDatatypeSuperclassVisitor(
                    new NabuccoToXmlVisitorContext(context), this.componentName, this.collector,
                    this.rootPackage);

            model.getModel().getUnit().accept(visitor, target);

            // Create basetype embeddables
            NabuccoToXmlBasetypeFacade.getInstance().createOrmBasetypeFragments(model, target,
                    new NabuccoToXmlVisitorContext(super.getVisitorContext()), this.componentName);

        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException("Error resolving XML dependency for Datatype '"
                    + type + "'.", e);
        }
    }

    /**
     * Creates the entity/mapped-superclass relation to another entity.
     * 
     * @param nabuccoDatatype
     *            the datatype
     * @param attributeList
     *            the list to add the mapping
     * @param name
     *            name of the root entity/mapped-superclass
     */
    void createEntityRelation(DatatypeDeclaration nabuccoDatatype, List<Node> attributeList,
            String name) {

        String type = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        String refName = nabuccoDatatype.nodeToken2.tokenImage;
        String pkg = super.getVisitorContext().getPackage();
        String typeImport = super.resolveImport(type);

        try {

            XmlTemplate ormTemplate = this.getVisitorContext().getTemplate(
                    NabuccoXmlTemplateConstants.ORM_TEMPLATE);

            // Transient Field
            if (nabuccoDatatype.nodeOptional.present()) {
                this.createTransientField(refName, attributeList);
                return;
            }

            // NType cannot be mapped
            if (NabuccoToJavaBasetypeMapping.N_TYPE.getName().equals(type)) {
                this.createTransientField(type, attributeList);
                return;
            }

            NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                    .mapToMultiplicity(nabuccoDatatype.nodeToken1.tokenImage);

            if (NabuccoCompilerSupport.isOtherComponent(pkg, typeImport)
                    && !NabuccoCompilerSupport.isBase(typeImport)) {

                if (multiplicity.isMultiple()) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Cannot create a reference for lists of datatypes between different components for ");
                    msg.append(refName).append(" in ").append(name).append(".");
                    throw new IllegalArgumentException(msg.toString());
                }

                this.createReferenceId(refName, attributeList);

            } else {

                AssociationStrategyType association = this
                        .extractAssociationStrategy(nabuccoDatatype);

                FetchStrategyType fetch = this.extractFetchStrategy(nabuccoDatatype);

                Element relation = NabuccoToXmlDatatypeVisitorSupport.resolveMapping(multiplicity,
                        association, ormTemplate);

                relation.setAttribute(NAME, refName);
                relation.setAttribute(TARGET_ENTITY, typeImport);
                relation.setAttribute(FETCH, fetch.getId());

                String foreignKey;
                if (association == AssociationStrategyType.COMPOSITION && multiplicity.isMultiple()) {
                    foreignKey = NabuccoTransformationUtility.toTableName(name)
                            + TABLE_SEPARATOR + ID;
                } else {
                    foreignKey = NabuccoTransformationUtility.toTableName(refName)
                            + TABLE_SEPARATOR + ID;
                }

                // 1:1, 1:N, N:1, N:N Mappings
                if (association == AssociationStrategyType.AGGREGATION && multiplicity.isMultiple()) {
                    this.createJoinTable(relation, name, type);
                } else {
                    this.createJoinColumn(relation, foreignKey);
                }

                attributeList.add(relation);
            }

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Cannot extract XML template '"
                    + NabuccoXmlTemplateConstants.ORM_TEMPLATE + "'", te);
        }
    }

    /**
     * Checks whether a type is of another componentName and creates a reference ID.
     * 
     * @param name
     *            the element name
     * 
     * @throws XmlTemplateException
     */
    private void createReferenceId(String name, List<Node> attributeList)
            throws XmlTemplateException {

        this.createTransientField(name, attributeList);

        XmlTemplate ormTemplate = this.getVisitorContext().getTemplate(
                NabuccoXmlTemplateConstants.ORM_TEMPLATE);

        List<Node> nodes = ormTemplate.copyNodesByXPath(XPATH_BASIC);

        if (nodes.size() == 1 && nodes.get(0) instanceof Element) {
            Element reference = (Element) nodes.get(0);
            reference.setAttribute(NAME, name + REF_ID);
            attributeList.add(reference);
        }
    }

    /**
     * Creates a join-column mapping for 1:1, 1:N, N:1 relationships.
     * 
     * @param mapping
     *            the xml mapping tag
     * @param name
     *            the foreign key name
     */
    private void createJoinColumn(Element mapping, String name) {
        Element joinColumn = (Element) mapping.getElementsByTagName(JOIN_COLUMN).item(0);
        joinColumn.setAttribute(NAME, name);
    }

    /**
     * Creates a join-table mapping for M:N relationships.
     * 
     * @param mapping
     *            the xml mapping tag
     * @param firstType
     *            the first type name
     * @param secondType
     *            the second type name
     */
    private void createJoinTable(Element mapping, String firstType, String secondType) {

        Element joinTable = (Element) mapping.getElementsByTagName(JOIN_TABLE).item(0);

        String first = NabuccoTransformationUtility.toTableName(firstType);
        String second = NabuccoTransformationUtility.toTableName(secondType);

        if (first.equalsIgnoreCase(second)) {
            first = first + TABLE_SEPARATOR + 1;
            second = second + TABLE_SEPARATOR + 2;
        }

        String tableName = first + TABLE_SEPARATOR + second;
        String joinColumnName = first + TABLE_SEPARATOR + ID;
        String inverseColumnName = second + TABLE_SEPARATOR + ID;

        joinTable.setAttribute(NAME, tableName);

        Element joinColumn = (Element) joinTable.getElementsByTagName(JOIN_COLUMN).item(0);
        Element inverseJoinColumn = (Element) joinTable.getElementsByTagName(INVERSE_JOIN_COLUMN)
                .item(0);

        joinColumn.setAttribute(NAME, joinColumnName);
        inverseJoinColumn.setAttribute(NAME, inverseColumnName);
    }

    /**
     * Extracts the association strategy of a datatype declaration
     * 
     * @param nabuccoDatatype
     *            the datatype holding the annotations
     * 
     * @return the association strategy type
     */
    private AssociationStrategyType extractAssociationStrategy(DatatypeDeclaration nabuccoDatatype) {
        NabuccoAnnotation annotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoDatatype.annotationDeclaration, NabuccoAnnotationType.ASSOCIATION_STRATEGY);

        if (annotation != null) {
            return AssociationStrategyType.getType(annotation.getValue());
        }
        return AssociationStrategyType.COMPOSITION;
    }

    /**
     * Extracts the fetch strategy of a datatype declaration
     * 
     * @param nabuccoDatatype
     *            the datatype holding the annotations
     * 
     * @return the fetch strategy type
     */
    private FetchStrategyType extractFetchStrategy(DatatypeDeclaration nabuccoDatatype) {
        NabuccoAnnotation annotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoDatatype.annotationDeclaration, NabuccoAnnotationType.FETCH_STRATEGY);

        if (annotation != null) {
            return FetchStrategyType.getType(annotation.getValue());
        }
        return FetchStrategyType.LAZY;
    }

    /**
     * Creates the entity/mapped-superclass relation to a basic type.
     * 
     * @param nabuccoBasetype
     *            the basetype
     * @param attributeList
     *            the list to add the mapping
     */
    void createBasetypeRelation(BasetypeDeclaration nabuccoBasetype, List<Node> attributeList) {

        String type = nabuccoBasetype.nodeToken1.tokenImage;
        String name = nabuccoBasetype.nodeToken3.tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoBasetype.nodeToken2.tokenImage);

        if (multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY) {
            throw new NabuccoVisitorException("Multiplicity '"
                    + multiplicity + "' is not supported for basetype '" + name + "'.");
        }

        String columnLength = NabuccoToXmlDatatypeVisitorSupport.extractColumnLength(
                nabuccoBasetype, super.getVisitorContext(), this.rootPackage,
                super.resolveImport(type));

        try {
            XmlTemplate ormTemplate = super.getVisitorContext().getTemplate(
                    NabuccoXmlTemplateConstants.ORM_TEMPLATE);

            // Transient Field
            if (nabuccoBasetype.nodeOptional.present()) {
                this.createTransientField(name, attributeList);
            } else {
                Element element = NabuccoToXmlDatatypeVisitorSupport.resolveElementType(
                        nabuccoBasetype.annotationDeclaration, ormTemplate, name, columnLength);

                if (this.isLarge(nabuccoBasetype)) {
                    List<Node> nodes = ormTemplate.copyNodesByXPath(XPATH_LOB);
                    if (nodes.size() > 0) {
                        element.appendChild(nodes.get(0));
                    }
                }

                attributeList.add(element);
            }

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error creating XML basetype '" + name + "'.", te);
        }
    }

    /**
     * Checks whether a basetype has the @Large annotation or not.
     * 
     * @param nabuccoBasetype
     *            the basetype holding the annotation
     * 
     * @return <b>true</b> if the basetype is large, <b>false</b> if not
     */
    private boolean isLarge(BasetypeDeclaration nabuccoBasetype) {

        // Since Basetypes are Embeddables, they must not be LOBs

        // try {
        // NabuccoAnnotation annotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
        // nabuccoBasetype.annotationDeclaration, NabuccoAnnotationType.LARGE);
        //
        // if (annotation != null) {
        // return true;
        // }
        // return false;
        // } catch (NabuccoAnnotationTransformationException te) {
        // throw new NabuccoVisitorException("Cannot resolve annotation '"
        // + NabuccoAnnotationType.LARGE + "'.", te);
        // }

        return false;
    }

    /**
     * Creates the entity/mapped-superclass relation to an enumeration.
     * 
     * @param nabuccoEnum
     *            the enumeration
     * @param attributeList
     *            the list to add the mapping
     */
    void createEnumRelation(EnumerationDeclaration nabuccoEnum, List<Node> attributeList) {

        String name = nabuccoEnum.nodeToken2.tokenImage;

        NabuccoMultiplicityType multiplicity = NabuccoMultiplicityTypeMapper.getInstance()
                .mapToMultiplicity(nabuccoEnum.nodeToken1.tokenImage);

        if (multiplicity == NabuccoMultiplicityType.ONE_TO_MANY
                || multiplicity == NabuccoMultiplicityType.ZERO_TO_MANY) {
            throw new NabuccoVisitorException("Multiplicity '"
                    + multiplicity + "' is not supported for enumeration '" + name + "'.");
        }

        try {
            XmlTemplate ormTemplate = this.getVisitorContext().getTemplate(
                    NabuccoXmlTemplateConstants.ORM_TEMPLATE);

            List<Node> basicNodes = ormTemplate.copyNodesByXPath(XPATH_BASIC);
            List<Node> enumNodes = ormTemplate.copyNodesByXPath(XPATH_ENUM);

            if (basicNodes.size() == 1 && basicNodes.get(0) instanceof Element) {
                Element reference = (Element) basicNodes.get(0);
                reference.setAttribute(NAME, name);
                if (enumNodes.size() == 1 && enumNodes.get(0) instanceof Element) {
                    Element enumeration = (Element) enumNodes.get(0);
                    reference.appendChild(enumeration);
                }
                attributeList.add(reference);
            }

        } catch (XmlTemplateException te) {
            throw new NabuccoVisitorException("Error creating XML enum '" + name + "'.", te);
        }
    }

    /**
     * Getter for the componentName.
     * 
     * @return Returns the componentName.
     */
    public String getComponentName() {
        return this.componentName;
    }

    /**
     * Setter for the componentName.
     * 
     * @param componentName
     *            the componentName to set.
     */
    void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     * Creates a transient field for the given name.
     * 
     * @param name
     *            name of the field
     * @param attribute
     *            list the list of attributes
     * 
     * @throws XmlTemplateException
     *             if the template is not valid
     */
    private void createTransientField(String name, List<Node> attributeList)
            throws XmlTemplateException {

        XmlTemplate ormTemplate = this.getVisitorContext().getTemplate(
                NabuccoXmlTemplateConstants.ORM_TEMPLATE);

        List<Node> nodes = ormTemplate.copyNodesByXPath(XPATH_TRANSIENT);

        if (nodes.size() == 1 && nodes.get(0) instanceof Element) {
            Element reference = (Element) nodes.get(0);
            reference.setAttribute(NAME, name);
            attributeList.add(reference);
        }
    }

    /**
     * Creates a copy of the current visitor context. Templates and root dir is copied, nothing
     * else.
     * 
     * @return the context copy
     */
    private NabuccoToXmlVisitorContext copyContext() {
        NabuccoToXmlVisitorContext context = new NabuccoToXmlVisitorContext();
        super.getVisitorContext().copyTemplates(context);
        context.setRootDir(super.getVisitorContext().getRootDir());
        context.setOutDir(super.getVisitorContext().getOutDir());
        return context;
    }

    /**
     * Resolves a dependency for a full quallified name.
     * 
     * @param name
     *            the full qualified name name
     * 
     * @return the resolved NABUCCO model
     * 
     * @throws NabuccoTransformationException
     */
    private MdaModel<NabuccoModel> resolveDependency(String name)
            throws NabuccoTransformationException {

        MdaModel<NabuccoModel> model = NabuccoDependencyResolver.getInstance().resolveDependency(
                super.getVisitorContext(), this.rootPackage, name);

        if (model.getModel() == null) {
            throw new IllegalStateException("Cannot resolve dependency "
                    + name + ". NabuccoModel is corrupt.");
        }

        return model;
    }

}
