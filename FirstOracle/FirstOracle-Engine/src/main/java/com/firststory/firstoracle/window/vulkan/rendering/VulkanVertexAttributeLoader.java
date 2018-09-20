/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.rendering;

import com.firststory.firstoracle.object.VertexAttribute;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.object.Vertices;
import com.firststory.firstoracle.window.vulkan.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.window.vulkan.buffer.VulkanDataBuffer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class VulkanVertexAttributeLoader implements VertexAttributeLoader< VulkanDataBuffer > {
    
    private final VulkanBufferProvider bufferProvider;
    
    private final Map<Integer, VulkanDataBuffer> bufferMap = new HashMap<>();
    
    public VulkanVertexAttributeLoader( VulkanBufferProvider bufferProvider ) {
        this.bufferProvider = bufferProvider;
    }
    
    @Override
    public VulkanDataBuffer provideBuffer( float[] array ) {
        return bufferProvider.createVertexBuffer( array );
    }
    
    @Override
    public void bindBuffer( VertexAttribute attribute, long key ) {
        VulkanDataBuffer buffer = ( VulkanDataBuffer ) attribute.getBuffer( key, this );
        bufferMap.put(
            attribute.getIndex(), buffer
        );
        buffer.bind();
    }
    
    VulkanDataBuffer getLastBoundVertexBuffer() {
        return bufferMap.get( 0 );
    }
    
    VulkanDataBuffer getLastBoundUvMapBuffer() {
        return bufferMap.get( 1 );
    }
    
    VulkanDataBuffer getLastBoundColourBuffer() {
        return bufferMap.get( 2 );
    }
}
