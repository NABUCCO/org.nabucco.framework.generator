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

import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoDependencyResolver
 * <p/>
 * Resolves dependencies of a NABUCCO model.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoDependencyResolver {

    /**
     * Singleton instance.
     */
    private static NabuccoDependencyResolver instance = new NabuccoDependencyResolver();

    /**
     * Private constructor.
     */
    private NabuccoDependencyResolver() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoDependencyResolver instance.
     */
    public static NabuccoDependencyResolver getInstance() {
        return instance;
    }

    /**
     * Loads necessary NABUCCO dependencies. Already resolved dependencies are not resolved twice.
     * 
     * @param model
     *            the current model
     * @param rootDirectory
     *            the NABUCCO root directory
     * @param outDirectory
     *            the out directory
     * 
     * @return the dependend models
     */
    public List<MdaModel<NabuccoModel>> resolveDependencies(MdaModel<NabuccoModel> model,
            String rootDirectory, String outDirectory) {
        return this.resolveDependencies(model, rootDirectory, outDirectory, false);
    }

    /**
     * Loads necessary NABUCCO dependencies.
     * 
     * @param model
     *            the current model
     * @param rootDirectory
     *            the root directory
     * @param outDirectory
     *            the out directory
     * @param forceResolve
     *            if <b>false</b> already resolved dependencies are not resolved twice, if
     *            <b>true</b> dependencies are resolved absolutely.
     * 
     * @return the dependend models
     */
    public List<MdaModel<NabuccoModel>> resolveDependencies(MdaModel<NabuccoModel> model,
            String rootDirectory, String outDirectory, boolean forceResolve) {

        List<MdaModel<NabuccoModel>> referenceList = new ArrayList<MdaModel<NabuccoModel>>();

        NabuccoDependencyVisitor visitor = new NabuccoDependencyVisitor(rootDirectory,
                outDirectory, forceResolve);

        model.getModel().getUnit().accept(visitor, referenceList);

        return referenceList;
    }

    /**
     * Loads the NABUCCO model for a single import reference.
     * 
     * @param rootDirectory
     *            the NABUCCO root directory
     * @param importString
     *            the import to resolve
     * @param outDirectory
     *            the NABUCCO out directory
     * 
     * @return the loaded NABUCCO model
     * 
     * @throws NabuccoTransformationException
     */
    public MdaModel<NabuccoModel> resolveDependency(String rootDirectory, String pkg,
            String importString, String outDirectory) throws NabuccoTransformationException {
        return NabuccoDependencySupport.loadModel(rootDirectory, pkg, importString, outDirectory);
    }

    /**
     * Loads the NABUCCO model for a single import reference.
     * 
     * @param context
     *            the visitor context holding the directory informations
     * @param importString
     *            the import to resolve
     * 
     * @return the loaded NABUCCO model
     * 
     * @throws NabuccoTransformationException
     */
    public MdaModel<NabuccoModel> resolveDependency(NabuccoVisitorContext context, String pkg,
            String importString) throws NabuccoTransformationException {
        return NabuccoDependencySupport.loadModel(context.getRootDir(), pkg, importString, context
                .getOutDir());
    }

    /**
     * Clears the dependency cache.
     */
    public void clearCache() {
        NabuccoDependencySupport.clearCache();
    }

}
