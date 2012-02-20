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
package org.nabucco.framework.common.authorization.ui.rcp.edit.user.view;

import java.util.Map;

import org.eclipse.jface.viewers.Viewer;

import org.nabucco.framework.base.facade.component.injector.NabuccoInjectionReciever;
import org.nabucco.framework.base.facade.component.injector.NabuccoInjector;
import org.nabucco.framework.plugin.base.component.picker.dialog.ElementPickerContentProvider;

/**
 * EditViewPickerContentProviderTemplate
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class EditViewPickerContentProviderTemplate implements ElementPickerContentProvider,
        NabuccoInjectionReciever {

    private PickerContentProviderHandler handler = NabuccoInjector.getInstance(
            PickerContentProvider.class).inject(PickerContentProviderHandler.class);

    private Map<String, DataType[]> values;
    
    private ModelDataType viewModel;

    private void initValues() {
        values = handler.loadAllDatatypes(viewModel);
    }
    
    public EditViewPickerContentProviderTemplate(ModelDataType viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public String[] getPaths() {
        initValues();
        return values.keySet().toArray(new String[values.size()]);
    }

    @Override
    public Object[] getElements(Object arg0) {
        initValues();
        if (arg0 instanceof String) {
            return values.get(arg0);
        }
        return new Object[0];
    }

    @Override
    public void dispose() {
        values = null;

    }

    @Override
    public void inputChanged(Viewer arg0, Object anOldValue, Object anNewValue) {
    }

}
