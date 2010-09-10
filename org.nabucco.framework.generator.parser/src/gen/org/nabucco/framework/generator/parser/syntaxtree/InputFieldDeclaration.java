//
// Generated by JTB 1.3.2
//

package org.nabucco.framework.generator.parser.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * annotationDeclaration -> AnnotationDeclaration()
 * nodeToken -> &lt;PRIVATE&gt;
 * nodeToken1 -> &lt;INPUT_FIELD&gt;
 * nodeToken2 -> &lt;NAME_IDENTIFIER&gt;
 * nodeToken3 -> &lt;SEMICOLON_CHAR&gt;
 * </PRE>
 */
public class InputFieldDeclaration implements Node {
   private Node parent;
   public AnnotationDeclaration annotationDeclaration;
   public NodeToken nodeToken;
   public NodeToken nodeToken1;
   public NodeToken nodeToken2;
   public NodeToken nodeToken3;

   public InputFieldDeclaration(AnnotationDeclaration n0, NodeToken n1, NodeToken n2, NodeToken n3, NodeToken n4) {
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
   }

   public InputFieldDeclaration(AnnotationDeclaration n0, NodeToken n1) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeToken = new NodeToken("private");
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeToken1 = new NodeToken("InputField");
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n1;
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

