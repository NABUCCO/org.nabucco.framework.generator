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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp.extension;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp.StructuredPropertyPathEntry;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp.StructuredPropertyPathExtension;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp.StructuredPropertyPathQuery;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;


/**
 * 
 * UsedMappedFieldCollector
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class UsedMappedFieldCollector extends StructuredPropertyPathExtension {

    private Set<Class<? extends Node>> visitedTypes;

    private Set<String> mappedFieldsInUse;

    @Override
    public void visit(AnnotationDeclaration n, StructuredPropertyPathEntry argu) {
        NabuccoAnnotationMapper annotationMapper = NabuccoAnnotationMapper.getInstance();
        if (annotationMapper.hasAnnotation(n, NabuccoAnnotationType.MAPPED_FIELD)) {
            getMappedFieldsInUse().add(
                    annotationMapper.mapToAnnotation(n, NabuccoAnnotationType.MAPPED_FIELD)
                            .getValue());
        }
        super.visit(n, argu);
    }

    /**
     * @return Returns the mappedFieldsInUse.
     */
    public Set<String> getMappedFieldsInUse() {
        if (mappedFieldsInUse == null) {
            mappedFieldsInUse = new HashSet<String>();
        }
        return mappedFieldsInUse;
    }

    /**
     * Returns a distinct set of all imports needed to work with the defined mapped fields.
     * 
     * @return distinct list of all imports
     */
    public Set<String> getNeededImports(StructuredPropertyPathEntry root) {
        Set<String> result = new HashSet<String>();
        for (String currentUsedField : this.getMappedFieldsInUse()) {
            for (String currentImport : root.getImports(currentUsedField)) {
                result.add(currentImport);
            }
        }
        return result;
    }

    /**
     * Returns the all Properties in use in an incremental order.
     * 
     * @param root
     *            element from which the search should be started from.
     * @return all elements that a field should be created for.
     */
    public Set<Entry<String, StructuredPropertyPathEntry>> getFieldEntries(
            StructuredPropertyPathEntry root) {
        Set<Entry<String, StructuredPropertyPathEntry>> result = new LinkedHashSet<Entry<String, StructuredPropertyPathEntry>>();
        for (Entry<String, StructuredPropertyPathEntry> element : new StructuredPropertyPathQuery(
                root, "").execute()) {
            for (String keyValue : getMappedFieldsInUse()) {
                if (keyValue.startsWith(element.getKey())) {
                    result.add(element);
                }
            }
        }
        return result;
    }

    /**
     * Returns all Properties that need to be notified in case of an update.
     * 
     * @param root
     *            the root entry
     * @param path
     *            the entry to evaluate
     * @return all properties in model use that need to be notified.
     */
    public Set<Entry<String, StructuredPropertyPathEntry>> getUpdateRelevantSubEntries(
            StructuredPropertyPathEntry root, String path) {
        Set<Entry<String, StructuredPropertyPathEntry>> execute = new StructuredPropertyPathQuery(
                root, path).execute();
        Set<Entry<String, StructuredPropertyPathEntry>> result = new HashSet<Entry<String, StructuredPropertyPathEntry>>();
        for (String usedField : getMappedFieldsInUse()) {
            for (Entry<String, StructuredPropertyPathEntry> current : execute) {
                if (current.getKey().compareTo(usedField) == 0) {
                    result.add(current);
                }
            }
        }
        return result;
    }

    @Override
    public Set<Class<? extends Node>> getVisitedTypes() {
        if (visitedTypes == null) {
            visitedTypes = new HashSet<Class<? extends Node>>();
            visitedTypes.add(AnnotationDeclaration.class);
        }
        return visitedTypes;
    }

}
