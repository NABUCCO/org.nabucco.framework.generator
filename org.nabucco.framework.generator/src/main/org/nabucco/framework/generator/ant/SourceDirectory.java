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
package org.nabucco.framework.generator.ant;

import java.io.File;

/**
 * SourcePath
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class SourceDirectory {

    private File path;

    private File file;

    private String src;

    /**
     * Getter for the file.
     * 
     * @return Returns the file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Setter for the file.
     * 
     * @param file
     *            The file to set.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Getter for the path
     * 
     * @return Returns the path.
     */
    public File getPath() {
        return path;
    }

    /**
     * Setter for the path
     * 
     * @param path
     *            The path to set.
     */
    public void setPath(File path) {
        this.path = path;
    }

    /**
     * Getter for the source.
     * 
     * @return Returns the src.
     */
    public String getSrc() {
        return src;
    }

    /**
     * Setter for the source.
     * 
     * @param src
     *            The src to set.
     */
    public void setSrc(String src) {
        this.src = src;
    }

}
