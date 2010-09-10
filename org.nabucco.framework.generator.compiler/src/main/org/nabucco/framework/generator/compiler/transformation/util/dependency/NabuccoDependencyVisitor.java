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
package org.nabucco.framework.generator.compiler.transformation.util.dependency;

import java.util.List;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.basetype.NabuccoToJavaBasetypeReferences;
import org.nabucco.framework.generator.compiler.transformation.java.service.NabuccoToJavaServiceReferences;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoDependencyVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoDependencyVisitor extends GJVoidDepthFirst<List<MdaModel<NabuccoModel>>> {

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoDependencyVisitor.class);

    private String rootDir;

    private String outDir;

    private String pkg;

    private boolean forceResolve;

    /**
     * Creates a new {@link NabuccoDependencyVisitor} instance.
     * 
     * @param rootDir
     *            the root NABUCCO directory.
     * @param outDir
     * @param forceResolve
     *            if <b>false</b> already resolved dependencies are not resolved twice, if
     *            <b>true</b> dependencies are resolved absolutely.
     */
    public NabuccoDependencyVisitor(String rootDir, String outDirectory, boolean forceResolve) {
        this.rootDir = rootDir;
        this.outDir = outDirectory;
        this.forceResolve = forceResolve;
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, List<MdaModel<NabuccoModel>> modelList) {
        this.pkg = nabuccoPackage.nodeToken1.tokenImage;
        super.visit(nabuccoPackage, modelList);
    }

    @Override
    public void visit(ImportDeclaration nabuccoImport, List<MdaModel<NabuccoModel>> modelList) {
        String importString = nabuccoImport.nodeToken1.tokenImage;
        this.resolveImportDependency(modelList, importString);
        super.visit(nabuccoImport, modelList);
    }

    @Override
    public void visit(ExtensionDeclaration nabucoExtension, List<MdaModel<NabuccoModel>> modelList) {
        this.resolveTypeDependency(modelList,
                ((NodeToken) nabucoExtension.nodeChoice.choice).tokenImage);
        super.visit(nabucoExtension, modelList);
    }

    @Override
    public void visit(DatatypeDeclaration nabucoDatatype, List<MdaModel<NabuccoModel>> modelList) {
        this.resolveTypeDependency(modelList,
                ((NodeToken) nabucoDatatype.nodeChoice1.choice).tokenImage);
        super.visit(nabucoDatatype, modelList);
    }

    private void resolveImportDependency(List<MdaModel<NabuccoModel>> modelList, String importString) {

        // Do not resolve already resolved dependencies except for force option
        if (NabuccoDependencyContainer.getInstance().containsImport(importString)
                && !this.forceResolve) {
            return;
        }

        try {
            MdaModel<NabuccoModel> model = NabuccoDependencySupport.loadModel(this.rootDir,
                    this.pkg, importString, this.outDir);
            NabuccoDependencyContainer.getInstance().putModel(importString, model);
            modelList.add(model);
        } catch (NabuccoTransformationException e) {
            logger.debug("Cannot resolve import '" + importString + "'.");
            throw new NabuccoVisitorException("Cannot resolve import '" + importString + "'.", e);
        }
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

        // Do not resolve already resolved dependencies except for force option
        if (NabuccoDependencyContainer.getInstance().containsType(type) && !this.forceResolve) {
            return;
        }

        // Do not resolve Basetypes (NString, NLong, NInteger, NDate, etc.)
        if (NabuccoToJavaBasetypeReferences.isBasetypeReference(type)) {
            return;
        }

        // Do not resolve Service Utilities (EntityManager, etc.)
        if (NabuccoToJavaServiceReferences.isServiceReference(type)) {
            return;
        }

        if (this.pkg != null) {
            String importString = this.pkg + "." + type;

            try {
                MdaModel<NabuccoModel> model = NabuccoDependencySupport.loadModel(this.rootDir,
                        this.pkg, importString, this.outDir);
                NabuccoDependencyContainer.getInstance().putModel(importString, model);
                modelList.add(model);
            } catch (NabuccoTransformationException e) {
                logger.debug("Cannot resolve import '" + importString + "'.");
                throw new NabuccoVisitorException("Cannot resolve import '" + importString + "'.", e);
            }
        }
    }
}
