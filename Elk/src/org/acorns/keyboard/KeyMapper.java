/*
 * KeyMapper.java
 *   Draw keyboard and record mapped indigenous font keys
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
package org.acorns.keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.event.*;
import java.util.*;

import org.acorns.data.*;
import org.acorns.deadkeys.*;

public class KeyMapper extends JPanel implements MouseListener, Constants
{
	private static final long serialVersionUID = 1L;

	private static final int ELEMENT_SIZE = 51;

   // Data to control modifier options that are active
   private String[]  ctrls = {"shift", "ctrl", "meta", "alt", "caps", " "};
   private boolean[] pressed;

   // Hash table of key mapping
   private Hashtable<HashKey, HashData> hash;

   // The object controlling the font and the keys.
   private Font font;
   private String fontFace;
   private GlyphVector glyphVector;

   private DeadKeyPanels deadKeys;
   private static char[] translate;

   /** Format of line0: uppercase, lowercase, mac code */
   private static int[][] line0
     = { {'~', '`', 50}, {'!', '1', 18}, {'@', '2', 19}
       , {'#', '3', 20}, {'$', '4', 21}, {'%', '5', 23}
       , {'^', '6', 22}, {'&', '7', 26}, {'*', '8', 28}
       , {'(', '9', 25}, {')', '0', 29}, {'_', '-', 27}
       , {'+', '=', 24} };

   /** Format of line1: uppercase, lowercase, mac code */
   private static int[][] line1
     = { {'Q', ' ',  12}, {'W', ' ', 13}, {'E', ' ', 14}
       , {'R', ' ',  15}, {'T', ' ', 17}, {'Y', ' ', 16}
       , {'U', ' ',  32}, {'I', ' ', 34}, {'O', ' ', 31}
       , {'P', ' ',  35}, {'{', '[', 33}, {'}', ']', 30}
       , {'|', '\\', 42}, };

   /** Format of line2: uppercase, lowercase, mac code */
   private static int[][] line2
     = { {CAPS, ' ', 0}, {'A', ' ', 0}, {'S', ' ', 1}, {'D', ' ', 2}
       , {'F', ' ', 3}, {'G', ' ', 5}, {'H', ' ', 4}
       , {'J', ' ', 38}, {'K', ' ', 40}, {'L', ' ', 37}
       , {':', ';', 41}, {'\"', '\'', 39} };

   /** Format of line3: uppercase, lowercase, mac code */
   private static int[][] line3
     = { {SHFT, ' ', 0}
       , {'Z', ' ', 6}, {'X', ' ', 7}, {'C', ' ', 8}
       , {'V', ' ', 9}, {'B', ' ', 11}, {'N', ' ', 45}
       , {'M', ' ', 46}, {'<', ',', 43}, {'>', '.', 47}
       , {'?', '/', 44}
       , {SHFT,' ', 1 } };

   /** Format of line4: uppercase, lowercase, mac code */
   private static int[][] line4
     = { {CTRL, ' ', 0}, {ALT, ' ', 0}, {CMD, ' ', 0}
       , {SPACE, ' ', 49}
       , {CMD, ' ', 1}, {ALT, ' ', 1}, {CTRL, ' ', 1} };

   private char[] standardDefaults =
   { 'a','s', 'd', 'f', 'h', 'g', 'z', 'x', 'c', 'v',            //0-9
     '\0','b', 'q', 'w', 'e', 'r', 'y', 't', '1', '2',           //10-19
     '3','4', '6', '5', '=', '9', '7', '-', '8', '0',            //20-29
     ']','o', 'u', '[', 'i', 'p', '\0', 'l', 'j', '\'',          //30-39
     'k',';','\\', ',', '/', 'n', 'm', '.', '\0', ' ',           //40-49
     '`','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',   //50-59
     '\0','\0', '\0', '\0', '\0', '.', '\0', '*', '\0', '+',     //60-69
     '\0','\0', '\0', '\0', '\0', '/', '\0', '\0', '-', '\0',    //70-79
     '\0','=', '0', '1', '2', '3', '4', '5', '6', '7',           //80-89
     '\0', '8','9', '\0', '\0', '\0', '\0', '\0', '\0', '\0',    //90-99
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //100-109
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //110-119
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //120-127
   };

   private char[] standardUpperCaseDefaults =
   { 'A','S', 'D', 'F', 'H', 'G', 'Z', 'X', 'C', 'V',            //0-9
     '\0','B', 'Q', 'W', 'E', 'R', 'Y', 'T', '!', '@',           //10-19
     '#','$', '^', '%', '+', '(', '&', '_', '*', ')',            //20-29
     '}','O', 'U', '{', 'I', 'P', '\0', 'L', 'J', '\"',          //30-39
     'K',':','|', '<', '?', 'N', 'M', '>', '\0', ' ',            //40-49
     '~','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',   //50-59
     '\0','\0', '\0', '\0', '\0', '.', '\0', '*', '\0', '+',     //60-69
     '\0','\0', '\0', '\0', '\0', '/', '\0', '\0', '-', '\0',    //70-79
     '\0','=', '0', '1', '2', '3', '4', '5', '6', '7',           //80-89
     '\0', '8','9', '\0', '\0', '\0', '\0', '\0', '\0', '\0',    //90-99
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //100-109
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //110-119
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //120-127
   };

   private char[] controlDefaults =
   { '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //0-9
     '0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '1', '2',     //10-19
     '3','4', '6', '5', '=', '9', '7', '\0', '8', '0',           //20-29
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\'',  //30-39
     '\0',';','\0', ',', '/', '\0', '\0', '.', '\0', '\0',       //40-49
     '`','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',   //50-59
     '\0','\0', '\0', '\0', '\0', '.', '\0', '*', '\0', '+',     //60-69
     '\0','\0', '\0', '\0', '\0', '/', '\0', '\0', '-', '\0',    //70-79
     '=','/', '0', '1', '2', '3', '4', '5', '6', '7',            //80-89
     '\0','8', '9', '\0', '\0', '\0', '\0', '\0', '\0', '\0',    //90-99
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //100-109
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //110-119
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //120-127
   };

