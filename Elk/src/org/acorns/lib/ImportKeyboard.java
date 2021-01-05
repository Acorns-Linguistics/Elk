/**
 * ImportKeyboard.java
 *   Class to import .keylayout files in preparation for keyboard mapping
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

import java.util.*;

import java.io.*;
import java.net.*;
import javax.swing.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/** Class to import .keylayout files for processing */
public class ImportKeyboard implements Constants, EntityResolver
{
  private KeyboardData keyboardData;
  private String language;

  private final static String[] modCombinations =
		    {  "shift",  "control", "command", "option", "caps"};

  /** Method to import information from .keylayout files
   *
   * @param url The URL to the file in question
   * @param label A Jlabel for error information or null to only get language
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws IOException
   */
  public ImportKeyboard(URL url, JLabel label)
          throws SAXException, ParserConfigurationException, IOException
  {
     // Parse the xml and create a DOM object.
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    builder.setEntityResolver(this);

    InputStream stream = url.openStream();
    XMLFilterStream xmlStream = new XMLFilterStream(stream);
    Document document = builder.parse(xmlStream);
    stream.close();
    System.gc();
    document.getDocumentElement().normalize();

    // Get the language name from the root element.
    Element keyboard = document.getDocumentElement();
    language = keyboard.getAttribute("name");
    if (label==null) return;

    keyboardData = new KeyboardData(language);

    // Get layout element and extract attributes.
    NodeList layoutList = document.getElementsByTagName("layouts");
    if (layoutList.getLength()==0) throw new SAXException("No key layouts");

    Element layoutElement = (Element)layoutList.item(0);
    String mapSet = getChildAttribute(layoutElement, "layout", "mapSet");
    String modifiers = getChildAttribute(layoutElement, "layout", "modifiers");

    // Now compute valid modifier combinations.
    int[] modifierList = getModifierIndices(document, modifiers);

    // Get the key code mappings for each modifier
    Element mapSetElement = document.getElementById(mapSet);
    if (mapSetElement==null) throw new SAXException("No keyMapSet tag");
    if (!mapSetElement.getTagName().equals("keyMapSet"))
    {   NodeList mapList = document.getElementsByTagName("keyMapSet");
        if (mapList.getLength()==0) throw new SAXException("No keyMapSet tag");
        else mapSetElement = (Element)mapList.item(0);
    }

    NodeList codes = mapSetElement.getElementsByTagName("keyMap");
    Element keyMap;
    ArrayList<KeyCodeSet> keyCodeSet = new ArrayList<KeyCodeSet>();
    char[] keyMapCodes;
    
    int[]  keyCodeSetModifiers;
    Hashtable<String, String> keySequences = new Hashtable<String, String>();
    int[] mods = new int[MODIFIERS];

    for (int i=0; i<codes.getLength(); i++)
    {  
       keyMap = (Element)codes.item(i);
       keyCodeSet.add(new KeyCodeSet(document, keyMap, modifierList, i, label));
       keyMapCodes = keyCodeSet.get(i).getKeyMap();
       keyCodeSetModifiers = keyCodeSet.get(i).getModifiers();
       
       for (int j=0; j<keyCodeSetModifiers.length; j++)
       {  
    	  mods[keyCodeSetModifiers[j]] = i;
          keyboardData.addModifierKeyMap(keyCodeSetModifiers[j], keyMapCodes);
       }  // End inner for
    }     // End outer for
    
    ArrayList<DeadSequence> deadSequences;
    String key;
    Vector<String> keys;
    int mod;
    for (int i=0; i<codes.getLength(); i++)
    {  keySequences = keyCodeSet.get(i).getKeySequences();

       // Handle where sequences starts with a different modifier set
       for (int j=0; j<codes.getLength(); j++)
       {  if (j==i) continue;
          keyCodeSet.get(i).addSequences(keySequences, keyCodeSet.get(j));
       }

       keys = new Vector<String>(keySequences.keySet());
       Collections.sort(keys);
       keyCodeSetModifiers = keyCodeSet.get(i).getModifiers();

       for (int k=0; k<keyCodeSetModifiers.length; k++)
       {  mod = keyCodeSetModifiers[k];
          deadSequences = new ArrayList<DeadSequence>();
          for (Enumeration<?> e = keys.elements() ; e.hasMoreElements();)
          {  key = (String)e.nextElement();
             deadSequences.add(new DeadSequence(key,keySequences.get(key)));
          }
          keyboardData.addKeySequences(mod, deadSequences);
       }        // End for each modifier in this set
    }           // End for each modifier set
  }             // End of constructor.

    /** Get keybordData object */
  public KeyboardData getData() { return keyboardData; }

  /** Method to return the language corresponding to this .keylayout file */
  public String getLanguage() { return language; }

  /** Set keyboardData object */
  public void setData(KeyboardData keyboardData)
  {  this.keyboardData = keyboardData; }

  /** Print keyboardData */
  public @Override String toString()
  {  if (keyboardData==null) return "No data";
     else return keyboardData.toString();
  }
    
