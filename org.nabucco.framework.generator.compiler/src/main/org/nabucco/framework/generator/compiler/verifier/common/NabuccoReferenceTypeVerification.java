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

import java.util.HashSet;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoReferenceTypeVerification
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoReferenceTypeVerification extends NabuccoModelVerificationVisitor implements
        NabuccoTransformationConstants {

    private String pkg;

    private String rootDirectory;

    private String outDirectory;

    private Set<String> imports = new HashSet<String>();

    /**
     * Creates a new {@link NabuccoReferenceTypeVerification} instance.
     * 
     * @param rootDirectory
     *            the project root directory
     * @param outDirectory
     *            the target directory
     */
    public NabuccoReferenceTypeVerification(String rootDirectory, String outDirectory) {
        this.rootDirectory = rootDirectory;
        this.outDirectory = outDirectory;
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, VerificationResult result) {
        this.pkg = nabuccoPackage.nodeToken1.tokenImage;
        super.visit(nabuccoPackage, result);
    }

    @Override
    public void visit(ImportDeclaration nabuccoImport, VerificationResult result) {
        this.imports.add(nabuccoImport.nodeToken1.tokenImage);
        super.visit(nabuccoImport, result);
    }

    @Override
    public void visit(BasetypeDeclaration declaration, VerificationResult result) {
        this.verifyType(declaration.nodeToken1, NabuccoModelType.BASETYPE, result);
    }

    @Override
    public void visit(DatatypeDeclaration declaration, VerificationResult result) {
        NodeToken token = (NodeToken) declaration.nodeChoice1.choice;
        this.verifyType(token, NabuccoModelType.DATATYPE, result);
    }

    @Override
    public void visit(EnumerationDeclaration declaration, VerificationResult result) {
        NodeToken token = (NodeToken) declaration.nodeChoice1.choice;
        this.verifyType(token, NabuccoModelType.ENUMERATION, result);
    }

    @Override
    public void visit(MethodDeclaration declaration, VerificationResult result) {

        if (declaration.nodeChoice.which == 1) {
            NodeToken token = (NodeToken) declaration.nodeChoice.choice;
            this.verifyType(token, NabuccoModelType.MESSAGE, result);
        }

        if (declaration.nodeOptional.present()) {
            NodeSequence sequence = (NodeSequence) declaration.nodeOptional.node;
            NodeToken token = (NodeToken) sequence.elementAt(1);
            this.verifyType(token, NabuccoModelType.EXCEPTION, result);
        }
    }

    @Override
    public void visit(Parameter declaration, VerificationResult result) {
        this.verifyType(declaration.nodeToken, NabuccoModelType.MESSAGE, result);
    }

    @Override
    public void visit(ServiceDeclaration declaration, VerificationResult result) {
        this.verifyType(declaration.nodeToken1, NabuccoModelType.SERVICE, result);
    }

    @Override
    public void visit(ComponentDeclaration declaration, VerificationResult result) {
        this.verifyType(declaration.nodeToken1, NabuccoModelType.COMPONENT, result);
    }

    /**
     * Verify the type of the choosen declaration.
     * 
     * @param token
     *            the token to verify
     * @param nabuccoType
     *            the expected nabucco type
     * @param result
     *            the verification result
     */
    private void verifyType(NodeToken token, NabuccoModelType nabuccoType, VerificationResult result) {
        String type = token.tokenImage;
        String importString = this.resolveImport(type);

        int beginLine = token.beginLine;
        int endLine = token.endLine;
        int beginColumn = token.beginColumn;
        int endColumn = token.endColumn;

        try {

            MdaModel<NabuccoModel> model = NabuccoDependencyResolver.getInstance().resolveDependency(
                    this.rootDirectory, this.pkg, importString, this.outDirectory);

            if (model.getModel().getNabuccoType() != nabuccoType) {
                result.addError(VerificationErrorCriticality.ERROR, beginLine, endLine, beginColumn, endColumn,
                        "Cannot reference ", type, " as ", nabuccoType.getId(), ". It is of type ", model.getModel()
                                .getNabuccoType().getId(), ".");
            }

        } catch (NabuccoTransformationException e) {
            // Shallow Exception (already marked in ImportVerification)
        }
    }

    /**
     * Resolve the import of the given type.
     * 
     * @param type
     *            the type to resolve
     * 
     * @return the fully qualified type
     */
    private String resolveImport(String type) {
        for (String nabuccoImport : this.imports) {
            if (nabuccoImport.endsWith(type)) {
                String[] importToken = nabuccoImport.split("\\.");
                if (importToken[importToken.length - 1].equals(type)) {
                    return nabuccoImport;
                }
            }
        }

        return this.pkg + PKG_SEPARATOR + type;
    }

}
