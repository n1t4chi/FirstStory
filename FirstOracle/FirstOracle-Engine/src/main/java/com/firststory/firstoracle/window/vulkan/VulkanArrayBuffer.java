/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.ArrayBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author n1t4chi
 */
class VulkanArrayBuffer extends VulkanStageableDataBuffer< float[] > implements ArrayBuffer {
    
    VulkanArrayBuffer( VulkanPhysicalDevice device, VulkanDataBufferProvider provider ) {
        super( device, provider );
    }
    
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
        ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc( extractLength( data ) * extractDataByteSize( data ) );
        FloatBuffer dataBuffer = vertexDataBuffer.asFloatBuffer();
        dataBuffer.put( data );
        return vertexDataBuffer;
    }
}
