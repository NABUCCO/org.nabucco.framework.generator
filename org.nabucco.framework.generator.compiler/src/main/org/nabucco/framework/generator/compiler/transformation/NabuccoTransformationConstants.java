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
package org.nabucco.framework.generator.compiler.transformation;

import java.io.File;

/**
 * NabuccoTransformationConstants
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface NabuccoTransformationConstants extends NabuccoConstants {

    final String CLASS = "class";

    final String IMPLEMENTATION = "Impl";
    
    final String SOURCE_SRC = "src";
    
    final String SOURCE_NBC = "nbc";

    final String PKG_FACADE = "facade";

    final String PKG_IMPL = "impl";

    final String PKG_UI = "ui";

    final String PKG_RCP = "rcp";

    final String PKG_WEB = "web";

    final String PKG_BROWSER = "browser";

    final String PKG_EDIT = "edit";

    final String PKG_LIST = "list";

    final String PKG_SEPARATOR = ".";

    final String TABLE_SEPARATOR = "_";

    final String CONF = "conf";

    final String EMPTY = "";

    final String REF_ID = "RefId";

    final String REF_ID_TYPE = "Long";

    final String NBC_SOURCE_FOLDER = "src" + File.separator + "nbc" + File.separator;

}
