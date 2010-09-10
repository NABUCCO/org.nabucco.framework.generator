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
package org.nabucco.framework.generator.compiler.transformation.java.enumeration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.nabucco.framework.generator.compiler.template.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.mapper.NabuccoModifierComponentMapper;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationLiteralDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaEnumerationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaEnumerationVisitor extends NabuccoToJavaVisitorSupport {

    private Set<String> literals = new HashSet<String>();

    private Set<String> literalIds = new HashSet<String>();

    private static final String DUMMY_LITERAL = "ENUM_LITERAL";

    private static final String CONSTRUCTOR_ARGUMENT = "String";

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoToJavaEnumerationVisitor.class);

    public NabuccoToJavaEnumerationVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EnumerationStatement nabuccoEnum, MdaModel<JavaModel> target) {

        // Visit sub-nodes first!
        super.visit(nabuccoEnum, target);

        String name = nabuccoEnum.nodeToken2.tokenImage;
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        String projectName = super.getComponentName(NabuccoModelType.ENUMERATION,
                NabuccoModifierComponentMapper.getModifierType(nabuccoEnum.nodeToken.tokenImage));

        try {
            // Load Template
            JavaCompilationUnit unit = super.extractAst(NabuccoJavaTemplateConstants.ENUM_TEMPLATE);
            TypeDeclaration type = unit.getType(NabuccoJavaTemplateConstants.ENUM_TEMPLATE);

            // Name and Package
            javaFactory.getJavaAstType().setTypeName(type, name);
            javaFactory.getJavaAstUnit().setPackage(unit.getUnitDeclaration(),
                    super.getVisitorContext().getPackage());

            // Super-classes
            super.createSuperClass();

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoEnum.annotationDeclaration, type);

            JavaAstSupport.convertAstNodes(unit, this.getVisitorContext().getContainerList(), this
                    .getVisitorContext().getImportList());

            // Sort enumeration fields
            javaFactory.getJavaAstType().beautify(type);

            this.removeDummyLiteral(type);
            this.removeSuperConstructorCall(type);

            // File creation
            unit.setProjectName(projectName);
            unit.setSourceFolder(super.getSourceFolder());

            target.getModel().getUnitList().add(unit);

        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST enum modification.");
            throw new NabuccoVisitorException("Error during Java AST enum modification.", jme);
        } catch (JavaTemplateException te) {
            logger.error(te, "Error during Java template enum processing.");
            throw new NabuccoVisitorException("Error during Java template enum processing.", te);
        }

    }

    /**
     * The dummy literal is necessary in the template, but must be removed.
     * 
     * @param type
     *            the java enumeration type
     * 
     * @throws JavaModelException
     */
    private void removeDummyLiteral(TypeDeclaration type) throws JavaModelException {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        FieldDeclaration dummyLiteral = javaFactory.getJavaAstType().getField(type, DUMMY_LITERAL);
        javaFactory.getJavaAstType().removeField(type, dummyLiteral);
    }

    /**
     * Removes the super constructor call generated by default.
     * 
     * @param type
     *            the java enumeration type
     * 
     * @throws JavaModelException
     */
    private void removeSuperConstructorCall(TypeDeclaration type) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String name = javaFactory.getJavaAstType().getTypeName(type);

        JavaAstMethodSignature signature = new JavaAstMethodSignature(name, CONSTRUCTOR_ARGUMENT);
        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type,
                signature);

        constructor.constructorCall = null;
    }

    @Override
    public void visit(EnumerationLiteralDeclaration nabuccoEnumLiteral, MdaModel<JavaModel> target) {

        String name = nabuccoEnumLiteral.nodeToken.tokenImage;

        if (this.literals.contains(name)) {
            throw new NabuccoVisitorException("Literal ["
                    + name + "] must not exist more than once.");
        }

        this.literals.add(name);

        try {
            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();
            FieldDeclaration literal = producer.createFieldDeclaration(name,
                    ClassFileConstants.AccDefault);

            String literalId = NabuccoAnnotationMapper.getInstance()
                    .mapToAnnotation(nabuccoEnumLiteral.annotationDeclaration,
                            NabuccoAnnotationType.LITERAL_ID).getValue();

            if (this.literalIds.contains(literalId)) {
                throw new NabuccoVisitorException("Literal ID ["
                        + literalId + "] must not exist more than once.");
            }

            this.literalIds.add(literalId);

            Literal stringLiteral = producer.createLiteral(literalId, LiteralType.STRING_LITERAL);
            literal.initialization = producer.createAllocationExpression(null, Arrays
                    .asList(stringLiteral));

            // Annotations
            JavaAstSupport.convertJavadocAnnotations(nabuccoEnumLiteral.annotationDeclaration,
                    literal);

            super.getVisitorContext().getContainerList().add(
                    new JavaAstContainter<FieldDeclaration>(literal, JavaAstType.FIELD));
        } catch (JavaModelException jme) {
            logger.error(jme, "Error during Java AST enum literal creation.");
            throw new NabuccoVisitorException("Error during Java AST enum literal creation.", jme);
        }

    }
}
