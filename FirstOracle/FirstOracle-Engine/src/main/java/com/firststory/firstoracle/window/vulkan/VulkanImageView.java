/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.vulkan.VK10;

/**
 * @author n1t4chi
 */
class VulkanImageView {
    
    private final VulkanPhysicalDevice device;
    private final VulkanSwapChain swapChain;
    private final VulkanAddress address;
    private final int index;
    
    VulkanImageView( VulkanPhysicalDevice device, VulkanSwapChain swapChain, VulkanAddress address, int index ) {
        this.device = device;
        this.swapChain = swapChain;
        this.address = address;
        this.index = index;
    }
    VulkanImageView( VulkanPhysicalDevice device, VulkanSwapChain swapChain, long address, int index ) {
        this( device, swapChain, new VulkanAddress( address ), index );
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        VulkanImageView that = ( VulkanImageView ) o;
        
        if ( index != that.index ) { return false; }
        if ( device != null ? !device.equals( that.device ) : that.device != null ) { return false; }
        if ( swapChain != null ? !swapChain.equals( that.swapChain ) : that.swapChain != null ) { return false; }
        return address != null ? address.equals( that.address ) : that.address == null;
    }
    
    @Override
    public int hashCode() {
        int result = device != null ? device.hashCode() : 0;
        result = 31 * result + ( swapChain != null ? swapChain.hashCode() : 0 );
        result = 31 * result + ( address != null ? address.hashCode() : 0 );
        result = 31 * result + index;
        return result;
    }
    
    void close() {
        VK10.vkDestroyImageView( device.getLogicalDevice(), address.getValue(), null );
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    public int getIndex() {
        return index;
    }
}
