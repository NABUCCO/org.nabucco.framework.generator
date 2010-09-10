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
package org.nabucco.framework.generator.compiler.transformation.java.constants;

/**
 * ViewConstants
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public interface ViewConstants extends JavaConstants {

    final String VIEW = "View";

    final String FORM = "Form";

    final String MODEL = "Model";

    final String READ_ONLY = "ReadOnly";

    final String SEARCH_PACKAGE = "search";

    final String SEARCH = "Search";

    final String MSG = "Msg";

    final String ELEMENT = "Element";

    final String LIST = "List";

    final String ARGUMENT_FIRST = "first";

    final String ARGUMENT_SECOND = "second";

    final String BROWSER = "Browser";

    final String BROWSER_HANDLER_FIELD = "browserHandler";

    final String BROWSER_HANDLER = BROWSER + HANDLER;

    final String BROWSER_ELEMENT = BROWSER + ELEMENT;

    final String BROWSER_VIEW_ELEMENT = BROWSER + VIEW + ELEMENT;

    final String BROWSER_VIEW_LIST_ELEMENT = BROWSER + VIEW + LIST + ELEMENT;

    final String BROWSER_LIST_ELEMENT = BROWSER + LIST + ELEMENT;

    final String COMBO_BOX_HANDLER = "ComboBox" + HANDLER;

    final String COMMUNICATION = "communication";

    final String COMPARATOR = "Comparator";

    final String COMPARE = "compare";

    final String CONCRETE = "Concrete";

    final String COMPARE_TO = COMPARE + "To";

    final String COMPOSITE = "Composite";

    final String CONTAINS = "contains";

    final String CONTENT_PROVIDER = "ContentProvider";

    final String CONTENT_PROVIDER_HANDLER = "ContentProviderHandler";

    final String ELEMENT_PICKER = "ElementPicker";

    final String LIST_PICKER = "ListPicker";

    final String LIST_PICKER_COMPOSITE = LIST_PICKER + COMPOSITE;

    final String CREATE = "create";

    final String CREATE_CHILDREN = CREATE + "Children";

    final String REMOVE_CHIILD = "removeChild";

    final String CREATE_CHILDREN_LIST = CREATE + "Children" + LIST;

    final String CREATE_DATATYPE = CREATE + "Datatype";

    final String CREATE_ELEMENT_COMBO = CREATE + "ElementCombo";

    final String CREATE_ELEMENT_PICKER = CREATE + ELEMENT_PICKER;

    final String CREATE_LIST_PICKER = CREATE + LIST_PICKER;

    final String CREATE_FORM_CONTROL = CREATE + FORM + "Control";

    final String CREATE_INPUT_FIELD = CREATE + "InputField";

    final String CREATE_INPUT_FIELD_READ_ONLY = CREATE_INPUT_FIELD + READ_ONLY;

    final String CREATE_LABEL = CREATE + "Label";

    final String DATATYPE_BROWSER_HANDLER_FIELD = "datatypeBrowserHandler";

    final String DATATYPE_FIELD = "datatype";

    final String EDIT_VIEW_MODEL = "EditViewModel";

    final String EDIT_VIEW_BROWSER_ELEMENT = "EditViewBrowserElement";

    final String EDIT_VIEW_BROWSER_ELEMENT_HANDLER = EDIT_VIEW_BROWSER_ELEMENT + HANDLER;

    final String DATATYPE_EDIT_VIEW_MODEL = DATATYPE + EDIT_VIEW_MODEL;

    final String DATATYPE_PACKAGE = "datatype";

    final String DATATYPE_STATE_MODIFIED = "DatatypeState.MODIFIED";

    final String DATATYPE_STATE_PERSISTENT = "DatatypeState.PERSISTENT";

    final String DOUBLE_BACKSLASH = "\\";

    final String ELEMENT_PICKER_PARAMETER = "ElementPickerParameter";

    final String ELEMENT_PICKER_COMBO_PARAMETER = "ElementPickerComboParameter";

    final String ELEMENT_SELECTED = "elementSelected";

    final String EMPTY_STRING = "";

    final String EQUALS = "equals";

    final String FACADE = "facade";

    final String FACADE_DATATYPE = FACADE + PKG_SEPARATOR + DATATYPE_PACKAGE;

    final String FIELD = "field";

    final String FIELD_SEPARATOR = DOUBLE_BACKSLASH + ".";

    final String FIELD_VALUE = "fieldValue";

    final String FILL_DATATYPE = "fill" + DATATYPE;

    final String GET = PREFIX_GETTER;

    final String GET_DATATYPE_STATE = GET + DATATYPE + "State";

    final String GET_ELEMENTS = GET + "Elements";

    final String GET_FIELD = GET + "Field";

    final String GET_FIELD_COMBO = GET_FIELD + "Combo";

    final String GET_FIELD_DATATYPE = GET_FIELD + DATATYPE;

    final String GET_FILTER = "getFilter";

    final String GET_ID = GET + "Id";

    final String GET_ID_UPPERCASE = GET + "ID";

    final String GET_MODEL = GET + MODEL;

    final String GET_SEARCH = GET + SEARCH;

    final String GET_SHORT_NAME = GET + "ShortName";

    final String GET_TEXT = GET + "Text";

    final String GET_VALUE = GET + "Value";

    final String GET_VALUES = GET + "Values";

    final String GET_VIEW_MODEL = GET + "ViewModel";

    final String HANDLER_INSTANCE = "handler";

    final String ID = "ID";

    final String INIT_VALUES = "initValues";

    final String LABEL = "LABEL";

    final String LABEL_PROVIDER = "LabelProvider";

    final String LAYOUT = "layout";

    final String LAYOUTER = "Layouter";

    final String LOAD_ALL = "loadAll";

    final String LOAD_ALL_DATATYPES = LOAD_ALL + "Datatypes";

    final String LOAD_FULL = "loadFull";

    final String LIST_MSG = LIST + MSG;

    final String LIST_VIEW_BROWSER_ELEMENT = "ListViewBrowserElement";

    final String LIST_VIEW_BROWSER_ELEMENT_HANDLER = "ListViewBrowserElementHandler";

    final String LIST_VIEW_BROWSER_ELEMENT_HANDLER_FIELD = "listViewBrowserElementHandler";

    final String LIST_VIEW_MODEL = LIST + VIEW + MODEL;

    final String MESSAGE_PACKAGE = "message";

    final String MESSAGE_UPPERCASE = "MESSAGE";

    final String MESSAGE_COMBO = "MESSAGE_COMBO";

    final String MESSAGE_SEARCH = MESSAGE_PACKAGE + PKG_SEPARATOR + SEARCH_PACKAGE;

    final String MESSAGE_TABLE = "MESSAGE_TABLE";

    final String MODEL_FIELD = "model";

    final String MODEL_PACKAGE = "model";

    final String NABUCCO_FORM_TOOLKIT = "NabuccoFormToolkit";

    final String NABUCCO_MESSAGE_MANAGER = "NabuccoMessageManager";

    final String NEW = "new";

    final String NEW_VALUE = "newValue";

    final String NULL = "null";

    final String OBJECT = "Object";

    final String OBSERVE_VALUE = "OBSERVE_VALUE";

    final String OLD_VALUE = "oldValue";

    final String PATH_LABEL = "PATH_LABEL";

    final String PICKER_CONTENT_PROVIDER = "PickerContentProvider";

    final String PICKER_CONTENT_PROVIDER_HANDLER = PICKER_CONTENT_PROVIDER + HANDLER;

    final String PICKER_HANDLER = "PickerHandler";

    final String PROPERTY = "PROPERTY";

    final String RESULT = "result";

    final String SEARCH_FILTER = "searchFilter";

    final String SEARCH_METHOD = "search";

    final String SEARCH_MODEL = "SearchModel";

    final String SEARCH_MSG = SEARCH + MSG;

    final String SELECT = "select";

    final String SELECTION_EVENT = "SelectionEvent";

    final String SERVICE_DELEGATE = SERVICE + DELEGATE;

    final String SERVICE_DELEGATE_FACTORY = SERVICE_DELEGATE + FACTORY;

    final String SET_DATATYPE = PREFIX_SETTER + DATATYPE;

    final String SET_DATATYPE_DATATYPE = PREFIX_SETTER + DATATYPE + DATATYPE;

    final String SET_DATATYPE_STATE = SET_DATATYPE + "State";

    final String SET_FIELD = PREFIX_SETTER + "Field";

    final String SET_FIELD_COMBO = SET_FIELD + "Combo";

    final String SET_FIELD_DATATYPE = SET_FIELD + DATATYPE;

    final String SET_DATATYPE_SET = PREFIX_SETTER + DATATYPE + "Set";

    final String SET_VALUE = PREFIX_SETTER + "Value";

    final String SET_VIEW_MODEL = PREFIX_SETTER + "ViewModel";

    final String SHELL_TITLE = "SHELL_TITLE";

    final String STRING = "String";

    final String TEMPLATE_METHOD = "templateMethod";

    final String TEMPLATE_SEARCH_MODEL = "TemplateSearchModel";

    final String THIS = "this";

    final String TITLE = "TITLE";

    final String TO_STRING = "to" + STRING;

    final String TYPED_EVENT = "TypedEvent";

    final String UI = "ui";

    final String UI_RCP = UI + "." + "rcp";

    final String UI_WEB = UI + "." + "web";

    final String UI_RCP_COMMUNICATION = UI_RCP + "." + COMMUNICATION;

    final String UI_WEB_COMMUNICATION = UI_WEB + "." + COMMUNICATION;

    final String UI_RCP_COMMUNICATION_SEARCH = UI_RCP_COMMUNICATION
            + PKG_SEPARATOR + SEARCH_PACKAGE;

    final String UNDERSCORE = "_";

    final String UPDATE = "update";

    final String UPDATE_PROPERTY = UPDATE + "Property";

    final String VALUES = "values";

    final String VIEW_PACKAGE = "view";

    final String COMPARATOR_PACKAGE = "comparator";

    final String LABEL_PACKAGE = "label";

    final String VIEWER = "Viewer";

    final String VIEW_MODEL_FIELD = "viewModel";

    final String WIDGET_FACTORY = "WidgetFactory";

    final String WIDGET_DEFAULT_SELECTED = "widgetDefaultSelected";

    final String WIDGET_SELECTED = "widgetSelected";
}
