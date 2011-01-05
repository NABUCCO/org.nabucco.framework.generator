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
package org.nabucco.framework.generator.view;

import java.io.File;

import org.junit.Test;
import org.nabucco.framework.generator.AbstractNabuccoGeneratorTest;

/**
 * NabuccoSearchViewGeneratorTest
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class NabuccoSearchViewGeneratorTest extends AbstractNabuccoGeneratorTest {

    // Authorization Search Views
    private static final File SEARCH_VIEW_DIR_AUTHORIZATION_GROUP = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/search/group/");

    private static final File SEARCH_VIEW_DIR_AUTHORIZATION_USER = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/search/user/");

    private static final File SEARCH_VIEW_DIR_AUTHORIZATION_ROLE = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/search/role/");

    private static final File SEARCH_VIEW_DIR_AUTHORIZATION_PERMISSION = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/search/permission/");

    // Dynamiccode Search Views
    private static final File SEARCH_VIEW_DIR_DYNAMICCODE_CODE = new File(
            "../../org.nabucco.framework.common.dynamiccode/org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/ui/search/code/");

    private static final File SEARCH_VIEW_DIR_DYNAMICCODE_CODEGROUP = new File(
            "../../org.nabucco.framework.common.dynamiccode/org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/ui/search/codegroup/");

    // Bundle Authorization
    private static final File[] AUTHORIZATION_SEARCH_VIEW_DIRS = {
            SEARCH_VIEW_DIR_AUTHORIZATION_GROUP, SEARCH_VIEW_DIR_AUTHORIZATION_USER,
            SEARCH_VIEW_DIR_AUTHORIZATION_ROLE, SEARCH_VIEW_DIR_AUTHORIZATION_PERMISSION };

    // Bundle Dynamiccode
    private static final File[] DYNAMICCODE_SEARCH_VIEW_DIRS = { SEARCH_VIEW_DIR_DYNAMICCODE_CODE,
            SEARCH_VIEW_DIR_DYNAMICCODE_CODEGROUP };

    // Component Workflow Definition
    private static final File SEARCH_VIEW_DIR_WORKFLOW_DEFINITION_WORKFLOW = new File(
            "../org.nabucco.framework.workflow.definition/src/nbc/org/nabucco/framework/workflow/definition/ui/search/workflow");

    private static final File[][] ALL_SEARCH_VIEWS = { AUTHORIZATION_SEARCH_VIEW_DIRS,
            DYNAMICCODE_SEARCH_VIEW_DIRS };

    // All Search Views
    @Test
    public void testSearchViewAll() throws Exception {
        super.generateDir(ALL_SEARCH_VIEWS);
    }

    // All Dynamiccode Search Views
    @Test
    public void testSearchViewDynamiccodeAll() throws Exception {
        super.generateDir(DYNAMICCODE_SEARCH_VIEW_DIRS);
    }

    // All Authorization Search Views
    @Test
    public void testSearchViewAuthorizationAll() throws Exception {
        super.generateDir(AUTHORIZATION_SEARCH_VIEW_DIRS);
    }

    // Dynamiccode Search Views
    public void testSearchViewDynamiccodeCode() throws Exception {
        super.generateDir(SEARCH_VIEW_DIR_DYNAMICCODE_CODE);
    }

    public void testSearchViewDynamiccodeCodeGroup() throws Exception {
        super.generateDir(SEARCH_VIEW_DIR_DYNAMICCODE_CODEGROUP);
    }

    // Authorization Search Views
    @Test
    public void testSearchViewAuthorizationGroup() throws Exception {
        super.generateDir(SEARCH_VIEW_DIR_AUTHORIZATION_GROUP);
    }

    @Test
    public void testSearchViewAuthorizationUser() throws Exception {
        super.generateDir(SEARCH_VIEW_DIR_AUTHORIZATION_USER);
    }

    @Test
    public void testSearchViewAuthorizationRole() throws Exception {
        super.generateDir(SEARCH_VIEW_DIR_AUTHORIZATION_ROLE);
    }

    @Test
    public void testSearchViewAuthorizationPermission() throws Exception {
        super.generateDir(SEARCH_VIEW_DIR_AUTHORIZATION_PERMISSION);
    }

    @Test
    public void testSearchViewWorkflowDefinitionWorkflow() throws Exception {
        super.generateDir(SEARCH_VIEW_DIR_WORKFLOW_DEFINITION_WORKFLOW);
    }

}
