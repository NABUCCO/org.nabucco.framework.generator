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
package org.nabucco.framework.generator.parser.model;

import java.io.File;

/**
 * ComponentLocator
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class ComponentFinder {

    private static final String SRC_NBC = "src/nbc";

    /**
     * Evaluates the NABUCCO component name for any given NBC file.
     * 
     * @param path
     *            a path to a File
     *            
     * @return the NABUCCO component name
     */
    public static String getComponentName(String path) {
        File f = new File(path);
        if (f.exists() && f.canRead()) {
            return getComponentName(f);
        }
        return "";
    }

    /**
     * Evaluates the NABUCCO component name for any given NBC file.
     * 
     * @param file
     *            a file instance
     *            
     * @return the NABUCCO component name
     */
    public static String getComponentName(File file) {
        if (file != null) {
            File componentDir = getComponentFile(file);
            return componentDir == null ? "" : componentDir.getName();
        }
        return "";
    }

    /**
     * Evaluates the NABUCCO component name for any given NBC file.
     * 
     * @param file
     *            a file instance
     *            
     * @return the NABUCCO component name
     */
    public static File getComponentFile(File file) {
        if (file != null) {
            if (file.exists() && file.isFile()) {
                // assure directory && assumption that if there is a file even in the root
                // than that file should have the parent , the root itself
                return getComponentFile(file.getParentFile());
            }
            if (hasForSrcNbcConstruct(file)) {
                return file;
            }
            if (file.getParentFile() != null) {
                return getComponentFile(file.getParentFile());
            }
        }
        return null;
    }

    /**
     * Checks whether a file contains the src/nbc folder.
     * 
     * @param file
     *            the file to check
     * 
     * @return <b>true</b> if the file contains a src/nbc folder, <b>false</b> if not
     */
    private static boolean hasForSrcNbcConstruct(File file) {
        File checkFile = new File(file, SRC_NBC);
        if (checkFile.exists() && checkFile.isDirectory()) {
            return true;
        }
        return false;
    }

}
