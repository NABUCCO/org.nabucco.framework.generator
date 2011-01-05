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
package org.nabucco.framework.generator.compiler.verifier.common;

import java.io.File;

import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;

/**
 * GeneralVerficication
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class NabuccoPackageVerification extends NabuccoModelVerificationVisitor {

    private PackageDeclaration packageDeclaration = null;

    @Override
    public void visit(PackageDeclaration nabuccoPackage, VerificationResult result) {
        this.packageDeclaration = nabuccoPackage;

        String pkg = packageDeclaration.nodeToken1.tokenImage;
        String location = convertToPackage(result.getModel().getPath());

        if (!location.endsWith(pkg)) {
            int row = packageDeclaration.nodeToken1.beginLine;
            int col = packageDeclaration.nodeToken1.beginColumn;

            result.addError(VerificationErrorCriticality.ERROR, row, col,
                    "Declared package missmatches the path.");
        }
    }

    /**
     * Converts the path to a package string.
     * 
     * @param path
     *            the file path
     * 
     * @return the package
     */
    private String convertToPackage(String path) {
        path = this.format(path);
        return path.substring(0, path.lastIndexOf('.'));
    }

    /**
     * Removes the .nbc suffix.
     * 
     * @param path
     *            the path to format
     * 
     * @return the formatted path
     */
    private String format(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path to NABUCCO model is not valid.");
        }
        String lowerCase = path.toLowerCase();
        if (!lowerCase.contains(".nbc")) {
            throw new IllegalArgumentException("Path to NABUCCO model does not point to a valid .nbc file.");
        }
        return path.substring(0, lowerCase.lastIndexOf(".nbc")).replace(File.separatorChar, '.').replace('/', '.');
    }

}
