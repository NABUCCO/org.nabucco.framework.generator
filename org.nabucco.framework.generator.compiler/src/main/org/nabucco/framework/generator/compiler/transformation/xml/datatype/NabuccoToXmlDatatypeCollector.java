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
package org.nabucco.framework.generator.compiler.transformation.xml.datatype;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nabucco.framework.mda.model.xml.XmlDocument;

/**
 * NabuccoToXmlDatatypeCollector
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlDatatypeCollector {

    private Set<String> basetypes = Collections.synchronizedSet(new HashSet<String>());

    private Map<String, XmlDocument> entities = Collections.synchronizedMap(new HashMap<String, XmlDocument>());

    private Map<String, XmlDocument> superclasses = Collections.synchronizedMap(new HashMap<String, XmlDocument>());

    /**
     * Adds a basetype reference to the collector object.
     * 
     * @param basetypeName
     *            the fully qualified basetype name
     */
    public void addBasetype(String basetypeName) {
        this.basetypes.add(basetypeName);
    }

    /**
     * Adds a entity reference to the collector object.
     * 
     * @param entityName
     *            the fully qualified entity name
     * @param document
     *            the XML document to add
     */
    public void addEntity(String entityName, XmlDocument document) {
        this.entities.put(entityName, document);
    }

    /**
     * Adds a mapped-superclass reference to the collector object.
     * 
     * @param superclassName
     *            the fully qualified mapped-superclass name
     * @param document
     *            the XML document to add
     */
    public void addMappedSuperclass(String superclassName, XmlDocument document) {
        this.superclasses.put(superclassName, document);
    }

    /**
     * Checks whether an entity import is already visited.
     * 
     * @param entity
     *            the entity
     * 
     * @return <b>true</b> if the import is already visited, <b>false</b> if not
     */
    public boolean isEntity(String entity) {
        return this.entities.containsKey(entity);
    }

    /**
     * Checks whether a mapped super-class import is already visited.
     * 
     * @param mappedSuperclass
     *            the mapped superclass
     * 
     * @return <b>true</b> if the import is already visited, <b>false</b> if not
     */
    public boolean isMappedSuperclass(String mappedSuperclass) {
        return this.superclasses.containsKey(mappedSuperclass);
    }

    /**
     * Checks wether a ORM type is contained in the collector.
     * 
     * @param name
     *            the name of the ORM type
     * 
     * @return <b>true</b> if the reference exists in the collector object, <b>false</b> otherwise.
     */
    public boolean contains(String name) {
        if (this.entities.containsKey(name)) {
            return true;
        }
        if (this.superclasses.containsKey(name)) {
            return true;
        }
        return false;
    }

    /**
     * Getter for the merged resulting documents.
     * 
     * @return the merged XML documents (entities and mapped-superclasses)
     */
    public Collection<XmlDocument> getDocuments() {
        Map<String, XmlDocument> resultMap = new HashMap<String, XmlDocument>();
        resultMap.putAll(this.superclasses);
        resultMap.putAll(this.entities);

        return resultMap.values();
    }

}
