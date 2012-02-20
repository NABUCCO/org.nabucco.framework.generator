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
package org.nabucco.framework.generator.compiler.verifier.support;

import java.util.HashSet;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoTraversingValidator
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoTraversingVerifier extends NabuccoModelVerificationVisitor {

    private String rootDir;

    private String outDir;

    private String currentPackage;

    private Set<String> imports;

    private NabuccoAnnotationMapper annotationMapper;

    private NabuccoDependencyResolver dependencyResolver;

    /**
     * Creates a new {@link NabuccoTraversingVerifier} instance.
     * 
     * @param rootDir
     *            the root directory
     * @param outDir
     *            the output directory
     */
    protected NabuccoTraversingVerifier(String rootDir, String outDir) {
        this.rootDir = rootDir;
        this.outDir = outDir;
    }

    @Override
    public void visit(NabuccoUnit n, VerificationResult argu) {
        this.imports = new HashSet<String>();
        super.visit(n, argu);
    }

    @Override
    public void visit(ImportDeclaration n, VerificationResult argu) {
        this.imports.add(n.nodeToken1.tokenImage);
        super.visit(n, argu);
    }

    @Override
    public void visit(PackageDeclaration n, VerificationResult argu) {
        this.currentPackage = n.nodeToken1.tokenImage;
        super.visit(n, argu);
    }

    /**
     * @return Returns the annotationMapper.
     */
    protected NabuccoAnnotationMapper getAnnotationMapper() {
        if (this.annotationMapper == null) {
            this.annotationMapper = NabuccoAnnotationMapper.getInstance();
        }
        return annotationMapper;
    }

    private String findImport(String unqualifiedType) {
        return NabuccoToJavaVisitorSupport.resolveImport(unqualifiedType, currentPackage, imports);
    }

    /**
     * @return Returns the outDir.
     */
    protected String getOutDir() {
        return outDir;
    }

    /**
     * @return Returns the rootDir.
     */
    protected String getRootDir() {
        return rootDir;
    }

    /**
     * Resolve the {@link NabuccoUnit} unit with the given name. The name must be unqualified and
     * will be resolved from the import list.
     * 
     * @param unqualifiedType
     *            the unqualified type name
     * 
     * @return the resolved unit
     */
    protected NabuccoModel resolveModel(String unqualifiedType) {
        if (this.dependencyResolver == null) {
            this.dependencyResolver = NabuccoDependencyResolver.getInstance();
        }
        String foundImport = findImport(unqualifiedType);
        try {
            MdaModel<NabuccoModel> model = this.dependencyResolver.resolveDependency(rootDir, currentPackage,
                    foundImport, outDir);
            return model.getModel();
        } catch (NabuccoTransformationException e) {
            getLogger().warning(e, "Unable to resolve '", unqualifiedType, "'.");

        }
        return null;
    }

}
