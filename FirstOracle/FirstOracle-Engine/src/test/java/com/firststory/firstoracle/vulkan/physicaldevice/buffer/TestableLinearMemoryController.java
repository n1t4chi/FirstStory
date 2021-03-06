/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

/**
 * @author n1t4chi
 */
class TestableLinearMemoryController extends VulkanLinearMemoryController< TestableLinearMemory, char[]> {
    
    TestableLinearMemoryController( TestableLinearMemory memory ) {
        super( memory, 1 );
    }
    
    char[] read( VulkanLinearMemoryLocation location ) {
        return getMemory().read( location );
    }
    
    TestableDataBufferInLinearMemory createBuffer( int length ) {
        return new TestableDataBufferInLinearMemory( this, super.allocate( length ) );
    }
}
