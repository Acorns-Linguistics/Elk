/**
 * KeyCodeSet.java
 *
 * This Class to hold the data structure containing the set of key codes 
 *   mapping extracted from an XML file 
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

import org.acorns.lib.*;

import java.util.*;
import javax.swing.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class KeyCodeSet implements Constants
{
   private char[] codeTable =
   {   'A', 'S', 'D', 'F', 'H', 'G', 'Z', 'X', 'C', 'V',          //0-9
       '|', 'B', 'Q', 'W', 'E', 'R', 'Y', 'T', '1', '2',          //10-19
       '3', '4', '6', '5', '=', '9', '7', '-', '8', '0',          //20-29
       ']', 'O', 'U', '[', 'I', 'P', '\n', 'L', 'J', '\'',        //30-39
       'K', ';', '\\',',', '/', 'N', 'M', '.', '\t', ' ',         //40-49
       '`', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', //50-59
       '\0','\0', '\0', '\0', '\0', '.', '\0', '*', '\0', '+',    //60-60
       '\0', '\0', '\0', '\0', '\0', '/', '\n', '\0', '-', '\0',  //70-79
       '\0', '=', '0', '1', '2', '3', '4', '5', '6', '7',         //80-89
       '\0', '8', '9', '\0', '\0', '\0', '\0', '\0', '\0', '\0',  //90-99
       '\0', '\0', '\0', '\0', '?', '\0', '\0', '\0', '\0', '\0', //100-109
       '\0', '\0', '\0', '\0', '\0','\0', '\0', '\0', '\0', '\0', //110-119
       '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'             //120-127
    };
    
    /** The list of modifier values that go with this key set */
    private int[] modifierList;
    
    /** A list representing key mapping including dead key strings */
    private ArrayList<KeyCodeNode>[] map;  
    
    /** An hash table of terminator states and outputs */
    private ArrayList<String[]> terminators;

    /** The <KeyCodeSet> to which this object applies */
    private int setNo;

    /** A JLabel for warning messages */
    private JLabel label;
               
    /** Constructor
     * @param document The XML DOM
     * @param keyMap The element containing the key codes
     * @param modifiers number of valid modifier keys
     * @param setNo which KeyCodeSet to which this object corresponds
     */
    public KeyCodeSet(Document document, Element keyMap
                , int[] modifiers, int setNo, JLabel label)  throws SAXException
    {
       this.setNo = setNo;
       this.label = label;
       
       // Get the terminator states and outputs
       terminators = getTerminatorStates(document);
  
       // Set the list of modifiers for this key map index 
       int  mod = Integer.parseInt(keyMap.getAttribute("index"));
       modifierList   = createModifierList(mod, modifiers);
       
       // Initialize the state table and the key map list
       NodeList list = keyMap.getElementsByTagName("key");
       map = initializeKeyMap(document, list);
         
    }  // End of constructor
   
    /** Method to initialize the keyCode keys list
      * @param document The XML document DOM
      * @param list The list of key tag elements
      * @param states the state table
      * @return the key code mapping    
      */

    @SuppressWarnings("unchecked")
	private ArrayList<KeyCodeNode>[]  initializeKeyMap
            (Document document, NodeList list)  throws SAXException
    {
       NodeList whenList, actionList;
       
       String output, next, state, term;
       String action, data;
       int code = 0;
       char type;
       Element tag, actionElement;
     
       map = new ArrayList[CODES];
       for (int i=0; i<list.getLength(); i++)
       {
         tag = (Element)list.item(i);
         try  { code = Integer.parseInt(tag.getAttribute("code"));  }
         catch (NumberFormatException nfe)
         {  label.setText("Warning: Skipped key tag in keyMap = " + setNo
                    + " code expected = " + ++code
                    + " code attribute = " + tag.getAttribute("code")
                    + " action attribute = " + tag.getAttribute("action"));
         }

         if (map[code]==null)  map[code] = new ArrayList<KeyCodeNode>();
         action = tag.getAttribute("action");
         output = tag.getAttribute("output");
   
         if (tag.hasAttribute("output"))
         {   map[code].add(new KeyCodeNode(action,"none",code,'O',output,""));
             if (tag.hasAttribute("action"))
                 throw new SAXException("Illegal key tag for code " + code);
              continue;
         }

         if (!tag.hasAttribute("action"))
         {
            actionList = tag.getElementsByTagName("action");
            if (actionList.getLength()!=1)
               throw new SAXException("Illegal actions in code " + code);
            tag = (Element)actionList.item(0);
         }
         else
         { tag=(Element)document.getElementById(action);
           if (tag==null) continue;
         }
         
         // Create keyMap and configure state table
         whenList = tag.getElementsByTagName("when");
         for (int j=0; j<whenList.getLength(); j++)
         {
            actionElement = (Element)whenList.item(j);
            state   = actionElement.getAttribute("state");
            output  = actionElement.getAttribute("output");
            next    = actionElement.getAttribute("next");
            
            if (!actionElement.hasAttribute("through"))
            {  term = "";
               if (actionElement.hasAttribute("next"))
               { term = findTerminator(next); } 
               
               type = 'O';
               data = output;
               if (actionElement.hasAttribute("next")) { type='N'; data=next; }
               map[code].add(new KeyCodeNode(action,state,code,type,data,term));
            }
            else
            {  int initial = 0, last = 0;
               try { initial = Integer.parseInt(state); }
               catch (NumberFormatException nfe)
               {  label.setText("Couldn't parse state attribute");
                  break;
               }
               int current = initial;
               
               try 
               { last=Integer.parseInt(actionElement.getAttribute("through")); }
               catch (NumberFormatException nfe)
               {  label.setText("Warning: Couldn't parse through attribute");
                  break;
               }
               
               int number = -1;
               if (actionElement.hasAttribute("output"))
               {  number = actionElement.getAttribute("output").charAt(0);
                  // Adjust if numbers are in control character range.
                  if (number>=0x2400 && number<0x2420)  { number -= 0x2400; }
               }
               if (actionElement.hasAttribute("next"))
               {  try
                  { number=Integer.parseInt(actionElement.getAttribute("next"));
                  }
                  catch (NumberFormatException nfe)
                  {  label.setText("Warning: couldn't parse next attribute"); 
                     break;
                  }
               }
               
               int mult = 1;
               if (actionElement.hasAttribute("multiplier"));
               {  try
                  { mult = Integer.parseInt
                                    (actionElement.getAttribute("multiplier"));
                  }
                  catch (NumberFormatException nfe)
                  { label.setText
                           ("Warning: couldn't parse multiplier attribute");
                    break;
                  }
               }
               if (number == -1) break;  // No next or output attribute

               while (current<last)
               {  term = "";
                  if (actionElement.hasAttribute("next"))
                  { term = findTerminator(next); }

                  type = 'O';
                  data = output;
                  if (actionElement.hasAttribute("next")) {type='N'; data=next;}
                  map[code].add(new KeyCodeNode(action,state,code,type,data,term));

                  current++;
                  if (actionElement.hasAttribute("next"))
                  {  next = "" + (char)((current-initial)*mult+number);
                  }
                  else { output = "" + ((current - initial)*mult + number); }
               }  // End while
            }     // End handling ranges
         }        // End of for loop through all the when tags
       }          // End of for loop through all the key codes
       return map;
       
    }  // End of initializeKeyMap()
      
    /** Method to find the termination output for this state
     *  @param termination state
     *  @return output state
     */
    private String findTerminator(String state)
    {  String[] entry;
       for (int i=0; i<terminators.size(); i++)
       {  entry = terminators.get(i);
          if (entry[0].equals(state)) { return entry[1]; }
       }
       return "";
    }
    
   /** Method to get the list of action states
     * @param document the XML DOM object
     * @return hash table of terminator actions
     */
    private ArrayList<String[]> getTerminatorStates(Document document) 
                                            throws SAXException
    {
       ArrayList<String[]> action = new ArrayList<String[]>();
           
       NodeList nodeList = document.getElementsByTagName("terminators");
       {  if (nodeList.getLength()==0) return action;
          
          Element terms = (Element)nodeList.item(0);
          NodeList whenList = terms.getElementsByTagName("when");
          Element when;
          String through, next, mult;
          String[] entry;
      
          for (int i=0; i<whenList.getLength(); i++)
          {
             entry = new String[2];
             when    = (Element)whenList.item(i);
             entry[0]= when.getAttribute("state");
             through = when.getAttribute("through");
             next    = when.getAttribute("next");
             entry[1]= when.getAttribute("output");
             mult    = when.getAttribute("multiplier");

             int length = (through+next+mult).length();
             if (length>0) throw new SAXException("Illegal terminatorl list");
            
             action.add(entry);
          }
          return action;
       }
    }     // End of getTerminatorStates()
    
    /** Method to create a list of modifiers to which this key code index applies
     *  @param the keycode index for this object
     *  @param modifiers an integer mapping from modifier to index
     */
    private int[] createModifierList(int mod, int[] modifiers)
    {
        // Get the modifier index. 
 
       int count = 0;
       for (int i=0; i<modifiers.length; i++)
       { if (modifiers[i]==mod) count++; }
       
       modifierList = new int[count];
       
       count=0;
       for (int i=0; i<modifiers.length; i++) 
       { if (modifiers[i]==mod) modifierList[count++] = i;   }
       
       return modifierList;   
    }
    
    /** Method to get the mpdifiers applying to this object
     *  @return array of modifier codes
     */
    public int[] getModifiers() { return modifierList; }
    
    /** Method to return key code sequences and outputs
     *  @param modifier the modifier to create dead key sequences
     *  @return a two dimension array of strings
     *         index 0 is the key code sequence
     *         index 1 is the output sequence
     */
     public Hashtable<String, String> getKeySequences()  throws SAXException
     {    
        KeyCodeNode node;
        String state, data, key, term;
        Hashtable<String,String> sequences = new Hashtable<String, String>();
        if (modifierList.length==0) return sequences;

        for (int i=0; i<map.length; i++)
        {  if (map[i]==null) continue;

           for (int j=0; j<map[i].size(); j++)
           {  node = map[i].get(j);
              if (node==null) continue;
              
              state = node.getState();
              data = node.getData();
              key  = "" + getCode(i);
              term = node.getTerm();

              if (!state.equals("none")) continue;
              if (key.equals("" + '\0')) continue;

              if (node.isNext())
              {  if (term.length()>1) sequences.put(key, term);
                    createKeySequences(sequences, key, data);
              }
              else if (data.length()>1) sequences.put(key, data);
           }
        }
        return sequences;
     }  // End of getKeySequences.
     
     /** Method to add sequences for the designated modifier
      * 
      * @param sequences Hash table of dead sequences
      * @param set KeyCodeSet object containing possible extensions
      */
     public void addSequences
        (Hashtable<String, String>sequences, KeyCodeSet set) throws SAXException
     {
        KeyCodeNode node;
        String state, action, key;
        if (modifierList.length==0) return;
        if (set.modifierList.length==0) return;
           
        for (int i=0; i<set.map.length; i++)
        {  if (set.map[i]==null) continue;

           for (int j=0; j<set.map[i].size(); j++)
           {  node = set.map[i].get(j);
              if (node==null) continue;
              
              state  = node.getState();
              action = node.getData();
              key  = "" + set.getCode(i);
              if (!state.equals("none")) continue;
              if (key.equals("" + '\0')) continue;
              if (node.isNext()) createKeySequences(sequences, key, action);
           }
        }
     }
    
     /** Method to create a key sequence from a sequence of state transitions
      *  @param list Hashtable of key codes and the associated output strings
      *  @param prefix the key code prefix for this state
      *  @param state the current state
      */
     private void createKeySequences
           (Hashtable<String, String> list, String prefix, String state)
                  throws SAXException
     {
        String nodeState, nodeTerm, nodeData, newPrefix, nodeId;
        KeyCodeNode node;
        
        for (char i=0; i<map.length; i++)
        {
		     char code = getCode(i);
			  if (code=='\0' || code=='?') continue;
           if (map[i]==null) continue;
           
           for (int j=0; j<map[i].size(); j++)
           {
              node = map[i].get(j);
              if (node==null) continue;
              
              nodeState = node.getState();
              nodeTerm = node.getTerm();
              nodeData = node.getData();
              nodeId   = node.getId();

              if (nodeState.equals("none")) continue;

              if (nodeState.equals(state))
              {  newPrefix = prefix +code;

					  if (node.isNext())
					  {  if (nodeId.equals(nodeData))
                    {  if (list.containsKey(prefix)) continue;
                       if (prefix.length()>0
                               && prefix.indexOf(code)==prefix.length()-1)
                                 continue;

                       if (nodeData.equals(state))
                          createKeySequences(list, newPrefix, nodeData);
                       continue;
                    }

                    createKeySequences(list, newPrefix, nodeData);
                    if (nodeTerm.length()>0)
                    {  replace(list, newPrefix, nodeTerm);
                    }
					  }
                 else  { replace(list, newPrefix, nodeData); }
                 continue;
              }  // End if matching state
           }     // End for states within a code
        }        // End for all codes.
     }           // End of createKeySequences()

     private void replace(Hashtable<String,String> list,String key,String data)
     {   if (list.containsKey(key))
         {  if (data.length()==0) return;
            list.remove(key);
         }
         list.put(key, data);
     }
     
     /** Translate KeyEvent.VK_code to ASCII value
      *  @param code The VK_code
      * @return The Equivalent ascii code 
		    *   Note: '\0' for undefined keys
		    */
     private char getCode(int code)
     {  char value = codeTable[code];
        if (value=='\0') return value;
        return (ModCase.setCase(modifierList[0], "" + value)).charAt(0);
     }
     
     /** Method to get the keyboard codes
      *  @return character array indexed by code.
      */
      public char[] getKeyMap()
      {
         char[] keyMap = new char[CODES];
         KeyCodeNode node;
         String data, state, term, action;
         
         for (int i=0; i<map.length; i++)
         {  keyMap[i] = '\0';
            if (map[i]!=null)
            {
               for (int j=0; j<map[i].size(); j++)
               {
                  node   = map[i].get(j);
                  state  = node.getState();
                  term   = node.getTerm();
                  action = node.getData();
                  
                  if (!state.equals("none")) continue;
                  
                  data = term;
                  if (!node.isNext()) data = action;
                  
                  if (data.length()!=1) continue;
                  
                  keyMap[i] = data.charAt(0);                    
               }
            }
         }
         return keyMap;
      }
     
    /** Method for outputting and debugging */      
    public @Override String toString()
    {  
       StringBuffer buffer = new StringBuffer("Character codes\nModifiers=");
       
       for (int i=0; i<modifierList.length; i++)
       {  buffer.append(modifierList[i] + " "); }
       buffer.append("\n");
          
       int size;
       for (int i=0; i<map.length; i++)
       {  if (map[i]!=null) 
          { size = map[i].size();
            if (size>1) buffer.append("\n");
            for (int j=0; j<size; j++)
            { buffer.append(map[i].get(j).toString() + "\n");
            }
            if (size>1) buffer.append("\n");
          }
       }
       return buffer.toString();
       
    }  // End of toString()
    
}  // End of KeyCodeSet class
