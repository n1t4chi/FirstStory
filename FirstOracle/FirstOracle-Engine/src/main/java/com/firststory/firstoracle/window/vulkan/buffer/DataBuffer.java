/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
public class DataBuffer {
    
    private final MemoryLocation location;
    private final MemoryController controller;
    private boolean usable = true;
    
    public DataBuffer( MemoryController controller, MemoryLocation memoryLocation ) {
        this.controller = controller;
        this.location = memoryLocation;
    }
    
    public int length() {
        assertUsable();
        return location.getLength();
    }
    
    public char[] read() {
        assertUsable();
        return controller.read( location );
    }
    
    public void write( char[] data ) {
        assertUsable();
        controller.write( location, data );
    }
    
    public void free() {
        assertUsable();
        usable = false;
        controller.free( location );
    }
    
    private void assertUsable() {
        if ( usable ) {
            return;
        }
        throw new BufferClosedException();
    }
    
    class BufferClosedException extends RuntimeException {
        
        private BufferClosedException() {
            super( "Buffer is already closed." );
        }
    }
}
