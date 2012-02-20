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
package org.nabucco.framework.generator.ant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.nabucco.framework.generator.NabuccoGenerator;
import org.nabucco.framework.generator.NabuccoGeneratorException;
import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.generator.compiler.NabuccoCompilerOptionType;
import org.nabucco.framework.generator.parser.file.NabuccoFile;


/**
 * NabuccoGeneratorTask
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class NabuccoGeneratorTask extends Task {

    private String logSeverity;

    private String engine;

    private String destDir;

    private String templateSource;

    private List<SourceDirectory> srcPathes = new ArrayList<SourceDirectory>();

    /**
     * Getter for the transformation type (default is 'NBC').
     * 
     * @return Returns the engine.
     */
    public String getEngine() {
        return engine;
    }

    /**
     * Setter for the transformation type (default is 'NBC').
     * 
     * @param engine
     *            The engine to set.
     */
    public void setEngine(String engine) {
        this.engine = engine;
    }

    /**
     * Getter for the logger severity.
     * 
     * @return Returns the logSeverity.
     */
    public String getLogSeverity() {
        return logSeverity;
    }

    /**
     * Setter for the logger severity.
     * 
     * @param logSeverity
     *            The logSeverity to set.
     */
    public void setLogSeverity(String logSeverity) {
        this.logSeverity = logSeverity;
    }

    /**
     * Getter for the destination directory.
     * 
     * @return Returns the destDir.
     */
    public String getDestDir() {
        return destDir;
    }

    /**
     * Setter for the destination directory.
     * 
     * @param destDir
     *            The destDir to set.
     */
    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    /**
     * Getter for the template source.
     * 
     * @return Returns the templateDir.
     */
    public String getTemplateSource() {
        return this.templateSource;
    }

    /**
     * Setter for the template source.
     * 
     * @param templateSource
     *            The templateDir to set.
     */
    public void setTemplateSource(String templateSource) {
        this.templateSource = templateSource;
    }

    /**
     * Called from the build system.
     * 
     * @return a {@link SourceDirectory} proxy object
     */
    public SourceDirectory createSrc() {
        SourceDirectory srcPath = new SourceDirectory();
        srcPathes.add(srcPath);
        return srcPath;
    }

    @Override
    public void execute() throws BuildException {

        try {
            List<NabuccoFile> fileList = this.collectSources();
            NabuccoCompilerOptions options = createCompilerOptions();

            NabuccoGenerator generator = new NabuccoGenerator(fileList, options);
            generator.generate();
        } catch (NabuccoGeneratorException nge) {
            super.log("Cannot generate NABUCCO. " + nge.getMessage());
            throw new BuildException(nge);
        } catch (IOException e) {
            super.log("Cannot generate NABUCCO. " + e.getMessage());
            throw new BuildException(e);
        }
    }

    /**
     * Collect the necessary NABUCCO files.
     * 
     * @return the list of nabucco files defined in the ANT target.
     * 
     * @throws IOException
     */
    private List<NabuccoFile> collectSources() throws IOException {

        List<NabuccoFile> fileList = new ArrayList<NabuccoFile>();

        for (SourceDirectory srcPath : srcPathes) {

            if (srcPath.getPath() != null) {
                fileList.add(new NabuccoFile(srcPath.getFile()));
            }

            if (srcPath.getFile() != null) {
                fileList.add(new NabuccoFile(srcPath.getFile()));
            }
        }

        return fileList;
    }

    /**
     * Initializes the compiler options for ANT compile target.
     * 
     * @throws NabuccoGeneratorException
     */
    private NabuccoCompilerOptions createCompilerOptions() throws NabuccoGeneratorException {

        if (engine == null || engine.isEmpty()) {
            throw new NabuccoGeneratorException("NABUCCO engine is not defined.");
        }

        if (destDir == null || destDir.isEmpty()) {
            throw new NabuccoGeneratorException("Destination directory is not defined.");
        }

        if (templateSource == null || templateSource.isEmpty()) {
            throw new NabuccoGeneratorException("Template directory is not defined.");
        }

        NabuccoCompilerOptions options = NabuccoCompilerOptions.getDefaultOptions();
        options.setOption(NabuccoCompilerOptionType.ENGINE, engine);
        options.setOption(NabuccoCompilerOptionType.GEN_JAVA, String.valueOf(Boolean.FALSE));
        options.setOption(NabuccoCompilerOptionType.GEN_XML, String.valueOf(Boolean.FALSE));
        options.setOption(NabuccoCompilerOptionType.GEN_DOC, String.valueOf(Boolean.FALSE));
        options.setOption(NabuccoCompilerOptionType.DISABLE_DOC_VALIDATION, String.valueOf(Boolean.TRUE));
        options.setOption(NabuccoCompilerOptionType.MERGE_FRAGMENTS, String.valueOf(Boolean.FALSE));
        options.setOption(NabuccoCompilerOptionType.VERBOSE, String.valueOf(Boolean.FALSE));

        options.setOption(NabuccoCompilerOptionType.TEMPLATE_DIR, templateSource);

        if (!destDir.isEmpty()) {
            options.setOption(NabuccoCompilerOptionType.OUT_DIR, destDir);
        }

        return options;
    }

}
