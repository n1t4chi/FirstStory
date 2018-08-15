/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
class TestableLinearMemory extends LinearMemory< char[] > {
    
    private final char[] data;
    
    TestableLinearMemory( int length ) {
        data = new char[ length ];
    }
    
    @Override
    public int length() {
        return data.length;
    }
    
    protected void writeUnsafe( LinearMemoryLocation location, char[] data ) {
        System.arraycopy( data, 0, this.data, location.getPosition(), data.length );
    }
    
    protected char[] readUnsafe( LinearMemoryLocation location ) {
        char[] data = new char[ location.getLength() ];
        System.arraycopy( this.data, location.getPosition(), data, 0, location.getLength() );
        return data;
    }
    
    @Override
    protected int getDataLength( char[] array ) {
        return array.length;
    }
    
    public char[] read( LinearMemoryLocation location ) {
        asserReadLength( location );
        return readUnsafe( location );
    }
    
    private void asserReadLength( LinearMemoryLocation location ) {
        if ( location.getLength() + location.getPosition() > length() ) {
            throw new ReadMemoryOutOfBoundException( location );
        }
    }
    
    static class ReadMemoryOutOfBoundException extends RuntimeException {
        
        private ReadMemoryOutOfBoundException( LinearMemoryLocation location ) {
            super( "Cannot read memory block: " + location + " " );
        }
    }
}
