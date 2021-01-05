/**
 *
 *   @name KeyboardChooser.java
 *      Class to filter .keylayout files for chooser dialogs
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

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;

import org.acorns.actions.*;

public class FrameDropTarget implements DropTargetListener
{   ButtonPanel buttons;

    public FrameDropTarget(ButtonPanel  buttons)
    {  this.buttons = buttons; }

    public void drop(DropTargetDropEvent dtde)
    {   dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        File[] files = getTransferObjects(dtde.getTransferable());
        if (files == null)
        { dtde.dropComplete(false);
          return;
        }
        String extension;
        String[] args = new String[1];
        for (int i=0; i<files.length; i++)
        {     args[0] = files[i].getPath();
              extension = args[0].substring(args[0].lastIndexOf(".")+1);
              if (extension.toLowerCase().equals("keylayout"))
              {  buttons.open(files[i], false);
                 break;
              }
              else if (extension.toLowerCase().equals("ttf"))
              {  buttons.open(files[i], true);
                 break;
              }
        }
        dtde.dropComplete(true);
    }
    public void dragEnter (DropTargetDragEvent dtde)
    {
        if (!acceptIt(dtde.getTransferable())) dtde.rejectDrag();
        else dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void dragExit (DropTargetEvent dte) {}

    public void dragOver (DropTargetDragEvent dtde)
    {   if (!acceptIt(dtde.getTransferable())) dtde.rejectDrag();
        else dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public boolean isDropAcceptable(DropTargetDropEvent dtde)
    {  return acceptIt(dtde.getTransferable());  }

    public boolean isDragAcceptable(DropTargetDragEvent dtde)
    { return acceptIt(dtde.getTransferable());
    }

    public void dropActionChanged (DropTargetDragEvent dtde)
    {   if (!acceptIt(dtde.getTransferable())) dtde.rejectDrag(); }

    /** Method to determine if drop type is correct
     *
     * @param transfer The transferable object
     * @return true if acceptible drop type.
     */
    private boolean acceptIt(Transferable transfer)
    {
        DataFlavor[] flavors = transfer.getTransferDataFlavors();
        for (int i=0; i<flavors.length; i++)
        { if (flavors[i].getRepresentationClass() == List.class) return true;
           if (flavors[i].getRepresentationClass() == AbstractList.class)
                return true;
        }
        return false;
    }

    /** Method to get the transferable list of files
     *
     * @param transfer The transferable object
     * @return An array of file objects or (null if none)
     */
    private File[] getTransferObjects(Transferable transfer)
    {
        DataFlavor[] flavors = transfer.getTransferDataFlavors();
        File[] file = new File[1];

        DataFlavor listFlavor = null;
        AbstractList<?> list = null;

        for (int i=0; i<flavors.length; i++)
        {  if (flavors[i].getRepresentationClass() == List.class)
                listFlavor = flavors[i];
           if (flavors[i].getRepresentationClass() == AbstractList.class)
                listFlavor = flavors[i];
        }

        try
        {  if (listFlavor!=null)
           {   list = (AbstractList<?>)transfer.getTransferData(listFlavor);

               int size = list.size();
               file = new File[1];
               String extension;

               for (int i=0; i<size; i++)
               {  file[0] = (File)list.get(i);
                  extension = file[0].getName();
                  extension = extension.substring(extension.lastIndexOf(".")+1);

                 if (extension.toLowerCase().equals("keylayout"))
                 {  if (buttons.isTTFLoaded()) return file;  
                    else buttons.setText("Please first load a font file");
                 }
                 else if (extension.toLowerCase().equals("ttf")) 
                 {   return file;  }
               }
           }
        }
        catch (Throwable e) {}
        return null;
    }   // End acceptIt()
}       // End of FrameDropTarget class
