//
// Generated by JTB 1.3.2
//

package org.nabucco.framework.generator.parser.syntaxtree;

/**
 * Represents a grammar choice, e.g. ( A | B )
 */
public class NodeChoice implements Node {
   public NodeChoice(Node node) {
      this(node, -1);
   }

   public NodeChoice(Node node, int whichChoice) {
      choice = node;
      choice.setParent(this);
      which = whichChoice;
   }

   public void accept(org.nabucco.framework.generator.parser.visitor.Visitor v) {
      choice.accept(v);
   }
   public <R,A> R accept(org.nabucco.framework.generator.parser.visitor.GJVisitor<R,A> v, A argu) {
      return choice.accept(v,argu);
   }
   public <R> R accept(org.nabucco.framework.generator.parser.visitor.GJNoArguVisitor<R> v) {
      return choice.accept(v);
   }
   public <A> void accept(org.nabucco.framework.generator.parser.visitor.GJVoidVisitor<A> v, A argu) {
      choice.accept(v,argu);
   }

   public void setParent(Node n) { parent = n; }
   public Node getParent()       { return parent; }

   private Node parent;
   public Node choice;
   public int which;
}

