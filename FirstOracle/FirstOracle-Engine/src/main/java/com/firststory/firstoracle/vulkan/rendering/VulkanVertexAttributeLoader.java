/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.vulkan.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;

/**
 * @author n1t4chi
 */
public class VulkanVertexAttributeLoader implements VertexAttributeLoader< VulkanDataBuffer > {
    
    private final VulkanBufferProvider bufferProvider;
    
    public VulkanVertexAttributeLoader( VulkanBufferProvider bufferProvider ) {
        this.bufferProvider = bufferProvider;
    }
    
    @Override
    public VulkanDataBuffer provideBuffer( float[] array ) {
        return bufferProvider.createVertexBuffer( array );
    }
}
