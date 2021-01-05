/**
 * DeadKeyPanel.java
 *   Class to maintain a dead key sequence and the associated output
 *   
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
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.net.*;

import org.acorns.data.*;
import org.acorns.lib.*;

/** Class to maintain a Deadkey panel
 *   It consists of a text field of keyboard a dead key sequence
 *   and the output that will echo when that sequence is entered
 */
public class DeadKeyPanel extends JPanel 
        implements Transferable, MouseListener, Constants, 
                   TableCellRenderer, DocumentListener
{
	private static final long serialVersionUID = 1L;
	private int          modifier;
    private JTextField   field;
    private KeyEditPanel keyEditor;

    // Static variables for the dialog panel
    private static ImageIcon arrow;
    private static JPanel dialogPanel;
    private static JTextField codeText, outputText;

    /** Constructor to make an object that can render DedKey components */
    public DeadKeyPanel() {}

    /** Constructor to create a dead key panel
     *
     * @param font The font to use
     * @param modifier The modifier for this panel
     */
    public DeadKeyPanel(Font font, int modifier)
    {   this.modifier = modifier;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Border border = BorderFactory.createEtchedBorder(Color.BLUE, DARKBLUE);
        setBorder(border);

        field = new JTextField();
        keyEditor = new KeyEditPanel(font);
        dialogPanel = null;

        if (arrow==null)
        {  URL imgURL = DeadKeyPanel.class.getResource("/resources/arrow.png");
           arrow = new ImageIcon(imgURL);
        }
        JLabel iconLabel = new JLabel(arrow);
        addMouseListener(this);

        setToolTipText("click to enter key sequence or drag glyph here");
       
        add(field);
        add(iconLabel);
        add(keyEditor);
    }

    public void setIndigenousFont(Font font) {keyEditor.setFont(font); }

    /** Method to get the current sequence from the panel */
    public DeadSequence getDeadSequence()
    {  String keyboard = field.getText();
       String output = keyEditor.getText();
       if (keyboard.length()==0) return null;
       return new DeadSequence(keyboard, output);
    }

    /** Method to determine if this sequence is valid */
    public boolean isValidSequence()
    {  if (!keyEditor.isValidText()) return false;
       if (!isValidCode(field.getText()))
          return false;
       return true;
    }

    /** Method to validate whether the sequence code is valid
     *
     * @param code The sequence code
     * @return fals if no, true otherwise
     */
    private boolean isValidCode(String code)
    {  boolean pass = code.length()!=0;
       if (pass)
       {   String last = code, newLast;
           last = code.substring(1);

           newLast = ModCase.setCase(modifier, last);
           if (!last.equals(newLast)) pass = false;
       }
       
       // Only keys that can be remapped are valid
       for (int i=0; i<code.length(); i++)
       {   if (code.charAt(i)<32 || code.charAt(i)>127)
          {  pass = false; break; }
       }
       return pass;
    }

    
    /** Method to set the date into the panel
     * 
     * @param key Keyboard key sequence
     * @param data Deadkey output
     */
    public void setData(String key, String data)
    {  field.setText(validateField(key));
       keyEditor.setText(data);
    }

     /* Mouse Listener methods */
    public void mouseEntered(MouseEvent event)  {}
    public void mouseExited(MouseEvent event)   {}
    public void mouseReleased(MouseEvent event) {}
    public void mousePressed(MouseEvent event)  {}

    /** Mouse Listener, respond with a JDialog
     *
     * @param event Mouse event triggering this listener
     */
    public void mouseClicked(MouseEvent event)
    {   JPanel panel           = getDialogPanel();
        JTextField codeField   = (JTextField)panel.getComponent(2);
        JTextField outputField = (JTextField)panel.getComponent(4);
        String title = "ELK key sequence dialog";

        while (true)
        {  int result = JOptionPane.showConfirmDialog(null, panel, title
                             , JOptionPane.OK_CANCEL_OPTION
                             , JOptionPane.INFORMATION_MESSAGE
                             , DeadKeyPanels.getIcon());
       
           if (result == JOptionPane.OK_OPTION)
           {   String code = codeField.getText();
               String output = outputField.getText();
               if (!(keyEditor.isValid(output) && isValidCode(code)))
               {  title =  "ELK Deadkey dialog - illegal input - try again"; }
               else
               {  field.setText(code);
                  keyEditor.setText(output);
                  return;
               }
           }
           else return;
        }  // End while.
    }

    private static DataFlavor deadKeyPanelFlavor = null;

    /** Method to get the DataFlavor for this transferable object
     *
     * @return DataFlavor object or null if none.
     */
    public static DataFlavor getDeadKeyPanelFlavor()
    {   try
        {  if (deadKeyPanelFlavor == null)
           {  deadKeyPanelFlavor  = new DataFlavor
                (DataFlavor.javaJVMLocalObjectMimeType
                               + ";class=org.acorns.deadkeys.DeadKeyPanel");
           }
        }
        catch (Exception e)
        { deadKeyPanelFlavor = null; }
        return deadKeyPanelFlavor;
    }

    public Object getTransferData(DataFlavor flavor)
    {  DataFlavor thisFlavor = getDeadKeyPanelFlavor();
       if (thisFlavor != null && flavor.equals(thisFlavor)) return thisFlavor;
       return null;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {   DataFlavor thisFlavor = getDeadKeyPanelFlavor();
        if (thisFlavor != null && flavor.equals(thisFlavor)) return true;
        return false;
    }

    public DataFlavor[] getTransferDataFlavors()
    {  DataFlavor thisFlavor = getDeadKeyPanelFlavor();
       if (thisFlavor != null)
           return new DataFlavor[] {thisFlavor};

       return null;
    }

    /** Render the dead key component in the parent JTable
     *
     * @param table The parent JTable
     * @param value The cell object
     * @param isSelected ture if the component is selected
     * @param hasFocus true if the component has focus
     * @param row Row in the JTable
     * @param column Column in the JTable
     * @return
     */
    public Component getTableCellRendererComponent
          ( JTable table, Object value, boolean isSelected, boolean hasFocus
                        , int row, int column)
    {
        if (value==null) return null;
        
        DeadKeyPanel panel = (DeadKeyPanel)value;
        Border border;

        if (hasFocus)
        {  border = BorderFactory.createEtchedBorder(Color.RED, DARKRED);
           panel.setBorder(border);
        }
        else
        {  border = BorderFactory.createEtchedBorder(Color.BLUE, DARKBLUE);  }
        panel.setBorder(border);
        return panel;
    }

    /** Method to create a new key sequence using a dialog
     *
     * @return Created key sequence panel
     */
    private JPanel getDialogPanel()
    {   if (dialogPanel==null)
        {   JLabel code = new JLabel("Code:");
            codeText = new JTextField(5);
            codeText.setToolTipText("Enter keyboard sequence");
            outputText = new JTextField(15);
            outputText.setFont(keyEditor.getFont());
            outputText.setToolTipText
               ("Enter ouput that will replace keyboard sequence");
            outputText.setDragEnabled(true);
            JLabel arrowLabel = new JLabel(arrow);

            dialogPanel = new JPanel();
            dialogPanel.setLayout
                    (new BoxLayout(dialogPanel, BoxLayout.X_AXIS));
            dialogPanel.add(Box.createHorizontalGlue());
            dialogPanel.add(code);
            dialogPanel.add(codeText);
            dialogPanel.add(arrowLabel);
            dialogPanel.add(outputText);
            dialogPanel.add(Box.createHorizontalGlue());

        }
        codeText.getDocument().addDocumentListener(this);
        codeText.setText(field.getText());
        outputText.setText(keyEditor.getText());
        return dialogPanel;
    }

    /** Method to append new text to the font field
     *
     * @param data the String data to append
     */
    public void insertFontKey(String data, Point point)
    {   int spot = keyEditor.isWithin(point);
        if (spot>=0) { keyEditor.insertString(spot, data); }
    }

    /** Method to determine if this point is within the
     *  @param point to determine if it is within the key editor.
     *  @return offset if >=0, negative otherwise.
     */
    public int isWithin(Point point)  { return keyEditor.isWithin(point); }

    public void changedUpdate(DocumentEvent e)
    {  String text = codeText.getText(), newText = validateField(text);
       if (!text.equals(newText)) new CodeThread(newText);
    }

    /** Return the string with the valid case for the code field */
    private String validateField(String text)
    {  String newText="", first="", rest="";

       if (text.length()>0) first = text.substring(0,1);
       if (text.length()>1) rest  = text.substring(1);

       newText = first + ModCase.setCase(modifier, rest);
       return newText;
    }

    public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
    public void insertUpdate(DocumentEvent e) { changedUpdate(e); }

    /** Thread to update the text field after the document leistener exits */
    class CodeThread extends Thread
    {  String data;

       public CodeThread(String data)
       {  this.data = data;
          start();
       }

        public @Override void run()  
        { if (data==null)
          {   System.out.println("null"); return; }
          int position = codeText.getCaretPosition();
          codeText.setText(data);
          codeText.setCaretPosition(position);
        }
    }
    
}   // End of DeadKeyPanel
