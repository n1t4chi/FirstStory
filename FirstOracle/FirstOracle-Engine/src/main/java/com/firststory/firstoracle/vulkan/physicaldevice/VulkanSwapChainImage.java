/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;

/**
 * @author n1t4chi
 */
public class VulkanSwapChainImage extends VulkanImage {
    
    private int index = -1;
    
    public VulkanSwapChainImage(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        super( allocator, device );
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
    
    public VulkanSwapChainImage update( VulkanAddress address, int index ) {
        super.updateAddress( address );
        this.index = index;
        return this;
    }
    
    void dispose() {
        getAllocator().deregisterSwapChainImage( this );
        
    }
    
    public void disposeUnsafe() {
//        VK10.vkDestroyImage( getDevice().getLogicalDevice(), getAddress().getValue(), null );
        getAddress().setNull();
        index = -1;
    }
    
    int getIndex() {
        return index;
    }
}
