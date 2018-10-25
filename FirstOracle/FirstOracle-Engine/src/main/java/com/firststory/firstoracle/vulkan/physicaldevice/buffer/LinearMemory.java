/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

/**
 * @author n1t4chi
 */
public abstract class LinearMemory< Data > {
    
    public abstract int length();
    
    public void write( LinearMemoryLocation location, Data data ) {
        assertWriteLength( location, data );
        writeUnsafe( location, data );
    }
    
    protected abstract void writeUnsafe( LinearMemoryLocation location, Data data );
    
    protected abstract int getDataLength( Data data );
    
    private void assertWriteLength( LinearMemoryLocation location, Data data ) {
        if ( location.getLength() < getDataLength( data ) ) {
            throw new WriteMemoryOutOfBoundException( location, getDataLength( data ) );
        }
    }
    
    static class WriteMemoryOutOfBoundException extends RuntimeException {
        
        private WriteMemoryOutOfBoundException( LinearMemoryLocation location, int length ) {
            super( "Provided data length: " + length + ", Maximum buffer length: " + location.getLength() );
        }
    }
}
