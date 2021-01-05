/**
 *
 *   @name NameRecord.java
 *
 *   @author  harveyd
 *   @version 6.00
 *
 *   Copyright 2010, all rights reserved
 */

package com.sun.ttf;

/** A class to hold the data associated with each NameTable record */
public class NameRecord implements Comparable<Object>
{
   /** Platform ID  */
   short platformID;

   /**Platform Specific ID (Encoding)  */
   short platformSpecificID;

   /** Language ID */
   short languageID;

   /** Name ID  */
   short nameID;

   /** Create a new record  */
   NameRecord(short platformID, short platformSpecificID,
              short languageID, short nameID)
   {
       this.platformID = platformID;
       this.platformSpecificID = platformSpecificID;
       this.languageID = languageID;
       this.nameID = nameID;
   }


   /** Compare two records */
   public boolean equals(Object o) { return (compareTo(o) == 0);  }

   /** Get Record ID */
   public int getNameID() { return nameID; }

   /** Compare two records  */
   public int compareTo(Object obj)
   {  if (!(obj instanceof NameRecord)) {  return -1;  }

      NameRecord rec = (NameRecord) obj;

      if (platformID > rec.platformID) {  return 1; }
      else if (platformID < rec.platformID) { return -1; }
      else if (platformSpecificID > rec.platformSpecificID) { return 1; }
      else if (platformSpecificID < rec.platformSpecificID) { return -1; }
      else if (languageID > rec.languageID) { return 1; }
      else if (languageID < rec.languageID) { return -1; }
      else if (nameID > rec.nameID) { return 1; }
      else if (nameID < rec.nameID) { return -1; }
      else { return 0; }
   }
}
