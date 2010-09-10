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

import java.util.Comparator;

import org.w3c.dom.Document;

import org.nabucco.framework.mda.model.xml.XmlModel;

/**
 * NabuccoXmlFragmentComparator
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoXmlFragmentComparator implements Comparator<XmlModel> {

    /**
     * Singleton instance.
     */
    private static NabuccoXmlFragmentComparator instance = new NabuccoXmlFragmentComparator();

    /**
     * Private constructor.
     */
    private NabuccoXmlFragmentComparator() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoXmlFragmentComparator instance.
     */
    public static NabuccoXmlFragmentComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(XmlModel x1, XmlModel x2) {

        if (x1 == null || x2 == null) {
            throw new IllegalArgumentException("Xml model must be defined!");
        }

        if (x1.getDocuments().size() != 1 || x2.getDocuments().size() != 1) {
            throw new IllegalArgumentException("Xml model must contain exactly one document!");
        }

        Document d1 = x1.getDocuments().get(0).getDocument();
        Document d2 = x2.getDocuments().get(0).getDocument();

        if (d1 == null || d2 == null) {
            throw new IllegalArgumentException("Xml document must be defined!");
        }

        int result = d1.getDocumentElement().getAttribute("type").compareTo(
                d2.getDocumentElement().getAttribute("type"));
        
        if (result != 0){
            return result;
        }

        return d1.getDocumentElement().getAttribute("order").compareTo(
                d2.getDocumentElement().getAttribute("order"));
    }

}
