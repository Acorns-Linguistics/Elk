/**
 *
 *   @name ExportModifier.java
 *      Class to process export of tags associated with each modifier
 *
 *   @author  Harveym Dan
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

package org.acorns.actions;

import org.acorns.data.*;

public class ExportModifier implements Constants
{
   private KeyboardData data;
   private int[] keyMap;
   
   private String[] modifierText =
   { "", "anyShift?", "anyControl?",      //0
               "anyControl? anyShift?",
     "command?", "command? anyShift?", "command? anyControl?",  // 4
               "command? anyControl? anyShift?",
     "anyOption?", "anyOption? anyShift?",  //8
               "anyOption? anyControl?",
               "anyOption? anyControl? anyShift?",
     "anyOption? command?", "anyOption? command? anyShift?",  //12
               "anyOption? command? anyControl?",
               "anyOption? command? anyControl? anyShift?",

     "caps? ", "caps? anyShift?", "caps? anyControl?",  //16
               "caps? anyControl? anyShift?",
     "caps? command?", "caps? command? anyShift?",   //20
               "caps? command? anyControl?",
               "caps? command? anyControl? anyShift?",
     "caps? anyOption?", "caps? anyOption? anyShift?",  //24
               "caps? anyOption? anyControl?",
               "caps? anyOption? anyControl? anyShift?",
     "caps? anyOption? command?", "caps? anyOption? command? anyShift?", //28
               "caps? anyOption? command? anyControl?",
               "caps? anyOption? command? anyControl? anyShift?",
   };

   private String[] requiredText =
   {  "anyShift ", "anyControl ", "command ", "anyOption ", "caps " };


   public ExportModifier(KeyboardData data)
   {  this.data = data;

      keyMap = new int[MODIFIERS];
      for (int i=0; i<MODIFIERS; i++) keyMap[i] = -1;
   }

   /** Compute the modifier string for the designated modifier
    *
    * @param modifier The modifier index
    * @param keyMapIndex The index to this modifier
    * @return The modifier string or null if this modifier was already handled
    */
   public String computeModifier(int modifier, int keyMapIndex)
   {   if (keyMap[modifier]>=0) return null;

       boolean optional[] = new boolean[MODIFIERS];
       optional[modifier] = true;
       for (int m=modifier+1; m<MODIFIERS; m++)
       {  if ((m&modifier)!=modifier) continue;
          if (!data.equals(modifier, m)) { continue; }
          optional[m] = true;
          for (int j=0; j<m; j++)
          {  if ((j & modifier) != modifier) continue;
             if ((j & m) != j) continue;
             if (!data.equals(m, j)) { optional[m] = false; break; }
             if (keyMap[j]>=0) { optional[m] = false; break; }
             if (optional[j]==false) {  optional[m] = false;  break; }
          }
       }

       keyMap[modifier] = keyMapIndex;
       String optionalString = "";
       for (int m=0; m<MODIFIERS; m++)
       {  if (optional[m]==true)
          { optionalString =  modifierText[m];
            keyMap[m] = keyMapIndex;
          }
       }

       String modifierString = "";
       String required;
       int length;
       int spot, index = 0;
       for (int m=1; m<MODIFIERS; m*=2)
       {  if ((m & modifier) != 0)
          {  required = requiredText[index];
             modifierString += required;
             required = required.trim() + "?";
             spot = optionalString.indexOf(required);
             length = required.length();
             if (spot>0) { spot--; length++; } //Remove an extra space
             if (spot>=0)
                optionalString = optionalString.substring(0, spot)
                         + optionalString.substring(spot + length);
          }
          index++;
       }

       modifierString += optionalString;
       modifierString = modifierString.trim();
       return modifierString;
   }

   /** Method to return the keyMap index of this modifier in the DOM */
   public int[] getKeyMapIndex()  { return keyMap; }

}  // End of ExportModifier class
