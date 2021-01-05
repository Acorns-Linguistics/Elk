/**
 * KeyCodeNode.java
 *
 * This Class to hold nodes keyMap sequence
 *   combinations. The data is needed to interface between the
 *   GUI keyboard application and XML .keymap files 
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

public class KeyCodeNode
{
   private String id;     // The action code of the parent node
   private String state;  // This state name;
   private int code;      // The character code
   private char type;     // 'O'=output, 'N'=next state
   private String action; // Output string or next state
   private String term;   // Terminator string if subsequent action doesn't match
	
     /** Constructor to create a keyCode node
      * @param id action code of the parent node
      * @param state is the name of the state of this node
      * @param code  is the code for this node's keyboard key
      * @param type  'O' for output, 'N' for next
      * @param action The output or the next state name or action
      * @param term  The termination output if a subsequent state is not matched
      */
     public KeyCodeNode
        (String id, String state, int code, char type, String data, String term)
     {  this.id     = id;
        this.state  = state;
        this.code   = code;
        this.type   = type;
        this.action = data;
        this.term   = term;
     }

    /** Method to get id of action tag spawning this node */
    public String getId()     { return id; }

    /** Method to get the state associated with this node */
    public String getState() { return state; }

    /** Method to get the character code of this node */
    public int getCode() { return code; }

    /** Method to get action string associated with this node */
    public String getData()	{ return action;	}
		
    /** Method to get terminator output */
    public String getTerm() { return term; }
	
    /** Method to return whether this is an output node
     *  @return true if yes, false otherwise
     */
    public boolean isNext()	{  return type=='N'; }

    /** Method to produce output for debugging purposes */
    public @Override String toString()
    {
      String idStr = (id + "          ").substring(0,10);
      String stateStr = (state + "          ").substring(0,10);
      String codeStr = (code + "   ").substring(0,3);
      String termStr = (term + "   ").substring(0,3);
      String actionStr = (action + "          ").substring(0,10);

      StringBuffer buffer = new StringBuffer
             ( "ID=" + idStr + " Code=" + codeStr + " State=" + stateStr
                            + " Terminator=" + termStr);

      if (isNext())  buffer.append(" Next=" + actionStr);
      else           buffer.append(" Output=" + actionStr);

      return buffer.toString();
    }  // End of toString()
}    // End of KeyCodeNode class