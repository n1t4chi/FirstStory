/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.object.VertexAttribute;
import com.firststory.firstoracle.object.VertexAttributeLoader;

/**
 * @author n1t4chi
 */
class VulkanVertexAttributeLoader implements VertexAttributeLoader< VulkanArrayBuffer > {
    
    private final VulkanDataBufferProvider bufferProvider;
    
    VulkanVertexAttributeLoader( VulkanDataBufferProvider bufferProvider ) {
        this.bufferProvider = bufferProvider;
    }
    
    @Override
    public VulkanArrayBuffer createEmptyBuffer() {
        return bufferProvider.createFloatBuffer();
    }
    
    @Override
    public void bindBuffer( VertexAttribute attribute, long key ) {
    
    }
}
