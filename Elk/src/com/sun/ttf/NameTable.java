/*
 * $Id: NameTable.java,v 1.2 2007/12/20 18:33:30 rbair Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.sun.ttf;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author  jon
 */
public class NameTable extends TrueTypeTable {
    /**
     * Values for platformID
     */
    public static final short PLATFORMID_UNICODE    = 0;
    public static final short PLATFORMID_MACINTOSH  = 1;
    public static final short PLATFORMID_MICROSOFT  = 3;
    
    /**
     * Values for platformSpecificID if platform is Mac
     */
    public static final short ENCODINGID_MAC_ROMAN = 0;
    
    /**
     * Values for platformSpecificID if platform is Unicode
     */
    public static final short ENCODINGID_UNICODE_DEFAULT = 0;
    public static final short ENCODINGID_UNICODE_V11     = 1;
    public static final short ENCODINGID_UNICODE_V2      = 3;
    
    /**
     * Values for language ID if platform is Mac
     */
    public static final short LANGUAGEID_MAC_ENGLISH     = 0;
    
    /**
     * Values for nameID
     */
    public static final short NAMEID_COPYRIGHT        = 0;
    public static final short NAMEID_FAMILY           = 1;
    public static final short NAMEID_SUBFAMILY        = 2;
    public static final short NAMEID_SUBFAMILY_UNIQUE = 3;
    public static final short NAMEID_FULL_NAME        = 4;
    public static final short NAMEID_VERSION          = 5;
    public static final short NAMEID_POSTSCRIPT_NAME  = 6;
    public static final short NAMEID_TRADEMARK        = 7;
    /**
     * The format of this table
     */
    private short format;
    
    /**
     * The actual name records
     */
    private SortedMap<NameRecord, String> records;
    
    
    /** Creates a new instance of NameTable */
    protected NameTable() {
        super (TrueTypeTable.NAME_TABLE);
        
        records = Collections.synchronizedSortedMap(new TreeMap<NameRecord, String>());
    }
    
    /**
     * Add a record to the table
     */
    public void addRecord(short platformID, short platformSpecificID,
                          short languageID, short nameID,
                          String value) {
        NameRecord rec = new NameRecord(platformID, platformSpecificID,
                                        languageID, nameID);
        records.put(rec, value);
    }
    
    /** Get a record from the table */
    public String getRecord(short platformID, short platformSpecificID,
                            short languageID, short nameID) {
    
        NameRecord rec = new NameRecord(platformID, platformSpecificID,
                                        languageID, nameID);
        return records.get(rec);
    }

    /** Get all records in the keySet */
    public Iterator<NameRecord> getRecords() { return records.keySet().iterator(); }
 
    /** Method to get a record based on the key value */
    public String getRecord(NameRecord record)
    { return records.get(record); }

    /** Update a record in the table */
    public void putRecord(NameRecord record, String newData)
    {   String data = records.get(record);
        if (data==null) return;

        records.put(record, newData);
    }
    
    /**
     * Remove a record from the table
     */
    public void removeRecord(short platformID, short platformSpecificID,
                             short languageID, short nameID) {
        NameRecord rec = new NameRecord(platformID, platformSpecificID,
                                        languageID, nameID);
        records.remove(rec);
    }
    
    /**
     * Determine if we have any records with a given platform ID
     */
    public boolean hasRecords(short platformID) {
        for (Iterator<NameRecord> i = records.keySet().iterator(); i.hasNext(); ) {
            NameRecord rec = i.next();
            
            if (rec.platformID == platformID) {
                return true;
            }
        }
        
        return false;
    }
    
