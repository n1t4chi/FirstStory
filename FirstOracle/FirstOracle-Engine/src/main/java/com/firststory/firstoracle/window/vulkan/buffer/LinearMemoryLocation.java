/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import java.util.Comparator;

/**
 * @author n1t4chi
 */
public class LinearMemoryLocation {
    
    static final Comparator< LinearMemoryLocation > BY_TRUE_SIZE = LinearMemoryLocation::compareTrueLengthTo;
    static final Comparator< LinearMemoryLocation > BY_POSITION = LinearMemoryLocation::comparePositionTo;
    private static final int MAX_BLOCK_FREE_SPACE = 1;
    private int position;
    private int length;
    private int trueLength;
    
    public LinearMemoryLocation( int position, int length, int trueLength ) {
        this.position = position;
        this.length = length;
        this.trueLength = trueLength;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void merge( LinearMemoryLocation adjacent ) {
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
        
        LinearMemoryLocation that = ( LinearMemoryLocation ) o;
        
        if ( position != that.position ) { return false; }
        return length == that.length;
    }
    
    @Override
    public String toString() {
        return "MemoryLocation@" + hashCode() + "{" + "position=" + position + ", length=" + length + '}';
    }
    
    public int compareTrueLengthTo( LinearMemoryLocation o ) {
        return Integer.compare( getTrueLength(), o.getTrueLength() );
    }
    
    public int comparePositionTo( LinearMemoryLocation o ) {
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
    
    boolean isOffsetEarlier( LinearMemoryLocation o ) {
        return getPosition() < o.getPosition();
    }
    
    boolean adjacent( LinearMemoryLocation o ) {
        if ( equals( o ) ) {
            return true;
        }
        if ( isOffsetEarlier( o ) ) {
            return end() >= o.getPosition();
        }
        
        return o.end() >= getPosition();
    }
    
    LinearMemoryLocation split( int length ) {
        if ( getTrueLength() - length < MAX_BLOCK_FREE_SPACE ) {
            setLength( length );
            return null;
        }
        LinearMemoryLocation newLocation = new LinearMemoryLocation( getPosition(), length, length );
        movePosition( length );
        return newLocation;
    }
}
