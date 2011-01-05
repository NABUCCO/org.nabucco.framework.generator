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
package org.nabucco.framework.generator.compiler.verifier;

import org.junit.Assert;
import org.junit.Test;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.NabuccoModelResourceType;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ImportDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.NodeChoice;
import org.nabucco.framework.generator.parser.syntaxtree.NodeListOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeOptional;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.mda.model.MdaModel;

/**
 * NabuccoVerifierTest
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoVerifierTest {

    private static final String COMPONENT = "org.nabucco.framework";

    private static final String PACKAGE = COMPONENT + ".test";

    private static final String NAME = "Description";
    
    private static final String ROOT = COMPONENT + "/";

    private static final String PATH = ROOT + COMPONENT.replace('.', '/') + "/" + NAME + ".nbc";
    
    private static final String OUT = "out/test";

    @Test
    public void testVerification() throws Exception {

        PackageDeclaration pkg = new PackageDeclaration(new NodeToken(PACKAGE));
        AnnotationDeclaration annotations = new AnnotationDeclaration(new NodeListOptional());
        NodeToken name = new NodeToken(NAME);

        NabuccoStatement basetype = new NabuccoStatement(new NodeChoice(new BasetypeStatement(
                annotations, name, new NodeOptional())));

        ImportDeclaration import1 = new ImportDeclaration(new NodeToken(
                "org.nabucco.framework.blub"));

        NabuccoUnit unit = new NabuccoUnit(pkg, new NodeListOptional(import1), basetype);

        NabuccoModel model = new NabuccoModel(unit, PATH, NabuccoModelType.BASETYPE,
                NabuccoModelResourceType.PROJECT);

        String content = model.printModel();
        Assert.assertNotNull(content);
        
        try {
            NabuccoVerifier.getInstance().verifyNabuccoModel(new MdaModel<NabuccoModel>(model),
                    ROOT, OUT);
            Assert.fail("VerificationException expected!");
        } catch (NabuccoVerificationException ve) {
            System.out.println(ve.getMessage());
        }

    }

}