 /** Method to compute which indices correspond to which modifier keys.
  *
  * @param document the XML document being parsed
  * @param the id of the modifier map tag
  */
  private int[] getModifierIndices(Document document, String id)
	                      throws SAXException
  {
     Element modifierMap = document.getElementById(id);
     if (modifierMap==null) throw new SAXException("No modifierMap tag");

     if (!modifierMap.getTagName().equals("modifierMap"))
     {   NodeList modList = document.getElementsByTagName("modifierMap");
         if (modList.getLength()==0)
                throw new SAXException("No modifierMap tag");
         else modifierMap = (Element)modList.item(0);
     }

     NodeList keyMapSelects =modifierMap.getElementsByTagName("keyMapSelect");
     String defIndex = modifierMap.getAttribute("defaultIndex");
     int index = 0;
     try { index = Integer.parseInt(defIndex); }
     catch(NumberFormatException nfe) { index = 0; }
					
     // Find the keyMap index for each modifier.
     int[] validModifiers = new int[1<<modCombinations.length];
     for (int i=0; i<validModifiers.length; i++) validModifiers[i] = index;
		 
     Element element;
     for (int i=0; i<keyMapSelects.getLength(); i++)
     {  element = (Element)keyMapSelects.item(i);
        validModifiers = computeModifiers(element, validModifiers);
     }
     return validModifiers;
   }

   /** Method to compute which modifiers are allowed for a keyMap table
    *
    * @param element keyMapSet element
    * @param validModifiers array of mapIndexes applying to selected
    *      modifiers. If null, array is created.
    * @returns appends to the array of valid modifier values
    *
    * Can throw SAXException if encounters a problem with the
    *     modifiers in the XML file
    */
   private int[] computeModifiers
	        (Element element, int[] validModifiers) throws SAXException
   {
      // verify the map index is within bounds
      String mapIndex = element.getAttribute("mapIndex");
      int index = Integer.parseInt(mapIndex);
      if (index>=MODIFIERS)  throw new SAXException("Illegal keyMap index");

      // Get the KeyMapSelect keys attribute.
      int allow, require;
      NodeList nodes = element.getElementsByTagName("modifier");
      Element child;
      String modifierKey;
      for (int m=0; m<nodes.getLength(); m++)
      {  child = (Element)nodes.item(m);
         modifierKey = child.getAttribute("keys").toLowerCase();
	 
         // Determine which modifiers applying to this keys value
         allow = require = 0;
			  
         // Create bit combinations representing valid modifiers
         for (int i=0; i<modCombinations.length; i++)
         {  if (modifierKey.contains(modCombinations[i]))
            {  if (modifierKey.contains(modCombinations[i]+"?")) allow |= 1<<i;
               else require |= 1<<i;
            }
         }

         int requireBits = require | allow;
         for (int i=0; i<validModifiers.length; i++)
         {  int indexBits = i | allow;
            if (requireBits == indexBits)  { validModifiers[i] = index; }
         }
      } // End of loop of child modifiers.
      return validModifiers;
   }  // End of computerModifiers()

			
   /** Method to get an attribute from a child element
    *
     * @param parent the parent element
     * @param tag the tag name of the desired child element
     * @param attribute to extract from the child element
     */
   private String getChildAttribute( Element parent, String tag, String attr )
   {  NodeList nodes = parent.getElementsByTagName(tag);
      Element child = (Element)nodes.item(0);
      return child.getAttribute(attr);
   }
	    
   public InputSource resolveEntity(String publicID, String systemID)
                                                            throws SAXException
   {  
	  String sep = "/";
      int lastPart = systemID.lastIndexOf(sep) + 1;
      systemID = systemID.substring(lastPart);
      try
      {   InputStream stream = ImportKeyboard.class.getResourceAsStream
                                        ("/resources/" + systemID);
          return new InputSource(stream);
      }
      catch (Exception e)
      { throw new SAXException(e.getMessage()); }
   }

   /** Class to eliminate control characters from the XML input */
   class XMLFilterStream extends InputStream
   {    private InputStream stream;
        private StringBuffer buffer;
       
        private final String HEX = "&#x", DECIMAL = "&#";
       
        XMLFilterStream(InputStream stream)
        {  this.stream = stream;
           buffer = new StringBuffer(20000);
        }

        public int read() throws IOException
        {  int data, hexNumber = 0;
           if (buffer.length()==0)
           {  data = stream.read();
              if (data!= '&') { return data; }

              data = stream.read();
              if (data!='#') { buffer.append(data); return (int)'&'; }

              stream.mark(2);
              int max = 5;
              data = stream.read();
              if (data !='x' && data !='X')
              {   stream.reset();
                  max = 6;
                  buffer.append(DECIMAL);
              }
              else buffer.append(HEX);

              StringBuffer stringBuffer = new StringBuffer();
              int count = 0;

              for (count=0; count<max; count++)
              {  stringBuffer.append((char)(stream.read())  );
                 if (stringBuffer.charAt(count) == ';') break;
              }

              String type = (max == 5)?"x":"";
              if (stringBuffer.charAt(count)!=';')
              {  throw new IOException("Illegal character code: &#"
                                            + type + stringBuffer.toString());
              }

              try
              { if (max == 6)
                     hexNumber
                        = Integer.parseInt(stringBuffer.substring(0,count));
                else hexNumber
                        = Integer.parseInt(stringBuffer.substring(0,count),16);
              }
              catch (NumberFormatException e)
              {  throw new IOException("Illegal character code: &#"
                                             + type + stringBuffer.toString());
              }
              if (hexNumber >= 0x20)  { buffer.append(stringBuffer);  }
              else
              {  // Convert control characters to unicode control picture range.
                 hexNumber += 0x2400;
                 String hex = "x" + Integer.toHexString(hexNumber) + ';';
                 if (max==6) buffer.append(hex);
                 else buffer.append(hex.substring(1));
              }
           }

           data = buffer.charAt(0);
           buffer.delete(0,1);
           return data;
        }  // End of read()
       
  }  // End of XMLFilterStream class

}   // End of Keyboard class
