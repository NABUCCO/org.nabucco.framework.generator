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
package org.nabucco.framework.generator.compiler.transformation.java.view;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.NabuccoToJavaVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.java.visitor.util.TraversingNabuccoToJavaVisitor;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaDatatypeFieldCollectionVisitor
 * 
 * @author Silas Schwarz, Stefanie Feld PRODYNA AG
 */
public class NabuccoToJavaDatatypeFieldCollectionVisitor extends
        TraversingNabuccoToJavaVisitor<Map<String, Map<String, JavaAstContainter<TypeReference>>>> implements
        ViewConstants {

    /**
     * Creates a new instance of NabuccoToJavaDatatypeFieldCollectionVisitor.
     * 
     * @param context
     *            The context
     */
    public NabuccoToJavaDatatypeFieldCollectionVisitor(NabuccoToJavaVisitorContext context) {
        super(context);
    }

    @Override
    public void visit(ExtensionDeclaration nabuccoExtension,
            Map<String, Map<String, JavaAstContainter<TypeReference>>> typeRefMap) {

        super.subVisit(((NodeToken) nabuccoExtension.nodeChoice.choice).tokenImage, typeRefMap);
        super.visit(nabuccoExtension, typeRefMap);
    }

    @Override
    public void visit(BasetypeDeclaration nabuccoBasetype,
            Map<String, Map<String, JavaAstContainter<TypeReference>>> fieldNameToTypeReferenceMap) {

        String fieldName = nabuccoBasetype.nodeToken3.tokenImage;
        String fieldType = nabuccoBasetype.nodeToken1.tokenImage;

        // select submap of Basetypes or initialize a new map
        Map<String, JavaAstContainter<TypeReference>> submap;
        if (fieldNameToTypeReferenceMap.containsKey(BASETYPE)) {
            submap = fieldNameToTypeReferenceMap.get(BASETYPE);
        } else {
            submap = new HashMap<String, JavaAstContainter<TypeReference>>();
            fieldNameToTypeReferenceMap.put(BASETYPE, submap);
        }

        if (!submap.containsKey(fieldName)) {
            try {
                JavaAstContainter<TypeReference> containter = new JavaAstContainter<TypeReference>(JavaAstModelProducer
                        .getInstance().createTypeReference(fieldType, false), JavaAstType.TYPE_REFERENCE);

                containter.getImports().add(resolveImport(fieldType));
                submap.put(fieldName, containter);
            } catch (JavaModelException e) {
                throw new NabuccoVisitorException(e);
            }
        }
    }

    @Override
    public void visit(EnumerationDeclaration nabuccoEnumeration,
            Map<String, Map<String, JavaAstContainter<TypeReference>>> fieldNameToTypeReferenceMap) {

        String fieldName = nabuccoEnumeration.nodeToken2.tokenImage;
        String fieldType = ((NodeToken) nabuccoEnumeration.nodeChoice1.choice).tokenImage;

        // select submap of Enumerations or initialize a new map
        Map<String, JavaAstContainter<TypeReference>> submap;
        if (fieldNameToTypeReferenceMap.containsKey(ENUMERATION)) {
            submap = fieldNameToTypeReferenceMap.get(ENUMERATION);
        } else {
            submap = new HashMap<String, JavaAstContainter<TypeReference>>();
            fieldNameToTypeReferenceMap.put(ENUMERATION, submap);
        }

        if (!submap.containsKey(fieldName)) {
            try {
                JavaAstContainter<TypeReference> containter = new JavaAstContainter<TypeReference>(JavaAstModelProducer
                        .getInstance().createTypeReference(fieldType, false), JavaAstType.TYPE_REFERENCE);

                containter.getImports().add(resolveImport(fieldType));
                submap.put(fieldName, containter);
            } catch (JavaModelException e) {
                throw new NabuccoVisitorException(e);
            }
        }
    }

    @Override
    public void visit(DatatypeDeclaration nabuccoEnumeration,
            Map<String, Map<String, JavaAstContainter<TypeReference>>> fieldNameToTypeReferenceMap) {

        String fieldName = nabuccoEnumeration.nodeToken2.tokenImage;
        String fieldType = ((NodeToken) nabuccoEnumeration.nodeChoice1.choice).tokenImage;

        // select submap of Datatypes or initialize a new map
        Map<String, JavaAstContainter<TypeReference>> submap;
        if (fieldNameToTypeReferenceMap.containsKey(DATATYPE)) {
            submap = fieldNameToTypeReferenceMap.get(DATATYPE);
        } else {
            submap = new HashMap<String, JavaAstContainter<TypeReference>>();
            fieldNameToTypeReferenceMap.put(DATATYPE, submap);
        }

        if (!submap.containsKey(fieldName)) {
            try {
                JavaAstContainter<TypeReference> containter = new JavaAstContainter<TypeReference>(JavaAstModelProducer
                        .getInstance().createTypeReference(fieldType, false), JavaAstType.TYPE_REFERENCE);

                containter.getImports().add(resolveImport(fieldType));
                submap.put(fieldName, containter);
            } catch (JavaModelException e) {
                throw new NabuccoVisitorException(e);
            }
        }
    }

}
