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
package org.nabucco.framework.generator.compiler.transformation.confluence.utils;

import java.util.HashMap;

import org.nabucco.framework.generator.compiler.transformation.confluence.NabuccoToConfluenceDeclarationVisitor;
import org.nabucco.framework.mda.model.text.confluence.ast.ConfluenceComponent;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceExternalLink;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceLink;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceLocalAnchor;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceText;

/**
 * NabuccoToConfluenceTableRow
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceTableRow {

    private static final String EMPTY = "";

    private NabuccoToConfluenceTableContainer parent;

    private HashMap<NabuccoToConfluenceTableSpaltenEnum, ConfluenceComponent> valueMap;

    private NabuccoToConfluenceDeclarationVisitor visitor;

    public NabuccoToConfluenceTableRow(NabuccoToConfluenceTableContainer parent,
            NabuccoToConfluenceDeclarationVisitor visitor) {
        this.parent = parent;
        this.valueMap = new HashMap<NabuccoToConfluenceTableSpaltenEnum, ConfluenceComponent>();
        this.visitor = visitor;
    }

    /**
     * Add a new Value to a row. If the column does not exist, it will be created
     * 
     * @param column
     *            column
     * @param value
     *            value
     */
    public void addValue(NabuccoToConfluenceTableSpaltenEnum column, ConfluenceComponent value) {
        if (!this.parent.getColumns().contains(column)) {
            this.parent.addColumn(column);
        }

        this.valueMap.put(column, value);
    }

    /**
     * Add a new Textelement to the row
     * 
     * @param column
     *            column for the new element
     * @param value
     *            value of the element
     */
    public void addText(NabuccoToConfluenceTableSpaltenEnum column, String value) {
        String normalizedValue = this.normalizeString(column, value);
        this.addValue(column, new ConfluenceText(normalizedValue));
    }

    /**
     * Add a new Linkelement to the row
     * 
     * @param column
     *            column for the new element
     * @param value
     *            value of the element
     */
    public void addLink(NabuccoToConfluenceTableSpaltenEnum column, String value) {
        if (!value.equals(EMPTY)) {
            String normalizedValue = this.normalizeString(column, value);
            ConfluenceLink link = new ConfluenceLink(normalizedValue, this.visitor.findComponentToElement(normalizedValue),
                    normalizedValue, normalizedValue);

            this.addValue(column, link);
        } else {
            this.addValue(column, new ConfluenceText(NabuccoConfluenceStringManipulator.CONF_EMPTY));
        }
    }

    /**
     * Add a new Linkelement to the row
     * 
     * @param column
     *            column for the new element
     * @param value
     *            value of the element
     */
    public void addExternalLink(NabuccoToConfluenceTableSpaltenEnum column, String value, String linkUrl) {
        if (!value.equals(EMPTY) && !linkUrl.equals(EMPTY)) {
            String normalizedValue = this.normalizeString(column, value);
            ConfluenceExternalLink link = new ConfluenceExternalLink(linkUrl, normalizedValue);

            this.addValue(column, link);
        } else {
            this.addValue(column, new ConfluenceText(NabuccoConfluenceStringManipulator.CONF_EMPTY));
        }
    }

    /**
     * Add a new local Linkelement to the row
     * 
     * @param column
     *            column for the new element
     * @param value
     *            value of the element
     */
    public void addLink(NabuccoToConfluenceTableSpaltenEnum column, ConfluenceLocalAnchor value) {
        this.addValue(column, new ConfluenceLink(value));
    }

    /**
     * Complete String normalization
     * 
     * @param column
     *            Column
     * @param value
     *            Value
     * @return
     */
    private String normalizeString(NabuccoToConfluenceTableSpaltenEnum column, String value) {
        String retVal = value;

        if (retVal == null) {
            retVal = NabuccoConfluenceStringManipulator.CONF_EMPTY;
        }

        switch (column) {
        case MULTIPLICITY:
            retVal = NabuccoConfluenceStringManipulator.normalizeString(retVal,
                    NabuccoConfluenceTextFormat.MULTIPLICITY);
            break;
        case EXCEPTION:
            retVal = NabuccoConfluenceStringManipulator.normalizeString(retVal, NabuccoConfluenceTextFormat.EXCEPTION);
            break;
        default:
            retVal = NabuccoConfluenceStringManipulator.normalizeString(retVal);
            break;
        }

        return retVal;
    }

    /**
     * Return a value from column in a row
     * 
     * @param column
     * @return
     */
    public ConfluenceComponent getValue(NabuccoToConfluenceTableSpaltenEnum column) {
        if (this.valueMap.containsKey(column)) {
            return this.valueMap.get(column);
        }

        return null;
    }
}
