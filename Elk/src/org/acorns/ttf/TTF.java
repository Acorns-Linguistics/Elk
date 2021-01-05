/**
 *
 *   @name TTF.java
 *     Class to handle the true type font save option
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
 *
 *   Note: CMap formats 2, 8, 10, and 12 are not now supported. Format 2
 *         is for Japanese, Korean, and Chinese. Formats 8, 10, 12 are
 *         variants of 0, 4, and 6 respectively for fonts with 32 bit
 *         character codes.
 *
 */

package org.acorns.ttf;

import java.io.*;
import com.sun.ttf.*;
import java.awt.*;
//import javax.swing.*;  // For debugging
import java.util.*;
import org.acorns.data.*;

/** Class to manage a true typ font object from a selected file */
public class TTF implements Constants
{  TrueTypeFont ttf;
   Font font;

   CMap[] cmaps;
   char[][] glyphs;

   /** Open a .ttf file, parse it, and instantiate its font object
    *
    * @param source The object corresponding to the .ttf file
    * @param size The desired point size of the instantiated font
    * @throws IOException
    * @throws FontFormatException
    */
   public TTF(File source, int size) throws IOException, FontFormatException
   {  RandomAccessFile raf = new RandomAccessFile(source, "r");
      int fileSize = (int) raf.length();
      byte[] data = new byte[fileSize];
      raf.readFully(data);
      raf.close();

      // Get the current character to glyph mapping from the font
      ttf = TrueTypeFont.parseFont(data);
      ByteArrayInputStream fontStream
              = new ByteArrayInputStream(ttf.writeFont());
      font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
      font = font.deriveFont(0.0f + size);

      cmaps = getCMap(ttf);
      glyphs = getGlyphMapping(cmaps);

   }
   
   /** Get the font object corresponding to this .ttf file */
   public Font getFont() { return font; }

   /** Set font size */
   public void setFontSize(int size) { font = font.deriveFont(0.0f + size); }

   /** Write a new .ttf file based on the keyboard mapping
    *
    * @param keyboard The object containing the keyboard mapping
    * @param dest The object corresponding to the destination .ttf file
    */
   public void writeTTF(KeyboardData keyboard, File dest) throws IOException
   {
      // Get the desired mapping of keys to unicode points
      int length = lowerKeyCodeMapping.length();
      char[] codePoints = new char[2*length];
      getCodePoints(codePoints, 0, keyboard, upperKeyCodeMapping);
      getCodePoints(codePoints, length, keyboard, lowerKeyCodeMapping);

      // Update the CMap tables in the ttf object
      String mappings = lowerKeyCodeMapping + upperKeyCodeMapping;
      setGlyphMappings(cmaps, codePoints, mappings);
      //printMappings(cmaps, codePoints, mappings); // For debugging

      // Update the NameTable in the ttf object
      NameTable nameTable = getNameTable(ttf);
      String family = font.getFamily();
      updateNameTable(nameTable, family, keyboard.getLanguage());
      //System.out.println(nameTable); // For debugging

      // Write the new .ttf file
      byte[] data = ttf.writeFont();
      FileOutputStream out = new FileOutputStream(dest);
      BufferedOutputStream buf = new BufferedOutputStream(out);
      buf.write(data, 0, data.length);
      buf.close();

      /* For debugging
      try
      {  TTF ttfFile = new TTF(dest, 12);
         Font newFont = ttfFile.getFont();
         JLabel label = new JLabel(mappings + " " + newFont.getFamily());
         label.setFont(newFont);
         JOptionPane.showMessageDialog(null, label);
      }
      catch (Exception e)
      { JOptionPane.showMessageDialog
                (null, "Couldn't reload ttf file after update");
      }
      */
   }

   /** Get user specified unicode code points from the keyboard data object
    *
    * @param codePoints The array to store code points into
    * @param offset  Offset into the code point array
    * @param keyboard The keyboard data object containing the code points
    * @param keyString The string containing desired code point characters
    */
   private void getCodePoints
      (char[] codePoints, int offset, KeyboardData keyboard, String keyString)
   {
      char[] keys = keyboard.getModifierKeyMap(offset==0?0:1);
      for (int i=0; i<keyString.length(); i++)
      {  if (keyString.charAt(i)=='\0') continue;
         codePoints[offset+i] = keys[i];
      }
   }

   /** Method to get an array of CMap tables from the font
    *
    * @param ttf The True Type Font object
    * @return array of CMap tables contained in the font
    */
   private CMap[] getCMap(TrueTypeFont ttf)
   {  String cmapTag = TrueTypeTable.tagToString(PostTable.CMAP_TABLE);
      CmapTable cmapTable = (CmapTable)ttf.getTable(cmapTag);
      return cmapTable.getCMaps();
   }

