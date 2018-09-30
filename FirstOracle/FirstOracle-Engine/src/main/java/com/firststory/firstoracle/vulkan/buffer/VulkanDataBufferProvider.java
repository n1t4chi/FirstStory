/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.buffer;

import com.firststory.firstoracle.data.BufferProvider;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author n1t4chi
 */
public class VulkanDataBufferProvider extends LinearMemoryController< VulkanBufferMemory, ByteBuffer > implements
    BufferProvider< VulkanDataBuffer, ByteBuffer >
{
    
    public VulkanDataBufferProvider( VulkanBufferMemory memory ) {
        super( memory );
    }
    
    @Override
    public VulkanDataBuffer create( ByteBuffer data ) throws CannotCreateBufferException {
        var buffer = new VulkanDataBuffer( this, getMemory(), allocate( data.remaining() ) );
        buffer.create();
        buffer.load( data );
        return buffer;
    }
    
    public VulkanDataBuffer create( float[] data ) throws CannotCreateBufferException {
        var buffer = new VulkanDataBuffer( this, getMemory(), allocate( data.length*4 ) );
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
        var buffer = new VulkanDataBuffer( this, getMemory(), allocate( data.length ) );
        buffer.create();
        buffer.load( data );
        return buffer;
    }
    
    public VulkanDataBuffer create( int[] data ) throws CannotCreateBufferException {
        var buffer = new VulkanDataBuffer( this, getMemory(), allocate( data.length*4 ) );
        buffer.create();
        buffer.load( data );
        return buffer;
    }
}
