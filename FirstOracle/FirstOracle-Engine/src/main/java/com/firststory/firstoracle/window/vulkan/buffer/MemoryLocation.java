/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
public class MemoryLocation {
    private final int position;
    private final int length;
    
    public MemoryLocation( int position, int length ) {
        this.position = position;
        this.length = length;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int getLength() {
        return length;
    }
    
    @Override
    public String toString() {
        return "MemoryLocation@"+hashCode()+"{" + "position=" + position + ", length=" + length + '}';
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
    public int hashCode() {
        int result = position;
        result = 31 * result + length;
        return result;
    }
}
