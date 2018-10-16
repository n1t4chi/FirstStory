/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.buffer;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.transfer.VulkanTransferCommandPool;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanBufferProvider {
    
    public static VulkanBufferProvider createProvider(
        VulkanPhysicalDevice device,
        VulkanTransferCommandPool dataTransferCommandPool,
        VulkanTransferCommandPool textureTransferCommandPool,
        long uniformBufferOffsetAlignment
    ) {
        return new VulkanBufferProvider( device, dataTransferCommandPool, textureTransferCommandPool ).init( uniformBufferOffsetAlignment );
    }
    
    private final VulkanPhysicalDevice device;
    private final VulkanTransferCommandPool dataTransferCommandPool;
    private final VulkanTransferCommandPool textureTransferCommandPool;
    private VulkanBufferMemory textureMemory;
    private VulkanDataBufferProvider textureBufferProvider;
    private VulkanBufferMemory vertexMemory;
    private VulkanDataBufferProvider vertexBufferProvider;
    private VulkanBufferMemory vertexQuickMemory;
    private VulkanDataBufferProvider vertexQuickBufferProvider;
    private VulkanBufferMemory uniformMemory;
    private VulkanDataBufferProvider uniformBufferProvider;
    private final List< VulkanAddress > memoryAddresses = new ArrayList<>();
    
    private VulkanBufferProvider(
        VulkanPhysicalDevice device,
        VulkanTransferCommandPool dataTransferCommandPool,
        VulkanTransferCommandPool textureTransferCommandPool
    ) {
        this.device = device;
        this.dataTransferCommandPool = dataTransferCommandPool;
        this.textureTransferCommandPool = textureTransferCommandPool;
    }
    
    public List< VulkanAddress  > getMemoryAddresses() {
        return memoryAddresses;
    }
    
    public VulkanDataBuffer createVertexBuffer( float[] array ) {
        return vertexBufferProvider.create2( array );
    }
    
    public VulkanDataBuffer createQuickVertexBuffer( float[] array ) {
        return vertexQuickBufferProvider.create2( array );
    }
    
    public VulkanDataBuffer createUniformBuffer( float[] uniformBufferData ) {
        return uniformBufferProvider.create( uniformBufferData );
    }
    
    public void dispose() {
        if ( textureMemory != null ) {
            textureMemory.close();
            textureMemory = null;
        }
        
        if ( vertexMemory != null ) {
            vertexMemory.close();
            vertexMemory = null;
        }
        
        if ( uniformMemory != null ) {
            uniformMemory.close();
            uniformMemory = null;
        }
    }
    
    public VulkanDataBuffer createTextureBuffer( ByteBuffer pixels ) {
        return textureBufferProvider.create( pixels );
    }
    
    private VulkanBufferProvider init( long uniformBufferOffsetAlignment ) {
        textureMemory = VulkanBufferMemory.createTextureMemory( device, getSuitableTextureMemoryLength(), textureTransferCommandPool );
        textureBufferProvider = new VulkanDataBufferProvider( textureMemory, 1 );
        
        vertexMemory = VulkanBufferMemory.createVertexMemory( device, getSuitableVertexDataMemoryLength(), dataTransferCommandPool );
        vertexBufferProvider = new VulkanDataBufferProvider( vertexMemory, 1 );
    
        vertexQuickMemory = VulkanBufferMemory.createVertexQuickMemory( device, getSuitableVertexDataMemoryLength(), dataTransferCommandPool );
        vertexQuickBufferProvider = new VulkanDataBufferProvider( vertexQuickMemory, 1 );
        
        uniformMemory = VulkanBufferMemory.createUniformMemory( device, getSuitableUniformMemoryLength(), dataTransferCommandPool );
        uniformBufferProvider = new VulkanDataBufferProvider( uniformMemory, uniformBufferOffsetAlignment );
        
        memoryAddresses.clear();
        memoryAddresses.add( textureMemory.getAddress() );
        memoryAddresses.add( vertexMemory.getAddress() );
        memoryAddresses.add( uniformMemory.getAddress() );
        return this;
    }
    
    private int getSuitableTextureMemoryLength() {
        return 256 * 1024 * 1024;
    }
    
    private int getSuitableVertexDataMemoryLength() {
        return 64 * 1024 * 1024;
    }
    
    private int getSuitableUniformMemoryLength() {
        return 16 * 1024 * 1024;
    }
}
