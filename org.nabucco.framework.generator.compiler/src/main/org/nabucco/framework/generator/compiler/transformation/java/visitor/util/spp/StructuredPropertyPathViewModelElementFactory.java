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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp;

import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * StructuredPropertyPathElementFactory
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class StructuredPropertyPathViewModelElementFactory {

    private static StructuredPropertyPathViewModelElementFactory instance;

    /**
     * @return Returns the instance.
     */
    public static StructuredPropertyPathViewModelElementFactory getInstance() {
        if (instance == null) {
            instance = new StructuredPropertyPathViewModelElementFactory();
        }
        return instance;
    }

    private StructuredPropertyPathViewModelElementFactory() {

    }

    /**
     * Creates a getter method for a given {@link StructuredPropertyPath} root element and a
     * absolute name. This method checks several conditions in order to determinate how the method
     * should look like. taken in to account are the element type
     * {@link StructuredPropertyPathEntryType} the multiplicity and location in the path (special
     * treatment for cases such as the parent is a set)
     * 
     * @param root
     *            the root element of the {@link StructuredPropertyPath}
     * @param name
     *            the absolute mapped property name
     * @return a getter methods
     * @throws JavaModelException
     */
    public MethodDeclaration createGetter(StructuredPropertyPathEntry root, String name) throws JavaModelException {
        String orginalPath = name;
        StructuredPropertyPathEntry entry = root.getEntry(name);
        JavaAstModelProducer modelProducer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory elementFactory = JavaAstElementFactory.getInstance();
        name = OperationType.GETTER.format(convertToMethodName(name));
        MethodDeclaration methodDeclaration = modelProducer.createMethodDeclaration(name, null, false);

        switch (entry.getEntryType()) {
        case BASETYPE: {
            elementFactory.getJavaAstMethod().setReturnType(methodDeclaration,
                    modelProducer.createTypeReference("String", false));
            if (!isParentSet(root, orginalPath)) {
                methodDeclaration.statements = new Statement[] { modelProducer
                        .createReturnStatement(CommonOperationSupport.createNullSaveGetterChain(
                                modelProducer.createThisReference(), orginalPath.concat(".value"))) };
            } else {
                methodDeclaration.statements = new Statement[] { modelProducer.createReturnStatement(modelProducer
                        .createFieldThisReference(convertToMethodName(orginalPath))) };
            }
            break;
        }
        case ENUMERATION: {
            elementFactory.getJavaAstMethod().setReturnType(methodDeclaration,
                    modelProducer.createTypeReference("String", false));
            MessageSend getterChain = CommonOperationSupport.createGetterChain(modelProducer.createThisReference(),
                    orginalPath);
            getterChain = modelProducer.createMessageSend("name", getterChain, Collections.<Expression> emptyList());
            methodDeclaration.statements = new Statement[] { modelProducer.createReturnStatement(CommonOperationSupport
                    .getNullChecks(getterChain, OperationType.GETTER)) };
            break;
        }
        case DATATYPE: {
            if (entry.isMultiple()) {
                TypeReference parameterizedTypeReference = modelProducer.createParameterizedTypeReference("Set", false,
                        Arrays.asList(new TypeReference[] { entry.getTypeReference() }));
                elementFactory.getJavaAstMethod().setReturnType(methodDeclaration, parameterizedTypeReference);

            } else {
                elementFactory.getJavaAstMethod().setReturnType(methodDeclaration, entry.getTypeReference());
                if (isRootEntry(root, orginalPath)) {
                    methodDeclaration.statements = new Statement[] { modelProducer.createReturnStatement(modelProducer
                            .createFieldThisReference(orginalPath)) };
                } // no else case only getter for rooted datatypes

            }

            break;
        }
        case ROOT:
        default: {
            throw new IllegalStateException("unreachable access in create getter for structured property support");
        }
        }
        return methodDeclaration;
    }

    /**
     * Creates a setter method for a given {@link StructuredPropertyPath} root element and a
     * absolute name. This method checks several conditions in order to determinate how the method
     * should look like. taken in to account are the element type
     * {@link StructuredPropertyPathEntryType} the multiplicity and location in the path (special
     * treatment for cases such as the parent is a set)
     * 
     * @param root
     *            the root element of the {@link StructuredPropertyPath}
     * @param name
     *            the absolute mapped property name
     * @return a setter methods
     * @throws JavaModelException
     */
    public MethodDeclaration createSetter(StructuredPropertyPathEntry root, String name) throws JavaModelException {
        String orginalPath = name;
        StructuredPropertyPathEntry entry = root.getEntry(name);
        JavaAstModelProducer modelProducer = JavaAstModelProducer.getInstance();
        JavaAstElementFactory elementFactory = JavaAstElementFactory.getInstance();
        String propertyName = NabuccoTransformationUtility.firstToLower(convertToMethodName(name));
        name = OperationType.SETTER.format(propertyName);
        MethodDeclaration methodDeclaration = modelProducer.createMethodDeclaration(name, null, false);
        switch (entry.getEntryType()) {
        case BASETYPE: {
            if (isParentSet(root, orginalPath)) {
                elementFactory.getJavaAstMethod().addArgument(methodDeclaration,
                        modelProducer.createArgument(propertyName, modelProducer.createTypeReference("String", false)));
                ThisReference thisReference = modelProducer.createThisReference();
                FieldReference fieldReference = modelProducer.createFieldThisReference(propertyName);
                SingleNameReference parameterReference = modelProducer.createSingleNameReference(propertyName);
                methodDeclaration.statements = new Statement[] { modelProducer
                        .createMessageSend("updateProperty", thisReference, Arrays.asList(new Expression[] {
                                fieldReference,
                                modelProducer.createAssignment(fieldReference, modelProducer
                                        .createConditionalExpression(
                                                CommonOperationSupport.createNullCheck(parameterReference),
                                                parameterReference, CommonOperationSupport.getEmptyStringLiteral())) })) };
            } else {

            }

            break;
        }
        case DATATYPE: {
            if (entry.isMultiple()) {

            } else {
                elementFactory.getJavaAstMethod().addArgument(
                        methodDeclaration,
                        modelProducer.createArgument(propertyName,
                                modelProducer.createTypeReference(entry.getTypeReference().toString(), false)));
            }
            break;
        }
        case ENUMERATION: {
            break;
        }
        case ROOT:
            break;
        default: {

        }
        }
        return methodDeclaration;

    }

    /**
     * @param root
     * @param orginalPath
     * @return
     */
    private boolean isParentSet(StructuredPropertyPathEntry root, String orginalPath) {
        int index = orginalPath.lastIndexOf(StructuredPropertyPathEntry.SEPARATOR);
        if (index > 0) {
            return root.getEntry(orginalPath.substring(0, index)).isMultiple();
        }
        return false;
    }

    private String convertToMethodName(String path) {
        StringTokenizer st = new StringTokenizer(path, StructuredPropertyPathEntry.SEPARATOR.toString());
        path = "";
        while (st.hasMoreTokens()) {
            path = path.concat(NabuccoTransformationUtility.firstToUpper(st.nextToken()));
        }
        return path;
    }

    private boolean isRootEntry(StructuredPropertyPathEntry root, String path) {
        return !(path.split("\\" + StructuredPropertyPathEntry.SEPARATOR.toString()).length > 1);
    }
}
