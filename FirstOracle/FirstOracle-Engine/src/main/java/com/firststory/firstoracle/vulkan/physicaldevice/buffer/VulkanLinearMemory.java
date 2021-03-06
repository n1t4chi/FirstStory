/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

/**
 * @author n1t4chi
 */
public abstract class VulkanLinearMemory< Data > {
    
    public abstract int length();
    
    public void write( VulkanLinearMemoryLocation location, Data data ) {
        assertWriteLength( location, data );
        writeUnsafe( location, data );
    }
    
    protected abstract void writeUnsafe( VulkanLinearMemoryLocation location, Data data );
    
    protected abstract int getDataLength( Data data );
    
    private void assertWriteLength( VulkanLinearMemoryLocation location, Data data ) {
        int dataLength = getDataLength( data );
        if ( location.getLength() < dataLength ) {
            throw new WriteMemoryOutOfBoundException( location, dataLength );
        }
    }
    
    static class WriteMemoryOutOfBoundException extends RuntimeException {
        
        private WriteMemoryOutOfBoundException( VulkanLinearMemoryLocation location, int length ) {
            super( "Provided data length: " + length + ", Maximum buffer length: " + location.getLength() );
        }
    }
}
