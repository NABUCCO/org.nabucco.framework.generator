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
 * NabuccoEditViewGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoEditViewGeneratorTest extends AbstractNabuccoGeneratorTest {

    // Component Authorization
    private static final File EDIT_VIEW_DIR_AUTHORIZATION_GROUP = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/edit/group");

    private static final File EDIT_VIEW_DIR_AUTHORIZATION_USER = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/edit/user");

    private static final File EDIT_VIEW_DIR_AUTHORIZATION_PERMISSION = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/edit/permission");

    private static final File EDIT_VIEW_DIR_AUTHORIZATION_ROLE = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/edit/role");

    // Component Dynamic Code
    private static final File EDIT_VIEW_DIR_DYNAMICCODE_CODE = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/ui/edit/code");

    // Component Workflow Definition
    private static final File EDIT_VIEW_DIR_WORKFLOW_DEFINITION_CONDITION = new File(
            "../org.nabucco.framework.workflow.definition/src/nbc/org/nabucco/framework/workflow/definition/ui/edit/condition");

    private static final File EDIT_VIEW_DIR_WORKFLOW_DEFINITION_STATE = new File(
            "../org.nabucco.framework.workflow.definition/src/nbc/org/nabucco/framework/workflow/definition/ui/edit/state");

    private static final File EDIT_VIEW_DIR_WORKFLOW_DEFINITION_EFFECT = new File(
            "../org.nabucco.framework.workflow.definition/src/nbc/org/nabucco/framework/workflow/definition/ui/edit/effect");

    private static final File EDIT_VIEW_DIR_WORKFLOW_DEFINITION_TRANSITION = new File(
            "../org.nabucco.framework.workflow.definition/src/nbc/org/nabucco/framework/workflow/definition/ui/edit/transition");

    private static final File EDIT_VIEW_DIR_WORKFLOW_DEFINITION_WORKFLOW = new File(
            "../org.nabucco.framework.workflow.definition/src/nbc/org/nabucco/framework/workflow/definition/ui/edit/workflow");

    // Component Testautomation
    private static final File EDIT_VIEW_DIR_TESTAUTOMATION = new File(
            "../org.nabucco.framework.testautomation/src/nbc/org/nabucco/framework/testautomation/ui/edit/script");

    private static final File EDIT_VIEW_DIR_DYNAMICCODE_CODEGROUP = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/ui/edit/codegroup");

    // Authorizazion Bundle
    private static final File[] AUTHORIZATION_EDIT_VIEW_DIRS = { EDIT_VIEW_DIR_AUTHORIZATION_GROUP,
            EDIT_VIEW_DIR_AUTHORIZATION_USER, EDIT_VIEW_DIR_AUTHORIZATION_PERMISSION,
            EDIT_VIEW_DIR_AUTHORIZATION_ROLE };

    // Dynamiccode Bundle
    private static final File[] DYNAMICCODE_EDIT_VIEW_DIRS = { EDIT_VIEW_DIR_DYNAMICCODE_CODE,
            EDIT_VIEW_DIR_DYNAMICCODE_CODEGROUP };

    // WokrflowDefinition Bundle
    private static final File[] WORKFLOW_DEFINITION_EDIT_VIEW_DIRS = {
            EDIT_VIEW_DIR_WORKFLOW_DEFINITION_CONDITION, EDIT_VIEW_DIR_WORKFLOW_DEFINITION_STATE,
            EDIT_VIEW_DIR_WORKFLOW_DEFINITION_TRANSITION,
            EDIT_VIEW_DIR_WORKFLOW_DEFINITION_WORKFLOW };

    private static final File[][] ALL_EDIT_VIEWS = { AUTHORIZATION_EDIT_VIEW_DIRS,
            DYNAMICCODE_EDIT_VIEW_DIRS };

    @Test
    public void testEditViewAll() throws Exception {
        super.generateDir(ALL_EDIT_VIEWS);
    }

    @Test
    public void testEditViewDynamiccodeAll() throws Exception {
        super.generateDir(DYNAMICCODE_EDIT_VIEW_DIRS);
    }

    @Test
    public void testEditViewDynamiccodeCode() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_DYNAMICCODE_CODE);
    }

    @Test
    public void testEditViewDynamiccodeCodeGroup() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_DYNAMICCODE_CODEGROUP);
    }

    @Test
    public void testEditViewAuthorizationAll() throws Exception {
        super.generateDir(AUTHORIZATION_EDIT_VIEW_DIRS);
    }

    @Test
    public void testEditViewAuthorizationGroup() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_AUTHORIZATION_GROUP);
    }

    @Test
    public void testEditViewAuthorizationUser() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_AUTHORIZATION_USER);
    }

    @Test
    public void testEditViewAuthorizationRole() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_AUTHORIZATION_ROLE);
    }

    @Test
    public void testEditViewAuthorizationPermission() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_AUTHORIZATION_PERMISSION);
    }

    @Test
    public void testEditViewTestautomationScript() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_TESTAUTOMATION);
    }

    @Test
    public void testEditViewWorkflowDefinitionAll() throws Exception {
        super.generateDir(WORKFLOW_DEFINITION_EDIT_VIEW_DIRS);
    }

    @Test
    public void testEditViewWorkflowDefinitionState() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_WORKFLOW_DEFINITION_STATE);
    }

    @Test
    public void testEditViewWorkflowDefinitionEffect() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_WORKFLOW_DEFINITION_EFFECT);
    }

    @Test
    public void testEditViewWorkflowDefinitionTransition() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_WORKFLOW_DEFINITION_TRANSITION);
    }

    @Test
    public void testEditViewWorkflowDefinitionCondition() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_WORKFLOW_DEFINITION_CONDITION);
    }

    @Test
    public void testEditViewWorkflowDefinitionWorkflow() throws Exception {
        super.generateDir(EDIT_VIEW_DIR_WORKFLOW_DEFINITION_WORKFLOW);
    }
}
