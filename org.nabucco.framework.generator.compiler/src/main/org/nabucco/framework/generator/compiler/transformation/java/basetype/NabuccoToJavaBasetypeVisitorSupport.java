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
package org.nabucco.framework.generator.compiler.transformation.java.basetype;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationType;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;

import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.discriminator.LiteralType;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaBasetypeVisitorSupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToJavaBasetypeVisitorSupport {

    private static final String TEMPLATE_NAME = "Basetype";
    
    /**
     * Private constructor must not be invoked.
     */
    private NabuccoToJavaBasetypeVisitorSupport() {
    }

    /**
     * Creates default values for the annotation declaration
     * 
     * @param annotationDeclaration
     *            the nabucco annotation
     * @param superType
     *            the type of the super basetype
     * @param type
     *            the type
     */
    public static void createDefaultValues(AnnotationDeclaration annotationDeclaration,
            String superType, TypeDeclaration type) {

        NabuccoAnnotation annotation = NabuccoAnnotationMapper.getInstance().mapToAnnotation(
                annotationDeclaration, NabuccoAnnotationType.DEFAULT);

        if (annotation != null) {
            NabuccoToJavaBasetypeVisitorSupport.addDefaultValue(annotation, superType, type);
        }
    }

    /**
     * Adds the default value to a basetype statement.
     * 
     * @param defaultValue
     *            the defalt value
     * @param superType
     *            the super type
     * @param type
     *            the type to holding the constructor
     */
    private static void addDefaultValue(NabuccoAnnotation defaultValue, String superType,
            TypeDeclaration type) {

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        String baseTypeType = NabuccoToJavaBasetypeReferences.mapToJavaType(superType);

        try {
            String typeName = javaFactory.getJavaAstType().getTypeName(type);
            ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type,
                    new JavaAstMethodSignature(typeName, new String[] {}));

            Literal literal = jamp.createLiteral(defaultValue.getValue(), (LiteralType
                    .mapFromString(baseTypeType)));

            MessageSend setValue = jamp.createMessageSend("setValue", jamp.createThisReference(),
                    Arrays.asList(literal));

            if (constructor.statements == null) {
                constructor.statements = new Statement[] {};
            }
            constructor.statements = Arrays.copyOf(constructor.statements,
                    constructor.statements.length + 1);
            constructor.statements[constructor.statements.length - 1] = setValue;

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error manipulating java ast.", e);
        }
    }

    public static void adjustAlternativeConstructor(String name, String superType,
            TypeDeclaration type) {

        try {
            JavaAstMethodSignature constuctorSignature = new JavaAstMethodSignature(name,
                    TEMPLATE_NAME);

            ConstructorDeclaration constructor = JavaAstElementFactory.getInstance()
                    .getJavaAstType().getConstructor(type, constuctorSignature);
            
            List<Argument> arguments = JavaAstElementFactory.getInstance().getJavaAstMethod().getAllArguments(constructor);

            if (arguments.size() == 1) {
                arguments.get(0).type = JavaAstModelProducer.getInstance().createTypeReference(
                        NabuccoToJavaBasetypeReferences.mapToJavaType(superType), false);
            }
            
        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error modifying Basetype constructor.", e);
        }
    }

}
