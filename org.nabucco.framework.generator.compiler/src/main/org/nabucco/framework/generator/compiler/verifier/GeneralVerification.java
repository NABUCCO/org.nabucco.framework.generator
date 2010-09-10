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
package org.nabucco.framework.generator.compiler.verifier;

import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotationMapper;
import org.nabucco.framework.generator.parser.model.NabuccoModel;
import org.nabucco.framework.generator.parser.syntaxtree.AnnotationDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.BasetypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.CommandStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ComponentStatement;
import org.nabucco.framework.generator.parser.syntaxtree.DatatypeStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EditViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.EnumerationStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ExceptionStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ListViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.MessageStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoStatement;
import org.nabucco.framework.generator.parser.syntaxtree.NabuccoUnit;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.generator.parser.syntaxtree.PackageDeclaration;
import org.nabucco.framework.generator.parser.syntaxtree.SearchViewStatement;
import org.nabucco.framework.generator.parser.syntaxtree.ServiceStatement;

import org.nabucco.framework.mda.model.MdaModel;

/**
 * GeneralVerficication
 * 
 * @author Silas Schwarz PRODYNA AG
 */
public class GeneralVerification extends NabuccoModelVerificationVisitor {

    private PackageDeclaration packageDeclaration = null;

    private String nabuccoTypeName = "undefined";

    // TODO: Validate Filename!

    @Override
    public void visit(NabuccoUnit nabuccoUnit, MdaModel<NabuccoModel> target) {
        super.visit(nabuccoUnit, target);
        String packageString = convertToPackageFormat(target.getModel().getPath());
        if (!packageString.endsWith(packageDeclaration.nodeToken1.tokenImage)) {
            logger.warning("Declared package missmatches the path in file: "
                    + target.getModel().getPath());
        }
        // filename == type name ?
        String typeNameInFile = getNbcFileName(target.getModel().getPath());
        if (!(typeNameInFile.compareTo(nabuccoTypeName) == 0)) {
            logger.error("Filename does not match NABUCCO Compilation Unit name");
            logger.error("In file definiton : " + nabuccoTypeName);
            logger.error("Filename : " + typeNameInFile);
        }
    }

    private String convertToPackageFormat(String path) {
        path = format(path);
        return path.substring(0, path.lastIndexOf('.'));
    }

    private String getNbcFileName(String path) {
        path = format(path);
        return path.substring(path.lastIndexOf('.') + 1);
    }

    private String format(String in) {
        return in.replace(".nbc", "").replace("\\", ".");
    }

    @Override
    public void visit(PackageDeclaration nabuccoPackage, MdaModel<NabuccoModel> target) {
        super.visit(nabuccoPackage, target);
        this.packageDeclaration = nabuccoPackage;
    }

    @Override
    public void visit(AnnotationDeclaration nabuccoAnnotation, MdaModel<NabuccoModel> target) {
        NabuccoAnnotationMapper.getInstance().mapToAnnotations(nabuccoAnnotation);
    }

    @Override
    public void visit(NabuccoStatement nabuccoStatement, MdaModel<NabuccoModel> target) {
        Node n = nabuccoStatement.nodeChoice.choice;
        if (n instanceof ComponentStatement) {
            nabuccoTypeName = ((ComponentStatement) n).nodeToken2.tokenImage;
        } else if (n instanceof DatatypeStatement) {
            nabuccoTypeName = ((DatatypeStatement) n).nodeToken2.tokenImage;
        } else if (n instanceof BasetypeStatement) {
            nabuccoTypeName = ((BasetypeStatement) n).nodeToken2.tokenImage;

        } else if (n instanceof EnumerationStatement) {
            nabuccoTypeName = ((EnumerationStatement) n).nodeToken2.tokenImage;

        } else if (n instanceof ExceptionStatement) {
            nabuccoTypeName = ((ExceptionStatement) n).nodeToken2.tokenImage;

        } else if (n instanceof ServiceStatement) {
            nabuccoTypeName = ((ServiceStatement) n).nodeToken2.tokenImage;

        } else if (n instanceof MessageStatement) {
            nabuccoTypeName = ((MessageStatement) n).nodeToken2.tokenImage;

        } else if (n instanceof EditViewStatement) {
            nabuccoTypeName = ((EditViewStatement) n).nodeToken2.tokenImage;

        } else if (n instanceof ListViewStatement) {
            nabuccoTypeName = ((ListViewStatement) n).nodeToken2.tokenImage;

        } else if (n instanceof SearchViewStatement) {
            nabuccoTypeName = ((SearchViewStatement) n).nodeToken2.tokenImage;

        } else if (n instanceof CommandStatement) {
            nabuccoTypeName = ((CommandStatement) n).nodeToken2.tokenImage;

        }
        super.visit(nabuccoStatement, target);
    }
}
