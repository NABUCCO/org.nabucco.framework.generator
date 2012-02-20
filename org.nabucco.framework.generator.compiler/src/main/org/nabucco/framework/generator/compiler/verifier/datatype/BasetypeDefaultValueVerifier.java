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
package org.nabucco.framework.generator.compiler.verifier.datatype;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeFacade;
import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeMapping;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.compiler.verifier.support.NabuccoTraversingVerifier;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * BasetypeDefaultValueVerifier
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class BasetypeDefaultValueVerifier extends NabuccoTraversingVerifier {

    private BasetypeDeclaration declaration;

    private String value;

    private NodeToken firstAnnotationToken;

    public BasetypeDefaultValueVerifier(String rootDir, String outDir, BasetypeDeclaration declaration) {
        super(rootDir, outDir);
        this.declaration = declaration;
    }

    @Override
    public void visit(ExtensionDeclaration n, VerificationResult argu) {
        NodeToken choice = (NodeToken) n.nodeChoice.choice;
        if (BasetypeFacade.isBasetype(choice.tokenImage)) {
            BasetypeMapping mapping = (BasetypeMapping) BasetypeMapping.getByName(choice.tokenImage);
            check(mapping, argu);
        } else {
            NabuccoUnit resolveUnit = super.resolveModel(choice.tokenImage).getUnit();
            BasetypeDefaultValueVerifier subVisit = new BasetypeDefaultValueVerifier(getRootDir(), getOutDir(),
                    declaration);
            resolveUnit.accept(subVisit, argu);
        }
        super.visit(n, argu);
    }

    private void check(BasetypeMapping mapping, VerificationResult argu) {
        switch (mapping) {
        case N_BOOLEAN: {
            Boolean.parseBoolean(getValue());
            break;
        }
        case N_DOUBLE: {
            try {
                Double.parseDouble(getValue());
            } catch (NumberFormatException nfe) {
                argu.addError(VerificationErrorCriticality.ERROR, firstAnnotationToken.beginLine,
                        firstAnnotationToken.endLine, firstAnnotationToken.beginColumn, firstAnnotationToken.endColumn,
                        "the value \"" + getValue() + "\" cannot be parsed into Double");
            }
            break;
        }
        case N_FLOAT: {
            try {
                Float.parseFloat(getValue());
            } catch (NumberFormatException nfe) {
                argu.addError(VerificationErrorCriticality.ERROR, firstAnnotationToken.beginLine,
                        firstAnnotationToken.endLine, firstAnnotationToken.beginColumn, firstAnnotationToken.endColumn,
                        "the value \"" + getValue() + "\" cannot be parsed into Float");
            }
            break;

        }
        case N_LONG: {
            try {
                Long.parseLong(getValue());
            } catch (NumberFormatException nfe) {
                argu.addError(VerificationErrorCriticality.ERROR, firstAnnotationToken.beginLine,
                        firstAnnotationToken.endLine, firstAnnotationToken.beginColumn, firstAnnotationToken.endColumn,
                        "the value \"" + getValue() + "\" cannot be parsed into Long");
            }
            break;
        }
        case N_BYTE: {
            try {
                Byte.parseByte(getValue());
            } catch (NumberFormatException nfe) {
                argu.addError(VerificationErrorCriticality.ERROR, firstAnnotationToken.beginLine,
                        firstAnnotationToken.endLine, firstAnnotationToken.beginColumn, firstAnnotationToken.endColumn,
                        "the value \"" + getValue() + "\" cannot be parsed into Byte");
            }
            break;
        }
        }

    }

    private String getValue() {
        if (value == null) {
            initFields();
        }
        return value;
    }

    private void initFields() {
        value = getAnnotationMapper().mapToAnnotation(this.declaration.annotationDeclaration,
                NabuccoAnnotationType.DEFAULT).getValue();
        firstAnnotationToken = (NodeToken) declaration.annotationDeclaration.nodeListOptional.nodes.firstElement();

    }
}
