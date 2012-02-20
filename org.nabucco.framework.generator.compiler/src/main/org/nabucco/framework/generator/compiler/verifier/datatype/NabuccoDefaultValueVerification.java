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

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.compiler.verifier.support.NabuccoTraversingVerifier;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationLiteralDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;

/**
 * NabuccoDefaultValueVerification
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoDefaultValueVerification extends NabuccoTraversingVerifier {

    /**
     * Creates a new {@link NabuccoDefaultValueVerification} instance.
     * 
     * @param rootDir
     *            the root directory
     * @param outDir
     *            the ouput directory
     */
    public NabuccoDefaultValueVerification(String rootDir, String outDir) {
        super(rootDir, outDir);
    }

    @Override
    public void visit(BasetypeDeclaration basetype, VerificationResult result) {
        if (getAnnotationMapper().hasAnnotation(basetype.annotationDeclaration, NabuccoAnnotationType.DEFAULT)) {
            checkBasetypeValue(basetype, result);
        }
        super.visit(basetype, result);
    }

    private void checkBasetypeValue(BasetypeDeclaration basetype, VerificationResult result) {
        BasetypeDefaultValueVerifier basetypeDefaultValueVerifier = new BasetypeDefaultValueVerifier(getRootDir(),
                super.getOutDir(), basetype);
        NabuccoUnit resolvedUnit = super.resolveModel(basetype.nodeToken1.tokenImage).getUnit();
        resolvedUnit.accept(basetypeDefaultValueVerifier, result);
    }

    @Override
    public void visit(EnumerationDeclaration enumeration, VerificationResult result) {
        if (getAnnotationMapper().hasAnnotation(enumeration.annotationDeclaration, NabuccoAnnotationType.DEFAULT)) {
            checkEnumValue(enumeration, result);
        }
        super.visit(enumeration, result);
    }

    /**
     * Checks if a EnumerationLiteral exits that has been used as default Value
     * 
     * @param enumeration
     *            the enumeration to validate
     * @param result
     *            the verification result
     */
    private void checkEnumValue(EnumerationDeclaration enumeration, VerificationResult result) {
        String enumerationType = ((NodeToken) enumeration.nodeChoice1.choice).tokenImage;
        final String usedLiteral = getAnnotationMapper().mapToAnnotation(enumeration.annotationDeclaration,
                NabuccoAnnotationType.DEFAULT).getValue();
        NabuccoUnit enumType = super.resolveModel(enumerationType).getUnit();

        Boolean[] literalExists = new Boolean[] { Boolean.FALSE };

        // FIXME: Silas Schwarz: Remove Anonymous Classes!
        enumType.accept(new GJVoidDepthFirst<Boolean[]>() {

            @Override
            public void visit(EnumerationLiteralDeclaration n, Boolean[] argu) {
                if (n.nodeToken.tokenImage.compareTo(usedLiteral) == 0) {
                    argu[0] = Boolean.TRUE;
                }
                super.visit(n, argu);
            }

        }, literalExists);

        if (!literalExists[0]) {
            NodeToken firstElement = (NodeToken) enumeration.annotationDeclaration.nodeListOptional.nodes
                    .firstElement();
            result.addError(VerificationErrorCriticality.ERROR, firstElement.beginLine, firstElement.endLine,
                    firstElement.beginColumn, firstElement.endColumn,
                    "Default value refers to non existing enumeration literal");
        }
    }
}
