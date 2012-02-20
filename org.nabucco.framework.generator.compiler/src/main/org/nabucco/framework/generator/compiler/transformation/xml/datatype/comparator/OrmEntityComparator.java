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
package org.nabucco.framework.generator.compiler.transformation.xml.datatype.comparator;

import java.util.Comparator;

import org.w3c.dom.Node;

/**
 * OrmAttributeComparator
 * <p/>
 * Compares XML Nodes of entity and sorts them to the XSD order.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class OrmEntityComparator implements Comparator<Node> {

    private static final String TABLE = "table";

    private static final String INHERITANCE = "inheritance";

    private static final String DISCRIMINATOR_COLUMN = "discriminator-column";
    
    private static final String DISCRIMINATOR_VALUE = "discriminator-value";

    private static final String ATTRIBUTES = "attributes";

    /**
     * Singleton instance.
     */
    private static OrmEntityComparator instance = new OrmEntityComparator();

    /**
     * Private constructor.
     */
    private OrmEntityComparator() {
    }

    /**
     * Singleton access.
     * 
     * @return the OrmAttributeComparator instance.
     */
    public static OrmEntityComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(Node n1, Node n2) {

        if (n1 == null || n2 == null) {
            throw new IllegalArgumentException("Cannot compare Nodes of type [null].");
        }

        String name1 = n1.getNodeName();
        String name2 = n2.getNodeName();

        if (name1 == null || name2 == null) {
            throw new IllegalArgumentException("Cannot compare Nodes with name [null].");
        }

        return this.getOrder(name1).compareTo(this.getOrder(name2));
    }

    /**
     * Mapps the attribute node name to appropriate order (see orm.xsd for order).
     * 
     * @param name
     *            name of the attribute node
     * 
     * @return the order
     */
    private Integer getOrder(String name) {

        if (name.equals(TABLE)) {
            return 1;
        } else if (name.equals(INHERITANCE)) {
            return 2;
        } else if (name.equals(DISCRIMINATOR_COLUMN)) {
            return 3;
        } else if (name.equals(DISCRIMINATOR_VALUE)) {
            return 3;
        } else if (name.equals(ATTRIBUTES)) {
            return 4;
        }

        return 10;
    }
}
