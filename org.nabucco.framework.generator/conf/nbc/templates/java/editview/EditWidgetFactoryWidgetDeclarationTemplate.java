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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.nabucco.framework.common.authorization.ui.rcp.edit.user.view.AuthorizationUserEditViewComboHandler;
import org.nabucco.framework.plugin.base.combo.picker.ElementPickerCombo;
import org.nabucco.framework.plugin.base.component.picker.dialog.ElementPickerParameter;

/**
 * EditWidgetFactoryWidgetDeclarationTemplate
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class EditWidgetFactoryWidgetDeclarationTemplate extends WidgetFactory {
    
    // LabeledInputField and LabeledInputFieldReadOnly
    public static final String LABEL = "LabelName";

    public Label createLabel(Composite parent) {
        return nabuccoFormToolKit.createRealLabel(parent, LABEL);
    }

    // LabeledInputFieldReadOnly
    public Text createInputFieldReadOnly(Composite parent) {
        Text result = nabuccoFormToolKit.createTextInput(parent);
        result.setEnabled(false);
        return result;
    }

    // LabeledInputField
    public static final String OBSERVE_VALUE = "observeValue";

    public Text createInputField(Composite parent) {
        Text result = nabuccoFormToolKit.createTextInput(parent);
        DataBindingContext bindingContext = new DataBindingContext();

        IObservableValue uiElement = SWTObservables.observeText(result, SWT.Modify);
        IObservableValue modelElement = BeansObservables.observeValue(model, OBSERVE_VALUE);

        bindingContext.bindValue(uiElement, modelElement, null, null);

        return result;
    }

    // LabeledPicker
    public static final String TITLE = "title";

    public static final String MESSAGE = "message";

    public static final String SHELL_TITLE = "Picker";

    public static final String MESSAGE_TABLE = "messageTable";

    public static final String MESSAGE_COMBO = "messageCombo";

    public static final String PATH_LABEL = "path";

    public void createElementPicker(Composite parent, ElementPickerParameter params) {
        ElementPickerComposite picker = new ElementPickerComposite(parent, SWT.NONE, params,
                params.getInputFieldLabelProvider(), new LabelForDialog(TITLE, MESSAGE,
                        SHELL_TITLE, MESSAGE_TABLE, MESSAGE_COMBO, PATH_LABEL));

        DataBindingContext bindingContext = new DataBindingContext();
        IObservableValue uiElement;
        IObservableValue modelElement;

        uiElement = SWTObservables.observeText(picker.getInputText(), SWT.Modify);
        modelElement = BeansObservables.observeValue(model, OBSERVE_VALUE);

        bindingContext.bindValue(uiElement, modelElement, null, null);

        picker.addElementSelected(new DatatypePickerHandler(model));
    }

    // ComboBox
    public void createElementCombo(Composite parent, ElementPickerComboParameter params) {
        ElementPickerCombo elementCombo = new ElementPickerCombo(parent, SWT.NONE, params
                .getContentProvider(), params.getTableLabelProvider(), "",false);
        DataBindingContext bindingContext = new DataBindingContext();
        IObservableValue uiElement;
        IObservableValue modelElement;
        uiElement = SWTObservables.observeSelection(elementCombo.getCombo());
        modelElement = BeansObservables.observeValue(model, OBSERVE_VALUE);
        bindingContext.bindValue(uiElement, modelElement, null, null);
        elementCombo.addSelectionListener(new DatatypeComboHandler(model));
    }
}