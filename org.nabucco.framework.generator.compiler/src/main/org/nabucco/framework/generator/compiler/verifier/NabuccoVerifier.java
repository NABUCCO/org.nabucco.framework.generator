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

import org.nabucco.framework.generator.compiler.NabuccoCompilerException;
import org.nabucco.framework.generator.compiler.verifier.type.NabuccoDatatypeVerification;
import org.nabucco.framework.generator.compiler.verifier.type.NabuccoEnumerationVerification;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoVerifier
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoVerifier {

    /**
     * Singleton instance.
     */
    private static NabuccoVerifier instance = new NabuccoVerifier();

    /**
     * Private constructor.
     */
    private NabuccoVerifier() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoVerifier instance.
     */
    public static NabuccoVerifier getInstance() {
        return instance;
    }

    /**
     * Verifies a NABUCCO model for correctness.
     * 
     * @param model
     *            the model to validate.
     * 
     * @throws NabuccoCompilerException
     */
    public void verifyNabuccoModel(MdaModel<NabuccoModel> model)
            throws NabuccoVerificationException {
        if (model == null) {
            throw new NabuccoVerificationException("Nabucco Model must be defined.");
        }
        if (model.getModel() == null) {
            throw new NabuccoVerificationException("Nabucco Model must be defined.");
        }

        // Per type verification
        switch (model.getModel().getNabuccoType()) {
        case ENUMERATION: {
            NabuccoEnumerationVerification enumerationVerification = new NabuccoEnumerationVerification();
            model.getModel().getUnit().accept(enumerationVerification, model);
            break;
        }
        case DATATYPE: {
            NabuccoDatatypeVerification datatypeVerification = new NabuccoDatatypeVerification();
            model.getModel().getUnit().accept(datatypeVerification, model);
            break;
        }
        }

        // general verification
        GeneralVerification generalVerification = new GeneralVerification();
        model.getModel().getUnit().accept(generalVerification, model);
    }

}
