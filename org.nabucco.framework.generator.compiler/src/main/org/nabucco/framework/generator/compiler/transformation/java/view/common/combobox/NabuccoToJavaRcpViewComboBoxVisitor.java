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
package org.nabucco.framework.generator.compiler.transformation.java.view.common.combobox;

import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;

/**
 * NabuccoToJavaRcpViewComboBoxVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewComboBoxVisitor extends NabuccoToJavaVisitorSupport implements
        ViewConstants {

    /**
     * Creates a new {@link NabuccoToJavaRcpViewComboBoxVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewComboBoxVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EditViewStatement nabuccoEditView, MdaModel<JavaModel> target) {

        String name = nabuccoEditView.nodeToken2.tokenImage;
        AnnotationDeclaration annotationDeclaration = nabuccoEditView.annotationDeclaration;

        NabuccoToJavaRcpViewComboBoxHandlerVisitor visitorHandler = new NabuccoToJavaRcpViewComboBoxHandlerVisitor(
                super.getVisitorContext(), name, annotationDeclaration);
        nabuccoEditView.accept(visitorHandler, target);

        NabuccoToJavaRcpViewComboBoxContentProviderVisitor visitorContentProvider = new NabuccoToJavaRcpViewComboBoxContentProviderVisitor(
                super.getVisitorContext(), name, annotationDeclaration, nabuccoEditView);
        nabuccoEditView.accept(visitorContentProvider, target);

        NabuccoToJavaRcpViewComboBoxLabelProviderVisitor visitorLabelProvider = new NabuccoToJavaRcpViewComboBoxLabelProviderVisitor(
                super.getVisitorContext(), name, annotationDeclaration);
        nabuccoEditView.accept(visitorLabelProvider, target);
    }

    @Override
    public void visit(SearchViewStatement nabuccoSearchView, MdaModel<JavaModel> target) {

        String name = nabuccoSearchView.nodeToken2.tokenImage;
        AnnotationDeclaration annotationDeclaration = nabuccoSearchView.annotationDeclaration;

        NabuccoToJavaRcpViewComboBoxHandlerVisitor visitor = new NabuccoToJavaRcpViewComboBoxHandlerVisitor(
                super.getVisitorContext(), name, annotationDeclaration);
        nabuccoSearchView.accept(visitor, target);

        NabuccoToJavaRcpViewComboBoxContentProviderVisitor visitorContentProvider = new NabuccoToJavaRcpViewComboBoxContentProviderVisitor(
                super.getVisitorContext(), name, annotationDeclaration, nabuccoSearchView);
        nabuccoSearchView.accept(visitorContentProvider, target);

        NabuccoToJavaRcpViewComboBoxLabelProviderVisitor visitorLabelProvider = new NabuccoToJavaRcpViewComboBoxLabelProviderVisitor(
                super.getVisitorContext(), name, annotationDeclaration);
        nabuccoSearchView.accept(visitorLabelProvider, target);
    }

}
