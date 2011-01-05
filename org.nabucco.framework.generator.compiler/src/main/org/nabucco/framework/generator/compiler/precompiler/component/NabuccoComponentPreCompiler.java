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
package org.nabucco.framework.generator.compiler.precompiler.component;

import java.util.List;
import java.util.Vector;

import org.nabucco.framework.generator.compiler.precompiler.NabuccoPreCompilerOptions;
import org.nabucco.framework.generator.compiler.precompiler.NabuccoPreCompilerSupport;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentPropertyDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeChoice;
import org.nabucco.framework.generator.parser.syntaxtree.NodeListOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;

/**
 * NabuccoComponentPreCompiler
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoComponentPreCompiler extends NabuccoPreCompilerSupport {

    private static final String TYPE_COMPONENT_RELATION = "ComponentRelationService";

    private static final String IMPORT_COMPONENT_RELATION = "org.nabucco.framework.base.facade.service.componentrelation.ComponentRelationService";

    @Override
    public void visit(NabuccoUnit unit, NabuccoPreCompilerOptions options) {
        super.visit(unit, options);

        if (!unit.nodeListOptional.present()) {
            unit.nodeListOptional.nodes = new Vector<Node>();
        }
        List<Node> importList = unit.nodeListOptional.nodes;

        NodeToken name = new NodeToken(IMPORT_COMPONENT_RELATION);
        ImportDeclaration importDeclaration = new ImportDeclaration(name);
        importDeclaration.setParent(unit.nodeListOptional);
        importList.add(importDeclaration);
    }

    @Override
    public void visit(ComponentStatement component, NabuccoPreCompilerOptions options) {
        super.visit(component, options);

        NodeToken name = new NodeToken(TYPE_COMPONENT_RELATION);
        AnnotationDeclaration annotations = new AnnotationDeclaration(new NodeListOptional());
        NodeChoice visibility = new NodeChoice(new NodeToken(NabuccoModifierType.PUBLIC.getName()));

        ServiceDeclaration service = new ServiceDeclaration(annotations, visibility, name);

        ComponentPropertyDeclaration property = new ComponentPropertyDeclaration(new NodeChoice(
                service, 2));

        property.setParent(component);

        if (!component.nodeListOptional.present()) {
            component.nodeListOptional.nodes = new Vector<Node>();
        }

        List<Node> declarations = component.nodeListOptional.nodes;
        declarations.add(property);
    }

}
