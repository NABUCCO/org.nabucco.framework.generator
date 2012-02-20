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
 * A skeleton output formatter for your language grammar.  Using the
 * add() method along with force(), indent(), and outdent(), you can
 * easily specify how this visitor will format the given syntax tree.
 * See the JTB documentation for more details.
 *
 * Pass your syntax tree to this visitor, and then to the TreeDumper
 * visitor in order to "pretty print" your tree.
 */
public class TreeFormatter extends DepthFirstVisitor {
   private Vector<FormatCommand> cmdQueue = new Vector<FormatCommand>();
   private boolean lineWrap;
   private int wrapWidth;
   private int indentAmt;
   private int curLine = 1;
   private int curColumn = 1;
   private int curIndent = 0;

   /**
    * The default constructor assumes an indentation amount of 3 spaces
    * and no line-wrap.  You may alternately use the other constructor to
    * specify your own indentation amount and line width.
    */
   public TreeFormatter() { this(3, 0); }

   /**
    * This constructor accepts an indent amount and a line width which is
    * used to wrap long lines.  If a token's beginColumn value is greater
    * than the specified wrapWidth, it will be moved to the next line and
    * indented one extra level.  To turn off line-wrapping, specify a
    * wrapWidth of 0.
    *
    * @param   indentAmt   Amount of spaces per indentation level.
    * @param   wrapWidth   Wrap lines longer than wrapWidth.  0 for no wrap.
    */
   public TreeFormatter(int indentAmt, int wrapWidth) {
      this.indentAmt = indentAmt;
      this.wrapWidth = wrapWidth;

      if ( wrapWidth > 0 )
         lineWrap = true;
      else
         lineWrap = false;
   }

   /**
    * Accepts a NodeListInterface object and performs an optional format
    * command between each node in the list (but not after the last node).
    */
   protected void processList(NodeListInterface n) {
      processList(n, null);
   }

