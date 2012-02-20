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

import java.io.File;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * AntTaskTest
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class AntTaskTest {

    private File buildFile;

    private DefaultLogger consoleLogger;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        buildFile = new File("conf/test/data/build.xml");
        consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
    }

    @Test
    public void testAntTarget() throws Exception {
        Project p = new Project();
        p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        p.setUserProperty("basedir", buildFile.getParentFile().getAbsolutePath());
        p.init();
        p.fireBuildStarted();
        p.addBuildListener(consoleLogger);
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        p.addReference("ant.projectHelper", helper);
        helper.parse(p, buildFile);
        p.executeTarget(p.getDefaultTarget());
        p.fireBuildFinished(null);
    }

}
