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
package org.nabucco.framework.generator.compiler.transformation.confluence.adapter;

import org.nabucco.framework.generator.compiler.transformation.confluence.component.NabuccoToConfluenceComponentDeclarationsVisitor;
import org.nabucco.framework.generator.compiler.transformation.confluence.visitor.NabuccoToConfluenceVisitorContext;
import org.nabucco.framework.mda.model.text.confluence.ast.ConfluenceComposite;


/**
 * NabuccoToConfluenceAdapterDeclarationsVisitor
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceAdapterDeclarationsVisitor extends NabuccoToConfluenceComponentDeclarationsVisitor {

    /**
     * Creates a new {@link NabuccoToConfluenceAdapterDeclarationsVisitor} instance.
     *
     * @param visitorContext
     * @param element
     */
    public NabuccoToConfluenceAdapterDeclarationsVisitor(NabuccoToConfluenceVisitorContext visitorContext,
            ConfluenceComposite element) {
        super(visitorContext, element);
        // The adapter has the uses the same visitor with component. If no - override Methods in this class
    }

}
