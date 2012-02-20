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
package org.nabucco.framework.generator.parser.visitor;
import org.nabucco.framework.generator.parser.syntaxtree.*;
import java.util.*;

/**
 * All GJ void visitors must implement this interface.
 */

public interface GJVoidVisitor<A> {

   //
   // GJ void Auto class visitors
   //

   public void visit(NodeList n, A argu);
   public void visit(NodeListOptional n, A argu);
   public void visit(NodeOptional n, A argu);
   public void visit(NodeSequence n, A argu);
   public void visit(NodeToken n, A argu);

   //
   // User-generated visitor methods below
   //

   /**
    * <PRE>
    * packageDeclaration -> PackageDeclaration()
    * nodeListOptional -> ( ImportDeclaration() )*
    * nabuccoStatement -> NabuccoStatement()
    * nodeToken -> &lt;EOF&gt;
    * </PRE>
    */
   public void visit(NabuccoUnit n, A argu);

   /**
    * <PRE>
    * nodeToken -> &lt;PACKAGE&gt;
    * nodeToken1 -> &lt;PACKAGE_IDENTIFIER&gt;
    * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(PackageDeclaration n, A argu);

   /**
    * <PRE>
    * nodeToken -> &lt;IMPORT&gt;
    * nodeToken1 -> &lt;QUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ImportDeclaration n, A argu);

   /**
    * <PRE>
    * nodeListOptional -> ( &lt;ANNOTATION&gt; )*
    * </PRE>
    */
   public void visit(AnnotationDeclaration n, A argu);

   /**
    * <PRE>
    * nodeToken -> &lt;EXTENDS&gt;
    * nodeChoice -> ( &lt;UNQUALIFIED_TYPE_NAME&gt; | &lt;QUALIFIED_TYPE_NAME&gt; )
    * </PRE>
    */
   public void visit(ExtensionDeclaration n, A argu);

