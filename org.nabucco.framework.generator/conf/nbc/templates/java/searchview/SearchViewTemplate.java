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

import org.eclipse.swt.widgets.Composite;

import org.nabucco.framework.plugin.base.view.NabuccoMessageManager;
import org.nabucco.framework.plugin.base.view.NabuccoSearchView;
import org.nabucco.framework.plugin.base.view.AbstractNabuccoSearchView;

/**
 * SearchViewTemplate
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class SearchViewTemplate extends AbstractNabuccoSearchView implements NabuccoSearchView {

    private TemplateSearchModel model;

    public SearchViewTemplate() {
        model = new TemplateSearchModel(getCorrespondingListView());
    }

    @Override
    public void createPartControl(final Composite parent,
            final NabuccoMessageManager aMessageManager) {
        getLayouter().layout(parent, aMessageManager, model);
    }

    @Override
    public TemplateModel getModel() {
        return model;
    }

}
