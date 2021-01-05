/**
 * ExportKeyboard.java
 *   Class to process the export option
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

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.acorns.data.*;

/** Class to export data to a .keylayout file */
public class ExportKeyboard implements Constants
{   public ExportKeyboard(File file, KeyboardData keyboardData)
                                                         throws SAXException
    {  try
       {  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document document = builder.newDocument();
          document.setXmlStandalone(true);
   
          String[][] keyboard = 
             { {"group", "126"}, {"id", "-" + System.currentTimeMillis()%10000},
               {"name", ""}  };
          String language = keyboardData.getLanguage();
          if (language.endsWith(".keylayout"))
             language = language.substring(0, language.length()-10);
          keyboard[2][1] = language;

          Element rootNode = makeNode(document, "keyboard", keyboard);
          document.appendChild(rootNode);

          Element layoutsNode = makeNode(document, "layouts", new String[0][0]);
          rootNode.appendChild(layoutsNode);

          String[][] layout =
          {  {"first", "0"}, {"last", "0"},
             {"modifiers", "mods"}, {"mapSet", "maps"} };

          layoutsNode.appendChild(makeNode(document, "layout", layout));

          String[][] modifierMap = {  {"id", "mods"}, {"defaultIndex", "0"} };
          Element modifierMapNode
                             = makeNode(document, "modifierMap", modifierMap);
          rootNode.appendChild(modifierMapNode);

          String[][] keyMapSelect = { {"mapIndex", "0" } };
          Element keyMapSelectNode;

          String[][] modifier = { {"keys", "" } };
          Element modifierNode;

          String[][] keyMapSet = {  {"id", "maps"} };
          Element keyMapSetNode = makeNode(document, "keyMapSet", keyMapSet);
          rootNode.appendChild(keyMapSetNode);

          String[][] keyMap = { {"index", "0"} };
          Element keyMapNode;

          String[][] key = { {"code", "0"}, {"output", "" } };
          Element keyNode;
          ExportModifier modifiers = new ExportModifier(keyboardData);
          int mapIndex = 0;
          char[] keyMapCodes;

          for (int i=0; i<MODIFIERS; i++)
          {   modifier[0][1] = modifiers.computeModifier(i, mapIndex);
              if (modifier[0][1]==null) continue;

              keyMapSelect[0][1] = "" + mapIndex;
              keyMapSelectNode = makeNode(document,"keyMapSelect",keyMapSelect);
              modifierMapNode.appendChild(keyMapSelectNode);

              modifierNode = makeNode(document, "modifier", modifier);
              keyMapSelectNode.appendChild(modifierNode);

              for (int j=i+1; j<MODIFIERS; j++)
              {  if (!keyboardData.equals(i, j)) continue;

                 modifier[0][1] = modifiers.computeModifier(j, mapIndex);
                 if (modifier[0][1]==null) continue;
                 
                 modifierNode = makeNode(document, "modifier", modifier);
                 keyMapSelectNode.appendChild(modifierNode);
              }
   
              keyMap[0][1] = "" + mapIndex;
              keyMapNode = makeNode(document, "keyMap", keyMap);
              keyMapSetNode.appendChild(keyMapNode);
              keyMapCodes = keyboardData.getModifierKeyMapCodes(i);
              char character;
              for (int code=0; code<127; code++)
              {  character = keyMapCodes[code];
                 if (keyMapCodes[code]!='\0')
                 {  key[0][1] = "" + code;
                    key[1][1] = "" + character;
                    if (character<' ' || character>'~'
                       || character=='&' || character=='<' || character=='>'
                       || character=='"')
                    {  key[1][1] = "&#x"+Integer.toHexString(character)+";"; }
                    keyNode = makeNode(document, "key", key);
                    keyMapNode.appendChild(keyNode);
                 }
              }
              mapIndex++;
          }

          ExportActions actions
                  = new ExportActions(document, modifiers.getKeyMapIndex());
          actions.createActionTable(keyboardData);

          // Use a Transformer for output
          TransformerFactory tFactory = TransformerFactory.newInstance();
          Transformer transformer = tFactory.newTransformer();
          transformer.setOutputProperty(OutputKeys.INDENT, "yes");
          transformer.setOutputProperty
                  ("{http://xml.apache.org/xslt}indent-amount", "3");

          transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, 
                  "file://localhost/System/Library/DTDs/KeyboardLayout.dtd");

          DOMSource source = new DOMSource(document);
          FileOutputStream out = new FileOutputStream(file);
          StreamResult result = new StreamResult(new FilterDOM(out));
          transformer.transform(source, result);

      } catch (TransformerConfigurationException tce)
      { throw new SAXException(tce.getMessage());  }
       catch (TransformerException te)
      {  throw new SAXException(te.getMessage());  }
      catch (ParserConfigurationException pce) 
      {   throw new SAXException("Parser Configuration"); }
      catch (IOException ioe)
      {   throw new SAXException("IO Error"); }
    }    // End of constructor.

    /** Method to create a node with its attributes
     *
     * @param doc The DOM object
     * @param node The name of the node to create
     * @param attributes Pairs of attribute names and values
     * @return The created element
     */
    public Element makeNode
            (Document doc, String node, String[][] attributes)
    {
        Element element = doc.createElement(node);
        for (int i=0; i<attributes.length; i++)
        {  element.setAttribute(attributes[i][0], attributes[i][1]);  }

        return element;
    }
}
