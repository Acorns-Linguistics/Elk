/**
 * FontTransferHandler.java
 *    Class to handle drags from the font display panel
 *
 *   @author  Harvey, Dan
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
package org.acorns.font;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class FontTransferHandler extends TransferHandler
{  
	private static final long serialVersionUID = 1L;
	private int dragRow = -1, dragCol = -1;

    /** Method to return drag options allowed
     *
     * @param c Component for actions allowed (JTable)
     * @return The allowed actions (COPY)
     */
    public @Override int getSourceActions(JComponent c)
    {   return TransferHandler.COPY;   }

    /** Method to create a transferable object for drag & drop
     *
     * @param comp The component to handle drag and drops
     * @return Transferrable cell in the table
     */
    public @Override Transferable createTransferable(JComponent comp)
    {   if (comp instanceof FontKeys)
        {   FontKeys table = (FontKeys)comp;
            Point point = comp.getMousePosition();
            if (point==null) return null;

            FontTableModel model = (FontTableModel)table.getModel();
            dragRow = table.getSelectedRow();
            dragCol = table.getSelectedColumn();
            if (dragRow<=0 && dragCol<=1) return null;

            String cell = (String)model.getValueAt(dragRow, dragCol);
            return new StringSelection(cell);
        }
        return null;
    }

    /** Method to determine if this component can import data
     *
     * @param comp component in question
     * @param transferFlavors list of data flavors suported
     * @return false, meaining we cannot import
     */
    public @Override boolean canImport
            (JComponent comp, DataFlavor[] transferFlavors)
    {  return false;  }

}  // End of FontTransferHandler
