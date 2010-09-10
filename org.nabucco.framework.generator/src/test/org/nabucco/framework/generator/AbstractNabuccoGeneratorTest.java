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
package org.nabucco.framework.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nabucco.framework.generator.NabuccoGenerator;
import org.nabucco.framework.generator.NabuccoGeneratorException;
import org.nabucco.framework.generator.compiler.NabuccoCompilerOptions;
import org.nabucco.framework.generator.parser.file.NabuccoFile;


/**
 * AbstractNabuccoGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class AbstractNabuccoGeneratorTest {

    protected void generateFile(File file) throws IOException, NabuccoGeneratorException {
        NabuccoFile nabuccoFile = new NabuccoFile(file);
        NabuccoGenerator generator = new NabuccoGenerator(nabuccoFile);
        generator.generate();
    }

    protected void generateFile(List<File> fileList) throws IOException, NabuccoGeneratorException {
        List<NabuccoFile> nabuccoFileList = new ArrayList<NabuccoFile>();
        for (File file : fileList) {
            nabuccoFileList.add(new NabuccoFile(file));
        }
        NabuccoGenerator generator = new NabuccoGenerator(nabuccoFileList);
        generator.generate();
    }

    protected void generateSkmFile(File file) throws IOException, NabuccoGeneratorException {
        NabuccoFile nabuccoFile = new NabuccoFile(file);

        NabuccoCompilerOptions options = new NabuccoCompilerOptions();
        options.setOption(NabuccoCompilerOptions.ENGINE, "SKM");

        NabuccoGenerator generator = new NabuccoGenerator(nabuccoFile, options);

        generator.generate();
    }

    protected void generateDir(File dir) throws IOException, NabuccoGeneratorException {
        generateFile(dir);
    }

    protected void generateDir(List<File> dirList) throws NabuccoGeneratorException, IOException {
        generateFile(dirList);
    }

    protected void generateDir(File[]... files) throws NabuccoGeneratorException, IOException {
        for (File[] current : files) {
            generateDir(current);
        }
    }

}
