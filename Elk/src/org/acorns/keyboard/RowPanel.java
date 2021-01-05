/*
 * KeyPanel.java
 *   Maintain a row of keyboard keys in the virtual keyboard
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
import java.awt.*;

import org.acorns.data.*;

public class RowPanel extends JPanel implements Constants
{
 	private static final long serialVersionUID = 1L;

	private static int INDENT_MULTIPLE = 10, STRUT = 2;

    private static String[] ctrls = {"shift","ctrl","meta","alt","caps"," "};


    /** Constructor to create a row of keys
     *
     * @param keys The data for the keys to buils
     * @param indent How much to indent the first column
     */
    public RowPanel(int[][] keys, int indent)
    {  KeyPanel panel;
       char top, bottom, code, ctrlKey;
       String name;
       
       setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
       add(Box.createHorizontalStrut(indent * INDENT_MULTIPLE));
       
       for (int i=0; i<keys.length;i++)
       { int index = keys[i][TOP];
         code = (char)keys[i][KEY];
         if (index>=STRING)
         {  ctrlKey = (char)(keys[i][TOP] - STRING);
            name = ctrls[ctrlKey];
            panel = new KeyPanel(name, ctrlKey, code);
         }
         else 
         {   top = (char)keys[i][TOP];
             bottom = (char)keys[i][BOTTOM];
             panel = new KeyPanel(top, bottom, code);
         }
         add(panel);
         add(Box.createHorizontalStrut(STRUT));
         setBackground(DARKGREY);
       }
    }

    /** Method to find the KeyPanel object corresponding to the appropriate code
     * 
     * @param name of the code of the component to find
     * @return KeyPanel if found, or none
     */
    public KeyPanel findKeyPanel(String name)
    {   Component component;
        KeyPanel  keyPanel;
        
        int count = getComponentCount();
        for (int i=0; i<count; i++)
        {   component = getComponent(i);
            if (component instanceof KeyPanel)
            {  keyPanel = (KeyPanel)component;
               if (keyPanel.getName().equals(name)) return keyPanel;                
            }
        }
        return null;
    }

    /** Method tofind a KeyPanel component at a particular mouse point.
     *
     * @param point The Point in question
     * @return KeyPanel if found, null otherwise
     */
    public KeyPanel findKeyPanelAtLocation(Point point)
    {   Component component;
        KeyPanel  keyPanel;
        Point location;

        int count = getComponentCount();
        for (int i=0; i<count; i++)
        {   component = getComponent(i);
            if (component instanceof KeyPanel)
            {   keyPanel = (KeyPanel)component;
                location = SwingUtilities.convertPoint(this, point,keyPanel);
               if (keyPanel.contains(location))  { return keyPanel;  }
            }
        }
        return null;
    }
}        // End of RowPanel class
