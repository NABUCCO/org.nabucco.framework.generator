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
package org.nabucco.framework.generator.compiler.verifier.common;

import java.io.File;

import org.nabucco.framework.generator.compiler.verifier.NabuccoModelVerificationVisitor;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationErrorCriticality;
import org.nabucco.framework.generator.compiler.verifier.error.VerificationResult;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.CommandStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ListViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;

/**
 * NabuccoFileVerification
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoFileVerification extends NabuccoModelVerificationVisitor {

    @Override
    public void visit(ComponentStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(DatatypeStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(BasetypeStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(EnumerationStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(ExceptionStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(ServiceStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(MessageStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(EditViewStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(ListViewStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(SearchViewStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    @Override
    public void visit(CommandStatement statement, VerificationResult result) {
        verify(statement.nodeToken2, result);
    }

    /**
     * Verifies the statement name.
     * 
     * @param token
     *            statement name token
     * @param result
     *            the verification result
     */
    private void verify(NodeToken token, VerificationResult result) {
        NabuccoModel nabuccoModel = result.getModel();

        String path = nabuccoModel.getPath();
        String fileName = this.getFileName(path);
        String modelName = token.tokenImage;

        if (!(fileName.equals(modelName))) {
            int beginLine = token.beginLine;
            int endLine = token.endLine;
            int beginColumn = token.beginColumn;
            int endColumn = token.endColumn;

            result.addError(VerificationErrorCriticality.WARNING, beginLine, endLine, beginColumn, endColumn,
                    "Name of NABUCCO compilation unit does not match to its filename");
        }
    }

    /**
     * Retrieves the NBC filename from the path.
     * 
     * @param path
     *            the path to the .nbc file
     * 
     * @return the name of the file
     */
    private String getFileName(String path) {
        path = this.format(path);
        return path.substring(path.lastIndexOf('.') + 1);
    }

    /**
     * Removes the .nbc suffix.
     * 
     * @param path
     *            the path to format
     * 
     * @return the formatted path
     */
    private String format(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path to NABUCCO model is not valid.");
        }
        String lowerCase = path.toLowerCase();
        if (!lowerCase.contains(".nbc")) {
            throw new IllegalArgumentException("Path to NABUCCO model does not point to a valid .nbc file.");
        }
        return path.substring(0, lowerCase.lastIndexOf(".nbc")).replace(File.separatorChar, '.').replace('/', '.');
    }

}
