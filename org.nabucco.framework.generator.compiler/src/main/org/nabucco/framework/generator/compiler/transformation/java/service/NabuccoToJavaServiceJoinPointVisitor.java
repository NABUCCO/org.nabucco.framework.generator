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
package org.nabucco.framework.generator.compiler.transformation.java.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaServiceJoinPointVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaServiceJoinPointVisitor extends NabuccoToJavaVisitorSupport {

    /** The getAspects method signature. */
    private static final JavaAstMethodSignature SIGNATURE_GET_ASPECTS = new JavaAstMethodSignature("getAspects",
            "String");

    private static final String NO_ASPECTS = "NO_ASPECTS";

    private org.eclipse.jdt.internal.compiler.ast.MethodDeclaration getAspects;

    private SingleNameReference aspectConstant;

    private Block aspectStatements;

    /**
     * Creates a new {@link NabuccoToJavaServiceJoinPointVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaServiceJoinPointVisitor(JavaCompilationUnit unit, NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);

        try {

            this.getAspects = (org.eclipse.jdt.internal.compiler.ast.MethodDeclaration) JavaAstElementFactory
                    .getInstance().getJavaAstType().getMethod(unit.getType(), SIGNATURE_GET_ASPECTS);

            this.aspectConstant = JavaAstModelProducer.getInstance().createSingleNameReference("ASPECTS");

            IfStatement ifStatement = (IfStatement) this.getAspects.statements[0];
            this.aspectStatements = (Block) ifStatement.thenStatement;

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Cannot find method 'getAspects' in java service template.");
        }
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, MdaModel<JavaModel> target) {

        try {
            String name = nabuccoMethod.nodeToken1.tokenImage;

            List<NabuccoAnnotation> annotationList = NabuccoAnnotationMapper.getInstance().mapToAnnotationList(
                    nabuccoMethod.annotationDeclaration, NabuccoAnnotationType.JOIN_POINT);

            JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

            Literal nameLiteral = producer.createLiteral(name, LiteralType.STRING_LITERAL);

            List<Expression> joinPoints = new ArrayList<Expression>();

            for (NabuccoAnnotation annotation : annotationList) {
                joinPoints.add(producer.createLiteral(annotation.getValue(), LiteralType.STRING_LITERAL));
            }

            Expression array;

            if (!joinPoints.isEmpty()) {
                ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
                arrayAllocation.type = producer.createTypeReference("String", false);
                arrayAllocation.dimensions = new Expression[1];
                arrayAllocation.initializer = new ArrayInitializer();
                arrayAllocation.initializer.expressions = joinPoints.toArray(new Expression[joinPoints.size()]);
                array = arrayAllocation;
            } else {
                array = producer.createSingleNameReference(NO_ASPECTS);
            }

            MessageSend statement = producer.createMessageSend("put", this.aspectConstant,
                    Arrays.asList(nameLiteral, array));

            this.aspectStatements.statements = Arrays.copyOf(this.aspectStatements.statements,
                    this.aspectStatements.statements.length + 1);
            this.aspectStatements.statements[this.aspectStatements.statements.length - 1] = statement;

        } catch (JavaModelException jme) {
            throw new NabuccoVisitorException("Error creating service operation aspect.");
        }

        super.visit(nabuccoMethod, target);
    }
}
