/*
 * $Id: MaxpTable.java,v 1.2 2007/12/20 18:33:31 rbair Exp $
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
 *
 * @author  jkaplan
 */
public class MaxpTable extends TrueTypeTable {
    
    /** Holds value of property version. */
    private int version;
    
    /** Holds value of property numGlyphs. */
    private short numGlyphs;
    
    /** Holds value of property maxPoints. */
    private short maxPoints;
    
    /** Holds value of property maxContours. */
    private short maxContours;
    
    /** Holds value of property maxComponentPoints. */
    private short maxComponentPoints;
    
    /** Holds value of property maxComponentContours. */
    private short maxComponentContours;
    
    /** Holds value of property maxZones. */
    private short maxZones;
    
    /** Holds value of property maxTwilightPoints. */
    private short maxTwilightPoints;
    
    /** Holds value of property maxStorage. */
    private short maxStorage;
    
    /** Holds value of property maxFunctionDefs. */
    private short maxFunctionDefs;
    
    /** Holds value of property maxInstructionDefs. */
    private short maxInstructionDefs;
    
    /** Holds value of property maxStackElements. */
    private short maxStackElements;
    
    /** Holds value of property maxSizeOfInstructions. */
    private short maxSizeOfInstructions;
    
    /** Holds value of property maxComponentElements. */
    private short maxComponentElements;
    
    /** Holds value of property maxComponentDepth. */
    private short maxComponentDepth;
    
    /** Creates a new instance of MaxpTable */
    protected MaxpTable() {
        super (TrueTypeTable.MAXP_TABLE);
        
        setVersion(0x10000);
        setNumGlyphs((short) 0);
        setMaxPoints((short) 0);
        setMaxContours((short) 0);
        setMaxComponentPoints((short) 0);
        setMaxComponentContours((short) 0);
        setMaxZones((short) 2);
        setMaxTwilightPoints((short) 0);
        setMaxStorage((short) 0);
        setMaxFunctionDefs((short) 0);
        setMaxInstructionDefs((short) 0);
        setMaxStackElements((short) 0);
        setMaxSizeOfInstructions((short) 0);
        setMaxComponentElements((short) 0);
        setMaxComponentDepth((short) 0);
    }
    
    /**
     * Set the values from data
     */
    public void setData(ByteBuffer data) {
        if (data.remaining() != 32) {
            throw new IllegalArgumentException("Bad size for Maxp table");
        }
        
        setVersion(data.getInt());
        setNumGlyphs(data.getShort());
        setMaxPoints(data.getShort());
        setMaxContours(data.getShort());
        setMaxComponentPoints(data.getShort());
        setMaxComponentContours(data.getShort());
        setMaxZones(data.getShort());
        setMaxTwilightPoints(data.getShort());
        setMaxStorage(data.getShort());
        setMaxFunctionDefs(data.getShort());
        setMaxInstructionDefs(data.getShort());
        setMaxStackElements(data.getShort());
        setMaxSizeOfInstructions(data.getShort());
        setMaxComponentElements(data.getShort());
        setMaxComponentDepth(data.getShort());
    }
    
    /**
     * Get a buffer from the data
     */
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(getLength());
        
        buf.putInt(getVersion());
        buf.putShort(getNumGlyphs());
        buf.putShort(getMaxPoints());
        buf.putShort(getMaxContours());
        buf.putShort(getMaxComponentPoints());
        buf.putShort(getMaxComponentContours());
        buf.putShort(getMaxZones());
        buf.putShort(getMaxTwilightPoints());
        buf.putShort(getMaxStorage());
        buf.putShort(getMaxFunctionDefs());
        buf.putShort(getMaxInstructionDefs());
        buf.putShort(getMaxStackElements());
        buf.putShort(getMaxSizeOfInstructions());
        buf.putShort(getMaxComponentElements());
        buf.putShort(getMaxComponentDepth());
    
