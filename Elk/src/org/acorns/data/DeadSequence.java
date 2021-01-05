/**
 *
 *   @name DeadSequence.java
 *      Dead Sequence objects (keyboard codes and associated output)
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

/** Class to hold a dead key sequence object */
public class DeadSequence implements Comparable<Object>
{
   String keyboard;
   String output;

   public DeadSequence(String keyboard, String output)
   {  this.keyboard = keyboard;
      this.output = output;
   }

   public String getKey()  { return keyboard; }
   public String getData() { return output; }

   public @Override boolean equals(Object object)
   {  if (object instanceof DeadSequence)
      {  DeadSequence key = (DeadSequence)object;
         if (!keyboard.toLowerCase().equals(key.keyboard.toLowerCase()))
            return false;
         if (!output.equals(key.output)) return false;
         return true;
      }
      return false;
   }

   /** Method to override the hashCode function for use in hash tables */
   public @Override int hashCode()
   {  int multiplier = 23;
      int keyboardHash = keyboard.hashCode();
      if (keyboardHash==0) keyboardHash = 1;
      int outputHash = output.hashCode();
      if (outputHash==0) outputHash = 1;

      return multiplier + multiplier*keyboardHash + outputHash;
  }

  /** Method to compare two strings
   *
   * @param object Object to compare to
   * @return >0 if this>object, 0 if this=object, <0 if this<object
   */
   public int compareTo(Object object)
   {  DeadSequence sequence = (DeadSequence)object;
      int value 
           = keyboard.toLowerCase().compareTo(sequence.keyboard.toLowerCase());
      if (value!=0) return value;

      value = output.compareTo(sequence.output);
      return value;
   }
}
