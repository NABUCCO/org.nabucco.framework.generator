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
package org.nabucco.framework.generator.compiler.transformation.util.dependency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeFacade;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
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
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoDependencyVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoDependencyVisitor extends GJVoidDepthFirst<List<MdaModel<NabuccoModel>>> implements
        NabuccoTransformationConstants {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoDependencyVisitor.class);

    private String rootDir;

    private String outDir;

    private String pkg;

    private List<String> imports;

    private Set<String> usedImports;

    /**
     * Creates a new {@link NabuccoDependencyVisitor} instance.
     * 
     * @param rootDir
     *            the root NABUCCO directory.
     * @param outDir
     *            the nbcc target directory
     */
    public NabuccoDependencyVisitor(String rootDir, String outDirectory) {
        this.rootDir = rootDir;
        this.outDir = outDirectory;

        this.imports = new ArrayList<String>();
        this.usedImports = new HashSet<String>();
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, List<MdaModel<NabuccoModel>> modelList) {
        this.pkg = nabuccoPackage.nodeToken1.tokenImage;
        super.visit(nabuccoPackage, modelList);
    }

    @Override
    public void visit(ImportDeclaration nabuccoImport, List<MdaModel<NabuccoModel>> modelList) {
        this.imports.add(nabuccoImport.nodeToken1.tokenImage);
        super.visit(nabuccoImport, modelList);
    }

    @Override
    public void visit(ExtensionDeclaration nabucoExtension, List<MdaModel<NabuccoModel>> modelList) {
        String name = ((NodeToken) nabucoExtension.nodeChoice.choice).tokenImage;
        this.resolveTypeDependency(modelList, name);
        super.visit(nabucoExtension, modelList);
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBasetype, List<MdaModel<NabuccoModel>> modelList) {
        this.resolveTypeDependency(modelList, nabuccoBasetype.nodeToken1.tokenImage);
        super.visit(nabuccoBasetype, modelList);
    }

    @Override
    public void visit(EnumerationDeclaration nabuccoEnum, List<MdaModel<NabuccoModel>> modelList) {
        String name = ((NodeToken) nabuccoEnum.nodeChoice1.choice).tokenImage;
        this.resolveTypeDependency(modelList, name);
        super.visit(nabuccoEnum, modelList);
    }

    @Override
    public void visit(DatatypeDeclaration nabucoDatatype, List<MdaModel<NabuccoModel>> modelList) {
        String name = ((NodeToken) nabucoDatatype.nodeChoice1.choice).tokenImage;
        this.resolveTypeDependency(modelList, name);
        super.visit(nabucoDatatype, modelList);
    }

    @Override
    public void visit(ComponentDatatypeDeclaration nabucoDatatype, List<MdaModel<NabuccoModel>> modelList) {
        String name = ((NodeToken) nabucoDatatype.nodeChoice1.choice).tokenImage;
        this.resolveTypeDependency(modelList, name);
        super.visit(nabucoDatatype, modelList);
    }

    @Override
    public void visit(ServiceDeclaration nabucoService, List<MdaModel<NabuccoModel>> modelList) {
        this.resolveTypeDependency(modelList, nabucoService.nodeToken1.tokenImage);
        super.visit(nabucoService, modelList);
    }

    @Override
    public void visit(ComponentDeclaration nabuccoComponent, List<MdaModel<NabuccoModel>> modelList) {
        this.resolveTypeDependency(modelList, nabuccoComponent.nodeToken1.tokenImage);
        super.visit(nabuccoComponent, modelList);
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, List<MdaModel<NabuccoModel>> modelList) {
        if (nabuccoMethod.nodeChoice.which == 1) {
            String type = ((NodeToken) nabuccoMethod.nodeChoice.choice).tokenImage;
            this.resolveTypeDependency(modelList, type);
        }
        if (nabuccoMethod.nodeOptional.present()) {
            NodeSequence nodes = (NodeSequence) nabuccoMethod.nodeOptional.node;
            String type = ((NodeToken) nodes.elementAt(1)).tokenImage;
            this.resolveTypeDependency(modelList, type);
        }
        super.visit(nabuccoMethod, modelList);
    }

    @Override
    public void visit(Parameter nabuccoParameter, List<MdaModel<NabuccoModel>> modelList) {
        this.resolveTypeDependency(modelList, nabuccoParameter.nodeToken.tokenImage);
        super.visit(nabuccoParameter, modelList);
    }

    /**
     * Resolve the dependency for a given type.
     * 
     * @param modelList
     *            the list of models to add the dependency.
     * @param type
     *            the type to resolve
     */
    private void resolveTypeDependency(List<MdaModel<NabuccoModel>> modelList, String type) {

        // Do not resolve Basetypes (NString, NLong, NDate, etc.)
        if (BasetypeFacade.isBasetype(type)) {
            return;
        }

        // Do not resolve custom declarations (EntityManager, etc.)
        if (NabuccoCustomDependencies.isCustomDeclaration(type)) {
            return;
        }

        String importString = this.resolveImport(type);

        if (importString == null) {
            logger.warning("Cannot resolve import '" + importString + "'.");
            throw new NabuccoVisitorException("Cannot resolve import '" + importString + "'.");
        }

        try {
            MdaModel<NabuccoModel> model = NabuccoDependencySupport.loadModel(this.rootDir, this.pkg, importString,
                    this.outDir);
            NabuccoDependencyContainer.getInstance().putModel(importString, model);
            modelList.add(model);
        } catch (NabuccoTransformationException e) {
            logger.debug("Cannot resolve import '" + importString + "'.");
            throw new NabuccoVisitorException("Cannot resolve import '" + importString + "'.", e);
        }
    }

    /**
     * Resolves the import of type depending on the previously collected usedImports.
     * 
     * @param type
     *            the type to resolve
     * 
     * @return the resolved import
     */
    private String resolveImport(String type) {
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
            importString = this.pkg + PKG_SEPARATOR + type;
        }
        return importString;
    }
}
