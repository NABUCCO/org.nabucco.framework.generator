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
package org.nabucco.framework.generator.compiler.transformation.java.common.constraint.util;

import java.util.List;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationGroupType;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeMapping;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;

/**
 * BasetypeConstraintResolver
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class BasetypeConstraintResolver extends GJVoidDepthFirst<List<NabuccoAnnotation>> {

    private boolean large;

    @Override
    public void visit(ExtensionDeclaration extension, List<NabuccoAnnotation> argu) {
        String type = extension.nodeToken.tokenImage;
        this.large = type.equalsIgnoreCase(BasetypeMapping.N_TEXT.getName());
    }

    @Override
    public void visit(BasetypeStatement basetype, List<NabuccoAnnotation> annotations) {
        annotations.addAll(NabuccoAnnotationMapper.getInstance().mapToAnnotationList(basetype.annotationDeclaration,
                NabuccoAnnotationGroupType.CONSTRAINT));
    }

    /**
     * Getter for the large flag.
     * 
     * @return Returns the large.
     */
    public boolean isLarge() {
        return this.large;
    }
}
