/**
 *
 *   @name ModCase.java
 *           Class for logic related to modifiers pressed
 *
 *   @author  HarveyD
 *   Dan Harvey - Professor of Computer Science
 *   Southern Oregon University, 1250 Siskiyou Blvd., Ashland, OR 97520-5028
 *   harveyd@sou.edu
 *   @version 1.00
 *
 *   Copyright 2010, all rights reserved
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * To receive a copy of the GNU Lesser General Public write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.acorns.lib;

import org.acorns.data.*;

public class ModCase implements Constants
{
   private static final String lowerLetters = "abcdefghijklmnopqrstuvwxyz";
   private static final String upperLetters = lowerLetters.toUpperCase();
   private static final String lowerSpcl = "`1234567890-=[]\\;',./";
   private static final String upperSpcl = "~!@#$%^&*()_+{}|:\"<>?";

   public static String setCase(int modifier, String data)
   {  int flag = modifier & (SHIFT_DOWN | CAPS_DOWN);
      switch (flag)
      {  case SHIFT_DOWN:
            return convert(data,lowerLetters+lowerSpcl,upperLetters+upperSpcl);
            
         case CAPS_DOWN:
            return convert(data,lowerLetters+upperSpcl,upperLetters+lowerSpcl);
            
         case SHIFT_DOWN | CAPS_DOWN:
            return convert(data,upperLetters+lowerSpcl,lowerLetters+upperSpcl);
            
         default:
            return convert(data,upperLetters+upperSpcl,lowerLetters+lowerSpcl);
      }  // End of switch
   }     // End of setCase
   
   /** Method to adjust the modifier based on the case of the character
    * 
    * @param modifier The current modifier table
    * @param character The character starting a dead sequence
    * @return The adjusted modifier table
    */
   public static int setModifier(int modifier, char character)
   {  int flag = modifier & (SHIFT_DOWN | CAPS_DOWN);
      
      // Only one of these can be set.
      boolean upperS = upperSpcl.indexOf(character)>=0;
      boolean upperL = upperLetters.indexOf(character)>=0;
      boolean lowerS = lowerSpcl.indexOf(character)>=0;
      boolean lowerL = lowerLetters.indexOf(character)>=0;
      
      switch (flag)
      {  case SHIFT_DOWN:
            if (upperS | upperL) return modifier;
            else return modifier &~ SHIFT_DOWN;
            
         case CAPS_DOWN:
            if (lowerS | upperL) return modifier;
            else return modifier | SHIFT_DOWN;
            
         case SHIFT_DOWN | CAPS_DOWN:
            if (upperS | lowerL) return modifier;
            return modifier & ~SHIFT_DOWN;
            
         default:
            if (lowerS | lowerL) return modifier;
            else return modifier | SHIFT_DOWN;
      }  // End of switch
   }     // End of setModifier

   /** Flip to the modifier with the shift bit flipped */
   public static int flipModifier(int modifier)
   {  return modifier ^ SHIFT_DOWN;  }

   /** Method to convert a character in the source template using the
    *    destination template. The source and destination strings must
    *    match in size and position.
    *
    * @param data The data to convert
    * @param source The source template of letters
    * @param destination The destination template of the same letters
    * @return the converted string
    */
   private static String convert(String data, String source, String destination)
   {   int index;
       char character;
       StringBuffer buf = new StringBuffer();

       for (int i=0; i<data.length(); i++)
       {   character = data.charAt(i);
           index = source.indexOf(character);
           if (index>=0) buf.append(destination.charAt(index));
           else          buf.append(character);
       }
       return buf.toString();
   }

}  // End of ModCase class
