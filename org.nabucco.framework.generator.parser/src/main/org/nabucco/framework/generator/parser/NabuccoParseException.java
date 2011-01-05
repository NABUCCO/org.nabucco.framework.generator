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
package org.nabucco.framework.generator.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nabucco.framework.generator.parser.model.NabuccoModelException;

/**
 * NabuccoParseException
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoParseException extends NabuccoModelException {

    private static final long serialVersionUID = 1L;

    private static final Pattern PATTERN = Pattern.compile("\\d+");

    private int lineStart;

    private int lineEnd;

    private int columnStart;

    private int columnEnd;

    /**
     * Creates a new {@link NabuccoParseException} instance.
     * 
     * @param fileName
     *            name of the file
     * @param pe
     *            the parse exception
     */
    public NabuccoParseException(String fileName, ParseException pe) {
        super(fileName, NabuccoParseExceptionMapper.createErrorMessage(pe));
        this.lineStart = pe.currentToken.beginLine;
        this.lineEnd = pe.currentToken.next.beginLine;
        this.columnStart = pe.currentToken.beginColumn;
        this.columnEnd = pe.currentToken.next.beginColumn;
    }

    /**
     * Creates a new {@link NabuccoParseException} instance.
     * 
     * @param fileName
     *            name of the file
     * @param te
     *            the lexical error
     */
    public NabuccoParseException(String fileName, TokenMgrError te) {
        super(fileName, te.getMessage());

        Matcher matcher = PATTERN.matcher(te.getMessage());
        List<String> tokens = new ArrayList<String>();

        while (matcher.find()) {
            tokens.add(matcher.group());
        }
            
        if (tokens.size() >= 2) {
            this.lineStart = Integer.parseInt(tokens.get(0));
            this.columnStart = Integer.parseInt(tokens.get(1));
        }
        
        this.lineEnd = -1;
        this.columnEnd = -1;
    }

    /**
     * Creates a new {@link NabuccoParseException} instance.
     * 
     * @param fileName
     *            name of the file
     * @param the
     *            exception message
     * @param lineStart
     *            the starting line
     * @param lineEnd
     *            the ending line
     * @param columnStart
     *            the starting column
     * @param columnEnd
     *            the ending column
     */
    public NabuccoParseException(String fileName, String msg, int lineStart, int lineEnd,
            int columnStart, int columnEnd) {
        super(fileName, msg);
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }

    /**
     * Getter for the lineStart.
     * 
     * @return Returns the lineStart.
     */
    public int getLineStart() {
        return this.lineStart;
    }

    /**
     * Getter for the lineEnd.
     * 
     * @return Returns the lineEnd.
     */
    public int getLineEnd() {
        return this.lineEnd;
    }

    /**
     * Getter for the columnStart.
     * 
     * @return Returns the columnStart.
     */
    public int getColumnStart() {
        return this.columnStart;
    }

    /**
     * Getter for the columnEnd.
     * 
     * @return Returns the columnEnd.
     */
    public int getColumnEnd() {
        return this.columnEnd;
    }

}