        // reset the position to the beginning of the buffer
        buf.flip();
        
        return buf;
    }
    
    /**
     * Get the length of this table
     */
    public int getLength() {
        return 32;
    }
    
    /** Getter for property version.
     * @return Value of property version.
     *
     */
    public int getVersion() {
        return this.version;
    }
    
    /** Setter for property version.
     * @param version New value of property version.
     *
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    /** Getter for property numGlyphs.
     * @return Value of property numGlyphs.
     *
     */
    public short getNumGlyphs() {
        return this.numGlyphs;
    }
    
    /** Setter for property numGlyphs.
     * @param numGlyphs New value of property numGlyphs.
     *
     */
    public void setNumGlyphs(short numGlyphs) {
        this.numGlyphs = numGlyphs;
    }
    
    /** Getter for property maxPoints.
     * @return Value of property maxPoints.
     *
     */
    public short getMaxPoints() {
        return this.maxPoints;
    }
    
    /** Setter for property maxPoints.
     * @param maxPoints New value of property maxPoints.
     *
     */
    public void setMaxPoints(short maxPoints) {
        this.maxPoints = maxPoints;
    }
    
    /** Getter for property maxContours.
     * @return Value of property maxContours.
     *
     */
    public short getMaxContours() {
        return this.maxContours;
    }
    
    /** Setter for property maxContours.
     * @param maxContours New value of property maxContours.
     *
     */
    public void setMaxContours(short maxContours) {
        this.maxContours = maxContours;
    }
    
    /** Getter for property maxComponentPoints.
     * @return Value of property maxComponentPoints.
     *
     */
    public short getMaxComponentPoints() {
        return this.maxComponentPoints;
    }
    
    /** Setter for property maxComponentPoints.
     * @param maxComponentPoints New value of property maxComponentPoints.
     *
     */
    public void setMaxComponentPoints(short maxComponentPoints) {
        this.maxComponentPoints = maxComponentPoints;
    }
    
    /** Getter for property maxComponentContours.
     * @return Value of property maxComponentContours.
     *
     */
    public short getMaxComponentContours() {
        return this.maxComponentContours;
    }
    
    /** Setter for property maxComponentContours.
     * @param maxComponentContours New value of property maxComponentContours.
     *
     */
    public void setMaxComponentContours(short maxComponentContours) {
        this.maxComponentContours = maxComponentContours;
    }
    
    /** Getter for property maxZones.
     * @return Value of property maxZones.
     *
     */
    public short getMaxZones() {
        return this.maxZones;
    }
    
    /** Setter for property maxZones.
     * @param maxZones New value of property maxZones.
     *
     */
    public void setMaxZones(short maxZones) {
        this.maxZones = maxZones;
    }
    
    /** Getter for property maxTwilightPoints.
     * @return Value of property maxTwilightPoints.
     *
     */
    public short getMaxTwilightPoints() {
        return this.maxTwilightPoints;
    }
    
    /** Setter for property maxTwilightPoints.
     * @param maxTwilightPoints New value of property maxTwilightPoints.
     *
     */
    public void setMaxTwilightPoints(short maxTwilightPoints) {
        this.maxTwilightPoints = maxTwilightPoints;
    }
    
    /** Getter for property maxStorage.
     * @return Value of property maxStorage.
     *
     */
    public short getMaxStorage() {
        return this.maxStorage;
    }
    
    /** Setter for property maxStorage.
     * @param maxStorage New value of property maxStorage.
     *
     */
    public void setMaxStorage(short maxStorage) {
        this.maxStorage = maxStorage;
    }
    
    /** Getter for property maxFunctionDefs.
     * @return Value of property maxFunctionDefs.
     *
     */
    public short getMaxFunctionDefs() {
        return this.maxFunctionDefs;
    }
    
    /** Setter for property maxFunctionDefs.
     * @param maxFunctionDefs New value of property maxFunctionDefs.
     *
     */
    public void setMaxFunctionDefs(short maxFunctionDefs) {
        this.maxFunctionDefs = maxFunctionDefs;
    }
    
    /** Getter for property maxInstructionDefs.
     * @return Value of property maxInstructionDefs.
     *
     */
    public short getMaxInstructionDefs() {
        return this.maxInstructionDefs;
    }
    
    /** Setter for property maxInstructionDefs.
     * @param maxInstructionDefs New value of property maxInstructionDefs.
     *
     */
    public void setMaxInstructionDefs(short maxInstructionDefs) {
        this.maxInstructionDefs = maxInstructionDefs;
    }
    
    /** Getter for property maxStackElements.
     * @return Value of property maxStackElements.
     *
     */
    public short getMaxStackElements() {
        return this.maxStackElements;
    }
    
    /** Setter for property maxStackElements.
     * @param maxStackElements New value of property maxStackElements.
     *
     */
    public void setMaxStackElements(short maxStackElements) {
        this.maxStackElements = maxStackElements;
    }
    
    /** Getter for property maxSizeOfInstructions.
     * @return Value of property maxSizeOfInstructions.
     *
     */
    public short getMaxSizeOfInstructions() {
        return this.maxSizeOfInstructions;
    }
    
    /** Setter for property maxSizeOfInstructions.
     * @param maxSizeOfInstructions New value of property maxSizeOfInstructions.
     *
     */
    public void setMaxSizeOfInstructions(short maxSizeOfInstructions) {
        this.maxSizeOfInstructions = maxSizeOfInstructions;
    }
    
    /** Getter for property maxComponentElements.
     * @return Value of property maxComponentElements.
     *
     */
    public short getMaxComponentElements() {
        return this.maxComponentElements;
    }
    
    /** Setter for property maxComponentElements.
     * @param maxComponentElements New value of property maxComponentElements.
     *
     */
    public void setMaxComponentElements(short maxComponentElements) {
        this.maxComponentElements = maxComponentElements;
    }
    
    /** Getter for property maxComponentDepth.
     * @return Value of property maxComponentDepth.
     *
     */
    public short getMaxComponentDepth() {
        return this.maxComponentDepth;
    }
    
    /** Setter for property maxComponentDepth.
     * @param maxComponentDepth New value of property maxComponentDepth.
     *
     */
    public void setMaxComponentDepth(short maxComponentDepth) {
        this.maxComponentDepth = maxComponentDepth;
    }
    
    /**
     * Create a pretty String
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
        
        buf.append(indent + "Version          : " + Integer.toHexString(getVersion()) + "\n");
        buf.append(indent + "NumGlyphs        : " + getNumGlyphs() + "\n");
        buf.append(indent + "MaxPoints        : " + getMaxPoints() + "\n");
        buf.append(indent + "MaxContours      : " + getMaxContours() + "\n");
        buf.append(indent + "MaxCompPoints    : " + getMaxComponentPoints() + "\n");
        buf.append(indent + "MaxCompContours  : " + getMaxComponentContours() + "\n");
        buf.append(indent + "MaxZones         : " + getMaxZones() + "\n");
        buf.append(indent + "MaxTwilightPoints: " + getMaxTwilightPoints() + "\n");
        buf.append(indent + "MaxStorage       : " + getMaxStorage() + "\n");
        buf.append(indent + "MaxFuncDefs      : " + getMaxFunctionDefs() + "\n");
        buf.append(indent + "MaxInstDefs      : " + getMaxInstructionDefs() + "\n");
        buf.append(indent + "MaxStackElements : " + getMaxStackElements() + "\n");
        buf.append(indent + "MaxSizeInst      : " + getMaxSizeOfInstructions() + "\n");
        buf.append(indent + "MaxCompElements  : " + getMaxComponentElements() + "\n");
        buf.append(indent + "MaxCompDepth     : " + getMaxComponentDepth() + "\n");
    
        return buf.toString();
    }
}

