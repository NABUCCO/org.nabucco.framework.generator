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
package org.nabucco.framework.generator.exception;

import java.io.File;

import org.junit.Test;
import org.nabucco.framework.generator.AbstractNabuccoGeneratorTest;


/**
 * NabuccoExceptionGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoExceptionGeneratorTest extends AbstractNabuccoGeneratorTest {

    private static final File BASE_EXCEPTION_DIR = new File(
            "../org.nabucco.framework.base/src/nbc/org/nabucco/framework/base/facade/exception");

    private static final File DYNAMICCODE_EXCEPTION_DIR = new File(
            "../org.nabucco.framework.common.dynamiccode/src/nbc/org/nabucco/framework/common/dynamiccode/facade/exception");

    private static final File AUTHORIZATION_EXCEPTION_DIR = new File(
            "../org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/facade/exception");

    @Test
    public void testBaseExceptionGeneration() throws Exception {
        super.generateDir(BASE_EXCEPTION_DIR);
    }

    @Test
    public void testDynamicCodeExceptionGeneration() throws Exception {
        super.generateDir(DYNAMICCODE_EXCEPTION_DIR);
    }

    @Test
    public void testAuthorizationExceptionGeneration() throws Exception {
        super.generateDir(AUTHORIZATION_EXCEPTION_DIR);
    }

}
