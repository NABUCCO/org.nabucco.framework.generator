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
package org.nabucco.framework.generator.component;

import java.io.File;

import org.junit.Test;
import org.nabucco.framework.generator.AbstractNabuccoGeneratorTest;
import org.nabucco.framework.generator.NabuccoGenerator;
import org.nabucco.framework.generator.parser.file.NabuccoFile;

/**
 * NabuccoComponentGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoComponentGeneratorTest extends AbstractNabuccoGeneratorTest {

    private static final File TEMP_COMPONENT = new File(
            "../../org.nabucco.business.scheduling/org.nabucco.business.scheduling/src/nbc/org/nabucco/business/scheduling/");

    // Framework Components

    private static final File BASE_COMPONENT = new File(
            "../../org.nabucco.framework.base/org.nabucco.framework.base/src/nbc/org/nabucco/framework/base/");

    private static final File AUTHORIZATION_COMPONENT = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/");

    private static final File DYNAMICCODE_COMPONENT = new File(
            "../../org.nabucco.framework.common.dynamiccode/org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/");

    private static final File SEARCH_COMPONENT = new File(
            "../../org.nabucco.framework.search/org.nabucco.framework.search/src/nbc/org/nabucco/framework/search/");

    private static final File SETUP_COMPONENT = new File(
            "../../org.nabucco.framework.setup/org.nabucco.framework.setup/src/nbc/org/nabucco/framework/setup/");

    private static final File EXPORTING_COMPONENT = new File(
            "../../org.nabucco.framework.exporting/org.nabucco.framework.exporting/src/nbc/org/nabucco/framework/exporting/");

    private static final File IMPORTING_COMPONENT = new File(
            "../../org.nabucco.framework.importing/org.nabucco.framework.importing/src/nbc/org/nabucco/framework/importing/");

    private static final File MONITOR_COMPONENT = new File(
            "../../org.nabucco.framework.monitor/org.nabucco.framework.monitor/src/nbc/org/nabucco/framework/monitor/");

    private static final File WORKFLOW_COMPONENT = new File(
            "../../org.nabucco.framework.workflow/org.nabucco.framework.workflow/src/nbc/org/nabucco/framework/workflow/");

    private static final File SCRIPT_COMPONENT = new File(
            "../../org.nabucco.framework.support.scripting/org.nabucco.framework.support.scripting/src/nbc/org/nabucco/framework/support/scripting/");

    // Business Components

    private static final File ORGANIZATION_COMPONENT = new File(
            "../../org.nabucco.business.organization/org.nabucco.business.organization/src/nbc/org/nabucco/business/organization/");

    private static final File PERSON_COMPONENT = new File(
            "../../org.nabucco.business.person/org.nabucco.business.person/src/nbc/org/nabucco/business/person/");

    private static final File PROVISION_COMPONENT = new File(
            "../../org.nabucco.business.provision/org.nabucco.business.provision/src/nbc/org/nabucco/business/provision/");

    private static final File PROJECT_COMPONENT = new File(
            "../../org.nabucco.business.project/org.nabucco.business.project/src/nbc/org/nabucco/business/project/");

    private static final File SCHEDULING_COMPONENT = new File(
            "../../org.nabucco.business.scheduling/org.nabucco.business.scheduling/src/nbc/org/nabucco/business/scheduling/");

    // Adapters

    private static final File LUCENE_ADAPTER = new File(
            "../../org.nabucco.adapter.lucene/org.nabucco.adapter.lucene/src/nbc/org/nabucco/adapter/lucene/");

    public void generateTemp() throws Exception {
        NabuccoFile dir = new NabuccoFile(TEMP_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

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
    public void generateScriptComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(SCRIPT_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateSearchComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(SEARCH_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
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
    public void generateImportingComponent() throws Exception {
        NabuccoFile file = new NabuccoFile(IMPORTING_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(file);
        generator.generate();
    }

    @Test
    public void generateMonitorComponent() throws Exception {
        NabuccoFile file = new NabuccoFile(MONITOR_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(file);
        generator.generate();
    }

    @Test
    public void generateWorkflowComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(WORKFLOW_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateOrganizationComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(ORGANIZATION_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generatePersonComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(PERSON_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateProvisionComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(PROVISION_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateProjectComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(PROJECT_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateSchedulingComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(SCHEDULING_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateLuceneAdapter() throws Exception {
        NabuccoFile dir = new NabuccoFile(LUCENE_ADAPTER);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

}
