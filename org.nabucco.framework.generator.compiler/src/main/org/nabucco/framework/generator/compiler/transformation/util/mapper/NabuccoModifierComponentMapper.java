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
package org.nabucco.framework.generator.compiler.transformation.util.mapper;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;

/**
 * NabuccoModifierComponentMapper
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoModifierComponentMapper {

    private static final String FACADE = "facade";

    private static final String IMPLEMENTATION = "impl";

    /**
     * Maps the modifier to the appropriate project token name (facade, impl, etc.).
     * 
     * @param modifier
     *            the modifier
     * 
     * @return the project token
     */
    public static String mapModifierToProjectString(NabuccoModifierType modifier) {

        switch (modifier) {

        case PUBLIC:
            return FACADE;

        case PROTECTED:
            return FACADE;

        case PRIVATE:
            return IMPLEMENTATION;
        }

        throw new IllegalArgumentException("Modifier '" + modifier + "' is not supported.");
    }

    /**
     * Creates a {@link NabuccoModifierType} instance by the given string representation.
     * 
     * @param modifier
     *            the modifier string
     * 
     * @return the enum instance
     */
    public static NabuccoModifierType getModifierType(String modifier) {
        return NabuccoModifierType.valueOf(modifier.toUpperCase());
    }

    /**
     * Maps the nabucco modifier to the java modifier.
     * 
     * @param modifier
     *            the nabucco modifier
     * 
     * @return the java modifier
     */
    public static int mapModifierToJava(NabuccoModifierType modifier) {
        return mapModifierToJava(modifier, false);
    }

    /**
     * Maps the nabucco modifier to the java modifier.
     * 
     * @param modifier
     *            the nabucco modifier
     * @param defines
     *            if the modifier is abstract or not (only for methods)
     * 
     * @return the java modifier
     */
    public static int mapModifierToJava(NabuccoModifierType modifier, boolean isAbstract) {
        int mod;

        switch (modifier) {
        case PUBLIC:
            mod = ClassFileConstants.AccPublic;
            break;
        case PROTECTED:
            mod = ClassFileConstants.AccProtected;
            break;
        case PRIVATE:
            mod = ClassFileConstants.AccPrivate;
            break;
        default:
            throw new IllegalArgumentException("Modifier '" + modifier + "' is not supported.");
        }
        if (isAbstract) {
            mod |= ClassFileConstants.AccAbstract;
            mod |= ExtraCompilerModifiers.AccSemicolonBody;
        }
        return mod;
    }

}