private char[] controlUpperCaseDefaults =
   { '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //0-9
     '0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '!', '@',     //10-19
     '#','$', '^', '%', '+', '(', '&', '\0', '*', ')',           //20-29
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\"',  //30-39
     '\0',':', '\0', '<', '?', '\0', '\0', '>', '\0', '\0',      //40-49
     '~','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',   //50-59
     '\0','\0', '\0', '\0', '\0', '.', '\0', '*', '\0', '+',     //60-69
     '\0','\0', '\0', '\0', '\0', '/', '\0', '\0', '-', '\0',    //70-79
     '=','/', '0', '1', '2', '3', '4', '5', '6', '7',            //80-89
     '\0','8', '9', '\0', '\0', '\0', '\0', '\0', '\0', '\0',    //90-99
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //100-109
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //110-119
     '\0','\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //120-127
   };

/** Constructor to create a web page for keyboard mapping */
   public KeyMapper(DeadKeyPanels deadKeys)
   {
      this.deadKeys = deadKeys;
      pressed = new boolean[ctrls.length];
      for (int i=0; i<ctrls.length; i++) pressed[i] = false;
      

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(new RowPanel(line0,0));
      add(new RowPanel(line1, 3));
      add(new RowPanel(line2, 0));
      add(new RowPanel(line3, 3));
      add(new RowPanel(line4, 3));
      add(Box.createVerticalGlue());

      this.addMouseListener(this);
      setBackground(DARKGREY);
      hash = new Hashtable<HashKey, HashData>();
      displayElements();  // Display default keys in page
   }

   /** Method to add default key map hash entries
    *
    * @param hash The hash table containing the keymap
    * @param data The array containing the defaults
    * @param modifier The modifier value
    * @param keyboard The existing keymap indata
    */
   private void addHashRow
           (Hashtable<HashKey, HashData> hash, int[][] data
                             , int modifier, char[] keyboard)
   {
       char key;
       int vkIndex;

       if (fontFace==null) return;
       for (int i=0; i<data.length; i++)
       {
           vkIndex = data[i][KEY];
           key = keyboard[vkIndex];
           if (data[i][TOP]>=STRING) continue;
           if (glyphVector.getGlyphCode(key)<=0) continue;
           if (keyboard[vkIndex]==0 || keyboard[vkIndex]==32) continue;

           hash.put( new HashKey((char)vkIndex, modifier)
                   , new HashData(key));
       }
   }


   /** Method to get a table mapping character codes to MAC keycodes
    *  Create the table if it didn't already exist
    */
   private static char[] getTranslateTable()
   {  if (translate==null)
      {  translate = new char[CODES];
         for (int i=0; i<CODES; i++) translate[i] = (char)255;

         createTranslateLine(translate, line0);
         createTranslateLine(translate, line1);
         createTranslateLine(translate, line2);
         createTranslateLine(translate, line3);
         createTranslateLine(translate, line4);
      }
      return translate;
   }

   /** Method to fill in translate table for a particular line */
   private static void createTranslateLine(char[] table, int[][] line)
   {  int code, key;
      for (int i=0; i<line.length; i++)
      {  key = line[i][KEY];
         code = line[i][TOP];
         if (code==SPACE)  { code = ' '; }
         if (code>= STRING) continue;
         table[code] = (char)key;

         code = line[i][BOTTOM];
         if (code==' ' && line[i][TOP]!=SPACE)
               code = Character.toLowerCase(line[i][TOP]);
         table[code] = (char)key;
      }
   }

   /** Method to convert a character code to a MAC keycode
    *
    * @param code The character code to translate
    * @return The MAC keycode value
    */
   public static char translateCode(char code)
   {  char[] translateTable = getTranslateTable();
      return translateTable[code];
   }

    /** Method to determine if a MAC keycode can be remapped
     *
     * @param code The keycode to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidKeyCode(int code)
    { if (code!=10 && code!=36 && code !=48 && code>=0 && code<=50 && code!=255)
           return true;
      else return false;
    }

    /** MouseListener methods */
    public void mouseClicked(MouseEvent e)  {}
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}
    public void mousePressed(MouseEvent e)
    {  Point point = e.getPoint();
       KeyPanel keyPanel = findKeyPanelAtLocation(point);
       if (keyPanel!=null)
       {   int id = keyPanel.getID();
           if (keyPanel.isControl())
           {  pressed[id] = !pressed[id];
              for (int i=0; i<2; i++)
              {
                  keyPanel = findPanel(ctrls[id] + i);
                  if (keyPanel!=null)
                  {  keyPanel.displayModifier(pressed[id]);

                  }
              }
              displayElements();
              if (deadKeys!=null) deadKeys.setModifier(getModifierIndex());
              return;
           }

           // If made it here, we clicked a normal key (clear its code).
           setKey((char)id, '\0');
           displayElement(keyPanel, '\0', font);
       }
       if (keyPanel==null) System.out.println("none");
       else System.out.println(keyPanel.getName());
       
    }
    public void mouseReleased(MouseEvent e) {}

    /** Method to find KeyPanel under the mouse press
     *
     * @param point Mouse position point
     * @return KeyPanel or null if not found
     */
    private KeyPanel findKeyPanelAtLocation(Point point)
    {   Component component;
        RowPanel rowPanel;
        Point location;

        int count = getComponentCount();
        for (int i=0; i<count; i++)
        {  component = this.getComponent(i);
           if (component instanceof RowPanel)
           {  rowPanel = (RowPanel)component;
              location = SwingUtilities.convertPoint(this, point,rowPanel);
              if (rowPanel.contains(location))
              {  return rowPanel.findKeyPanelAtLocation(location);  }
           }
        }
        return null;
    }

    /** Compute the index for the current modifier
     *
     * @return shift*1 + ctrl*2 + meta*4 + alt*8 + caps*16
     */
    private int getModifierIndex()
    {
        int modifier = 0;
        if (pressed[0]) modifier += SHIFT_DOWN;
        if (pressed[1]) modifier += CTRL_DOWN;
        if (pressed[2]) modifier += META_DOWN;
        if (pressed[3]) modifier += ALT_DOWN;
        if (pressed[4]) modifier += CAPS_DOWN;
        return modifier;
    }

    /** Method to display the list of key elements */
    private void displayElements()
    {   KeyPanel element;
        for (char i=0; i<ELEMENT_SIZE; i++)
        {  element = findPanel("" + (int)i);
           if (element!=null) displayElement(element, i, font);
        }
    }

    /** Method to find the key panel element containing the specified id */
    private KeyPanel findPanel(String id)
    {
        KeyPanel key;
        Component component;
        RowPanel rowPanel;

        int count = getComponentCount();
        for (int i=0; i<count; i++)
        {  component = this.getComponent(i);
           if (component instanceof RowPanel)
           {  rowPanel = (RowPanel)component;
              key = rowPanel.findKeyPanel(id);
              if (key!=null)return key;
           }
        }
        return null;
    }

    /** Method to display a single key on the keyboard. */
    private void displayElement(KeyPanel key, char keyCode, Font fontFace)
    {
       HashKey   hashKey = new HashKey(keyCode, getModifierIndex());
       HashData hashData = hash.get(hashKey);

       char letter = '\0';
       if (hashData!=null) letter = hashData.getLetter();
       key.displayKey(letter, fontFace);
    }

    /** Method to erase the key map settings.
     *
     * @param reset true if we should clear the fontface
     */
    public void clearDefaults(boolean reset)
    {
        hash = new Hashtable<HashKey, HashData>();
        if (reset) fontFace = null;
        displayElements();
    }

    /** Method to set the font and initialize the hash table */
    public void initialize(Font font)
    {   if (font!=null)
        {  font = font.deriveFont(14.0F);
           fontFace = font.getFamily();
        }
        else fontFace = null;
        this.font = font;

        setFont(font);
        hash = new Hashtable<HashKey, HashData>();

        final FontRenderContext fontRenderContext
                           = new FontRenderContext(null, false, false);
        char[] array = new char[65536];
        for (int i=0; i<array.length; i++)  array[i] = (char)i;
        glyphVector = font.createGlyphVector(fontRenderContext, array);

    }

    /** Method to set default keys based on an existing keymap
     *  @param modifier integer value specifying keyboard modifiers
     *  @param keyboard a character array indexed by Mac keycodes
     */
    public void setDefaults(int modifier, char[] keyboard)
    {
        boolean isShift = (modifier & SHIFT_DOWN) != 0;
        boolean isCaps  = (modifier & CAPS_DOWN) != 0;

        if (keyboard==null)
        {  if ((modifier & CTRL_DOWN) != 0)
           {  if (isCaps ^ isShift)  keyboard = controlUpperCaseDefaults;
              else                   keyboard = controlDefaults;
           }
           else
           {  if (isCaps ^ isShift)  keyboard = standardUpperCaseDefaults;
              else                   keyboard = standardDefaults;
           }
       }
       addHashRow(hash, line0, modifier, keyboard);
       addHashRow(hash, line1, modifier, keyboard);
       addHashRow(hash, line2, modifier, keyboard);
       addHashRow(hash, line3, modifier, keyboard);
       if (modifier == getModifierIndex()) displayElements();
    }

   /** Method to set a selected key into the hash table */
    public void setKey(char keyCode, char keyValue)
    {
       HashKey   hashKey = new HashKey(keyCode, getModifierIndex());
       HashData hashData = hash.get(hashKey);

       HashData newData = new HashData(keyValue);

       if (hashData==null)
       {
           if (keyValue=='\0') return;

           hash.put(hashKey, newData);
       }
       else
       {
           hashData = new HashData(keyCode);
           if (keyValue=='\0') hash.remove(hashKey);
           else hash.put(hashKey, newData);
       }
    }

    /** Method to transfer the key mappings to a KeyboardData object
     *
     * @param data KeyboardData object
     * @return KeyboardData object
     */
    public KeyboardData getMap(KeyboardData data)
    {
        char[] keyboardMap;

        HashKey  key;
        HashData hashData;

        for (int mod=0; mod<Constants.MODIFIERS; mod++)
        {  keyboardMap = new char[Constants.CODES];

           for (char code=0; code<Constants.CODES; code++)
           {  key = new HashKey(code, mod);
              hashData = hash.get(key);
              if (hashData!=null)
              {
                  keyboardMap[code] = hashData.getLetter();
              }
           }
           data.addModifierKeyMap(mod, keyboardMap);
        }
        return data;
    }

    /** Local class to contain hash key objects */
    private class HashKey
    {
        private char vkIndex;
        private int  modifier;

        /** Constructor to instantiate a HashData object
         *
         * @param vkIndex virtual keyboard index
         * @param modifier modifier value
         *
         * Note: Modifiers defined as
         *   1 = shift down
         *   2 = ctrl down
         *   4 = meta down
         *   8 = alt down
         *  16 = caps down
         */
        public HashKey(char vkIndex, int modifier)
        {   this.vkIndex = vkIndex;
            this.modifier = modifier;
        }

        //public char getIndex()     { return vkIndex;  }
        //public int  getModifier()  { return modifier; }

        public @Override boolean equals(Object o)
        {   if (getClass() != o.getClass()) return false;
            HashKey key = (HashKey)o;
            return vkIndex==key.vkIndex && modifier==key.modifier;
        }

        public @Override int hashCode() { return 1 + CODES*vkIndex + modifier; }

    }   // End of HashData class

    /** Local class to contain hash objects */
    private class HashData
    {
        private char letter;

        /** Constructor to instantiate a HashData object
         * @param letter keyboard unicode code point
         */
        public HashData(char letter) { this.letter = letter; }
        public char getLetter()    { return letter;   }
    }       // End of HashData class
}          // End of KeyMapper class