/*
 * KeyPanel.java
 *   Maintain a keyboard key in the virtual keyboard
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

package org.acorns.keyboard;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import org.acorns.data.*;

public class KeyPanel extends JPanel implements Constants
{
	private static final long serialVersionUID = 1L;
	private static Color CONTROL_SELECT = Color.RED;
    private static Color CONTROL_BG = new Color(0xc0,0xc0,0xc0);
    private static Color KEY_FG = Color.RED;
    private static Color LABEL_BG = new Color(0xd0,0xd0,0xd0);
    private static Color LABEL_FG = Color.BLACK;

    private static int LABEL_HEIGHT = 35;
    private JLabel keyLabel, controlLabel;
    private char   id;
    private int    leftOrRight;

    /** Constructor to create a control component
     * 
     * @param name Name of text to go in the component
     * @param index to indicate which control component
     * @param code flag for left or right key (0==left)
     */
     public KeyPanel(String name, char index, char code)
     {  controlLabel = new JLabel(name);
        if (name.length()<2)
             initializeLabel(controlLabel, new Dimension(200, LABEL_HEIGHT));
        else initializeLabel(controlLabel, new Dimension(40, LABEL_HEIGHT));

        controlLabel.setForeground(LABEL_FG);
        controlLabel.setBackground(CONTROL_BG);
        add(controlLabel);

        setName(controlLabel.getText() + (int)code);
        id = index;
        leftOrRight = code;
        int width = controlLabel.getPreferredSize().width;
        initialize(controlLabel, new Dimension(width, LABEL_HEIGHT));
    }

    /** Constructor for normal keys of the virtual keyboard
     *
     * @param top The upper case key value
     * @param bottom The lower case key value
     * @param code The MAC key code
     */
    public KeyPanel(char top, char bottom, char code)
    {  JLabel label = new JLabel("<html>"+top+"<br>"+bottom+"</html>");
       initializeLabel(label, new Dimension(15, LABEL_HEIGHT));
       label.setForeground(LABEL_FG);
       add(label);

       keyLabel = new JLabel("   ");
       initializeLabel(keyLabel, new Dimension(30,LABEL_HEIGHT));
       keyLabel.setForeground(KEY_FG);
       add(keyLabel);


       setName("" + (int)code);
       id = code;
       leftOrRight = -1;
       initialize(label, new Dimension(40,LABEL_HEIGHT));
    }

    /** Return whether this is a control key */
    public boolean isControl() { return leftOrRight>=0; }

    /* Get the components id value */
    public char getID() { return id; }


    /** Method to finish up thew work of the constructor
     *  @param label The initial component of the panel
     *  @param size of panel
     */
    private void initialize(JLabel label, Dimension size)
    {  setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
       setPreferredSize(size);
       setMinimumSize(size);
       setMaximumSize(size);
       setBackground(LABEL_BG);
       Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
       setBorder(border);
       setTransferHandler(new KeyboardTransferHandler());
    }

    /** Method to initialize label size, background, and alignment */
    private void initializeLabel(JLabel label, Dimension size)
    {   label.setPreferredSize(size);
        label.setMaximumSize(size);
        label.setMinimumSize(size);

        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        label.setOpaque(true);
        label.setBackground(LABEL_BG);
    }
    
    /** Method to display a character in one of the virtual keyboard keys
     * 
     * @param keyCode The character to display
     * @param font The appropriate fong
     */
    public void displayKey(char keyCode, Font font)
    {   if (isControl()) return;
        keyLabel.setFont(font);
        keyLabel.setText(" " + keyCode + " ");
    }
   
    /** Method to display the control key color based on the modifier setting
     * 
     * @param pressed true if the modifierisactive
     */
    public void displayModifier(boolean pressed)
    {   if (!isControl()) return;
        if (pressed) controlLabel.setForeground(CONTROL_SELECT);
        else controlLabel.setForeground(LABEL_FG);        
    }

    /** Method to set the text of the keyPanel object */
    public void setText(String text)
    {  if (isControl()) return;
       keyLabel.setText(text + " ");
    }

}
