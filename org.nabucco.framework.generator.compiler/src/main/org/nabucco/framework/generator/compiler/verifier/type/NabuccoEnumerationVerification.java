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
package org.nabucco.framework.generator.compiler.verifier.type;

import java.util.HashSet;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationLiteralDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;

import org.nabucco.framework.mda.model.MdaModel;

/**
 * EnumerationVerification
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class NabuccoEnumerationVerification extends NabuccoModelVerificationVisitor {

    private Set<String> literalNames = new HashSet<String>();

    private Set<String> literalIds = new HashSet<String>();

    private EnumerationStatement currentEnumerationStatement = null;

    @Override
    public void visit(EnumerationStatement nabuccoEnum, MdaModel<NabuccoModel> target) {
        currentEnumerationStatement = nabuccoEnum;
        super.visit(nabuccoEnum, target);
    }

    @Override
    public void visit(EnumerationLiteralDeclaration nabuccoLiteral, MdaModel<NabuccoModel> target) {
        super.visit(nabuccoLiteral, target);
        if (!literalNames.add(nabuccoLiteral.nodeToken.tokenImage)) {
            duplicateLiteralName(nabuccoLiteral, nabuccoLiteral.nodeToken.tokenImage);
        }
        NabuccoAnnotation literalId = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoLiteral.annotationDeclaration, NabuccoAnnotationType.LITERAL_ID);
        if (literalId == null) {
            noValidLiteralId(nabuccoLiteral);
        } else if (literalId.getValue() == null) {
            noValidLiteralId(nabuccoLiteral);
        } else if (!literalIds.add(literalId.getValue())) {
            duplicateLiteralId(nabuccoLiteral, literalId.getValue());
        }
    }

    private final void noValidLiteralId(EnumerationLiteralDeclaration nabuccoLiteral) {
        logger.warning("Enumeration "
                + currentEnumerationStatement.nodeToken2.tokenImage
                + ", Enumeration Literal Declaration : " + nabuccoLiteral.nodeToken.tokenImage
                + " does not define a literalId Annotation");
    }

    private final void duplicateLiteralId(EnumerationLiteralDeclaration nabuccoLiteral,
            String literalId) {
        logger.warning("Enumeration "
                + currentEnumerationStatement.nodeToken2.tokenImage
                + " Enumeration Literal Declaration : " + nabuccoLiteral.nodeToken.tokenImage
                + " defines duplicated a literalId: " + literalId);
    }

    private final void duplicateLiteralName(EnumerationLiteralDeclaration nabuccoLiteral,
            String literalName) {
        logger.warning("Enumeration "
                + currentEnumerationStatement.nodeToken2.tokenImage
                + " Enumeration Literal Declaration : " + nabuccoLiteral.nodeToken.tokenImage
                + " defines duplicated a literal name: " + literalName);
    }
}
