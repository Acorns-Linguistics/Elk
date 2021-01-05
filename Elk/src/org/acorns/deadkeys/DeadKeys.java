/**
 * DeadKeys.java
 * Class for a JTable of deadkey components
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
import java.awt.event.*;

import javax.swing.border.*;

import java.util.*;

import org.acorns.data.*;

public class DeadKeys extends JTable 
        implements Constants, MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	private TableColumnModel colModel;
    private DeadKeyPanel deadKeyRenderer;
    private ButtonRenderer buttonRenderer;

    /** Constructor
     *
     * @param modifier value of the shift,ctrl, meta, alt, caps modifier
     */
    public DeadKeys(int modifier)
    {   colModel = getColumnModel();
        setModel(new DeadKeyTableModel(modifier));
        TableCellRenderer defaultRenderer
                     = getDefaultRenderer(JButton.class);
        setDefaultRenderer(JButton.class,
			       new ButtonRenderer(defaultRenderer));
   
        setPreferredScrollableViewportSize
                           (new Dimension(400, DEAD_HEIGHT));
        addMouseListener(this);
        addMouseMotionListener(this);

        Border border = BorderFactory.createEtchedBorder();
        setBorder(border);
  	     setBackground(GREY);

        // Set model for displaying components & eliminate header
        setIntercellSpacing(new Dimension(10,10));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(DEAD_KEY_HEIGHT);
        setTableHeader(null);

        setTransferHandler(new JTableTransferHandler());
        setDragEnabled(true);
    }

    /** Method to create a list of dead key sequences
     *
     * @param sequences The array of dead key sequences
     */
    public void setSequences(ArrayList<DeadSequence> sequences)
    {
       int length = sequences.size();
       DeadKeyTableModel   model = (DeadKeyTableModel)getModel();

       String key, data;
       for (int i=0; i<length; i++)
       {  key = sequences.get(i).getKey();
          data = sequences.get(i).getData();
          model.makeDeadKeyPanel(key, data);  }
    }

    /** Method to get the dead key sequences from this table */
    public ArrayList<DeadSequence> getSequences()
    {  ArrayList<DeadSequence> sequences = new ArrayList<DeadSequence>();
       TableModel model = (TableModel)getModel();
       int rows = model.getRowCount();
       int cols = model.getColumnCount();
       DeadSequence deadSequence;
       
       Object cell;
       DeadKeyPanel deadKey;
       for (int r=0; r<rows; r++)
       {  for (int c=0; c<cols; c++)
          {  cell = model.getValueAt(r, c);
             if (cell instanceof DeadKeyPanel)
             {  deadKey = (DeadKeyPanel)cell;
                deadSequence = deadKey.getDeadSequence();
                if (deadSequence == null) continue;
                sequences.add(deadSequence);
             }
          }
       }
       return sequences;
    }

    public void setIndigenousFont(Font font)
    {
       TableModel model = (TableModel)getModel();
       int rows = model.getRowCount();
       int cols = model.getColumnCount();

       Object cell;
       DeadKeyPanel deadKey;
       for (int r=0; r<rows; r++)
       {  for (int c=0; c<cols; c++)
          {  cell = model.getValueAt(r, c);
             if (cell instanceof DeadKeyPanel)
             {  deadKey = (DeadKeyPanel)cell;
                deadKey.setIndigenousFont(font);
             }
          }
       }
       repaint();
    }

   /** Method to determine if all the modifier's dead sequences are valid
    *
    * @return true if yes, false otherwise
    */
	public boolean isValidSequence()
    {  TableModel model = (TableModel)getModel();
       int rows = model.getRowCount();
       int cols = model.getColumnCount();

       Object cell;
       DeadKeyPanel deadKey;
       for (int r=0; r<rows; r++)
       {  for (int c=0; c<cols; c++)
          {  cell = model.getValueAt(r, c);
             if (cell instanceof DeadKeyPanel)
             {  deadKey = (DeadKeyPanel)cell;
                if (!deadKey.isValidSequence()) return false;
             }
          }
       }
       
       // Make sure there are no duplicates.
       ArrayList<DeadSequence> sequences = getSequences();
       Collections.sort(sequences);
       if (sequences.size()==0) return true;

       String previous = sequences.get(0).getKey(), thisOne;
       for (int i=1; i<sequences.size(); i++)
       {   thisOne = sequences.get(i).getKey();
           if (thisOne.equals(previous)) return false;
           previous = thisOne;
       }
       return true;
    }

    /** Method to get appropriate renderer for a row and column
     *
     * @param row The row for the renderer
     * @param column The column for the renderer
     * @return The appropriate TableCellRenderer
     */
    public @Override TableCellRenderer getCellRenderer(int row, int column)
    {
        if (row==0 && column<=1)
        {
            if (buttonRenderer==null)
                buttonRenderer 
                = new ButtonRenderer(getDefaultRenderer(JButton.class));
            return buttonRenderer;
        }

        if (deadKeyRenderer==null)
            deadKeyRenderer = new DeadKeyPanel();
        return deadKeyRenderer;
    }

    // Override so dragging won't repeatedly display panels.
    protected @Override void firePropertyChange
         (String propertyName, Object oldValue, Object newValue) {}
    
    /** Method to forward events to the cell object
     *
     * @param e event triggering this action
     */
    private void forwardEvent(MouseEvent e)
    {
      int row = 0, column = 0;
      column = colModel.getColumnIndexAtX(e.getX());
      row    = e.getY() / getRowHeight();

      try
      {
          Object value = getValueAt(row, column);

          // Handle clicks on the DeadKeyPanel
          if (value instanceof DeadKeyPanel)
          {
             DeadKeyPanel deadKey = (DeadKeyPanel)value;
             MouseEvent buttonEvent =
              (MouseEvent)SwingUtilities.convertMouseEvent
                                            (this, e, deadKey);
             deadKey.dispatchEvent(buttonEvent);
             deadKey.repaint();
          }

          // Handle clicks on the button
          if((value instanceof JButton))
          {

             JButton button = (JButton)value;

             MouseEvent buttonEvent =
              (MouseEvent)SwingUtilities.convertMouseEvent
                                             (this, e, button);
             button.dispatchEvent(buttonEvent);
             validate();
             repaint();
          }
      }
      catch (Exception ex) {return;}
    }

    private boolean drag = false;

    /** MouseListener methods */
    public void mouseClicked(MouseEvent e)  {forwardEvent(e);}
    public void mouseEntered(MouseEvent e)  {forwardEvent(e);}
    public void mouseExited(MouseEvent e)   {forwardEvent(e);}
    public void mousePressed(MouseEvent e)  {forwardEvent(e); drag = true; }
    public void mouseReleased(MouseEvent e) {forwardEvent(e); drag = false;}

    public void mouseMoved(MouseEvent e)    {}
    
    public void mouseDragged(MouseEvent e)
    {   if (drag)
        {   DeadKeyTableModel model = (DeadKeyTableModel)getModel();
            Point point = getMousePosition();
            if (point==null) return;
            int row = rowAtPoint(point);
            int col = columnAtPoint(point);
            if (model.isInsertOK(row, col)&&(model.getValueAt(row, col)!=null))
            {   changeSelection(row, col, false, false);
                TransferHandler handler = getTransferHandler();
                JComponent component = (JComponent)e.getSource();
                handler.exportAsDrag(component, e, TransferHandler.COPY);
            }
            else drag = false;
       }
    }


}       // End of DeadKeys class
