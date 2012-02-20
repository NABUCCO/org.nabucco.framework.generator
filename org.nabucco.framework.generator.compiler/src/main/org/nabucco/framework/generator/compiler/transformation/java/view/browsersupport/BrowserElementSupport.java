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
package org.nabucco.framework.generator.compiler.transformation.java.view.browsersupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.nabucco.framework.generator.compiler.constants.NabuccoJavaTemplateConstants;
import org.nabucco.framework.generator.compiler.transformation.common.annotation.NabuccoAnnotation;
import org.nabucco.framework.generator.compiler.transformation.java.constants.ViewConstants;
import org.nabucco.framework.generator.compiler.transformation.util.NabuccoTransformationUtility;
import org.nabucco.framework.mda.model.java.JavaCompilationUnit;
import org.nabucco.framework.mda.model.java.JavaModelException;
import org.nabucco.framework.mda.model.java.ast.element.JavaAstElementFactory;
import org.nabucco.framework.mda.model.java.ast.element.method.JavaAstMethodSignature;
import org.nabucco.framework.mda.model.java.ast.produce.JavaAstModelProducer;
import org.nabucco.framework.mda.template.java.JavaTemplateException;
import org.nabucco.framework.mda.template.java.extract.JavaAstExtractorFactory;
import org.nabucco.framework.mda.template.java.extract.JavaAstStatementExtractor;

