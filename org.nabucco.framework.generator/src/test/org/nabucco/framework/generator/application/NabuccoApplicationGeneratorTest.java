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
package org.nabucco.framework.generator.application;

import java.io.File;

import org.junit.Test;
import org.nabucco.framework.generator.AbstractNabuccoGeneratorTest;
import org.nabucco.framework.generator.NabuccoGenerator;
import org.nabucco.framework.generator.parser.file.NabuccoFile;

/**
 * NabuccoApplicationGeneratorTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoApplicationGeneratorTest extends AbstractNabuccoGeneratorTest {

    private static final File SHOWCASE_APPLICATION = new File(
            "../../org.nabucco.framework.showcase/org.nabucco.framework.showcase/src/nbc/org/nabucco/framework/showcase/");

    private static final File AUTHORIZATION_COMPONENT = new File(
            "../../../../NABUCCO/org.nabucco.framework.common.authorization/org.nabucco.framework.common.authorization/src/nbc/org/nabucco/framework/common/authorization/");

    @Test
    public void generateShowcaseApplication() throws Exception {
        NabuccoFile dir = new NabuccoFile(SHOWCASE_APPLICATION);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }

    @Test
    public void generateAuthorizationComponent() throws Exception {
        NabuccoFile dir = new NabuccoFile(AUTHORIZATION_COMPONENT);

        NabuccoGenerator generator = new NabuccoGenerator(dir);
        generator.generate();
    }
}
