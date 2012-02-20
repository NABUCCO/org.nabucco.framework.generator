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

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeFacade;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.compiler.verifier.support.NabuccoTraversingVerifier;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;

/**
 * NabuccoMemberVerification to verify that a member name is only used once. and method signatures
 * are unique.
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoMemberVerification extends NabuccoTraversingVerifier {

    private Set<String> names;

    private static final String ERROR_MESASGE = "Duplicate member {0}";

    private MessageFormat errorMessage = new MessageFormat(ERROR_MESASGE);

    public NabuccoMemberVerification(String rootDir, String outDir) {
        super(rootDir, outDir);

    }

    @Override
    public void visit(NabuccoUnit n, VerificationResult argu) {
        names = new HashSet<String>();
        super.visit(n, argu);
    }

    @Override
    public void visit(BasetypeDeclaration n, VerificationResult argu) {
        if (!getAnnotationMapper().hasAnnotation(n.annotationDeclaration,
                NabuccoAnnotationType.REDEFINED)) {
            check(n.nodeToken3, argu);
        }
        super.visit(n, argu);
    }

    @Override
    public void visit(DatatypeDeclaration n, VerificationResult argu) {
        if (!getAnnotationMapper().hasAnnotation(n.annotationDeclaration,
                NabuccoAnnotationType.REDEFINED)) {
            check(n.nodeToken2, argu);
        }
        super.visit(n, argu);
    }

    @Override
    public void visit(EnumerationDeclaration n, VerificationResult argu) {
        if (!getAnnotationMapper().hasAnnotation(n.annotationDeclaration,
                NabuccoAnnotationType.REDEFINED)) {
            check(n.nodeToken2, argu);
        }
        ;
        super.visit(n, argu);
    }

    @Override
    public void visit(MethodDeclaration n, VerificationResult argu) {
        StringBuffer sb = new StringBuffer();
        NodeToken checkedToken = n.nodeToken1;
        sb.append(n.nodeToken1.tokenImage);
        sb.append("(");
        if (n.parameterList != null
                && n.parameterList.nodeListOptional != null
                && n.parameterList.nodeListOptional.present()) {
            Iterator<Node> iterator = n.parameterList.nodeListOptional.nodes.iterator();
            // can only contain instances of Parameter...
            while (iterator.hasNext()) {
                Node next = iterator.next();
                if (next instanceof Parameter) {
                    Parameter asParam = (Parameter) next;
                    if (asParam.nodeToken != null) {
                        sb.append((asParam).nodeToken.tokenImage);
                    }
                }
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }

        }
        sb.append(")");
        checkedToken = new NodeToken(sb.toString(), checkedToken.kind, checkedToken.beginLine,
                checkedToken.beginColumn, checkedToken.endLine, checkedToken.endColumn);
        check(checkedToken, argu);
        super.visit(n, argu);
    }

    @Override
    public void visit(ExtensionDeclaration n, VerificationResult argu) {
        Node choice = n.nodeChoice.choice;
        if (choice instanceof NodeToken) {
            String tokenImage = ((NodeToken) choice).tokenImage;
            if (!BasetypeFacade.isBasetype(tokenImage)) {
                NabuccoModel resolvedModel = resolveModel(tokenImage);
                if (resolvedModel != null
                        && resolvedModel.getUnit() != null
                        && resolvedModel.getUnit().nabuccoStatement != null) {

                    // set package
                    resolvedModel.getUnit().packageDeclaration.accept(this, argu);
                    // gather imports [NBC-1305]
                    resolvedModel.getUnit().nodeListOptional.accept(this, argu);
                    // sub visit
                    resolvedModel.getUnit().nabuccoStatement.accept(this, argu);
                }
            }
        }
        super.visit(n, argu);
    }

    private void check(NodeToken token, VerificationResult result) {
        if (!names.add(token.tokenImage)) {
            result.addError(VerificationErrorCriticality.ERROR, token.beginLine, token.endLine,
                    token.beginColumn, token.endColumn,
                    errorMessage.format(new Object[] { token.tokenImage }));
        }
    }

}
