/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
public abstract class Memory {
    
    public abstract int length();
    
    protected abstract void writeUnsafe( MemoryLocation location, char[] data );
    
    protected abstract char[] readUnsafe( MemoryLocation location );
    
    public void write( MemoryLocation location, char[] data ) {
        assertWriteLength( location, data );
        writeUnsafe( location, data );
    }
    
    public char[] read( MemoryLocation location ) {
        asserReadLength( location );
        return readUnsafe( location );
    }
    
    private void assertWriteLength( MemoryLocation location, char[] data ) {
        if ( location.getLength() < data.length ) {
            throw new WriteMemoryOutOfBoundException( location, data.length );
        }
    }
    
    private void asserReadLength( MemoryLocation location ) {
        if ( location.getLength() + location.getPosition() > length() ) {
            throw new ReadMemoryOutOfBoundException( location );
        }
    }
    
    class ReadMemoryOutOfBoundException extends RuntimeException {
        private ReadMemoryOutOfBoundException( MemoryLocation location ) {
            super( "Cannot read memory block: " + location + " " );
        }
    }
    class WriteMemoryOutOfBoundException extends RuntimeException {
        
        private WriteMemoryOutOfBoundException( MemoryLocation location, int length ) {
            super( "Provided data length: " + length + ", Maximum buffer length: " + location.getLength() );
        }
    }
}
