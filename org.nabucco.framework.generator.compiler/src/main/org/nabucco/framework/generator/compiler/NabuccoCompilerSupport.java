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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.nabucco.framework.generator.compiler.component.NabuccoComponentConstants;

/**
 * NabuccoCompilerSupport
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public final class NabuccoCompilerSupport implements NabuccoComponentConstants {

    /** Regex pattern to extract the root component name. */
    private static final Pattern PATTERN = Pattern.compile("\\.(facade|impl|ui|server)");

    /** FileFilter to filter for .metadata directories. */
    private static final FileFilter METADATA_FILEFILTER = new MetadataFileFilter();

    /**
     * Private constructor.
     */
    private NabuccoCompilerSupport() {
        throw new IllegalStateException("Private constructor must not be invoked.");
    }

    /**
     * Returns all root component names.
     * 
     * @param rootPath
     *            the component root directory
     * 
     * @return a list of all root component names
     */
    public static List<String> getParentComponentNames(String rootPath) {

        File workspaceRoot = getRootDirectory(rootPath);

        List<String> result = new ArrayList<String>();
        for (File file : workspaceRoot.listFiles()) {
            if (file.isDirectory() && isNbcDirectory(file)) {
                result.add(file.getName());
            }
        }
        return result;
    }

    /**
     * Searches the workspace root directory starting at the given file.
     * 
     * @param file
     *            the file to start search
     * 
     * @return the workspace root directory or null
     */
    public static File searchWorkspaceRoot(File file) {
        File result = file.getParentFile();
        while (result != null && !isWorkspaceRoot(result)) {
            result = result.getParentFile();
        }
        return result;
    }

    /**
     * Checks whether a file is the src/nbc/ directory.
     * 
     * @param file
     *            the file to validate.
     * 
     * @return <b>true</b> if the file is the NABUCCO source directory, <b>false</b> if not.
     */
    private static boolean isNbcDirectory(File file) {
        file = new File(file.getAbsolutePath() + "/src/nbc");
        return file.exists();
    }

    /**
     * check if a give file which is should be a directory is a eclipse workspace root. the
     * condition is the existance of an .metadata folder
     * 
     * @param f
     *            any given folder
     * @return <code>true</code> if .metadata folder exists as a child element.
     */
    private static boolean isWorkspaceRoot(File f) {
        return (f.listFiles(METADATA_FILEFILTER).length != 0);
    }

    /**
     * Returns the name of the parent component. E.g.
     * <code>org.nabucco.framework.base.facade.datatype</code> returns
     * <code>org.nabucco.framework.base</code>.
     * 
     * @param componentName
     *            the component name
     * 
     * @return the parent component
     */
    public static String getParentComponentName(String componentName) {
        if (componentName == null) {
            throw new IllegalArgumentException("Cannot resolve component name for [null].");
        }
        
        String[] tokens = PATTERN.split(componentName);

        if (tokens.length > 0) {
            return tokens[0];
        }
        return componentName;
    }

    /**
     * Checks whether a full qualified name/import is of component org.nabucco.framework.base.
     * 
     * @param importString
     *            the import string
     * 
     * @return <b>true</b> whether it is a basetype import, <b>false</b> if not.
     */
    public static boolean isBase(String importString) {
        if (importString == null) {
            return false;
        }
        return importString.startsWith(BASE);
    }

    /**
     * Checks whether an import is of another component or not. Except for element of
     * org.nabucco.framework.base.
     * 
     * @param pkg
     *            the current package
     * @param importString
     *            the import of the other element
     * 
     * @return <b>true</b> if the import points on another component, <b>false</b> if not
     */
    public static boolean isOtherComponent(String pkg, String importString) {
        String firstComponent = getParentComponentName(pkg);
        String secondComponent = getParentComponentName(importString);

        return !firstComponent.equalsIgnoreCase(secondComponent);
    }

    /**
     * Validates the path and returns the appropriate file.
     * 
     * @param rootPath
     *            the path to the root directory
     * 
     * @return the root directory
     */
    private static File getRootDirectory(String rootPath) {
        if (rootPath == null) {
            throw new IllegalArgumentException("Root path must be defined.");
        }

        File rootDir = new File(rootPath);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new IllegalArgumentException("Root path does not point on a valid directory.");
        }
        return rootDir;
    }

}
