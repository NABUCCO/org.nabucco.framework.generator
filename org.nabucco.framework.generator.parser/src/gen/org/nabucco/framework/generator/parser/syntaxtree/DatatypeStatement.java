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
 * nodeOptional -> [ &lt;ABSTRACT&gt; ]
 * nodeToken1 -> &lt;DATATYPE&gt;
 * nodeToken2 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
 * nodeOptional1 -> [ ExtensionDeclaration() ]
 * nodeToken3 -> &lt;LBRACE_CHAR&gt;
 * nodeListOptional -> ( PropertyDeclaration() )*
 * nodeToken4 -> &lt;RBRACE_CHAR&gt;
 * </PRE>
 */
public class DatatypeStatement implements Node {
   private Node parent;
   public AnnotationDeclaration annotationDeclaration;
   public NodeToken nodeToken;
   public NodeOptional nodeOptional;
   public NodeToken nodeToken1;
   public NodeToken nodeToken2;
   public NodeOptional nodeOptional1;
   public NodeToken nodeToken3;
   public NodeListOptional nodeListOptional;
   public NodeToken nodeToken4;

   public DatatypeStatement(AnnotationDeclaration n0, NodeToken n1, NodeOptional n2, NodeToken n3, NodeToken n4, NodeOptional n5, NodeToken n6, NodeListOptional n7, NodeToken n8) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeToken = n1;
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeOptional = n2;
      if ( nodeOptional != null ) nodeOptional.setParent(this);
      nodeToken1 = n3;
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n4;
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
      nodeOptional1 = n5;
      if ( nodeOptional1 != null ) nodeOptional1.setParent(this);
      nodeToken3 = n6;
      if ( nodeToken3 != null ) nodeToken3.setParent(this);
      nodeListOptional = n7;
      if ( nodeListOptional != null ) nodeListOptional.setParent(this);
      nodeToken4 = n8;
      if ( nodeToken4 != null ) nodeToken4.setParent(this);
   }

   public DatatypeStatement(AnnotationDeclaration n0, NodeOptional n1, NodeToken n2, NodeOptional n3, NodeListOptional n4) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeToken = new NodeToken("public");
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeOptional = n1;
      if ( nodeOptional != null ) nodeOptional.setParent(this);
      nodeToken1 = new NodeToken("Datatype");
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n2;
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
      nodeOptional1 = n3;
      if ( nodeOptional1 != null ) nodeOptional1.setParent(this);
      nodeToken3 = new NodeToken("{");
      if ( nodeToken3 != null ) nodeToken3.setParent(this);
      nodeListOptional = n4;
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

