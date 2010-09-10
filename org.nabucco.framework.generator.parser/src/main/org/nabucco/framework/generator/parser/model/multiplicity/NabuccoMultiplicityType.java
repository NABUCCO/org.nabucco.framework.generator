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
 * NabuccoMultiplicityType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum NabuccoMultiplicityType {

    ZERO_TO_ONE("0,1"),

    ONE("1,1"),

    ZERO_TO_MANY("0,n"),

    ONE_TO_MANY("1,n");

    private NabuccoMultiplicityType(String constraint) {
        this.constraint = constraint;
    }

    private String constraint;

    /**
     * @return Returns the constraint.
     */
    public String getConstraint() {
        return this.constraint;
    }

    public boolean isMultiple() {
        
        switch (this) {
            case ZERO_TO_MANY:
                return true;
            case ONE_TO_MANY:
                return true;
        }
        
        return false;
    }

}
