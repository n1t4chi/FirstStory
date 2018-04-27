/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
class VulkanImage {
    
    private final long address;
    private final int index;
    
    VulkanImage( long address, int index ) {
        this.address = address;
        this.index = index;
    }
    
    @Override
    public int hashCode() {
        return ( int ) ( address ^ ( address >>> 32 ) );
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        VulkanImage that = ( VulkanImage ) o;
        
        return address == that.address;
    }
    
    long getAddress() {
        return address;
    }
    
    int getIndex() {
        return index;
    }
}
