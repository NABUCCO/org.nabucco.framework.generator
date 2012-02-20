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
package org.nabucco.framework.generator.compiler.verifier.common;

import java.util.Map;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.syntaxtree.AdapterStatement;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ApplicationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.CommandStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ConnectorStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionStatement;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;


/**
 * NabuccoDeprecationVerification
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoDeprecationVerification extends NabuccoModelVerificationVisitor {
    private static final String STATEMENT = "statement";

    
    @Override
    public void visit(AdapterStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(ApplicationStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(BasetypeStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(CommandStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(ComponentStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(ConnectorStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(DatatypeStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(EnumerationStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(ExceptionStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(MessageStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }

    @Override
    public void visit(ServiceStatement n, VerificationResult result) {
        this.checkStatementAnnotation(n.annotationDeclaration, n.nodeToken2, result);
        super.visit(n, result);
    }


    /**
     * Check if the statement annotation is not deprecated
     * 
     * @param annotation
     *            annotation
     * @param owner
     *            statement
     * @param result
     *            errors list
     */
    private void checkStatementAnnotation(AnnotationDeclaration annotation, NodeToken owner, VerificationResult result) {
        Map<NabuccoAnnotationType, NabuccoAnnotation> annotationsMap = NabuccoAnnotationMapper.getInstance()
                .convertAnnotationsToMap(annotation);

        for (Node node : annotation.nodeListOptional.nodes) {

            NodeToken annotationNode = (NodeToken) node;
            String annotationName = annotationNode.tokenImage;

            if (!NabuccoAnnotationMapper.getInstance().isAnnotation(annotationName)) {

            }
        }

        if (annotationsMap.containsKey(NabuccoAnnotationType.DEPRECATED)) {
            createWarning(NabuccoAnnotationType.DEPRECATED, owner, STATEMENT, result);
        }
    }

   
    /**
     * Creates a warning about a missing annotation
     * 
     * @param annotationType
     *            type if missing annotation
     * @param owner
     *            owner nodetoken of the annotation
     * @param type
     *            property or statement
     * @param result
     *            errors list
     */
    private void createWarning(NabuccoAnnotationType annotationType, NodeToken owner, String type,
            VerificationResult result) {

        int beginLine = owner.beginLine;
        int endLine = owner.endLine;
        int beginColumn = owner.beginColumn;
        int endColumn = owner.endColumn;

        result.addError(VerificationErrorCriticality.WARNING, beginLine, endLine, beginColumn, endColumn,
                "The type ",owner.tokenImage, " is deprecated.");
    }

}
