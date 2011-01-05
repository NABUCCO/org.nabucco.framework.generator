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

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * NabuccoAnnotationVerification
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoAnnotationVerification extends NabuccoModelVerificationVisitor {

    @Override
    public void visit(AnnotationDeclaration nabuccoAnnotations, VerificationResult result) {

        for (Node node : nabuccoAnnotations.nodeListOptional.nodes) {
            if (!(node instanceof NodeToken)) {
                result.addError(VerificationErrorCriticality.ERROR, "Cannot resolve Annotation.");
                continue;
            }

            NodeToken annotation = (NodeToken) node;
            String annotationName = annotation.tokenImage;

            if (!NabuccoAnnotationMapper.getInstance().isAnnotation(annotationName)) {
                result.addError(VerificationErrorCriticality.ERROR, annotation.beginLine,
                        annotation.beginColumn, "Cannot resolve Annotation '", annotationName, "'.");
            }
        }
    }
}
