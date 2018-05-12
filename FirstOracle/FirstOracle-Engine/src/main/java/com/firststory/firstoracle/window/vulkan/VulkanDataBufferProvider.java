/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferProvider;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class VulkanDataBufferProvider implements BufferProvider< VulkanDataBuffer > {
    private final VulkanPhysicalDevice device;
    private final List<VulkanDataBuffer > buffers = new ArrayList<>(  );
    private final Map< Object, VulkanStagingBuffer > stagingBuffers = new HashMap<>();
    
    VulkanDataBufferProvider( VulkanPhysicalDevice device ) {
        this.device = device;
    }
    
    @Override
    public VulkanDataBuffer create() throws CannotCreateBufferException {
        throw new UnsupportedOperationException( "use dedicated method" );
    }
    
    VulkanStagableDataBuffer< float[] > createFloatBuffer() throws CannotCreateBufferException {
        return new VulkanStagableDataBuffer< float[] >( device, this ) {
    
            @Override
            int extractLength( float[] data ) {
                return data.length;
            }
    
            @Override
            int extractDataByteSize( float[] data ) {
                return 4;
            }
    
            @Override
            ByteBuffer toByteBuffer( float[] data ) {
                ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc(
                    extractLength( data ) * extractDataByteSize( data )
                );
                FloatBuffer dataBuffer = vertexDataBuffer.asFloatBuffer();
                dataBuffer.put( data );
                return vertexDataBuffer;
            }
        };
    }
    
    VulkanStagableDataBuffer< byte[] > createByteBuffer() throws CannotCreateBufferException {
        return new VulkanStagableDataBuffer< byte[] >( device, this ) {
            
            @Override
            int extractLength( byte[] data ) {
                return data.length;
            }
            
            @Override
            int extractDataByteSize( byte[] data ) {
                return 1;
            }
            
            @Override
            ByteBuffer toByteBuffer( byte[] data ) {
                ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc(
                    extractLength( data ) * extractDataByteSize( data )
                );
                vertexDataBuffer.put( data );
                return vertexDataBuffer;
            }
        };
    }
    
    VulkanUniformBuffer createUniformBuffer( int dataCount, int dataSize ) {
        VulkanUniformBuffer uniformBuffer = new VulkanUniformBuffer( device, this );
        uniformBuffer.createBuffer( dataCount, dataSize );
        return uniformBuffer;
    }
    
    <D> VulkanStagingBuffer provideStagingBuffer( D data, int dataLength, int dataByteSize, VulkanStagableDataBuffer<D> dataBuffer ) {
        return stagingBuffers.computeIfAbsent( data, dataA -> {
            VulkanStagingBuffer stagingBuffer = new VulkanStagingBuffer(
                device,
                this
            );
            stagingBuffer.createBuffer( dataLength, dataByteSize );
            stagingBuffer.load( dataBuffer.toByteBuffer( data ) );
            return stagingBuffer;
        } );
    }
    
    
    public void close() {
        stagingBuffers.values().forEach( VulkanDataBuffer::close );
        stagingBuffers.clear();
        buffers.forEach( VulkanDataBuffer::close );
        buffers.clear();
    }
    
}
