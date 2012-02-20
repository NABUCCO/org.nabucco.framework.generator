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
package org.nabucco.framework.generator.compiler.verifier.service;

import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.compiler.verifier.support.NabuccoTraversingVerifier;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;

/**
 * NabuccoServiceExtensionVerification
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoServiceExtensionVerification extends NabuccoTraversingVerifier {

    private NodeToken extensionToken;

    /**
     * Creates a new {@link NabuccoServiceExtensionVerification} instance.
     * 
     * @param rootDir
     *            the root directory
     * @param outDir
     *            the out directory
     */
    protected NabuccoServiceExtensionVerification(String rootDir, String outDir) {
        super(rootDir, outDir);
    }

    @Override
    public void visit(ExtensionDeclaration nabuccoExtension, VerificationResult result) {
        this.extensionToken = (NodeToken) nabuccoExtension.nodeChoice.choice;
    }

    @Override
    public void visit(ServiceStatement service, VerificationResult result) {

        // Visit sub-nodes first!
        super.visit(service, result);

        boolean isAbstract = service.nodeOptional.present();
        boolean isExtension = service.nodeOptional1.present();

        if (isExtension) {

            // Abstract Services must not inherit from other services.
            if (isAbstract) {

                NodeToken token = service.nodeToken2;
                int beginLine = token.beginLine;
                int endLine = token.endLine;
                int beginColumn = token.beginColumn;
                int endColumn = token.endColumn;

                result.addError(VerificationErrorCriticality.ERROR, beginLine, endLine, beginColumn, endColumn,
                        "Abstract Services must not inherit from other services.");

            } else {
                NabuccoModel parent = super.resolveModel(this.extensionToken.tokenImage);
                NabuccoModelType type = parent.getNabuccoType();

                int beginLine = this.extensionToken.beginLine;
                int endLine = this.extensionToken.endLine;
                int beginColumn = this.extensionToken.beginColumn;
                int endColumn = this.extensionToken.endColumn;

                if (type == NabuccoModelType.SERVICE) {
                    NabuccoStatement statement = parent.getUnit().nabuccoStatement;
                    ServiceStatement parentService = ((ServiceStatement) statement.nodeChoice.choice);

                    if (!parentService.nodeOptional.present()) {
                        result.addError(VerificationErrorCriticality.ERROR, beginLine, endLine, beginColumn, endColumn,
                                "Services must only inherit from abstract services.");
                    }

                } else {
                    result.addError(VerificationErrorCriticality.ERROR, beginLine, endLine, beginColumn, endColumn,
                            this.extensionToken.tokenImage + " must be of type Service.");
                }

            }
        }
    }
}
