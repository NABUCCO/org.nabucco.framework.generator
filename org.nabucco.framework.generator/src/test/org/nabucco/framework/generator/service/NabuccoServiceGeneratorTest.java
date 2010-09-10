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
package org.nabucco.framework.generator.service;

import java.io.File;

import org.junit.Test;
import org.nabucco.framework.generator.AbstractNabuccoGeneratorTest;


/**
 * NabuccoServiceGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoServiceGeneratorTest extends AbstractNabuccoGeneratorTest {

    private static final File SERVICE_DIR_DYNAMICCODE_LINK = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/facade/service/link");

    private static final File SERVICE_DIR_DYNAMICCODE_MAINTAIN = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/facade/service/maintain");

    private static final File SERVICE_DIR_DYNAMICCODE_SEARCH = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/facade/service/search");

    private static final File SERVICE_DIR_DYNAMICCODE_PRODUCE = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/facade/service/produce");

    private static final File SERVICE_DIR_AUTHORIZATION_LOGIN = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/facade/service/login");

    private static final File SERVICE_DIR_AUTHORIZATION_MAINTAIN = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/facade/service/maintain");

    private static final File SERVICE_DIR_AUTHORIZATION_SEARCH = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/facade/service/search");

    private static final File SERVICE_DIR_AUTHORIZATION_PRODUCE = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/facade/service/produce");

    private static final File SERVICE_DIR_WORKFLOW_DEFINITION_RESOLVE = new File(
            "../org.nabucco.framework.workflow.definition/src/nbc/org/nabucco/framework/workflow/definition/facade/service/resolve");

    @Test
    public void testDynamicCodeServiceLinkGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_DYNAMICCODE_LINK);
    }

    @Test
    public void testDynamicCodeServiceMaintainGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_DYNAMICCODE_MAINTAIN);
    }

    @Test
    public void testDynamicCodeServiceSearchGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_DYNAMICCODE_SEARCH);
    }

    @Test
    public void testDynamicCodeServiceProduceGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_DYNAMICCODE_PRODUCE);
    }

    @Test
    public void testAuthorizationServiceLoginGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_AUTHORIZATION_LOGIN);
    }

    @Test
    public void testAuthorizationServiceMaintainGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_AUTHORIZATION_MAINTAIN);
    }

    @Test
    public void testAuthorizationServiceSearchGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_AUTHORIZATION_SEARCH);
    }

    @Test
    public void testAuthorizationServiceProduceGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_AUTHORIZATION_PRODUCE);
    }

    @Test
    public void testWorkflowDefinitionServiceResolveGeneration() throws Exception {
        super.generateDir(SERVICE_DIR_WORKFLOW_DEFINITION_RESOLVE);
    }

}
