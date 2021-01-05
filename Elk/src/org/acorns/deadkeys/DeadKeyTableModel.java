/**
 * TableModel.java
 *   Dead key table model to maintain rows and columns of cells
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

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;

import org.acorns.data.*;

public class DeadKeyTableModel extends AbstractTableModel
                      implements Constants, MouseListener
{
	private static final long serialVersionUID = 1L;
	private int modifier;
    private ArrayList<Object> rows;
    private int numDeadKeys;

    private Font font; // The indigenous font to use

    /** Create a table model with two initial cells. The first
     *    has the add button, and the second has the trash button.
     *
     * @param modifier shift, ctrl, meta, alt, caps modifier value
     */
    public DeadKeyTableModel(int modifier)
    {   this.modifier = modifier;
        rows = new ArrayList<Object>();
        numDeadKeys = 0;
        
        JButton addButton = new JButton("Add sequence");
        addButton.setToolTipText("Click to add a new key sequence");

        JButton garbageButton = new JButton("Remove");
        garbageButton.setToolTipText("Drag the key sequence here to delete");
        addButton.addMouseListener(this);
        addRow();
        setValueAt(addButton, 0, 0);
        setValueAt(garbageButton, 0, 1);
    }

    /** Method to clear the table of all its cells */
    public void clearAll()
    {   // Get the two buttons in the table.
        Object first = rows.get(0);
        Object second = rows.get(1);

        rows.clear();
        addRow();
        setValueAt(first, 0,0);
        setValueAt(second, 0, 1);
        numDeadKeys = 0;
    }

    /** Method to get the object in a table cell
     *
     * @param row selected row
     * @param column selected column
     * @return Object in the selected cell
     */
    public Object getValueAt(int row, int column)
    {   int index = row * DEAD_COLUMNS + column;
        if (index>=rows.size()) return null;
        return rows.get(index);
    }

    public boolean isInsertOK(int row, int column)
    {   if (row<0 || column<0) return false;

        int index = row * DEAD_COLUMNS + column;
        return index<=numDeadKeys+2 && index>=2;
    }

    /** Method to return the number of table columns
     *
     * @return number of columns
     */
    public int getColumnCount() { return DEAD_COLUMNS; }

    /** Method to return the number of table rows
     *
     * @return number of rows
     */
    public int getRowCount()  { return rows.size()/DEAD_COLUMNS; }

    /** Method to determine if a cell is editable
     *
     * @param row of cell
     * @param column of cell
     * @return false because this table's cells are not editable
     */
    public @Override boolean isCellEditable(int row, int column) {return false;}

    /** Method to get the name of a table column
     *
     * @param column column number
     * @return empty string, because this table has no headers
     */
    public @Override String getColumnName(int column) { return ""; }

    /** Method to add a blank row to the table */
    public void addRow()
    {  for (int i=0; i<DEAD_COLUMNS; i++)  rows.add(null);

       int rowNumber = getRowCount() - 1;
       fireTableRowsInserted(rowNumber, rowNumber);
    }

    /** Method to deleter a row from the table */
    public void deleteRow()
    {   // Don't remove the last row.
        if (getRowCount()==1) return;

        int endData = getRowCount() * DEAD_COLUMNS;
        for (int i=endData - 1; i>endData-DEAD_COLUMNS; i--)
            rows.remove(i);

        int rowNumber = getRowCount();
        fireTableRowsDeleted(rowNumber, rowNumber);
    }

    /** Method to eliminate a cell from the table
     *
     * @param srcRow row of cell to remove
     * @param srcCol column of cell to remove
     */
    public void trashValueAt(int srcRow, int srcCol)
    {   removeValueAt(srcRow, srcCol);
        numDeadKeys--;
        if ((numDeadKeys + 2) % 8 == 0)
        { deleteRow(); }
    }

    /** Method to remove a cell from the table
     *
     * @param srcRow row of cell to remove
     * @param srcCol column of cell to remove
     */
    public void removeValueAt(int srcRow, int srcCol)
    {   // Don't delete the two buttons.
        if (srcRow==0 && srcCol<=1) return;

        // Just return if the cell is empty.
        int index = srcRow * DEAD_COLUMNS + srcCol;
        if (getValueAt(srcRow, srcCol)==null) return;

        // Remove the cell and add null one to the end.
        rows.remove(index);
        rows.add(null);
    }

    /** Method to insert a cell into the table
     *
     * @param object the cell to insert
     * @param srcRow row of cell to remove
     * @param srcCol column of cell to remove
     */
    public void insertValueAt(Object object, int srcRow, int srcCol)
    {   // Don't insert before the button cells.
        if (srcRow==0 && srcCol<=1) return;

        int index = srcRow * DEAD_COLUMNS + srcCol;
        rows.add(index, object);
    }

    /** Method to change a value in a cell
     *
     * @param cell The value to drop into the cell
     * @param srcRow The selected row
     * @param srcCol The selected column
     */
    public @Override void setValueAt(Object cell, int srcRow, int srcCol)
    {   int index = srcRow * DEAD_COLUMNS + srcCol;
        rows.set(index, cell);
        this.fireTableDataChanged();
    }

    /** Method to create a new dead key sequence
     *
     * @param key The dead keyboard keys
     * @param data The output if deadkey sequence is entered
     */
    public void makeDeadKeyPanel(String key, String data)
    {
        int count = numDeadKeys + 2;
        int row    = count/DEAD_COLUMNS;
        int column = count%DEAD_COLUMNS;

        DeadKeyPanel deadKey = new DeadKeyPanel(font, modifier);
        deadKey.setData(key, data);
        if (count%8==0)
        {  addRow();
           setValueAt(deadKey, row, 0);
        }
        else  setValueAt(deadKey, row, column);
        numDeadKeys++;
    }

    /** Method to process add deadkeys to the table
     *
     * @param mouseEvent the event triggering this action
     */
    public void mouseClicked(MouseEvent mouseEvent)
    {   if (font==null)
        {   JOptionPane.showMessageDialog(null, "Please first select a font");
            return;
        }
        makeDeadKeyPanel("", "");
    }

    /** Unused MouseListener methods */
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

        /** Method to set the font for the dead key panels
     *
     * @param font the indigenous font to use
     */
    public void setIndigenousFont(Font font)
    {  this.font = font; }


}   // End of TableModel class
