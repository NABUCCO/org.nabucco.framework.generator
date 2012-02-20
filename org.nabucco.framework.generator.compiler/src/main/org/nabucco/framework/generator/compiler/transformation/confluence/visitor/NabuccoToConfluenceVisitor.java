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
package org.nabucco.framework.generator.compiler.transformation.confluence.visitor;

import org.nabucco.framework.generator.compiler.NabuccoCompilerSupport;
import org.nabucco.framework.generator.compiler.transformation.confluence.signature.NabuccoSignatureStatementVisitor;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitor;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorContext;
import org.nabucco.framework.generator.compiler.visitor.NabuccoVisitorException;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;
import org.nabucco.framework.generator.parser.model.modifier.NabuccoModifierType;
import org.nabucco.framework.generator.parser.syntaxtree.Node;
import org.nabucco.framework.mda.model.MdaModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModel;
import org.nabucco.framework.mda.model.text.confluence.ConfluenceModelException;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeading;
import org.nabucco.framework.mda.model.text.confluence.ast.heading.ConfluenceHeadingSize;
import org.nabucco.framework.mda.model.text.confluence.ast.link.ConfluenceExternalLink;
import org.nabucco.framework.mda.model.text.confluence.ast.page.ConfluencePage;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceText;
import org.nabucco.framework.mda.model.text.confluence.ast.text.ConfluenceTextEffect;

/**
 * NabuccoToConfluenceVisitor
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public abstract class NabuccoToConfluenceVisitor extends
        NabuccoVisitor<MdaModel<ConfluenceModel>, NabuccoVisitorContext> {

    private static final String JAVA_LINK_LABEL = "JAVA Source";

    private static final String NBC_LINK_LABEL = "NBC Source";

    protected ConfluencePage page;

    // TODO : Make the main url configurable over settings
    protected final static String GITHUB_LINK = "https://github.com/NABUCCO/";

    protected final static String GITHUB_CONNECTOR = "/blob/master/";

    protected final static String GITHUB_GEN = "/src/main/gen/";

    protected final static String GITHUB_NBC = "/src/nbc/";

    /**
     * Creates a new {@link NabuccoToConfluenceVisitor} instance.
     * 
     * @param visitorContext
     *            the confluence visitor context
     */
    public NabuccoToConfluenceVisitor(NabuccoToConfluenceVisitorContext visitorContext) {
        super(visitorContext);
    }

    @Override
    protected NabuccoToConfluenceVisitorContext getVisitorContext() {
        return (NabuccoToConfluenceVisitorContext) super.getVisitorContext();
    }

    /**
     * Add page heading
     * 
     * @param type
     *            type of the Element (Datatype, Basetype...)
     * @param datatypeName
     *            name of the element
     */
    protected void printPageHeader(NabuccoModelType type, String datatypeName) {
        // Add page heading
        ConfluenceHeading datatypePart = new ConfluenceHeading(ConfluenceHeadingSize.H2);
        datatypePart.addElement(new ConfluenceText(type.getId(), ConfluenceTextEffect.UNDERLINE));
        datatypePart.addElement(new ConfluenceText(datatypeName));
        this.page.addElement(datatypePart);

        // Add package
        String package_string = this.getVisitorContext().getPackage();
        ConfluenceText packageString = new ConfluenceText(package_string);
        this.page.addElement(packageString);


        // Add Github links
        String linkToNBCSource = this.getLinkToNBCSource(type, datatypeName);
        if (linkToNBCSource != null) {
            this.page.addElement(new ConfluenceText(ConfluenceTextEffect.NEWLINE));
            ConfluenceExternalLink nbcLink = new ConfluenceExternalLink(linkToNBCSource, NBC_LINK_LABEL);
            this.page.addElement(nbcLink);
        }

        String linkToJavaSource = this.getLinkToJavaSource(type, datatypeName);
        if (linkToJavaSource != null) {
            this.page.addElement(new ConfluenceText(ConfluenceTextEffect.NEWLINE));
            ConfluenceExternalLink javaLink = new ConfluenceExternalLink(linkToJavaSource, JAVA_LINK_LABEL);
            this.page.addElement(javaLink);
        }
    }

    /**
     * Getter for the generated java source.
     * 
     * If overridden then the link will appear in confluence.
     * 
     * @return source url or null if not needed
     */
    public String getLinkToJavaSource(NabuccoModelType type, String datatypeName) {
        NabuccoToConfluenceVisitorContext context = this.getVisitorContext();
        String packageString = context.getPackage();
        String package_path = packageString.replaceAll("\\.", "/");

        String mainProjectName = NabuccoCompilerSupport.getParentComponentName(packageString);
        String projectName = this.getProjectName(type, NabuccoModifierType.PUBLIC);

        String url = GITHUB_LINK;
        url += mainProjectName;
        url += GITHUB_CONNECTOR;
        url += projectName;
        url += GITHUB_GEN;
        url += package_path + "/";
        url += datatypeName + ".java";

        return url;
    }

    /**
     * Getter for the generated nbc source
     * 
     * @return source url or null if not needed
     */
    public String getLinkToNBCSource(NabuccoModelType type, String datatypeName) {
        NabuccoToConfluenceVisitorContext context = this.getVisitorContext();
        String packageString = context.getPackage();
        String package_path = packageString.replaceAll("\\.", "/");

        String mainProjectName = NabuccoCompilerSupport.getParentComponentName(packageString);

        String url = GITHUB_LINK;
        url += mainProjectName;
        url += GITHUB_CONNECTOR;
        url += mainProjectName;
        url += GITHUB_NBC;
        url += package_path + "/";
        url += datatypeName + ".nbc";

        return url;
    }
    /**
     * Print the original signature
     * 
     * @param node
     *            node that schould be printed out
     * @param target
     */
    protected void printOriginalSignature(Node node, MdaModel<ConfluenceModel> target) {
        // Print the original signature
        StringBuilder signatureBuilder = new StringBuilder();
        NabuccoSignatureStatementVisitor signatureVisitor = new NabuccoSignatureStatementVisitor(
                this.getVisitorContext(), signatureBuilder);
        node.accept(signatureVisitor, target);

        ConfluenceText signatureCode = new ConfluenceText(signatureBuilder.toString().trim(), ConfluenceTextEffect.CODE);
        this.page.addElement(signatureCode);
        this.page.addElement(new ConfluenceText(ConfluenceTextEffect.NEWLINE));
    }

    /**
     * Initialise the new page and add it to the model
     * 
     * @param pageName
     * @param target
     */
    protected void initPage(String pageName, String pageType, MdaModel<ConfluenceModel> target) {
        try {
            String projectName = NabuccoCompilerSupport.getParentComponentName(this.getProjectName(null, null));
            this.page = new ConfluencePage(pageName, projectName, pageType);
        } catch (ConfluenceModelException e) {
            throw new NabuccoVisitorException("Invalid page name", e);
        }

        target.getModel().getPages().add(this.page);
    }

    /**
     * Returns a name of the component for the element
     * 
     * @param element
     *            element to search for
     * @return component
     */
    public String findComponentToElement(String element) {
        String retVal = null;
        String importPath = resolveImport(element, this.getVisitorContext());
        retVal = NabuccoCompilerSupport.getParentComponentName(importPath);
        return retVal;
    }
}
