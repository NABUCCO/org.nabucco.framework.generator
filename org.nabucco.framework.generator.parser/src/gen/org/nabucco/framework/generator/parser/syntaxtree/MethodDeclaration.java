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
public class MethodDeclaration implements Node {
   private Node parent;
   public AnnotationDeclaration annotationDeclaration;
   public NodeToken nodeToken;
   public NodeChoice nodeChoice;
   public NodeToken nodeToken1;
   public NodeToken nodeToken2;
   public ParameterList parameterList;
   public NodeToken nodeToken3;
   public NodeOptional nodeOptional;
   public NodeChoice nodeChoice1;

   public MethodDeclaration(AnnotationDeclaration n0, NodeToken n1, NodeChoice n2, NodeToken n3, NodeToken n4, ParameterList n5, NodeToken n6, NodeOptional n7, NodeChoice n8) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeToken = n1;
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeChoice = n2;
      if ( nodeChoice != null ) nodeChoice.setParent(this);
      nodeToken1 = n3;
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n4;
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
      parameterList = n5;
      if ( parameterList != null ) parameterList.setParent(this);
      nodeToken3 = n6;
      if ( nodeToken3 != null ) nodeToken3.setParent(this);
      nodeOptional = n7;
      if ( nodeOptional != null ) nodeOptional.setParent(this);
      nodeChoice1 = n8;
      if ( nodeChoice1 != null ) nodeChoice1.setParent(this);
   }

   public MethodDeclaration(AnnotationDeclaration n0, NodeChoice n1, NodeToken n2, ParameterList n3, NodeOptional n4, NodeChoice n5) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeToken = new NodeToken("public");
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeChoice = n1;
      if ( nodeChoice != null ) nodeChoice.setParent(this);
      nodeToken1 = n2;
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = new NodeToken("(");
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
      parameterList = n3;
      if ( parameterList != null ) parameterList.setParent(this);
      nodeToken3 = new NodeToken(")");
      if ( nodeToken3 != null ) nodeToken3.setParent(this);
      nodeOptional = n4;
      if ( nodeOptional != null ) nodeOptional.setParent(this);
      nodeChoice1 = n5;
      if ( nodeChoice1 != null ) nodeChoice1.setParent(this);
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

