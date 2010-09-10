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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * NabuccoCompilerOptions
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class NabuccoCompilerOptions {

    public static final String ENGINE = "ENGINE";

    public static final String MODEL_TYPE = "MODEL_TYPE";

    public static final String TEMPLATE_DIR = "TEMPLATE_DIR";

    public static final String JAVA_FORMATTER_CONFIG = "JAVA_FORMATTER_CONFIG";

    public static final String GEN_JAVA = "GEN_JAVA";

    public static final String GEN_XML = "GEN_XML";

    public static final String MERGE_FRAGMENTS = "MERGE_FRAGMENTS";

    public static final String OUT_DIR = "OUT_DIR";

    private Properties properties;

    /**
     * Creates compiler options with default NABUCCO behavior.
     * 
     * @return the default NABUCCO compiler options
     */
    public static NabuccoCompilerOptions getDefaultOptions() {
        NabuccoCompilerOptions options = new NabuccoCompilerOptions();
        return options;
    }

    /**
     * Creates compiler options with default NABUCCO behavior.
     */
    public NabuccoCompilerOptions() {
        this.properties = new Properties(this.defaultOptions());
    }

    /**
     * Creates compiler options cloning existing compiler options.
     * 
     * @param options
     *            the existing compiler options
     */
    public NabuccoCompilerOptions(NabuccoCompilerOptions options) {
        this();

        if (options == null) {
            throw new IllegalArgumentException("Compiler Options must be defined.");
        }

        this.properties.putAll(options.properties);
    }

    /**
     * Creates compiler options depending on properties map.
     * 
     * @param properties
     *            the properties map containing compiler options
     */
    public NabuccoCompilerOptions(Properties properties) {
        this();

        if (properties == null) {
            throw new IllegalArgumentException("Properties map must be defined.");
        }

        this.properties.putAll(properties);
    }

    /**
     * Creates compiler options depending on a properties file.
     * 
     * @param file
     *            the properties file containing compiler options
     * 
     * @throws IOException
     *             when the file does not exist or cannot be parsed for properties
     */
    public NabuccoCompilerOptions(File file) throws IOException {
        this();

        if (file == null) {
            throw new IllegalArgumentException("Properties file must be defined.");
        }

        this.properties.load(new FileInputStream(file));
    }

    /**
     * Creates compiler options depending on a properties file.
     * 
     * @param inputStream
     *            the properties as stream containing compiler options
     * 
     * @throws IOException
     *             when the properties cannot be loaded from the stream
     */
    public NabuccoCompilerOptions(InputStream inputStream) throws IOException {
        this();

        if (inputStream == null) {
            throw new IllegalArgumentException("Properties stream must be defined.");
        }

        this.properties.load(inputStream);
    }

    /**
     * Gets a compiler option of the option map.
     * 
     * @param key
     *            the option key
     *            
     * @return the option value
     */
    public String getOption(String key) {
        String option = this.properties.getProperty(key);
        
        if (option == null) {
            throw new IllegalArgumentException("Key is not a compiler option '" + key + "'.");
        }
        
        return option;
    }

    /**
     * Adds a compiler option to the option map.
     * 
     * @param key
     *            the option key
     * @param value
     *            the option value
     */
    public void setOption(String key, String value) {
        this.properties.setProperty(key, value);
    }

    /**
     * Initialize default NABUCCO compiler properties.
     * 
     * @return the default properties
     */
    private Properties defaultOptions() {

        Properties properties = new Properties();
        properties.setProperty(MODEL_TYPE, NabuccoCompilerDefaults.NBC);
        properties.setProperty(ENGINE, NabuccoCompilerDefaults.NBC_ENGINE);
        properties.setProperty(JAVA_FORMATTER_CONFIG, NabuccoCompilerDefaults.NBC_FORMATTER);
        properties.setProperty(TEMPLATE_DIR, NabuccoCompilerDefaults.TEMPLATE_PATH);
        properties.setProperty(GEN_JAVA, String.valueOf(Boolean.TRUE));
        properties.setProperty(GEN_XML, String.valueOf(Boolean.TRUE));
        properties.setProperty(MERGE_FRAGMENTS, String.valueOf(Boolean.TRUE));
        properties.setProperty(OUT_DIR, NabuccoCompilerDefaults.NBC_OUT_DIR);

        // Add other default properties here.

        return properties;
    }
}
