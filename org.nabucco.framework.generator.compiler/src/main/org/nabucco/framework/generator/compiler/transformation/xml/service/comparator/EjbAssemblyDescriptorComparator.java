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
package org.nabucco.framework.generator.compiler.transformation.xml.service.comparator;

import java.util.Comparator;

import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.w3c.dom.Node;

/**
 * EjbAssemblyDescriptorComparator
 * <p/>
 * Compares XML Nodes of attributes and sorts them to the XSD order.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class EjbAssemblyDescriptorComparator implements Comparator<Node>, EjbJarConstants {

    /**
     * Singleton instance.
     */
    private static EjbAssemblyDescriptorComparator instance = new EjbAssemblyDescriptorComparator();

    /**
     * Private constructor.
     */
    private EjbAssemblyDescriptorComparator() {
    }

    /**
     * Singleton access.
     * 
     * @return the EjbAssemblyDescriptorComparator instance.
     */
    public static EjbAssemblyDescriptorComparator getInstance() {
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

        if (name.equals(CONTAINER_TRANSACTION)) {
            return 1;
        } else if (name.equals(INTERCEPTOR_BINDING)) {
            return 2;
        } else if (name.equals(APPLICATION_EXCEPTION)) {
            return 3;
        }

        return 4;
    }
}
