/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
public class Memory {
    
    private final char[] data;
    
    public Memory( int length ) {
        data = new char[ length ];
    }
    
    public int length() {
        return data.length;
    }
    
    public void write( MemoryLocation location, char[] data ) {
        assertWriteLength( location, data );
        write( location.getPosition(), data );
    }
    
    public char[] read( MemoryLocation location ) {
        asserReadLength( location );
        return read( location.getPosition(), location.getLength() );
    }
    
    private void write( int position, char[] data ) {
        System.arraycopy( data, 0, this.data, position, data.length );
    }
    
    private char[] read( int position, int length ) {
        char[] data = new char[ length ];
        System.arraycopy( this.data, position, data, 0, length );
        return data;
    }
    
    private void assertWriteLength( MemoryLocation location, char[] data ) {
        if ( location.getLength() < data.length ) {
            throw new WriteMemoryOutOfBoundException( location, data.length );
        }
    }
    
    private void asserReadLength( MemoryLocation location ) {
        if ( location.getLength() + location.getPosition() > data.length ) {
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
