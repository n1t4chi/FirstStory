/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
class VulkanImage {
    
    private final VulkanAddress address;
    private final int index;
    
    VulkanImage( VulkanAddress address, int index ) {
        this.address = address;
        this.index = index;
    }
    
    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        VulkanImage that = ( VulkanImage ) o;
        
        if ( index != that.index ) { return false; }
        return address != null ? address.equals( that.address ) : that.address == null;
    }
    
    public VulkanAddress getAddress() {
        return address;
    }
    
    int getIndex() {
        return index;
    }
}
