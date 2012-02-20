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

/**
 * Grammar production:
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
public class EnumerationDeclaration implements Node {
   private Node parent;
   public AnnotationDeclaration annotationDeclaration;
   public NodeChoice nodeChoice;
   public NodeOptional nodeOptional;
   public NodeToken nodeToken;
   public NodeChoice nodeChoice1;
   public NodeToken nodeToken1;
   public NodeToken nodeToken2;
   public NodeToken nodeToken3;

   public EnumerationDeclaration(AnnotationDeclaration n0, NodeChoice n1, NodeOptional n2, NodeToken n3, NodeChoice n4, NodeToken n5, NodeToken n6, NodeToken n7) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeChoice = n1;
      if ( nodeChoice != null ) nodeChoice.setParent(this);
      nodeOptional = n2;
      if ( nodeOptional != null ) nodeOptional.setParent(this);
      nodeToken = n3;
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeChoice1 = n4;
      if ( nodeChoice1 != null ) nodeChoice1.setParent(this);
      nodeToken1 = n5;
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n6;
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
      nodeToken3 = n7;
      if ( nodeToken3 != null ) nodeToken3.setParent(this);
   }

   public EnumerationDeclaration(AnnotationDeclaration n0, NodeChoice n1, NodeOptional n2, NodeChoice n3, NodeToken n4, NodeToken n5) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeChoice = n1;
      if ( nodeChoice != null ) nodeChoice.setParent(this);
      nodeOptional = n2;
      if ( nodeOptional != null ) nodeOptional.setParent(this);
      nodeToken = new NodeToken("Enumeration");
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeChoice1 = n3;
      if ( nodeChoice1 != null ) nodeChoice1.setParent(this);
      nodeToken1 = n4;
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n5;
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
      nodeToken3 = new NodeToken(";");
      if ( nodeToken3 != null ) nodeToken3.setParent(this);
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
}

