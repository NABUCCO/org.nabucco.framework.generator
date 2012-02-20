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
package org.nabucco.framework.generator.compiler.transformation.xml.visitor;

import java.util.Arrays;

import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeMapping;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTraversingVisitor;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorContext;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * NabuccoToXmlTraversingVisitor
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoToXmlTraversingVisitor extends NabuccoTraversingVisitor<Object> {

    private static final BasetypeMapping[] JPA_BASETYPE_CANDIDATES = { BasetypeMapping.N_TIME,
            BasetypeMapping.N_TIMESTAMP, BasetypeMapping.N_DATE };

    private boolean jpaSuffix;

    /**
     * Creates a new {@link NabuccoToXmlTraversingVisitor} instance.
     * 
     * @param context
     *            the visitor context
     */
    public NabuccoToXmlTraversingVisitor(NabuccoVisitorContext context) {
        super(context);
        jpaSuffix = false;
    }

    public boolean isJpaSuffix() {
        return jpaSuffix;
    }

    @Override
    public void visit(BasetypeDeclaration n, Object argu) {
        subVisit(n.nodeToken1.tokenImage, this);
        super.visit(n, argu);
    }

    @Override
    public void visit(BasetypeStatement n, Object argu) {

        if (n.nodeOptional.present()) {

            Node node = n.nodeOptional.node;
            if (node instanceof ExtensionDeclaration) {

                Node superTypeNameNode = ((ExtensionDeclaration) node).nodeChoice.choice;
                if (superTypeNameNode instanceof NodeToken) {

                    String superTypeName = ((NodeToken) superTypeNameNode).tokenImage;
                    BasetypeMapping mapping = mapToBasetypeMapping(superTypeName);

                    if (mapping != null) {
                        if (Arrays.asList(JPA_BASETYPE_CANDIDATES).contains(mapping)) {
                            jpaSuffix = true;
                        }
                    } else {
                        subVisit(superTypeName, this);
                    }
                }

            }
        }
    }

    /**
     * Returns a BasetypeMapping for String
     * 
     * @param in
     *            resolved import type
     * @return BasetypeMapping if mapping is possible, <code>null</code> otherwise
     */
    private BasetypeMapping mapToBasetypeMapping(String in) {
        for (BasetypeMapping current : BasetypeMapping.values()) {
            if (in.compareTo(current.getName()) == 0) {
                return current;
            }
        }
        return null;
    }
}
