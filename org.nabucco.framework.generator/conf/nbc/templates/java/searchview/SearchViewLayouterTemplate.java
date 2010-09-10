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

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.nabucco.framework.common.authorization.ui.rcp.search.group.model.AuthorizationGroupSearchModel;
import org.nabucco.framework.plugin.base.view.NabuccoFormToolkit;
import org.nabucco.framework.plugin.base.view.NabuccoMessageManager;

/**
 * SearchViewLayouterTemplate
 * 
 * @author Silas Schwarz, PRODYNA AG
 */
public class SearchViewLayouterTemplate {

    private final String MESSAGE_OWNER_ID = "MESSAGE_OWNER_ID";

    private SearchViewWidgetFactoryTemplate widgetFactory;

    private NabuccoMessageManager messageManager;

    public void layout(final Composite parent, final NabuccoMessageManager aMessageManager,
            final TemplateSearchModel aModel) {
        NabuccoFormToolkit ntk = new NabuccoFormToolkit(new FormToolkit(parent.getDisplay()));
        widgetFactory = new SearchViewWidgetFactoryTemplate(ntk, aModel);
        messageManager = aMessageManager;
        parent.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent arg0) {
                messageManager.showMessages(MESSAGE_OWNER_ID);
            }
        });

        Section section = ntk.createSection(parent, "SectionName", new RowLayout());
        Composite child = ntk.createComposite(section, new GridLayout(2, true));

        // create widgets

        section.setClient(child);
    }

}