   /** Method to get the entire original glyph mappings
    *
    * @param cmaps Array of CMap tables from the font
    * @return The original glyph mapping
    */
   private char[][] getGlyphMapping(CMap[] cmaps)
   {   int numGlyphs = 65535;
       char[][] glyphTable = new char[cmaps.length][numGlyphs];
       for (int m=0; m<cmaps.length; m++)
       {   for (char c=0; c<numGlyphs; c++)
           {  glyphTable[m][c] = '\0';
              try {  glyphTable[m][c] = cmaps[m].map(c);  }
              catch (Exception e) {}
           }
       }
       return glyphTable;
   }

   /** Method to get the current glyph mapping from the font
    * 
    * @param cmaps Array of CMap tables
    * @param keys The String of characters desired
    * @return The character to glyph mapping for each of the font's cmap tables
    */
   private char[][] getGlyphMapping(CMap[] cmaps, String keys)
   {   char[][] glyphs = new char[cmaps.length][keys.length()];
       char key;
       for (int m=0; m<cmaps.length; m++)
       {   for (char c=0; c<keys.length(); c++)
           {  key = keys.charAt(c);
              if (key=='\0') continue;
              glyphs[m][c] = cmaps[m].map(key);
           }
       }
       return glyphs;
   }

   /** Method to alter the mappings of characters to glyphs
    *
    * @param cmaps The font's CMap table objects
    * @param codePoints The desired code points
    * @param keys The characters that map to the code points
    */
   private void setGlyphMappings(CMap[] cmaps, char[] codePoints, String keys)
   {   char key, glyph;
       for (int m=0; m<cmaps.length; m++)
       {   for (char c=0; c<keys.length(); c++)
           {  key = keys.charAt(c);
              if (key=='\0') continue;
              if (codePoints[c]=='\0') continue;
              glyph = glyphs[m][codePoints[c]];
              cmaps[m].setMap(key, glyph);
           }
       }
       System.out.println(cmaps[0].map('A'));
   }

   /** Method to print current and desired code points
    *
    * Note: For debugging purposes.
    *
    * @param codePoints The desired code points
    * @param maps The current glyph id for the character obtained from the font
    * @param keys The characters for which code points are to be mapped
    */
   public void printMappings(CMap[] cmaps, char[] codePoints, String keys)
   {  char point;
      String hexMap, hexPoint;
      String mappings = lowerKeyCodeMapping + upperKeyCodeMapping;
      char[][] glyphs = getGlyphMapping(cmaps, mappings);

      for (int m=0; m<glyphs.length; m++)
      {  System.out.println( "CMap Table " + m);
         for (int p=0; p<keys.length(); p++)
         {   point = keys.charAt(p);
             if (point=='\0') continue;
             hexMap = Integer.toHexString((int)glyphs[m][p]);
             hexPoint = Integer.toHexString((int)codePoints[p]);
             System.out.println(point + " " + hexMap + " " + hexPoint);

         }
      }
   }

   /** Method to get the name table from the font
    *
    * @param ttf The True Type Font object
    * @return
    */
   private NameTable getNameTable(TrueTypeFont ttf)
   {  String nameTag = TrueTypeTable.tagToString(PostTable.NAME_TABLE);
      NameTable nameTable = (NameTable)ttf.getTable(nameTag);
      return nameTable;
   }

   /** Method to append the language to font family names
    *
    * @param names The ttf NameTable object
    * @param family The current font family name
    * @param language The language name to append to the font family name
    */
   private void updateNameTable(NameTable names, String family, String language)
   {
       String data, compressFamily = family.replaceAll("\\s+", "");
       NameRecord rec;
       int len = family.length(), compressLen = compressFamily.length();
       int index, nameID;

        /** Iterate through records replacing family name with extended family */
       for (Iterator<?> iterate = names.getRecords(); iterate.hasNext();)
       {  rec = (NameRecord) iterate.next();
          
          // Only continue records containing the font family name
          nameID = rec.getNameID();
          data = names.getRecord(rec);
          if (data==null) continue;
          
          switch (nameID)
          {   case 1:
              case 3:
              case 4:
              case 16:
              case 18:
              case 21:
                  index = data.indexOf(family);
                  if (index >=0  && !family.endsWith(language))
                  { data = data.substring(0, index+len) + " "
                                    + language + data.substring(index+len);
                  }
                  break;
              case 6:
              case 20:
                  // Filter any illegal postscript charaters.
                  index = data.indexOf(compressFamily);
                  if (index>=0)
                  {   data = data.substring(0, index+compressLen)
                             + language.replaceAll("[\\[\\]<>/()\\s%]+", "")
                                + data.substring(index+compressLen);
                  }
              default: break;
          }
          names.putRecord(rec, data);
       }     // End for
   }         // End updateNameTable

   public Font getFont(File source)
   {  try
      {  FileInputStream stream = new FileInputStream(source);
         Font f = Font.createFont(Font.TRUETYPE_FONT, stream);
         f = f.deriveFont(16.0f);
         stream.close();
         return f;
      }
      catch (Exception e) { return null; }
   }
}            // End TTF class
