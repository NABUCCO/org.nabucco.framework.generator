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
package org.nabucco.framework.generator.component;

import java.io.File;

import org.junit.Test;
import org.nabucco.framework.generator.NabuccoGenerator;
import org.nabucco.framework.generator.parser.file.NabuccoFile;


/**
 * NabuccoComponentGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoComponentGeneratorTest {

    private static final File BASE_COMPONENT = new File(
            "../org.nabucco.framework.base/src/nbc/org/nabucco/framework/base/");
    
    private static final File AUTHORIZATION_COMPONENT = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/");

    private static final File DYNAMICCODE_COMPONENT = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/");

    private static final File SETUP_COMPONENT = new File(
            "../org.nabucco.framework.common.setup/src/nbc/org/nabucco/framework/common/setup/");

    private static final File EXPORTING_COMPONENT = new File(
            "../org.nabucco.framework.exporting/src/nbc/org/nabucco/framework/exporting/");

    private static final File WORKFLOW_DEFINITION_COMPONENT = new File(
            "../org.nabucco.framework.workflow.definition/src/nbc/org/nabucco/framework/workflow/definition/");
    
    private static final File WORKFLOW_ENGINE_COMPONENT = new File(
            "../org.nabucco.framework.workflow.engine/src/nbc/org/nabucco/framework/workflow/engine/");

    private static final File WORKFLOW_INSTANCE_COMPONENT = new File(
            "../org.nabucco.framework.workflow.instance/src/nbc/org/nabucco/framework/workflow/instance/");

    private static final File SCRIPT_COMPONENT = new File(
            "../org.nabucco.framework.support.scripting/src/nbc/org/nabucco/framework/support/scripting/");

    private static final File TEST_SCRIPT_COMPONENT = new File(
            "../org.nabucco.framework.testautomation.script/src/nbc/org/nabucco/framework/testautomation/script/");
    
    private static final File TEST_CONFIG_COMPONENT = new File(
            "../org.nabucco.framework.testautomation.config/src/nbc/org/nabucco/framework/testautomation/config/");

    private static final File SKM_BASE_COMPONENT = new File(
            "../org.nabucco.framework.skm.base/src/nbc/org/nabucco/framework/skm/base/");

    private static final File SKM_PERSON_COMPONENT = new File(
            "../org.nabucco.framework.skm.person/src/nbc/org/nabucco/framework/skm/person/");
    
    @Test
    public void generateBaseComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(BASE_COMPONENT);
        
        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateAuthorizationComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(AUTHORIZATION_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateDynamicCodeComponent() throws Exception {
        NabuccoFile file = new NabuccoFile(DYNAMICCODE_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(file);
        generator.generate();
    }

    @Test
    public void generateSetupComponent() throws Exception {
        NabuccoFile file = new NabuccoFile(SETUP_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(file);
        generator.generate();
    }

    @Test
    public void generateExportingComponent() throws Exception {
        NabuccoFile file = new NabuccoFile(EXPORTING_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(file);
        generator.generate();
    }

    @Test
    public void generateWorkflowDefinitionComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(WORKFLOW_DEFINITION_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateWorkflowEngineComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(WORKFLOW_ENGINE_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }
    
    @Test
    public void generateWorkflowInstanceComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(WORKFLOW_INSTANCE_COMPONENT);
        
        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateScriptComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(SCRIPT_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    public void generateTestautomationScriptComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(TEST_SCRIPT_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    public void generateTestautomationConfigComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(TEST_CONFIG_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateSkmBaseComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(SKM_BASE_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateSkmPersonComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(SKM_PERSON_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

}
