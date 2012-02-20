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
package org.nabucco.framework.generator.compiler.verifier;

import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;

/**
 * NabuccoModelVerificationVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoModelVerificationVisitor extends GJVoidDepthFirst<VerificationResult> implements
        NabuccoModelVerification {

    /** The Verification Logger. */
    private MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoModelVerification.class);

    /**
     * Getter for the logger.
     * 
     * @return Returns the logger.
     */
    public MdaLogger getLogger() {
        return this.logger;
    }

    @Override
    public void verify(VerificationResult result) throws NabuccoVerificationException {
        result.getModel().getUnit().accept(this, result);

        if (result.hasFatals()) {
            logger.error("Errors in file '", result.getModel().getName(), "'.");
            throw new NabuccoVerificationException(result);
        }
    }

}
