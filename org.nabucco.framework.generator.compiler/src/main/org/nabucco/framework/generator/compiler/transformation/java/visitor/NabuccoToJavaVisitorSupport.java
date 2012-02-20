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
package org.nabucco.framework.generator.compiler.transformation.java.visitor;

import java.io.File;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.JavaAstSupport;
import org.nabucco.framework.generator.compiler.transformation.java.common.ast.container.JavaAstContainter;
import org.nabucco.framework.generator.compiler.transformation.java.constants.JavaConstants;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;

/**
 * NabuccoToJavaPropertiesVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoToJavaVisitorSupport extends NabuccoToJavaVisitor implements JavaConstants {

    /**
     * Creates a new {@link NabuccoToJavaPropertiesVisitor} with an appropriate visitor context.
     * 
     * @param visitorContext
     *            the visitor context
     */
    public NabuccoToJavaVisitorSupport(NabuccoToJavaVisitorContext visitorContext) {
        super(visitorContext);
    }

    /**
     * Creates a super class declaration for a type.
     * 
     * @throws JavaModelException
     */
    protected void createSuperClass() throws JavaModelException {
        String nabuccoExtension = super.getVisitorContext().getNabuccoExtension();

        if (nabuccoExtension != null) {
            JavaAstContainter<TypeReference> superClass = JavaAstSupport.createSuperClass(nabuccoExtension);

            this.getVisitorContext().getContainerList().add(superClass);
        }

    }

    /**
     * Creates an interface declaration for a type with the given name. Uses the package of the
     * NABUCCO file.
     * 
     * @param interfaceName
     *            name of the interface
     * 
     * @throws JavaModelException
     */
    protected void createInterface(String interfaceName) throws JavaModelException {
        this.createInterface(interfaceName, super.resolveImport(interfaceName));
    }

    /**
     * Creates an interface declaration for a type with the given name and package.
     * 
     * @param interfaceName
     *            name of the interface
     * @param interfaceImport
     *            import of the interface
     * 
     * @throws JavaModelException
     */
    protected void createInterface(String interfaceName, String interfaceImport) throws JavaModelException {
        if (interfaceName != null) {
            JavaAstContainter<TypeReference> container = JavaAstSupport.createInterface(interfaceName);
            container.getImports().add(interfaceImport);
            this.getVisitorContext().getContainerList().add(container);
        }
    }

    /**
     * Getter for the source folder.
     * 
     * @return the source folder
     * 
     * @throws NabuccoVisitorException
     */
    protected String getSourceFolder() throws NabuccoVisitorException {
        StringBuilder sourceFolder = new StringBuilder();
        sourceFolder.append("src");
        sourceFolder.append(File.separatorChar);
        sourceFolder.append("main");
        sourceFolder.append(File.separatorChar);
        sourceFolder.append("gen");

        return sourceFolder.toString();
    }

    /**
     * Extracts and copies the java AST from the template map the visitor context and inserts it
     * into a {@link JavaCompilationUnit}.
     * 
     * @param template
     *            the template name
     * 
     * @return the extracted AST
     * 
     * @throws JavaTemplateException
     */
    protected JavaCompilationUnit extractAst(String template) throws JavaTemplateException {
        return super.getVisitorContext().getTemplate(template).extractModel().getUnitList().get(0);
    }

    /**
     * Removes an import from the import list.
     * 
     * @param unit
     *            the compilation unit
     * @param name
     *            the import name
     * 
     * @throws JavaModelException
     */
    protected void removeImport(CompilationUnitDeclaration unit, String name) throws JavaModelException {
        ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(name);
        JavaAstElementFactory.getInstance().getJavaAstUnit().removeImport(unit, importReference);
    }

}
