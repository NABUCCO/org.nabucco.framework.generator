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
package org.nabucco.framework.generator.compiler.transformation.java.visitor;

import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitor;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;

/**
 * NabuccoToJavaModelVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoToJavaModelVisitor extends
        NabuccoVisitor<MdaModel<JavaModel>, NabuccoToJavaVisitorContext> {

    /**
     * Creates a new {@link NabuccoToJavaModelVisitor} with an appropriate visitor context.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaModelVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }
    
    @Override
    public String toString() {
        return "NABUCCO -> Java";
    }
    
}
