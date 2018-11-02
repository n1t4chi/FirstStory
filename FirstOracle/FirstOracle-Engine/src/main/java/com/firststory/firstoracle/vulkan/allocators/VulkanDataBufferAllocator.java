/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import com.firststory.firstoracle.buffer.DataBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferMemory;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBufferProvider;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanLinearMemoryLocation;

/**
 * @author n1t4chi
 */
public class VulkanDataBufferAllocator {
    
    private final VulkanDeviceAllocator allocator;
    
    private final VulkanReusableObjectsRegistry< VulkanDataBuffer > buffers = new VulkanReusableObjectsRegistry<>( buffer -> {}, DataBuffer::delete, DataBuffer::delete );
    
    public VulkanDataBufferAllocator( VulkanDeviceAllocator allocator ) {
        this.allocator = allocator;
    }
    
    public VulkanDataBuffer createBuffer(
        VulkanDataBufferProvider controller,
        VulkanBufferMemory deviceMemory,
        VulkanLinearMemoryLocation deviceMemoryLocation
    ) {
        return buffers.register(
            () -> new VulkanDataBuffer( this, controller, deviceMemory, deviceMemoryLocation ),
            buffer -> {}
        );
    }
    
    public void deregisterBuffer( VulkanDataBuffer buffer ) {
        buffers.deregister( buffer );
    }
    
    public void dispose() {
        allocator.deregisterDataBufferAllocator( this );
    }
    
    void disposeUnsafe() {
        buffers.dispose();
    }
}
