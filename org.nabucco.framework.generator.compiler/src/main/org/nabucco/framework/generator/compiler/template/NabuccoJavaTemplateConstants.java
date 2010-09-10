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
package org.nabucco.framework.generator.compiler.template;

import org.nabucco.framework.mda.template.java.JavaTemplateConstants;

/**
 * NabuccoJavaTemplateConstants
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface NabuccoJavaTemplateConstants extends JavaTemplateConstants {

    /* Common Templates */

    final String BASETYPE_TEMPLATE = "Basetype" + TEMPLATE;

    final String DATATYPE_TEMPLATE = "Datatype" + TEMPLATE;

    final String DATATYPE_VISITOR_TEMPLATE = "Datatype" + VISITOR + TEMPLATE;

    final String EXCEPTION_TEMPLATE = "Exception" + TEMPLATE;

    final String MESSAGE_TEMPLATE = "Message" + TEMPLATE;

    final String COMPONENT_INTERFACE_TEMPLATE = COMPONENT + INTERFACE + TEMPLATE;

    final String COMPONENT_IMPLEMENTATION_TEMPLATE = COMPONENT + IMPLEMENTATION + TEMPLATE;

    final String COMPONENT_LOCATOR_TEMPLATE = COMPONENT + LOCATOR + TEMPLATE;

    final String COMPONENT_OPERATION_TEMPLATE = COMPONENT + OPERATION + TEMPLATE;

    final String SERVICE_COMPONENT_DELEGATE_FACTORY_PROVIDER_TEMPLATE = SERVICE + DELEGATE
            + FACTORY + TEMPLATE;

    final String SERVICE_DELEGATE_TEMPLATE = SERVICE + DELEGATE + TEMPLATE;

    final String SERVICE_INTERFACE_TEMPLATE = SERVICE + INTERFACE + TEMPLATE;

    final String SERVICE_IMPLEMENTATION_TEMPLATE = SERVICE + IMPLEMENTATION + TEMPLATE;

    final String SERVICE_OPERATION_TEMPLATE = SERVICE + OPERATION + TEMPLATE;

    final String SERVICE_HANDLER_TEMPLATE = ABSTRACT + SERVICE + HANDLER + TEMPLATE;

    final String ENTITY_TEMPLATE = "Entity" + TEMPLATE;

    final String ENUM_TEMPLATE = "Enum" + TEMPLATE;

    final String VIEW_COMMAND_TEMAPLTE = VIEW + COMMAND + TEMPLATE;

    final String VIEW_COMMAND_HANDLER_TEMAPLTE = VIEW + COMMAND + HANDLER + TEMPLATE;

    final String LIST_VIEW_TEMPLATE = LIST + VIEW + TEMPLATE;

    final String LIST_VIEW_MODEL_TEMPLATE = LIST + VIEW + MODEL + TEMPLATE;

    final String LIST_VIEW_LABEL_PROVIDER_TEMPLATE = LIST + VIEW + LABEL_PROVIDER + TEMPLATE;

    final String LIST_VIEW_WIDGET_FACTORY_TEMPLATE = LIST + VIEW + WIDGET_FACTORY + TEMPLATE;

    final String COMPARATOR = "Comparator";

    final String LIST_VIEW_COMPARATOR_TEMPLATE = LIST + VIEW + COMPARATOR + TEMPLATE;

    final String LIST_VIEW_TABLE_FILTER_TEMPLATE = LIST + VIEW + TABLE + FILTER + TEMPLATE;

    final String COMMON_VIEW_MODEL_TEMPLATE = COMMON + VIEW + MODEL + TEMPLATE;

    final String COMMON_VIEW_VIEW_TEMPLATE = COMMON + VIEW + VIEW + TEMPLATE;

    final String COMMON_VIEW_MODEL_METHOD_TEMPLATE = COMMON + VIEW + MODEL + METHOD + TEMPLATE;

    final String SEARCH_VIEW_TEMPLATE = SEARCH + VIEW + TEMPLATE;

    final String SEARCH_VIEW_WIDGET_FACTORY_TEMPLATE = SEARCH + VIEW + WIDGET_FACTORY + TEMPLATE;

    final String SEARCH_VIEW_LAYOUTER_TEMPLATE = SEARCH + VIEW + LAYOUTER + TEMPLATE;

    final String SEARCH_VIEW_MODEL_TEMPLATE = SEARCH + VIEW + MODEL + TEMPLATE;

    final String EDIT_VIEW_TEMPLATE = EDIT + VIEW + TEMPLATE;

    final String EDIT_WIDGET_FACTORY_TEMPLATE = EDIT + WIDGET_FACTORY + TEMPLATE;

    final String EDIT_WIDGET_FACTORY_WIDGET_DECLARATION_TEMPLATE = EDIT + WIDGET_FACTORY + WIDGET
            + DECLARATION + TEMPLATE;

    final String EDIT_VIEW_PICKER_CONTENT_PROVIDER_TEMPLATE = EDIT + VIEW + "PickerContentProvider"
            + TEMPLATE;

    final String EDIT_VIEW_PICKER_CONTENT_PROVIDER_HANDLER_TEMPLATE = EDIT + VIEW
            + "PickerContentProviderHandler" + TEMPLATE;

    final String EDIT_VIEW_PICKER_HANDLER_TEMPLATE = EDIT + VIEW + "PickerHandler" + TEMPLATE;

    final String EDIT_VIEW_PICKER_COMPARATOR_TEMPLATE = EDIT + VIEW + "PickerComparator" + TEMPLATE;

    final String COMMON_VIEW_COMBO_BOX_LABEL_PROVIDER_TEMPLATE = COMMON + VIEW
            + "ComboBoxLabelProvider" + TEMPLATE;

    final String COMMON_VIEW_COMBO_BOX_CONTENT_PROVIDER_TEMPLATE = COMMON + VIEW
            + "ComboBoxContentProvider" + TEMPLATE;

    final String COMMON_VIEW_COMBO_BOX_HANDLER_TEMPLATE = COMMON + VIEW + "ComboBoxHandler"
            + TEMPLATE;

    final String BROWSER_VIEW_ELEMENT_TEMPLATE = BROWSER + VIEW + "Element" + TEMPLATE;

    final String BROWSER_VIEW_ELEMENT_HANDLER_TEMPLATE = BROWSER + VIEW + "Element" + HANDLER
            + TEMPLATE;

    final String BROWSER_VIEW_LIST_ELEMENT_TEMPLATE = BROWSER + VIEW + LIST + "Element" + TEMPLATE;

    final String BROWSER_VIEW_LIST_ELEMENT_HANDLER_TEMPLATE = BROWSER + VIEW + LIST + "Element"
            + HANDLER + TEMPLATE;

    final String BROWSER_VIEW_HELPER_TEMPLATE = BROWSER + VIEW + "Helper" + TEMPLATE;

}
