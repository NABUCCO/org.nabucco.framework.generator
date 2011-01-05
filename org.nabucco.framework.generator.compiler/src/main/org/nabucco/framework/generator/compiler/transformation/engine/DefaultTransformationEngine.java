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
package org.nabucco.framework.generator.compiler.transformation.engine;

import java.util.List;

import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformation;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationContext;
import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;
import org.nabucco.framework.generator.compiler.transformation.java.application.NabuccoToJavaApplicationTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.basetype.NabuccoToJavaBasetypeTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.component.NabuccoToJavaComponentTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.datatype.NabuccoToJavaDatatypeTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.enumeration.NabuccoToJavaEnumerationTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.exception.NabuccoToJavaExceptionTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.message.NabuccoToJavaMessageTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.service.NabuccoToJavaServiceTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.view.command.NabuccoToJavaRcpViewCommandTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.view.edit.NabuccoToJavaViewEditTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.view.list.NabuccoToJavaViewListTransformation;
import org.nabucco.framework.generator.compiler.transformation.java.view.search.NabuccoToJavaRcpViewSearchTransformation;
import org.nabucco.framework.generator.compiler.transformation.xml.component.NabuccoToXmlComponentTransformation;
import org.nabucco.framework.generator.compiler.transformation.xml.exception.NabuccoToXmlServiceExceptionTransformation;
import org.nabucco.framework.generator.compiler.transformation.xml.service.NabuccoToXmlServiceTransformation;
import org.nabucco.framework.generator.parser.model.NabuccoModel;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.xml.XmlModel;

/**
 * DefaultTransformationEngine
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class DefaultTransformationEngine extends NabuccoTransformationEngine {

    /**
     * Creates a new {@link DefaultTransformationEngine} instance by a root directory.
     * 
     * @param rootDir
     *            the component root directory
     * 
     * @throws NabuccoTransformationException
     */
    public DefaultTransformationEngine(String rootDir, NabuccoCompilerOptions options)
            throws NabuccoTransformationException {
        super(rootDir, options);
    }

    @Override
    protected void produceTransformations(MdaModel<NabuccoModel> source,
            List<NabuccoTransformation<?>> transformationList) {

        String rootDir = super.getRootDir();
        NabuccoCompilerOptions options = super.getOptions();

        MdaModel<JavaModel> javaTarget = super.getJavaTarget();
        MdaModel<XmlModel> xmlTarget = super.getXmlTarget();

        NabuccoTransformationContext context = new NabuccoTransformationContext(rootDir, options);

        switch (source.getModel().getNabuccoType()) {

        case APPLICATION:
            transformationList.add(new NabuccoToJavaApplicationTransformation(source, javaTarget, context));
            break;
            
        case BASETYPE:
            transformationList.add(new NabuccoToJavaBasetypeTransformation(source, javaTarget, context));
            break;
            
        case COMPONENT:
            transformationList.add(new NabuccoToJavaComponentTransformation(source, javaTarget, context));
            transformationList.add(new NabuccoToXmlComponentTransformation(source, xmlTarget, context));
            break;

        case DATATYPE:
            transformationList.add(new NabuccoToJavaDatatypeTransformation(source, javaTarget, context));
            break;

        case ENUMERATION:
            transformationList.add(new NabuccoToJavaEnumerationTransformation(source, javaTarget, context));
            break;

        case EXCEPTION:
            transformationList.add(new NabuccoToJavaExceptionTransformation(source, javaTarget, context));
            break;

        case MESSAGE:
            transformationList.add(new NabuccoToJavaMessageTransformation(source, javaTarget, context));
            break;

        case SERVICE:
            transformationList.add(new NabuccoToJavaServiceTransformation(source, javaTarget, context));
            transformationList.add(new NabuccoToXmlServiceTransformation(source, xmlTarget, context));
            transformationList.add(new NabuccoToXmlServiceExceptionTransformation(source, xmlTarget, context));
            break;

        // RCP Transformations

        case EDIT_VIEW:
            transformationList.add(new NabuccoToJavaViewEditTransformation(source, javaTarget, context));
            break;

        case LIST_VIEW:
            transformationList.add(new NabuccoToJavaViewListTransformation(source, javaTarget, context));
            break;

        case SEARCH_VIEW:
            transformationList.add(new NabuccoToJavaRcpViewSearchTransformation(source, javaTarget, context));
            break;

        case COMMAND:
            transformationList.add(new NabuccoToJavaRcpViewCommandTransformation(source, javaTarget, context));
            break;

        // Add other transformations here.

        default:
            throw new IllegalStateException("NabuccoType '" + source.getModel().getNabuccoType()
                    + "' is not supported.");
        }
    }

}
