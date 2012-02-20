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
 * NabuccoCompilerOptionsEnum
 * 
 * @author Leonid Agranovskiy, PRODYNA AG
 */
public enum NabuccoCompilerOptionType {

    /** Default Transformation Engine */
    ENGINE("ENGINE", "NBC"),

    /** Default Model Type */
    MODEL_TYPE("MODEL_TYPE", "NABUCCO"),

    /** Default Template Path */
    TEMPLATE_DIR("TEMPLATE_DIR", "../org.nabucco.framework.generator/conf/nbc/templates"),

    /** Default Java Formatter Properties */
    JAVA_FORMATTER_CONFIG("JAVA_FORMATTER_CONFIG", "../org.nabucco.framework.generator/conf/nbc/formatter.properties"),

    /** Generate Documentation */
    GEN_DOC("GEN_DOC", String.valueOf(Boolean.TRUE)),

    /** Generate JAVA */
    GEN_JAVA("GEN_JAVA", String.valueOf(Boolean.TRUE)),

    /** Generate XML */
    GEN_XML("GEN_XML", String.valueOf(Boolean.TRUE)),

    /** Disable Annotations Validation */
    DISABLE_DOC_VALIDATION("DISABLE_DOC_VALIDATION", String.valueOf(Boolean.FALSE)),

    /** Merge XML Fragmente */
    MERGE_FRAGMENTS("MERGE_FRAGMENTS", String.valueOf(Boolean.TRUE)),

    /** Default Serialization Root Directory */
    OUT_DIR("OUT_DIR", "out/ide/nbc"),

    /** Default Log Level */
    LOG_LEVEL("LOG_LEVEL", "INFO"),

    /** Default Verbosity */
    VERBOSE("VERBOSE", String.valueOf(Boolean.TRUE));

    private String key;

    private String defaultValue;

    public String getKey() {
        return this.key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    NabuccoCompilerOptionType(String value, String defaultValue) {
        this.key = value;
        this.defaultValue = defaultValue;

    }
}
