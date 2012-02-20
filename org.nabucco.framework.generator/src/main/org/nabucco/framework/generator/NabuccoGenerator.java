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
package org.nabucco.framework.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.compiler.NabuccoCompilationTarget;
import org.nabucco.framework.generator.compiler.NabuccoCompiler;
import org.nabucco.framework.generator.compiler.NabuccoCompilerException;
import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.generator.compiler.NabuccoCompilerOptionType;
import org.nabucco.framework.generator.parser.file.NabuccoFile;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelLoader;
import org.nabucco.framework.mda.logger.MdaLogger;
import org.nabucco.framework.mda.logger.MdaLoggingFactory;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.ModelException;

/**
 * The NabuccoGenerator parses an appropriate NABUCCO nabuccoFileList nabuccoFileList, compiles and
 * validates the parsed model and generates target models out of it.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class NabuccoGenerator {

    private boolean isRun = false;

    private NabuccoCompilerOptions options;

    private String projectName;

    private String projectPath;

    private List<NabuccoFile> nabuccoFileList = new ArrayList<NabuccoFile>();

    private static MdaLogger logger = MdaLoggingFactory.getInstance().getLogger(NabuccoGenerator.class);

    /**
     * Creates a new {@link NabuccoGenerator} instance for an appropriate .nbc file.
     * 
     * @param nabuccoFile
     *            the .nbc file
     */
    public NabuccoGenerator(NabuccoFile nabuccoFile) {
        if (nabuccoFile == null) {
            throw new IllegalArgumentException("NABUCCO file is not valid.");
        }

        this.nabuccoFileList.add(nabuccoFile);
        this.initCompilerOptions(null);
    }

    /**
     * Creates a new {@link NabuccoGenerator} instance for an appropriate {@link NabuccoFile} with
     * generator options.
     * 
     * @param nabuccoFile
     *            the NABUCCO file
     * @param properties
     *            .properties nabuccoFileList containing NABUCCO compiler options
     */
    public NabuccoGenerator(NabuccoFile nabuccoFile, File properties) {
        if (nabuccoFile == null) {
            throw new IllegalArgumentException("NABUCCO file is not valid.");
        }

        this.nabuccoFileList.add(nabuccoFile);
        this.initCompilerOptions(properties);
    }

    /**
     * Creates a new {@link NabuccoGenerator} instance for an appropriate {@link NabuccoFile} with
     * generator options.
     * 
     * @param nabuccoFile
     *            the NABUCCO file
     * @param options
     *            the NABUCCO compiler options
     */
    public NabuccoGenerator(NabuccoFile nabuccoFile, NabuccoCompilerOptions options) {
        if (nabuccoFile == null) {
            throw new IllegalArgumentException("NABUCCO file is not valid.");
        }
        if (options == null) {
            initCompilerOptions(null);
        }

        this.nabuccoFileList.add(nabuccoFile);
        this.options = options;
    }

    /**
     * Creates a new {@link NabuccoGenerator} instance for an appropriate {@link NabuccoFile} with
     * generator options.
     * 
     * @param nabuccoFileList
     *            the NABUCCO nabuccoFileList
     */
    public NabuccoGenerator(List<NabuccoFile> nabuccoFileList) {
        if (nabuccoFileList == null) {
            throw new IllegalArgumentException("NABUCCO file list is not valid.");
        }
        if (options == null) {
            initCompilerOptions(null);
        }

        this.nabuccoFileList.addAll(nabuccoFileList);
        this.initCompilerOptions(null);
    }

    /**
     * Creates a new {@link NabuccoGenerator} instance for an appropriate {@link NabuccoFile} with
     * generator options.
     * 
     * @param nabuccoFileList
     *            the NABUCCO nabuccoFileList
     * @param options
     *            the NABUCCO compiler options
     */
    public NabuccoGenerator(List<NabuccoFile> nabuccoFileList, NabuccoCompilerOptions options) {
        if (nabuccoFileList == null) {
            throw new IllegalArgumentException("NABUCCO file list is not valid.");
        }
        if (options == null) {
            initCompilerOptions(null);
        } else {
            this.options = new NabuccoCompilerOptions(options);
        }

        this.nabuccoFileList.addAll(nabuccoFileList);
    }

    /**
     * Initializes the compiler options for the given .properties file.
     * 
     * @param propertiesFile
     *            the properties file
     */
    private void initCompilerOptions(File propertiesFile) {

        if (propertiesFile == null) {
            logger.debug("No compiler properties defined. Using default NABUCCO properties.");
            this.options = NabuccoCompilerOptions.getDefaultOptions();
        } else {
            try {
                this.options = new NabuccoCompilerOptions(propertiesFile);
            } catch (IOException e) {
                logger.warning("Compiler properties [", propertiesFile.getName() + "] not valid. ", e.getMessage(),
                        " Using default NABUCCO properties.");
                this.options = NabuccoCompilerOptions.getDefaultOptions();
            }
        }
    }

    /**
     * Executes the compilation process and generates target models out of it.
     * 
     * @throws NabuccoGeneratorException
     */
    public void generate() throws NabuccoGeneratorException {

        this.printHeading();

        this.validateFileList();

        logger.info("Start generating NABUCCO:");
        logger.info("Compiling in project '", this.projectName, "'.");

        if (this.nabuccoFileList.isEmpty()) {
            logger.warning("No NABUCCO files selected for generation.");
            return;
        }

        try {
            this.generateFileList();
            this.isRun = true;

        } catch (ModelException me) {
            logger.error(me, "Error loading NABUCCO model.");
            throw new NabuccoGeneratorException("Error loading NABUCCO model: " + nabuccoFileList, me);
        } catch (NabuccoCompilerException ce) {
            logger.error(ce, "Error compiling NABUCCO model.");
            throw new NabuccoGeneratorException("Error compiling NABUCCO model: " + nabuccoFileList, ce);
        } catch (Exception e) {
            logger.error(e, "Error generating NABUCCO model.");
            throw new NabuccoGeneratorException("Error generating NABUCCO model: " + nabuccoFileList, e);
        }
    }

    /**
     * Print the NABUCCO Generator Heading.
     */
    private void printHeading() {
        StringBuilder heading = new StringBuilder();

        heading.append("\n\n");
        heading.append(" ###############################################################################\n");
        heading.append(" #                                                                             #\n");
        heading.append(" #    NABUCCO Framework Generator - (2009-2011) PRODYNA AG, Germany.           #\n");
        heading.append(" #                                                                             #\n");
        heading.append(" #    http://nabuccosource.org/          https://github.com/nabucco/           #\n");
        heading.append(" #                                                                             #\n");
        heading.append(" ###############################################################################\n");

        logger.info(heading.toString());
    }

    /**
     * Validates the NABUCCO file list and extract project information.
     * 
     * @throws NabuccoGeneratorException
     */
    private void validateFileList() throws NabuccoGeneratorException {

        if (this.isRun) {
            throw new NabuccoGeneratorException("Generator must not be invoked multiple times.");
        }

        if (this.nabuccoFileList.size() < 1) {
            logger.warning("No NABUCCO files selected.");
            return;
        }

        NabuccoFile nabuccoFile = this.nabuccoFileList.get(0);
        this.projectName = nabuccoFile.getProjectName();
        this.projectPath = nabuccoFile.getProjectPath();
    }

    /**
     * Generates a NABUCCO directory.
     * 
     * @throws Exception
     */
    private void generateFileList() throws Exception {

        List<NabuccoFile> fileList = new ArrayList<NabuccoFile>();

        for (NabuccoFile nabuccoFile : this.nabuccoFileList) {
            if (nabuccoFile.isDirectory()) {
                fileList.addAll(nabuccoFile.listNabuccoFiles());
            } else {
                fileList.add(nabuccoFile);
            }
        }
        
        if (fileList.isEmpty()) {
            logger.warning("No NABUCCO files selected for generation.");
            return;
        }

        int size = fileList.size();
        String target = this.options.getOption(NabuccoCompilerOptionType.OUT_DIR);

        if (size > 1) {
            logger.info("Compiling ", String.valueOf(size), " NABUCCO files to '", target, "'.");
        } else {
            logger.info("Compiling ", String.valueOf(size), " NABUCCO file to '", target, "'.");
        }

        List<MdaModel<NabuccoModel>> modelList = new ArrayList<MdaModel<NabuccoModel>>();

        for (NabuccoFile nabuccoFile : fileList) {
            modelList.add(this.loadModel(nabuccoFile));
        }

        long before = System.currentTimeMillis();
        this.compileModel(modelList);
        long after = System.currentTimeMillis();

        logger.info("Generation finished successfully after " + ((after - before) / 1000.0) + "s.");
    }

    /**
     * Loads the NABUCCO model from a NABUCCO nabuccoFileList.
     * 
     * @param file
     *            the nabucco file to load
     * 
     * @return the loaded model
     * 
     * @throws ModelException
     */
    private MdaModel<NabuccoModel> loadModel(NabuccoFile file) throws ModelException {

        String targetDirectory = this.options.getOption(NabuccoCompilerOptionType.OUT_DIR);

        NabuccoModelLoader modelLoader = new NabuccoModelLoader(targetDirectory);
        NabuccoModel nabuccoModel = modelLoader.loadModel(file);

        return new MdaModel<NabuccoModel>(nabuccoModel);
    }

    /**
     * Compiles a NABUCCO model and generates code.
     * 
     * @param modelList
     *            the list of NABUCCO models
     * 
     * @throws NabuccoCompilerException
     */
    private void compileModel(List<MdaModel<NabuccoModel>> modelList) throws NabuccoCompilerException,
            NabuccoGeneratorException {

        String component = this.projectName;
        for (NabuccoFile nabuccoFile : nabuccoFileList) {
            if (component != null && !component.equals(nabuccoFile.getProjectName())) {
                throw new NabuccoGeneratorException("Cannot generate over multiple components.");
            }
        }

        StringBuilder rootDir = new StringBuilder();
        rootDir.append(this.projectPath);
        rootDir.append(File.separatorChar);
        rootDir.append("..");

        NabuccoCompilationTarget target = new NabuccoCompilationTarget(modelList, rootDir.toString(), component);

        NabuccoCompiler compiler = new NabuccoCompiler(this.options);
        compiler.compile(target);
    }

}
