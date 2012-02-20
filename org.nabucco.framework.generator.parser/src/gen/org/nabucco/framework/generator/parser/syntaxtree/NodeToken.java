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
package org.nabucco.framework.generator.parser.syntaxtree;

import java.util.*;
/**
 * Represents a single token in the grammar.  If the "-tk" option
 * is used, also contains a Vector of preceding special tokens.
 */
public class NodeToken implements Node {
   public NodeToken(String s) {
      this(s, -1, -1, -1, -1, -1);    }

   public NodeToken(String s, int kind, int beginLine, int beginColumn, int endLine, int endColumn) {
      tokenImage = s;
      specialTokens = null;
      this.kind = kind;
      this.beginLine = beginLine;
      this.beginColumn = beginColumn;
      this.endLine = endLine;
      this.endColumn = endColumn;
   }

   public NodeToken getSpecialAt(int i) {
      if ( specialTokens == null )
         throw new java.util.NoSuchElementException("No specials in token");
      return specialTokens.elementAt(i);
   }

   public int numSpecials() {
      if ( specialTokens == null ) return 0;
      return specialTokens.size();
   }

   public void addSpecial(NodeToken s) {
      if ( specialTokens == null ) specialTokens = new Vector<NodeToken>();
      specialTokens.addElement(s);
      s.setParent(this);
   }

   public void trimSpecials() {
      if ( specialTokens == null ) return;
      specialTokens.trimToSize();
   }

   public String toString()     { return tokenImage; }

   public String withSpecials() {
      if ( specialTokens == null )
          return tokenImage;

       StringBuffer buf = new StringBuffer();

       for ( Enumeration<NodeToken> e = specialTokens.elements(); e.hasMoreElements(); )
          buf.append(e.nextElement().toString());

       buf.append(tokenImage);
       return buf.toString();
   }

   public void accept(org.nabucco.framework.generator.parser.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(org.nabucco.framework.generator.parser.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(org.nabucco.framework.generator.parser.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(org.nabucco.framework.generator.parser.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }

   public void setParent(Node n) { parent = n; }
   public Node getParent()       { return parent; }

   private Node parent;
   public String tokenImage;

   // Stores a list of NodeTokens
   public Vector<NodeToken> specialTokens;

   // -1 for these ints means no position info is available.
   public int beginLine, beginColumn, endLine, endColumn;

   // Equal to the JavaCC token "kind" integer.
   // -1 if not available.
   public int kind;
}

