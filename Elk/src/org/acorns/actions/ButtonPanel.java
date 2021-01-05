/**
 * ButtonPanel.java
 *   Panel of buttons to control user options
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

package org.acorns.actions;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Component;
import java.awt.FileDialog;

import java.net.*;
import java.io.*;
import java.beans.*;
import javax.help.*;

import org.acorns.language.keyboards.data.Status;
import org.acorns.data.*;
import org.acorns.keyboard.*;
import org.acorns.deadkeys.*;
import org.acorns.font.*;
import org.acorns.lib.*;
import org.acorns.ttf.*;

public class ButtonPanel extends JPanel
       implements ActionListener, Constants, PropertyChangeListener
{
	private static final long serialVersionUID = 1L;
	private final static int NAME=0, TOOLTIP=1, STRUT=2, ICON_SIZE = 30;
    private final static int OPEN=0, IMPORT=1, EXPORT=2, SAVE=3;
    
    private KeyMapper map;
    private JLabel label;
    private FontPanel fontPanel;
    private DeadKeyPanels deadKeys;

    private FontKeys keys;
    private Thread   thread;
    private TTF      ttf;

    /** Constructor to cretat the panel of action buttons 
     *  @param keys panel holding the display of the font keys
     *  @param map panel that holds the current key map
     *  @param label label for holding error messages
     *  @param font Panel holding the selected font and language
     *  @param deadKeys class holding the array of dead key panels
     */
    public ButtonPanel(JScrollPane scroll, KeyMapper map, JLabel label
                         , FontPanel font, DeadKeyPanels deadKeys)
    {   this.map       = map;
        this.label     = label;
        this.fontPanel = font;
        this.deadKeys  = deadKeys;
        this.keys      = new FontKeys();

        scroll.setViewportView(keys);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(DARKGREY);

        String[][] names = { { "reset", "Reset font display", "50"},
                             { "open", "Open ttf font file", "0"},
                             { "save", "Save as ttf file", "50"},
                             { "export", "Export in .keylayout format", "0"},
                             { "import", "Import .keylayout file","50"},
                             { "help", "Help facility", "-1"},
                           };

        JButton[] buttons = new JButton[names.length];
        int strut;

        add(Box.createHorizontalGlue());
        HelpSet helpSet = getHelpSet();
        boolean help;

        for (int i=0; i<names.length; i++)
        {   help = names[i][NAME].equals("help");;
            if (help && helpSet==null) continue;

            buttons[i] = new JButton(getIcon(names[i][NAME] + ".png"));
            buttons[i].setToolTipText(names[i][TOOLTIP]);
            buttons[i].setName(names[i][NAME]);
            buttons[i].setSize(new Dimension(40,40));
            buttons[i].setPreferredSize(buttons[i].getSize());
            buttons[i].setMinimumSize(buttons[i].getSize());
            
            if (help)
            {   HelpBroker helpBroker = helpSet.createHelpBroker("Main_Window");
                ActionListener contentListener
                    = new CSH.DisplayHelpFromSource(helpBroker);

                // Attach help to the help button.
                contentListener = new CSH.DisplayHelpFromSource(helpBroker);
                buttons[i].addActionListener(contentListener);
            }
            else  buttons[i].addActionListener(this);

            add(buttons[i]);
            strut = Integer.parseInt(names[i][STRUT]);
            if (strut>0) add(Box.createHorizontalStrut(strut));
            if (strut<0) add(Box.createHorizontalGlue());
        }

        font.addPropertyChangeListener(this);

    }   // End of Constructor.

    /** <p>Method to retrieve an icon display</p>
     *
     * @param icon name of  icon to display
     */
     public ImageIcon getIcon(String icon)
     {
        ImageIcon image = null;
        try
        {
           URL url = ButtonPanel.class.getResource("/resources/" + icon);
           Image newImage  = Toolkit.getDefaultToolkit().getImage(url);
           newImage = newImage.getScaledInstance
                               (ICON_SIZE, ICON_SIZE, Image.SCALE_REPLICATE);
           image = new ImageIcon(newImage);
        }
        catch (Exception e)
        {  label.setText("couldn't load " + icon);
           System.exit(1);
        }
        return image;
     }

     /** <p>Method to respond to button clicks
      *
      * @param event Button click event triggering this method
      *
      */
     public void actionPerformed(ActionEvent event)
     {   File file = null;
         label.setText(" ");

         if (thread!=null && thread.isAlive())
         {  Toolkit.getDefaultToolkit().beep(); return; }

         JButton button = (JButton)event.getSource();
         String  name = button.getName();

         if (name.equals("reset"))
         {   keys.setVisible(false);
             map.clearDefaults(true);
             deadKeys.setIndigenousFont(null, true);
             keys.displayFont(null);
             label.setText("Reset operation complete");
         }
         else if (name.equals("open")) 
         {  file = getSelectedFile(OPEN, null);
            if (file==null) return;

            open(file, true);
         }
         else if (name.equals("import"))
         {  if (keys.getFontFace()==null)
            {   label.setText("Please first select a font");
                return;
            }

            file = getSelectedFile(IMPORT, null);
            if (file==null) return;
            open(file, false);
         }     // End import operation
         else if (name.equals("export"))
         {
            String language = fontPanel.getLanguage();
            if (language.length()==0)
            {   label.setText("Please enter the name of the language");
                return;
            }

            if (!deadKeys.isValidSequences())
            {  label.setText("Sorry, there are invalid, duplicate, "
                       + "or empty dead key sequences");
               return;
            }

            if (language.length()>0) language += ".keylayout";
            file = getSelectedFile(EXPORT, new File(language));
            if (file==null) return;

            try
            {  KeyboardData keyboardData = new KeyboardData(language);
               map.getMap(keyboardData);
               deadKeys.getSequences(keyboardData);
               new ExportKeyboard(file, keyboardData);
               label.setText("Export operation complete");
            }
            catch (Exception e)
            {  label.setText
                       ("Couldn't export "+file.getName()+" "+e.getMessage());
            }
         }     // End export operation
         else  if (name.equals("save"))
         {  Font fontObject = keys.getFontFace();
            if (fontObject==null)
            { label.setText("Please first select a font");  return; }

            String saveFile = fontObject.getFamily();
            String language = fontPanel.getLanguage();
            if (!saveFile.endsWith(language))  saveFile += language;
            saveFile = saveFile.replaceAll("\\s+","");
            if (language.length()==0)
            { label.setText("Please enter the name of the language"); return; }
            
            file = getSelectedFile(SAVE, new File(saveFile));
            if (file!=null) 
            {  try 
               {  KeyboardData keyboardData = new KeyboardData(language);
                  map.getMap(keyboardData);
                  ttf.writeTTF(keyboardData, file);
                  label.setText("Save operation complete");
               } catch (Exception e) { label.setText( e.toString()); }
            }   // End if file not null
         }      // End if save operation
         else label.setText(button.getName()); // Illegal operation
     }

     private final static String[][] options = {
        {"TTF Font", ".ttf", "Open"},
        {"Keyboard Layout", ".keylayout", "Import"},
        {"Keyboard Layout", ".keylayout", "Export"},
        {"TTF Font", ".ttf", "Save"}   };

     /** Method to get the name of a file for the various options
      *
      * @param option OPEN, IMPORT, EXPORT, or SAVE
      * @param file The default file name, if EXPORT or SAVE
      * @return The selected File object or null
      */
     private File getSelectedFile(int option, File file)
     {  
        String type = options[option][0];
        String op = options[option][2];
        String title = op + " " + type;
        
        String extension = options[option][1];
        KeyboardChooser chooser = new KeyboardChooser(type, extension);

        boolean ttfFlag = (option==OPEN) || (option==SAVE);
        boolean load = (option==OPEN) || (option==IMPORT);

        File directory = Status.getPath(ttfFlag);
        Component parent = getParent();

        String osName = System.getProperty("os.name");
        if (!osName.contains("Mac")) 
        {  
            JFileChooser fc = new JFileChooser(directory);
            fc.setFileFilter(chooser);
            
            if (file!=null)  fc.setSelectedFile(file);
            
	        fc.setDialogTitle(title);
            int returnVal = JFileChooser.CANCEL_OPTION;
	        try
	        {   
	        	if (load) returnVal = fc.showOpenDialog(parent);
	            else returnVal = fc.showSaveDialog(parent);
	            if (returnVal != JFileChooser.APPROVE_OPTION)
	            {   
	            	label.setText(op + " canceled");
	                return null;
	            }
	        }
	        catch (Exception e)
	        {
	        	label.setText("Concurrent execution rejected - try again");
	        	return null;
	        }

            Status.setPath(fc.getCurrentDirectory(), ttfFlag);
            file = fc.getSelectedFile();
        }
        else
        {
      	    int dialogOption = FileDialog.SAVE;
      	    if (load)
      		    dialogOption = FileDialog.LOAD;
      	  
      	    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            FileDialog fd = new FileDialog(new Dialog(topFrame), title, dialogOption);
            
            fd.setDirectory("");
            if (load)
          	  fd.setDirectory(directory.getAbsolutePath());
            
            fd.setFilenameFilter(chooser);
 
	        if (file!=null && !load)
	        	fd.setFile(new File(file + extension).getName());

            fd.setVisible(true);
            
            String fileName = fd.getFile();
            String dir = fd.getDirectory();
            String fullPath = dir + fileName;
            
            if (fileName != null && fileName.length()!=0)
            {
                file = new File(fullPath);
            }
        }

        String path;
        try { path = file.getCanonicalPath(); }
        catch (IOException ioe)
        { label.setText("Illegal file name: " + file.getName());
          return null;
        }

        if (!path.toLowerCase().endsWith(extension))
        {  path += extension;
           file = new File(path);
        }

        if (option!=IMPORT && option!=OPEN)
        {   
        	if (file.exists())
            {  
        		int returnVal = JOptionPane.showConfirmDialog
                             (getParent(), "File exists - delete?");
               if (returnVal!=JOptionPane.YES_OPTION) 
               {   
            	   label.setText(op + " canceled"); return null; 
               }
            }
        }
        return file;
     }

     /** Method to set the font panel
      *
      * @param panel the FontPanel object
      */
     public void setFont(FontPanel panel)  { fontPanel = panel; }

     /** Internal class to open a font and display it in the panels  */
     class FontAction extends Thread
     {  TTF ttf;
        boolean reset;

        /** Thread to display font
         *
         * @param ttf The ttf font object
         * @param reset Flag as to whether we should reset the dead key panel
         */
        public FontAction(TTF ttf, boolean reset)
        {  this.ttf = ttf;
           this.reset = reset;
           start();
        }

        public @Override void run()
        {    label.setText("Font loading, please wait for font to display");

             keys.setVisible(false);
             Font fontObject = ttf.getFont();
             fontPanel.setFontFace(fontObject);
             map.initialize(fontObject);
             for (int i=0; i<MODIFIERS; i++)  map.setDefaults(i, null);

             deadKeys.setIndigenousFont(fontObject, reset);
             keys.displayFont(fontObject);
             keys.setVisible(true);
             label.setText("Font loaded and is ready for use");
        }
     }

     /** Listener method to respond to font size changes */
     public void propertyChange(PropertyChangeEvent event)
     {   if (keys.getFontFace()==null) return;
         if (!event.getPropertyName().equals("FontChange")) return;
         if (!(event.getOldValue() instanceof Integer)) return;
         if (!(event.getNewValue() instanceof Integer)) return;
         if (thread!=null && thread.isAlive())
         {  Toolkit.getDefaultToolkit().beep(); return; }

         Integer oldInt = (Integer)event.getOldValue();
         Integer newInt = (Integer)event.getNewValue();

         int oldVal = oldInt.intValue();
         int newVal = newInt.intValue();

         if (oldVal==newVal) return;
         ttf.setFontSize(newVal);
         thread = new FontAction(ttf, false);
     }

    /** Method to get the ELK help set
     *
     * @return HelpSet object
     */
    public static HelpSet getHelpSet()
    {  try
       {  URL helpURL = ButtonPanel.class.getResource("/helpData/elk.hs" );
          if (helpURL==null) throw new Exception();

          ClassLoader loader =  ButtonPanel.class.getClassLoader();
 	  HelpSet helpSet = new HelpSet(loader, helpURL);
          return helpSet;
       }
       catch (Throwable t) {}
       return null;
    }

    /** Method to determine if a TTF file is loaded */
    public boolean isTTFLoaded()  { return keys.getFontFace()!=null; }

    /** Method to open ttf or keylayout files */
    public void open(File file, boolean ttfFlag)
    {   
    	if (ttfFlag)
        {   try
            {  ttf = new TTF(file, fontPanel.getFontSize());
               thread = new FontAction(ttf, true);
            }
            catch (Exception e)
            {  label.setText("Font instantiation error: " + e.getMessage()); }
        }
        else
        {   if (isTTFLoaded())
            {   URL url = null;
                try
                {  url = file.toURI().toURL();
                   Font fontObject = ttf.getFont();
                   deadKeys.setIndigenousFont(fontObject, true);

                   ImportKeyboard keyboard = new ImportKeyboard(url, label);
                   KeyboardData data = keyboard.getData();
                   fontPanel.setLanguage(data.getLanguage());

                   char[] keyMap;
                   map.clearDefaults(false);
                   for (int i=0; i<MODIFIERS; i++)
                   {  keyMap = data.getModifierKeyMap(i);
                      map.setDefaults(i, keyMap);
                      if (keyMap==null) continue;
                      deadKeys.setSequences(i, data.getKeySequences(i) );
                   }
                   if (label.getText().length()==1)
                      label.setText("Import operation complete");
                }
                catch (Exception e)
                {  if (url==null) { label.setText("Illegal URL"); return; }
                   label.setText
                       ("Couldn't Parse " + file.getName() + " " + e.getMessage());
                }
            }
            else label.setText("Please first load a font file");
        }
    }       // End of open()

    /** Method to set text into the error label */
    public void setText(String text)
    {   label.setText(text); }

}           // End of ButtonPanel
