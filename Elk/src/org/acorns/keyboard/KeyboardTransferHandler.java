/**
 * KeyboardTransferHandler.java
 *   Class to handle drops of glyphs onto the keyboard panel
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

package org.acorns.keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class KeyboardTransferHandler extends TransferHandler
{    
	private static final long serialVersionUID = 1L;

	public @Override boolean importData(JComponent comp, Transferable tran)
     {  if (comp instanceof KeyPanel)
        {   KeyPanel keyboard = (KeyPanel)comp;
            Point point = keyboard.getMousePosition();
            if (point==null) return false;

            try
            {  Object data = tran.getTransferData(DataFlavor.stringFlavor);
               String str = (String)data;
               if (keyboard.isControl())
               {   Toolkit.getDefaultToolkit().beep();
                   return false;
               }

               keyboard.setText(str);
               Container component = keyboard.getParent().getParent();
               KeyMapper mapper = (KeyMapper)component;

               char keyChar = str.charAt(0);
               char keyCode = keyboard.getID();
               mapper.setKey((char)keyCode, keyChar);
               return true;
            } catch(Exception e) {}
        }
 
        return false;
    }   // End of importData()

    /** Method to determine if the import is legal
     *
     * @param support object with transfer support properties
     * @return true if ok, false otherwise
     */
    public @Override boolean canImport(JComponent comp, DataFlavor[] flavors)
    {
       if (comp instanceof KeyPanel)
       {   for (int i=0; i<flavors.length; i++)
           {   if (flavors[i].isFlavorTextType()) return true;  }
           return false;
       }
       return false;
    }

    /** Method to return drag options allowed
     *
     * @param c Component for actions allowed (JTable)
     * @return The allowed actions (NONE)
     */
    public @Override int getSourceActions(JComponent c) { return NONE; }

}   // End of KeyboardTransferHandler
