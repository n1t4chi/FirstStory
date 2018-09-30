/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.buffer;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import com.firststory.firstoracle.vulkan.VulkanAddress;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class VulkanDataBuffer extends DataBufferInLinearMemory< ByteBuffer > {
    
    private final VulkanBufferMemory memory;
    private ByteBuffer byteBuffer = MemoryUtil.memAlloc( 1024 * 4 );
    
    VulkanDataBuffer(
        VulkanDataBufferProvider controller, VulkanBufferMemory memory, LinearMemoryLocation memoryLocation
    )
    {
        super( controller, memoryLocation );
        this.memory = memory;
    }
    
    public VulkanAddress getBufferAddress() {
        return memory.getAddress();
    }
    
    public void load( FloatBuffer data ) throws BufferNotCreatedException {
        load( MemoryUtil.memByteBuffer( MemoryUtil.memAddress( data ), data.remaining() ) );
    }
    
    public void load( IntBuffer data ) throws CannotCreateBufferException {
        load( MemoryUtil.memByteBuffer( MemoryUtil.memAddress( data ), data.remaining() ) );
    }
    
    public void load( byte[] data ) throws CannotCreateBufferException {
        load( data.length, () -> byteBuffer.put( data ) );
    }
    
    
    public void load( float[] data ) throws CannotCreateBufferException {
        if( false ) {
            var bb = MemoryUtil.memAlloc( data.length * 4 );
            bb.asFloatBuffer().put( data );
            var ba = new byte[ data.length * 4];
            bb.get( ba );
            System.out.println( " float local: " + Arrays.toString( data ) );
            System.out.println( " byte local: " + Arrays.toString( ba ) );
        }
        
        load( data.length*4, () -> byteBuffer.asFloatBuffer().put( data ) );
    }
    
    public void load( int[] data ) throws CannotCreateBufferException {
        load( data.length*4, () -> byteBuffer.asIntBuffer().put( data ) );
    }
    
    private void load( int dataLength, Runnable putData ) {
        if ( byteBuffer.capacity() < dataLength ) {
            MemoryUtil.memFree( byteBuffer );
            byteBuffer = MemoryUtil.memAlloc( dataLength );
        }
        byteBuffer.clear();
        putData.run();
        byteBuffer.limit( dataLength );
        load( byteBuffer );
    }
}
