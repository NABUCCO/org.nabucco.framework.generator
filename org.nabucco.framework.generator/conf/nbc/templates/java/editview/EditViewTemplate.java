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

import java.util.Map;
import java.io.Serializable;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;

import org.nabucco.framework.base.facade.datatype.utils.I18N;
import org.nabucco.framework.plugin.base.view.ManagedFormViewPart;
import org.nabucco.framework.plugin.base.view.NabuccoFormToolkit;

/**
 * EditViewTemplate
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class EditViewTemplate extends ManagedFormViewPart {

    public static final String TITLE = ID + ".title";
    
    public static final String TAB_TITLE = ID + ".tabTitle"; 
    
    public Map<String, Serializable> getValues() { 
        return model.getValues(); 
    } 

    private NabuccoFormToolkit ntk;

    @Override
    protected String getManagedFormTitle() {
        return I18N.i18n(TITLE);
    }
    
    @Override 
    public String getNewPartName() { 
        return I18N.i18n(TAB_TITLE, getValues()); 
    }

    @Override
    protected void createFormControl(Form form) {
        ntk = new NabuccoFormToolkit(form.getBody());
        Composite frame = ntk.createComposite(form.getBody(), new RowLayout(SWT.VERTICAL));
        model = new EditModel();
        getLayouter().layout(frame, getMessageManager(), model);
    }

    @Override
    protected void createHeadControl(Composite head) {
    }

    @Override
    protected void createToolbarActions(IToolBarManager toolbarManager) {
    }

}