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
package org.nabucco.framework.generator.parser;

import java.io.File;
import java.io.FileReader;

import junit.framework.Assert;

import org.junit.Test;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;

/**
 * GrammarTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class GrammarTest {

    private static final String BASETYPE = "Description";

    private static final String ENUMERATION = "Type";

    private static final String DATATYPE = "User";

    private static final String SERVICE = "MaintainService";

    private static final String PROJECT_TOKEN = "org.nabucco.framework";

    private static final String SUFFIX = ".nbc";

    private static final String TEST_PROJECT_PATH = "conf/sample/" + PROJECT_TOKEN + "/";

    private static final String TEST_SRC_PATH = TEST_PROJECT_PATH + "src/";

    private static final String TEST_SRC_PKG = TEST_SRC_PATH + "nbc/org/nabucco/framework/";

    @Test
    public void testBasetypeGrammar() throws Exception {

        String filePath = TEST_SRC_PKG + BASETYPE + SUFFIX;
        FileReader reader = new FileReader(new File(filePath));

        NabuccoParser parser = new NabuccoParser(reader);
        NabuccoUnit unit = parser.NabuccoUnit();

        Assert.assertNotNull(unit);
    }

    @Test
    public void testEnumerationGrammar() throws Exception {

        String filePath = TEST_SRC_PKG + ENUMERATION + SUFFIX;
        FileReader reader = new FileReader(new File(filePath));

        NabuccoParser parser = new NabuccoParser(reader);
        NabuccoUnit unit = parser.NabuccoUnit();

        Assert.assertNotNull(unit);
    }

    @Test
    public void testDatatypeGrammar() throws Exception {

        String filePath = TEST_SRC_PKG + DATATYPE + SUFFIX;
        FileReader reader = new FileReader(new File(filePath));

        NabuccoParser parser = new NabuccoParser(reader);
        NabuccoUnit unit = parser.NabuccoUnit();

        Assert.assertNotNull(unit);
    }

    @Test
    public void testServiceGrammar() throws Exception {

        String filePath = TEST_SRC_PKG + SERVICE + SUFFIX;
        FileReader reader = new FileReader(new File(filePath));

        NabuccoParser parser = new NabuccoParser(reader);
        NabuccoUnit unit = parser.NabuccoUnit();

        Assert.assertNotNull(unit);
    }

}
