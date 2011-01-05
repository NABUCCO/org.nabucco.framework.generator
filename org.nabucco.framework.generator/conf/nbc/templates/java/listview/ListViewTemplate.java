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

package org.nabucco.framework.common.authorization.ui.rcp.list.user.view;

import java.util.Map;
import java.io.Serializable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;

import org.nabucco.framework.base.facade.datatype.utils.I18N;
import org.nabucco.framework.plugin.base.component.list.view.NabuccoComponentListView;
import org.nabucco.framework.plugin.base.component.list.view.NabuccoCompositeTextFilter;
import org.nabucco.framework.plugin.base.component.list.view.NabuccoTableViewer;

/**
 * ListViewTemplate
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class ListViewTemplate extends NabuccoComponentListView {

    public static final String TITLE = ID + ".title";
    
    public static final String TAB_TITLE = ID + ".tabTitle";

    public ListViewTemplate() {
        model = new ListViewModel();
    }

    @Override
    protected void createFormControl(Form form) {
        Composite o = getLayouter().layout(form.getBody(), getMessageManager(), model, this);
        if (o instanceof NabuccoTableViewer) {
            tableViewer = (NabuccoTableViewer) o;
        }
    }

    @Override
    protected NabuccoCompositeTextFilter createFilter(final Composite parent) {
        return new NabuccoCompositeTextFilter(parent);
    }

    @Override
    public void setFocus() {
    }
    
    public Map<String, Serializable> getValues() { 
        return model.getValues(); 
    } 
    
    @Override 
    public String getNewPartName() { 
        return I18N.i18n(TAB_TITLE, getValues()); 
    }

    @Override
    public String getManagedFormTitle() {
        return I18N.i18n(TITLE);
    }
}
