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

import org.nabucco.framework.generator.parser.model.NabuccoModelException;


/**
 * NabuccoParseExceptionMapper
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoParseExceptionMapper {

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoParseExceptionMapper() {
    }

    /**
     * Mapps a {@link ParseException} to a {@link NabuccoModelException}.
     * 
     * @param pe
     *            the parse exception
     * 
     * @return the mapped NabuccoModelException
     */
    public static String createErrorMessage(ParseException pe) {

        int line = pe.currentToken.next.beginLine;
        int column = pe.currentToken.next.beginColumn;

        String expected = getExpectedToken(pe);
        String encountered = getEncounteredToken(pe);
        
        StringBuilder message = new StringBuilder();
        message.append("Error at line ");
        message.append(line);
        message.append(", column ");
        message.append(column);
        message.append(". Encountered: [");

        message.append(encountered);
        message.append("]");

        if (pe.expectedTokenSequences.length == 1) {
            message.append(" Was expecting: [ ");
        } else {
            message.append(" Was expecting one of: [ ");
        }

        message.append(expected);
        message.append(" ].");
        
        return message.toString();
    }

    /**
     * Resolves the encountered token.
     * 
     * @param pe
     *            the parse exception
     * 
     * @return the encountered token
     */
    private static String getEncounteredToken(ParseException pe) {

        int maxSize = calculateMax(pe);

        StringBuilder token = new StringBuilder();

        Token tok = pe.currentToken.next;
        for (int i = 0; i < maxSize; i++) {
            if (tok.kind == 0) {
                token.append(pe.tokenImage[0]);
                break;
            }

            token.append(" ");
            token.append(pe.tokenImage[tok.kind]);
            token.append(" - \"");
            token.append(escape(tok.image));
            token.append("\" ");
            tok = tok.next;
        }

        return token.toString();
    }

    /**
     * Resolves the expected token.
     * 
     * @param pe
     *            the parse exception
     * 
     * @return the expected token
     */
    private static String getExpectedToken(ParseException pe) {
        StringBuilder expected = new StringBuilder();

        for (int i = 0; i < pe.expectedTokenSequences.length; i++) {
            for (int j = 0; j < pe.expectedTokenSequences[i].length; j++) {
                expected.append(pe.tokenImage[pe.expectedTokenSequences[i][j]]);

                if ((i + 1) < pe.expectedTokenSequences.length
                        || (j + 1) < pe.expectedTokenSequences[i].length) {
                    expected.append(", ");
                }
                
            }
        }

        return expected.toString();
    }

    /**
     * Calculate the maximum tokens.
     * 
     * @param pe
     *            the parse exception
     * 
     * @return the maximum number of tokens
     */
    private static int calculateMax(ParseException pe) {
        int maxSize = 0;
        for (int i = 0; i < pe.expectedTokenSequences.length; i++) {
            if (maxSize < pe.expectedTokenSequences[i].length) {
                maxSize = pe.expectedTokenSequences[i].length;
            }
        }
        return maxSize;
    }

    /**
     * Used to convert raw characters to their escaped version when these raw version cannot be used
     * as part of an ASCII string literal.
     * 
     * @param message
     *            the message containing escape characters
     * 
     * @return the escaped message
     */
    private static String escape(String message) {
        StringBuilder token = new StringBuilder();
        char ch;
        for (int i = 0; i < message.length(); i++) {
            switch (message.charAt(i)) {
            case 0:
                continue;
            case '\b':
                token.append("\\b");
                continue;
            case '\t':
                token.append("\\t");
                continue;
            case '\n':
                token.append("\\n");
                continue;
            case '\f':
                token.append("\\f");
                continue;
            case '\r':
                token.append("\\r");
                continue;
            case '\"':
                token.append("\\\"");
                continue;
            case '\'':
                token.append("\\\'");
                continue;
            case '\\':
                token.append("\\\\");
                continue;
            default:
                if ((ch = message.charAt(i)) < 0x20 || ch > 0x7e) {
                    String s = "0000" + Integer.toString(ch, 16);
                    token.append("\\u" + s.substring(s.length() - 4, s.length()));
                } else {
                    token.append(ch);
                }
                continue;
            }
        }
        return token.toString();
    }

}
