/*
 * $Id: HmtxTable.java,v 1.2 2007/12/20 18:33:31 rbair Exp $
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

/**
 * Model the TrueType Post table
 *
 * @author  jkaplan
 */
public class HmtxTable extends TrueTypeTable {
    /** advance widths for any glyphs that have one */
    short advanceWidths[];
    
    /** left side bearings for each glyph */
    short leftSideBearings[];
    
    /** Creates a new instance of HmtxTable */
    protected HmtxTable(TrueTypeFont ttf) {
        super (TrueTypeTable.HMTX_TABLE);
    
        MaxpTable maxp = (MaxpTable) ttf.getTable("maxp");
        int numGlyphs = maxp.getNumGlyphs();
        
        HheaTable hhea = (HheaTable) ttf.getTable("hhea");
        int numOfLongHorMetrics = hhea.getNumOfLongHorMetrics();
        
        advanceWidths = new short[numOfLongHorMetrics];
        leftSideBearings = new short[numGlyphs]; 
    }
    
    /** get the advance of a given glyph */
    public short getAdvance(int glyphID) {
        if (glyphID < advanceWidths.length) {
            return advanceWidths[glyphID];
        } else {
            return advanceWidths[advanceWidths.length - 1];
        }
    }
      
    /** get the left side bearing of a given glyph */
    public short getLeftSideBearing(int glyphID) {
        return leftSideBearings[glyphID];
    }
    
    /** get the data in this map as a ByteBuffer */
    public ByteBuffer getData() {
        int size = getLength();
        
        ByteBuffer buf = ByteBuffer.allocate(size);
        
        // write the metrics
        for (int i = 0; i < leftSideBearings.length; i++) {
            if (i < advanceWidths.length) {
                buf.putShort(advanceWidths[i]);
            }
            
            buf.putShort(leftSideBearings[i]);
        }
        
        // reset the start pointer
        buf.flip();
        
        return buf;
    }
    
    /** Initialize this structure from a ByteBuffer */
    public void setData(ByteBuffer data) {
        for (int i = 0; i < leftSideBearings.length; i++) {
            if (i < advanceWidths.length) {
                advanceWidths[i] = data.getShort();
            }
            
            leftSideBearings[i] = data.getShort();
        }
    }
    
    /**
     * Get the length of this table
     */
    public int getLength() {
        return (advanceWidths.length * 2) + (leftSideBearings.length * 2);
    }
}

