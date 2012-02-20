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
package org.nabucco.framework.generator.compiler.transformation.java.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorContext;
import org.nabucco.framework.mda.template.java.JavaTemplate;
import org.nabucco.framework.mda.template.java.JavaTemplateException;
import org.nabucco.framework.mda.template.xml.XmlTemplateException;

/**
 * NabuccoToJavaVisitorContext
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaVisitorContext extends NabuccoVisitorContext {

    private List<JavaAstContainter<? extends ASTNode>> containerList;

    private Map<String, JavaTemplate> templateMap = new HashMap<String, JavaTemplate>();

    /**
     * Creates an empty {@link NabuccoToJavaVisitorContext} instance.
     */
    public NabuccoToJavaVisitorContext() {
    }

    /**
     * Creates a visitor context with all existing templates of another context.
     * 
     * @param context
     *            another context.
     */
    public NabuccoToJavaVisitorContext(NabuccoToJavaVisitorContext context) {
        super(context);
        this.templateMap.putAll(context.templateMap);
    }

    /**
     * Getter for the AST container list.
     * 
     * @return Returns the containerList.
     */
    public List<JavaAstContainter<? extends ASTNode>> getContainerList() {
        if (this.containerList == null) {
            this.containerList = new ArrayList<JavaAstContainter<? extends ASTNode>>();
        }
        return this.containerList;
    }

    /**
     * Inserts a template into the context.
     * 
     * @param name
     *            name of the template
     * @param template
     *            the template
     * 
     * @throws JavaTemplateException
     */
    public void putTemplate(String name, JavaTemplate template) throws JavaTemplateException {

        if (template.getModel() == null || template.getModel().getUnitList().size() != 1) {
            throw new JavaTemplateException("Template does not contain a Java targetModel: " + name);
        }

        this.templateMap.put(name, template);
    }

    /**
     * Get a template from the context.
     * 
     * @param name
     *            the template name
     * 
     * @return the template
     * 
     * @throws XmlTemplateException
     */
    public JavaTemplate getTemplate(String name) throws JavaTemplateException {
        JavaTemplate template = this.templateMap.get(name);

        if (template == null) {
            throw new JavaTemplateException("Template not found: " + name);
        }

        return template;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JavaVisitor");
        builder.append(super.toString());
        builder.append("\n - ASTs=");
        builder.append((this.containerList == null) ? 0 : this.containerList.size());
        builder.append("\n - Templates=");
        builder.append(this.templateMap.size());
        return builder.toString();
    }

}
