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
package org.nabucco.framework.generator.parser;

import org.nabucco.framework.generator.parser.syntaxtree.*;
import java.util.Vector;


public class NabuccoParser implements NabuccoParserConstants {

  final public NabuccoUnit NabuccoUnit() throws ParseException {
   PackageDeclaration n0;
   NodeListOptional n1 = new NodeListOptional();
   ImportDeclaration n2;
   NabuccoStatement n3;
   NodeToken n4;
   Token n5;
    n0 = PackageDeclaration();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IMPORT:
        ;
        break;
      default:
        break label_1;
      }
      n2 = ImportDeclaration();
        n1.addNode(n2);
    }
     n1.nodes.trimToSize();
    n3 = NabuccoStatement();
    n5 = jj_consume_token(0);
      n5.beginColumn++; n5.endColumn++;
      n4 = JTBToolkit.makeNodeToken(n5);
     {if (true) return new NabuccoUnit(n0,n1,n3,n4);}
    throw new Error("Missing return statement in function");
  }

  final public PackageDeclaration PackageDeclaration() throws ParseException {
   NodeToken n0;
   Token n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
    n1 = jj_consume_token(PACKAGE);
                  n0 = JTBToolkit.makeNodeToken(n1);
    n3 = jj_consume_token(PACKAGE_IDENTIFIER);
                             n2 = JTBToolkit.makeNodeToken(n3);
    n5 = jj_consume_token(SEMICOLON_CHAR);
                         n4 = JTBToolkit.makeNodeToken(n5);
     {if (true) return new PackageDeclaration(n0,n2,n4);}
    throw new Error("Missing return statement in function");
  }

  final public ImportDeclaration ImportDeclaration() throws ParseException {
   NodeToken n0;
   Token n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
    n1 = jj_consume_token(IMPORT);
                 n0 = JTBToolkit.makeNodeToken(n1);
    n3 = jj_consume_token(QUALIFIED_TYPE_NAME);
                              n2 = JTBToolkit.makeNodeToken(n3);
    n5 = jj_consume_token(SEMICOLON_CHAR);
                         n4 = JTBToolkit.makeNodeToken(n5);
     {if (true) return new ImportDeclaration(n0,n2,n4);}
    throw new Error("Missing return statement in function");
  }

  final public AnnotationDeclaration AnnotationDeclaration() throws ParseException {
   NodeListOptional n0 = new NodeListOptional();
   NodeToken n1;
   Token n2;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ANNOTATION:
        ;
        break;
      default:
        break label_2;
      }
      n2 = jj_consume_token(ANNOTATION);
                        n1 = JTBToolkit.makeNodeToken(n2);
        n0.addNode(n1);
    }
     n0.nodes.trimToSize();
     {if (true) return new AnnotationDeclaration(n0);}
    throw new Error("Missing return statement in function");
  }

  final public ExtensionDeclaration ExtensionDeclaration() throws ParseException {
   NodeToken n0;
   Token n1;
   NodeChoice n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
    n1 = jj_consume_token(EXTENDS);
                  n0 = JTBToolkit.makeNodeToken(n1);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case UNQUALIFIED_TYPE_NAME:
      n4 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                      n3 = JTBToolkit.makeNodeToken(n4);
           n2 = new NodeChoice(n3, 0);
      break;
    case QUALIFIED_TYPE_NAME:
      n6 = jj_consume_token(QUALIFIED_TYPE_NAME);
                                    n5 = JTBToolkit.makeNodeToken(n6);
           n2 = new NodeChoice(n5, 1);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new ExtensionDeclaration(n0,n2);}
    throw new Error("Missing return statement in function");
  }

  final public NabuccoStatement NabuccoStatement() throws ParseException {
   NodeChoice n0;
   ApplicationStatement n1;
   ComponentStatement n2;
   AdapterStatement n3;
   DatatypeStatement n4;
   BasetypeStatement n5;
   EnumerationStatement n6;
   ExceptionStatement n7;
   ServiceStatement n8;
   MessageStatement n9;
   EditViewStatement n10;
   ListViewStatement n11;
   SearchViewStatement n12;
   CommandStatement n13;
    if (jj_2_1(2147483647)) {
      n1 = ApplicationStatement();
           n0 = new NodeChoice(n1, 0);
    } else if (jj_2_2(2147483647)) {
      n2 = ComponentStatement();
           n0 = new NodeChoice(n2, 1);
    } else if (jj_2_3(2147483647)) {
      n3 = AdapterStatement();
           n0 = new NodeChoice(n3, 2);
    } else if (jj_2_4(2147483647)) {
      n4 = DatatypeStatement();
           n0 = new NodeChoice(n4, 3);
    } else if (jj_2_5(2147483647)) {
      n5 = BasetypeStatement();
           n0 = new NodeChoice(n5, 4);
    } else if (jj_2_6(2147483647)) {
      n6 = EnumerationStatement();
           n0 = new NodeChoice(n6, 5);
    } else if (jj_2_7(2147483647)) {
      n7 = ExceptionStatement();
           n0 = new NodeChoice(n7, 6);
    } else if (jj_2_8(2147483647)) {
      n8 = ServiceStatement();
           n0 = new NodeChoice(n8, 7);
    } else if (jj_2_9(2147483647)) {
      n9 = MessageStatement();
           n0 = new NodeChoice(n9, 8);
    } else if (jj_2_10(2147483647)) {
      n10 = EditViewStatement();
           n0 = new NodeChoice(n10, 9);
    } else if (jj_2_11(2147483647)) {
      n11 = ListViewStatement();
           n0 = new NodeChoice(n11, 10);
    } else if (jj_2_12(2147483647)) {
      n12 = SearchViewStatement();
           n0 = new NodeChoice(n12, 11);
    } else if (jj_2_13(2147483647)) {
      n13 = CommandStatement();
           n0 = new NodeChoice(n13, 12);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new NabuccoStatement(n0);}
    throw new Error("Missing return statement in function");
  }

  final public ApplicationStatement ApplicationStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   ApplicationPropertyDeclaration n10;
   NodeToken n11;
   Token n12;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(APPLICATION);
                      n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_3;
      }
      n10 = ApplicationPropertyDeclaration();
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n12 = jj_consume_token(RBRACE_CHAR);
                       n11 = JTBToolkit.makeNodeToken(n12);
     {if (true) return new ApplicationStatement(n0,n1,n3,n5,n7,n9,n11);}
    throw new Error("Missing return statement in function");
  }

  final public ComponentStatement ComponentStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   ComponentPropertyDeclaration n10;
   NodeToken n11;
   Token n12;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(COMPONENT);
                    n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_4;
      }
      n10 = ComponentPropertyDeclaration();
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n12 = jj_consume_token(RBRACE_CHAR);
                       n11 = JTBToolkit.makeNodeToken(n12);
     {if (true) return new ComponentStatement(n0,n1,n3,n5,n7,n9,n11);}
    throw new Error("Missing return statement in function");
  }

  final public AdapterStatement AdapterStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   ServiceDeclaration n10;
   NodeToken n11;
   Token n12;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(ADAPTER);
                  n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_5;
      }
      n10 = ServiceDeclaration();
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n12 = jj_consume_token(RBRACE_CHAR);
                       n11 = JTBToolkit.makeNodeToken(n12);
     {if (true) return new AdapterStatement(n0,n1,n3,n5,n7,n9,n11);}
    throw new Error("Missing return statement in function");
  }

  final public DatatypeStatement DatatypeStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeOptional n3 = new NodeOptional();
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeToken n8;
   Token n9;
   NodeOptional n10 = new NodeOptional();
   ExtensionDeclaration n11;
   NodeToken n12;
   Token n13;
   NodeListOptional n14 = new NodeListOptional();
   PropertyDeclaration n15;
   NodeToken n16;
   Token n17;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ABSTRACT:
      n5 = jj_consume_token(ABSTRACT);
                      n4 = JTBToolkit.makeNodeToken(n5);
        n3.addNode(n4);
      break;
    default:
      ;
    }
    n7 = jj_consume_token(DATATYPE);
                   n6 = JTBToolkit.makeNodeToken(n7);
    n9 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n8 = JTBToolkit.makeNodeToken(n9);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EXTENDS:
      n11 = ExtensionDeclaration();
        n10.addNode(n11);
      break;
    default:
      ;
    }
    n13 = jj_consume_token(LBRACE_CHAR);
                       n12 = JTBToolkit.makeNodeToken(n13);
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_6;
      }
      n15 = PropertyDeclaration();
        n14.addNode(n15);
    }
     n14.nodes.trimToSize();
    n17 = jj_consume_token(RBRACE_CHAR);
                       n16 = JTBToolkit.makeNodeToken(n17);
     {if (true) return new DatatypeStatement(n0,n1,n3,n6,n8,n10,n12,n14,n16);}
    throw new Error("Missing return statement in function");
  }

  final public BasetypeStatement BasetypeStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeOptional n7 = new NodeOptional();
   ExtensionDeclaration n8;
   NodeToken n9;
   Token n10;
   NodeToken n11;
   Token n12;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(BASETYPE);
                   n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EXTENDS:
      n8 = ExtensionDeclaration();
        n7.addNode(n8);
      break;
    default:
      ;
    }
    n10 = jj_consume_token(LBRACE_CHAR);
                       n9 = JTBToolkit.makeNodeToken(n10);
    n12 = jj_consume_token(RBRACE_CHAR);
                       n11 = JTBToolkit.makeNodeToken(n12);
     {if (true) return new BasetypeStatement(n0,n1,n3,n5,n7,n9,n11);}
    throw new Error("Missing return statement in function");
  }

  final public EnumerationStatement EnumerationStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   EnumerationLiteralDeclaration n10;
   NodeToken n11;
   Token n12;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(ENUMERATION);
                      n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CONSTANT_IDENTIFIER:
      case ANNOTATION:
        ;
        break;
      default:
        break label_7;
      }
      n10 = EnumerationLiteralDeclaration();
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n12 = jj_consume_token(RBRACE_CHAR);
                       n11 = JTBToolkit.makeNodeToken(n12);
     {if (true) return new EnumerationStatement(n0,n1,n3,n5,n7,n9,n11);}
    throw new Error("Missing return statement in function");
  }

  final public ServiceStatement ServiceStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeOptional n3 = new NodeOptional();
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeToken n8;
   Token n9;
   NodeOptional n10 = new NodeOptional();
   ExtensionDeclaration n11;
   NodeToken n12;
   Token n13;
   NodeListOptional n14 = new NodeListOptional();
   ServicePropertyDeclaration n15;
   NodeToken n16;
   Token n17;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ABSTRACT:
      n5 = jj_consume_token(ABSTRACT);
                      n4 = JTBToolkit.makeNodeToken(n5);
        n3.addNode(n4);
      break;
    default:
      ;
    }
    n7 = jj_consume_token(SERVICE);
                  n6 = JTBToolkit.makeNodeToken(n7);
    n9 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n8 = JTBToolkit.makeNodeToken(n9);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EXTENDS:
      n11 = ExtensionDeclaration();
        n10.addNode(n11);
      break;
    default:
      ;
    }
    n13 = jj_consume_token(LBRACE_CHAR);
                       n12 = JTBToolkit.makeNodeToken(n13);
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_8;
      }
      n15 = ServicePropertyDeclaration();
        n14.addNode(n15);
    }
     n14.nodes.trimToSize();
    n17 = jj_consume_token(RBRACE_CHAR);
                       n16 = JTBToolkit.makeNodeToken(n17);
     {if (true) return new ServiceStatement(n0,n1,n3,n6,n8,n10,n12,n14,n16);}
    throw new Error("Missing return statement in function");
  }

  final public MessageStatement MessageStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   PropertyDeclaration n10;
   NodeToken n11;
   Token n12;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(MESSAGE);
                  n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_9;
      }
      n10 = PropertyDeclaration();
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n12 = jj_consume_token(RBRACE_CHAR);
                       n11 = JTBToolkit.makeNodeToken(n12);
     {if (true) return new MessageStatement(n0,n1,n3,n5,n7,n9,n11);}
    throw new Error("Missing return statement in function");
  }

  final public ExceptionStatement ExceptionStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeOptional n7 = new NodeOptional();
   ExtensionDeclaration n8;
   NodeToken n9;
   Token n10;
   NodeListOptional n11 = new NodeListOptional();
   ExceptionParameterDeclaration n12;
   NodeToken n13;
   Token n14;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(EXCEPTION);
                    n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EXTENDS:
      n8 = ExtensionDeclaration();
        n7.addNode(n8);
      break;
    default:
      ;
    }
    n10 = jj_consume_token(LBRACE_CHAR);
                       n9 = JTBToolkit.makeNodeToken(n10);
    label_10:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_10;
      }
      n12 = ExceptionParameterDeclaration();
        n11.addNode(n12);
    }
     n11.nodes.trimToSize();
    n14 = jj_consume_token(RBRACE_CHAR);
                       n13 = JTBToolkit.makeNodeToken(n14);
     {if (true) return new ExceptionStatement(n0,n1,n3,n5,n7,n9,n11,n13);}
    throw new Error("Missing return statement in function");
  }

  final public ConnectorStatement ConnectorStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   ConnectorPropertyDeclaration n10;
   NodeToken n11;
   Token n12;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(CONNECTOR);
                    n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_11:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_11;
      }
      n10 = ConnectorPropertyDeclaration();
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n12 = jj_consume_token(RBRACE_CHAR);
                       n11 = JTBToolkit.makeNodeToken(n12);
     {if (true) return new ConnectorStatement(n0,n1,n3,n5,n7,n9,n11);}
    throw new Error("Missing return statement in function");
  }

  final public EditViewStatement EditViewStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   NodeChoice n10;
   DatatypeDeclaration n11;
   WidgetDeclaration n12;
   NodeToken n13;
   Token n14;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(EDITVIEW);
                   n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_12:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_12;
      }
      if (jj_2_14(2147483647)) {
        n11 = DatatypeDeclaration();
           n10 = new NodeChoice(n11, 0);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case PRIVATE:
        case ANNOTATION:
          n12 = WidgetDeclaration();
           n10 = new NodeChoice(n12, 1);
          break;
        default:
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n14 = jj_consume_token(RBRACE_CHAR);
                       n13 = JTBToolkit.makeNodeToken(n14);
     {if (true) return new EditViewStatement(n0,n1,n3,n5,n7,n9,n13);}
    throw new Error("Missing return statement in function");
  }

  final public ListViewStatement ListViewStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   NodeChoice n10;
   DatatypeDeclaration n11;
   ColumnDeclaration n12;
   NodeToken n13;
   Token n14;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(LISTVIEW);
                   n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_13:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_13;
      }
      if (jj_2_15(2147483647)) {
        n11 = DatatypeDeclaration();
           n10 = new NodeChoice(n11, 0);
      } else if (jj_2_16(2147483647)) {
        n12 = ColumnDeclaration();
           n10 = new NodeChoice(n12, 1);
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n14 = jj_consume_token(RBRACE_CHAR);
                       n13 = JTBToolkit.makeNodeToken(n14);
     {if (true) return new ListViewStatement(n0,n1,n3,n5,n7,n9,n13);}
    throw new Error("Missing return statement in function");
  }

  final public SearchViewStatement SearchViewStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   NodeChoice n10;
   DatatypeDeclaration n11;
   WidgetDeclaration n12;
   NodeToken n13;
   Token n14;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(SEARCHVIEW);
                     n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_14:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PUBLIC:
      case PROTECTED:
      case PRIVATE:
      case ANNOTATION:
        ;
        break;
      default:
        break label_14;
      }
      if (jj_2_17(2147483647)) {
        n11 = DatatypeDeclaration();
           n10 = new NodeChoice(n11, 0);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case PRIVATE:
        case ANNOTATION:
          n12 = WidgetDeclaration();
           n10 = new NodeChoice(n12, 1);
          break;
        default:
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n14 = jj_consume_token(RBRACE_CHAR);
                       n13 = JTBToolkit.makeNodeToken(n14);
     {if (true) return new SearchViewStatement(n0,n1,n3,n5,n7,n9,n13);}
    throw new Error("Missing return statement in function");
  }

  final public CommandStatement CommandStatement() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeListOptional n9 = new NodeListOptional();
   ViewDeclaration n10;
   MethodDeclaration n11;
   NodeToken n12;
   Token n13;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(COMMAND);
                  n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(LBRACE_CHAR);
                      n7 = JTBToolkit.makeNodeToken(n8);
    label_15:
    while (true) {
      if (jj_2_18(2147483647)) {
        ;
      } else {
        break label_15;
      }
      n10 = ViewDeclaration();
        n9.addNode(n10);
    }
     n9.nodes.trimToSize();
    n11 = MethodDeclaration();
    n13 = jj_consume_token(RBRACE_CHAR);
                       n12 = JTBToolkit.makeNodeToken(n13);
     {if (true) return new CommandStatement(n0,n1,n3,n5,n7,n9,n11,n12);}
    throw new Error("Missing return statement in function");
  }

  final public ApplicationPropertyDeclaration ApplicationPropertyDeclaration() throws ParseException {
   NodeChoice n0;
   ComponentDeclaration n1;
   ConnectorStatement n2;
    if (jj_2_19(2147483647)) {
      n1 = ComponentDeclaration();
           n0 = new NodeChoice(n1, 0);
    } else if (jj_2_20(2147483647)) {
      n2 = ConnectorStatement();
           n0 = new NodeChoice(n2, 1);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new ApplicationPropertyDeclaration(n0);}
    throw new Error("Missing return statement in function");
  }

  final public ComponentPropertyDeclaration ComponentPropertyDeclaration() throws ParseException {
   NodeChoice n0;
   ComponentDatatypeDeclaration n1;
   EnumerationDeclaration n2;
   ServiceDeclaration n3;
   ComponentDeclaration n4;
    if (jj_2_21(2147483647)) {
      n1 = ComponentDatatypeDeclaration();
           n0 = new NodeChoice(n1, 0);
    } else if (jj_2_22(2147483647)) {
      n2 = EnumerationDeclaration();
           n0 = new NodeChoice(n2, 1);
    } else if (jj_2_23(2147483647)) {
      n3 = ServiceDeclaration();
           n0 = new NodeChoice(n3, 2);
    } else if (jj_2_24(2147483647)) {
      n4 = ComponentDeclaration();
           n0 = new NodeChoice(n4, 3);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new ComponentPropertyDeclaration(n0);}
    throw new Error("Missing return statement in function");
  }

  final public ServicePropertyDeclaration ServicePropertyDeclaration() throws ParseException {
   NodeChoice n0;
   CustomDeclaration n1;
   ServiceDeclaration n2;
   MethodDeclaration n3;
    if (jj_2_25(2147483647)) {
      n1 = CustomDeclaration();
           n0 = new NodeChoice(n1, 0);
    } else if (jj_2_26(2147483647)) {
      n2 = ServiceDeclaration();
           n0 = new NodeChoice(n2, 1);
    } else if (jj_2_27(2147483647)) {
      n3 = MethodDeclaration();
           n0 = new NodeChoice(n3, 2);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new ServicePropertyDeclaration(n0);}
    throw new Error("Missing return statement in function");
  }

  final public ConnectorPropertyDeclaration ConnectorPropertyDeclaration() throws ParseException {
   NodeChoice n0;
   DatatypeDeclaration n1;
   ServiceLinkDeclaration n2;
    if (jj_2_28(2147483647)) {
      n1 = DatatypeDeclaration();
           n0 = new NodeChoice(n1, 0);
    } else if (jj_2_29(2147483647)) {
      n2 = ServiceLinkDeclaration();
           n0 = new NodeChoice(n2, 1);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new ConnectorPropertyDeclaration(n0);}
    throw new Error("Missing return statement in function");
  }

  final public PropertyDeclaration PropertyDeclaration() throws ParseException {
   NodeChoice n0;
   BasetypeDeclaration n1;
   DatatypeDeclaration n2;
   EnumerationDeclaration n3;
    if (jj_2_30(2147483647)) {
      n1 = BasetypeDeclaration();
           n0 = new NodeChoice(n1, 0);
    } else if (jj_2_31(2147483647)) {
      n2 = DatatypeDeclaration();
           n0 = new NodeChoice(n2, 1);
    } else if (jj_2_32(2147483647)) {
      n3 = EnumerationDeclaration();
           n0 = new NodeChoice(n3, 2);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new PropertyDeclaration(n0);}
    throw new Error("Missing return statement in function");
  }

  final public CustomDeclaration CustomDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeChoice n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeToken n8;
   Token n9;
   NodeToken n10;
   Token n11;
   NodeToken n12;
   Token n13;
   NodeToken n14;
   Token n15;
    n0 = AnnotationDeclaration();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      n3 = jj_consume_token(PUBLIC);
                       n2 = JTBToolkit.makeNodeToken(n3);
           n1 = new NodeChoice(n2, 0);
      break;
    case PROTECTED:
      n5 = jj_consume_token(PROTECTED);
                          n4 = JTBToolkit.makeNodeToken(n5);
           n1 = new NodeChoice(n4, 1);
      break;
    case PRIVATE:
      n7 = jj_consume_token(PRIVATE);
                        n6 = JTBToolkit.makeNodeToken(n7);
           n1 = new NodeChoice(n6, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n9 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n8 = JTBToolkit.makeNodeToken(n9);
    n11 = jj_consume_token(MULTIPLICITY);
                        n10 = JTBToolkit.makeNodeToken(n11);
    n13 = jj_consume_token(NAME_IDENTIFIER);
                           n12 = JTBToolkit.makeNodeToken(n13);
    n15 = jj_consume_token(SEMICOLON_CHAR);
                          n14 = JTBToolkit.makeNodeToken(n15);
     {if (true) return new CustomDeclaration(n0,n1,n8,n10,n12,n14);}
    throw new Error("Missing return statement in function");
  }

  final public BasetypeDeclaration BasetypeDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeChoice n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeOptional n8 = new NodeOptional();
   NodeToken n9;
   Token n10;
   NodeToken n11;
   Token n12;
   NodeToken n13;
   Token n14;
   NodeToken n15;
   Token n16;
   NodeToken n17;
   Token n18;
   NodeToken n19;
   Token n20;
    n0 = AnnotationDeclaration();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      n3 = jj_consume_token(PUBLIC);
                       n2 = JTBToolkit.makeNodeToken(n3);
           n1 = new NodeChoice(n2, 0);
      break;
    case PROTECTED:
      n5 = jj_consume_token(PROTECTED);
                          n4 = JTBToolkit.makeNodeToken(n5);
           n1 = new NodeChoice(n4, 1);
      break;
    case PRIVATE:
      n7 = jj_consume_token(PRIVATE);
                        n6 = JTBToolkit.makeNodeToken(n7);
           n1 = new NodeChoice(n6, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TRANSIENT:
      n10 = jj_consume_token(TRANSIENT);
                        n9 = JTBToolkit.makeNodeToken(n10);
        n8.addNode(n9);
      break;
    default:
      ;
    }
    n12 = jj_consume_token(BASETYPE);
                    n11 = JTBToolkit.makeNodeToken(n12);
    n14 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                 n13 = JTBToolkit.makeNodeToken(n14);
    n16 = jj_consume_token(MULTIPLICITY);
                        n15 = JTBToolkit.makeNodeToken(n16);
    n18 = jj_consume_token(NAME_IDENTIFIER);
                           n17 = JTBToolkit.makeNodeToken(n18);
    n20 = jj_consume_token(SEMICOLON_CHAR);
                          n19 = JTBToolkit.makeNodeToken(n20);
     {if (true) return new BasetypeDeclaration(n0,n1,n8,n11,n13,n15,n17,n19);}
    throw new Error("Missing return statement in function");
  }

  final public ComponentDatatypeDeclaration ComponentDatatypeDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeChoice n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeOptional n8 = new NodeOptional();
   NodeToken n9;
   Token n10;
   NodeToken n11;
   Token n12;
   NodeChoice n13;
   NodeToken n14;
   Token n15;
   NodeToken n16;
   Token n17;
   NodeToken n18;
   Token n19;
   NodeToken n20;
   Token n21;
   NodeToken n22;
   Token n23;
    n0 = AnnotationDeclaration();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      n3 = jj_consume_token(PUBLIC);
                       n2 = JTBToolkit.makeNodeToken(n3);
           n1 = new NodeChoice(n2, 0);
      break;
    case PROTECTED:
      n5 = jj_consume_token(PROTECTED);
                          n4 = JTBToolkit.makeNodeToken(n5);
           n1 = new NodeChoice(n4, 1);
      break;
    case PRIVATE:
      n7 = jj_consume_token(PRIVATE);
                        n6 = JTBToolkit.makeNodeToken(n7);
           n1 = new NodeChoice(n6, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PERSISTENT:
      n10 = jj_consume_token(PERSISTENT);
                         n9 = JTBToolkit.makeNodeToken(n10);
        n8.addNode(n9);
      break;
    default:
      ;
    }
    n12 = jj_consume_token(DATATYPE);
                    n11 = JTBToolkit.makeNodeToken(n12);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case QUALIFIED_TYPE_NAME:
      n15 = jj_consume_token(QUALIFIED_TYPE_NAME);
                                     n14 = JTBToolkit.makeNodeToken(n15);
           n13 = new NodeChoice(n14, 0);
      break;
    case UNQUALIFIED_TYPE_NAME:
      n17 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                       n16 = JTBToolkit.makeNodeToken(n17);
           n13 = new NodeChoice(n16, 1);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n19 = jj_consume_token(MULTIPLICITY);
                        n18 = JTBToolkit.makeNodeToken(n19);
    n21 = jj_consume_token(NAME_IDENTIFIER);
                           n20 = JTBToolkit.makeNodeToken(n21);
    n23 = jj_consume_token(SEMICOLON_CHAR);
                          n22 = JTBToolkit.makeNodeToken(n23);
     {if (true) return new ComponentDatatypeDeclaration(n0,n1,n8,n11,n13,n18,n20,n22);}
    throw new Error("Missing return statement in function");
  }

  final public ConnectorDeclaration ConnectorDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeChoice n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeToken n8;
   Token n9;
   NodeToken n10;
   Token n11;
   NodeToken n12;
   Token n13;
   NodeToken n14;
   Token n15;
    n0 = AnnotationDeclaration();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      n3 = jj_consume_token(PUBLIC);
                       n2 = JTBToolkit.makeNodeToken(n3);
           n1 = new NodeChoice(n2, 0);
      break;
    case PROTECTED:
      n5 = jj_consume_token(PROTECTED);
                          n4 = JTBToolkit.makeNodeToken(n5);
           n1 = new NodeChoice(n4, 1);
      break;
    case PRIVATE:
      n7 = jj_consume_token(PRIVATE);
                        n6 = JTBToolkit.makeNodeToken(n7);
           n1 = new NodeChoice(n6, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n9 = jj_consume_token(CONNECTOR);
                    n8 = JTBToolkit.makeNodeToken(n9);
    n11 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                 n10 = JTBToolkit.makeNodeToken(n11);
    n13 = jj_consume_token(NAME_IDENTIFIER);
                           n12 = JTBToolkit.makeNodeToken(n13);
    n15 = jj_consume_token(SEMICOLON_CHAR);
                          n14 = JTBToolkit.makeNodeToken(n15);
     {if (true) return new ConnectorDeclaration(n0,n1,n8,n10,n12,n14);}
    throw new Error("Missing return statement in function");
  }

  final public ServiceLinkDeclaration ServiceLinkDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
   NodeToken n9;
   Token n10;
   NodeToken n11;
   Token n12;
   NodeToken n13;
   Token n14;
   NodeToken n15;
   Token n16;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(SERVICELINK);
                      n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(QUALIFIED_TYPE_NAME);
                              n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(DOT_CHAR);
                   n7 = JTBToolkit.makeNodeToken(n8);
    n10 = jj_consume_token(NAME_IDENTIFIER);
                           n9 = JTBToolkit.makeNodeToken(n10);
    n12 = jj_consume_token(LPAREN_CHAR);
                       n11 = JTBToolkit.makeNodeToken(n12);
    n14 = jj_consume_token(RPAREN_CHAR);
                       n13 = JTBToolkit.makeNodeToken(n14);
    n16 = jj_consume_token(SEMICOLON_CHAR);
                          n15 = JTBToolkit.makeNodeToken(n16);
     {if (true) return new ServiceLinkDeclaration(n0,n1,n3,n5,n7,n9,n11,n13,n15);}
    throw new Error("Missing return statement in function");
  }

  final public DatatypeDeclaration DatatypeDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeChoice n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeOptional n8 = new NodeOptional();
   NodeToken n9;
   Token n10;
   NodeToken n11;
   Token n12;
   NodeChoice n13;
   NodeToken n14;
   Token n15;
   NodeToken n16;
   Token n17;
   NodeToken n18;
   Token n19;
   NodeToken n20;
   Token n21;
   NodeToken n22;
   Token n23;
    n0 = AnnotationDeclaration();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      n3 = jj_consume_token(PUBLIC);
                       n2 = JTBToolkit.makeNodeToken(n3);
           n1 = new NodeChoice(n2, 0);
      break;
    case PROTECTED:
      n5 = jj_consume_token(PROTECTED);
                          n4 = JTBToolkit.makeNodeToken(n5);
           n1 = new NodeChoice(n4, 1);
      break;
    case PRIVATE:
      n7 = jj_consume_token(PRIVATE);
                        n6 = JTBToolkit.makeNodeToken(n7);
           n1 = new NodeChoice(n6, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TRANSIENT:
      n10 = jj_consume_token(TRANSIENT);
                        n9 = JTBToolkit.makeNodeToken(n10);
        n8.addNode(n9);
      break;
    default:
      ;
    }
    n12 = jj_consume_token(DATATYPE);
                    n11 = JTBToolkit.makeNodeToken(n12);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case QUALIFIED_TYPE_NAME:
      n15 = jj_consume_token(QUALIFIED_TYPE_NAME);
                                     n14 = JTBToolkit.makeNodeToken(n15);
           n13 = new NodeChoice(n14, 0);
      break;
    case UNQUALIFIED_TYPE_NAME:
      n17 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                       n16 = JTBToolkit.makeNodeToken(n17);
           n13 = new NodeChoice(n16, 1);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n19 = jj_consume_token(MULTIPLICITY);
                        n18 = JTBToolkit.makeNodeToken(n19);
    n21 = jj_consume_token(NAME_IDENTIFIER);
                           n20 = JTBToolkit.makeNodeToken(n21);
    n23 = jj_consume_token(SEMICOLON_CHAR);
                          n22 = JTBToolkit.makeNodeToken(n23);
     {if (true) return new DatatypeDeclaration(n0,n1,n8,n11,n13,n18,n20,n22);}
    throw new Error("Missing return statement in function");
  }

  final public EnumerationDeclaration EnumerationDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeChoice n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeOptional n8 = new NodeOptional();
   NodeToken n9;
   Token n10;
   NodeToken n11;
   Token n12;
   NodeChoice n13;
   NodeToken n14;
   Token n15;
   NodeToken n16;
   Token n17;
   NodeToken n18;
   Token n19;
   NodeToken n20;
   Token n21;
   NodeToken n22;
   Token n23;
    n0 = AnnotationDeclaration();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      n3 = jj_consume_token(PUBLIC);
                       n2 = JTBToolkit.makeNodeToken(n3);
           n1 = new NodeChoice(n2, 0);
      break;
    case PROTECTED:
      n5 = jj_consume_token(PROTECTED);
                          n4 = JTBToolkit.makeNodeToken(n5);
           n1 = new NodeChoice(n4, 1);
      break;
    case PRIVATE:
      n7 = jj_consume_token(PRIVATE);
                        n6 = JTBToolkit.makeNodeToken(n7);
           n1 = new NodeChoice(n6, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TRANSIENT:
      n10 = jj_consume_token(TRANSIENT);
                        n9 = JTBToolkit.makeNodeToken(n10);
        n8.addNode(n9);
      break;
    default:
      ;
    }
    n12 = jj_consume_token(ENUMERATION);
                       n11 = JTBToolkit.makeNodeToken(n12);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case QUALIFIED_TYPE_NAME:
      n15 = jj_consume_token(QUALIFIED_TYPE_NAME);
                                     n14 = JTBToolkit.makeNodeToken(n15);
           n13 = new NodeChoice(n14, 0);
      break;
    case UNQUALIFIED_TYPE_NAME:
      n17 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                       n16 = JTBToolkit.makeNodeToken(n17);
           n13 = new NodeChoice(n16, 1);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n19 = jj_consume_token(MULTIPLICITY);
                        n18 = JTBToolkit.makeNodeToken(n19);
    n21 = jj_consume_token(NAME_IDENTIFIER);
                           n20 = JTBToolkit.makeNodeToken(n21);
    n23 = jj_consume_token(SEMICOLON_CHAR);
                          n22 = JTBToolkit.makeNodeToken(n23);
     {if (true) return new EnumerationDeclaration(n0,n1,n8,n11,n13,n18,n20,n22);}
    throw new Error("Missing return statement in function");
  }

  final public EnumerationLiteralDeclaration EnumerationLiteralDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(CONSTANT_IDENTIFIER);
                              n1 = JTBToolkit.makeNodeToken(n2);
     {if (true) return new EnumerationLiteralDeclaration(n0,n1);}
    throw new Error("Missing return statement in function");
  }

  final public ExceptionParameterDeclaration ExceptionParameterDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(PARAMETER);
                    n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new ExceptionParameterDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public ComponentDeclaration ComponentDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeChoice n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeToken n8;
   Token n9;
   NodeToken n10;
   Token n11;
   NodeToken n12;
   Token n13;
   NodeToken n14;
   Token n15;
    n0 = AnnotationDeclaration();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      n3 = jj_consume_token(PUBLIC);
                       n2 = JTBToolkit.makeNodeToken(n3);
           n1 = new NodeChoice(n2, 0);
      break;
    case PROTECTED:
      n5 = jj_consume_token(PROTECTED);
                          n4 = JTBToolkit.makeNodeToken(n5);
           n1 = new NodeChoice(n4, 1);
      break;
    case PRIVATE:
      n7 = jj_consume_token(PRIVATE);
                        n6 = JTBToolkit.makeNodeToken(n7);
           n1 = new NodeChoice(n6, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n9 = jj_consume_token(COMPONENT);
                    n8 = JTBToolkit.makeNodeToken(n9);
    n11 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                 n10 = JTBToolkit.makeNodeToken(n11);
    n13 = jj_consume_token(NAME_IDENTIFIER);
                           n12 = JTBToolkit.makeNodeToken(n13);
    n15 = jj_consume_token(SEMICOLON_CHAR);
                          n14 = JTBToolkit.makeNodeToken(n15);
     {if (true) return new ComponentDeclaration(n0,n1,n8,n10,n12,n14);}
    throw new Error("Missing return statement in function");
  }

  final public ServiceDeclaration ServiceDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeChoice n1;
   NodeToken n2;
   Token n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeToken n8;
   Token n9;
   NodeToken n10;
   Token n11;
   NodeToken n12;
   Token n13;
    n0 = AnnotationDeclaration();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PUBLIC:
      n3 = jj_consume_token(PUBLIC);
                       n2 = JTBToolkit.makeNodeToken(n3);
           n1 = new NodeChoice(n2, 0);
      break;
    case PROTECTED:
      n5 = jj_consume_token(PROTECTED);
                          n4 = JTBToolkit.makeNodeToken(n5);
           n1 = new NodeChoice(n4, 1);
      break;
    case PRIVATE:
      n7 = jj_consume_token(PRIVATE);
                        n6 = JTBToolkit.makeNodeToken(n7);
           n1 = new NodeChoice(n6, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n9 = jj_consume_token(SERVICE);
                  n8 = JTBToolkit.makeNodeToken(n9);
    n11 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                 n10 = JTBToolkit.makeNodeToken(n11);
    n13 = jj_consume_token(SEMICOLON_CHAR);
                          n12 = JTBToolkit.makeNodeToken(n13);
     {if (true) return new ServiceDeclaration(n0,n1,n8,n10,n12);}
    throw new Error("Missing return statement in function");
  }

  final public MethodDeclaration MethodDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeChoice n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeToken n8;
   Token n9;
   NodeToken n10;
   Token n11;
   ParameterList n12;
   NodeToken n13;
   Token n14;
   NodeOptional n15 = new NodeOptional();
   NodeSequence n16;
   NodeToken n17;
   Token n18;
   NodeToken n19;
   Token n20;
   NodeChoice n21;
   NodeToken n22;
   Token n23;
   NodeSequence n24;
   NodeToken n25;
   Token n26;
   MethodBody n27;
   NodeToken n28;
   Token n29;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PUBLIC);
                 n1 = JTBToolkit.makeNodeToken(n2);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case VOID:
      n5 = jj_consume_token(VOID);
                     n4 = JTBToolkit.makeNodeToken(n5);
           n3 = new NodeChoice(n4, 0);
      break;
    case UNQUALIFIED_TYPE_NAME:
      n7 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                      n6 = JTBToolkit.makeNodeToken(n7);
           n3 = new NodeChoice(n6, 1);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n9 = jj_consume_token(NAME_IDENTIFIER);
                          n8 = JTBToolkit.makeNodeToken(n9);
    n11 = jj_consume_token(LPAREN_CHAR);
                       n10 = JTBToolkit.makeNodeToken(n11);
    n12 = ParameterList();
    n14 = jj_consume_token(RPAREN_CHAR);
                       n13 = JTBToolkit.makeNodeToken(n14);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case THROWS:
        n16 = new NodeSequence(2);
      n18 = jj_consume_token(THROWS);
                     n17 = JTBToolkit.makeNodeToken(n18);
        n16.addNode(n17);
      n20 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                    n19 = JTBToolkit.makeNodeToken(n20);
        n16.addNode(n19);
        n15.addNode(n16);
      break;
    default:
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SEMICOLON_CHAR:
      n23 = jj_consume_token(SEMICOLON_CHAR);
                                n22 = JTBToolkit.makeNodeToken(n23);
           n21 = new NodeChoice(n22, 0);
      break;
    case LBRACE_CHAR:
           n24 = new NodeSequence(3);
      n26 = jj_consume_token(LBRACE_CHAR);
                             n25 = JTBToolkit.makeNodeToken(n26);
           n24.addNode(n25);
      n27 = MethodBody();
           n24.addNode(n27);
      n29 = jj_consume_token(RBRACE_CHAR);
                             n28 = JTBToolkit.makeNodeToken(n29);
           n24.addNode(n28);
           n21 = new NodeChoice(n24, 1);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new MethodDeclaration(n0,n1,n3,n8,n10,n12,n13,n15,n21);}
    throw new Error("Missing return statement in function");
  }

  final public ParameterList ParameterList() throws ParseException {
   NodeListOptional n0 = new NodeListOptional();
   Parameter n1;
    label_16:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA_CHAR:
      case UNQUALIFIED_TYPE_NAME:
        ;
        break;
      default:
        break label_16;
      }
      n1 = Parameter();
        n0.addNode(n1);
    }
     n0.nodes.trimToSize();
     {if (true) return new ParameterList(n0);}
    throw new Error("Missing return statement in function");
  }

  final public Parameter Parameter() throws ParseException {
   NodeOptional n0 = new NodeOptional();
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMMA_CHAR:
      n2 = jj_consume_token(COMMA_CHAR);
                        n1 = JTBToolkit.makeNodeToken(n2);
        n0.addNode(n1);
      break;
    default:
      ;
    }
    n4 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
     {if (true) return new Parameter(n0,n3,n5);}
    throw new Error("Missing return statement in function");
  }

  final public MethodBody MethodBody() throws ParseException {
   Block n0;
    n0 = Block();
     {if (true) return new MethodBody(n0);}
    throw new Error("Missing return statement in function");
  }

  Block Block() throws ParseException {
   Token tok;
   StringBuffer buffer = new StringBuffer();
   while ( getToken(1) != null )
   {
      tok = getToken(1);
      if ( tok.kind == RBRACE_CHAR )
      {
         break;
      }
      if ( buffer.length() != 0 )
      {
         buffer.append(' ');
      }
      buffer.append(tok.image);
      getNextToken();
   }
   return new Block(buffer.toString());
  }

  final public ViewDeclaration ViewDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeChoice n3;
   NodeToken n4;
   Token n5;
   NodeToken n6;
   Token n7;
   NodeToken n8;
   Token n9;
   NodeToken n10;
   Token n11;
   NodeToken n12;
   Token n13;
   NodeToken n14;
   Token n15;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EDITVIEW:
      n5 = jj_consume_token(EDITVIEW);
                         n4 = JTBToolkit.makeNodeToken(n5);
           n3 = new NodeChoice(n4, 0);
      break;
    case LISTVIEW:
      n7 = jj_consume_token(LISTVIEW);
                         n6 = JTBToolkit.makeNodeToken(n7);
           n3 = new NodeChoice(n6, 1);
      break;
    case SEARCHVIEW:
      n9 = jj_consume_token(SEARCHVIEW);
                           n8 = JTBToolkit.makeNodeToken(n9);
           n3 = new NodeChoice(n8, 2);
      break;
    default:
      jj_consume_token(-1);
      throw new ParseException();
    }
    n11 = jj_consume_token(UNQUALIFIED_TYPE_NAME);
                                 n10 = JTBToolkit.makeNodeToken(n11);
    n13 = jj_consume_token(NAME_IDENTIFIER);
                           n12 = JTBToolkit.makeNodeToken(n13);
    n15 = jj_consume_token(SEMICOLON_CHAR);
                          n14 = JTBToolkit.makeNodeToken(n15);
     {if (true) return new ViewDeclaration(n0,n1,n3,n10,n12,n14);}
    throw new Error("Missing return statement in function");
  }

  final public WidgetDeclaration WidgetDeclaration() throws ParseException {
   NodeChoice n0;
   LabeledInputFieldDeclaration n1;
   InputFieldDeclaration n2;
   LabeledPickerDeclaration n3;
   PickerDeclaration n4;
   LabeledListPickerDeclaration n5;
   ListPickerDeclaration n6;
   LabeledComboBoxDeclaration n7;
   ComboBoxDeclaration n8;
    if (jj_2_33(2147483647)) {
      n1 = LabeledInputFieldDeclaration();
        n0 = new NodeChoice(n1, 0);
    } else if (jj_2_34(2147483647)) {
      n2 = InputFieldDeclaration();
        n0 = new NodeChoice(n2, 1);
    } else if (jj_2_35(2147483647)) {
      n3 = LabeledPickerDeclaration();
        n0 = new NodeChoice(n3, 2);
    } else if (jj_2_36(2147483647)) {
      n4 = PickerDeclaration();
        n0 = new NodeChoice(n4, 3);
    } else if (jj_2_37(2147483647)) {
      n5 = LabeledListPickerDeclaration();
        n0 = new NodeChoice(n5, 4);
    } else if (jj_2_38(2147483647)) {
      n6 = ListPickerDeclaration();
        n0 = new NodeChoice(n6, 5);
    } else if (jj_2_39(2147483647)) {
      n7 = LabeledComboBoxDeclaration();
        n0 = new NodeChoice(n7, 6);
    } else if (jj_2_40(2147483647)) {
      n8 = ComboBoxDeclaration();
        n0 = new NodeChoice(n8, 7);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
     {if (true) return new WidgetDeclaration(n0);}
    throw new Error("Missing return statement in function");
  }

  final public LabeledInputFieldDeclaration LabeledInputFieldDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(LABELED_INPUT_FIELD);
                              n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new LabeledInputFieldDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public InputFieldDeclaration InputFieldDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(INPUT_FIELD);
                      n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new InputFieldDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public LabeledPickerDeclaration LabeledPickerDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(LABELED_PICKER);
                         n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new LabeledPickerDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public PickerDeclaration PickerDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(PICKER);
                 n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new PickerDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public LabeledListPickerDeclaration LabeledListPickerDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(LABELED_LIST_PICKER);
                              n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new LabeledListPickerDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public ListPickerDeclaration ListPickerDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(LIST_PICKER);
                      n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new ListPickerDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public LabeledComboBoxDeclaration LabeledComboBoxDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(LABELED_COMBO_BOX);
                            n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new LabeledComboBoxDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public ComboBoxDeclaration ComboBoxDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(COMBO_BOX);
                    n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new ComboBoxDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  final public ColumnDeclaration ColumnDeclaration() throws ParseException {
   AnnotationDeclaration n0;
   NodeToken n1;
   Token n2;
   NodeToken n3;
   Token n4;
   NodeToken n5;
   Token n6;
   NodeToken n7;
   Token n8;
    n0 = AnnotationDeclaration();
    n2 = jj_consume_token(PRIVATE);
                  n1 = JTBToolkit.makeNodeToken(n2);
    n4 = jj_consume_token(COLUMN);
                 n3 = JTBToolkit.makeNodeToken(n4);
    n6 = jj_consume_token(NAME_IDENTIFIER);
                          n5 = JTBToolkit.makeNodeToken(n6);
    n8 = jj_consume_token(SEMICOLON_CHAR);
                         n7 = JTBToolkit.makeNodeToken(n8);
     {if (true) return new ColumnDeclaration(n0,n1,n3,n5,n7);}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_13(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_14(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_14(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_15(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_15(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_16(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_16(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_17(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_17(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_18(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_18(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_19(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_19(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_20(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_20(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_21(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_21(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_22(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_22(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_23(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_23(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_24(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_24(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_25(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_25(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_26(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_26(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_27(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_27(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_28(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_28(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_29(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_29(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_30(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_30(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_31(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_31(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_32(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_32(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_33(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_33(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_34(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_34(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_35(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_35(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_36(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_36(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_37(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_37(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_38(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_38(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_39(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_39(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_2_40(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_40(); }
    catch(LookaheadSuccess ls) { return true; }
  }

  private boolean jj_3_39() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(LABELED_COMBO_BOX)) return true;
    return false;
  }

  private boolean jj_3_13() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(COMMAND)) return true;
    return false;
  }

  private boolean jj_3_38() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(LIST_PICKER)) return true;
    return false;
  }

  private boolean jj_3_12() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(SEARCHVIEW)) return true;
    return false;
  }

  private boolean jj_3_37() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(LABELED_LIST_PICKER)) return true;
    return false;
  }

  private boolean jj_3_20() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    if (jj_scan_token(CONNECTOR)) return true;
    return false;
  }

  private boolean jj_3_11() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(LISTVIEW)) return true;
    return false;
  }

  private boolean jj_3_36() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(PICKER)) return true;
    return false;
  }

  private boolean jj_3_19() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(COMPONENT)) return true;
    return false;
  }

  private boolean jj_3_10() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(EDITVIEW)) return true;
    return false;
  }

  private boolean jj_3_29() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(SERVICELINK)) return true;
    return false;
  }

  private boolean jj_3_35() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(LABELED_PICKER)) return true;
    return false;
  }

  private boolean jj_3_9() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(MESSAGE)) return true;
    return false;
  }

  private boolean jj_3_28() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    xsp = jj_scanpos;
    if (jj_scan_token(58)) jj_scanpos = xsp;
    if (jj_scan_token(DATATYPE)) return true;
    return false;
  }

  private boolean jj_3_34() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(INPUT_FIELD)) return true;
    return false;
  }

  private boolean jj_3_16() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(COLUMN)) return true;
    return false;
  }

  private boolean jj_3_8() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(56)) jj_scanpos = xsp;
    if (jj_scan_token(SERVICE)) return true;
    return false;
  }

  private boolean jj_3_33() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(LABELED_INPUT_FIELD)) return true;
    return false;
  }

  private boolean jj_3_15() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(DATATYPE)) return true;
    return false;
  }

  private boolean jj_3R_18() {
    if (jj_scan_token(ANNOTATION)) return true;
    return false;
  }

  private boolean jj_3_7() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(EXCEPTION)) return true;
    return false;
  }

  private boolean jj_3R_17() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_18()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_6() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(ENUMERATION)) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(BASETYPE)) return true;
    return false;
  }

  private boolean jj_3_4() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(56)) jj_scanpos = xsp;
    if (jj_scan_token(DATATYPE)) return true;
    return false;
  }

  private boolean jj_3_18() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(44)) {
    jj_scanpos = xsp;
    if (jj_scan_token(45)) {
    jj_scanpos = xsp;
    if (jj_scan_token(46)) return true;
    }
    }
    return false;
  }

  private boolean jj_3_27() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(51)) {
    jj_scanpos = xsp;
    if (jj_scan_token(78)) return true;
    }
    if (jj_scan_token(NAME_IDENTIFIER)) return true;
    if (jj_scan_token(LPAREN_CHAR)) return true;
    return false;
  }

  private boolean jj_3_3() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(ADAPTER)) return true;
    return false;
  }

  private boolean jj_3_26() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(SERVICE)) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(COMPONENT)) return true;
    return false;
  }

  private boolean jj_3_25() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(UNQUALIFIED_TYPE_NAME)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PUBLIC)) return true;
    if (jj_scan_token(APPLICATION)) return true;
    return false;
  }

  private boolean jj_3_14() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(DATATYPE)) return true;
    return false;
  }

  private boolean jj_3_24() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(COMPONENT)) return true;
    return false;
  }

  private boolean jj_3_23() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(SERVICE)) return true;
    return false;
  }

  private boolean jj_3_17() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(DATATYPE)) return true;
    return false;
  }

  private boolean jj_3_22() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(ENUMERATION)) return true;
    return false;
  }

  private boolean jj_3_32() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    if (jj_scan_token(ENUMERATION)) return true;
    return false;
  }

  private boolean jj_3_21() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    xsp = jj_scanpos;
    if (jj_scan_token(57)) jj_scanpos = xsp;
    if (jj_scan_token(DATATYPE)) return true;
    return false;
  }

  private boolean jj_3_31() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    xsp = jj_scanpos;
    if (jj_scan_token(58)) jj_scanpos = xsp;
    if (jj_scan_token(DATATYPE)) return true;
    return false;
  }

  private boolean jj_3_30() {
    if (jj_3R_17()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(53)) {
    jj_scanpos = xsp;
    if (jj_scan_token(54)) {
    jj_scanpos = xsp;
    if (jj_scan_token(55)) return true;
    }
    }
    xsp = jj_scanpos;
    if (jj_scan_token(58)) jj_scanpos = xsp;
    if (jj_scan_token(BASETYPE)) return true;
    return false;
  }

  private boolean jj_3_40() {
    if (jj_3R_17()) return true;
    if (jj_scan_token(PRIVATE)) return true;
    if (jj_scan_token(COMBO_BOX)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public NabuccoParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;

  /** Constructor with InputStream. */
  public NabuccoParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public NabuccoParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new NabuccoParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
  }

  /** Constructor. */
  public NabuccoParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new NabuccoParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
  }

  /** Constructor with generated Token Manager. */
  public NabuccoParser(NabuccoParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
  }

  /** Reinitialise. */
  public void ReInit(NabuccoParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      return token;
    }
    token = oldToken;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    Token errortok = token.next;
    int line = errortok.beginLine, column = errortok.beginColumn;
    String mess = (errortok.kind == 0) ? tokenImage[0] : errortok.image;
    return new ParseException("Parse error at line " + line + ", column " + column + ".  Encountered: " + mess);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}

class JTBToolkit {
   static NodeToken makeNodeToken(Token t) {
      return new NodeToken(t.image.intern(), t.kind, t.beginLine, t.beginColumn, t.endLine, t.endColumn);
   }
}
