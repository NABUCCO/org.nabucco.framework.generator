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
package org.nabucco.framework.generator.compiler;

/**
 * NabuccoCompilerDefaults
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface NabuccoCompilerDefaults {

    /** Default Model Type */
    final String NBC = "NABUCCO";

    /** Default Template Path */
    final String TEMPLATE_PATH = "../org.nabucco.framework.generator/conf/nbc/templates";

    /** Default Java Formatter Properties */
    final String NBC_FORMATTER = "../org.nabucco.framework.generator/conf/nbc/formatter.properties";

    /** Default Transformation Engine */
    final String NBC_ENGINE = "NBC";

    /** Default Generation Root Directory */
    final String NBC_ROOTDIR = "src/gen/";

    /** Default Serialization Root Directory */
    final String NBC_OUT_DIR = "out/ide/nbc";

    /** Default Log Level */
    final String LOG_LEVEL = "INFO";

    /** Default Verbosity */
    final String VERBOSE = "true";

    /** Generate Documentation */
    final String GEN_DOC = String.valueOf(Boolean.TRUE);

    /** Generate Java */
    final String GEN_JAVA = String.valueOf(Boolean.TRUE);

    /** Generate XML */
    final String GEN_XML = String.valueOf(Boolean.TRUE);

    /** Merge XML Fragmente */
    final String MERGE_FRAGMENTS = String.valueOf(Boolean.TRUE);
}
