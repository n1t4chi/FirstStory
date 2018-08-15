/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @author n1t4chi
 */
public class VulkanUniformBuffer extends VulkanMappableBuffer {
    
    private static final int[] UNIFORM_BUFFER_USAGE_FLAGS = { VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT };
    private static final int[] UNIFORM_BUFFER_MEMORY_FLAGS = {
        VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    };
    
    VulkanUniformBuffer( VulkanPhysicalDevice device ) {
        super( device, UNIFORM_BUFFER_USAGE_FLAGS, UNIFORM_BUFFER_MEMORY_FLAGS );
    }
    
    public void load( float[] uniformData ) throws BufferNotCreatedException {
        load( toByteBuffer( uniformData ) );
    }
    
    private ByteBuffer toByteBuffer( float[] data ) {
        ByteBuffer vertexDataBuffer = MemoryUtil.memAlloc( data.length * 4 );
        FloatBuffer dataBuffer = vertexDataBuffer.asFloatBuffer();
        dataBuffer.put( data );
        return vertexDataBuffer;
    }
}
