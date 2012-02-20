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
package org.nabucco.framework.generator.compiler.transformation.common.annotation;

/**
 * NabuccoAnnotationGroupType
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public enum NabuccoAnnotationGroupType {

    ASPECT("Aspect annotation"),

    CONSTRAINT("Constraint annotation"),

    CONNECTOR("Connector annotation"),

    DB("Database annotation"),

    DOCUMENTATION("Documentation annotation"),

    EXTENSION("Extension annotation"),

    GENERATOR("Generator annotation"),

    INIT("Initialization annotation"),

    INJECTION("Injection annotation"),

    SERVICE("Service annotation"),
    
    RELATION("Relationship annotation"),

    UI("User Interface annotation");

    private NabuccoAnnotationGroupType(String description) {
        this.description = description;
    }

    private String description;

    /**
     * Getter for the annotation type description.
     * 
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
