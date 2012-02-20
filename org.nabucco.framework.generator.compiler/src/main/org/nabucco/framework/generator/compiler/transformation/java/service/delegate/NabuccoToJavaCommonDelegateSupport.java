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
package org.nabucco.framework.generator.compiler.transformation.java.service.delegate;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.Parameter;
import org.nabucco.framework.mda.model.java.JavaModelException;

/**
 * NabuccoToJavaCommonDelegateSupport
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
class NabuccoToJavaCommonDelegateSupport implements ViewConstants {

    private static final String EMPTY_SERVICE_MSG = "EmptyServiceMessage";

    private static final String IMPORT_EMPTY_SERVICE_MSG = "org.nabucco.framework.base.facade.message.EmptyServiceMessage";

    /**
     * Resolves the service request message
     * 
     * @param method
     *            the service operation
     * @param container
     *            the method container
     * 
     * @return the request as string
     * 
     * @throws JavaModelException
     *             if the import cannot be created
     */
    static final String getRequest(final MethodDeclaration method,
            final JavaAstContainter<AbstractMethodDeclaration> container) throws JavaModelException {
        if (method.parameterList.nodeListOptional.nodes.isEmpty()) {
            container.getImports().add(IMPORT_EMPTY_SERVICE_MSG);
            return EMPTY_SERVICE_MSG;
        }
        final Parameter param = (Parameter) method.parameterList.nodeListOptional.nodes.get(0);
        return param.nodeToken.tokenImage;
    }

    /**
     * Resolves the service response message.
     * 
     * @param method
     *            the service operation
     * @param container
     *            the method container
     * 
     * @return the response as string
     * 
     * @throws JavaModelException
     *             if the import cannot be created
     */
    static final String getResponse(final MethodDeclaration method,
            final JavaAstContainter<AbstractMethodDeclaration> container) throws JavaModelException {
        final String rs = ((NodeToken) method.nodeChoice.choice).tokenImage;
        if (rs == null || rs.equalsIgnoreCase(VOID)) {
            container.getImports().add(IMPORT_EMPTY_SERVICE_MSG);
            return EMPTY_SERVICE_MSG;
        }
        return rs;
    }

}
