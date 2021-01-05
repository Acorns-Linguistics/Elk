/**
 * Constants.java
 *   Class defining constants used by various modules
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

import java.awt.*;

public interface Constants
{
   /** Various colors used by the program */
   public final static Color GREY        = new Color(192,192,192);
   public final static Color OPTIONCOLOR = new Color(220, 220, 220);
   public final static Color DARKGREY    = new Color(80, 80, 80);
   public final static Color LIGHTBLUE   = new Color(210,210,255);
   public final static Color DARKRED     = new Color(128,0,0);
   public final static Color DARKBLUE    = new Color(0,0,128);

   /** Parameters for the DeadKeys class */
   public final static int DEAD_COLUMNS      = 8;
   public final static int DEAD_KEY_HEIGHT   = 30;
   public final static int DEAD_HEIGHT       = 200;

   /** Number of modifers
    *     (shift, ctrl, meta, alt, caps means there are 2^5 possibilities)
    */
   public final static int MODIFIERS = 32;
   public final static int NUM_MODIFIERS = 5;
   
   public final static int SHIFT_DOWN = 1;
   public final static int ALT_DOWN   = 2;
   public final static int META_DOWN  = 4;
   public final static int CTRL_DOWN  = 8;
   public final static int CAPS_DOWN  =16;  // Needed: Java has no caps modifier mask

   /** Number of character codes */
   public final static int CODES = 128;

   /** Maximum size of dead key sequence */
   public final static int MAX_DEAD_SEQUENCE = 4;

   /** Map from characters to MAX keycodes */
   public final static String lowerKeyCodeMapping =
      "asdfhgzxcv" + "\0bqweryt12" + "3465=97-80" + "]ou[ip\0lj'"
         + "k;\\,/nm.\0\0" + "`";

   public final static String upperKeyCodeMapping =
      "ASDFHGZXCV" + "\0BQWERYT!@" + "#$^%+(&_*)" + "}OU{IP\0LJ\""
         + "K:|<?NM>\0\0" +"~";

   /** String offsets for keyboard mapping keys */
   public static final int TOP=0, BOTTOM=1, KEY=2;

   /** Values for modifier and space keys for virtual keyboard classes */
   public static final int STRING=65500;
   public static final int SHFT=65500;
   public static final int CTRL=65501;
   public static final int CMD=65502;
   public static final int ALT=65503;
   public static final int CAPS=65504;
   public static final int SPACE=65505;
   public static final int CTRL_SIZE = SPACE + 1 - STRING;

}