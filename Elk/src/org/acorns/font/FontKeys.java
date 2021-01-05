/**
 * FontKeys.java
 *    Class to display a user selected font
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
import java.awt.event.*;

public class FontKeys extends JTable implements MouseListener
{
 	private static final long serialVersionUID = 1L;
	FontTransferHandler handler;
    Font fontFace;

    public FontKeys()
    {   setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(30);

        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
 

        setModel( new FontTableModel());
        setDefaultRenderer(String.class, new FontTableRenderer());


        setDragEnabled(true);
        handler = new FontTransferHandler();
        setTransferHandler(handler);
        addMouseListener(this);
    }

    /** Display a font in the font dispay panel
     *
     * @param fontName The font to display
     */
    public void displayFont(Font fontName)
    {
      fontFace = fontName;
      setFont(fontName);
      FontTableModel model = (FontTableModel)getModel();
      model.fillCells(fontName);
    }
    
    /** Method to get the font being displayed */
    public Font getFontFace()  { return fontFace; }

    /** get the class of a JTable column
     *
     * @param column The column
     * @return The class object
     */
    public @Override Class<?> getColumnClass(int column) {return String.class;}


    /** Method to process add drags from the table
     *
     * @param mouseEvent the event triggering this action
     */
    public void mouseClicked(MouseEvent mouseEvent)
    {
    }

    /** Unused MouseListener methods */
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e)
    {
        if (fontFace==null)
        {   JOptionPane.showMessageDialog(null, "Please first select a font");
            return;
        }
       JComponent comp = (JComponent)e.getSource();
       handler.exportAsDrag(comp, e,TransferHandler.COPY);

    }
}      // End of FOntKeys class
