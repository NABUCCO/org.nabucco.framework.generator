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
package org.nabucco.framework.generator.compiler.transformation.java.component;

import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstType;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ServerConstants;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstMethod;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * NabuccoToJavaComponentVisitorSupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
final class NabuccoToJavaComponentVisitorSupport implements ServerConstants {

    /**
     * Private constructor must not be invoked.
     */
    private NabuccoToJavaComponentVisitorSupport() {
    }

    /**
     * Creates the ComponentInterface operation
     * <p/>
     * <code>public Service getService() throws NabuccoServiceException;</code>
     * 
     * @param serviceName
     *            name of the service
     * @param type
     *            type of the service
     * @param operationName
     *            name of the operation
     * 
     * @return the container holding the operation
     * 
     * @throws JavaModelException
     */
    public static JavaAstContainter<MethodDeclaration> createComponentInterfaceOperation(String serviceName,
            TypeDeclaration type, String operationName) throws JavaModelException {

        if (operationName == null) {
            operationName = PREFIX_GETTER + serviceName;
        }

        // Extract method
        MethodDeclaration method = extractMethod(type, COMPONENT_INTERFACE_OPERATION);
        JavaAstMethod methodFactory = JavaAstElementFactory.getInstance().getJavaAstMethod();

        JavaAstContainter<MethodDeclaration> container = new JavaAstContainter<MethodDeclaration>(method,
                JavaAstType.METHOD);

        // Name and modifier
        methodFactory.setMethodName(method, operationName);

        // ComponentResponse
        TypeReference componentResponse = JavaAstModelProducer.getInstance().createTypeReference(serviceName, false);
        methodFactory.setReturnType(method, componentResponse);

        container.getImports().add(serviceName);

        return container;
    }

    private static MethodDeclaration extractMethod(TypeDeclaration type, String name) throws JavaModelException {
        JavaAstMethodSignature signature = new JavaAstMethodSignature(name);
        MethodDeclaration method = (MethodDeclaration) JavaAstElementFactory.getInstance().getJavaAstType()
                .getMethod(type, signature);
        return method;
    }

}
