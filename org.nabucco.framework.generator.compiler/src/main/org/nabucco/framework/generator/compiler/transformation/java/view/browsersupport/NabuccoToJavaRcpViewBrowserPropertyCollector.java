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
package org.nabucco.framework.generator.compiler.transformation.java.view.browsersupport;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.TraversingNabuccoToJavaVisitor;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * NabuccoToJavaRcpViewBrowserPropertyCollector
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewBrowserPropertyCollector extends
        TraversingNabuccoToJavaVisitor<Map<String, Set<PropertyContainer>>> implements ViewConstants {

    /**
     * Saves the superClass from the extends-statement.
     */
    private String superClass;

    /**
     * Creates a new {@link NabuccoToJavaRcpViewBrowserPropertyCollector} instance.
     * 
     * @param context
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewBrowserPropertyCollector(NabuccoToJavaVisitorContext context) {
        super(context);
    }

    @Override
    public void visit(DatatypeStatement nabuccoStatement, Map<String, Set<PropertyContainer>> propertyNameMap) {
        super.visit(nabuccoStatement, propertyNameMap);
        // superClass must be set null, otherwise it ends in an endless loop
        if (this.superClass != null) {
            String superClass = this.superClass;
            this.superClass = null;
            super.subVisit(superClass, propertyNameMap);
        }

    }

    @Override
    public void visit(ExtensionDeclaration nabuccoExtension, Map<String, Set<PropertyContainer>> propertyNameMap) {
        superClass = ((NodeToken) nabuccoExtension.nodeChoice.choice).tokenImage;
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBaseDeclaration, Map<String, Set<PropertyContainer>> propertyNameMap) {
        // add only the basetypes without @Primary or @OptimisticLock
        NabuccoAnnotation primaryAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoBaseDeclaration.annotationDeclaration, NabuccoAnnotationType.PRIMARY);
        NabuccoAnnotation optimisticLockAnnotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                nabuccoBaseDeclaration.annotationDeclaration, NabuccoAnnotationType.OPTIMISTIC_LOCK);

        if (primaryAnnotation == null && optimisticLockAnnotation == null) {

            String name = nabuccoBaseDeclaration.nodeToken3.tokenImage;
            String multiplicity = nabuccoBaseDeclaration.nodeToken2.tokenImage;
            String type = nabuccoBaseDeclaration.nodeToken1.tokenImage;
            String importString = super.resolveImport(type);
            PropertyContainer property = new PropertyContainer(name, multiplicity, type, importString);
            if (propertyNameMap.containsKey(BASETYPE)) {
                propertyNameMap.get(BASETYPE).add(property);
            } else {
                Set<PropertyContainer> set = new TreeSet<PropertyContainer>();
                set.add(property);
                propertyNameMap.put(BASETYPE, set);
            }
        }

    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatypeDeclaration,
            Map<String, Set<PropertyContainer>> propertyNameMap) {
        String name = nabuccoDatatypeDeclaration.nodeToken2.tokenImage;
        String multiplicity = nabuccoDatatypeDeclaration.nodeToken1.tokenImage;
        String type = ((NodeToken) nabuccoDatatypeDeclaration.nodeChoice1.choice).tokenImage;
        String importString = super.resolveImport(type);
        PropertyContainer property = new PropertyContainer(name, multiplicity, type, importString);
        if (propertyNameMap.containsKey(DATATYPE)) {
            propertyNameMap.get(DATATYPE).add(property);
        } else {
            Set<PropertyContainer> set = new TreeSet<PropertyContainer>();
            set.add(property);
            propertyNameMap.put(DATATYPE, set);
        }
    }

}
