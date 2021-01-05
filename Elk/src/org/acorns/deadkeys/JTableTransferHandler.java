/**
 * JTableTransferHandler.java
 *   Class to handle drops to tables of dead key components
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
import java.awt.datatransfer.*;

public class JTableTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	/** Table row of the cell being dragged. */
     private int dragRow = -1;
    
     /** Table column of the cell being dragged. */
     private int dragCol = -1;
    
     public @Override boolean importData(JComponent comp, Transferable tran)
     {  Object data = null;
        try { data = tran.getTransferData(DataFlavor.stringFlavor); }
        catch(Exception e) {}

        if (comp instanceof DeadKeys)
        {   // Get drop location.
            DeadKeys table = (DeadKeys)comp;
            Point point = table.getMousePosition();
            int dropRow = table.rowAtPoint(point);
            int dropCol = table.columnAtPoint(point);

            // Handle droping a key into a cell.
            DeadKeyTableModel model = (DeadKeyTableModel)table.getModel();
            if (data!=null)
            {   DeadKeyPanel cell
                    = (DeadKeyPanel)model.getValueAt(dropRow, dropCol);
                Rectangle rect = table.getCellRect(dropRow, dropCol, true);

                Point location = new Point(point.x-rect.x, point.y - rect.y);
                
                cell.insertFontKey((String)data, location);
                model.setValueAt(cell, dropRow, dropCol);
                return true;
            }

            // Cannot drop into the same spot.
            if (dragRow==dropRow && dragCol==dropCol) return false;

            // Handle removal of a cell.
            if (dropRow==0 && dropCol==1)
            {  model.trashValueAt(dragRow, dragCol);
               return finishUp(table);
            }

            // Get original cell
            if (!model.isInsertOK(dropRow, dropCol)) return false;
            Object cell = model.getValueAt(dragRow, dragCol);

            if (dragRow<dropRow || (dragRow==dropRow && dragCol<dropCol))
            {   model.insertValueAt(cell, dropRow, dropCol);
                model.removeValueAt(dragRow, dragCol);
            }
            else
            {   model.removeValueAt(dragRow, dragCol);
                model.insertValueAt(cell, dropRow, dropCol);
            }
            return finishUp(table);
        }
        return false;

    }   // End of importData()

    /** Method to update the DeadKeys panel after an import */
    private boolean finishUp(DeadKeys keys)
    {   dragRow = dragCol = -1;
        keys.repaint();
        return true;
    }

    /** Method to determine if the import is legal
     *
     * @param support object with transfer support properties
     * @return true if ok, false otherwise
     */
    public @Override boolean canImport(JComponent comp, DataFlavor[] flavors)
    {  Point point = comp.getMousePosition();
       if (point==null) return false;
       
       if (comp instanceof DeadKeys)
       {   //DeadKeys deadKeys = (DeadKeys)comp;
           //DeadKeyTableModel model = (DeadKeyTableModel)deadKeys.getModel();
           
           for (int i=0; i<flavors.length; i++)
           {   if (flavors[i].isFlavorTextType())   return true;

               String rep = flavors[i].getRepresentationClass().toString();
               if (rep.contains("org.acorns.deadkeys.DeadKeyPanel"))
               {  return true;  }
           }
       }
       return false;
    }

    /** Method to return drag options allowed
     *
     * @param c Component for actions allowed (JTable)
     * @return The allowed actions (MOVE)
     */
    public @Override int getSourceActions(JComponent c)
    { return TransferHandler.COPY_OR_MOVE; }

    /** Method to create a transferable object for drag & drop
     *
     * @param comp The component to handle drag and drops
     * @return Transferrable cell in the table
     */
    public @Override Transferable createTransferable(JComponent comp)
    {   if (comp instanceof DeadKeys)
        {   DeadKeys table = (DeadKeys)comp;
            //DeadKeyTableModel model = (DeadKeyTableModel)table.getModel();
            Point point = table.getMousePosition();
            if (point==null) return null;
            dragRow = table.rowAtPoint(point);
            dragCol = table.columnAtPoint(point);
            if (dragRow>=0 || dragCol>=0)
            {  Object cell = table.getModel().getValueAt(dragRow, dragCol);
               if (cell!=null && cell instanceof DeadKeyPanel)  
               { return (DeadKeyPanel)cell; }
            }
        }
        dragRow = dragCol = -1;
        return null;
    }
 
}   // End of JTableTransferHandler
