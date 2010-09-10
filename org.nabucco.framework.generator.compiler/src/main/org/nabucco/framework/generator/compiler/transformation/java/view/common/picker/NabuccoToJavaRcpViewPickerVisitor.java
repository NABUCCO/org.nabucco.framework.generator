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
package org.nabucco.framework.generator.compiler.transformation.java.view.common.picker;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.view.NabuccoToJavaDatatypeFieldCollectionVisitor;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorSupport;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;

/**
 * NabuccoToJavaRcpViewPickerVisitor
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoToJavaRcpViewPickerVisitor extends NabuccoToJavaVisitorSupport implements
        ViewConstants {
    /**
     * Mapping from field name to field type properties of all datatypes.
     */
    private Map<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>> fieldNameToFieldTypeProperties = new HashMap<String, Map<String, Map<String, JavaAstContainter<TypeReference>>>>();

    /**
     * Mapping from Datatype/Enumeration/Basetype to field name to type reference.
     */
    private Map<String, Map<String, JavaAstContainter<TypeReference>>> fieldNameToTypeReference = new HashMap<String, Map<String, JavaAstContainter<TypeReference>>>();

    /**
     * Creates a new {@link NabuccoToJavaRcpViewPickerVisitor} instance.
     * 
     * @param visitorContext
     *            the context of the visitor.
     */
    public NabuccoToJavaRcpViewPickerVisitor(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(EditViewStatement nabuccoEditView, MdaModel<JavaModel> target) {
        super.visit(nabuccoEditView, target);

        String name = nabuccoEditView.nodeToken2.tokenImage;
        AnnotationDeclaration annotationDeclaration = nabuccoEditView.annotationDeclaration;

        NabuccoToJavaRcpViewPickerHandlerVisitor visitorHandler = new NabuccoToJavaRcpViewPickerHandlerVisitor(
                super.getVisitorContext(), name, annotationDeclaration,
                this.fieldNameToTypeReference, this.fieldNameToFieldTypeProperties);
        nabuccoEditView.accept(visitorHandler, target);

        NabuccoToJavaRcpViewPickerContentProviderVisitor visitorContentProvider = new NabuccoToJavaRcpViewPickerContentProviderVisitor(
                super.getVisitorContext(), annotationDeclaration, this.fieldNameToTypeReference,
                this.fieldNameToFieldTypeProperties);
        nabuccoEditView.accept(visitorContentProvider, target);

        NabuccoToJavaRcpViewPickerContentProviderHandlerVisitor visitorContentProviderHandler = new NabuccoToJavaRcpViewPickerContentProviderHandlerVisitor(
                super.getVisitorContext(), annotationDeclaration, this.fieldNameToTypeReference,
                this.fieldNameToFieldTypeProperties);
        nabuccoEditView.accept(visitorContentProviderHandler, target);

        // only needed for datatype-picker
        NabuccoToJavaRcpViewPickerComparatorVisitor visitorComparatorHandler = new NabuccoToJavaRcpViewPickerComparatorVisitor(
                super.getVisitorContext(), annotationDeclaration,
                this.fieldNameToFieldTypeProperties);
        nabuccoEditView.accept(visitorComparatorHandler, target);

        NabuccoToJavaRcpViewPickerLabelProviderVisitor visitorLabelProviderHandler = new NabuccoToJavaRcpViewPickerLabelProviderVisitor(
                super.getVisitorContext(), annotationDeclaration,
                this.fieldNameToFieldTypeProperties);
        nabuccoEditView.accept(visitorLabelProviderHandler, target);
    }

    @Override
    public void visit(SearchViewStatement nabuccoSearchView, MdaModel<JavaModel> target) {
        super.visit(nabuccoSearchView, target);

        String name = nabuccoSearchView.nodeToken2.tokenImage;
        AnnotationDeclaration annotationDeclaration = nabuccoSearchView.annotationDeclaration;

        NabuccoToJavaRcpViewPickerHandlerVisitor visitorHandler = new NabuccoToJavaRcpViewPickerHandlerVisitor(
                super.getVisitorContext(), name, annotationDeclaration,
                this.fieldNameToTypeReference, this.fieldNameToFieldTypeProperties);
        nabuccoSearchView.accept(visitorHandler, target);

        NabuccoToJavaRcpViewPickerContentProviderVisitor visitorContentProvider = new NabuccoToJavaRcpViewPickerContentProviderVisitor(
                super.getVisitorContext(), annotationDeclaration, this.fieldNameToTypeReference,
                this.fieldNameToFieldTypeProperties);
        nabuccoSearchView.accept(visitorContentProvider, target);

        NabuccoToJavaRcpViewPickerContentProviderHandlerVisitor visitorContentProviderHandler = new NabuccoToJavaRcpViewPickerContentProviderHandlerVisitor(
                super.getVisitorContext(), annotationDeclaration, this.fieldNameToTypeReference,
                this.fieldNameToFieldTypeProperties);
        nabuccoSearchView.accept(visitorContentProviderHandler, target);

        // only needed for datatype-picker
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoDatatype, MdaModel<JavaModel> target) {

        String unqualifiedType = ((NodeToken) nabuccoDatatype.nodeChoice1.choice).tokenImage;
        String importString = super.resolveImport(unqualifiedType);

        NabuccoToJavaVisitorContext context = new NabuccoToJavaVisitorContext(super
                .getVisitorContext());
        context.setRootDir(super.getVisitorContext().getRootDir());
        context.setOutDir(super.getVisitorContext().getOutDir());
        NabuccoToJavaDatatypeFieldCollectionVisitor visitor = new NabuccoToJavaDatatypeFieldCollectionVisitor(
                context);
        try {
            String pkg = super.getVisitorContext().getPackage();
            NabuccoDependencyResolver.getInstance().resolveDependency(context, pkg, importString)
                    .getModel().getUnit().accept(visitor, this.fieldNameToTypeReference);
        } catch (NabuccoTransformationException e) {
            throw new NabuccoVisitorException(e);
        }
        this.fieldNameToFieldTypeProperties.put(nabuccoDatatype.nodeToken2.tokenImage,
                this.fieldNameToTypeReference);

        super.visit(nabuccoDatatype, target);

    }

}
