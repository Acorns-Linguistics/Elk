/*
 * $Id: CMap.java,v 1.2 2007/12/20 18:33:31 rbair Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.sun.ttf;

import java.nio.ByteBuffer;

/**
 *
 * @author  jkaplan
 */
public abstract class CMap
{
   /** The format of this map */
   private short format;
    
   /** The language of this map, or 0 for language-independent  */
   private short language;
    
   /** Creates a new instance of CMap
     * Don't use this directly, use <code>CMap.createMap()</code>
     */
   protected CMap(short format, short language)
   {  this.format = format;
      this.language = language;
   }
    
   /** Create a map for the given format and language  */
   public static CMap createMap(short format, short language)
   {  CMap outMap = null;
        
      switch (format)
      {  case 0: // CMap format 0
            outMap = new CMapFormat0(language);
            break;
         case 4: // CMap format 4
            outMap = new CMapFormat4(language);
            break;
         case 6:  // CMap format 6
            outMap = new CMapFormat6(language);
            break;
         default:
            System.out.println("Unsupport CMap format: " + format);
            return null;
      }
      return outMap;
   }
    
   /** Get a map from the given data
    *
    * This method reads the format, data and length variables of the map.
    */
   public static CMap getMap(ByteBuffer data)
   {  short format = data.getShort();
      short length = data.getShort();
        
      // make sure our data slice only contains up to the length of this table
      data.limit((int)length);
        
      short language = data.getShort();

      CMap outMap = createMap(format, language);
      if (outMap == null) {  return null;  }
        
      outMap.setData(length, data);
      return outMap;
   }
    
   /** Get the format of this map  */
   public short getFormat() {  return format; }
    
   /** Get the language of this map  */
   public short getLanguage() { return language; }
       
   /** Set the data for this map */
   public abstract void setData(int length, ByteBuffer data);
    
   /** Get the data in this map as a byte buffer */
   public abstract ByteBuffer getData();
    
   /** Get the length of this map */
   public abstract short getLength();
    
   /** Map an 8 bit value to another 8 bit value */
   public abstract byte map(byte src);
    
   /** Map a 16 bit value to another 16 but value */
   public abstract char map(char src);

   /** Alter the character to glyph mapping for a single code point */
   public abstract void setMap(char src, char glyph);

   /** Get the src code which maps to the given glyphID */
   public abstract char reverseMap(short glyphID);
    
   /** Print a pretty string */
   @Override public String toString()
   {  String indent = "        ";
      return indent + " format: " + getFormat() + " length: " +
               getLength() + " language: " + getLanguage() + "\n";
   }
}