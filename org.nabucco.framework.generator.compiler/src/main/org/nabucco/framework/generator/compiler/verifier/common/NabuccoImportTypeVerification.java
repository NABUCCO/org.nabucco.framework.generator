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
package org.nabucco.framework.generator.compiler.verifier.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeFacade;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoCustomDependencies;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;

/**
 * NabuccoImportTypeVerification
 * <p/>
 * Verifies the presence of imports for a given type.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoImportTypeVerification extends NabuccoModelVerificationVisitor implements
        NabuccoTransformationConstants {

    private List<String> imports = new ArrayList<String>();

    private Set<String> usedImports = new HashSet<String>();
    
    private Set<ImportDeclaration> visited = new HashSet<ImportDeclaration>();

    private String pkg;

    private String rootDirectory;

    private String outDirectory;

    /**
     * Creates a new {@link NabuccoImportTypeVerification} instance.
     * 
     * @param rootDirectory
     *            the project root directory
     * @param outDirectory
     *            the target directory
     */
    public NabuccoImportTypeVerification(String rootDirectory, String outDirectory) {
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

        if (!visited.add(nabuccoImport)) {
            return;
        }
        
        String importString = nabuccoImport.nodeToken1.tokenImage;
        int col = nabuccoImport.nodeToken1.beginColumn;
        int line = nabuccoImport.nodeToken1.beginLine;

        this.imports.add(importString);

        // Visit types first!
        nabuccoImport.getParent().getParent().accept(this, result);

        if (!this.usedImports.contains(importString)) {
            result.addError(VerificationErrorCriticality.WARNING, line, col, "The import '"
                    + importString + "' is never used.");
        }
    }

    @Override
    public void visit(ExtensionDeclaration nabucoExtension, VerificationResult result) {
        this.validateImport(result, (NodeToken) nabucoExtension.nodeChoice.choice);
        super.visit(nabucoExtension, result);
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBasetype, VerificationResult result) {
        this.validateImport(result, nabuccoBasetype.nodeToken1);
        super.visit(nabuccoBasetype, result);
    }

    @Override
    public void visit(EnumerationDeclaration nabuccoEnum, VerificationResult result) {
        this.validateImport(result, (NodeToken) nabuccoEnum.nodeChoice1.choice);
        super.visit(nabuccoEnum, result);
    }

    @Override
    public void visit(DatatypeDeclaration nabucoDatatype, VerificationResult result) {
        this.validateImport(result, (NodeToken) nabucoDatatype.nodeChoice1.choice);
        super.visit(nabucoDatatype, result);
    }

    @Override
    public void visit(ComponentDatatypeDeclaration nabucoDatatype, VerificationResult result) {
        this.validateImport(result, (NodeToken) nabucoDatatype.nodeChoice1.choice);
        super.visit(nabucoDatatype, result);
    }

    @Override
    public void visit(ServiceDeclaration nabucoService, VerificationResult result) {
        this.validateImport(result, nabucoService.nodeToken1);
        super.visit(nabucoService, result);
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, VerificationResult result) {
        this.validateImport(result, nabuccoComponent.nodeToken1);
        super.visit(nabuccoComponent, result);
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, VerificationResult result) {
        if (nabuccoMethod.nodeChoice.which == 1) {
            this.validateImport(result, (NodeToken) nabuccoMethod.nodeChoice.choice);
        }
        if (nabuccoMethod.nodeOptional.present()) {
            NodeSequence nodes = (NodeSequence) nabuccoMethod.nodeOptional.node;
            this.validateImport(result, (NodeToken) nodes.elementAt(1));
        }
        super.visit(nabuccoMethod, result);
    }

    @Override
    public void visit(Parameter nabuccoParameter, VerificationResult result) {
        this.validateImport(result, nabuccoParameter.nodeToken);
        super.visit(nabuccoParameter, result);
    }

    /**
     * Validate the type token for import correctness.
     * 
     * @param result
     *            the verification result
     * @param token
     *            the type token
     */
    private void validateImport(VerificationResult result, NodeToken token) {

        String type = token.tokenImage;

        // Ignore Basetypes (NString, NLong, NDate, etc.)
        if (BasetypeFacade.isBasetype(type)) {
            return;
        }

        // Ignore custom declarations (EntityManager, etc.)
        if (NabuccoCustomDependencies.isCustomDeclaration(type)) {
            return;
        }

        int column = token.beginColumn;
        int line = token.beginLine;

        String importString = null;

        for (String nabuccoImport : this.imports) {
            if (nabuccoImport.endsWith(type)) {
                String[] importToken = nabuccoImport.split("\\.");
                if (importToken[importToken.length - 1].equals(type)) {
                    importString = nabuccoImport;

                    this.usedImports.add(nabuccoImport);

                    break;
                }
            }
        }

        if (importString == null) {
            try {
                importString = this.pkg + PKG_SEPARATOR + type;
                NabuccoDependencyResolver.getInstance().resolveDependency(this.rootDirectory,
                        this.pkg, importString, this.outDirectory);
            } catch (NabuccoTransformationException e) {
                result.addError(VerificationErrorCriticality.ERROR, line, column, type
                        + " cannot be resolved to a type.");
            }
        }

    }
}