    /** 
     * Determine if we have any records with a given platform ID and
     * platform-specific ID
     */
    public boolean hasRecords(short platformID, short platformSpecificID) {
        for (Iterator<NameRecord> i = records.keySet().iterator(); i.hasNext(); ) {
            NameRecord rec = i.next();
            
            if (rec.platformID == platformID && 
                    rec.platformSpecificID == platformSpecificID) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Read the table from data
     */
    public @Override void setData(ByteBuffer data) {
        //read table header
        setFormat(data.getShort());
        int count = data.getShort();
        int stringOffset = data.getShort();
        
        // read the records
        for (int i = 0; i < count; i++) {
            short platformID = data.getShort();
            short platformSpecificID = data.getShort();
            short languageID = data.getShort();
            short nameID = data.getShort();
            
            int length = data.getShort();
            int offset = data.getShort();
            
            // read the String data
            data.mark();
            data.position(stringOffset + offset);
            
            ByteBuffer stringBuf = data.slice();
            stringBuf.limit(length);
            
            data.reset();
            
            // choose the character set
            String charsetName = getCharsetName(platformID, platformSpecificID);
            Charset charset = Charset.forName(charsetName);
            
            // parse the data as a string
            String value = charset.decode(stringBuf).toString();
        
            // add to the mix
            addRecord(platformID, platformSpecificID, languageID, nameID, value);
        }
    }
    
    /**
     * Get the data in this table as a buffer
     */
    public ByteBuffer getData() {
        // alocate the output buffer
        ByteBuffer buf = ByteBuffer.allocate(getLength());
        
        // the start of string data
        short headerLength = (short) (6 + (12 * getCount()));
        
        // write the header
        buf.putShort(getFormat());
        buf.putShort(getCount());
        buf.putShort(headerLength);
        
        // the offset from the start of the strings table
        short curOffset = 0;
        
        // add the size of each record
        for (Iterator<NameRecord> i = records.keySet().iterator(); i.hasNext();)
        {
            NameRecord rec = i.next();
            String value = records.get(rec);
        
            // choose the charset
            String charsetName = getCharsetName(rec.platformID,
            rec.platformSpecificID);
            Charset charset = Charset.forName(charsetName);
            
            // encode
            ByteBuffer strBuf = charset.encode(value);
            // Remove the byte order mark if it exists
            if (strBuf.remaining()>=2 && strBuf.get(0)==-2 && strBuf.get(1)==-1)
                strBuf.position(2);

            short strLen = (short) strBuf.remaining();
            
            // write the IDs
            buf.putShort(rec.platformID);
            buf.putShort(rec.platformSpecificID);
            buf.putShort(rec.languageID);
            buf.putShort(rec.nameID);
            
            // write the size and offset
            buf.putShort(strLen);
            buf.putShort(curOffset);
            
            // remember or current position
            buf.mark();
            
            // move to the current offset and write the data
            buf.position(headerLength + curOffset);
            buf.put(strBuf);
            
            // reset stuff
            buf.reset();
            
            // increment offset
            curOffset += strLen;
        }
        
        // reset the pointer on the buffer
        buf.position(headerLength + curOffset);
        buf.flip();
        
        return buf;
    }
    
    /**
     * Get the length of this table
     */
    public int getLength() {
        // start with the size of the fixed header plus the size of the
        // records
        int length = 6 + (12 * getCount());
        
        // add the size of each record
        for (Iterator<NameRecord> i = records.keySet().iterator(); i.hasNext();)
        {   NameRecord rec = i.next();
            String value = records.get(rec);
        
            // choose the charset
            String charsetName = getCharsetName(rec.platformID,
            rec.platformSpecificID);
            Charset charset = Charset.forName(charsetName);
            
            // encode
            ByteBuffer buf = charset.encode(value);
            // Remove the byte order mark if it exists
            if (buf.remaining()>=2 && buf.get(0)==-2 && buf.get(1)==-1)
                buf.position(2);
                
            // add the size of the coded buffer
            length += buf.remaining();
        }
        
        return length;
    }
    
    /**
     * Get the format of this table
     */
    public short getFormat() {
        return format;
    }
    
    /**
     * Set the format of this table
     */
    public void setFormat(short format) {
        this.format = format;
    }
    
    /**
     * Get the number of records in the table
     */
    public short getCount() {
        return (short) records.size();
    }
    
    /**
     * Get the charset name for a given platform, encoding and language
     */
    public static String getCharsetName(int platformID, int encodingID) {
        String charset = "US-ASCII";   
            
        switch (platformID) {
            case PLATFORMID_UNICODE:
                charset = "UTF-16";
                break;
            case PLATFORMID_MICROSOFT:
                charset = "UTF-16";
                break;
        }
        
        return charset;
    }
    
    /** Get a pretty string */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
        
        buf.append(indent + "Format: " + getFormat() + "\n");
        buf.append(indent + "Count : " + getCount() + "\n");
        
        for (Iterator<NameRecord> i = records.keySet().iterator(); i.hasNext();) {
            NameRecord rec = i.next();
            
            buf.append(indent + " platformID: " + rec.platformID);
            buf.append(" platformSpecificID: " + rec.platformSpecificID);
            buf.append(" languageID: " + rec.languageID);
            buf.append(" nameID: " + rec.nameID + "\n");
            buf.append(indent + "  " + records.get(rec) + "\n");
        }
        
        return buf.toString();
    }
}

