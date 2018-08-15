/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

/**
 * @author n1t4chi
 */
class TestableDataBufferInLinearMemory extends DataBufferInLinearMemory< char[] > {
    
    private final TestableLinearMemoryController controller;
    
    TestableDataBufferInLinearMemory( TestableLinearMemoryController controller, LinearMemoryLocation memoryLocation ) {
        super( controller, memoryLocation );
        this.controller = controller;
    }
    
    char[] readBuffer( ) {
        assertCreated();
        assertLoaded();
        return controller.read( getLocation() );
    }
    
    @Override
    public boolean isLoaded() {
        return true;
    }
    
//    @Override
//    public boolean isCreated() {
//        return true;
//    }
}
