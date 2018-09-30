/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

/**
 * @author n1t4chi
 */
abstract class VulkanImage {
    
    private final VulkanAddress address;
    private final VulkanPhysicalDevice device;
    
    VulkanImage( VulkanPhysicalDevice device, VulkanAddress address ) {
        this.device = device;
        this.address = address;
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    VulkanImageView createImageView( int format, int aspectMask, int mipLevels ) {
        return new VulkanImageView( device, this, format, aspectMask, mipLevels );
    }
    
    VulkanPhysicalDevice getDevice() {
        return device;
    }
}
