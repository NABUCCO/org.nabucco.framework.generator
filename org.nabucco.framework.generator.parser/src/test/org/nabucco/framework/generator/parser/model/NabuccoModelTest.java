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
package org.nabucco.framework.generator.parser.model;

import org.junit.Assert;
import org.junit.Test;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.NodeChoice;
import org.nabucco.framework.generator.parser.syntaxtree.NodeListOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;

/**
 * NabuccoModelTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoModelTest {

    @Test
    public void testModelName() throws Exception {
        PackageDeclaration pkg = new PackageDeclaration(new NodeToken("org.nabucco.framework"));
        AnnotationDeclaration annotations = new AnnotationDeclaration(new NodeListOptional());
        NodeToken name = new NodeToken("TestBasetype");

        NabuccoStatement basetype = new NabuccoStatement(new NodeChoice(new BasetypeStatement(
                annotations, name, new NodeOptional())));

        NabuccoUnit unit = new NabuccoUnit(pkg, new NodeListOptional(), basetype);

        NabuccoModel model = new NabuccoModel(unit, "", NabuccoModelType.BASETYPE,
                NabuccoModelResourceType.PROJECT);

        Assert.assertEquals("TestBasetype", model.getName());
    }

}
