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
 * Compares XML Nodes of attributes and sorts them to the XSD order.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class OrmAttributeComparator implements Comparator<Node> {

    private static final String ID = "id";

    private static final String EMBEDDED_ID = "embedded_id";

    private static final String BASIC = "basic";

    private static final String VERSION = "version";

    private static final String MANY_TO_ONE = "many-to-one";

    private static final String ONE_TO_MANY = "one-to-many";

    private static final String ONE_TO_ONE = "one-to-one";

    private static final String MANY_TO_MANY = "many-to-many";

    private static final String EMBEDDED = "embedded";

    private static final String TRANSIENT = "transient";

    /**
     * Singleton instance.
     */
    private static OrmAttributeComparator instance = new OrmAttributeComparator();

    /**
     * Private constructor.
     */
    private OrmAttributeComparator() {
    }

    /**
     * Singleton access.
     * 
     * @return the OrmAttributeComparator instance.
     */
    public static OrmAttributeComparator getInstance() {
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

        if (name.equals(ID)) {
            return 1;
        } else if (name.equals(EMBEDDED_ID)) {
            return 1;
        } else if (name.equals(BASIC)) {
            return 2;
        } else if (name.equals(VERSION)) {
            return 3;
        } else if (name.equals(MANY_TO_ONE)) {
            return 4;
        } else if (name.equals(ONE_TO_MANY)) {
            return 5;
        } else if (name.equals(ONE_TO_ONE)) {
            return 6;
        } else if (name.equals(MANY_TO_MANY)) {
            return 7;
        } else if (name.equals(EMBEDDED)) {
            return 8;
        } else if (name.equals(TRANSIENT)) {
            return 9;
        }

        return 10;
    }
}
