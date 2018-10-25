/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanBufferProvider {
    
    private final VulkanDataBufferProvider textureBufferProvider;
    private final VulkanDataBufferProvider vertexBufferProvider;
    private final VulkanDataBufferProvider vertexQuickBufferProvider;
    private final VulkanDataBufferProvider uniformBufferProvider;
    private final List< VulkanBufferMemory > memories = new ArrayList<>(  );
    private final VulkanDeviceAllocator allocator;
    
    public VulkanBufferProvider(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanTransferCommandPool vertexDataTransferCommandPool,
        VulkanTransferCommandPool quickDataTransferCommandPool,
        VulkanTransferCommandPool uniformDataTransferCommandPool,
        VulkanTransferCommandPool textureTransferCommandPool,
        long uniformBufferOffsetAlignment
    ) {
        this.allocator = allocator;
        var textureMemory = VulkanBufferMemory.createTextureMemory( device, getSuitableTextureMemoryLength(), textureTransferCommandPool );
        textureBufferProvider = new VulkanDataBufferProvider( textureMemory, 1 );
    
        var vertexMemory = VulkanBufferMemory.createVertexMemory( device, getSuitableVertexDataMemoryLength(), vertexDataTransferCommandPool );
        vertexBufferProvider = new VulkanDataBufferProvider( vertexMemory, 1 );
    
        var vertexQuickMemory = VulkanBufferMemory.createVertexQuickMemory( device, getSuitableQuickDataMemoryLength(), quickDataTransferCommandPool );
        vertexQuickBufferProvider = new VulkanDataBufferProvider( vertexQuickMemory, 1 );
    
        var uniformMemory = VulkanBufferMemory.createUniformMemory( device, getSuitableUniformMemoryLength(), uniformDataTransferCommandPool );
        uniformBufferProvider = new VulkanDataBufferProvider( uniformMemory, uniformBufferOffsetAlignment );
    
        memories.add( textureMemory );
        memories.add( vertexMemory );
        memories.add( vertexQuickMemory );
        memories.add( uniformMemory );
    }
    
    public VulkanDataBuffer createVertexBuffer( float[] array ) {
        return vertexBufferProvider.create( array );
    }
    
    public VulkanDataBuffer createQuickVertexBuffer( float[] array ) {
        return vertexQuickBufferProvider.create( array );
    }
    
    public VulkanDataBuffer createUniformBuffer( float[] uniformBufferData ) {
        return uniformBufferProvider.create( uniformBufferData );
    }
    
    public void dispose() {
        allocator.deregisterBufferProvider( this );
    }
    
    public void disposeUnsafe() {
        memories.forEach( VulkanBufferMemory::dispose );
    }
    
    public VulkanDataBuffer createTextureBuffer( ByteBuffer pixels ) {
        return textureBufferProvider.create( pixels );
    }
    
    private int getSuitableTextureMemoryLength() {
        return 256 * 1024 * 1024;
    }
    
    private int getSuitableVertexDataMemoryLength() {
        return 64 * 1024 * 1024;
    }
    
    private int getSuitableQuickDataMemoryLength() {
        return 256 * 1024 * 1024;
    }
    
    private int getSuitableUniformMemoryLength() {
        return 16 * 1024 * 1024;
    }
}
