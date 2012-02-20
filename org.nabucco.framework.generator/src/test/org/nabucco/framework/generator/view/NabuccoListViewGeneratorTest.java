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
package org.nabucco.framework.generator.view;

import java.io.File;

import org.junit.Test;
import org.nabucco.framework.generator.AbstractNabuccoGeneratorTest;

/**
 * NabuccoEditViewGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoListViewGeneratorTest extends AbstractNabuccoGeneratorTest {

    private static final File LIST_VIEW_DIR_AUTHORIZATION_GROUP = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/list/group");

    private static final File LIST_VIEW_DIR_AUTHORIZATION_USER = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/list/user");

    private static final File LIST_VIEW_DIR_AUTHORIZATION_PERMISSION = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/list/permission");

    private static final File LIST_VIEW_DIR_AUTHORIZATION_ROLE = new File(
            "../../org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/list/role");

    private static final File LIST_VIEW_DIR_DYNAMICCODE_CODEGROUP = new File(
            "../../org.nabucco.framework.common.dynamiccode/org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/ui/list/codegroup");

    private static final File LIST_VIEW_DIR_DYNAMICCODE_CODE = new File(
            "../../org.nabucco.framework.common.dynamiccode/org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/ui/list/code");

    @Test
    public void testListViewGeneration() throws Exception {
        super.generateDir(LIST_VIEW_DIR_AUTHORIZATION_GROUP);
        super.generateDir(LIST_VIEW_DIR_AUTHORIZATION_USER);
        super.generateDir(LIST_VIEW_DIR_AUTHORIZATION_PERMISSION);
        super.generateDir(LIST_VIEW_DIR_AUTHORIZATION_ROLE);
        super.generateDir(LIST_VIEW_DIR_DYNAMICCODE_CODE);
        super.generateDir(LIST_VIEW_DIR_DYNAMICCODE_CODEGROUP);
    }

    @Test
    public void testListViewGenerationDynamiccode() throws Exception {
        super.generateDir(LIST_VIEW_DIR_DYNAMICCODE_CODE);
        super.generateDir(LIST_VIEW_DIR_DYNAMICCODE_CODEGROUP);
    }

    @Test
    public void testListViewGenerationAuthorizationGroup() throws Exception {
        super.generateDir(LIST_VIEW_DIR_AUTHORIZATION_GROUP);
    }

    @Test
    public void testListViewGenerationAuthorizationPermission() throws Exception {
        super.generateDir(LIST_VIEW_DIR_AUTHORIZATION_PERMISSION);
    }

    @Test
    public void testListViewGenerationAuthorizationRole() throws Exception {
        super.generateDir(LIST_VIEW_DIR_AUTHORIZATION_ROLE);
    }

    @Test
    public void testListViewGenerationAuthorizationUser() throws Exception {
        super.generateDir(LIST_VIEW_DIR_AUTHORIZATION_USER);
    }

}
