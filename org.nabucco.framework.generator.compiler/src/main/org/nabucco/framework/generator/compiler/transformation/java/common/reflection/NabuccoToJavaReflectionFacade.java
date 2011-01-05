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
package org.nabucco.framework.generator.compiler.transformation.java.common.reflection;

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.java.common.basetype.BasetypeFacade;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExtensionDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaReflectionFacade
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoToJavaReflectionFacade {

    /** Signature for getProperties() method. */
    static final JavaAstMethodSignature GET_PROPERTIES = new JavaAstMethodSignature("getProperties");

    /**
     * Singleton instance.
     */
    private static NabuccoToJavaReflectionFacade instance = new NabuccoToJavaReflectionFacade();

    /**
     * Private constructor.
     */
    private NabuccoToJavaReflectionFacade() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoToJavaReflectionFacade instance.
     */
    public static NabuccoToJavaReflectionFacade getInstance() {
        return instance;
    }

    /**
     * Create getProperties() method for basetypes.
     * 
     * @param basetype
     *            nabucco basetype statement
     * @param unit
     *            java compilation unit
     * 
     * @throws JavaModelException
     *             when the reflection methods cannot be created
     */
    public void createReflection(BasetypeStatement basetype, JavaCompilationUnit unit)
            throws JavaModelException {

        if (!basetype.nodeOptional.present()) {
            return;
        }

        Node node = basetype.nodeOptional.node;
        if (!(node instanceof ExtensionDeclaration)) {
            return;
        }

        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();
        JavaAstModelProducer producer = JavaAstModelProducer.getInstance();

        ExtensionDeclaration extension = (ExtensionDeclaration) node;
        String superType = ((NodeToken) extension.nodeChoice.choice).tokenImage;
        String simpleType = BasetypeFacade.mapToPrimitiveType(superType);
        TypeReference simpleTypeRef = producer.createTypeReference(simpleType, false);
        TypeReference propertyTypeRef = producer.createParameterizedTypeReference(
                PropertyType.SIMPLE.getName(), false, Arrays.asList(simpleTypeRef));

        MethodDeclaration getProperties = (MethodDeclaration) javaFactory.getJavaAstType()
                .getMethod(unit.getType(), GET_PROPERTIES);

        MessageSend addProperty = (MessageSend) getProperties.statements[1];
        AllocationExpression allocation = (AllocationExpression) addProperty.arguments[0];
        allocation.type = propertyTypeRef;
        ClassLiteralAccess classAccess = (ClassLiteralAccess) allocation.arguments[1];
        classAccess.type = simpleTypeRef;

        ImportReference importRef = producer.createImportReference(PropertyType.SIMPLE.getImport());
        javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importRef);
    }

    /**
     * Create getProperties() method for datatypes.
     * 
     * @param datatype
     *            nabucco datatype statement
     * @param unit
     *            java compilation unit
     * 
     * @throws JavaModelException
     *             when the reflection methods cannot be created
     */
    public void createReflection(DatatypeStatement datatype, JavaCompilationUnit unit)
            throws JavaModelException {
        NabuccoToJavaDatatypeReflectionVisitor visitor = new NabuccoToJavaDatatypeReflectionVisitor();
        datatype.accept(visitor);
        visitor.finish(unit);
    }

    /**
     * Create getProperties() method for messages.
     * 
     * @param message
     *            nabucco message statement
     * @param unit
     *            java compilation unit
     * 
     * @throws JavaModelException
     *             when the reflection methods cannot be created
     */
    public void createReflection(MessageStatement message, JavaCompilationUnit unit)
            throws JavaModelException {
        NabuccoToJavaDatatypeReflectionVisitor visitor = new NabuccoToJavaDatatypeReflectionVisitor();
        message.accept(visitor);
        visitor.finish(unit);
    }

}
