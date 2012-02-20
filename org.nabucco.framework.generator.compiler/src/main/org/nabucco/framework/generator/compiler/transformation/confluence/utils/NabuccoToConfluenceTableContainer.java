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

import java.util.ArrayList;

/**
 * NabuccoToConfluenceTableContainer
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public class NabuccoToConfluenceTableContainer {

    private ArrayList<NabuccoToConfluenceTableRow> rows = new ArrayList<NabuccoToConfluenceTableRow>();

    private ArrayList<NabuccoToConfluenceTableSpaltenEnum> columns = new ArrayList<NabuccoToConfluenceTableSpaltenEnum>();

    public NabuccoToConfluenceTableContainer() {
    };

    /**
     * returns the columns as they would initialized
     * 
     * @return List of columns
     */
    public ArrayList<NabuccoToConfluenceTableSpaltenEnum> getColumns() {
        return this.columns;
    }

    /**
     * Add a new column to a table
     * 
     * @param column
     */
    public void addColumn(NabuccoToConfluenceTableSpaltenEnum column) {
        this.columns.add(column);
    }

    /**
     * Add a new row to a table
     * 
     * @param column
     */
    public void addNewRow(NabuccoToConfluenceTableRow row) {
        this.rows.add(row);
    }

    /**
     * Returns a list of rows
     * 
     * @return
     */
    public ArrayList<NabuccoToConfluenceTableRow> getRows() {
        return this.rows;
    }

}
