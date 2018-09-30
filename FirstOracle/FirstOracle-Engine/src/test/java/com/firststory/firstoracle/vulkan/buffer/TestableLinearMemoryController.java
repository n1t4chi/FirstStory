/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.buffer;

/**
 * @author n1t4chi
 */
class TestableLinearMemoryController extends LinearMemoryController< TestableLinearMemory, char[]> {
    
    TestableLinearMemoryController( TestableLinearMemory memory ) {
        super( memory );
    }
    
    char[] read( LinearMemoryLocation location ) {
        return getMemory().read( location );
    }
    
    TestableDataBufferInLinearMemory createBuffer( int length ) {
        return new TestableDataBufferInLinearMemory( this, super.allocate( length ) );
    }
}
