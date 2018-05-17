/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
abstract class VulkanImage {
    
    private final VulkanAddress address;
    private VulkanPhysicalDevice device;
    
    VulkanImage( VulkanPhysicalDevice device, VulkanAddress address ) {
        this.device = device;
        this.address = address;
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    VulkanImageView createImageView( int format, int aspectMask ) {
        return new VulkanImageView( device, this, format, aspectMask );
    }
    
    VulkanPhysicalDevice getDevice() {
        return device;
    }
}
