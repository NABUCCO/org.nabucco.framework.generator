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
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class GJDepthFirst<R,A> implements GJVisitor<R,A> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public R visit(NodeList n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeListOptional n, A argu) {
      if ( n.present() ) {
         R _ret=null;
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
         }
         return _ret;
      }
      else
         return null;
   }

   public R visit(NodeOptional n, A argu) {
      if ( n.present() )
         return n.node.accept(this,argu);
      else
         return null;
   }

   public R visit(NodeSequence n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeToken n, A argu) { return null; }

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
   public R visit(NabuccoUnit n, A argu) {
      R _ret=null;
      n.packageDeclaration.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nabuccoStatement.accept(this, argu);
      n.nodeToken.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeToken -> &lt;PACKAGE&gt;
    * nodeToken1 -> &lt;PACKAGE_IDENTIFIER&gt;
    * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(PackageDeclaration n, A argu) {
      R _ret=null;
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeToken -> &lt;IMPORT&gt;
    * nodeToken1 -> &lt;QUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(ImportDeclaration n, A argu) {
      R _ret=null;
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeListOptional -> ( &lt;ANNOTATION&gt; )*
    * </PRE>
    */
   public R visit(AnnotationDeclaration n, A argu) {
      R _ret=null;
      n.nodeListOptional.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeToken -> &lt;EXTENDS&gt;
    * nodeChoice -> ( &lt;UNQUALIFIED_TYPE_NAME&gt; | &lt;QUALIFIED_TYPE_NAME&gt; )
    * </PRE>
    */
   public R visit(ExtensionDeclaration n, A argu) {
      R _ret=null;
      n.nodeToken.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeChoice -> ( ApplicationStatement() | ComponentStatement() | AdapterStatement() | DatatypeStatement() | BasetypeStatement() | EnumerationStatement() | ExceptionStatement() | ServiceStatement() | MessageStatement() | EditViewStatement() | ListViewStatement() | SearchViewStatement() | CommandStatement() )
    * </PRE>
    */
   public R visit(NabuccoStatement n, A argu) {
      R _ret=null;
      n.nodeChoice.accept(this, argu);
      return _ret;
   }

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
   public R visit(ApplicationStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(ComponentStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(AdapterStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(DatatypeStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeOptional1.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(BasetypeStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(EnumerationStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(ServiceStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeOptional1.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(MessageStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(ExceptionStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(ConnectorStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(EditViewStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(ListViewStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(SearchViewStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(CommandStatement n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeListOptional.accept(this, argu);
      n.methodDeclaration.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeChoice -> ( ComponentDeclaration() | ConnectorStatement() )
    * </PRE>
    */
   public R visit(ApplicationPropertyDeclaration n, A argu) {
      R _ret=null;
      n.nodeChoice.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeChoice -> ( ComponentDatatypeDeclaration() | EnumerationDeclaration() | ServiceDeclaration() | ComponentDeclaration() )
    * </PRE>
    */
   public R visit(ComponentPropertyDeclaration n, A argu) {
      R _ret=null;
      n.nodeChoice.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeChoice -> ( CustomDeclaration() | ServiceDeclaration() | MethodDeclaration() )
    * </PRE>
    */
   public R visit(ServicePropertyDeclaration n, A argu) {
      R _ret=null;
      n.nodeChoice.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeChoice -> ( DatatypeDeclaration() | ServiceLinkDeclaration() )
    * </PRE>
    */
   public R visit(ConnectorPropertyDeclaration n, A argu) {
      R _ret=null;
      n.nodeChoice.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeChoice -> ( BasetypeDeclaration() | DatatypeDeclaration() | EnumerationDeclaration() )
    * </PRE>
    */
   public R visit(PropertyDeclaration n, A argu) {
      R _ret=null;
      n.nodeChoice.accept(this, argu);
      return _ret;
   }

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
   public R visit(CustomDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

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
   public R visit(BasetypeDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      return _ret;
   }

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
   public R visit(ComponentDatatypeDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeChoice1.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

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
   public R visit(ConnectorDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

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
   public R visit(ServiceLinkDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeToken4.accept(this, argu);
      n.nodeToken5.accept(this, argu);
      n.nodeToken6.accept(this, argu);
      n.nodeToken7.accept(this, argu);
      return _ret;
   }

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
   public R visit(DatatypeDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeChoice1.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

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
   public R visit(EnumerationDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeChoice1.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;CONSTANT_IDENTIFIER&gt;
    * </PRE>
    */
   public R visit(EnumerationLiteralDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;PARAMETER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(ExceptionParameterDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

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
   public R visit(ComponentDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
    * nodeToken -> &lt;SERVICE&gt;
    * nodeToken1 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(ServiceDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      return _ret;
   }

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
   public R visit(MethodDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.parameterList.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      n.nodeOptional.accept(this, argu);
      n.nodeChoice1.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeListOptional -> ( Parameter() )*
    * </PRE>
    */
   public R visit(ParameterList n, A argu) {
      R _ret=null;
      n.nodeListOptional.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * nodeOptional -> [ &lt;COMMA_CHAR&gt; ]
    * nodeToken -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken1 -> &lt;NAME_IDENTIFIER&gt;
    * </PRE>
    */
   public R visit(Parameter n, A argu) {
      R _ret=null;
      n.nodeOptional.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * block -> Block()
    * </PRE>
    */
   public R visit(MethodBody n, A argu) {
      R _ret=null;
      n.block.accept(this, argu);
      return _ret;
   }

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
   public R visit(ViewDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeChoice.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

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
   public R visit(WidgetDeclaration n, A argu) {
      R _ret=null;
      n.nodeChoice.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LABELED_INPUT_FIELD&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(LabeledInputFieldDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;INPUT_FIELD&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(InputFieldDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LABELED_PICKER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(LabeledPickerDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;PICKER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(PickerDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LABELED_LIST_PICKER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(LabeledListPickerDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LIST_PICKER&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(ListPickerDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;LABELED_COMBO_BOX&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(LabeledComboBoxDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;COMBO_BOX&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(ComboBoxDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;PRIVATE&gt;
    * nodeToken1 -> &lt;COLUMN&gt;
    * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
    * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public R visit(ColumnDeclaration n, A argu) {
      R _ret=null;
      n.annotationDeclaration.accept(this, argu);
      n.nodeToken.accept(this, argu);
      n.nodeToken1.accept(this, argu);
      n.nodeToken2.accept(this, argu);
      n.nodeToken3.accept(this, argu);
      return _ret;
   }

}
