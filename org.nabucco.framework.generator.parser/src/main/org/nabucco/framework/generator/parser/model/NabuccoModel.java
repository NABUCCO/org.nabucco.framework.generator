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
package org.nabucco.framework.generator.parser.model;

import java.io.Serializable;
import java.io.StringWriter;

import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.visitor.TreeDumper;
import org.nabucco.framework.generator.parser.visitor.TreeFormatter;
import org.nabucco.framework.mda.model.MdaModelType;
import org.nabucco.framework.mda.model.ModelImplementation;

/**
 * NabuccoModel
 * <p/>
 * The model of a NABUCCO (.nbc) file contains a single compilation unit. This model should be
 * created using the {@link NabuccoModelLoader}.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoModel extends ModelImplementation implements Serializable {

    private static final long serialVersionUID = 1L;

    private NabuccoUnit unit;

    private String path;

    private NabuccoModelType type;

    private transient NabuccoModelResourceType resourceType;

    /**
     * Creates a new {@link NabuccoModel} instance.
     * 
     * @param unit
     *            the NABUCCO compilation unit.
     * @param path
     *            path to the original file.
     * @param modelType
     *            the NABUCCO type
     * @param resourceType
     *            type of the ressource
     */
    public NabuccoModel(NabuccoUnit unit, String path, NabuccoModelType modelType,
            NabuccoModelResourceType resourceType) {
        super(MdaModelType.NABUCCO);

        if (unit == null) {
            throw new IllegalArgumentException("Cannot create model for NabuccoUnit [null].");
        }
        if (modelType == null) {
            throw new IllegalArgumentException("Cannot create model for NabuccoModelType [null].");
        }
        if (resourceType == null) {
            throw new IllegalArgumentException(
                    "Cannot create model for NabuccoResourceType [null].");
        }

        this.unit = unit;
        this.path = path;
        this.type = modelType;
        this.resourceType = resourceType;
    }

    /**
     * Getter for the NABUCCO compilation unit.
     * 
     * @return the compilation unit
     */
    public NabuccoUnit getUnit() {
        return this.unit;
    }

    /**
     * Getter for the name of the NabuccoModel.
     * 
     * @return the statement name
     */
    public String getName() {
        StringBuilder name = new StringBuilder();
        NabuccoModelNameVisitor nameVisitor = new NabuccoModelNameVisitor();
        this.unit.accept(nameVisitor, name);
        return name.toString();
    }

    /**
     * Getter for the simple name of the NabuccoModel as given in the source code.
     * 
     * @return the simple name of the model
     */
    public String getSimpleName() {
        return this.getName();
    }

    /**
     * Getter for the canonical name of the NabuccoModel
     * 
     * @return
     */
    public String getCanonicalName() {
        StringBuilder name = new StringBuilder();
        name.append(this.getPackage());
        name.append('.');
        name.append(this.getName());
        return name.toString();
    }

    /**
     * Getter for the package name of this NABUCCO model.
     * 
     * @return the package representation as string
     */
    public String getPackage() {
        if (this.unit.packageDeclaration == null || this.unit.packageDeclaration.nodeToken1 == null) {
            return "";
        }
        return this.unit.packageDeclaration.nodeToken1.tokenImage;
    }

    /**
     * Getter for the path.
     * 
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Getter for the NABUCCO type.
     * 
     * @return the NABUCCO type
     */
    public NabuccoModelType getNabuccoType() {
        return this.type;
    }

    /**
     * Getter for the ressource type (whether the model was loaded from Archive or Project).
     * 
     * @return the ressource type of the model.
     */
    public NabuccoModelResourceType getResourceType() {
        return this.resourceType;
    }

    /**
     * Setter for the ressource type.
     * 
     * @param resourceType
     *            the ressource type to set
     */
    public void setResourceType(NabuccoModelResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        NabuccoModel other = (NabuccoModel) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (resourceType == null) {
            if (other.resourceType != null)
                return false;
        } else if (!resourceType.equals(other.resourceType))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (unit == null) {
            if (other.unit != null)
                return false;
        } else if (!unit.equals(other.unit))
            return false;
        return true;
    }

    /**
     * Print the NABUCCO model.
     * 
     * @return the model as string
     */
    public String printModel() {
        StringWriter writer = new StringWriter();
        TreeFormatter formatter = new TreeFormatter(3, 40);
        TreeDumper dumper = new TreeDumper(writer);

        this.getUnit().accept(formatter);
        this.getUnit().accept(dumper);

        dumper.flushWriter();
        return writer.getBuffer().toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(super.toString());
        result.append(" (");
        result.append(this.getName());
        result.append(")\n\n");
        result.append(this.printModel());
        return result.toString();
    }

}
