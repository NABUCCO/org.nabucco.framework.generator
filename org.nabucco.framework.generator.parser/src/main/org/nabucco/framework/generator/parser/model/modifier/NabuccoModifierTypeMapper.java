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
package org.nabucco.framework.generator.parser.model.modifier;

/**
 * NabuccoModifierTypeMapper
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoModifierTypeMapper {

    /**
     * Singleton instance.
     */
    private static NabuccoModifierTypeMapper instance = new NabuccoModifierTypeMapper();

    /**
     * Private constructor.
     */
    private NabuccoModifierTypeMapper() {
    }

    /**
     * Singleton access.
     * 
     * @return the NabuccoModifierTypeMapper instance.
     */
    public static NabuccoModifierTypeMapper getInstance() {
        return instance;
    }

    public NabuccoModifierType mapToModifier(String name) {

        if (name.equalsIgnoreCase(NabuccoModifierType.PUBLIC.name())) {
            return NabuccoModifierType.PUBLIC;
        } else if (name.equalsIgnoreCase(NabuccoModifierType.PROTECTED.name())) {
            return NabuccoModifierType.PROTECTED;
        } else if (name.equalsIgnoreCase(NabuccoModifierType.PRIVATE.name())) {
            return NabuccoModifierType.PRIVATE;
        }

        throw new IllegalArgumentException("NabuccoModifierType is not supported: " + name);
    }
}
