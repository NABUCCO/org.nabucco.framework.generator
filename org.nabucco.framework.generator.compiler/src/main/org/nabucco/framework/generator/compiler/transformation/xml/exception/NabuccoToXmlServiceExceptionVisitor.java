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
package org.nabucco.framework.generator.compiler.transformation.xml.exception;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.util.dependency.NabuccoDependencyResolver;
import org.nabucco.framework.generator.compiler.transformation.xml.constants.EjbJarConstants;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorContext;
import org.nabucco.framework.generator.compiler.transformation.xml.visitor.NabuccoToXmlVisitorSupport;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.MethodDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.NodeSequence;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;

/**
 * NabuccoToXmlServiceExceptionVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoToXmlServiceExceptionVisitor extends NabuccoToXmlVisitorSupport implements
        EjbJarConstants {

    /**
     * Creates a new {@link NabuccoToXmlServiceExceptionVisitor} instance.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToXmlServiceExceptionVisitor(NabuccoToXmlVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    public void visit(MethodDeclaration nabuccoMethod, MdaModel<XmlModel> target) {

        if (nabuccoMethod.nodeOptional.present()) {

            String componentName = super.getComponentName(null, null);

            final NodeSequence nodeSequence = (NodeSequence) nabuccoMethod.nodeOptional.node;

            for (Node node : nodeSequence.nodes) {

                if (!(node instanceof NodeToken)) {
                    continue;
                }

                String exceptionType = ((NodeToken) node).tokenImage;

                if (exceptionType.equals("throws")) {
                    continue;
                }

                String exceptionImport = super.resolveImport(exceptionType);

                NabuccoToXmlVisitorContext context = super.getVisitorContext();
                NabuccoToXmlVisitorContext contextClone = new NabuccoToXmlVisitorContext(context);

                NabuccoToXmlExceptionVisitor visitor = new NabuccoToXmlExceptionVisitor(
                        contextClone, componentName);

                try {

                    String pkg = super.getVisitorContext().getPackage();
                    MdaModel<NabuccoModel> exceptionModel = NabuccoDependencyResolver.getInstance()
                            .resolveDependency(context, pkg, exceptionImport);

                    exceptionModel.getModel().getUnit().accept(visitor, target);
                } catch (NabuccoTransformationException e) {
                    throw new NabuccoVisitorException("Error resolving service exception.", e);
                }
            }
        }
    }
}