/**
 * BrowserElementSupport
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class BrowserElementSupport implements ViewConstants {

    /**
     * Selects and modifies the method getValues and adds ifStatements for each basetype.
     * 
     * @deprecated not needed anymore since the view model changed.
     * @param datatypeName
     *            the name of the datatype in the view.
     * @param viewName
     *            the name of the view.
     * @param annotationDeclarationList
     *            the list of all mapped field annotation declarations.
     * @param type
     *            the type declaration.
     * @param helperUnit
     *            the java compilation unit for the helperTemplate.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     * @throws JavaTemplateException
     *             if an error occurred loading the template.
     */
    @Deprecated
    public static void addGetValuesStatements(String datatypeName, String viewName,
            List<NabuccoAnnotation> annotationDeclarationList, TypeDeclaration type, JavaCompilationUnit helperUnit)
            throws JavaModelException, JavaTemplateException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // select getValues()
        JavaAstMethodSignature signature = new JavaAstMethodSignature(GET_VALUES);
        MethodDeclaration getValues = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        // count how many statements are from the datatype
        int length = 0;
        for (NabuccoAnnotation annotationDeclaration : annotationDeclarationList) {
            if (annotationDeclaration.getValue().split(FIELD_SEPARATOR)[0].matches(datatypeName)) {
                length++;
            }
        }

        // copy the statements from getValues and add an if statement for each basetype property
        Statement[] statement = getValues.statements;
        int position = statement.length;
        int size = position + length;
        Statement[] newStatement = Arrays.copyOf(statement, size);

        // the original method has two statements
        // the first statement of the copy has to be the first of the original
        // the last statement of the copy has to be the second of the original
        // new statements are added between
        newStatement[size - 1] = statement[1];
        position--;

        for (NabuccoAnnotation annotationDeclaration : annotationDeclarationList) {

            if (annotationDeclaration.getValue().split(FIELD_SEPARATOR)[0].matches(datatypeName)) {

                // load the template
                TypeDeclaration helperType = helperUnit
                        .getType(NabuccoJavaTemplateConstants.BROWSER_VIEW_HELPER_TEMPLATE);

                // select helper method getValues
                JavaAstMethodSignature helperSignature = new JavaAstMethodSignature(GET_VALUES);
                MethodDeclaration getValuesHelper = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(
                        helperType, helperSignature);

                JavaAstStatementExtractor statementExtractor = JavaAstExtractorFactory.getInstance()
                        .getStatementExtractor();
                MessageSend messageSendStatement = (MessageSend) statementExtractor.extractStatement(
                        getValuesHelper.statements[0], getValuesHelper.scope);

                modifyGetValuesStatement(viewName, annotationDeclaration, messageSendStatement);

                // add the statement to the list of statements
                newStatement[position] = messageSendStatement;
                position++;
            }
        }

        getValues.statements = newStatement;
    }

    /**
     * Modifies the given statement for a property.
     * 
     * @param viewName
     *            the name of the view.
     * @param annotationDeclaration
     *            the mapped field of the property.
     * @param messageSendStatement
     *            the statement to modify.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    private static void modifyGetValuesStatement(String viewName, NabuccoAnnotation annotationDeclaration,
            MessageSend messageSendStatement) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();

        String property = PROPERTY;
        String getProperty = GET;
        if (annotationDeclaration.getValue() != null) {
            for (int i = 0; i < annotationDeclaration.getValue().split(FIELD_SEPARATOR).length; i++) {
                String actual = annotationDeclaration.getValue().split(FIELD_SEPARATOR)[i];
                property = property + UNDERSCORE + actual.toUpperCase();
                getProperty = getProperty + NabuccoTransformationUtility.firstToUpper(actual);
            }
        }

        // change first argument
        QualifiedNameReference firstArgument = jamp.createQualifiedNameReference(viewName + MODEL, property);
        messageSendStatement.arguments[0] = firstArgument;

        // change second argument
        MessageSend secondArgument = (MessageSend) messageSendStatement.arguments[1];
        SingleNameReference receiver = (SingleNameReference) secondArgument.receiver;

        secondArgument = jamp.createMessageSend(getProperty, receiver, null);
        messageSendStatement.arguments[1] = secondArgument;

    }

    /**
     * Adds the import of a given String to the javaAstUnit.
     * 
     * @param importString
     *            the String with the import.
     * @param unit
     *            the java compilation unit where all imports are added to.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    public static void addImport(String importString, JavaCompilationUnit unit) throws JavaModelException {
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        ImportReference importReference = JavaAstModelProducer.getInstance().createImportReference(importString);
        javaFactory.getJavaAstUnit().addImport(unit.getUnitDeclaration(), importReference);
    }

    /**
     * Changes the constructor.
     * 
     * @param datatypeName
     *            the name of the datatype in the view.
     * @param datatype
     *            the name of the datatype type.
     * @param name
     *            the name of the constructor.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    public static void changeConstructor(String viewName, String datatypeName, String datatype, String name,
            TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // select the constructor
        JavaAstMethodSignature signature = new JavaAstMethodSignature(name, DATATYPE);
        ConstructorDeclaration constructor = javaFactory.getJavaAstType().getConstructor(type, signature);

        Argument parameter = constructor.arguments[0];

        // change type of parameter
        TypeReference typeReference = jamp.createTypeReference(datatype, false);
        parameter.type = typeReference;

        // change argument of first statement
        LocalDeclaration firstStatement = (LocalDeclaration) constructor.statements[0];
        MessageSend initialization = (MessageSend) firstStatement.initialization;
        ClassLiteralAccess browserElement = jamp.createClassLiteralAccess(datatype + EDIT_VIEW_BROWSER_ELEMENT);
        initialization.arguments[0] = browserElement;

        // change second statement
        Assignment secondStatement = (Assignment) constructor.statements[1];
        MessageSend secondStatementExpression = (MessageSend) secondStatement.expression;
        ClassLiteralAccess browserElementHandler = jamp.createClassLiteralAccess(datatype
                + EDIT_VIEW_BROWSER_ELEMENT_HANDLER);
        secondStatementExpression.arguments[0] = browserElementHandler;

        // change third statement
        Assignment thirdStatement = (Assignment) constructor.statements[2];
        TypeReference viewModelReference = jamp.createTypeReference(viewName + MODEL, false);
        AllocationExpression allocationExpression = jamp.createAllocationExpression(viewModelReference, null);
        thirdStatement.expression = allocationExpression;

        // change fourth statement
        SingleNameReference viewModel = jamp.createSingleNameReference(VIEW_MODEL_FIELD);
        SingleNameReference datatypeReference = jamp.createSingleNameReference(DATATYPE_FIELD);
        List<Expression> arguments = new ArrayList<Expression>();
        arguments.add(datatypeReference);
        MessageSend fourthStatement = jamp.createMessageSend(
                PREFIX_SETTER + NabuccoTransformationUtility.firstToUpper(datatypeName), viewModel, arguments);
        constructor.statements[3] = fourthStatement;

    }

    /**
     * Changes the method getViewModel().
     * 
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    public static void changeGetViewModel(String viewName, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // select method getViewModel()
        JavaAstMethodSignature signature = new JavaAstMethodSignature(GET_VIEW_MODEL);
        MethodDeclaration getViewModel = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        // change returnType
        TypeReference typeReference = jamp.createTypeReference(viewName + MODEL, false);
        getViewModel.returnType = typeReference;
    }

    /**
     * Changes the method setViewModel().
     * 
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    public static void changeSetViewModel(String viewName, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // select method getViewModel()
        JavaAstMethodSignature signature = new JavaAstMethodSignature(SET_VIEW_MODEL, EDIT_VIEW_MODEL);
        MethodDeclaration getViewModel = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        // change type of argument
        TypeReference typeReference = jamp.createTypeReference(viewName + MODEL, false);
        getViewModel.arguments[0].type = typeReference;
    }

    /**
     * Changes the setter.
     * 
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    public static void changeSetter(String datatype, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String datatypeName = NabuccoTransformationUtility.firstToLower(datatype);

        // select method setDatatype()
        JavaAstMethodSignature signature = new JavaAstMethodSignature(PREFIX_SETTER + DATATYPE, DATATYPE);
        MethodDeclaration setter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        // change name
        setter.selector = (PREFIX_SETTER + datatype).toCharArray();

        // change the name of the argument
        setter.arguments[0].name = datatypeName.toCharArray();

        // change the type of the argument
        TypeReference typeReference = jamp.createTypeReference(datatype, false);
        setter.arguments[0].type = typeReference;

        // change first statement
        SingleNameReference nameReference = jamp.createSingleNameReference(datatypeName);
        FieldReference fieldReference = jamp.createFieldThisReference(datatypeName);
        Assignment statement = (Assignment) setter.statements[0];
        statement.expression = nameReference;
        statement.lhs = fieldReference;
    }

    /**
     * Changes the getter.
     * 
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    public static void changeGetter(String datatype, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        String datatypeName = NabuccoTransformationUtility.firstToLower(datatype);

        // select method getDatatype()
        JavaAstMethodSignature signature = new JavaAstMethodSignature(GET + DATATYPE);
        MethodDeclaration getter = (MethodDeclaration) javaFactory.getJavaAstType().getMethod(type, signature);

        // change returnType
        TypeReference typeReference = jamp.createTypeReference(datatype, false);
        getter.returnType = typeReference;

        // change name
        getter.selector = (GET + datatype).toCharArray();

        // change the expression of the return statement
        SingleNameReference nameReference = jamp.createSingleNameReference(datatypeName);
        ReturnStatement returnStatement = (ReturnStatement) getter.statements[0];
        returnStatement.expression = nameReference;
    }

    /**
     * Changes the local field view model.
     * 
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    public static void changeFieldViewModel(String viewName, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // select field datatype
        FieldDeclaration datatypeField = javaFactory.getJavaAstType().getField(type, VIEW_MODEL_FIELD);

        // change type
        TypeReference reference = jamp.createTypeReference(viewName + MODEL, false);
        datatypeField.type = reference;
    }

    /**
     * Changes the local field browser handler.
     * 
     * @param datatype
     *            the name of the datatype.
     * @param type
     *            the type declaration.
     * @throws JavaModelException
     *             if an error occurred transforming the model.
     */
    public static void changeFieldBrowserHandler(String datatype, TypeDeclaration type) throws JavaModelException {
        JavaAstModelProducer jamp = JavaAstModelProducer.getInstance();
        JavaAstElementFactory javaFactory = JavaAstElementFactory.getInstance();

        // select field datatype
        FieldDeclaration datatypeField = javaFactory.getJavaAstType().getField(type, BROWSER_HANDLER_FIELD);

        // change type
        TypeReference reference = jamp.createTypeReference(datatype + EDIT_VIEW_BROWSER_ELEMENT_HANDLER, false);
        datatypeField.type = reference;
    }
}
