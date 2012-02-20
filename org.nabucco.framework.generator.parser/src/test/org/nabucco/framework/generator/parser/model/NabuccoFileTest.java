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
package org.nabucco.framework.generator.parser.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.nabucco.framework.generator.parser.file.NabuccoFile;


/**
 * NabuccoFileTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoFileTest {

    private static final String PROJECT_TOKEN = "org.nabucco.framework";

    private static final String NBC_FILE_NAME_TOKEN = "Description";

    private static final String TEST_PROJECT_PATH = "conf/sample/" + PROJECT_TOKEN + "/";

    private static final String TEST_SRC_PATH = TEST_PROJECT_PATH + "src/";

    private static final String TEST_SRC_PKG = TEST_SRC_PATH
            + "nbc/org/nabucco/framework/";

    private static final String TEST_FILE_PATH = TEST_SRC_PKG + NBC_FILE_NAME_TOKEN + ".nbc";

    @Test
    public void testWithNabuccoFile() throws Exception {
        NabuccoFile nabuccoFile = createNabuccoFile(TEST_FILE_PATH);

        Assert.assertEquals(NBC_FILE_NAME_TOKEN, nabuccoFile.getFileName());
        Assert.assertEquals(PROJECT_TOKEN, nabuccoFile.getProjectName());
    }

    @Test
    public void testWithNabuccoPackage() throws Exception {
        NabuccoFile nabuccoFile = createNabuccoFile(TEST_SRC_PKG);
        Assert.assertEquals(PROJECT_TOKEN, nabuccoFile.getProjectName());
    }

    @Test
    public void testWithSrcDir() throws Exception {
        NabuccoFile nabuccoFile = createNabuccoFile(TEST_SRC_PATH);
        Assert.assertEquals(PROJECT_TOKEN, nabuccoFile.getProjectName());
    }

    @Test
    public void testWithProjectDir() throws Exception {
        NabuccoFile nabuccoFile = createNabuccoFile(TEST_PROJECT_PATH);
        Assert.assertEquals(PROJECT_TOKEN, nabuccoFile.getProjectName());
    }

    private NabuccoFile createNabuccoFile(String path) throws Exception {
        File file = new File(path);
        Assert.assertTrue("NABUCCO File does not exist.", file.exists());

        return new NabuccoFile(file);
    }

}
