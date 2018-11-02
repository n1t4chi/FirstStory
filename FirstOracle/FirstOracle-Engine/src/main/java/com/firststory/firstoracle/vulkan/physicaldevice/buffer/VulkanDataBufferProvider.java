/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

import com.firststory.firstoracle.buffer.BufferProvider;
import com.firststory.firstoracle.buffer.CannotCreateBufferException;
import com.firststory.firstoracle.vulkan.allocators.VulkanDataBufferAllocator;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author n1t4chi
 */
public class VulkanDataBufferProvider
    extends VulkanLinearMemoryController< VulkanBufferMemory, ByteBuffer >
    implements BufferProvider< VulkanDataBuffer, ByteBuffer >
{
    private final VulkanDataBufferAllocator allocator;
    
    VulkanDataBufferProvider(
        VulkanDeviceAllocator allocator,
        VulkanBufferMemory memory,
        long memoryOffsetAlignment
    ) {
        super( memory, memoryOffsetAlignment );
        this.allocator = allocator.createDataBufferAllocator();
    }
    
    @Override
    public VulkanDataBuffer create( ByteBuffer data ) throws CannotCreateBufferException {
        var buffer = allocator.createBuffer( this, getMemory(), allocate( data.remaining() ) );
        buffer.create();
        buffer.load( data );
        return buffer;
    }
    
    public VulkanDataBuffer create( float[] data ) throws CannotCreateBufferException {
        var buffer = allocator.createBuffer( this, getMemory(), allocate( data.length*4 ) );
        buffer.create();
        buffer.load( data );
        return buffer;
    }
    
    public VulkanDataBuffer create( FloatBuffer data ) throws CannotCreateBufferException {
        return create( MemoryUtil.memByteBuffer( MemoryUtil.memAddress( data ), data.remaining()*4 ) );
    }
    
    public VulkanDataBuffer create( IntBuffer data ) throws CannotCreateBufferException {
        return create( MemoryUtil.memByteBuffer( MemoryUtil.memAddress( data ), data.remaining()*4 ) );
    }
    
    public VulkanDataBuffer create( byte[] data ) throws CannotCreateBufferException {
        var buffer = allocator.createBuffer( this, getMemory(), allocate( data.length ) );
        buffer.create();
        buffer.load( data );
        return buffer;
    }
    
    public VulkanDataBuffer create( int[] data ) throws CannotCreateBufferException {
        var buffer = allocator.createBuffer( this, getMemory(), allocate( data.length*4 ) );
        buffer.create();
        buffer.load( data );
        return buffer;
    }
    
    void dispose() {
        allocator.dispose();
    }
}
