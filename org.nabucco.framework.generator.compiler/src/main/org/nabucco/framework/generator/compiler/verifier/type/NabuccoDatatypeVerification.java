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
package org.nabucco.framework.generator.compiler.verifier.type;

import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;

/**
 * NabuccoDatatypeVerification
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class NabuccoDatatypeVerification extends NabuccoModelVerificationVisitor {

    // TODO: Remove this! Warnig is not be necessary! Self-references are no problem!

    // private String currentDatatypeDeclaration = null;
    //    
    // @Override
    // public void visit(DatatypeStatement nabuccoDatatype, MdaModel<NabuccoModel> target) {
    // currentDatatypeDeclaration = nabuccoDatatype.nodeToken2.tokenImage;
    // super.visit(nabuccoDatatype, target);
    // }
    //
    // @Override
    // public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<NabuccoModel> target) {
    // if (currentDatatypeDeclaration
    // .compareTo(((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage) == 0) {
    // logger.warning("Self reference in: " + currentDatatypeDeclaration);
    // }
    // super.visit(nabuccoDatatype, target);
    // }

}
