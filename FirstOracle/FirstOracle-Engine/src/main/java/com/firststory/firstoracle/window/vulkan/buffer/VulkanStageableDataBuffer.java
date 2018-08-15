/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.BufferNotLoadedException;
import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import org.lwjgl.vulkan.VK10;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public abstract class VulkanStageableDataBuffer< Data > extends VulkanDataBuffer< Data > {
    
    private static final int[] LOCAL_BUFFER_USAGE_FLAGS = {
        VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT, VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
    };
    private static final int[] LOCAL_BUFFER_MEMORY_FLAGS = { VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT };
    private final VulkanDataBufferProvider provider;
    private VulkanStagingBuffer stagingBuffer;
    
    VulkanStageableDataBuffer( VulkanPhysicalDevice device, VulkanDataBufferProvider provider ) {
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
        stagingBuffer.copyBuffer( this, getDevice().getVertexDataTransferCommandPool() );
    }
    
    public void copy( Data data ) {
        if( stagingBuffer == null ) {
            load( data );
        } else {
            stagingBuffer.load( toByteBuffer( data ) );
        }
    }
    
    @Override
    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
    
    }
    
    private VulkanStagingBuffer extractStagingBufferFromLoader( Data data ) {
        return provider.provideUniqueStagingBuffer( data, extractLength( data ), extractDataByteSize( data ), this );
    }
    
    @Override
    public void close() {
        super.close();
        if( stagingBuffer != null ) {
            stagingBuffer.close();
            stagingBuffer = null;
        }
    }
    
    abstract int extractLength( Data data );
    
    abstract int extractDataByteSize( Data data );
    
    abstract ByteBuffer toByteBuffer( Data data );
}
