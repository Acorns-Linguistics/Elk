/**
 * FontTransferHandler.java
 *    Class to render cells in the font display panel
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
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;


public class FontTableRenderer  extends JLabel implements TableCellRenderer
{
	private static final long serialVersionUID = 1L;
	Color background;
    Font  indexFont;

    public FontTableRenderer()
    {   background = new Color(220, 220, 220);
        setOpaque(true); //MUST do this for background to show up.
        setHorizontalAlignment( SwingConstants.CENTER );
        indexFont = new Font("Arial", Font.PLAIN, 12);
    }

    /** Render the JTable cell
     *
     * @param table The JTable holding the cell
     * @param value The cell data
     * @param isSelected Indicate if cell is selected
     * @param hasFocus Indicate if cell has focus
     * @param row The JTable row
     * @param column The JTable column
     * @return A rendered component
     */
    public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected, boolean hasFocus,
                            int row, int column)
    {
        String data = (String) value;
        if (column==0)
        {  setFont(indexFont);
           setBorder(null);
           setBackground(Color.white);
           setText(data);
           return this;
        }
        
        setFont(table.getFont());
        setText((String)value);
        Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        setBackground(background);
        setBorder(border);
        return this;
    }
}       // End of FontTableRenderer class

