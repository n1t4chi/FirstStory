/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;

import java.util.function.Supplier;

/**
 * @author n1t4chi
 */
public class VulkanCommandBufferAllocator< CommandBuffer extends VulkanCommandBuffer< ? > > {
    
    private final VulkanDeviceAllocator allocator;
    private final Supplier< CommandBuffer > supplier;
    
    private final VulkanImmutableObjectsRegistry< CommandBuffer > buffers = new VulkanImmutableObjectsRegistry<>( VulkanCommandBuffer::disposeUnsafe );
    
    
    VulkanCommandBufferAllocator(
        VulkanDeviceAllocator allocator,
        Supplier< CommandBuffer > supplier
    ) {
        this.allocator = allocator;
        this.supplier = supplier;
    }
    
    public void dispose() {
        allocator.deregisterBufferAllocator( this );
    }
    
    public CommandBuffer createBuffer() {
        return buffers.register( supplier );
    }
    
    public void deregisterBuffer( CommandBuffer image ) {
        buffers.deregister( image );
    }
    
    void disposeUnsafe() {
        buffers.dispose();
    }
}
