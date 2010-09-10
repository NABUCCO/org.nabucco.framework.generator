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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.TraversingNabuccoToJavaVisitor;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ColumnDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.CommandStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentPropertyDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.CustomDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationLiteralDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionParameterDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.InputFieldDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledComboBoxDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledInputFieldDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledListPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.LabeledPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ListPickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ListViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.MapDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeList;
import org.nabucco.framework.generator.parser.syntaxtree.NodeListOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.generator.parser.syntaxtree.ParameterList;
import org.nabucco.framework.generator.parser.syntaxtree.PickerDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.PropertyDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ServicePropertyDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ViewDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.WidgetDeclaration;


/**
 * StructuredPropertyPathSupport
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public abstract class StructuredPropertyPathSupport extends
        TraversingNabuccoToJavaVisitor<StructuredPropertyPathEntry> {

    /**
     * @param context
     */
    public StructuredPropertyPathSupport(NabuccoToJavaVisitorContext context) {
        super(context);
    }

    private Map<Class<? extends Node>, Set<StructuredPropertyPathExtension>> extensions;

    /**
     * @return Returns the extensions.
     */
    private Set<StructuredPropertyPathExtension> getExtensions(Class<? extends Node> node) {
        if (this.extensions == null) {
            this.extensions = new HashMap<Class<? extends Node>, Set<StructuredPropertyPathExtension>>();
        }
        if (this.extensions.get(node) == null) {
            this.extensions.put(node, new HashSet<StructuredPropertyPathExtension>());
        }
        return extensions.get(node);
    }

    private void handleExtensions(Node n, StructuredPropertyPathEntry argu) {
        for (StructuredPropertyPathExtension currentExtension : getExtensions(n.getClass())) {
            n.accept(currentExtension, argu);
        }
    }

    /**
     * Adds an {@link StructuredPropertyPathExtension} to a {@link StructuredPropertyPath}.
     * 
     * @param extension
     */
    public void addExtension(StructuredPropertyPathExtension extension) {
        for (Class<? extends Node> current : extension.getVisitedTypes()) {
            getExtensions(current).add(extension);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.AnnotationDeclaration, java.lang.Object)
     */
    @Override
    public void visit(AnnotationDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.BasetypeDeclaration, java.lang.Object)
     */
    @Override
    public void visit(BasetypeDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.BasetypeStatement, java.lang.Object)
     */
    @Override
    public void visit(BasetypeStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ColumnDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ColumnDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ComboBoxDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ComboBoxDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.CommandStatement, java.lang.Object)
     */
    @Override
    public void visit(CommandStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ComponentDatatypeDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ComponentDatatypeDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ComponentDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ComponentDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ComponentPropertyDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ComponentPropertyDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ComponentStatement, java.lang.Object)
     */
    @Override
    public void visit(ComponentStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.CustomDeclaration, java.lang.Object)
     */
    @Override
    public void visit(CustomDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.DatatypeDeclaration, java.lang.Object)
     */
    @Override
    public void visit(DatatypeDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.DatatypeStatement, java.lang.Object)
     */
    @Override
    public void visit(DatatypeStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.EditViewStatement, java.lang.Object)
     */
    @Override
    public void visit(EditViewStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.EnumerationDeclaration, java.lang.Object)
     */
    @Override
    public void visit(EnumerationDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.EnumerationLiteralDeclaration, java.lang.Object)
     */
    @Override
    public void visit(EnumerationLiteralDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.EnumerationStatement, java.lang.Object)
     */
    @Override
    public void visit(EnumerationStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ExceptionParameterDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ExceptionParameterDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ExceptionStatement, java.lang.Object)
     */
    @Override
    public void visit(ExceptionStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ExtensionDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ExtensionDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.InputFieldDeclaration, java.lang.Object)
     */
    @Override
    public void visit(InputFieldDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.LabeledComboBoxDeclaration, java.lang.Object)
     */
    @Override
    public void visit(LabeledComboBoxDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.LabeledInputFieldDeclaration, java.lang.Object)
     */
    @Override
    public void visit(LabeledInputFieldDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.LabeledListPickerDeclaration, java.lang.Object)
     */
    @Override
    public void visit(LabeledListPickerDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.LabeledPickerDeclaration, java.lang.Object)
     */
    @Override
    public void visit(LabeledPickerDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ListPickerDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ListPickerDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ListViewStatement, java.lang.Object)
     */
    @Override
    public void visit(ListViewStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.MapDeclaration, java.lang.Object)
     */
    @Override
    public void visit(MapDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.MessageStatement, java.lang.Object)
     */
    @Override
    public void visit(MessageStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.MethodDeclaration, java.lang.Object)
     */
    @Override
    public void visit(MethodDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.NabuccoStatement, java.lang.Object)
     */
    @Override
    public void visit(NabuccoStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.NabuccoUnit, java.lang.Object)
     */
    @Override
    public void visit(NabuccoUnit n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.NodeList, java.lang.Object)
     */
    @Override
    public void visit(NodeList n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.NodeListOptional, java.lang.Object)
     */
    @Override
    public void visit(NodeListOptional n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.NodeOptional, java.lang.Object)
     */
    @Override
    public void visit(NodeOptional n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.NodeSequence, java.lang.Object)
     */
    @Override
    public void visit(NodeSequence n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.NodeToken, java.lang.Object)
     */
    @Override
    public void visit(NodeToken n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.Parameter, java.lang.Object)
     */
    @Override
    public void visit(Parameter n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ParameterList, java.lang.Object)
     */
    @Override
    public void visit(ParameterList n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.PickerDeclaration, java.lang.Object)
     */
    @Override
    public void visit(PickerDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.PropertyDeclaration, java.lang.Object)
     */
    @Override
    public void visit(PropertyDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.SearchViewStatement, java.lang.Object)
     */
    @Override
    public void visit(SearchViewStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ServiceDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ServiceDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ServicePropertyDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ServicePropertyDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ServiceStatement, java.lang.Object)
     */
    @Override
    public void visit(ServiceStatement n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.ViewDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ViewDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.nabucco.framework.generator.parser.visitor.GJVoidDepthFirst#visit(org.nabucco.framework.generator
     * .parser.syntaxtree.WidgetDeclaration, java.lang.Object)
     */
    @Override
    public void visit(WidgetDeclaration n, StructuredPropertyPathEntry argu) {
        this.handleExtensions(n, argu);
        super.visit(n, argu);
    }

}
