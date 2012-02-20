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
package org.nabucco.framework.generator.compiler;

import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoCompilationTarget
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoCompilationTarget {

    private String rootDirectory;

    private String component;

    private List<MdaModel<NabuccoModel>> modelList = new ArrayList<MdaModel<NabuccoModel>>();

    /**
     * Creates a {@link NabuccoCompilationTarget} instance containing necessary compilation
     * properties.
     * 
     * @param modelList
     *            the NABUCCO model to compile
     * @param rootDirectory
     *            the project root directory
     * @param component
     *            name of the appropriate NABUCCO component
     */
    public NabuccoCompilationTarget(MdaModel<NabuccoModel> model, String rootDirectory, String component) {
        if (model == null) {
            throw new IllegalArgumentException("Source model must be defined.");
        }
        if (rootDirectory == null) {
            throw new IllegalArgumentException("Root directory must be defined.");
        }
        if (component == null) {
            throw new IllegalArgumentException("NABUCCO component must be defined.");
        }

        this.modelList.add(model);
        this.rootDirectory = rootDirectory;
        this.component = component;
    }

    /**
     * Creates a {@link NabuccoCompilationTarget} instance containing necessary compilation
     * properties.
     * 
     * @param modelList
     *            the list of NABUCCO models to compile
     * @param rootDirectory
     *            the project root directory
     * @param component
     *            name of the appropriate NABUCCO component
     */
    public NabuccoCompilationTarget(List<MdaModel<NabuccoModel>> modelList, String rootDirectory, String component) {
        if (modelList == null) {
            throw new IllegalArgumentException("Source modelList must be defined.");
        }
        if (rootDirectory == null) {
            throw new IllegalArgumentException("Root directory must be defined.");
        }
        if (component == null) {
            throw new IllegalArgumentException("NABUCCO component must be defined.");
        }

        this.modelList.addAll(modelList);
        this.rootDirectory = rootDirectory;
        this.component = component;
    }

    /**
     * Getter for the compilation modelList.
     * 
     * @return Returns the modelList.
     */
    public List<MdaModel<NabuccoModel>> getModelList() {
        return this.modelList;
    }

    /**
     * Getter for the project root directory.
     * 
     * @return Returns the rootDirectory.
     */
    public String getRootDirectory() {
        return this.rootDirectory;
    }

    /**
     * Getter for the component name.
     * 
     * @return Returns the component.
     */
    public String getComponent() {
        return this.component;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Target [root=");
        builder.append(this.rootDirectory);
        builder.append(", component=");
        builder.append(this.component);
        builder.append(", models=");
        builder.append(this.modelList);
        builder.append("]");
        return builder.toString();
    }

}
