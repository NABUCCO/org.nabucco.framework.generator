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
package org.nabucco.framework.generator.compiler;

/**
 * NabuccoCompilerDefaults
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface NabuccoCompilerDefaults {

    final String NBC = "NABUCCO";
    
    final String TEMPLATE_PATH = "../org.nabucco.framework.generator/conf/nbc/templates";

    final String NBC_FORMATTER = "../org.nabucco.framework.generator/conf/nbc/formatter.properties";

    final String NBC_ENGINE = "NBC";

    final String NBC_ROOTDIR = "src/gen/";
    
    final String NBC_OUT_DIR = "out/nbc";

    final String LOG_LEVEL = "INFO";

}
