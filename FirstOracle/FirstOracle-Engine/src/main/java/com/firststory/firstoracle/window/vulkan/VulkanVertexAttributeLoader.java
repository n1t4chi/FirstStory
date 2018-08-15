/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.object.VertexAttribute;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.window.vulkan.buffer.VulkanArrayBuffer;
import com.firststory.firstoracle.window.vulkan.buffer.VulkanDataBufferProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
class VulkanVertexAttributeLoader implements VertexAttributeLoader< VulkanArrayBuffer > {
    
    private final VulkanDataBufferProvider bufferProvider;
    
    private final Map<Integer, VulkanArrayBuffer> bufferMap = new HashMap<>();
    
    VulkanVertexAttributeLoader( VulkanDataBufferProvider bufferProvider ) {
        this.bufferProvider = bufferProvider;
    }
    
    @Override
    public VulkanArrayBuffer createEmptyBuffer() {
        return bufferProvider.createFloatBuffer();
    }
    
    @Override
    public void bindBuffer( VertexAttribute attribute, long key ) {
        VulkanArrayBuffer buffer = ( VulkanArrayBuffer ) attribute.getBuffer( key, this );
        bufferMap.put(
            attribute.getIndex(), buffer
        );
        buffer.bind();
    }
    
    VulkanArrayBuffer getLastBoundPositionBuffer() {
        return bufferMap.get( 0 );
    }
    
    VulkanArrayBuffer getLastBoundUvMapBuffer() {
        return bufferMap.get( 1 );
    }
}
