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
package org.nabucco.framework.generator.compiler.verifier.datatype;

import java.util.Iterator;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * NabuccoRedefineSubclass
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoRedefineSubclass extends NabuccoModelVerificationVisitor {

    private static final String NABUCCO_DATATYPE = "NabuccoDatatype";

    private DatatypeStatement current;

    private NabuccoAnnotationMapper mapper = NabuccoAnnotationMapper.getInstance();

    @Override
    public void visit(DatatypeStatement n, VerificationResult argu) {
        this.current = n;
        super.visit(n, argu);
        this.current = null;
    }

    @Override
    public void visit(BasetypeDeclaration n, VerificationResult argu) {
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.REDEFINED) && subclassCheck()) {
            complain(n.annotationDeclaration, argu);
        }
        super.visit(n, argu);
    }

    @Override
    public void visit(DatatypeDeclaration n, VerificationResult argu) {
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.REDEFINED) && subclassCheck()) {
            complain(n.annotationDeclaration, argu);
        }
        super.visit(n, argu);
    }

    @Override
    public void visit(EnumerationDeclaration n, VerificationResult argu) {
        if (mapper.hasAnnotation(n.annotationDeclaration, NabuccoAnnotationType.REDEFINED) && subclassCheck()) {
            complain(n.annotationDeclaration, argu);
        }
        super.visit(n, argu);
    }

    private void complain(AnnotationDeclaration annotationDeclaration, VerificationResult result) {
        Iterator<Node> iterator = annotationDeclaration.nodeListOptional.nodes.iterator();
        while (iterator.hasNext()) {
            Node next = iterator.next();
            NodeToken token = (NodeToken) next;
            if (token.tokenImage.compareTo(NabuccoAnnotationType.REDEFINED.getName()) == 0) {
                result.addError(VerificationErrorCriticality.WARNING, token.beginLine, token.endLine,
                        token.beginColumn, token.endColumn, "Illegal use of @Redefine Annotation, not subclassing");
            }

        }

    }

    private boolean subclassCheck() {
        if (current == null) {
            return false;
        }
        if (!current.nodeOptional1.present()) {
            return true;
        } else if (current.nodeOptional1.node instanceof ExtensionDeclaration) {
            ExtensionDeclaration node = (ExtensionDeclaration) current.nodeOptional1.node;
            NodeToken tokenImage = (NodeToken) node.nodeChoice.choice;
            String superType = tokenImage.tokenImage;
            if (superType.isEmpty() || superType.endsWith(NABUCCO_DATATYPE)) {
                return true;
            }
        }
        return false;
    }

}
