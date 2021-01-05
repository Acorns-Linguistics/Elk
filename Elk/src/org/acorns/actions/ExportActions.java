/**
 *
 *   @name ExportActions.java
 *     Class to process export of dead key sequences
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

import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;

import org.acorns.keyboard.*;
import org.acorns.data.*;
import org.acorns.lib.*;

public class ExportActions implements Constants
{
   private Document document;        // The DOM object
   private int[]    modifierTable;   // The keyMap indices for each modifier
   private Element  actionsNode;     // The root actions element
   private Element  terminatorsNode; // The root terminators element
   private Element  rootNode;        // The document root element (<keyboard>)

   private NodeList[]   keyNodes;     // The list of key nodes for each index
   private char[][]     keyMaps;      // The keyboard mappings
   private int          state;        // The last created action state

   /** The constructor for the object to create action elements in the dom
    * 
    * @param document The DOM object
    * @param modifierTable The keyMap indices for each modifier
    * 
    * Note: We assume that the key code table is already created
    */
   public ExportActions(Document document, int[] modifierTable)
   {   this.document = document;
       this.modifierTable = modifierTable;

       // Get the keySet for each modifier
       NodeList keyMapNodes = document.getElementsByTagName("keyMap");
       int length = keyMapNodes.getLength();
       keyNodes = new NodeList[length];
       for (int i=0; i<length; i++)
       {   keyNodes[i] = keyMapNodes.item(i).getChildNodes();
       }
        
       // Create <actions> root element
       NodeList list = document.getElementsByTagName("keyboard");
       rootNode = (Element)list.item(0);
       actionsNode = document.createElement("actions");
       rootNode.appendChild(actionsNode);

       // Create <terminators> root element
       terminatorsNode = document.createElement("terminators");
       rootNode.appendChild(terminatorsNode);

       state = 0;      // The last action state created (0 = none).
   }   // End of constructor

   /** Method to create all of the <action> elements and set maxout attribute
    * 
    * @param keyboardData The object containing all of the data
    */
   public void createActionTable(KeyboardData keyboardData) throws SAXException
   {  ArrayList<DeadSequence> deadList;
      DeadSequence sequence;
      String key, data;
      int maxOut = 0;

      keyMaps = new char[MODIFIERS][];
      for (int i=0; i<MODIFIERS; i++)
         keyMaps[i] = keyboardData.getModifierKeyMapCodes(i);

      for (int i=0; i<keyNodes.length; i++)
      {  int modifier = 0;
         while (modifierTable[modifier]!=i && modifier<MODIFIERS) modifier++;
         deadList = keyboardData.getKeySequences(modifier);
         for (int d=0; d<deadList.size(); d++)
         {  sequence = deadList.get(d);
            key = sequence.getKey();
            data = sequence.getData();
            if (data.length()>maxOut) maxOut = data.length();
            createActionElementsForModifier(key, data, modifier);
         }
      }

      // Create none when elements were necessary
      Element element, actionElement, whenElement;
      String actionId, actionOutput;
      for (int i=0; i<keyNodes.length; i++)
      {  for (int j=0; j<keyNodes[i].getLength(); j++)
         {  element = (Element)keyNodes[i].item(j);
            if (element.hasAttribute("action"))
            {  actionId = element.getAttribute("action");
               actionOutput = element.getAttribute("output");
               if (element.hasAttribute("output"))
                     element.removeAttribute("output");
               actionElement = document.getElementById(actionId);
               if (actionElement==null) continue;
               whenElement = findWhen(actionElement, "none");
               if (whenElement==null)
               {  makeWhen(actionElement, "none", actionOutput, false);
               }  // End if no when element
            }     // End if an action attribute
         }        // end For each key element
      }           // end For each <keyMap> element

      // Set the maxout attribute to the <keyboard> element
      rootNode.setAttribute("maxout", ""+maxOut);
   }     // End of createActionTable()

   /** Method to create action elements in the DOM structure
    *
    * @param sequence The dead key sequence
    * @param data The output corresponding to this sequence
    * @param modifier The modifier value
    */
   private void createActionElementsForModifier
           (String sequence, String data, int modifier) throws SAXException
   {  int length = sequence.length();
      if (length==0) return;

      char value, code;
      Element element, when;
      int translateTable;
      String previousState = "none", output = "";
      for (int i=0; i<length; i++)
      {  value = sequence.charAt(i);
         code = KeyMapper.translateCode(value);
         if (i>0) translateTable = modifierTable[modifier];
         else translateTable 
                 = modifierTable[ModCase.setModifier(modifier, value)];
         element = searchKeyRecords(translateTable, code);
         output += element.getAttribute("output");

         element = getRootNode(element, code, translateTable);
         when = findWhen(element, previousState);
         if (when ==null)
         {  if (i==length-1)
                 when = makeWhen(element, previousState, data, false);
            else when = makeWhen(element, previousState, "s"+(++state), true);
         }
         else
         {  if (when.hasAttribute("output"))
            {  output = when.getAttribute("output");
               if (i==length-1)
               {   if (output.equals(data)) return;
                   else continue;
                   //throw new SAXException
                     //     ("Duplicate dead sequence " + sequence + "/" + data);
               }
               else
               {   when.removeAttribute("output");
                   when.setAttribute("next", "s"+(++state));
               }   // End if this is last character of sequence
            }      // End if when has output attribute
         }         // End if when record exists
         previousState = when.getAttribute("next");
         if (previousState.length()>0)
         {  when = findWhen(terminatorsNode, previousState);
            if (when==null) makeWhen(terminatorsNode, "s"+state, output, false);
         }
      }
   }

   /** Method to get the root node for a particular character
    *
    * @param modifier The modifier value
    * @param character The ASCII character
    * @param modifier The <keycodeset> in question
    * @return The <action> tag element
    *
    * Note: If the node doesn't exist, it will be created
    */
   private Element getRootNode(Element element,  char code, int set)
                                            throws SAXException
   {  Element root = null;
      if (element.hasAttribute("action"))
         root = document.getElementById(element.getAttribute("action"));
      if (root!=null) return root;

      String setString = "00" + set;
      setString = setString.substring(setString.length()-2);
      String id = "a" + (int)code + "." + setString;
      element.setAttribute("action", id);

      Element actionElement = document.getElementById(id);
      if (actionElement!=null) return actionElement;

      actionElement = document.createElement("action");
      actionElement.setAttribute("id", id);
      actionElement.setIdAttribute("id", true);
      
      /** Insert the new element in the correct place */
      NodeList list = actionsNode.getChildNodes();
      Element listNode;
      float idValue;
      float codeValue = Float.parseFloat(id.substring(1));
      for (int i=0; i<list.getLength(); i++)
      {  listNode = (Element)list.item(i);
         idValue = Float.parseFloat(listNode.getAttribute("id").substring(1));
         if (idValue>codeValue)
         {   actionsNode.insertBefore(actionElement, listNode);
             return actionElement;
         }
      }
      actionsNode.appendChild(actionElement);
      return actionElement;
   }

   /** Method to find the root <key> element in a list of nodes
    *
    * @param keyList The list of nodes
    * @param code The code to find
    * @return The matching element or null if not found
    */
   private Element searchKeyRecords(int which, char code)
   {  NodeList keyList = keyNodes[which];
      int top = -1, bottom = keyList.getLength(), middle;
      int codeValue;
      boolean found = false;
      Element element = null;
      while(bottom > top+1)
      {  middle = (bottom + top)/2;
         element = (Element)keyList.item(middle);
         codeValue = Integer.parseInt(element.getAttribute("code"));
         if (codeValue==code) { found = true; break; }
         if (codeValue<code) top = middle;
         else                bottom = middle;
      }
      if (!found)  return makeKeyNode(which, bottom, code);
      return element;
   }

   /** Method to create a when tag and link it to the parent action element
    *
    * @param action The parent action element
    * @param value The output value or null if link to a state
    * @param next flag to determine if this is an output or next element
    * @return The when element
    */
   private Element makeWhen
                  (Element action, String stateId, String value, boolean next)
   {  Element when = document.createElement("when");
      when.setAttribute("state", stateId);
      if (next) when.setAttribute("next", value);
      else      setOutput(when, value);
      action.appendChild(when);
      return when;
   }

   /** Method: find <when> element as a child of an <action> element
    *
    * @param action The <action> element object
    * @param state The state attribute value of the desired <when> element
    * @return The <when> element or null
    */
   private Element findWhen(Element action, String state)
   {   NodeList list = action.getElementsByTagName("when");
       Element element;
       for (int i=0; i<list.getLength(); i++)
       {  element = (Element)list.item(i);
          if (element.getAttribute("state").equals(state)) return element;
       }
       return null;
   }

   /** Method to set the output attribute, replacing " by \"
    *
    * @param element Element to add output attribute
    * @param output Output attribute value
    */
   private void setOutput(Element element, String output)
   {  StringBuffer buf = new StringBuffer();
      char character, next;
      for (int i=0; i<output.length(); i++)
      {  character = output.charAt(i);
         if (i<output.length()-1) next = output.charAt(i+1);
         else next = '\0';

         if (character<' ' || character>'~'
               || (character=='&' && next!='#') || character=='<'
               || character=='>' || character=='"' || character=='\'')
         {
            buf.append("&#x");
            buf.append(Integer.toHexString(character));
            buf.append(";");
         }
         else buf.append(character);
      }
      element.setAttribute("output", buf.toString());
   }

   /** Create a <key> element if it was not found
    *
    * @param which indicate which <keyMapSet> element
    * @param bottom The index before which to insert the new node
    * @param code The code attribute value
    * @return The created element
    */
   private Element makeKeyNode(int which, int bottom, char code)
   {  NodeList keyList = keyNodes[which];
      Element key = document.createElement("key");
      key.setAttribute("code", "" + (int)code);
      key.setAttribute("output", "");

      NodeList keyMapNodes = document.getElementsByTagName("keyMap");
      Element parent = (Element)keyMapNodes.item(which);

      if (bottom>=keyList.getLength()) parent.appendChild(key);
      else
      {   Element element = (Element)keyList.item(bottom);
          parent.insertBefore(key, element);
      }

       keyNodes[which] = parent.getChildNodes();
       return key;
   }
}      // End of ExportKeyboard class
