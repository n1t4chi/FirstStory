/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.BufferNotLoadedException;
import org.lwjgl.vulkan.VK10;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public abstract class VulkanStagableDataBuffer< Data > extends VulkanDataBuffer< Data > {
    
    private static final int[] LOCAL_BUFFER_USAGE_FLAGS = {
        VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT, VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
    };
    private static final int[] LOCAL_BUFFER_MEMORY_FLAGS = { VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT };
    private VulkanStagingBuffer stagingBuffer;
    private VulkanDataBufferProvider provider;
    
    VulkanStagableDataBuffer( VulkanPhysicalDevice device, VulkanDataBufferProvider provider ) {
        super( device, LOCAL_BUFFER_USAGE_FLAGS, LOCAL_BUFFER_MEMORY_FLAGS );
        this.provider = provider;
    }
    
    @Override
    public boolean isLoaded() {
        return super.isLoaded() && stagingBuffer != null && stagingBuffer.isLoaded();
    }
    
    @Override
    public void load( Data data ) throws BufferNotCreatedException {
        stagingBuffer = extractStagingBufferFromLoader( data );
        createBuffer( extractLength( data ), extractDataByteSize( data ) );
    }
    
    VulkanDataBuffer getStagingBuffer() {
        return stagingBuffer;
    }
    
    @Override
    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
        stagingBuffer.copyBuffer( this, getDevice().getTransferCommandPool() );
    }
    
    private VulkanStagingBuffer extractStagingBufferFromLoader( Data data ) {
        return provider.provideStagingBuffer( data, extractLength( data ), extractDataByteSize( data ), this );
    }
    
    abstract int extractLength( Data data );
    
    abstract int extractDataByteSize( Data data );
    
    abstract ByteBuffer toByteBuffer( Data data );
}
