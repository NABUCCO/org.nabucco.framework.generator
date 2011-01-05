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

import org.nabucco.framework.generator.compiler.verifier.common.NabuccoAnnotationVerification;
import org.nabucco.framework.generator.compiler.verifier.common.NabuccoFileVerification;
import org.nabucco.framework.generator.compiler.verifier.common.NabuccoImportTypeVerification;
import org.nabucco.framework.generator.compiler.verifier.common.NabuccoImportVerification;
import org.nabucco.framework.generator.compiler.verifier.common.NabuccoKeywordVerification;
import org.nabucco.framework.generator.compiler.verifier.common.NabuccoPackageVerification;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationError;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.compiler.verifier.type.NabuccoEnumerationVerification;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoVerifier
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoVerifier {

    /** The Logger */
    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(
            NabuccoVerifier.class);

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
     * Verifies an MDA model for correctness.
     * 
     * @param model
     *            the model to validate.
     * 
     * @return the verification result containing warnings
     * 
     * @throws NabuccoVerificationException
     *             if errors or fatals appear that force the compiler to stop
     */
    public VerificationResult verifyNabuccoModel(MdaModel<NabuccoModel> model, String rootDir,
            String outDir) throws NabuccoVerificationException {
        if (model == null) {
            throw new IllegalArgumentException("MDA Model must be defined.");
        }
        return this.verifyNabuccoModel(model.getModel(), rootDir, outDir);
    }

    /**
     * Verifies a NABUCCO model for correctness.
     * 
     * @param model
     *            the model to validate.
     * @param rootDir
     *            the root directory
     * @param outDir
     *            the target directory
     * 
     * @return the verification result containing warnings
     * 
     * @throws NabuccoVerificationException
     *             if errors or fatals appear that force the compiler to stop
     */
    public VerificationResult verifyNabuccoModel(NabuccoModel model, String rootDir, String outDir)
            throws NabuccoVerificationException {
        if (model == null) {
            throw new IllegalArgumentException("Nabucco Model must be defined.");
        }

        VerificationResult result = new VerificationResult(model);

        // Archives must not be validated!
        if (result.getModel().getResourceType() == NabuccoModelResourceType.ARCHIVE) {
            return result;
        }

        this.verifyCommon(result);
        this.verifyImports(rootDir, outDir, result);
        this.verifyType(result);

        if (result.hasErrors() || result.hasFatals()) {
            throw new NabuccoVerificationException(result);
        }
        if (result.hasWarnings()) {
            logger.warning("Warnings in file '", result.getModel().getName(), ".nbc'.");
            for (VerificationError warning : result.getWarnings()) {
                logger.warning(warning.toString());
            }
        }
        return result;
    }

    /**
     * Verifies common nodes (e.g. Filename, Package, etc.).
     * 
     * @param result
     *            the verification result
     * 
     * @throws NabuccoVerificationException
     */
    private void verifyCommon(VerificationResult result) throws NabuccoVerificationException {

        NabuccoFileVerification fileVerification = new NabuccoFileVerification();
        fileVerification.verify(result);

        NabuccoPackageVerification pkgVerification = new NabuccoPackageVerification();
        pkgVerification.verify(result);

        NabuccoKeywordVerification keywordVerification = new NabuccoKeywordVerification();
        keywordVerification.verify(result);
        
        NabuccoAnnotationVerification annotationVerification = new NabuccoAnnotationVerification();
        annotationVerification.verify(result);
    }

    /**
     * Verifies the NABUCCO imports.
     * 
     * @param outDir
     *            the target directory
     * @param rootDir
     *            the root directory
     * @param result
     *            the verification result
     * 
     * @throws NabuccoVerificationException
     */
    private void verifyImports(String rootDir, String outDir, VerificationResult result)
            throws NabuccoVerificationException {
        
        NabuccoModelVerificationVisitor verificationVisitor;

        // Import Target Model
        verificationVisitor = new NabuccoImportVerification(rootDir, outDir);
        verificationVisitor.verify(result);

        // Import Existence
        verificationVisitor = new NabuccoImportTypeVerification(rootDir, outDir);
        verificationVisitor.verify(result);
    }

    /**
     * Verifies individual statements (e.g. Basetype, Enumeration, etc.).
     * 
     * @param result
     *            the verification result
     * 
     * @throws NabuccoVerificationException
     */
    private void verifyType(VerificationResult result) throws NabuccoVerificationException {
        NabuccoModelVerification verification;

        // Individual Verification
        switch (result.getModel().getNabuccoType()) {

        case ENUMERATION:
            verification = new NabuccoEnumerationVerification();
            break;

        default:
            verification = null;

        }

        if (verification != null) {
            verification.verify(result);
        }
    }

}
