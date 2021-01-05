/**
 *
 *   @name CMapFormat6.java
 *
 *   @author  harveyd
 *   @version 6.00
 *
 *   Copyright 2010, all rights reserved
 */

package com.sun.ttf;

import java.nio.ByteBuffer;

/** Extension to the original package which did not support this format */
public class CMapFormat6 extends CMap
{
   /** The glyph index array */
   private short firstCode;
   private short entryCount;
   private char[] glyphIndex;

  /** Creates a new instance of CMapFormat0 */
  protected CMapFormat6(short language)
  { super((short) 0, language); }

  /** Get the length of this table */
  public short getLength()
  {  return (short)(10 + 2*entryCount); }

  /** Map from a byte */
  public byte map(byte src)
  {  char c = map((char) src);
     if (c < Byte.MIN_VALUE || c > Byte.MAX_VALUE)
     {  return 0; }  // Out of range
     return (byte) c;
  }

  /** Map from a char */
  public char map(char src)
  {  if (src<firstCode) return 0;
     if (src> firstCode + entryCount) return 0;
     return glyphIndex[src-firstCode];
  }


  /** Get the src code which maps to the given glyphID  */
  public char reverseMap(short glyphID)
  {  for (int i = 0; i < glyphIndex.length; i++)
     {  if (glyphIndex[i] == glyphID) { return (char)(i+firstCode); }  }
     return (char) 0;
  }

  /** Set a single mapping entry  */
  public void setMap(char src, char glyph)
  {  if (src<firstCode) return;
     if (src> firstCode + entryCount) return;
     glyphIndex[src-firstCode] = glyph;
  }

  /**Get the data in this map as a ByteBuffer */
  public ByteBuffer getData()
  {  ByteBuffer buf = ByteBuffer.allocate(getLength());

     buf.putShort(getFormat());
     buf.putShort(getLength());
     buf.putShort(getLanguage());
     buf.putShort(firstCode);
     buf.putShort(entryCount);
     for (int i=0; i<entryCount; i++) { buf.putShort((short)glyphIndex[i]); }

     // reset the position to the beginning of the buffer
     buf.flip();
     return buf;
  }

   /** Read the map in from a byte buffer */
   public void setData(int length, ByteBuffer data)
   {   firstCode = data.getShort();
       entryCount = data.getShort();

       int remain = data.remaining();
       if (remain != entryCount*2)
       {  throw new IllegalArgumentException
                         ("Wrong amount of data for CMap format 6");
       }

       glyphIndex = new char[entryCount];
       for (int i=0; i<entryCount; i++) {glyphIndex[i] = (char)data.getShort();}
    }   // End of setData()
}       // End of CMapFormat6 class
