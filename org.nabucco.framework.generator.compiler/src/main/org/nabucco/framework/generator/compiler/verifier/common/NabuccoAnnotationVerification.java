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

                int beginLine = annotation.beginLine;
                int endLine = annotation.endLine;
                int beginColumn = annotation.beginColumn;
                int endColumn = annotation.endColumn;

                result.addError(VerificationErrorCriticality.ERROR, beginLine, endLine, beginColumn, endColumn,
                        "Cannot resolve Annotation '", annotationName, "'.");
            }
        }
    }
}
