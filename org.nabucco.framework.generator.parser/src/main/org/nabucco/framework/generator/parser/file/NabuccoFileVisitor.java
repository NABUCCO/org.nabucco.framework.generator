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
package org.nabucco.framework.generator.parser.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.nabucco.framework.mda.util.file.FileTreeVisitor;

/**
 * NabuccoFileVisitor
 * <p/>
 * Traverses the file directory for .nbc files.
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
class NabuccoFileVisitor extends FileTreeVisitor implements NabuccoFileConstants {

    private List<NabuccoFile> fileList;

    /**
     * Creates a new {@link NabuccoFileVisitor} instance.
     * 
     * @param fileList
     */
    public NabuccoFileVisitor(List<NabuccoFile> fileList) {
        this.fileList = fileList;
    }

    @Override
    protected void visitFile(File file) throws IOException {
        this.fileList.add(new NabuccoFile(file));
    }

    /**
     * Getter for the nabucco file list.
     * 
     * @return Returns the fileList.
     */
    public List<NabuccoFile> getFileList() {
        return this.fileList;
    }
}
