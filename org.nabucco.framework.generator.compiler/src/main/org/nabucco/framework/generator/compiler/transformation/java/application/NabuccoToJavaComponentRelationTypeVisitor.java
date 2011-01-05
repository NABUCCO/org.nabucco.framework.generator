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
package org.nabucco.framework.generator.compiler.transformation.java.application;

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.constants.ComponentRelationConstants;
import org.nabucco.framework.generator.compiler.transformation.java.application.connector.util.ComponentRelationTypeLiteralComparator;
import org.nabucco.framework.generator.compiler.transformation.java.application.connector.util.DatatypeCollector;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.ApplicationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ConnectorStatement;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaComponentRelationTypeVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaComponentRelationTypeVisitor extends NabuccoToJavaVisitorSupport implements
        ComponentRelationConstants {

    private ApplicationStatement application;

    private JavaCompilationUnit unit;
    
    private boolean hasFields = false;

    private static final Object CONNECTOR_TYPE_DATATYPE = "DATATYPE";

    private static final JavaAstMethodSignature SIGNATURE_BY_SOURCE = new JavaAstMethodSignature(
            "valuesBySource", "Class");

    private static final JavaAstMethodSignature SIGNATURE_BY_TARGET = new JavaAstMethodSignature(
            "valuesByTarget", "Class");

    /**
     * Creates a new {@link NabuccoToJavaComponentRelationTypeVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaComponentRelationTypeVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ApplicationStatement nabuccoApplication, MdaModel<JavaModel> target) {

        this.application = nabuccoApplication;

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = this.application.nodeToken2.tokenImage + COMPONENT_RELATION_TYPE;

        NabuccoModifierType modifier = NabuccoModifierComponentMapper
                .getModifierType(nabuccoApplication.nodeToken.tokenImage);

        String projectName = super.getProjectName(NabuccoModelType.CONNECTOR, modifier);

        try {
            // Load Template
            this.unit = super
                    .extractAst(NabuccoJavaTemplateConstants.COMPONENT_RELATION_TYPE_TEMPLATE);
            TypeDeclaration type = this.unit
                    .getType(NabuccoJavaTemplateConstants.COMPONENT_RELATION_TYPE_TEMPLATE);

            javaFactory.getJavaAstType().setTypeName(type, name);

            this.createPackage();

            // Enums cannot be loaded appropriately, templates are loaded as class and are converted
            javaFactory.getJavaAstType().addModifier(type, ClassFileConstants.AccEnum);

            for (ConstructorDeclaration constructor : javaFactory.getJavaAstType().getConstructors(
                    type)) {
                constructor.constructorCall = null;
            }

            this.createMethods(type);

            // File creation
            this.unit.setProjectName(projectName);
            this.unit.setSourceFolder(super.getSourceFolder());

            super.visit(nabuccoApplication, target);

            if (this.hasFields) {
                target.getModel().getUnitList().add(this.unit);
            }

            Arrays.sort(type.fields, ComponentRelationTypeLiteralComparator.getInstance());

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error creating ComponentRelationType.", me);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error creating ComponentRelationType.", te);
        }
    }

    /**
     * Create the package of the connector.
     * 
     * @throws JavaModelException
     */
    private void createPackage() throws JavaModelException {
        StringBuilder pkg = new StringBuilder();
        pkg.append(super.getVisitorContext().getPackage());
        pkg.append(PKG_SEPARATOR);
        pkg.append(CR_PACKAGE);

        JavaAstElementFactory.getInstance().getJavaAstUnit()
                .setPackage(this.unit.getUnitDeclaration(), pkg.toString());
    }

    /**
     * Create the valuesBySource() and valuesByTarget() methods.
     * 
     * @param type
     *            the public type
     * 
     * @throws JavaModelException
     */
    private void createMethods(TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        String relationType = this.application.nodeToken2.tokenImage + COMPONENT_RELATION_TYPE;
        TypeReference relationTypeRef = producer.createTypeReference(relationType, false);

        MethodDeclaration bySource = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                type, SIGNATURE_BY_SOURCE);

        MethodDeclaration byTarget = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                type, SIGNATURE_BY_TARGET);

        this.createMethod(relationTypeRef, bySource);
        this.createMethod(relationTypeRef, byTarget);

    }

    /**
     * Modifies the given values() method.
     * 
     * @param relationType
     *            the relation type
     * @param method
     *            the method to adjust
     * 
     * @throws JavaModelException
     */
    private void createMethod(TypeReference relationType, MethodDeclaration method)
            throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        ParameterizedSingleTypeReference returnType = (ParameterizedSingleTypeReference) javaFactory
                .getJavaAstMethod().getReturnType(method);

        returnType.typeArguments = new TypeReference[] { relationType };

        LocalDeclaration list = (LocalDeclaration) method.statements[0];
        list.type = returnType;

        AllocationExpression allocation = (AllocationExpression) list.initialization;
        ParameterizedSingleTypeReference arrayList = (ParameterizedSingleTypeReference) allocation.type;
        arrayList.typeArguments = new TypeReference[] { relationType };

        ForeachStatement foreach = (ForeachStatement) method.statements[1];
        foreach.elementVariable.type = relationType;

        MessageSend callValues = (MessageSend) foreach.collection;
        callValues.receiver = relationType;
    }

    /**
     * Create the enum literals for the component relation type.
     * 
     * @param nabuccoConnector
     *            the connector
     * 
     * @throws JavaModelException
     */
    @Override
    public void visit(ConnectorStatement nabuccoConnector, MdaModel<JavaModel> target) {

        NabuccoAnnotation connectorType = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoConnector.annotationDeclaration, NabuccoAnnotationType.CONNECTOR_TYPE);

        if (!connectorType.getValue().equals(CONNECTOR_TYPE_DATATYPE)) {
            return;
        }

        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        DatatypeCollector collector = new DatatypeCollector(super.getVisitorContext(),
                this.application);
        collector.accept(nabuccoConnector);

        String sourceType = collector.getSourceType();
        String upperCaseSource = sourceType.toUpperCase();

        try {

            String sourceImport = super.resolveImport(sourceType);
            ImportReference sourceImportRef = producer.createImportReference(sourceImport);
            javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(), sourceImportRef);

            ClassLiteralAccess sourceClass = producer.createClassLiteralAccess(sourceType);

            for (String targetName : collector.getTargetMap().keySet()) {
                String targetType = collector.getTargetMap().get(targetName);
                String upperCaseTarget = targetType.toUpperCase();
                String literalName = upperCaseSource + CONSTANT_SEPARATOR + upperCaseTarget;

                FieldDeclaration literal = producer.createFieldDeclaration(literalName,
                        ClassFileConstants.AccDefault);

                String relationType = targetType + COMPONENT_RELATION;
                ClassLiteralAccess relationClass = producer.createClassLiteralAccess(relationType);
                ClassLiteralAccess targetClass = producer.createClassLiteralAccess(targetType);

                literal.initialization = producer.createAllocationExpression(null,
                        Arrays.asList(sourceClass, relationClass, targetClass));

                javaFactory.getJavaAstType().addField(this.unit.getType(), literal);

                String targetImport = super.resolveImport(targetType);
                ImportReference targetImportRef = producer.createImportReference(targetImport);
                javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(),
                        targetImportRef);

                String relationImport = super.resolveImport(targetType) + COMPONENT_RELATION;
                ImportReference relationImportRef = producer.createImportReference(relationImport);
                javaFactory.getJavaAstUnit().addImport(this.unit.getUnitDeclaration(),
                        relationImportRef);
            }
            
            this.hasFields = true;

        } catch (JavaModelException me) {
            throw new NabuccoVisitorException("Error creating ComponentRelationType.", me);
        }
    }
}
