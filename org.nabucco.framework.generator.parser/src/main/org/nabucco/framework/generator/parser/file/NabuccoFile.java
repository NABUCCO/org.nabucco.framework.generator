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
package org.nabucco.framework.generator.parser.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.parser.model.ComponentFinder;


/**
 * NabuccoFile
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoFile implements Serializable, NabuccoFileConstants {

    private static final long serialVersionUID = 1L;

    private File sourceFile;

    private File projectDir;

    private String filePath;

    private String projectName;

    private String projectPath;

    /**
     * Creates a new {@link NabuccoFile} instance for an appropriate .nbc file.
     * 
     * @param file
     *            the IO file
     * 
     * @throws IOException
     */
    public NabuccoFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Source file does not exist [" + file + "].");
        }
        if (file.isFile() && !file.getName().endsWith(NABUCCO_SUFFIX)) {
            throw new IllegalArgumentException("Source file is no valid NABUCCO file ["
                    + file + "].");
        }

        this.sourceFile = file;

        this.initFileInformation();
    }

    /**
     * Initializes filePath and projectName of the corresponding file.
     * 
     * @throws IOException
     */
    private void initFileInformation() throws IOException {
        File componentDir = ComponentFinder.getComponentFile(this.getSourceFile());
        this.projectName = componentDir.getName();
        this.projectDir = componentDir.getCanonicalFile();
        this.projectPath = this.projectDir.getCanonicalPath();
    }

    /**
     * Getter for the source {@link File}.
     * 
     * @return Returns the sourceFile.
     */
    File getSourceFile() {
        return this.sourceFile;
    }

    /**
     * Getter for the project directory {@link File}.
     * 
     * @return the project directory.
     */
    File getProjectDir() {
        return this.projectDir;
    }

    /**
     * Getter for the source file name.
     * 
     * @return Returns the fileName.
     */
    public String getFileName() {
        return this.sourceFile.getName().replace(NABUCCO_SUFFIX, "");
    }

    /**
     * Getter for the absolute file path.
     * 
     * @return Returns the absolute filePath of the file.
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * Returns the name of the project containing the {@link NabuccoFile}.
     * 
     * @return the project name
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Returns the time, the NABUCCO file was last modified.
     * 
     * @return the modification time
     */
    public long lastModified(){
        return this.sourceFile.lastModified();
    }

    /**
     * Checks whether the file is a directory or not.
     * 
     * @return <b>true</b> if the file is a directory, <b>false</b> if not.
     */
    public boolean isDirectory() {
        return this.sourceFile.isDirectory();
    }

    /**
     * Returns a list of {@link NabuccoFile} contained by this file. Includes all .nbc files and
     * directories.
     * 
     * @return the list of files
     * 
     * @throws IOException if the
     */
    public List<NabuccoFile> listNabuccoFiles() throws IOException {
        List<NabuccoFile> fileList = new ArrayList<NabuccoFile>();

        if (this.sourceFile.isFile()) {
            return fileList;
        }

        NabuccoFileVisitor visitor = new NabuccoFileVisitor(fileList);
        visitor.visit(this.sourceFile, NabuccoFileFilter.getInstance());

        return fileList;
    }

    /**
     * Return the project path of the current file.
     * 
     * @return Returns the projectPath.
     */
    public String getProjectPath() {
        return this.projectPath;
    }

    /**
     * Returns the canonical pathname of this nabucco file.
     * 
     * @return the canonical path
     * 
     * @throws IOException
     *             if the file does not exist
     */
    public String getCanonicalPath() throws IOException{
        return this.sourceFile.getCanonicalPath();
    }

    /**
     * Returns a new FileReader for the NABUCCO file.
     * 
     * @return the file reader for this file
     * 
     * @throws FileNotFoundException
     *             if the file does not exist
     */
    public FileReader getFileReader() throws FileNotFoundException {
        return new FileReader(sourceFile);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getFileName());
        builder.append(" [");
        builder.append(this.projectName);
        builder.append("]");
        return builder.toString();
    }

}
