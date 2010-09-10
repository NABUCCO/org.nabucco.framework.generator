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
package org.nabucco.framework.generator.compiler.transformation.util.dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * NabuccoPath
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoPath {

    /** The list of path entries. */
    private List<NabuccoPathEntry> pathEntries = new ArrayList<NabuccoPathEntry>();

    /**
     * Creates a new {@link NabuccoPath} instance.
     * 
     * @param pathEntries
     *            the path entries.
     */
    public NabuccoPath(List<NabuccoPathEntry> pathEntries) {
        if (pathEntries != null) {
            this.pathEntries.addAll(pathEntries);
        }
    }

    /**
     * Getter for the path entries.
     * 
     * @return the path entries.
     */
    public List<NabuccoPathEntry> getPathEntries() {
        return pathEntries;
    }

    @Override
    public String toString() {
        if (pathEntries != null && !pathEntries.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (NabuccoPathEntry entry : pathEntries) {
                result.append(entry.toString());
                result.append("\n");
            }
            return result.toString();
        }
        return "No NABUCCO path entries defined.";
    }
    
}
