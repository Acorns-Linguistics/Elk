/**
 * KeyboardData.java
 *
 * This class holds the data needed for the Keyboard Mapping applications
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
package org.acorns.data;

import java.util.*;

public class KeyboardData implements Constants
{    private char[][] keyboardMap;
     private ArrayList<DeadSequence>[] keyboardSequences;
     private String language;

     /** Constructor to initialize this object */
    @SuppressWarnings("unchecked")
	public KeyboardData(String language)
    {  keyboardMap = new char[MODIFIERS][];
       keyboardSequences = new ArrayList[MODIFIERS];
       for (int i=0; i<MODIFIERS; i++)  keyboardSequences[i] = new ArrayList<DeadSequence>();
       this.language = language;
    }
	
  /** Method to add a modifier code to the keyboard data
   *  @param modifier index into keyMap table
   *  @param keyCode and array of character codes
   */
  public void addModifierKeyMap(int modifier, char[] keyCode)
  { keyboardMap[modifier] = new char[CODES];
    for (int i=0; i<CODES; i++) { keyboardMap[modifier][i] = keyCode[i]; }
  }

  /** Method to return the keyboard mapping for a given modifier
   *
   * @param modifier The desired mofifier
   * @return array of characters for the keyboard mapping
   */
  public char[] getModifierKeyMap(int modifier)
  {   if (keyboardMap[modifier]==null) return null;

      char[] newMap = new char[CODES];

      for (int map=0; map<CODES; map++)
      {  newMap[map] = keyboardMap[modifier][map];  }
      return newMap;
  }

  /** Method to return the keyboard mapping for a given modifier
   *
   * @param modifier The desired mofifier
   * @return array of Strings for the keyboard mapping
   *
   * Note: The control characters are returned in hex format
   */
  public char[] getModifierKeyMapCodes(int modifier)
  {  if (keyboardMap[modifier] == null) return null;

     char[] newMap = new char[CODES];

     for (int map=0; map<CODES; map++)
     {  newMap[map] = keyboardMap[modifier][map];
        if (keyboardMap[modifier][map] == '\0')
        {  switch (map)
           {  case 10:
                 if ((modifier & SHIFT_DOWN) ==0)
                      newMap[map] = 0x00a7;
                 else newMap[map] = 0x00b1;
                 break;
              case 36: newMap[map] = (char)0x000d;  break;
              case 48: newMap[map] = 0x0009;  break;
              case 49: newMap[map] = ' ';     break;
              case 51: newMap[map] = 0x0008;  break;
              case 52: newMap[map] = 0x0003;  break;
              case 53: newMap[map] = 0x001b;  break;
              case 64: newMap[map] = 0x0010;  break;
              case 65: newMap[map] = '.';     break;
              case 66: newMap[map] = 0x001d;  break;
              case 67: newMap[map] = '*';     break;
              case 69: newMap[map] = '+';     break;
              case 70: newMap[map] = 0x001c;  break;
              case 71: newMap[map] = 0x001b;  break;
              case 72: newMap[map] = 0x001f;  break;
              case 75: newMap[map] = '/';     break;
              case 76: newMap[map] = 0x0003;  break;
              case 77: newMap[map] = 0x001e;  break;
              case 78: newMap[map] = '-';     break;
              case 79: newMap[map] = 0x0010;  break;
              case 80: newMap[map] = 0x0010;  break;
              case 81: newMap[map] = '=';     break;
              case 82: newMap[map] = '0';     break;
              case 83: newMap[map] = '1';     break;
              case 84: newMap[map] = '2';     break;
              case 85: newMap[map] = '3';     break;
              case 86: newMap[map] = '4';     break;
              case 87: newMap[map] = '5';     break;
              case 88: newMap[map] = '6';     break;
              case 89: newMap[map] = '7';     break;
              case 91: newMap[map] = '8';     break;
              case 92: newMap[map] = '9';     break;
              case 96: newMap[map] = 0x0010;  break;
              case 97: newMap[map] = 0x0010;  break;
              case 98: newMap[map] = 0x0010;  break;
              case 99: newMap[map] = 0x0010;  break;
              case 100: newMap[map] = 0x0010; break;
              case 101: newMap[map] = 0x0010; break;
              case 102: newMap[map] = 0x0010; break;
              case 103: newMap[map] = 0x0010; break;
              case 104: newMap[map] = 0x0010; break;
              case 105: newMap[map] = 0x0010; break;
              case 106: newMap[map] = 0x0010; break;
              case 107: newMap[map] = 0x0010; break;
              case 108: newMap[map] = 0x0010; break;
              case 109: newMap[map] = 0x0010; break;
              case 110: newMap[map] = 0x0010; break;
              case 111: newMap[map] = 0x0010; break;
              case 112: newMap[map] = 0x0010; break;
              case 113: newMap[map] = 0x0010; break;
              case 114: newMap[map] = 0x0005; break;
              case 115: newMap[map] = 0x0001; break;
              case 116: newMap[map] = 0x000b; break;
              case 117: newMap[map] = 0x007f; break;
              case 118: newMap[map] = 0x0010; break;
              case 119: newMap[map] = 0x0004; break;
              case 120: newMap[map] = 0x0010; break;
              case 121: newMap[map] = 0x000c; break;
              case 122: newMap[map] = 0x0010; break;
              case 123: newMap[map] = 0x001c; break;
              case 124: newMap[map] = 0x001d; break;
              case 125: newMap[map] = 0x001f; break;
              case 126: newMap[map] = 0x001e; break;
           }
           if ((modifier & CTRL_DOWN) !=0)
           {  switch (map)
              {  case 0:  newMap[map] = 0x0001; break;
                 case 1:  newMap[map] = 0x0013; break;
                 case 2:  newMap[map] = 0x0004; break;
                 case 3:  newMap[map] = 0x0006; break;
                 case 4:  newMap[map] = 0x0008; break;
                 case 5:  newMap[map] = 0x0007; break;
                 case 6:  newMap[map] = 0x001a; break;
                 case 7:  newMap[map] = 0x0018; break;
                 case 8:  newMap[map] = 0x0003; break;
                 case 9:  newMap[map] = 0x0016; break;
                 case 10: newMap[map] = '0';    break;
                 case 11: newMap[map] = 0x0002; break;
                 case 12: newMap[map] = 0x0011; break;
                 case 13: newMap[map] = 0x0017; break;
                 case 14: newMap[map] = 0x0005; break;
                 case 15: newMap[map] = 0x0012; break;
                 case 16: newMap[map] = 0x0019; break;
                 case 17: newMap[map] = 0x0014; break;
                 case 27: newMap[map] = 0x001f; break;
                 case 30: newMap[map] = 0x001d; break;
                 case 31: newMap[map] = 0x000f; break;
                 case 32: newMap[map] = 0x0015; break;
                 case 33: newMap[map] = 0x001b; break;
                 case 34: newMap[map] = 0x0009; break;
                 case 35: newMap[map] = 0x0010; break;
                 case 37: newMap[map] = 0x000c; break;
                 case 38: newMap[map] = 0x000a; break;
                 case 40: newMap[map] = 0x000b; break;
                 case 42: newMap[map] = 0x001c; break;
                 case 45: newMap[map] = 0x000e; break;
                 case 46: newMap[map] = 0x000d; break;
                 case 48: newMap[map] = 0x0009; break;
                 case 49: newMap[map] = 0x0000; break;

              }  // End control switch statement
           }     // End if this is a control modifier
        }        // End if code not null
     }           // End for through map codes
     return newMap;
  }  // End of getModifierKeyMapString

  /** Add a new set of dead key sequences for a given modifier
   *
   * @param modifier The modifier in question
   * @param keySequences The array of dead key sequences
   */
  public void addKeySequences
          (int modifier, ArrayList<DeadSequence> keySequences)
  {
      keyboardSequences[modifier] = keySequences;
      Collections.sort(keySequences);
  }

  /** Method to return an array of dead key sequences.
   *
   * @param modifier The modifier in question
   * @return An array of key and outputs
   */
  public ArrayList<DeadSequence> getKeySequences(int modifier)
  {   return keyboardSequences[modifier];  }

  public void setKeySequences(ArrayList<DeadSequence>[] sequences)
  {   keyboardSequences = sequences;  
      for (int i=0; i<sequences.length; i++) { Collections.sort(sequences[i]); }
  }
  
  /** Method to get the language name */
  public String getLanguage() { return language; }

  /** method to display object for debugging purposes */
  public @Override String toString()
  {  StringBuffer buffer = new StringBuffer();
     String[] header ={ "Caps", "Alt", "Meta", "Ctrl", "Shift"};

     String data, key, output;
     int index, character;
     for (int modifier=0; modifier<MODIFIERS; modifier++)
     {   index = 0;
         buffer.append("\nModifier key map");
         if (modifier!=0) buffer.append(":");
         for (int k=MODIFIERS/2; k>=1; k/=2)
         {  if ((k&modifier)!=0) buffer.append(header[index] + " ");
                 index++;
         }

         for (int code=0; code<CODES; code++)
         {   if (code%16==0) buffer.append("\n");

             data = keyboardMap[modifier][code] + "    ";
             character = (int)(keyboardMap[modifier][code]);
             if (character<32 || character==127  )
             {  data = "*" + character + "    ";
                data = data.substring(0,5);
             }
             buffer.append(data);
         }  // End for code

          buffer.append("\nKey Sequences\n");

          for (int seq = 0; seq<keyboardSequences[modifier].size(); seq++)
          {
             key  = keyboardSequences[modifier].get(seq).getKey();
                  output = keyboardSequences[modifier].get(seq).getData();
                  key = (key + "          ").substring(0,10);
                  output = (output + "          ").substring(0,10);

             buffer.append(" key=" + key + " sequence=" + output + "\n");
          }
     }     // End for modifier
     return buffer.toString();
  }  // End toString()

  /** Method to determine if data for two keyboard modifiers are identical.
   *
   * @param f index to the first keyboard modifier
   * @param s index to the second keyboard modifier
   * @return true if identical, false otherwise
   */
  public boolean equals(int f, int s)
  {  boolean r = false;
     if (keyboardMap[f].length != keyboardMap[s].length) return r;
     for (int j=0; j<keyboardMap[f].length; j++)
     { if (Character.toUpperCase(keyboardMap[f][j])
               !=Character.toUpperCase(keyboardMap[s][j])) return r;
     }

     if (keyboardSequences[f].size()!=keyboardSequences[s].size()) return r;
     if (keyboardSequences[f].size()==0) return true;

     for (int j=0; j<keyboardSequences[f].size(); j++)
     {  if (!keyboardSequences[f].get(j).equals(keyboardSequences[s].get(j)))
          return r;
     }
     return true;
  }
}    // End KeyboardData class