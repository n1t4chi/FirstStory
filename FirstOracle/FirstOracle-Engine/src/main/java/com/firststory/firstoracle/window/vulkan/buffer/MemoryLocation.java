/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import java.util.Comparator;

/**
 * @author n1t4chi
 */
public class MemoryLocation {
    
    static final Comparator< MemoryLocation > BY_TRUE_SIZE = MemoryLocation::compareTrueLengthTo;
    static final Comparator< MemoryLocation > BY_POSITION = MemoryLocation::comparePositionTo;
    private static final int MAX_BLOCK_FREE_SPACE = 1;
    private int position;
    private int length;
    private int trueLength;
    
    public MemoryLocation( int position, int length, int trueLength ) {
        this.position = position;
        this.length = length;
        this.trueLength = trueLength;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void merge( MemoryLocation adjacent ) {
        this.position = Math.min( position, adjacent.position );
        this.length = this.length + adjacent.length;
        this.trueLength = this.trueLength + adjacent.trueLength;
}
    
    void setPosition( int position ) {
        this.position = position;
    }
    
    public int getLength() {
        return length;
    }
    
    void setLength( int length ) {
        this.length = length;
    }
    
    public int getTrueLength() {
        return trueLength;
    }
    
    void setTrueLength( int length ) {
        this.trueLength = length;
    }
    
    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + length;
        return result;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        MemoryLocation that = ( MemoryLocation ) o;
        
        if ( position != that.position ) { return false; }
        return length == that.length;
    }
    
    @Override
    public String toString() {
        return "MemoryLocation@" + hashCode() + "{" + "position=" + position + ", length=" + length + '}';
    }
    
    public int compareTrueLengthTo( MemoryLocation o ) {
        return Integer.compare( getTrueLength(), o.getTrueLength() );
    }
    
    public int comparePositionTo( MemoryLocation o ) {
        return Integer.compare( getPosition(), o.getPosition() );
    }
    
    /**
     * @return first index outside this location.
     */
    public int end() {
        return position + length;
    }
    
    public void movePosition( int offset ) {
        this.position += offset;
        this.length -= offset;
        this.trueLength -= offset;
    }
    
    boolean isOffsetEarlier( MemoryLocation o ) {
        return getPosition() < o.getPosition();
    }
    
    boolean adjacent( MemoryLocation o ) {
        if ( equals( o ) ) {
            return true;
        }
        if ( isOffsetEarlier( o ) ) {
            return end() >= o.getPosition();
        }
        
        return o.end() >= getPosition();
    }
    
    MemoryLocation split( int length ) {
        if ( getTrueLength() - length < MAX_BLOCK_FREE_SPACE ) {
            setLength( length );
            return null;
        }
        MemoryLocation newLocation = new MemoryLocation( getPosition(), length, length );
        movePosition( length );
        return newLocation;
    }
}
