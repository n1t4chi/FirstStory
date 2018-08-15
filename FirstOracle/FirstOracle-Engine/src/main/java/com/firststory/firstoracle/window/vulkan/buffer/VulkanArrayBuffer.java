/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.data.ArrayBuffer;
import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author n1t4chi
 */
public class VulkanArrayBuffer extends VulkanStageableDataBuffer< float[] > implements ArrayBuffer {
    
    public VulkanArrayBuffer( VulkanPhysicalDevice device, VulkanDataBufferProvider provider ) {
        super( device, provider );
    }
    
    @Override
    public int extractLength( float[] data ) {
        return data.length;
    }
    
    @Override
    public int extractDataByteSize( float[] data ) {
        return 4;
    }
    
    @Override
    public ByteBuffer toByteBuffer( float[] data ) {
        ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc( extractLength( data ) * extractDataByteSize( data ) );
        FloatBuffer dataBuffer = vertexDataBuffer.asFloatBuffer();
        dataBuffer.put( data );
        return vertexDataBuffer;
    }
}
