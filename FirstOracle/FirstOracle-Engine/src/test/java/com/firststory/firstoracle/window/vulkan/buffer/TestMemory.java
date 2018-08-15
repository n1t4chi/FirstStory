/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
class TestMemory extends Memory {
    
    private final char[] data;
    
    TestMemory( int length ) {
        data = new char[ length ];
    }
    
    @Override
    public int length() {
        return data.length;
    }
    
    protected void writeUnsafe( MemoryLocation location, char[] data ) {
        System.arraycopy( data, 0, this.data, location.getPosition(), data.length );
    }
    
    protected char[] readUnsafe( MemoryLocation location ) {
        char[] data = new char[ location.getLength() ];
        System.arraycopy( this.data, location.getPosition(), data, 0, location.getLength() );
        return data;
    }
}
