/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.window.vulkan.VulkanTransferCommandPool;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public class VulkanBufferProvider {
    
    public static VulkanBufferProvider createProvider(
        VulkanPhysicalDevice device,
        VulkanTransferCommandPool dataTransferCommandPool,
        VulkanTransferCommandPool textureTransferCommandPool
    ) {
        return new VulkanBufferProvider( device, dataTransferCommandPool, textureTransferCommandPool ).init();
    }
    
    private final VulkanPhysicalDevice device;
    private final VulkanTransferCommandPool dataTransferCommandPool;
    private final VulkanTransferCommandPool textureTransferCommandPool;
    private VulkanBufferMemory textureMemory;
    private VulkanDataBufferProvider textureBufferProvider;
    private VulkanBufferMemory vertexMemory;
    private VulkanDataBufferProvider vertexBufferProvider;
    private VulkanBufferMemory uniformMemory;
    private VulkanDataBufferProvider uniformBufferProvider;
    
    private VulkanBufferProvider(
        VulkanPhysicalDevice device,
        VulkanTransferCommandPool dataTransferCommandPool,
        VulkanTransferCommandPool textureTransferCommandPool
    ) {
        this.device = device;
        this.dataTransferCommandPool = dataTransferCommandPool;
        this.textureTransferCommandPool = textureTransferCommandPool;
    }
    
    public VulkanDataBuffer createVertexBuffer( float[] array ) {
        return vertexBufferProvider.create( array );
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
    
    private VulkanBufferProvider init() {
        textureMemory = VulkanBufferMemory.createTextureMemory( device, getSuitableTextureMemoryLength(), textureTransferCommandPool );
        textureBufferProvider = new VulkanDataBufferProvider( textureMemory );
        
        vertexMemory = VulkanBufferMemory.createVertexMemory( device, getSuitableVertexDataMemoryLength(), dataTransferCommandPool );
        vertexBufferProvider = new VulkanDataBufferProvider( vertexMemory );
        
        uniformMemory = VulkanBufferMemory.createUniformMemory( device, getSuitableUniformMemoryLength(), dataTransferCommandPool );
        uniformBufferProvider = new VulkanDataBufferProvider( uniformMemory );
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
