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

/** Token Manager. */
public class NabuccoParserTokenManager implements NabuccoParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x2000000000L) != 0L)
         {
            jjmatchedKind = 76;
            return 37;
         }
         if ((active0 & 0x488000000000L) != 0L)
         {
            jjmatchedKind = 76;
            return 8;
         }
         if ((active0 & 0x1000000000L) != 0L)
         {
            jjmatchedKind = 76;
            return 50;
         }
         if ((active0 & 0x40000L) != 0L)
            return 43;
         if ((active0 & 0x840400000000L) != 0L || (active1 & 0x12L) != 0L)
         {
            jjmatchedKind = 76;
            return 17;
         }
         if ((active0 & 0x8L) != 0L)
            return 39;
         if ((active1 & 0x1L) != 0L)
         {
            jjmatchedKind = 76;
            return 23;
         }
         if ((active0 & 0x800134a00000000L) != 0L || (active1 & 0x4L) != 0L)
         {
            jjmatchedKind = 76;
            return 67;
         }
         if ((active0 & 0x7ff000000000000L) != 0L)
         {
            jjmatchedKind = 77;
            return 68;
         }
         if ((active0 & 0xf000200000000000L) != 0L || (active1 & 0x8L) != 0L)
         {
            jjmatchedKind = 76;
            return 26;
         }
         return -1;
      case 1:
         if ((active0 & 0x7ff000000000000L) != 0L)
         {
            jjmatchedKind = 75;
            jjmatchedPos = 1;
            return 68;
         }
         if ((active0 & 0xf800fffe00000000L) != 0L || (active1 & 0x1eL) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 1;
            return 41;
         }
         if ((active1 & 0x1L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 1;
            return 22;
         }
         return -1;
      case 2:
         if ((active0 & 0x7ff000000000000L) != 0L)
         {
            jjmatchedKind = 75;
            jjmatchedPos = 2;
            return 68;
         }
         if ((active0 & 0xf800fffe00000000L) != 0L || (active1 & 0x1fL) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 2;
            return 41;
         }
         return -1;
      case 3:
         if ((active0 & 0x8000000000000L) != 0L)
            return 68;
         if ((active0 & 0x7f7000000000000L) != 0L)
         {
            jjmatchedKind = 75;
            jjmatchedPos = 3;
            return 68;
         }
         if ((active0 & 0xf800fffe00000000L) != 0L || (active1 & 0x1fL) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 3;
            return 41;
         }
         return -1;
      case 4:
         if ((active0 & 0xf800fffe00000000L) != 0L || (active1 & 0x1fL) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 4;
            return 41;
         }
         if ((active0 & 0x7f7000000000000L) != 0L)
         {
            jjmatchedKind = 75;
            jjmatchedPos = 4;
            return 68;
         }
         return -1;
      case 5:
         if ((active0 & 0x32000000000000L) != 0L)
            return 68;
         if ((active1 & 0x14L) != 0L)
            return 41;
         if ((active0 & 0x7c5000000000000L) != 0L)
         {
            jjmatchedKind = 75;
            jjmatchedPos = 5;
            return 68;
         }
         if ((active0 & 0xf800fffe00000000L) != 0L || (active1 & 0xbL) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 5;
            return 41;
         }
         return -1;
      case 6:
         if ((active0 & 0x85000000000000L) != 0L)
            return 68;
         if ((active0 & 0x8a8800000000L) != 0L)
            return 41;
         if ((active0 & 0x740000000000000L) != 0L)
         {
            if (jjmatchedPos != 6)
            {
               jjmatchedKind = 75;
               jjmatchedPos = 6;
            }
            return 68;
         }
         if ((active0 & 0xf800757600000000L) != 0L || (active1 & 0xbL) != 0L)
         {
            if (jjmatchedPos != 6)
            {
               jjmatchedKind = 78;
               jjmatchedPos = 6;
            }
            return 41;
         }
         return -1;
      case 7:
         if ((active0 & 0x100000000000000L) != 0L)
            return 68;
         if ((active0 & 0xf8004d4600000000L) != 0L || (active1 & 0x9L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 7;
            return 41;
         }
         if ((active0 & 0x303000000000L) != 0L || (active1 & 0x2L) != 0L)
            return 41;
         if ((active0 & 0x640000000000000L) != 0L)
         {
            jjmatchedKind = 75;
            jjmatchedPos = 7;
            return 68;
         }
         return -1;
      case 8:
         if ((active0 & 0x440000000000000L) != 0L)
            return 68;
         if ((active0 & 0x200000000000000L) != 0L)
         {
            jjmatchedKind = 75;
            jjmatchedPos = 8;
            return 68;
         }
         if ((active0 & 0x800050400000000L) != 0L)
            return 41;
         if ((active0 & 0xf000484200000000L) != 0L || (active1 & 0x9L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 8;
            return 41;
         }
         return -1;
      case 9:
         if ((active0 & 0x200000000000000L) != 0L)
            return 68;
         if ((active0 & 0x400000000000L) != 0L || (active1 & 0x9L) != 0L)
            return 41;
         if ((active0 & 0xf000084200000000L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 9;
            return 41;
         }
         return -1;
      case 10:
         if ((active0 & 0x84200000000L) != 0L)
            return 41;
         if ((active0 & 0xf000000000000000L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 10;
            return 41;
         }
         return -1;
      case 11:
         if ((active0 & 0xf000000000000000L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 11;
            return 41;
         }
         return -1;
      case 12:
         if ((active0 & 0x4000000000000000L) != 0L)
            return 41;
         if ((active0 & 0xb000000000000000L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 12;
            return 41;
         }
         return -1;
      case 13:
         if ((active0 & 0xb000000000000000L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 13;
            return 41;
         }
         return -1;
      case 14:
         if ((active0 & 0x9000000000000000L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 14;
            return 41;
         }
         if ((active0 & 0x2000000000000000L) != 0L)
            return 41;
         return -1;
      case 15:
         if ((active0 & 0x9000000000000000L) != 0L)
         {
            jjmatchedKind = 78;
            jjmatchedPos = 15;
            return 41;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0, long active1)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         return jjStopAtPos(0, 27);
      case 35:
         return jjStopAtPos(0, 4);
      case 36:
         return jjStopAtPos(0, 30);
      case 37:
         return jjStopAtPos(0, 20);
      case 38:
         return jjStopAtPos(0, 31);
      case 39:
         return jjStopAtPos(0, 24);
      case 40:
         return jjStopAtPos(0, 5);
      case 41:
         return jjStopAtPos(0, 6);
      case 42:
         return jjStopAtPos(0, 11);
      case 43:
         return jjStopAtPos(0, 10);
      case 44:
         return jjStopAtPos(0, 26);
      case 45:
         return jjStopAtPos(0, 9);
      case 46:
         return jjStopAtPos(0, 21);
      case 47:
         jjmatchedKind = 12;
         return jjMoveStringLiteralDfa1_0(0x0L, 0x20000L);
      case 58:
         return jjStopAtPos(0, 22);
      case 59:
         return jjStopAtPos(0, 23);
      case 60:
         jjmatchedKind = 15;
         return jjMoveStringLiteralDfa1_0(0x20000L, 0x0L);
      case 61:
         return jjStopAtPos(0, 13);
      case 62:
         jjmatchedKind = 14;
         return jjMoveStringLiteralDfa1_0(0x10000L, 0x0L);
      case 63:
         return jjStopAtPos(0, 28);
      case 64:
         return jjStopAtPos(0, 84);
      case 65:
         return jjMoveStringLiteralDfa1_0(0xa00000000L, 0x0L);
      case 66:
         return jjMoveStringLiteralDfa1_0(0x1000000000L, 0x0L);
      case 67:
         return jjMoveStringLiteralDfa1_0(0x840400000000L, 0x12L);
      case 68:
         return jjMoveStringLiteralDfa1_0(0x2000000000L, 0x0L);
      case 69:
         return jjMoveStringLiteralDfa1_0(0x114000000000L, 0x0L);
      case 73:
         return jjMoveStringLiteralDfa1_0(0x0L, 0x1L);
      case 76:
         return jjMoveStringLiteralDfa1_0(0xf000200000000000L, 0x8L);
      case 77:
         return jjMoveStringLiteralDfa1_0(0x20000000000L, 0x0L);
      case 80:
         return jjMoveStringLiteralDfa1_0(0x800000000000000L, 0x4L);
      case 83:
         return jjMoveStringLiteralDfa1_0(0x488000000000L, 0x0L);
      case 91:
         return jjStartNfaWithStates_0(0, 18, 43);
      case 93:
         return jjStopAtPos(0, 19);
      case 95:
         return jjStartNfaWithStates_0(0, 3, 39);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x100000000000000L, 0x0L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x4000000000000L, 0x0L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x2000000000000L, 0x0L);
      case 112:
         return jjMoveStringLiteralDfa1_0(0x2e1000000000000L, 0x0L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x410000000000000L, 0x0L);
      case 118:
         return jjMoveStringLiteralDfa1_0(0x8000000000000L, 0x0L);
      case 123:
         return jjStopAtPos(0, 7);
      case 124:
         return jjStopAtPos(0, 25);
      case 125:
         return jjStopAtPos(0, 8);
      case 126:
         return jjStopAtPos(0, 29);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0, long active1)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0, active1);
      return 1;
   }
   switch(curChar)
   {
      case 42:
         if ((active1 & 0x20000L) != 0L)
            return jjStopAtPos(1, 81);
         break;
      case 61:
         if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(1, 16);
         else if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(1, 17);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0xf801003000000000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa2_0(active0, 0x100000000000000L, active1, 0L);
      case 100:
         return jjMoveStringLiteralDfa2_0(active0, 0x100800000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x2004a8000000000L, active1, 0L);
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x10000000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000000L, active1, 0xcL);
      case 109:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000000000000L, active1, 0L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000000000L, active1, 0x1L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x8840400000000L, active1, 0x12L);
      case 112:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x4c0000000000000L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x20000000000000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa2_0(active0, 0x4010000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0, active1);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(0, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0, active1);
      return 2;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x400400800000000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa3_0(active0, 0xf020000000000000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa3_0(active0, 0x1010000000000L, active1, 0x4L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x88100000000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0x10L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x800400000000L, active1, 0x2L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000000000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000000000000L, active1, 0L);
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000200000000L, active1, 0x1L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0xa10088000000000L, active1, 0L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x100221000000000L, active1, 0x8L);
      case 116:
         return jjMoveStringLiteralDfa3_0(active0, 0x4002000000000L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0, active1);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(1, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0, active1);
      return 3;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x800002000000000L, active1, 0L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x2L);
      case 100:
         if ((active0 & 0x8000000000000L) != 0L)
            return jjStartNfaWithStates_0(3, 51, 68);
         break;
      case 101:
         return jjMoveStringLiteralDfa4_0(active0, 0xf004011000000000L, active1, 0L);
      case 107:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000000000L, active1, 0x4L);
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000200000000L, active1, 0L);
      case 109:
         return jjMoveStringLiteralDfa4_0(active0, 0x804000000000L, active1, 0L);
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x400040000000000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x12000000000000L, active1, 0L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0xc00000000L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000000L, active1, 0L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x200020000000000L, active1, 0L);
      case 116:
         return jjMoveStringLiteralDfa4_0(active0, 0x140300000000000L, active1, 0x8L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0x11L);
      case 118:
         return jjMoveStringLiteralDfa4_0(active0, 0x80088000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0, active1);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(2, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0, active1);
      return 4;
   }
   switch(curChar)
   {
      case 80:
         return jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0x8L);
      case 86:
         return jjMoveStringLiteralDfa5_0(active0, 0x300000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x81820000000000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x40044000000000L, active1, 0x4L);
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x220088200000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0xf000000000000000L, active1, 0L);
      case 109:
         return jjMoveStringLiteralDfa5_0(active0, 0x800000000000000L, active1, 0x10L);
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000000000000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000000L, active1, 0x2L);
      case 112:
         return jjMoveStringLiteralDfa5_0(active0, 0x10000000000L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x102000000000000L, active1, 0L);
      case 115:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000000000000L, active1, 0L);
      case 116:
         return jjMoveStringLiteralDfa5_0(active0, 0x3800000000L, active1, 0x1L);
      case 119:
         return jjMoveStringLiteralDfa5_0(active0, 0x10000000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0, active1);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(3, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0, active1);
      return 5;
   }
   switch(curChar)
   {
      case 66:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x2L);
      case 70:
         return jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0x1L);
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x100000000000000L, active1, 0L);
      case 99:
         if ((active0 & 0x20000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 53, 68);
         return jjMoveStringLiteralDfa6_0(active0, 0x400c8200000000L, active1, 0L);
      case 100:
         return jjMoveStringLiteralDfa6_0(active0, 0x4000000000000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa6_0(active0, 0xf800000800000000L, active1, 0L);
      case 103:
         return jjMoveStringLiteralDfa6_0(active0, 0x1020000000000L, active1, 0L);
      case 104:
         return jjMoveStringLiteralDfa6_0(active0, 0x400000000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x400300000000000L, active1, 0x8L);
      case 110:
         if ((active1 & 0x10L) != 0L)
            return jjStartNfaWithStates_0(5, 68, 41);
         return jjMoveStringLiteralDfa6_0(active0, 0x800400000000L, active1, 0L);
      case 114:
         if ((active1 & 0x4L) != 0L)
            return jjStartNfaWithStates_0(5, 66, 41);
         return jjMoveStringLiteralDfa6_0(active0, 0x4000000000L, active1, 0L);
      case 115:
         if ((active0 & 0x10000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 52, 68);
         return jjMoveStringLiteralDfa6_0(active0, 0x200000000000000L, active1, 0L);
      case 116:
         if ((active0 & 0x2000000000000L) != 0L)
            return jjStartNfaWithStates_0(5, 49, 68);
         return jjMoveStringLiteralDfa6_0(active0, 0x80010000000000L, active1, 0L);
      case 121:
         return jjMoveStringLiteralDfa6_0(active0, 0x3000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0, active1);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(4, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0, active1);
      return 6;
   }
   switch(curChar)
   {
      case 86:
         return jjMoveStringLiteralDfa7_0(active0, 0x400000000000L, active1, 0L);
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x4200000000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x100000000000000L, active1, 0x8L);
      case 100:
         if ((active0 & 0x800000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 47, 41);
         return jjMoveStringLiteralDfa7_0(active0, 0xf000000000000000L, active1, 0L);
      case 101:
         if ((active0 & 0x8000000000L) != 0L)
         {
            jjmatchedKind = 39;
            jjmatchedPos = 6;
         }
         else if ((active0 & 0x20000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 41, 41);
         else if ((active0 & 0x1000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 48, 68);
         else if ((active0 & 0x80000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 55, 68);
         return jjMoveStringLiteralDfa7_0(active0, 0x400380400000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa7_0(active0, 0x10000000000L, active1, 0x1L);
      case 111:
         return jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0x2L);
      case 112:
         return jjMoveStringLiteralDfa7_0(active0, 0x3000000000L, active1, 0L);
      case 114:
         if ((active0 & 0x800000000L) != 0L)
            return jjStartNfaWithStates_0(6, 35, 41);
         break;
      case 115:
         if ((active0 & 0x4000000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 50, 68);
         break;
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0xa40040000000000L, active1, 0L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0, active1);
}
private int jjMoveStringLiteralDfa7_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(5, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0, active1);
      return 7;
   }
   switch(curChar)
   {
      case 67:
         return jjMoveStringLiteralDfa8_0(active0, 0x2000000000000000L, active1, 0L);
      case 73:
         return jjMoveStringLiteralDfa8_0(active0, 0x1000000000000000L, active1, 0L);
      case 76:
         return jjMoveStringLiteralDfa8_0(active0, 0x8000080000000000L, active1, 0L);
      case 80:
         return jjMoveStringLiteralDfa8_0(active0, 0x4000000000000000L, active1, 0L);
      case 101:
         if ((active0 & 0x1000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 36, 41);
         else if ((active0 & 0x2000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 37, 41);
         return jjMoveStringLiteralDfa8_0(active0, 0xa40000000000000L, active1, 0x1L);
      case 105:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000000000L, active1, 0L);
      case 107:
         return jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0x8L);
      case 110:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000400000000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0x50000000000L, active1, 0L);
      case 116:
         if ((active0 & 0x100000000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 56, 68);
         return jjMoveStringLiteralDfa8_0(active0, 0x4200000000L, active1, 0L);
      case 119:
         if ((active0 & 0x100000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 44, 41);
         else if ((active0 & 0x200000000000L) != 0L)
            return jjStartNfaWithStates_0(7, 45, 41);
         break;
      case 120:
         if ((active1 & 0x2L) != 0L)
            return jjStartNfaWithStates_0(7, 65, 41);
         break;
      default :
         break;
   }
   return jjStartNfa_0(6, active0, active1);
}
private int jjMoveStringLiteralDfa8_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(6, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0, active1);
      return 8;
   }
   switch(curChar)
   {
      case 100:
         if ((active0 & 0x40000000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 54, 68);
         break;
      case 101:
         return jjMoveStringLiteralDfa9_0(active0, 0x400000000000L, active1, 0x8L);
      case 105:
         return jjMoveStringLiteralDfa9_0(active0, 0xc000084200000000L, active1, 0L);
      case 108:
         return jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0x1L);
      case 110:
         if ((active0 & 0x10000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 40, 41);
         return jjMoveStringLiteralDfa9_0(active0, 0x1200000000000000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa9_0(active0, 0x2000000000000000L, active1, 0L);
      case 114:
         if ((active0 & 0x40000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 42, 41);
         else if ((active0 & 0x800000000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 59, 41);
         break;
      case 116:
         if ((active0 & 0x400000000L) != 0L)
            return jjStartNfaWithStates_0(8, 34, 41);
         else if ((active0 & 0x400000000000000L) != 0L)
            return jjStartNfaWithStates_0(8, 58, 68);
         break;
      default :
         break;
   }
   return jjStartNfa_0(7, active0, active1);
}
private int jjMoveStringLiteralDfa9_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(7, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0, active1);
      return 9;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa10_0(active0, 0x4000000000000000L, active1, 0L);
      case 100:
         if ((active1 & 0x1L) != 0L)
            return jjStartNfaWithStates_0(9, 64, 41);
         break;
      case 109:
         return jjMoveStringLiteralDfa10_0(active0, 0x2000000000000000L, active1, 0L);
      case 110:
         return jjMoveStringLiteralDfa10_0(active0, 0x80000000000L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa10_0(active0, 0x4200000000L, active1, 0L);
      case 112:
         return jjMoveStringLiteralDfa10_0(active0, 0x1000000000000000L, active1, 0L);
      case 114:
         if ((active1 & 0x8L) != 0L)
            return jjStartNfaWithStates_0(9, 67, 41);
         break;
      case 115:
         return jjMoveStringLiteralDfa10_0(active0, 0x8000000000000000L, active1, 0L);
      case 116:
         if ((active0 & 0x200000000000000L) != 0L)
            return jjStartNfaWithStates_0(9, 57, 68);
         break;
      case 119:
         if ((active0 & 0x400000000000L) != 0L)
            return jjStartNfaWithStates_0(9, 46, 41);
         break;
      default :
         break;
   }
   return jjStartNfa_0(8, active0, active1);
}
private int jjMoveStringLiteralDfa10_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(8, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0, 0L);
      return 10;
   }
   switch(curChar)
   {
      case 98:
         return jjMoveStringLiteralDfa11_0(active0, 0x2000000000000000L);
      case 107:
         if ((active0 & 0x80000000000L) != 0L)
            return jjStartNfaWithStates_0(10, 43, 41);
         return jjMoveStringLiteralDfa11_0(active0, 0x4000000000000000L);
      case 110:
         if ((active0 & 0x200000000L) != 0L)
            return jjStartNfaWithStates_0(10, 33, 41);
         else if ((active0 & 0x4000000000L) != 0L)
            return jjStartNfaWithStates_0(10, 38, 41);
         break;
      case 116:
         return jjMoveStringLiteralDfa11_0(active0, 0x8000000000000000L);
      case 117:
         return jjMoveStringLiteralDfa11_0(active0, 0x1000000000000000L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0, 0L);
}
private int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0, 0L);
      return 11;
   }
   switch(curChar)
   {
      case 80:
         return jjMoveStringLiteralDfa12_0(active0, 0x8000000000000000L);
      case 101:
         return jjMoveStringLiteralDfa12_0(active0, 0x4000000000000000L);
      case 111:
         return jjMoveStringLiteralDfa12_0(active0, 0x2000000000000000L);
      case 116:
         return jjMoveStringLiteralDfa12_0(active0, 0x1000000000000000L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0, 0L);
}
private int jjMoveStringLiteralDfa12_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0, 0L);
      return 12;
   }
   switch(curChar)
   {
      case 66:
         return jjMoveStringLiteralDfa13_0(active0, 0x2000000000000000L);
      case 70:
         return jjMoveStringLiteralDfa13_0(active0, 0x1000000000000000L);
      case 105:
         return jjMoveStringLiteralDfa13_0(active0, 0x8000000000000000L);
      case 114:
         if ((active0 & 0x4000000000000000L) != 0L)
            return jjStartNfaWithStates_0(12, 62, 41);
         break;
      default :
         break;
   }
   return jjStartNfa_0(11, active0, 0L);
}
private int jjMoveStringLiteralDfa13_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(11, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(12, active0, 0L);
      return 13;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa14_0(active0, 0x8000000000000000L);
      case 105:
         return jjMoveStringLiteralDfa14_0(active0, 0x1000000000000000L);
      case 111:
         return jjMoveStringLiteralDfa14_0(active0, 0x2000000000000000L);
      default :
         break;
   }
   return jjStartNfa_0(12, active0, 0L);
}
private int jjMoveStringLiteralDfa14_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(12, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(13, active0, 0L);
      return 14;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa15_0(active0, 0x1000000000000000L);
      case 107:
         return jjMoveStringLiteralDfa15_0(active0, 0x8000000000000000L);
      case 120:
         if ((active0 & 0x2000000000000000L) != 0L)
            return jjStartNfaWithStates_0(14, 61, 41);
         break;
      default :
         break;
   }
   return jjStartNfa_0(13, active0, 0L);
}
private int jjMoveStringLiteralDfa15_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(13, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(14, active0, 0L);
      return 15;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa16_0(active0, 0x8000000000000000L);
      case 108:
         return jjMoveStringLiteralDfa16_0(active0, 0x1000000000000000L);
      default :
         break;
   }
   return jjStartNfa_0(14, active0, 0L);
}
private int jjMoveStringLiteralDfa16_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(14, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(15, active0, 0L);
      return 16;
   }
   switch(curChar)
   {
      case 100:
         if ((active0 & 0x1000000000000000L) != 0L)
            return jjStartNfaWithStates_0(16, 60, 41);
         break;
      case 114:
         if ((active0 & 0x8000000000000000L) != 0L)
            return jjStartNfaWithStates_0(16, 63, 41);
         break;
      default :
         break;
   }
   return jjStartNfa_0(15, active0, 0L);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 67;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x100001200L & l) != 0L)
                  {
                     if (kind > 2)
                        kind = 2;
                  }
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 68:
                  if (curChar == 46)
                     jjAddStates(0, 1);
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 61;
                  break;
               case 1:
                  if (curChar == 10 && kind > 1)
                     kind = 1;
                  break;
               case 2:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 3:
                  if ((0x100001200L & l) != 0L)
                     kind = 2;
                  break;
               case 43:
                  if ((0x3000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(44, 47);
                  break;
               case 44:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 45;
                  break;
               case 45:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 46;
                  break;
               case 46:
                  if ((0x2040000000000L & l) != 0L)
                     jjCheckNAdd(47);
                  break;
               case 60:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 61;
                  break;
               case 63:
                  if (curChar == 46)
                     jjAddStates(0, 1);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 22:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 78)
                        kind = 78;
                     jjCheckNAdd(41);
                  }
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 21;
                  break;
               case 23:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 78)
                        kind = 78;
                     jjCheckNAdd(41);
                  }
                  if ((0x87fffffeL & l) != 0L)
                  {
                     if (kind > 76)
                        kind = 76;
                     jjCheckNAdd(39);
                  }
                  else if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 22;
                  break;
               case 0:
                  if ((0x87fffffeL & l) != 0L)
                  {
                     if (kind > 76)
                        kind = 76;
                     jjCheckNAdd(39);
                  }
                  else if ((0x7fffffe00000000L & l) != 0L)
                  {
                     if (kind > 77)
                        kind = 77;
                     jjCheckNAddStates(2, 6);
                  }
                  else if (curChar == 91)
                     jjstateSet[jjnewStateCnt++] = 43;
                  if ((0x7fffffeL & l) != 0L)
                     jjCheckNAdd(41);
                  if (curChar == 66)
                     jjAddStates(7, 8);
                  else if (curChar == 68)
                     jjstateSet[jjnewStateCnt++] = 37;
                  else if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 31;
                  else if (curChar == 76)
                     jjstateSet[jjnewStateCnt++] = 26;
                  else if (curChar == 73)
                     jjstateSet[jjnewStateCnt++] = 23;
                  else if (curChar == 67)
                     jjstateSet[jjnewStateCnt++] = 17;
                  else if (curChar == 83)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 68:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 75)
                        kind = 75;
                     jjCheckNAdd(58);
                  }
                  if ((0x7fffffe00000000L & l) != 0L)
                     jjCheckNAddTwoStates(62, 63);
                  if ((0x7fffffe00000000L & l) != 0L)
                  {
                     if (kind > 77)
                        kind = 77;
                     jjCheckNAddTwoStates(59, 60);
                  }
                  break;
               case 17:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 78)
                        kind = 78;
                     jjCheckNAdd(41);
                  }
                  if ((0x87fffffeL & l) != 0L)
                  {
                     if (kind > 76)
                        kind = 76;
                     jjCheckNAdd(39);
                  }
                  else if (curChar == 104)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 26:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 78)
                        kind = 78;
                     jjCheckNAdd(41);
                  }
                  if ((0x87fffffeL & l) != 0L)
                  {
                     if (kind > 76)
                        kind = 76;
                     jjCheckNAdd(39);
                  }
                  else if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 25;
                  break;
               case 50:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 78)
                        kind = 78;
                     jjCheckNAdd(41);
                  }
                  if ((0x87fffffeL & l) != 0L)
                  {
                     if (kind > 76)
                        kind = 76;
                     jjCheckNAdd(39);
                  }
                  else if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 55;
                  else if (curChar == 121)
                     jjstateSet[jjnewStateCnt++] = 49;
                  break;
               case 8:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 78)
                        kind = 78;
                     jjCheckNAdd(41);
                  }
                  if ((0x87fffffeL & l) != 0L)
                  {
                     if (kind > 76)
                        kind = 76;
                     jjCheckNAdd(39);
                  }
                  else if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 67:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 78)
                        kind = 78;
                     jjCheckNAdd(41);
                  }
                  if ((0x87fffffeL & l) != 0L)
                  {
                     if (kind > 76)
                        kind = 76;
                     jjCheckNAdd(39);
                  }
                  break;
               case 37:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 78)
                        kind = 78;
                     jjCheckNAdd(41);
                  }
                  if ((0x87fffffeL & l) != 0L)
                  {
                     if (kind > 76)
                        kind = 76;
                     jjCheckNAdd(39);
                  }
                  else if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 4:
                  if (curChar == 103 && kind > 69)
                     kind = 69;
                  break;
               case 5:
               case 25:
                  if (curChar == 110)
                     jjCheckNAdd(4);
                  break;
               case 6:
                  if (curChar == 105)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 7:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 9:
                  if (curChar == 83)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if (curChar == 114 && kind > 69)
                     kind = 69;
                  break;
               case 11:
               case 19:
                  if (curChar == 101)
                     jjCheckNAdd(10);
                  break;
               case 12:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 13:
                  if (curChar == 99)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 14:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 15:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 16:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 18:
                  if (curChar == 67)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 20:
                  if (curChar == 103)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 21:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 24:
                  if (curChar == 73)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 27:
                  if (curChar == 76)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 28:
                  if (curChar == 116 && kind > 69)
                     kind = 69;
                  break;
               case 29:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 28;
                  break;
               case 30:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 31:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 30;
                  break;
               case 32:
                  if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 31;
                  break;
               case 33:
                  if (curChar == 101 && kind > 69)
                     kind = 69;
                  break;
               case 34:
                  if (curChar == 108)
                     jjCheckNAdd(33);
                  break;
               case 35:
                  if (curChar == 98)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 36:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 35;
                  break;
               case 38:
                  if (curChar == 68)
                     jjstateSet[jjnewStateCnt++] = 37;
                  break;
               case 39:
                  if ((0x87fffffeL & l) == 0L)
                     break;
                  if (kind > 76)
                     kind = 76;
                  jjCheckNAdd(39);
                  break;
               case 40:
                  if ((0x7fffffeL & l) != 0L)
                     jjCheckNAdd(41);
                  break;
               case 41:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 78)
                     kind = 78;
                  jjCheckNAdd(41);
                  break;
               case 42:
                  if (curChar == 91)
                     jjstateSet[jjnewStateCnt++] = 43;
                  break;
               case 47:
                  if (curChar == 93)
                     kind = 80;
                  break;
               case 48:
                  if (curChar == 66)
                     jjAddStates(7, 8);
                  break;
               case 49:
                  if (curChar == 116)
                     jjCheckNAdd(33);
                  break;
               case 51:
                  if (curChar == 110 && kind > 69)
                     kind = 69;
                  break;
               case 52:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 51;
                  break;
               case 53:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 52;
                  break;
               case 54:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 53;
                  break;
               case 55:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 54;
                  break;
               case 56:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 55;
                  break;
               case 57:
                  if ((0x7fffffe00000000L & l) == 0L)
                     break;
                  if (kind > 77)
                     kind = 77;
                  jjCheckNAddStates(2, 6);
                  break;
               case 58:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 75)
                     kind = 75;
                  jjCheckNAdd(58);
                  break;
               case 59:
                  if ((0x7fffffe00000000L & l) == 0L)
                     break;
                  if (kind > 77)
                     kind = 77;
                  jjCheckNAddTwoStates(59, 60);
                  break;
               case 61:
                  if ((0x7fffffe00000000L & l) == 0L)
                     break;
                  if (kind > 77)
                     kind = 77;
                  jjCheckNAddTwoStates(60, 61);
                  break;
               case 62:
                  if ((0x7fffffe00000000L & l) != 0L)
                     jjCheckNAddTwoStates(62, 63);
                  break;
               case 64:
                  if ((0x7fffffe00000000L & l) != 0L)
                     jjCheckNAddTwoStates(64, 63);
                  break;
               case 65:
                  if ((0x7fffffeL & l) != 0L)
                     jjCheckNAdd(66);
                  break;
               case 66:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 79)
                     kind = 79;
                  jjCheckNAdd(66);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 67 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_2(int pos, long active0, long active1)
{
   return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0, active1), pos + 1);
}
private int jjMoveStringLiteralDfa0_2()
{
   switch(curChar)
   {
      case 9:
         return jjStartNfaWithStates_2(0, 86, 0);
      case 12:
         return jjStartNfaWithStates_2(0, 87, 0);
      case 32:
         return jjStartNfaWithStates_2(0, 85, 0);
      default :
         return jjMoveNfa_2(1, 0);
   }
}
private int jjStartNfaWithStates_2(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_2(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_2(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 4;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0xffffffffffffdbffL & l) != 0L)
                  {
                     if (kind > 89)
                        kind = 89;
                     jjCheckNAdd(0);
                  }
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 90)
                        kind = 90;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 0:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  kind = 89;
                  jjCheckNAdd(0);
                  break;
               case 2:
                  if (curChar == 10 && kind > 90)
                     kind = 90;
                  break;
               case 3:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
               case 0:
                  kind = 89;
                  jjCheckNAdd(0);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
               case 0:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 89)
                     kind = 89;
                  jjCheckNAdd(0);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private int jjMoveStringLiteralDfa0_1()
{
   switch(curChar)
   {
      case 42:
         return jjMoveStringLiteralDfa1_1(0x80000L);
      default :
         return 1;
   }
}
private int jjMoveStringLiteralDfa1_1(long active1)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 1;
   }
   switch(curChar)
   {
      case 47:
         if ((active1 & 0x80000L) != 0L)
            return jjStopAtPos(1, 83);
         break;
      default :
         return 2;
   }
   return 2;
}
static final int[] jjnextStates = {
   64, 65, 58, 59, 60, 62, 63, 50, 56, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, "\137", "\43", "\50", "\51", "\173", "\175", "\55", "\53", 
"\52", "\57", "\75", "\76", "\74", "\76\75", "\74\75", "\133", "\135", "\45", "\56", 
"\72", "\73", "\47", "\174", "\54", "\41", "\77", "\176", "\44", "\46", null, 
"\101\160\160\154\151\143\141\164\151\157\156", "\103\157\155\160\157\156\145\156\164", "\101\144\141\160\164\145\162", 
"\102\141\163\145\164\171\160\145", "\104\141\164\141\164\171\160\145", 
"\105\156\165\155\145\162\141\164\151\157\156", "\123\145\162\166\151\143\145", "\105\170\143\145\160\164\151\157\156", 
"\115\145\163\163\141\147\145", "\103\157\156\156\145\143\164\157\162", 
"\123\145\162\166\151\143\145\114\151\156\153", "\105\144\151\164\126\151\145\167", "\114\151\163\164\126\151\145\167", 
"\123\145\141\162\143\150\126\151\145\167", "\103\157\155\155\141\156\144", "\160\141\143\153\141\147\145", 
"\151\155\160\157\162\164", "\145\170\164\145\156\144\163", "\166\157\151\144", 
"\164\150\162\157\167\163", "\160\165\142\154\151\143", "\160\162\157\164\145\143\164\145\144", 
"\160\162\151\166\141\164\145", "\141\142\163\164\162\141\143\164", 
"\160\145\162\163\151\163\164\145\156\164", "\164\162\141\156\163\151\145\156\164", 
"\120\141\162\141\155\145\164\145\162", "\114\141\142\145\154\145\144\111\156\160\165\164\106\151\145\154\144", 
"\114\141\142\145\154\145\144\103\157\155\142\157\102\157\170", "\114\141\142\145\154\145\144\120\151\143\153\145\162", 
"\114\141\142\145\154\145\144\114\151\163\164\120\151\143\153\145\162", "\111\156\160\165\164\106\151\145\154\144", 
"\103\157\155\142\157\102\157\170", "\120\151\143\153\145\162", "\114\151\163\164\120\151\143\153\145\162", 
"\103\157\154\165\155\156", null, null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, null, null, };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
   "IN_COMMENT",
   "IN_ANNOTATION",
};

/** Lex State array. */
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, 1, -1, 0, 2, -1, -1, -1, -1, -1, 0, 
};
static final long[] jjtoToken = {
   0xfffffffefffffff9L, 0x201f83fL, 
};
static final long[] jjtoSkip = {
   0x6L, 0x4f80000L, 
};
static final long[] jjtoMore = {
   0x0L, 0x60000L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[67];
private final int[] jjstateSet = new int[134];
protected char curChar;
/** Constructor. */
public NabuccoParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public NabuccoParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 67; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 3 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   for (;;)
   {
     switch(curLexState)
     {
       case 0:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_0();
         break;
       case 1:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_1();
         if (jjmatchedPos == 0 && jjmatchedKind > 82)
         {
            jjmatchedKind = 82;
         }
         break;
       case 2:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_2();
         break;
     }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else if ((jjtoSkip[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
      if (jjnewLexState[jjmatchedKind] != -1)
        curLexState = jjnewLexState[jjmatchedKind];
        curPos = 0;
        jjmatchedKind = 0x7fffffff;
        try {
           curChar = input_stream.readChar();
           continue;
        }
        catch (java.io.IOException e1) { }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
   }
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
