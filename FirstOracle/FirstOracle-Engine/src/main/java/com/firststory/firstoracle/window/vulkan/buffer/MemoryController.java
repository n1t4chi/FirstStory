/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
public class MemoryController {
    
    private final Memory memory;
    private int firstFreeOffset = 0;
    
    public MemoryController( int length ) {
        memory = new Memory( length );
    }
    
    Memory getMemory() {
        return memory;
    }
    
    public DataBuffer createBuffer( int length ) {
        int position = provideNewOffset( length );
        int backup = firstFreeOffset;
        try {
            MemoryLocation memoryLocation = new MemoryLocation( firstFreeOffset, length );
            firstFreeOffset = position;
            return new DataBuffer( memory, memoryLocation );
        } catch ( Exception ex ) {
            firstFreeOffset = backup;
            throw ex;
        }
    }
    
    private int provideNewOffset( int length ) {
        int newOffset = firstFreeOffset + length;
        if ( memory.length() < newOffset ) {
            throw new OutOfMemoryException( memory.length() - firstFreeOffset, length );
        }
        return newOffset;
    }
    
    class OutOfMemoryException extends RuntimeException {
        
        private OutOfMemoryException( int freeMemory, int length ) {
            super( "Cannot create block of size " + length + ". Free memory left: " + freeMemory );
        }
    }
}
