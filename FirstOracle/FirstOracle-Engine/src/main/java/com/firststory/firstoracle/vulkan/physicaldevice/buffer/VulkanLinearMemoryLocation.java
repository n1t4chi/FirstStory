/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

import java.util.Comparator;

/**
 * @author n1t4chi
 */
public class VulkanLinearMemoryLocation {
    
    static final Comparator< VulkanLinearMemoryLocation > BY_TRUE_SIZE = VulkanLinearMemoryLocation::compareTrueLengthTo;
    static final Comparator< VulkanLinearMemoryLocation > BY_POSITION = VulkanLinearMemoryLocation::comparePositionTo;
    private static final int MAX_BLOCK_FREE_SPACE = 4;
    private long position;
    private long length;
    private long trueLength;
    
    public VulkanLinearMemoryLocation( long position, long length, long trueLength ) {
        this.position = position;
        this.length = length;
        this.trueLength = trueLength;
    }
    
    public long getPosition() {
        return position;
    }
    
    public void merge( VulkanLinearMemoryLocation adjacent ) {
        this.position = Math.min( position, adjacent.position );
        this.length = this.length + adjacent.length;
        this.trueLength = this.trueLength + adjacent.trueLength;
}
    
    void setPosition( int position ) {
        this.position = position;
    }
    
    public long getLength() {
        return length;
    }
    
    void setLength( long length ) {
        if( length > trueLength ) {
            throw new CannotResizeException( length, trueLength );
        }
        this.length = length;
    }
    
    public long getTrueLength() {
        return trueLength;
    }
    
    void setTrueLength( int length ) {
        this.trueLength = length;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
    
        var that = ( VulkanLinearMemoryLocation ) o;
        
        if ( position != that.position ) { return false; }
        return length == that.length;
    }
    
    @Override
    public int hashCode() {
        var result = ( int ) ( position ^ ( position >>> 32 ) );
        result = 31 * result + ( int ) ( length ^ ( length >>> 32 ) );
        return result;
    }
    
    @Override
    public String toString() {
        return "MemoryLocation@" + hashCode() + "{" + "position=" + position + ", length=" + length + '}';
    }
    
    public int compareTrueLengthTo( VulkanLinearMemoryLocation o ) {
        return Long.compare( getTrueLength(), o.getTrueLength() );
    }
    
    public int comparePositionTo( VulkanLinearMemoryLocation o ) {
        return Long.compare( getPosition(), o.getPosition() );
    }
    
    /**
     * @return first index outside this location.
     */
    public Long end() {
        return position + length;
    }
    
    public void movePosition( long offset ) {
        this.position += offset;
        this.length -= offset;
        this.trueLength -= offset;
    }
    
    boolean isOffsetEarlier( VulkanLinearMemoryLocation o ) {
        return getPosition() < o.getPosition();
    }
    
    boolean adjacent( VulkanLinearMemoryLocation o ) {
        if ( equals( o ) ) {
            return true;
        }
        if ( isOffsetEarlier( o ) ) {
            return end() >= o.getPosition();
        }
        
        return o.end() >= getPosition();
    }
    
    VulkanLinearMemoryLocation split( long length, long memoryOffsetAlignment ) {
        if ( getTrueLength() - length < MAX_BLOCK_FREE_SPACE ) {
            setLength( length );
            return null;
        }
        var offset = length + ( ( length % memoryOffsetAlignment == 0 )
            ? 0
            : memoryOffsetAlignment - ( length % memoryOffsetAlignment )
        );
        
        var newLocation = new VulkanLinearMemoryLocation( getPosition(), length, offset );
        
        movePosition( offset );
        return newLocation;
    }
    
    private class CannotResizeException extends RuntimeException {
        
        private CannotResizeException( long length, long trueLength ) {
            super( "Cannot resize location to " + length + " when maximum length is " + trueLength );
        }
    }
}
