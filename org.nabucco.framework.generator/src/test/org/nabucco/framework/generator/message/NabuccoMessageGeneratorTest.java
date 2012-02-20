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
package org.nabucco.framework.generator.message;

import java.io.File;

import org.junit.Test;
import org.nabucco.framework.generator.AbstractNabuccoGeneratorTest;

/**
 * NabuccoMessageGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoMessageGeneratorTest extends AbstractNabuccoGeneratorTest {

    private static final File BASE_MSG_DIR = new File(
            "../../org.nabucco.framework.base/org.nabucco.framework.base/src/nbc/org/nabucco/framework/base/facade/message");

    private static final File DYNAMICCODE_MSG_DIR = new File(
            "../../org.nabucco.framework.common.dynamiccode/org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/facade/message");

    private static final File AUTHORIZATION_MSG_DIR = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/facade/message");

    private static final File ADDRESS_MSG_DIR = new File(
            "../../org.nabucco.business.address/org.nabucco.business.address/src/nbc/org/nabucco/business/address/facade/message");

    private static final File WORKFLOW_MSG_DIR = new File(
            "../../org.nabucco.framework.workflow/org.nabucco.framework.workflow/src/nbc/org/nabucco/framework/workflow/facade/message");

    @Test
    public void testBaseMessageGeneration() throws Exception {
        super.generateDir(BASE_MSG_DIR);
    }

    @Test
    public void testDynamicCodeMessageGeneration() throws Exception {
        super.generateDir(DYNAMICCODE_MSG_DIR);
    }

    @Test
    public void testAuthorizationMessageGeneration() throws Exception {
        super.generateDir(AUTHORIZATION_MSG_DIR);
    }

    @Test
    public void testAddressMessageGeneration() throws Exception {
        super.generateDir(ADDRESS_MSG_DIR);
    }

    @Test
    public void testWorkflowMessageGeneration() throws Exception {
        super.generateDir(WORKFLOW_MSG_DIR);
    }

}
