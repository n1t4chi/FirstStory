/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;

/**
 * @author n1t4chi
 */
public abstract class VulkanImage {
    
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    private VulkanAddress address;
    
    VulkanImage(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        this.allocator = allocator;
        this.device = device;
    }
    
    protected void updateAddress( VulkanAddress address ) {
        this.address = address;
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    protected VulkanDeviceAllocator getAllocator() {
        return allocator;
    }
    
    VulkanImageView createImageView( int format, int aspectMask, int mipLevels ) {
        return allocator.createImageView( this, format, aspectMask, mipLevels );
    }
    
    VulkanPhysicalDevice getDevice() {
        return device;
    }
}
