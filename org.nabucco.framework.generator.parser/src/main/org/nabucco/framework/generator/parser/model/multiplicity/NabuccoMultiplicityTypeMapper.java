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
package org.nabucco.framework.generator.parser.model.multiplicity;


/**
 * NabuccoMultiplicityTypeMapper
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoMultiplicityTypeMapper {

    private static final String ZERO_TO_ONE = "[0..1]";

    private static final String ONE = "[1]";

    private static final String ZERO_TO_MANY = "[0..*]";

    private static final String ONE_TO_ONE = "[1..*]";
    
    /**
     * Singleton instance.
     */
    private static NabuccoMultiplicityTypeMapper instance = new NabuccoMultiplicityTypeMapper();

    /**
     * Private constructor.
     */
    private NabuccoMultiplicityTypeMapper() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoMultiplicityTypeMapper instance.
     */
    public static NabuccoMultiplicityTypeMapper getInstance() {
        return instance;
    }
    
    public NabuccoMultiplicityType mapToMultiplicity(String multiplicity) {

        if (multiplicity.equals(ZERO_TO_ONE)) {
            return NabuccoMultiplicityType.ZERO_TO_ONE;
        } else if (multiplicity.equals(ONE)) {
            return NabuccoMultiplicityType.ONE;
        } else if (multiplicity.equals(ZERO_TO_MANY)) {
            return NabuccoMultiplicityType.ZERO_TO_MANY;
        } else if (multiplicity.equals(ONE_TO_ONE)) {
            return NabuccoMultiplicityType.ONE_TO_MANY;
        }

        throw new IllegalArgumentException("NabuccoModifierType is not supported: " + multiplicity);
    }
    
}
