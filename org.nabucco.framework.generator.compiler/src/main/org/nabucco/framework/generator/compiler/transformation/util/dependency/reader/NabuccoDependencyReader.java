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
package org.nabucco.framework.generator.compiler.transformation.util.dependency.reader;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationConstants;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoDependencyReader
 * <p/>
 * Interface for reading {@link NabuccoModel} dependencies.
 * 
 * @see NabuccoArchiveReader
 * @see NabuccoProjectReader
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface NabuccoDependencyReader extends NabuccoTransformationConstants {

    /**
     * Reads from a NABUCCO model from the given resource.
     * 
     * @param importString
     *            the import string
     * 
     * @return the read NABUCCO model
     * 
     * @throws NabuccoTransformationException
     *             if the model cannot be found
     */
    MdaModel<NabuccoModel> read(String importString) throws NabuccoTransformationException;

}
