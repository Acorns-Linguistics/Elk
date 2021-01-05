/**
 *
 *   @name KeyboardHandler.java
 *      Class to process to remap keyboards at runtime
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

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.xml.sax.*;

import javax.xml.parsers.*;

import org.acorns.data.*;

/** Notes: KeyEventDispatcher is global across the JVM whereas
 *         KeyListener is specific to a particular component
 */
public class KeyboardHandler 
        implements KeyEventDispatcher, Constants, KeyListener
{
   private static boolean libraryExists = false;
   private char[][] keyMap;
   private ArrayList<DeadSequence>[] sequences;
   private String language;

   private String previousKey;
   private int firstModifier, previousModifier;

   /** Constructor to set up the .keylayout data structures */
   @SuppressWarnings("unchecked")
public KeyboardHandler(URL url)
           throws SAXException, ParserConfigurationException, 
                  IOException, NoSuchMethodException
   {
      KeyboardData data;
      JLabel label = new JLabel(" ");
      ImportKeyboard keyboard = new ImportKeyboard(url, label);
      data = keyboard.getData();
      String text = label.getText();
      if (text.length()!=1)  {  throw new IOException(text); }

      keyMap = new char[MODIFIERS][];
      sequences = new ArrayList[MODIFIERS];
      language = data.getLanguage();

      // Load the data for processing
      for (int i=0; i<MODIFIERS; i++)
      {  keyMap[i] = data.getModifierKeyMap(i);
         if (keyMap==null) continue;
         sequences[i] = data.getKeySequences(i);
      }
      previousKey = "";
  }

   /** Attempt to load the native library at startup */
   static  // Load the native library if it exists.
   {  try
      { 
	      String bits = System.getProperty("sun.arch.data.model");
	   		System.out.println(bits + " " + System.getProperty("java.library.path"));
	      System.loadLibrary("elkDynamicLibrary64");
          libraryExists = true; 
      }
      catch(Throwable t) 
   	  { 
          JOptionPane.showMessageDialog(null, t.getMessage());
          t.printStackTrace();

    	  libraryExists = false; 
      }
   }

   /** Method to translate key sequences based on .keylayout specifications */
   public String processChar(char character, int modifier)
   {
      character = convertChar(character, modifier);
      if (previousKey.length()==0) firstModifier = modifier;
      if (previousKey.length()==1) previousModifier = modifier;
      previousKey += character;

      String output = computeOutput(modifier);
      return output;
   }  // End processCharacter()
   
   /** Method to establish hook to intercept JVM keyboard actions */
   public void setJavaHook()
   {  KeyboardFocusManager focus
              = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      focus.addKeyEventDispatcher(this);
   }

   /* Method to release hook to intercept JVM keyboard actions */
   public void releaseKeyboardHook()
   {  if (libraryExists) releaseOSHook();
      else
      {   KeyboardFocusManager focus
              = KeyboardFocusManager.getCurrentKeyboardFocusManager();
          focus.removeKeyEventDispatcher(this);
      }
   }

   /** Method to determine if native library is  loaded */
   public boolean isLibraryLoaded() { return libraryExists; }

   /** Method to create OS hook to intercept all keyboard actions */
   public native void setOSHook();
   
   /** Method to release OS Hook to intercept all keyboard actions */
   public native void releaseOSHook();

   /** Method to intercept keyboard characters that are entered */
   public boolean dispatchKeyEvent(KeyEvent e)
   { if (e.getID()!=KeyEvent.KEY_TYPED) return false;

      char character = e.getKeyChar();
      int modifiers = e.getModifiersEx();
      int newModifiers = 0;
      if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0)
    	  newModifiers |= SHIFT_DOWN;
      if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0)
    	  newModifiers |= ALT_DOWN;
      if ((modifiers & InputEvent.META_DOWN_MASK) != 0)
    	  newModifiers |= META_DOWN;
      if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0)
    	  newModifiers |= CTRL_DOWN;
      
      modifiers = newModifiers;
      
      if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK))
         modifiers += CAPS_DOWN;

      String sequence = processChar(character, modifiers);
      if (sequence==null) return true;
      if (sequence.length()==0) { return true; }

      Component component = e.getComponent();
      if (component instanceof JTextComponent)
      {  JTextComponent field = (JTextComponent)component;
         int start = field.getSelectionStart();
         int end = field.getSelectionEnd();
         String text = field.getText();
         text = text.substring(0,start) + sequence + text.substring(end);
         field.setText(text);
         field.setCaretPosition(start + sequence.length());
         return true;
      }
      return false;
   }

   /** Handle the key typed event from a text field. */
    public void keyTyped(KeyEvent e)  { if (dispatchKeyEvent(e)) e.consume();  }
    public void keyPressed(KeyEvent e)  {}
    public void keyReleased(KeyEvent e) {}

   /** Translate the character based on the keyboardMapping
    *
    * @param mapping The keyboard character mapping to use
    * @param character The character to translate based on the mapping
    * @return The translated character ('\0' if none exists).
    */
   private char translateChar(char[] mapping, char character)
   {  if (character==' ')  return character;

      int lower = lowerKeyCodeMapping.indexOf(character);
      int upper = upperKeyCodeMapping.indexOf(character);

      int mapValue = lower;
      if (lower<0) mapValue = upper;
      if (mapValue<0) return '\0';
      return mapping[mapValue];
   }

   /** Method to make sure the character agrees with its case */
   private char convertChar(char character, int modifier)
   {   int lower = lowerKeyCodeMapping.indexOf(character);
       int upper = upperKeyCodeMapping.indexOf(character);

       int mask = SHIFT_DOWN | CAPS_DOWN;
       int flag = modifier & mask;
       if (flag==SHIFT_DOWN || flag==CAPS_DOWN)
       {  if (lower<0) return character;
          else return upperKeyCodeMapping.charAt(lower);
       }
       else
       {  if (upper<0) return character;
          else return lowerKeyCodeMapping.charAt(upper);
       }
   }

    /** Find the largest key subsequence that matches the key
    *
    * @param current modifier
    * @param key the key value
    * @return The largest matching output
    */
   private String computeOutput(int modifier)
   {  int index, flipIndex, flipModifier, len;
      ArrayList<DeadSequence> dead, flipDead;
      String output = "";
      char xlate;
      boolean partial, flipPartial;

      while (previousKey.length()>0)
      {   // See if what remains is a dead keysequence
          if ( (previousKey.length()>1 && modifier==previousModifier)
                                       || previousKey.length()==1)
          {
              dead = sequences[firstModifier];
              index = findSequence(dead, previousKey);
              partial = isPartial(dead,  index, previousKey);

              if (partial) { return output; }
              else
              {  flipModifier = ModCase.flipModifier(firstModifier);
                 flipDead = sequences[flipModifier];
                 flipIndex = findSequence(flipDead, previousKey);
                 flipPartial = isPartial(flipDead, flipIndex, previousKey);

                 if (flipPartial)
                 {  firstModifier = flipModifier; return output;  }
              }

              if (flipIndex>=0) { index = flipIndex; dead = flipDead; }

              if (index<0) index = findMaxSequence(dead, previousKey);
              if (index>=0)
              {  len = dead.get(index).getKey().length();
                 output += dead.get(index).getData();
                 previousKey = previousKey.substring(len);
                 firstModifier = modifier;
              }
              else
              {   xlate = translateChar
                          (keyMap[firstModifier], previousKey.charAt(0));
                  firstModifier = previousModifier;
                  previousModifier = modifier;
                  if (xlate!='\0')  output += xlate;
                  previousKey = previousKey.substring(1);
              }
          }
      }
      return output;
   }

   /** Method to find the largest dead key sequence in list
    *
    * @param dead List of dead key sequences
    * @param sequence The sequence to find
    * @return
    */
   private int findMaxSequence(ArrayList<DeadSequence> dead, String sequence)
   {   int index;
       String substring;
       for (int i=sequence.length(); i>=1; i--)
       {  substring = previousKey.substring(0,i);
          index = findSequence(dead, substring);
          if (index>=0 && dead.get(index).getKey().equals(substring)) 
              return index;
       }
       return -1;
   }

   /** Method to find key sequence within a modifier array using a binary search
    *
    * @param dead Sorted array of dead key sequences
    * @param sequence A key sequence to find
    * @return return index into the array or -1 if not found
    */
   private int findSequence(ArrayList<DeadSequence> dead, String sequence)
   {  int top = dead.size();
      int bottom = -1;
      int middle = 0;
      String key = "";

      while (bottom + 1 < top)
      {  middle = (top + bottom) / 2;
         key = dead.get(middle).getKey();
         if (key.equals(sequence)) return middle;
         if (key.compareTo(sequence) > 0) top = middle;
         else bottom = middle;
      }
      if (key.startsWith(sequence)) return middle;

      if (++middle<dead.size()&&dead.get(middle).getKey().startsWith(sequence))
      {   return middle;  }
      return -1;
   }

   /** method to find modifier starting a dead sequence beginning with key
    *
    * @param modifier The expected modifier
    * @param key The possible dead key sequence
    * @return A modifier of the first character or -1 if none
    */
   private int partialSequenceModifier(int modifier, String key)
   {  int index = findSequence(sequences[modifier], key);
      boolean partial = isPartial(sequences[modifier], index, key);

      if (partial) return modifier;

      int flip  = ModCase.flipModifier(modifier);
      index = findSequence(sequences[flip], key);
      if (isPartial(sequences[modifier], index, key)) return flip;
      else return -1;
   }

   /** Determine if this key sequence is partial to another larger one
    *
    * @param dead Sorted array of dead key sequences
    * @param index Index to a sequence containing the one in question
    * @param key The key to the sequence in question
    * @return true if the output if partial, false otherwise
    */
   private boolean isPartial
           (ArrayList<DeadSequence> dead, int index, String key)
   {
      if (index<0) return false;

      DeadSequence sequence = dead.get(index);
      String deadKey = sequence.getKey();

      while (deadKey.length()==key.length() && deadKey.startsWith(key))
      {  if (dead.size() <= ++ index) return false;
         sequence = dead.get(index);
         deadKey = sequence.getKey();
      }

      if (deadKey.startsWith(key)) return true;
      return false;
   }
}      // End of KeyboardHandler class
