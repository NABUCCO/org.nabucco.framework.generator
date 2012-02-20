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

import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerification;
import org.nabucco.framework.generator.compiler.verifier.NabuccoVerificationException;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;

/**
 * NabuccoKeywordVerification
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoKeywordVerification implements NabuccoModelVerification {

    @Override
    public void verify(VerificationResult result) throws NabuccoVerificationException {

        String path = result.getModel().getPath();

        if (path.contains(".facade.")) {
            result.addError(VerificationErrorCriticality.WARNING,
                    "NABUCCO keyword 'facade' should not be used in component path.");
        }
        if (path.contains(".impl.")) {
            result.addError(VerificationErrorCriticality.WARNING,
                    "NABUCCO keyword 'impl' should not be used in component path.");
        }
        if (path.contains(".ui.")) {
            result.addError(VerificationErrorCriticality.WARNING,
                    "NABUCCO keyword 'ui' should not be used in component path.");
        }
    }

}
