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
/*
* Copyright 2010 PRODYNA AG
*
* Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.opensource.org/licenses/eclipse-1.0.php or
* http://nabuccosource.org/License.html
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
import java.io.Serializable;
import java.util.Map;

import org.nabucco.framework.base.facade.component.injector.NabuccoInjectionReciever;
import org.nabucco.framework.base.facade.component.injector.NabuccoInjector;
import org.nabucco.framework.plugin.base.model.browser.DatatypeBrowserElement;

/**
 * BrowserViewElementTemplate.
 * 
 * @author Stefanie Feld, PRODYNA AG
 */

public class BrowserViewElementTemplate extends DatatypeBrowserElement implements
NabuccoInjectionReciever {

    private EditViewModel viewModel;
    
    private EditViewBrowserElementHandler browserHandler;

    public BrowserViewElementTemplate(final Datatype datatype) {
        NabuccoInjector instance = NabuccoInjector
            .getInstance(EditViewBrowserElement.class);
        browserHandler = instance
            .inject(EditViewBrowserElementHandler.class);
        viewModel = new EditViewModel();
        viewModel.setDatatype(datatype);
    }

    @Override
    public Map<String, Serializable> getValues() {
        return this.viewModel.getValues();
    }
    
    @Override
    protected void fillDatatype() {
        viewModel = browserHandler.loadFull(viewModel);
    }
    
    @Override
    protected void createChildren() {
        clearChildren();
        browserHandler.createChildren(viewModel, this);
    }
    
    public EditViewModel getViewModel() {
        return this.viewModel;
    }
    
    public void setViewModel(EditViewModel viewModel) {
        this.viewModel = viewModel;
    }
}
