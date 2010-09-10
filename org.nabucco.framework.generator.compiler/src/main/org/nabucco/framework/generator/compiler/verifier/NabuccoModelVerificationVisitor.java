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
package org.nabucco.framework.generator.compiler.verifier;

import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitor;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorContext;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoModelVerificationVisitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public abstract class NabuccoModelVerificationVisitor extends
        NabuccoVisitor<MdaModel<NabuccoModel>, NabuccoVisitorContext> {

    protected MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoModelVerificationVisitor.class);

    public NabuccoModelVerificationVisitor() {
        super(new NabuccoVisitorContext());
    }

}
