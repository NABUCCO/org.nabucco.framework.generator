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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * StructuredPathQuery
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class StructuredPropertyPathQuery {

    private StructuredPropertyPathEntry root;

    private String query;

    /**
     * Create a new query to {@link StructuredPropertyPathEntry}
     * 
     * @param root
     * @param query
     */
    public StructuredPropertyPathQuery(StructuredPropertyPathEntry root, String query) {
        this.root = root;
        this.query = query;
    }

    /**
     * Returns in incremental order the structure entries below the given query element.
     * 
     * @return incremental order the structure entries below the given query element.
     */
    public Set<Entry<String, StructuredPropertyPathEntry>> execute() {
        Map<String, StructuredPropertyPathEntry> result = new LinkedHashMap<String, StructuredPropertyPathEntry>();
        StructuredPropertyPathEntry entry = this.root.getEntry(query);
        extractEntries(entry, this.query, result);
        return result.entrySet();
    }

    private void extractEntries(StructuredPropertyPathEntry sppe, String prefix,
            Map<String, StructuredPropertyPathEntry> result) {
        Iterator<Entry<String, StructuredPropertyPathEntry>> iterator = sppe.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, StructuredPropertyPathEntry> next = iterator.next();
            String identifier = (prefix.isEmpty() ? prefix : prefix
                    + StructuredPropertyPathEntry.SEPARATOR)
                    + next.getKey();
            result.put(identifier, next.getValue());
            extractEntries(next.getValue(), identifier, result);
        }
    }

}
