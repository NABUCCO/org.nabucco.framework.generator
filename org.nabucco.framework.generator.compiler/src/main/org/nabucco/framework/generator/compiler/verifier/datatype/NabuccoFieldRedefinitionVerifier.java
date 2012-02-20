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

import java.util.HashSet;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.compiler.verifier.support.NabuccoTraversingVerifier;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * NabuccoFieldRedefinitionVerifier
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoFieldRedefinitionVerifier extends NabuccoTraversingVerifier {

    private Set<String> usedNames;

    /**
     * Creates a new {@link NabuccoFieldRedefinitionVerifier} instance.
     * 
     * @param rootDir
     *            the root directory
     * @param outDir
     *            the output directory
     */
    public NabuccoFieldRedefinitionVerifier(String rootDir, String outDir) {
        super(rootDir, outDir);
        this.usedNames = new HashSet<String>();
    }

    @Override
    public void visit(ExtensionDeclaration extension, VerificationResult result) {
        NodeToken choice = (NodeToken) extension.nodeChoice.choice;
        NabuccoUnit resolveUnit = resolveModel(choice.tokenImage).getUnit();
        if (resolveUnit != null) {
            resolveUnit.accept(this, result);
        }
        super.visit(extension, result);
    }

    @Override
    public void visit(EnumerationDeclaration enumeration, VerificationResult result) {
        NodeToken choice = enumeration.nodeToken2;
        check(enumeration.annotationDeclaration, choice, result);
        super.visit(enumeration, result);
    }

    @Override
    public void visit(BasetypeDeclaration basetype, VerificationResult result) {
        NodeToken token1 = basetype.nodeToken3;
        check(basetype.annotationDeclaration, token1, result);
        super.visit(basetype, result);
    }

    @Override
    public void visit(DatatypeDeclaration datatype, VerificationResult result) {
        NodeToken choice = datatype.nodeToken2;
        check(datatype.annotationDeclaration, choice, result);
        super.visit(datatype, result);
    }

    /**
     * @param annotationDeclaration
     * @param choice
     * @param result
     */
    private void check(AnnotationDeclaration annotationDeclaration, NodeToken token, VerificationResult result) {
        boolean hasAnnotation = getAnnotationMapper().hasAnnotation(annotationDeclaration,
                NabuccoAnnotationType.REDEFINED);
        if (!hasAnnotation && !usedNames.add(token.tokenImage)) {
            result.addError(VerificationErrorCriticality.WARNING, token.beginLine, token.endLine, token.beginColumn,
                    token.endColumn, "Missing @Redefined Annotation");
        }

    }

}