   protected void processList(NodeListInterface n, FormatCommand cmd) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this);
         if ( cmd != null && e.hasMoreElements() )
            cmdQueue.addElement(cmd);
      }
   }

   /**
    * A Force command inserts a line break and indents the next line to
    * the current indentation level.  Use "add(force());".
    */
   protected FormatCommand force() { return force(1); }
   protected FormatCommand force(int i) {
      return new FormatCommand(FormatCommand.FORCE, i);
   }

   /**
    * An Indent command increases the indentation level by one (or a
    * user-specified amount).  Use "add(indent());".
    */
   protected FormatCommand indent() { return indent(1); }
   protected FormatCommand indent(int i) {
      return new FormatCommand(FormatCommand.INDENT, i);
   }

   /**
    * An Outdent command is the reverse of the Indent command: it reduces
    * the indentation level.  Use "add(outdent());".
    */
   protected FormatCommand outdent() { return outdent(1); }
   protected FormatCommand outdent(int i) {
      return new FormatCommand(FormatCommand.OUTDENT, i);
   }

   /**
    * A Space command simply adds one or a user-specified number of
    * spaces between tokens.  Use "add(space());".
    */
   protected FormatCommand space() { return space(1); }
   protected FormatCommand space(int i) {
      return new FormatCommand(FormatCommand.SPACE, i);
   }

   /**
    * Use this method to add FormatCommands to the command queue to be
    * executed when the next token in the tree is visited.
    */
   protected void add(FormatCommand cmd) {
      cmdQueue.addElement(cmd);
   }

   /**
    * Executes the commands waiting in the command queue, then inserts the
    * proper location information into the current NodeToken.
    *
    * If there are any special tokens preceding this token, they will be
    * given the current location information.  The token will follow on
    * the next line, at the proper indentation level.  If this is not the
    * behavior you want from special tokens, feel free to modify this
    * method.
    */
   public void visit(NodeToken n) {
      for ( Enumeration<FormatCommand> e = cmdQueue.elements(); e.hasMoreElements(); ) {
         FormatCommand cmd = e.nextElement();
         switch ( cmd.getCommand() ) {
         case FormatCommand.FORCE :
            curLine += cmd.getNumCommands();
            curColumn = curIndent + 1;
            break;
         case FormatCommand.INDENT :
            curIndent += indentAmt * cmd.getNumCommands();
            break;
         case FormatCommand.OUTDENT :
            if ( curIndent >= indentAmt )
               curIndent -= indentAmt * cmd.getNumCommands();
            break;
         case FormatCommand.SPACE :
            curColumn += cmd.getNumCommands();
            break;
         default :
            throw new TreeFormatterException(
               "Invalid value in command queue.");
         }
      }

      cmdQueue.removeAllElements();

      //
      // Handle all special tokens preceding this NodeToken
      //
      if ( n.numSpecials() > 0 )
         for ( Enumeration<NodeToken> e = n.specialTokens.elements();
               e.hasMoreElements(); ) {
            NodeToken special = e.nextElement();

            //
            // -Place the token.
            // -Move cursor to next line after the special token.
            // -Don't update curColumn--want to keep current indent level.
            //
            placeToken(special, curLine, curColumn);
            curLine = special.endLine + 1;
         }

      placeToken(n, curLine, curColumn);
      curLine = n.endLine;
      curColumn = n.endColumn;
   }

   /**
    * Inserts token location (beginLine, beginColumn, endLine, endColumn)
    * information into the NodeToken.  Takes into account line-wrap.
    * Does not update curLine and curColumn.
    */
   private void placeToken(NodeToken n, int line, int column) {
      int length = n.tokenImage.length();

      //
      // Find beginning of token.  Only line-wrap for single-line tokens
      //
      if ( !lineWrap || n.tokenImage.indexOf('\n') != -1 ||
           column + length <= wrapWidth )
         n.beginColumn = column;
      else {
         ++line;
         column = curIndent + indentAmt + 1;
         n.beginColumn = column;
      }

      n.beginLine = line;

      //
      // Find end of token; don't count \n if it's the last character
      //
      for ( int i = 0; i < length; ++i ) {
         if ( n.tokenImage.charAt(i) == '\n' && i < length - 1 ) {
            ++line;
            column = 1;
         }
         else
            ++column;
      }

      n.endLine = line;
      n.endColumn = column;
   }

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
   public void visit(NabuccoUnit n) {
      n.packageDeclaration.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nabuccoStatement.accept(this);
      n.nodeToken.accept(this);
   }

   /**
    * <PRE>
    * nodeToken -> &lt;PACKAGE&gt;
    * nodeToken1 -> &lt;PACKAGE_IDENTIFIER&gt;
    * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(PackageDeclaration n) {
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
   }

   /**
    * <PRE>
    * nodeToken -> &lt;IMPORT&gt;
    * nodeToken1 -> &lt;QUALIFIED_TYPE_NAME&gt;
    * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
    * </PRE>
    */
   public void visit(ImportDeclaration n) {
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
   }

   /**
    * <PRE>
    * nodeListOptional -> ( &lt;ANNOTATION&gt; )*
    * </PRE>
    */
   public void visit(AnnotationDeclaration n) {
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
   }

   /**
    * <PRE>
    * nodeToken -> &lt;EXTENDS&gt;
    * nodeChoice -> ( &lt;UNQUALIFIED_TYPE_NAME&gt; | &lt;QUALIFIED_TYPE_NAME&gt; )
    * </PRE>
    */
   public void visit(ExtensionDeclaration n) {
      n.nodeToken.accept(this);
      n.nodeChoice.accept(this);
   }

   /**
    * <PRE>
    * nodeChoice -> ( ApplicationStatement() | ComponentStatement() | AdapterStatement() | DatatypeStatement() | BasetypeStatement() | EnumerationStatement() | ExceptionStatement() | ServiceStatement() | MessageStatement() | EditViewStatement() | ListViewStatement() | SearchViewStatement() | CommandStatement() )
    * </PRE>
    */
   public void visit(NabuccoStatement n) {
      n.nodeChoice.accept(this);
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
   public void visit(ApplicationStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(ComponentStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(AdapterStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(DatatypeStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      if ( n.nodeOptional1.present() ) {
         n.nodeOptional1.accept(this);
      }
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(BasetypeStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken3.accept(this);
      n.nodeToken4.accept(this);
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
   public void visit(EnumerationStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(ServiceStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      if ( n.nodeOptional1.present() ) {
         n.nodeOptional1.accept(this);
      }
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(MessageStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(ExceptionStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(ConnectorStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(EditViewStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(ListViewStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(SearchViewStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.nodeToken4.accept(this);
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
   public void visit(CommandStatement n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
      n.methodDeclaration.accept(this);
      n.nodeToken4.accept(this);
   }

   /**
    * <PRE>
    * nodeChoice -> ( ComponentDeclaration() | ConnectorStatement() )
    * </PRE>
    */
   public void visit(ApplicationPropertyDeclaration n) {
      n.nodeChoice.accept(this);
   }

   /**
    * <PRE>
    * nodeChoice -> ( ComponentDatatypeDeclaration() | EnumerationDeclaration() | ServiceDeclaration() | ComponentDeclaration() )
    * </PRE>
    */
   public void visit(ComponentPropertyDeclaration n) {
      n.nodeChoice.accept(this);
   }

   /**
    * <PRE>
    * nodeChoice -> ( CustomDeclaration() | ServiceDeclaration() | MethodDeclaration() )
    * </PRE>
    */
   public void visit(ServicePropertyDeclaration n) {
      n.nodeChoice.accept(this);
   }

   /**
    * <PRE>
    * nodeChoice -> ( DatatypeDeclaration() | ServiceLinkDeclaration() )
    * </PRE>
    */
   public void visit(ConnectorPropertyDeclaration n) {
      n.nodeChoice.accept(this);
   }

   /**
    * <PRE>
    * nodeChoice -> ( BasetypeDeclaration() | DatatypeDeclaration() | EnumerationDeclaration() )
    * </PRE>
    */
   public void visit(PropertyDeclaration n) {
      n.nodeChoice.accept(this);
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
   public void visit(CustomDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeChoice.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(BasetypeDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeChoice.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      n.nodeToken4.accept(this);
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
   public void visit(ComponentDatatypeDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeChoice.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken.accept(this);
      n.nodeChoice1.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(ConnectorDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeChoice.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(ServiceLinkDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
      n.nodeToken4.accept(this);
      n.nodeToken5.accept(this);
      n.nodeToken6.accept(this);
      n.nodeToken7.accept(this);
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
   public void visit(DatatypeDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeChoice.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken.accept(this);
      n.nodeChoice1.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(EnumerationDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeChoice.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken.accept(this);
      n.nodeChoice1.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
   }

   /**
    * <PRE>
    * annotationDeclaration -> AnnotationDeclaration()
    * nodeToken -> &lt;CONSTANT_IDENTIFIER&gt;
    * </PRE>
    */
   public void visit(EnumerationLiteralDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
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
   public void visit(ExceptionParameterDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(ComponentDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeChoice.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(ServiceDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeChoice.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
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
   public void visit(MethodDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeChoice.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.parameterList.accept(this);
      n.nodeToken3.accept(this);
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeChoice1.accept(this);
   }

   /**
    * <PRE>
    * nodeListOptional -> ( Parameter() )*
    * </PRE>
    */
   public void visit(ParameterList n) {
      if ( n.nodeListOptional.present() ) {
         processList(n.nodeListOptional);
      }
   }

   /**
    * <PRE>
    * nodeOptional -> [ &lt;COMMA_CHAR&gt; ]
    * nodeToken -> &lt;UNQUALIFIED_TYPE_NAME&gt;
    * nodeToken1 -> &lt;NAME_IDENTIFIER&gt;
    * </PRE>
    */
   public void visit(Parameter n) {
      if ( n.nodeOptional.present() ) {
         n.nodeOptional.accept(this);
      }
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
   }

   /**
    * <PRE>
    * block -> Block()
    * </PRE>
    */
   public void visit(MethodBody n) {
      n.block.accept(this);
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
   public void visit(ViewDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeChoice.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(WidgetDeclaration n) {
      n.nodeChoice.accept(this);
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
   public void visit(LabeledInputFieldDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(InputFieldDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(LabeledPickerDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(PickerDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(LabeledListPickerDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(ListPickerDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(LabeledComboBoxDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(ComboBoxDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
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
   public void visit(ColumnDeclaration n) {
      n.annotationDeclaration.accept(this);
      n.nodeToken.accept(this);
      n.nodeToken1.accept(this);
      n.nodeToken2.accept(this);
      n.nodeToken3.accept(this);
   }

}

class FormatCommand {
   public static final int FORCE = 0;
   public static final int INDENT = 1;
   public static final int OUTDENT = 2;
   public static final int SPACE = 3;

   private int command;
   private int numCommands;

   FormatCommand(int command, int numCommands) {
      this.command = command;
      this.numCommands = numCommands;
   }

   public int getCommand()             { return command; }
   public int getNumCommands()         { return numCommands; }
   public void setCommand(int i)       { command = i; }
   public void setNumCommands(int i)   { numCommands = i; }
}

class TreeFormatterException extends RuntimeException {
   TreeFormatterException()         { super(); }
   TreeFormatterException(String s) { super(s); }
}
