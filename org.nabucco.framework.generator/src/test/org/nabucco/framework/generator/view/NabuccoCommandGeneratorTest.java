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
public class NabuccoCommandGeneratorTest extends AbstractNabuccoGeneratorTest {

    private static final File COMMAND_DIR_AUTHORIZATION_USER = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/command/user");

    private static final File COMMAND_DIR_AUTHORIZATION_GROUP = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/command/group");

    private static final File COMMAND_DIR_AUTHORIZATION_PERMISSION = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/command/permission");

    private static final File COMMAND_DIR_AUTHORIZATION_ROLE = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/ui/command/role");

    private static final File COMMAND_DIR_DYNAMICCODE_CODE = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/ui/command/code");

    private static final File COMMAND_DIR_DYNAMICCODE_CODEGROUP = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/ui/command/codegroup");

    // Component Dynamic Code Bundle
    private static final File[] DYNAMICCODE_COMMAND_DIRS = { COMMAND_DIR_DYNAMICCODE_CODE,
            COMMAND_DIR_DYNAMICCODE_CODEGROUP };

    // Component Authorization Bundle
    private static final File[] AUTHORIZATION_COMMAND_DIRS = { COMMAND_DIR_AUTHORIZATION_USER,
            COMMAND_DIR_AUTHORIZATION_PERMISSION, COMMAND_DIR_AUTHORIZATION_ROLE,
            COMMAND_DIR_AUTHORIZATION_GROUP };

    // All Command Components
    private static final File[][] ALL_COMMAND_DIRS = { AUTHORIZATION_COMMAND_DIRS,
            DYNAMICCODE_COMMAND_DIRS };

    @Test
    public void testCommandAll() throws Exception {
        super.generateDir(ALL_COMMAND_DIRS);
    }

    @Test
    public void testCommandAuthorizationAll() throws Exception {
        super.generateDir(AUTHORIZATION_COMMAND_DIRS);
    }

    @Test
    public void testCommandDynamiccodeAll() throws Exception {
        super.generateDir(DYNAMICCODE_COMMAND_DIRS);
    }

    // Component Authorization
    @Test
    public void testCommandAuthorizationUserGeneration() throws Exception {
        super.generateDir(COMMAND_DIR_AUTHORIZATION_USER);
    }

    @Test
    public void testCommandAuthorizationGroupGeneration() throws Exception {
        super.generateDir(COMMAND_DIR_AUTHORIZATION_GROUP);
    }

    @Test
    public void testCommandAuthorizationPermissionGeneration() throws Exception {
        super.generateDir(COMMAND_DIR_AUTHORIZATION_PERMISSION);
    }

    @Test
    public void testCommandAuthorizationRoleGeneration() throws Exception {
        super.generateDir(COMMAND_DIR_AUTHORIZATION_ROLE);
    }

    // Component Dynamic Code
    @Test
    public void testCommandDynamiccodeCodeGeneration() throws Exception {
        super.generateDir(COMMAND_DIR_DYNAMICCODE_CODE);
    }

    @Test
    public void testCommandDynamiccodeCodeGroupGeneration() throws Exception {
        super.generateDir(COMMAND_DIR_DYNAMICCODE_CODEGROUP);
    }

}
