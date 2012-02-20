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
package org.nabucco.framework.generator.compiler.transformation.confluence.signature;

import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;

/**
 * NabuccoSignatureDeclarationVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoSignatureDeclarationVisitor extends NabuccoToConfluenceVisitor {

    private static final String TOKEN_SAPARATOR = " ";

    private static final String SEMICOLON = ";";
    
    private StringBuilder signatureBuilder;

    /**
     * Creates a new {@link NabuccoToConfluenceDatatypeVisitor} instance.
     * Signature visitor visit all node token of the tree
     * 
     * @param visitorContext
     *            the confluence visitor context
     */
    public NabuccoSignatureDeclarationVisitor(NabuccoToConfluenceVisitorContext visitorContext,
            StringBuilder signatureBuilder) {
        super(visitorContext);

        this.signatureBuilder = signatureBuilder;
    }

    @Override
    public void visit(AnnotationDeclaration n, MdaModel<ConfluenceModel> argu) {

    }

    @Override
    public void visit(NodeToken n, MdaModel<ConfluenceModel> argu) {
        if (!n.tokenImage.equals(SEMICOLON)) {
            signatureBuilder.append(n.tokenImage);
            signatureBuilder.append(TOKEN_SAPARATOR);
        }
    }

}
