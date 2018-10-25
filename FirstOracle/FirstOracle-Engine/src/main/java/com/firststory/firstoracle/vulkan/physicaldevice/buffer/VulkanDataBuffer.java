/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

import com.firststory.firstoracle.buffer.BufferNotCreatedException;
import com.firststory.firstoracle.buffer.CannotCreateBufferException;
import com.firststory.firstoracle.vulkan.VulkanAddress;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author n1t4chi
 */
public class VulkanDataBuffer extends DataBufferInLinearMemory< ByteBuffer > {
    
    private final VulkanBufferMemory deviceMemory;
    private ByteBuffer byteBuffer = MemoryUtil.memAlloc( 1024 * 4 );
    
    VulkanDataBuffer(
        VulkanDataBufferProvider controller,
        VulkanBufferMemory deviceMemory,
        LinearMemoryLocation deviceMemoryLocation
    ) {
        super( controller, deviceMemoryLocation );
        this.deviceMemory = deviceMemory;
    }
    
    public VulkanAddress getBufferAddress() {
        return deviceMemory.getAddress();
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
    
    @Override
    public String toString() {
        return "Buffer@" + hashCode() + "{ buf: " + getBufferAddress().getValue() + ", of: " + getMemoryOffset() +" }";
    }
}
