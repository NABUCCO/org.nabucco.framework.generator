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
package org.nabucco.framework.generator.compiler.transformation.java.datatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.TraversingNabuccoToJavaVisitor;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

/**
 * CodePathVisitor
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class CodePathVisitor extends TraversingNabuccoToJavaVisitor<Void> {

    private static final char VALUE_SEPERATOR = '=';

    private Map<String, String> replacements;

    private NabuccoToJavaVisitorContext initialContext;

    /**
     * @param mapToAnnotationList
     * @param context
     */
    public CodePathVisitor(List<NabuccoAnnotation> mapToAnnotationList,
            NabuccoToJavaVisitorContext context) {
        super(new NabuccoToJavaVisitorContext(context));
        initialContext = context;
        replacements = new HashMap<String, String>();
        for (NabuccoAnnotation current : mapToAnnotationList) {
            replacements.put(getFieldName(current), getCodePathValue(current));
        }
    }

    @Override
    public void visit(DatatypeDeclaration n, Void argu) {
        if (isTypeValid(n) && replacements.containsKey(n.nodeToken2.tokenImage)) {
            CodePathSupport.createCodePath(n.nodeToken2.tokenImage,
                    replacements.get(n.nodeToken2.tokenImage), initialContext);
        }
        super.visit(n, argu);
    }

    private boolean isTypeValid(DatatypeDeclaration n) {
        Node choice = n.nodeChoice1.choice;
        if (choice instanceof NodeToken) {
            String tokenImage = ((NodeToken) choice).tokenImage;
            return resolveImport(tokenImage).compareTo(CodePathSupport.IMPORT_CODE) == 0;
        }
        return false;
    }

    @Override
    public void visit(ExtensionDeclaration n, Void argu) {
        Node choice = n.nodeChoice.choice;
        if (choice instanceof NodeToken) {
            String tokenImage = ((NodeToken) choice).tokenImage;
            subVisit(tokenImage, argu);
        }
    }

    private String getCodePathValue(NabuccoAnnotation anno) {
        String value = anno.getValue();
        Integer seperatorPosition = getSeperatorPosition(value);
        if (seperatorPosition > -1) {
            return value.substring(seperatorPosition + 1).trim();
        }
        return "";
    }

    private String getFieldName(NabuccoAnnotation anno) {
        String value = anno.getValue();
        Integer seperatorPosition = getSeperatorPosition(value);
        if (seperatorPosition > -1) {
            return value.substring(0, seperatorPosition).trim();
        }
        return "";
    }

    private Integer getSeperatorPosition(String input) {
        return input.indexOf(VALUE_SEPERATOR, 0);
    }

}
