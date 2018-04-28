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
    private final long address;
    private final int index;
    
    VulkanImageView( VulkanPhysicalDevice device, VulkanSwapChain swapChain, long address, int index ) {
        this.device = device;
        this.swapChain = swapChain;
        this.address = address;
        this.index = index;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
    
        VulkanImageView that = ( VulkanImageView ) o;
        
        return address == that.getAddress();
    }
    
    @Override
    public int hashCode() {
        return ( int ) ( address ^ ( address >>> 32 ) );
    }
    
    void close() {
        VK10.vkDestroyImageView( device.getLogicalDevice(), address, null );
    }
    
    long getAddress() {
        return address;
    }
    
    public int getIndex() {
        return index;
    }
}
