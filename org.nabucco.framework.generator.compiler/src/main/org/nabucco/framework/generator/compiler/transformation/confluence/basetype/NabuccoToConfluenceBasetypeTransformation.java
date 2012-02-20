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
package org.nabucco.framework.generator.compiler.transformation.confluence.basetype;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceTransformation;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;


/**
 * NabuccoToConfluenceBasetypeTransformation
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceBasetypeTransformation  extends NabuccoToConfluenceTransformation {

    /**
     * Creates a new {@link NabuccoToConfluenceBasetypeTransformation} instance.
     * 
     * @param source
     *            the source nabucco model
     * @param target
     *            the target confluence model
     * @param context
     *            the transformation context
     */
    public NabuccoToConfluenceBasetypeTransformation(MdaModel<NabuccoModel> source, MdaModel<ConfluenceModel> target,
            NabuccoTransformationContext context) {
        super(source, target, context);
    }

    @Override
    public void transformModel(MdaModel<NabuccoModel> source, MdaModel<ConfluenceModel> target,
            NabuccoTransformationContext context) throws NabuccoTransformationException {

        NabuccoModel nabuccoModel = source.getModel();
        NabuccoToConfluenceVisitorContext visitorContext = new NabuccoToConfluenceVisitorContext();
        NabuccoToConfluenceBasetypeVisitor visitor = new NabuccoToConfluenceBasetypeVisitor(visitorContext);
        nabuccoModel.getUnit().accept(visitor, target);
    }

   
}
