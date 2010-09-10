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
package org.nabucco.framework.generator.compiler.transformation.util.file;

import org.nabucco.framework.generator.compiler.transformation.NabuccoTransformationException;

import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelException;
import org.nabucco.framework.mda.model.java.JavaModel;
import org.nabucco.framework.mda.model.java.file.JavaFileCreator;
import org.nabucco.framework.mda.model.xml.XmlModel;
import org.nabucco.framework.mda.model.xml.file.XmlFileCreator;

/**
 * NabuccoTargetFileCreator
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoTargetFileCreator {

    /**
     * Singleton instance.
     */
    private static NabuccoTargetFileCreator instance = new NabuccoTargetFileCreator();

    /**
     * Private constructor.
     */
    private NabuccoTargetFileCreator() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoTargetFileCreator instance.
     */
    public static NabuccoTargetFileCreator getInstance() {
        return instance;
    }

    /**
     * Creates .java files for a java model.
     * 
     * @param model
     *            the java model
     * @param rootDir
     *            the root directory
     * @param formatterConfigFile
     *            the configuration file for the java formatter
     * 
     * @throws NabuccoTransformationException
     */
    public void createJavaFiles(MdaModel<JavaModel> model, String rootDir,
            String formatterConfigFile) throws NabuccoTransformationException {
        try {
            JavaFileCreator fileCreator = new JavaFileCreator(model, rootDir, formatterConfigFile);
            fileCreator.createFiles();
        } catch (ModelException e) {
            throw new NabuccoTransformationException("Error creating java files.", e);
        }
    }

    /**
     * Creates .xml files for an XML model.
     * 
     * @param model
     *            the XML model
     * @param rootDir
     *            the root directory
     */
    public void createXmlFiles(MdaModel<XmlModel> model, String rootDir)
            throws NabuccoTransformationException {
        try {
            XmlFileCreator fileCreator = new XmlFileCreator(model, rootDir);
            fileCreator.createFiles();
        } catch (ModelException e) {
            throw new NabuccoTransformationException("Error creating xml files.", e);
        }
    }

    /**
     * Merges existing fragment files and creates the result file.
     * 
     * @param rootDir
     *            the root directory
     * @param componentName
     *            the name of the component
     * 
     * @throws NabuccoTransformationException
     */
    public void mergeFragments(String rootDir, String componentName)
            throws NabuccoTransformationException {
        MdaModel<XmlModel> model = NabuccoXmlFragmentMerger.getInstance().mergeFragments(rootDir,
                componentName);
        this.createXmlFiles(model, rootDir);
    }

    // Other file creations

}
