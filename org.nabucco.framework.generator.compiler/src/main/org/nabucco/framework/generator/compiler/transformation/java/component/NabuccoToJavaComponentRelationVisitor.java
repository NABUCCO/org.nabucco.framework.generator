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
package org.nabucco.framework.generator.compiler.transformation.java.component;

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.common.constants.ComponentRelationConstants;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaComponentRelationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaComponentRelationVisitor extends NabuccoToJavaVisitorSupport implements ComponentRelationConstants {

    private static final JavaAstMethodSignature SIGNATURE_GET_TARGET = new JavaAstMethodSignature("getTarget");

    private static final JavaAstMethodSignature SIGNATURE_SET_TARGET = new JavaAstMethodSignature("setTarget",
            "DatatypeTemplate");

    private static final JavaAstMethodSignature SIGNATURE_CLONE_OBJECT = new JavaAstMethodSignature("cloneObject");

    /**
     * Creates a new {@link NabuccoToJavaComponentRelationVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaComponentRelationVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(ComponentDatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {
        if (!NabuccoAnnotationMapper.getInstance().hasAnnotation(nabuccoDatatype.annotationDeclaration,
                NabuccoAnnotationType.REFERENCEABLE)) {
            return;
        }

        String type = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        String importString = super.resolveImport(type);

        try {
            MdaModel<NabuccoModel> model = NabuccoDependencyResolver.getInstance().resolveDependency(
                    super.getVisitorContext(), super.getVisitorContext().getPackage(), importString);

            if (model.getModel() == null) {
                throw new IllegalStateException("Cannot resolve dependency " + type + ". NabuccoModel is corrupt.");
            }

            model.getModel().getUnit().accept(this, target);

        } catch (NabuccoTransformationException te) {
            throw new NabuccoVisitorException("Cannot resolve dependency " + type + "", te);
        }
    }

    @Override
    public void visit(DatatypeStatement nabuccoDatatype, MdaModel<JavaModel> target) {

        String datatypeType = nabuccoDatatype.nodeToken2.tokenImage;
        String relationName = datatypeType + COMPONENT_RELATION;
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        String projectName = super.getProjectName(NabuccoModelType.DATATYPE,
                NabuccoModifierComponentMapper.getModifierType(nabuccoDatatype.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.COMPONENT_RELATION_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.COMPONENT_RELATION_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, relationName);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(), super.getVisitorContext().getPackage());

            this.modifyType(datatypeType, type);

            // Javadoc
            JavaAstSupport.convertJavadocAnnotations(nabuccoDatatype.annotationDeclaration, type);

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error during component relation creation.", jme);
        } catch (JavaTemplateException te) {
            throw new NabuccoVisitorException("Error during component relation creation.", te);
        }
    }

    /**
     * Modify the component relation by the concrete datatype.
     * 
     * @param datatypeType
     *            the datatype type
     * @param type
     *            the public java class
     * 
     * @throws JavaModelException
     */
    private void modifyType(String datatypeType, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        ParameterizedSingleTypeReference superClass = (ParameterizedSingleTypeReference) javaFactory.getJavaAstType()
                .getSuperClass(type);

        TypeReference datatypeTypeRef = producer.createTypeReference(datatypeType, false);

        superClass.typeArguments = new TypeReference[] { datatypeTypeRef };

        // getTarget()

        MethodDeclaration getTarget = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                SIGNATURE_GET_TARGET);

        javaFactory.getJavaAstMethod().setReturnType(getTarget, datatypeTypeRef);

        // setTarget()

        MethodDeclaration setTarget = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                SIGNATURE_SET_TARGET);

        List<Argument> arguments = javaFactory.getJavaAstMethod().getAllArguments(setTarget);

        if (arguments.size() != 1) {
            throw new IllegalStateException("ComponentRelationTemplate setTarget() is not valid.");
        }

        javaFactory.getJavaAstArgument().setType(arguments.get(0), datatypeTypeRef);

        // cloneObject()

        MethodDeclaration cloneObject = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type,
                SIGNATURE_CLONE_OBJECT);

        String typeName = javaFactory.getJavaAstType().getTypeName(type);
        TypeReference relationType = producer.createTypeReference(typeName, false);

        javaFactory.getJavaAstMethod().setReturnType(cloneObject, relationType);

        LocalDeclaration declaration = (LocalDeclaration) cloneObject.statements[0];
        declaration.type = relationType;
        AllocationExpression allocation = (AllocationExpression) declaration.initialization;
        allocation.type = relationType;
    }
}
