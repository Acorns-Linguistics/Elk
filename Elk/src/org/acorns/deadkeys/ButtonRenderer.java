/**
 * ButtonRenderer.java
 *   Class to properly render the buttons contained in a JTable of dead keys
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
import javax.swing.table.*;
import java.awt.*;

public class ButtonRenderer implements TableCellRenderer
{
    private TableCellRenderer defaultRenderer;

    public ButtonRenderer(TableCellRenderer renderer)
    {  defaultRenderer = renderer;  }

    public Component getTableCellRendererComponent
          ( JTable table, Object value, boolean isSelected, boolean hasFocus
                        , int row, int column)
    {
       if(value instanceof Component)
          return (Component)value;

       return defaultRenderer.getTableCellRendererComponent(
	             table, value, isSelected, hasFocus, row, column);
    }
}      // End of ButtonRenderer class.

  