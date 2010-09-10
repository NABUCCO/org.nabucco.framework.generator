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
package org.nabucco.framework.generator.compiler.transformation.java.visitor.util.spp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.parser.model.multiplicity.NabuccoMultiplicityTypeMapper;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.NodeToken;

import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;

/**
 * StrcuturedPropertyPathEntry
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class StructuredPropertyPathEntry extends HashMap<String, StructuredPropertyPathEntry> {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    static final Character SEPARATOR = '.';

    private String importString;

    private TypeReference typeReference;

    private Boolean multiple;

    private Object declaration;

    private StructuredPropertyPathEntryType entryType;

    StructuredPropertyPathEntry() {
        this.entryType = StructuredPropertyPathEntryType.ROOT;
    }

    private StructuredPropertyPathEntry(String importString, Object declaration,
            StructuredPropertyPathEntryType type) {
        this.importString = importString;
        this.declaration = declaration;
        this.entryType = type;
        this.multiple = evaluateMultiplicity();
    }

    /**
     * @return
     */
    private Boolean evaluateMultiplicity() {
        switch (this.getEntryType()) {
        case BASETYPE: {
            return NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                    ((BasetypeDeclaration) this.declaration).nodeToken2.tokenImage).isMultiple();
        }
        case ENUMERATION: {
            return NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                    ((EnumerationDeclaration) this.declaration).nodeToken1.tokenImage).isMultiple();
        }
        case DATATYPE: {
            return NabuccoMultiplicityTypeMapper.getInstance().mapToMultiplicity(
                    ((DatatypeDeclaration) this.declaration).nodeToken1.tokenImage).isMultiple();

        }
        case ROOT:
        default: {
            return Boolean.FALSE;
        }
        }
    }

    StructuredPropertyPathEntry(DatatypeDeclaration d, String importString) {
        this(importString, d, StructuredPropertyPathEntryType.DATATYPE);
    }

    StructuredPropertyPathEntry(BasetypeDeclaration b, String importString) {
        this(importString, b, StructuredPropertyPathEntryType.BASETYPE);
    }

    StructuredPropertyPathEntry(EnumerationDeclaration e, String importString) {
        this(importString, e, StructuredPropertyPathEntryType.ENUMERATION);
    }

    /**
     * @return Returns the type.
     */
    public TypeReference getTypeReference() throws JavaModelException {
        if (typeReference == null) {
            typeReference = initTypeReference();
        }
        return typeReference;
    }

    private TypeReference initTypeReference() throws JavaModelException {
        String typeName = null;
        TypeReference result = null;
        switch (this.entryType) {
        case BASETYPE: {
            typeName = ((BasetypeDeclaration) this.declaration).nodeToken1.tokenImage;
            break;
        }
        case DATATYPE: {
            typeName = ((NodeToken) ((DatatypeDeclaration) this.declaration).nodeChoice1.choice).tokenImage;
            break;
        }
        case ENUMERATION: {
            typeName = ((NodeToken) ((EnumerationDeclaration) this.declaration).nodeChoice1.choice).tokenImage;
            break;
        }
        }
        if (typeName != null) {
            result = JavaAstModelProducer.getInstance().createTypeReference(typeName, false);
        }
        return result;
    }

    /**
     * @return Returns the entryType.
     */
    public StructuredPropertyPathEntryType getEntryType() {
        return entryType;
    }

    /**
     * @return Returns the multiple.
     */
    public Boolean isMultiple() {
        return multiple;
    }

    /**
     * Returns the StructuredPropertyPathEntry for a given access path.
     * 
     * @param path
     *            the access path
     * @return {@link StructuredPropertyPathEntry} if path is valid, otherwise <code>null</code>.
     */
    public StructuredPropertyPathEntry getEntry(String path) {
        if (path.isEmpty()) {
            return this;
        }
        if (path.indexOf(SEPARATOR) > 0) {
            String nextToken = path.substring(0, path.indexOf(SEPARATOR));
            if (containsKey(nextToken)) {
                return get(nextToken).getEntry(path.substring(path.indexOf(SEPARATOR) + 1));
            }
            return null;
        }
        return get(path);
    }

    /**
     * Gets all imports for a given property path.
     * 
     * @param path
     *            mapped property path.
     * @return all imports needed to use that property.
     */
    public Set<String> getImports(String path) {
        Set<String> result = new HashSet<String>();
        getEntryImport(path, result);
        return result;
    }

    /**
     * @return Returns the importString.
     */
    private String getImportString() {
        return importString;
    }

    private void getEntryImport(String path, Set<String> result) {
        if (path.indexOf(SEPARATOR) > 0) {
            String nextToken = path.substring(0, path.indexOf(SEPARATOR));
            if (containsKey(nextToken)) {
                StructuredPropertyPathEntry nextEntry = get(nextToken);
                result.add(nextEntry.getImportString());
                nextEntry.getEntryImport(path.substring(path.indexOf(SEPARATOR) + 1), result);
            }
        } else {
            result.add(get(path).getImportString());
        }
    }

}
