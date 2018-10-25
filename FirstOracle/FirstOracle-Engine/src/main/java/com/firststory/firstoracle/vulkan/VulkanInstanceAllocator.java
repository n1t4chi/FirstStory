/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

/**
 * @author n1t4chi
 */
public class VulkanInstanceAllocator {
    
    private final VulkanFrameworkAllocator allocator;
    
    VulkanInstanceAllocator( VulkanFrameworkAllocator allocator ) {
        this.allocator = allocator;
    }
    
    void disposeUnsafe() {
    
    }
    
    void dispose() {
        allocator.deregisterPhysicalDeviceAllocator( this );
    }
}
