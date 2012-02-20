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
package org.nabucco.framework.generator.compiler.transformation.java.view.util;

import java.util.HashSet;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.visitor.GJNoArguDepthFirst;

/**
 * MappedFieldVisitor
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class MappedFieldVisitor extends GJNoArguDepthFirst<AnnotationDeclaration> {

    private Set<String> mappedFields = new HashSet<String>();

    @Override
    public AnnotationDeclaration visit(AnnotationDeclaration n) {
        NabuccoAnnotation mappedFieldAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(n,
                NabuccoAnnotationType.MAPPED_FIELD);
        if (mappedFieldAnnotation != null && mappedFieldAnnotation.getValue() != null) {
            this.mappedFields.add(mappedFieldAnnotation.getValue());
        }
        return super.visit(n);
    }

    /**
     * @return Returns the mappedFields.
     */
    public Set<String> getMappedFields() {
        return mappedFields;
    }

}
