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

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationLiteralDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;

/**
 * EnumerationVerification
 * 
 * @author Silas Schwarz PRODYNA AG
 * @author Nicolas Moser PRODYNA AG
 */
public class NabuccoEnumerationVerification extends NabuccoModelVerificationVisitor {

    /** The current enumeration. */
    private EnumerationStatement enumStatement;

    /** The enumeration literal ids. */
    private Set<String> literalIds = new HashSet<String>();

    /** The enumeration literal names. */
    private Set<String> literalNames = new HashSet<String>();

    @Override
    public void visit(EnumerationStatement nabuccoEnum, VerificationResult result) {
        enumStatement = nabuccoEnum;
        super.visit(nabuccoEnum, result);
    }

    @Override
    public void visit(EnumerationLiteralDeclaration nabuccoLiteral, VerificationResult result) {

        super.visit(nabuccoLiteral, result);

        String literalName = nabuccoLiteral.nodeToken.tokenImage;

        if (!literalNames.add(literalName)) {
            duplicateLiteralName(nabuccoLiteral, literalName, result);
        }

        NabuccoAnnotation literalId = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoLiteral.annotationDeclaration, NabuccoAnnotationType.LITERAL_ID);

        if (literalId == null) {
            noValidLiteralId(nabuccoLiteral, result);
        } else if (literalId.getValue() == null || literalId.getValue().isEmpty()) {
            noValidLiteralId(nabuccoLiteral, result);
        } else if (!literalIds.add(literalId.getValue())) {
            duplicateLiteralId(nabuccoLiteral, literalId.getValue(), result);
        }
    }

    /**
     * Warn for missing LiteralId annotation.
     * 
     * @param nabuccoLiteral
     *            the enum literal
     * @param result
     *            the verification result
     */
    private void noValidLiteralId(EnumerationLiteralDeclaration nabuccoLiteral, VerificationResult result) {
        String name = enumStatement.nodeToken2.tokenImage;
        String literal = nabuccoLiteral.nodeToken.tokenImage;
        int beginLine = nabuccoLiteral.nodeToken.beginLine;
        int endLine = nabuccoLiteral.nodeToken.endLine;
        int beginColumn = nabuccoLiteral.nodeToken.beginColumn;
        int endColumn = nabuccoLiteral.nodeToken.endColumn;

        result.addError(VerificationErrorCriticality.ERROR, beginLine, endLine, beginColumn, endColumn, "Enumeration ",
                name, ", Enumeration Literal Declaration : ", literal, " does not define a @LiteralId Annotation.");
    }

    /**
     * Warn for duplicate LitearalId annotation.
     * 
     * @param nabuccoLiteral
     *            the enum literal
     * @param literalId
     *            the literal id
     * @param result
     *            the verification result
     */
    private void duplicateLiteralId(EnumerationLiteralDeclaration nabuccoLiteral, String literalId,
            VerificationResult result) {
        String name = enumStatement.nodeToken2.tokenImage;
        String literal = nabuccoLiteral.nodeToken.tokenImage;
        int beginLine = nabuccoLiteral.nodeToken.beginLine;
        int endLine = nabuccoLiteral.nodeToken.endLine;
        int beginColumn = nabuccoLiteral.nodeToken.beginColumn;
        int endColumn = nabuccoLiteral.nodeToken.endColumn;

        result.addError(VerificationErrorCriticality.ERROR, beginLine, endLine, beginColumn, endColumn, "Enumeration ",
                name, " Enumeration Literal Declaration : ", literal, " defines duplicated a @LiteralId: ", literalId);
    }

    /**
     * Warn for duplicate literal name.
     * 
     * @param nabuccoLiteral
     *            the enum literal
     * @param literalName
     *            the literal name
     * @param result
     *            the verification result
     */
    private void duplicateLiteralName(EnumerationLiteralDeclaration nabuccoLiteral, String literalName,
            VerificationResult result) {
        String name = enumStatement.nodeToken2.tokenImage;
        String literal = nabuccoLiteral.nodeToken.tokenImage;
        int beginLine = nabuccoLiteral.nodeToken.beginLine;
        int endLine = nabuccoLiteral.nodeToken.endLine;
        int beginColumn = nabuccoLiteral.nodeToken.beginColumn;
        int endColumn = nabuccoLiteral.nodeToken.endColumn;

        result.addError(VerificationErrorCriticality.ERROR, beginLine, endLine, beginColumn, endColumn, "Enumeration ",
                name, " Enumeration Literal Declaration : ", literal, " defines duplicated a literal name: ",
                literalName);
    }
}
