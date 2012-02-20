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
package org.nabucco.framework.generator.compiler.transformation.java.application.connector.util;

import java.util.Comparator;

import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.JavaAstField;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;

/**
 * ComponentRelationTypeLiteralComparator
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ComponentRelationTypeLiteralComparator implements Comparator<FieldDeclaration> {

    /**
     * Singleton instance.
     */
    private static ComponentRelationTypeLiteralComparator instance = new ComponentRelationTypeLiteralComparator();

    /**
     * Private constructor.
     */
    private ComponentRelationTypeLiteralComparator() {
    }

    /**
     * Singleton access.
     * 
     * @return the ComponentRelationTypeLiteralComparator instance.
     */
    public static ComponentRelationTypeLiteralComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(FieldDeclaration field1, FieldDeclaration field2) {
        if (field1 == null || field2 == null) {
            throw new IllegalArgumentException("Cannot compare fields [null].");
        }

        JavaAstField javaField = JavaAstElementFactory.getInstance().getJavaAstField();

        try {
            Integer modifier1 = javaField.getModifier(field1);
            Integer modifier2 = javaField.getModifier(field2);

            if (modifier1 != modifier2) {
                return modifier1.compareTo(modifier2);
            }

            String name1 = javaField.getFieldName(field1);
            String name2 = javaField.getFieldName(field2);

            return name1.compareTo(name2);

        } catch (JavaModelException e) {
            throw new NabuccoVisitorException("Error comparing component relation type fields.", e);
        }
    }

}
