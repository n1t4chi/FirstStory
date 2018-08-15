/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
class TestableLinearMemoryController extends LinearMemoryController< char[]> {
    
    TestableLinearMemoryController( TestableLinearMemory memory ) {
        super( memory );
    }
    
    char[] read( LinearMemoryLocation location ) {
        return ((TestableLinearMemory)getMemory()).read( location );
    }
    
    @Override
    TestableDataBufferInLinearMemory createBuffer( int length ) {
        DataBufferInLinearMemory< char[] > buffer = super.createBuffer( length );
        return new TestableDataBufferInLinearMemory( this, buffer.provideLocation() );
    }
}
