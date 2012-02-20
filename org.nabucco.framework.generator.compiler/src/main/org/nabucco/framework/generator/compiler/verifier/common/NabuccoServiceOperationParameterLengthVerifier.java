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
package org.nabucco.framework.generator.compiler.verifier.common;

import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ParameterList;

/**
 * NabuccoServiceOperationParameterLengthVerifier
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoServiceOperationParameterLengthVerifier extends NabuccoModelVerificationVisitor {

    @Override
    public void visit(ParameterList n, VerificationResult argu) {
        if (n.nodeListOptional.present()) {
            Parameter firstParameter = (Parameter) n.nodeListOptional.nodes.firstElement();
            Parameter lastParameter = (Parameter) n.nodeListOptional.nodes.lastElement();

            int size = n.nodeListOptional.nodes.size();
            if (size < 0 || size > 1) {
                argu.addError(VerificationErrorCriticality.ERROR,
                        firstParameter.nodeToken.beginLine, lastParameter.nodeToken1.endLine,
                        firstParameter.nodeToken.beginColumn, lastParameter.nodeToken1.endColumn,
                        "service operations may only have zero to one Parameter");
            }
        }
        super.visit(n, argu);
    }

}
