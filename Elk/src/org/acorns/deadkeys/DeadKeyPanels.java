/**
 * DeadKeyPanels.java
 *   Class to maintain a JTable of dead keys for each modifier
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
package org.acorns.deadkeys;

import javax.swing.*;

import java.awt.*;
import java.util.*;

import org.acorns.data.*;

public class DeadKeyPanels extends JScrollPane implements Constants
{
 	private static final long serialVersionUID = 1L;
	private static ImageIcon icon;
    private DeadKeys[] keysTable;
    
    /** Constructor to initialize the table of dead keys
     * 
     * @param icon image to use in dialogs.
     */
    public DeadKeyPanels(ImageIcon icon)
    {   DeadKeyPanels.icon = icon;
  
        keysTable = new DeadKeys[MODIFIERS];
        for (int i=0; i<MODIFIERS; i++)
        {  keysTable[i] = new DeadKeys(i); }

        getViewport().setBackground(DARKGREY);
        setViewportView(keysTable[0]);
   }

    /** If a new font is set, we need to clear all of dead key panels
     *
     * @param font New font to use
     * @param reset true to clear all previous deadkeys
     */
    public void setIndigenousFont(Font font, boolean reset)
    {   DeadKeyTableModel model;

        for (int i=0; i<MODIFIERS; i++)
        {   model = (DeadKeyTableModel)keysTable[i].getModel();
            model.setIndigenousFont(font);
            if (reset) model.clearAll();
            else keysTable[i].setIndigenousFont(font);
        }
    }

    /** Method to create a panel of dead key sequences
     *
     * @param modifier The modifier in question
     * @param sequences The array of dead key sequences
     */
    public void setSequences(int modifier, ArrayList<DeadSequence> sequences)
    {  keysTable[modifier].setSequences(sequences);  }

    /** Method to store the sequences into a KeyboardData object
     *
     * @param data KeyboardData object
     * @return modified KeyboardData object
     */
    public KeyboardData getSequences(KeyboardData data)
    {  
       @SuppressWarnings("unchecked")
	   ArrayList<DeadSequence>[] sequences = new ArrayList[MODIFIERS];
       for (int i=0; i<MODIFIERS; i++)
       {  sequences[i] = keysTable[i].getSequences(); }

       data.setKeySequences(sequences);
       return data;
    }

    /** Method to switch to new table when modifier changes
     *
     * @param modifier new modifier value
     */
    public void setModifier(int modifier)
    { setViewportView(keysTable[modifier]); }

    public static ImageIcon getIcon() { return icon; }

    /** Method to determine if all the dead sequences are valid
     *
     * @return true if yes, false otherwise
     */
    public boolean isValidSequences()
    {  for (int i=0; i<MODIFIERS; i++)
       {  if (!keysTable[i].isValidSequence()) return false; }
       return true;
    }

}  // End of DeadKeyPanels class
