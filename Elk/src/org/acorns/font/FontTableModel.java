/**
 * FontTransferHandler.java
 *    Class to handle cell display in the font display JTable
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


import java.awt.*;
import java.awt.font.*;
import javax.swing.table.*;


public class FontTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

	private static final int FONTS_PER_LINE = 8;

    private StringBuilder charBuffer;

    private Font font; // The indigenous font to use

    /** Create a table model with two initial cells. The first
     *    has the add button, and the second has the trash button.
     *
     * @param modifier shift, ctrl, meta, alt, caps modifier value
     */
    public FontTableModel() { charBuffer = new StringBuilder(); }

    /** Method to clear the table of all its cells */
    public void clearAll()  { charBuffer.setLength(0); }

    /** Method to get the object in a table cell
     *
     * @param row selected row
     * @param column selected column
     * @return Object in the selected cell
     */
    public Object getValueAt(int row, int column)
    {   int index = row * (FONTS_PER_LINE +1) + column;
        String value;
        if (index>=charBuffer.length()) return "";

        if (column==0)  
        {    value = Integer.toString (charBuffer.charAt(index),16);  }
        else value = "" + charBuffer.charAt(index);
        return value;
    }

    /** Method to return the number of table columns
     *
     * @return number of columns
     */
    public int getColumnCount()  {  return FONTS_PER_LINE + 1;  }

    /** Method to return the number of table rows
     *
     * @return number of rows
     */
    public int getRowCount()  
    { return (charBuffer.length() + FONTS_PER_LINE)/(FONTS_PER_LINE + 1); }

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

    /** Get the name of the class of data for this column
     *
     * @param column Column number
     * @return Class object
     */
    public @Override Class<?> getColumnClass(int column)
    { return String.class; }

 
    /** Method to add a cell to the table
     *
     * @param object the cell to insert
     * @param srcRow row of cell to remove
     * @param srcCol column of cell to remove
     */
    public void fillCells(Font font)
    {   clearAll();
        FontRenderContext fontRenderContext
              = new FontRenderContext(null, false, false);
        char[] array = new char[65536];
        for (int i=0; i<array.length; i++)  array[i] = (char)i;
        if (font!=null)
        {   GlyphVector glyphVector
                = font.createGlyphVector(fontRenderContext, array);

            for (char c=' '; c<Character.MAX_VALUE; c++)
            {  if (glyphVector.getGlyphCode(c)>0)
               {   if (charBuffer.length()%(FONTS_PER_LINE + 1) == 0)
                   {   charBuffer.append(c); }
                   charBuffer.append(c);
               }
            }
        }
        fireTableDataChanged();
    }

    /** Method to change a value in a cell
     *
     * @param cell The value to drop into the cell
     * @param srcRow The selected row
     * @param srcCol The selected column
     */
    public @Override void setValueAt(Object cell, int srcRow, int srcCol)
    {   int index = srcRow * (FONTS_PER_LINE + 1) + srcCol;
        if (cell instanceof String)
        {
            String cellString = (String)cell;
            charBuffer.setCharAt(index, cellString.charAt(0));
        }
        fireTableDataChanged();
    }

    

    /** Method to set the font for the dead key panels
     *
     * @param font the indigenous font to use
     */
    public void setIndigenousFont(Font font)
    {  this.font = font; }
    
    public Font getIndigenousFont()
    { return font; }


}   // End of FontTableModel class
