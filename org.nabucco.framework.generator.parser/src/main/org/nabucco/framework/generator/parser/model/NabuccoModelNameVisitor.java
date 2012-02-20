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
package org.nabucco.framework.generator.parser.model;

import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.CommandStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ListViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst;

/**
 * NabuccoModelNameVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoModelNameVisitor extends GJVoidDepthFirst<StringBuilder> {

    @Override
    public void visit(ComponentStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(DatatypeStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(BasetypeStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(EnumerationStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(ExceptionStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(ServiceStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(MessageStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(EditViewStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(ListViewStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(SearchViewStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

    @Override
    public void visit(CommandStatement statement, StringBuilder name) {
        name.append(statement.nodeToken2.tokenImage);
    }

}
