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

import java.util.List;

import org.nabucco.framework.base.facade.component.injector.NabuccoInjectionReciever;
import org.nabucco.framework.base.facade.component.injector.NabuccoInjector;
import org.nabucco.framework.plugin.base.model.browser.BrowserElement;
import org.nabucco.framework.plugin.base.model.browser.BrowserListElement;

/**
 * BrowserViewListElementTemplate.
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class BrowserViewListElementTemplate extends BrowserListElement<ListViewModel> implements
        NabuccoInjectionReciever {

    private ListViewBrowserElementHandler listViewBrowserElementHandler;

    public BrowserViewListElementTemplate(final List<Datatype> datatypeList) {
        this(datatypeList.toArray(new Datatype[datatypeList.size()]));
    }

    @Override
    public void removeBrowserElement(final BrowserElement element) {
        super.removeBrowserElement(element);
        listViewBrowserElementHandler.removeChild(element, this);
    }

    public BrowserViewListElementTemplate(final Datatype[] datatypeArray) {
        NabuccoInjector instance = NabuccoInjector.getInstance(ListViewBrowserElement.class);
        listViewBrowserElementHandler = instance.inject(ListViewBrowserElementHandler.class);

        viewModel = new ListViewModel();
        viewModel.setElements(datatypeArray);
    }

    @Override
    protected void createChildren() {
        clearChildren();
        listViewBrowserElementHandler.createChildren(viewModel, this);
    }
}
