/*
 * FontPanel.java
 *   Panel to display the current font, font size, language, and font preview
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

import org.acorns.data.*;

public class FontPanel extends JPanel implements Constants
{
   private static final long serialVersionUID = 1L;
   private JComboBox<String> sizeCombo;
   private JLabel           fontLabel;
   private JLabel           preview;
   private JTextField       languageText;

   private Font             font;

   private JPanel           fontPanel;
   private int              oldValue;
	
   public FontPanel()
   {  Font active = new Font(null, Font.PLAIN, 12);
      setBackground(DARKGREY);
      
      JPanel previewPanel = new JPanel();
      previewPanel.setFont(active);
      previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.X_AXIS));
      previewPanel.setBackground(DARKGREY);
      preview =new JLabel("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
                                        , JLabel.CENTER );
      preview.setForeground(LIGHTBLUE);
      previewPanel.add(Box.createHorizontalGlue());
      previewPanel.add(preview);
      previewPanel.add(Box.createHorizontalGlue());
    
      // Configure font size options
      fontPanel = this;
      String[] sizes = {"8", "10", "12", "14", "16", "18", "20"};
      oldValue = 12;

      sizeCombo = new JComboBox<String>( sizes );
	  sizeCombo.setBackground(OPTIONCOLOR);
      sizeCombo.setEditable(false);
      sizeCombo.setSelectedItem("" + active.getSize());
      sizeCombo.setSelectedIndex(2);
      sizeCombo.setMinimumSize(new Dimension(70,35));
      sizeCombo.addActionListener(
            new ActionListener() 
            {  public void actionPerformed(ActionEvent e)
               {  int newValue
                        = Integer.parseInt((String)sizeCombo.getSelectedItem());
                  previewFont(font);
                  fontPanel.firePropertyChange("FontChange",oldValue,newValue);
                  oldValue = newValue;
               }
            });

      // Configure font name options
      fontLabel = new JLabel(" ");
      fontLabel.setSize(new Dimension(250,25));
      fontLabel.setMaximumSize(fontLabel.getSize());
      fontLabel.setPreferredSize(fontLabel.getSize());
      fontLabel.setMinimumSize(fontLabel.getSize());
      fontLabel.setOpaque(true);
      fontLabel.setBackground(OPTIONCOLOR);
      fontLabel.setForeground(Color.BLACK);
            
      // Create language label and text field.
      languageText = new JTextField();
		Dimension size = new Dimension(200, 25);
      languageText.setMaximumSize(size);
      languageText.setPreferredSize(size);
      languageText.setSize(size);
      languageText.setToolTipText
              ("Enter language that applies to the selected font");
      
      // Create top panel to hold dropdowns for font selection, font size, and language box.
      JPanel selections = new JPanel();
      selections.setLayout(new BoxLayout(selections, BoxLayout.X_AXIS));
      selections.setBackground(DARKGREY);

      JLabel fontHeading = new JLabel("Font: ");
      fontHeading.setOpaque(true);
      fontHeading.setBackground(DARKGREY);
      fontHeading.setForeground(LIGHTBLUE);
      selections.add(fontHeading);

      selections.add(fontLabel);
      selections.add(Box.createHorizontalStrut(10));

      JLabel sizeLabel = new JLabel("Size: ");
      sizeLabel.setOpaque(true);
      sizeLabel.setBackground(DARKGREY);
      sizeLabel.setForeground(LIGHTBLUE);
      selections.add(sizeLabel);
      selections.add(sizeCombo);
      selections.add(Box.createHorizontalStrut(10));

      JLabel languageLabel = new JLabel("Language: ");
      languageLabel.setOpaque(true);
      languageLabel.setBackground(DARKGREY);
      languageLabel.setForeground(LIGHTBLUE);
      selections.add(languageLabel);
      selections.add(languageText);
      selections.setSize(new Dimension(600,30));
      selections.setPreferredSize(selections.getSize());
      selections.setMaximumSize(selections.getSize());

      // Now create the main panel
      setBackground(DARKGREY);
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(selections);
      add(Box.createVerticalStrut(5));
      add(previewPanel);
   }
	
  /** Preview a font and set the active language */
  private void previewFont(Font fontName)
  {   int size = Integer.parseInt((String)sizeCombo.getSelectedItem());
      if (font==null) font = new Font(null, Font.BOLD, size);
      else font = fontName.deriveFont(Font.PLAIN, size);
      preview.setFont(font);
  }

    /** Method to set the selected font name */
    public void setFontFace(Font fontName)
    {  this.font = fontName;
       if (fontName!=null)  fontLabel.setText(fontName.getFamily());
       else fontLabel.setText(" ");
       previewFont(fontName);
    }

    /** Method to get the selected font size */
    public int getFontSize()
    { return Integer.parseInt((String)sizeCombo.getSelectedItem()); }

    // Method to get language from the text field.
    public String getLanguage()
    { return languageText.getText(); }

    // Method to set language text field
    public void setLanguage(String language)
    {  languageText.setText(language); }

}     // End of FontPanel