   /**
    * <PRE>
    * nodeChoice -> ( ApplicationStatement() | ComponentStatement() | AdapterStatement() | DatatypeStatement() | BasetypeStatement() | EnumerationStatement() | ExceptionStatement() | ServiceStatement() | MessageStatement() | EditViewStatement() | ListViewStatement() | SearchViewStatement() | CommandStatement() )
    * </PRE>
    */
   public void visit(NabuccoStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;APPLICATION&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( ApplicationPropertyDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(ApplicationStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;COMPONENT&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( ComponentPropertyDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(ComponentStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;ADAPTER&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( ServiceDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(AdapterStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeOptional -> [ &lt;ABSTRACT&gt; ]
    * nodeToken1 -> &lt;DATATYPE&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeOptional1 -> [ ExtensionDeclaration() ]
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( PropertyDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(DatatypeStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;BASETYPE&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeOptional -> [ ExtensionDeclaration() ]
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(BasetypeStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;ENUMERATION&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( EnumerationLiteralDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(EnumerationStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeOptional -> [ &lt;ABSTRACT&gt; ]
    * nodeToken1 -> &lt;SERVICE&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeOptional1 -> [ ExtensionDeclaration() ]
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( ServicePropertyDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(ServiceStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;MESSAGE&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( PropertyDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(MessageStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;EXCEPTION&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeOptional -> [ ExtensionDeclaration() ]
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( ExceptionParameterDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(ExceptionStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;CONNECTOR&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( ConnectorPropertyDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(ConnectorStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;EDITVIEW&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( DatatypeDeclaration() | WidgetDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(EditViewStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;LISTVIEW&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( DatatypeDeclaration() | ColumnDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(ListViewStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;SEARCHVIEW&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( DatatypeDeclaration() | WidgetDeclaration() )*
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(SearchViewStatement n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeToken1 -> &lt;COMMAND&gt;
    * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;LBRACE_CHAR&gt;
    * nodeListOptional -> ( ViewDeclaration() )*
    * methodDeclaration -> MethodDeclaration()
    * nodeToken4 -> &lt;RBRACE_CHAR&gt;
    * </PRE>
    */
   public void visit(CommandStatement n, A argu);

   /**
    * <PRE>
    * nodeChoice -> ( ComponentDeclaration() | ConnectorStatement() )
    * </PRE>
    */
   public void visit(ApplicationPropertyDeclaration n, A argu);

   /**
    * <PRE>
    * nodeChoice -> ( ComponentDatatypeDeclaration() | EnumerationDeclaration() | ServiceDeclaration() | ComponentDeclaration() )
    * </PRE>
    */
   public void visit(ComponentPropertyDeclaration n, A argu);

   /**
    * <PRE>
    * nodeChoice -> ( CustomDeclaration() | ServiceDeclaration() | MethodDeclaration() )
    * </PRE>
    */
   public void visit(ServicePropertyDeclaration n, A argu);

   /**
    * <PRE>
    * nodeChoice -> ( DatatypeDeclaration() | ServiceLinkDeclaration() )
    * </PRE>
    */
   public void visit(ConnectorPropertyDeclaration n, A argu);

   /**
    * <PRE>
    * nodeChoice -> ( BasetypeDeclaration() | DatatypeDeclaration() | EnumerationDeclaration() )
    * </PRE>
    */
   public void visit(PropertyDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeToken -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken1 -> &lt;MULTIPLICITY&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(CustomDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeOptional -> [ &lt;TRANSIENT&gt; ]
    * nodeToken -> &lt;BASETYPE&gt;
    * nodeToken1 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;MULTIPLICITY&gt;
    * nodeToken3 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken4 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(BasetypeDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeOptional -> [ &lt;PERSISTENT&gt; ]
    * nodeToken -> &lt;DATATYPE&gt;
    * nodeChoice1 -> ( &lt;QUALIFIED_TYPE_NAME&gt; | &lt;UNQUALIFIED_TYPE_NAME&gt; )
    * nodeToken1 -> &lt;MULTIPLICITY&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ComponentDatatypeDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeToken -> &lt;CONNECTOR&gt;
    * nodeToken1 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ConnectorDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;SERVICELINK&gt;
    * nodeToken2 -> &lt;QUALIFIED_TYPE_NAME&gt;
    * nodeToken3 -> &lt;DOT_CHAR&gt;
    * nodeToken4 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken5 -> &lt;LPAREN_CHAR&gt;
    * nodeToken6 -> &lt;RPAREN_CHAR&gt;
    * nodeToken7 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ServiceLinkDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeOptional -> [ &lt;TRANSIENT&gt; ]
    * nodeToken -> &lt;DATATYPE&gt;
    * nodeChoice1 -> ( &lt;QUALIFIED_TYPE_NAME&gt; | &lt;UNQUALIFIED_TYPE_NAME&gt; )
    * nodeToken1 -> &lt;MULTIPLICITY&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(DatatypeDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeOptional -> [ &lt;TRANSIENT&gt; ]
    * nodeToken -> &lt;ENUMERATION&gt;
    * nodeChoice1 -> ( &lt;QUALIFIED_TYPE_NAME&gt; | &lt;UNQUALIFIED_TYPE_NAME&gt; )
    * nodeToken1 -> &lt;MULTIPLICITY&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(EnumerationDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;CONSTANT_IDENTIFIER&gt;
    * </PRE>
    */
   public void visit(EnumerationLiteralDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;PARAMETER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ExceptionParameterDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeToken -> &lt;COMPONENT&gt;
    * nodeToken1 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ComponentDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeToken -> &lt;SERVICE&gt;
    * nodeToken1 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ServiceDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PUBLIC&gt;
    * nodeChoice -> ( &lt;VOID&gt; | &lt;UNQUALIFIED_TYPE_NAME&gt; )
    * nodeToken1 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken2 -> &lt;LPAREN_CHAR&gt;
    * parameterList -> ParameterList()
    * nodeToken3 -> &lt;RPAREN_CHAR&gt;
    * nodeOptional -> [ &lt;THROWS&gt; &lt;UNQUALIFIED_TYPE_NAME&gt; ]
    * nodeChoice1 -> ( &lt;SEMICOLON_CHAR&gt; | &lt;LBRACE_CHAR&gt; MethodBody() &lt;RBRACE_CHAR&gt; )
    * </PRE>
    */
   public void visit(MethodDeclaration n, A argu);

   /**
    * <PRE>
    * nodeListOptional -> ( Parameter() )*
    * </PRE>
    */
   public void visit(ParameterList n, A argu);

   /**
    * <PRE>
    * nodeOptional -> [ &lt;COMMA_CHAR&gt; ]
    * nodeToken -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken1 -> &lt;NAME_IDENTIFIER&gt;
    * </PRE>
    */
   public void visit(Parameter n, A argu);

   /**
    * <PRE>
    * block -> Block()
    * </PRE>
    */
   public void visit(MethodBody n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeChoice -> ( &lt;EDITVIEW&gt; | &lt;LISTVIEW&gt; | &lt;SEARCHVIEW&gt; )
    * nodeToken1 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ViewDeclaration n, A argu);

   /**
    * <PRE>
    * nodeChoice -> LabeledInputFieldDeclaration()
    *       | InputFieldDeclaration()
    *       | LabeledPickerDeclaration()
    *       | PickerDeclaration()
    *       | LabeledListPickerDeclaration()
    *       | ListPickerDeclaration()
    *       | LabeledComboBoxDeclaration()
    *       | ComboBoxDeclaration()
    * </PRE>
    */
   public void visit(WidgetDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LABELED_INPUT_FIELD&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(LabeledInputFieldDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;INPUT_FIELD&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(InputFieldDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LABELED_PICKER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(LabeledPickerDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;PICKER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(PickerDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LABELED_LIST_PICKER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(LabeledListPickerDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LIST_PICKER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ListPickerDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LABELED_COMBO_BOX&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(LabeledComboBoxDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;COMBO_BOX&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ComboBoxDeclaration n, A argu);

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;COLUMN&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ColumnDeclaration n, A argu);

}

