/**
 *
 *   @name FilterDOM.java
 *     Class to replace entities by &amp; to their hex codes.
 *           Necessary for Mac .keylayout compatibllity
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

public class FilterDOM extends FilterOutputStream
{
   private static final int NONE=0, AMP=1, AMPA=2, AMPM=3, AMPP=4;
   private int state;

   public FilterDOM(OutputStream stream) { super(stream); state = 0; }

   public @Override void write(byte[] b) throws IOException
   {  for (int i=0; i<b.length; i++) write(b[i]); }

   public @Override void write(byte[] b, int off, int len) throws IOException
   { int last = off+len;
     if (last>b.length) last = b.length;
     for (int i=off; i<last; i++) write(b[i]);
   }

   public @Override void write(int b) throws IOException
   {  switch (state)
      {   case AMP: if (b=='a')  {state=AMP; return; }
          case AMPA: if (b=='m') { state=AMPM; return; }
          case AMPM: if (b=='p') { state=AMPP; return; }
          case AMPP: if (b==';') { state=NONE; return; }
      }
      if (b=='&') { state=AMP; } else state=NONE;
      super.write(b); return; 
   }

   public @Override void flush() throws IOException {super.flush(); }
   public @Override void close() throws IOException {super.close(); }
}
