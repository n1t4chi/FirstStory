/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

/**
 * @author n1t4chi
 */
class VulkanSwapChainImage extends VulkanImage {
    
    private final int index;
    
    VulkanSwapChainImage( VulkanPhysicalDevice device, VulkanAddress address, int index ) {
        super( device, address );
        this.index = index;
    }
    
    @Override
    public int hashCode() {
        var result = getAddress() != null ? getAddress().hashCode() : 0;
        result = 31 * result + index;
        return result;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
    
        var that = ( VulkanSwapChainImage ) o;
        
        if ( index != that.index ) { return false; }
        return getAddress() != null ? getAddress().equals( that.getAddress() ) : that.getAddress() == null;
    }
    
    int getIndex() {
        return index;
    }
}
