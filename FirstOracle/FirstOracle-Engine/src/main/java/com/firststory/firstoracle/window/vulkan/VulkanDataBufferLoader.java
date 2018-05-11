/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferProvider;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import org.lwjgl.vulkan.VK10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class VulkanDataBufferLoader implements BufferProvider< VulkanDataBuffer > {
    
    private static final int[] LOCAL_BUFFER_USAGE_FLAGS = { VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT,
        VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
    };
    private static final int[] LOCAL_BUFFER_MEMORY_FLAGS = { VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT };
    
    private static final int[] STAGING_BUFFER_USAGE_FLAGS = { VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT };
    private static final int[] STAGING_BUFFER_MEMORY_FLAGS = { VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT,
        VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    };
    
    private static final int[] UNIFORM_BUFFER_USAGE_FLAGS = { VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT };
    private static final int[] UNIFORM_BUFFER_MEMORY_FLAGS = { VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT,
        VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    };
    
    private final VulkanPhysicalDevice device;
    private final List<VulkanDataBuffer > buffers = new ArrayList<>(  );
    private final Map< Object, VulkanDataBuffer > stagingBuffers = new HashMap<>();
    
    VulkanDataBufferLoader( VulkanPhysicalDevice device ) {
        
        this.device = device;
    }
    
    @Override
    public VulkanDataBuffer create() throws CannotCreateBufferException {
        return new VulkanDataBuffer( device, this, LOCAL_BUFFER_USAGE_FLAGS, LOCAL_BUFFER_MEMORY_FLAGS );
    }
    
    VulkanDataBuffer createUniformBuffer( int dataCount, int dataSize ) {
        VulkanDataBuffer uniformBuffer = new VulkanDataBuffer(
            device,
            this,
            UNIFORM_BUFFER_USAGE_FLAGS,
            UNIFORM_BUFFER_MEMORY_FLAGS
        );
        uniformBuffer.createBuffer( dataCount, dataSize );
        return uniformBuffer;
    }
    
    public void bind( VulkanDataBuffer buffer ) {
        VulkanDataBuffer stagingBuffer = buffer.getStagingBuffer();
        stagingBuffer.copyBuffer( buffer, device.getTransferCommandPool() );
    }
    
    void load( VulkanDataBuffer buffer, byte[] bufferData ) {
        buffer.setStagingBuffer( stagingBuffers.computeIfAbsent( bufferData, data -> {
            VulkanDataBuffer stagingBuffer = new VulkanDataBuffer(
                device,
                this,
                STAGING_BUFFER_USAGE_FLAGS,
                STAGING_BUFFER_MEMORY_FLAGS
            );
            stagingBuffer.createBuffer( bufferData.length, 1 );
            stagingBuffer.mapMemory( bufferData );
            stagingBuffer.setStagingBuffer( stagingBuffer );
            return stagingBuffer;
        } ) );
        buffer.createBuffer( bufferData.length, 1 );
    }
    
    void load( VulkanDataBuffer buffer, float[] bufferData ) {
        buffer.setStagingBuffer( stagingBuffers.computeIfAbsent( bufferData, data -> {
            VulkanDataBuffer stagingBuffer = new VulkanDataBuffer(
                device,
                this,
                STAGING_BUFFER_USAGE_FLAGS,
                STAGING_BUFFER_MEMORY_FLAGS
            );
            stagingBuffer.createBuffer( bufferData.length, 4 );
            stagingBuffer.mapMemory( bufferData );
            stagingBuffer.setStagingBuffer( stagingBuffer );
            return stagingBuffer;
        } ) );
        buffer.createBuffer( bufferData.length, 4 );
    }
    
    public void delete( VulkanDataBuffer buffer ) {
        buffer.close();
    }
    
    public void close() {
        stagingBuffers.values().forEach( VulkanDataBuffer::close );
        stagingBuffers.clear();
        buffers.forEach( VulkanDataBuffer::close );
        buffers.clear();
    }
    
}
