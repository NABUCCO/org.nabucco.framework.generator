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
package org.nabucco.framework.generator.parser;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;
import org.nabucco.framework.generator.parser.NabuccoParser;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;


public class ParserTest {
    
	@Test
	public void testNabuccoParserSimple() throws Exception {
	    
		StringBuilder builder = new StringBuilder();
		builder.append("package org.nabucco.framework;\n");
		builder.append("public Component org.nabucco.framework.SampleComponent {\n");
		builder.append("  public Datatype org.nabucco.framework.SampleDatatype sampleDataType;\n");
		builder.append("  public Enumeration org.nabucco.framework.SampleEnum sampleEnum;\n");
		builder.append("  public Service sampleMethod(org.nabucco.framework.SampleOne sampleOne, org.nabucco.framework.SampleTwo sampleTwo) throws org.nabucco.framework.SampleException;\n");
		builder.append("}\n");
		
		StringReader reader = new StringReader(builder.toString());

		NabuccoParser parser = new NabuccoParser(reader);
		NabuccoUnit unit = parser.NabuccoUnit();
		
		Assert.assertNotNull(unit);
	}
	
	@Test
	public void testNabuccoParserComplex() throws Exception {
	    
	    FileReader reader = new FileReader(new File("./conf/test/SampleComponent.nbc"));
	    
	    NabuccoParser parser = new NabuccoParser(reader);
	    NabuccoUnit unit = parser.NabuccoUnit();

        Assert.assertNotNull(unit);
	}
}
