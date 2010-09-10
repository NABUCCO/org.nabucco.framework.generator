//
// Generated by JTB 1.3.2
//

package org.nabucco.framework.generator.parser.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * annotationDeclaration -> AnnotationDeclaration()
 * nodeChoice -> ( &lt;PUBLIC&gt; | &lt;PROTECTED&gt; | &lt;PRIVATE&gt; )
 * nodeToken -> &lt;SERVICE&gt;
 * nodeToken1 -> &lt;UNQUALIFIED_TYPE_NAME&gt;
 * nodeToken2 -> &lt;SEMICOLON_CHAR&gt;
 * </PRE>
 */
public class ServiceDeclaration implements Node {
   private Node parent;
   public AnnotationDeclaration annotationDeclaration;
   public NodeChoice nodeChoice;
   public NodeToken nodeToken;
   public NodeToken nodeToken1;
   public NodeToken nodeToken2;

   public ServiceDeclaration(AnnotationDeclaration n0, NodeChoice n1, NodeToken n2, NodeToken n3, NodeToken n4) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeChoice = n1;
      if ( nodeChoice != null ) nodeChoice.setParent(this);
      nodeToken = n2;
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeToken1 = n3;
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = n4;
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
   }

   public ServiceDeclaration(AnnotationDeclaration n0, NodeChoice n1, NodeToken n2) {
      annotationDeclaration = n0;
      if ( annotationDeclaration != null ) annotationDeclaration.setParent(this);
      nodeChoice = n1;
      if ( nodeChoice != null ) nodeChoice.setParent(this);
      nodeToken = new NodeToken("Service");
      if ( nodeToken != null ) nodeToken.setParent(this);
      nodeToken1 = n2;
      if ( nodeToken1 != null ) nodeToken1.setParent(this);
      nodeToken2 = new NodeToken(";");
      if ( nodeToken2 != null ) nodeToken2.setParent(this);
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
