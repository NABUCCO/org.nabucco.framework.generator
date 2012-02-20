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

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * RedefineVisibilityValidator
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoRedefineVisibilityVerification extends NabuccoModelVerificationVisitor {

    private static final String PRIVATE_TOKEN = "private";

    private static final NabuccoAnnotationMapper MAPPER = NabuccoAnnotationMapper.getInstance();

    private NodeToken checkElement;

    @Override
    public void visit(AnnotationDeclaration n, VerificationResult argu) {
        if (MAPPER.hasAnnotation(n, NabuccoAnnotationType.REDEFINED)
                && this.checkElement != null && this.checkElement.tokenImage.compareTo(PRIVATE_TOKEN) == 0) {
            argu.addError(VerificationErrorCriticality.ERROR, checkElement.beginLine, checkElement.endLine,
                    checkElement.beginColumn, checkElement.endColumn, "@Redefine is not allowed for private fields");
        }
        super.visit(n, argu);
    }

    @Override
    public void visit(BasetypeDeclaration n, VerificationResult argu) {
        checkElement = (NodeToken) n.nodeChoice.choice;
        super.visit(n, argu);
        checkElement = null;
    }

    @Override
    public void visit(EnumerationDeclaration n, VerificationResult argu) {
        this.checkElement = (NodeToken) n.nodeChoice.choice;
        super.visit(n, argu);
        this.checkElement = null;
    }
}
