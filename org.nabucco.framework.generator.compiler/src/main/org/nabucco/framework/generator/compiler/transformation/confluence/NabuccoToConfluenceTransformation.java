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
package org.nabucco.framework.generator.compiler.transformation.confluence;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformation;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;

/**
 * NabuccoToConfluenceTransformation
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoToConfluenceTransformation extends NabuccoTransformation<ConfluenceModel> {

    /**
     * Creates a new {@link NabuccoToConfluenceTransformation} instance.
     * 
     * @param source
     *            the source nabucco model
     * @param target
     *            the target confluence model
     * @param context
     *            the transformation context
     */
    public NabuccoToConfluenceTransformation(MdaModel<NabuccoModel> source, MdaModel<ConfluenceModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

}
