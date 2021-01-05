/**
 * KeyEditPanel.java
 *   Class to maintain the output associated with a dead key sequence
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

import java.awt.*;
import javax.swing.*;
import org.acorns.data.*;

public class KeyEditPanel  extends JTextField implements Constants
{
	private static final long serialVersionUID = 1L;

	public KeyEditPanel(Font font)   { setFont(font);  }

    /** Method to validate the text in this component.
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValidText() { return isValid(getText()); }

    /** Method to validate the unicode specifications within the text
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid(String data)
    {   String[] unicodes = new String[0];
        try { unicodes = data.toLowerCase().split("&#");  }
        catch (Exception e) {  System.out.println(e);  }

        int i=1, index = 0;
        if (unicodes[0].startsWith("&#")) i = 0;
        NumberFormatException nfe = new NumberFormatException();

        for (; i<unicodes.length; i++)
        {   index = unicodes[i].indexOf(';');
            try
            {   if (unicodes[i].toLowerCase().startsWith("x"))
                {  unicodes[i] = unicodes[i].substring(1);
                   if (index<1 || index>4) throw nfe;
                   unicodes[i] = unicodes[i].substring(0,index-1);
                   Integer.parseInt(unicodes[i],16);
                }
                else
                {  if (index<1 || index>6) throw nfe;
                   unicodes[i]=unicodes[i].substring(0,index);
                   Integer.parseInt(unicodes[i]);
                }
            }
            catch (NumberFormatException ex) { return false; }
        }
        return true;
    }

    /** Method to insert a string at the appropriate pixel position
     *
     * @param pixel offset to where to insert the string
     * @param data string to insert
     */
    public void insertString(int pixel, String data)
    {   Graphics g = getGraphics();
        FontMetrics metrics = getFontMetrics(getFont());

        String subData, text = getText();
        int width = (int)(metrics.getStringBounds(text, g).getWidth());
        if (pixel > width || width > getWidth()-20)
        {   setText(getText() + data);
            return;
        }
        
        for (int i=text.length(); i>0; i--)
        {   subData = text.substring(0,i);
            width = (int)(metrics.getStringBounds(subData, g).getWidth());
            if (pixel > width)
            {   subData = getText();
                setText(subData.substring(0,i-1) + data + subData.substring(i-1));
                return;
            }
        }
        setText(data + getText());
    }

    /** Method to determine if a point is within
     *     this component's bounds
     *
     * @param point point to determin if within this component
     * @return offset if yes, -1 if no
     */
    public int isWithin(Point point)
    {   Rectangle bounds = getBounds();
        if (bounds.x<=point.x && bounds.x+bounds.width>point.x)
             return point.x - bounds.x;
        else return -1;
    }
}   // End of KeyEditPanel
