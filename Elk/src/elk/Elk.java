/**
 *
 *   @name Main.java
 *      Main application class to process .keylayout and .ttf files
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
package elk;

import org.acorns.language.keyboards.data.Status;
import org.acorns.data.*;
import org.acorns.deadkeys.*;
import org.acorns.font.*;
import org.acorns.actions.*;
import org.acorns.keyboard.*;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.net.*;

public class Elk implements Constants
{  private static KeyMapper mapper;
   private static JScrollPane scroll;
   private static ImageIcon icon;

   public static void main(String[] args)
   {  Status.readStatus();

      // Create font panel for west side
      JPanel fontLabel = new JPanel();
      fontLabel.setBackground(DARKGREY);
      fontLabel.setLayout(new BoxLayout(fontLabel, BoxLayout.Y_AXIS));

      JPanel fontInstruction
                       = makeLabel("  Drag a glyph from this panel  ", null);
      fontLabel.add(fontInstruction);
      fontLabel.add(Box.createVerticalStrut(10));

      scroll = new JScrollPane();
      fontLabel.add(scroll);

      // Create the dead key area
      DeadKeyPanels deadKeysPanels = new DeadKeyPanels(getIcon());

      // Create keyboard map in the center
      mapper = new KeyMapper(deadKeysPanels);
      JPanel mapPanel = new JPanel();
      mapPanel.setBackground(DARKGREY);
      mapPanel.setLayout(new BoxLayout(mapPanel, BoxLayout.Y_AXIS));

      FontPanel fontPanel = new FontPanel();
      JLabel label = new JLabel(" ", JLabel.CENTER);
      label.setForeground(Color.RED);
      label.setPreferredSize(new Dimension(700, 25));
      label.setMinimumSize(label.getPreferredSize());
      label.setAlignmentY(0.5f);
      JPanel errors = makeLabel(" ", label);

      ButtonPanel buttons = new ButtonPanel
                          (scroll, mapper, label, fontPanel, deadKeysPanels);
      mapPanel.add(buttons);
      mapPanel.add(Box.createVerticalStrut(20));

      mapPanel.add(fontPanel);
      mapPanel.add(Box.createVerticalStrut(10));
      mapPanel.add(errors);
      mapPanel.add(Box.createVerticalStrut(10));

      JPanel mapInstruction
              = makeLabel("Drop the glyph over the keyboard key", null);
      mapPanel.add(mapInstruction);

      mapPanel.add(Box.createVerticalStrut(15));
      mapPanel.add(mapper);
      mapPanel.add(Box.createVerticalGlue());

      // Create and initialize the frame
      JFrame frame = new JFrame("[E]xtended [L]inquistic [K]eyboards");
      frame.addWindowListener(new WindowAdapter()
          {   public @Override void windowClosing(WindowEvent ev)
              {   Status.writeStatus();
                  System.exit(0);
              }
          });
      DropTarget target = new DropTarget(frame, new FrameDropTarget(buttons));
      frame.setDropTarget(target);

      Container container = frame.getContentPane();

      // Fill out the frame
      container.setLayout(new BorderLayout());
      container.add(fontLabel, BorderLayout.WEST);
      container.add(mapPanel, BorderLayout.CENTER);

      container.add(deadKeysPanels, BorderLayout.SOUTH);

      frame.pack();
      frame.setIconImage(getIcon().getImage());
      frame.setVisible(true);
   }  // End of main()

 /** Method to create one the instruction labels at the top of the panels
  *  @param text to go in the label
  *  @param the label to include.
  */
   public static JPanel makeLabel(String text, JLabel newLabel)
   {  JLabel label;
      if (newLabel==null) label = new JLabel(text);
      else label = newLabel;

      label.setOpaque(true);
      label.setBackground(LIGHTBLUE);
      if (newLabel==null) label.setFont(new Font(null, Font.PLAIN, 24));
      label.setHorizontalTextPosition(JLabel.CENTER);
      label.setBorder(BorderFactory.createEtchedBorder());

      JPanel labelPanel = new JPanel();
      labelPanel.setBackground(DARKGREY);
      labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
      labelPanel.add(Box.createHorizontalGlue());
      labelPanel.add(label);
      labelPanel.add(Box.createHorizontalGlue());
      return labelPanel;
   }   // End of makeLabel()

   /** Get primary icon for frames and dialogs
    *
    * @return The Image object for the icon
    */
   public static ImageIcon getIcon()
   {   if (icon==null)
       {  URL url = Elk.class.getResource("/resources/elk.png");
          icon = new ImageIcon(url);
       }
       return icon;
   }
}      // End of Main class