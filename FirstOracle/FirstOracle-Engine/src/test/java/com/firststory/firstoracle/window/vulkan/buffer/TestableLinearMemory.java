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
        System.arraycopy( data, 0, this.data, ( int ) location.getPosition(), data.length );
    }
    
    protected char[] readUnsafe( LinearMemoryLocation location ) {
        var data = new char[ ( int ) location.getLength() ];
        System.arraycopy( this.data, ( int ) location.getPosition(), data, 0, ( int ) location.getLength() );
        return data;
    }
    
    @Override
    protected int getDataLength( char[] array ) {
        return array.length;
    }
    
    public char[] read( LinearMemoryLocation location ) {
        assertReadLength( location );
        return readUnsafe( location );
    }
    
    private void assertReadLength( LinearMemoryLocation location ) {
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
