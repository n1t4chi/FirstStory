/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
public class DataBuffer {
    
    private final Memory block;
    private final MemoryLocation location;
    
    public DataBuffer( Memory memory, MemoryLocation memoryLocation ) {
        this.block = memory;
        this.location = memoryLocation;
    }
    
    public int length() {
        return location.getLength();
    }
    
    public char[] read() {
        return block.read( location );
    }
    
    public void write( char[] data ) {
        block.write( location, data );
    }
    
}
