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
 * packageDeclaration -> PackageDeclaration()
 * nodeListOptional -> ( ImportDeclaration() )*
 * nabuccoStatement -> NabuccoStatement()
 * nodeToken -> &lt;EOF&gt;
 * </PRE>
 */
public class NabuccoUnit implements Node {
   private Node parent;
   public PackageDeclaration packageDeclaration;
   public NodeListOptional nodeListOptional;
   public NabuccoStatement nabuccoStatement;
   public NodeToken nodeToken;

   public NabuccoUnit(PackageDeclaration n0, NodeListOptional n1, NabuccoStatement n2, NodeToken n3) {
      packageDeclaration = n0;
      if ( packageDeclaration != null ) packageDeclaration.setParent(this);
      nodeListOptional = n1;
      if ( nodeListOptional != null ) nodeListOptional.setParent(this);
      nabuccoStatement = n2;
      if ( nabuccoStatement != null ) nabuccoStatement.setParent(this);
      nodeToken = n3;
      if ( nodeToken != null ) nodeToken.setParent(this);
   }

   public NabuccoUnit(PackageDeclaration n0, NodeListOptional n1, NabuccoStatement n2) {
      packageDeclaration = n0;
      if ( packageDeclaration != null ) packageDeclaration.setParent(this);
      nodeListOptional = n1;
      if ( nodeListOptional != null ) nodeListOptional.setParent(this);
      nabuccoStatement = n2;
      if ( nabuccoStatement != null ) nabuccoStatement.setParent(this);
      nodeToken = new NodeToken("");
      if ( nodeToken != null ) nodeToken.setParent(this);
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

