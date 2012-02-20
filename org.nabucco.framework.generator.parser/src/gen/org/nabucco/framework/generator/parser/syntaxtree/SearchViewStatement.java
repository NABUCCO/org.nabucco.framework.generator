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
 * nodeToken1 -> &lt;SEARCHVIEW&gt;
 * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
 * nodeToken3 -> &lt;LBRACE_CHAR&gt;
 * nodeListOptional -> ( DatatypeDeclaration() | WidgetDeclaration() )*
 * nodeToken4 -> &lt;RBRACE_CHAR&gt;
 * </PRE>
 */
public class SearchViewStatement implements Node {
   private Node parent;
   public AnnotationDeclaration annotationDeclaration;
   public NodeToken nodeToken;
   public NodeToken nodeToken1;
   public NodeToken nodeToken2;
   public NodeToken nodeToken3;
   public NodeListOptional nodeListOptional;
   public NodeToken nodeToken4;

   public SearchViewStatement(AnnotationDeclaration n0, NodeToken n1, NodeToken n2, NodeToken n3, NodeToken n4, NodeListOptional n5, NodeToken n6) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeToken = n1;
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeToken1 = n2;
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n3;
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
      nodeToken3 = n4;
      if ( nodeToken3 != null ) nodeToken3.setParent(this);
      nodeListOptional = n5;
      if ( nodeListOptional != null ) nodeListOptional.setParent(this);
      nodeToken4 = n6;
      if ( nodeToken4 != null ) nodeToken4.setParent(this);
   }

   public SearchViewStatement(AnnotationDeclaration n0, NodeToken n1, NodeListOptional n2) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeToken = new NodeToken("public");
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeToken1 = new NodeToken("SearchView");
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n1;
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
      nodeToken3 = new NodeToken("{");
      if ( nodeToken3 != null ) nodeToken3.setParent(this);
      nodeListOptional = n2;
      if ( nodeListOptional != null ) nodeListOptional.setParent(this);
      nodeToken4 = new NodeToken("}");
      if ( nodeToken4 != null ) nodeToken4.setParent(this);
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

