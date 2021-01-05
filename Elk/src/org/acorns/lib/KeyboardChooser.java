/**
 *
 *   @name KeyboardChooser.java
 *      class to filter .keylayout files for chooser dialogs
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

package org.acorns.lib;

import javax.swing.filechooser.FileFilter;
import java.io.*;

public class KeyboardChooser extends FileFilter implements FilenameFilter
{
   String extension, description;

   public KeyboardChooser(String desc, String filter)
   {  
	  extension = filter.toLowerCase();
      description = desc;
   }

   /** Method to accept only files with the correct extension
    *
    * @param name Path to the file to check
    * @return true if okay, false otherwise
    */
   public boolean accept(File name)
   {   
       if (name == null) return false;
	   if (name.isDirectory()) return true;
       if (name.getName().toLowerCase().endsWith(extension)) return true;
       return false;
   }

   public boolean accept(File directory, String fileName)
   { if (directory==null || fileName==null) return false;
     return fileName.toLowerCase().endsWith(extension);
   }

   /** Method to return the name of this filter */
   public String getDescription()
   { return description + "  files (*" + extension + ")"; }
}  // End of KeyboardChooser class
