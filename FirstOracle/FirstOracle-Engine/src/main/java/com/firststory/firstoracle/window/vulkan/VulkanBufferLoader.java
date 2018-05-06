/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.ArrayBufferProvider;
import com.firststory.firstoracle.data.CannotCreateBufferException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanBufferLoader implements ArrayBufferProvider< VulkanDataBuffer > {
    
    static final float[] VERTICES = new float[]{
        /*1*/ /*pos*/ -0.75f, -0.75f, /*col*/ 1.0f, 0.0f, 1.0f,
        /*2*/ /*pos*/ 0.75f, -0.75f, /*col*/ 1.0f, 1.0f, 0.0f,
        /*3*/ /*pos*/ 0.0f, 0.75f, /*col*/ 0.0f, 1.0f, 1.0f
    };
    
    private final VulkanPhysicalDevice device;
    private List<VulkanDataBuffer > buffers = new ArrayList<>(  );
    VulkanDataBuffer buffer;
    
    VulkanBufferLoader( VulkanPhysicalDevice device ) {
        
        this.device = device;
    }
    
    @Override
    public VulkanDataBuffer create() throws CannotCreateBufferException {
        return null;
    }
    
    @Override
    public void bind( VulkanDataBuffer bufferID ) {
    
    }
    
    @Override
    public void load( VulkanDataBuffer bufferID, float[] bufferData ) {
    
    }
    
    @Override
    public void delete( VulkanDataBuffer bufferID ) {
    
    }
    
    @Override
    public void close() {
        buffers.forEach( VulkanDataBuffer::close );
    }
    
    int getVertexLength() {
        return VERTICES.length;
    }
    
    void update() {
        if(buffers.isEmpty()) {
            buffer = new VulkanDataBuffer( device, VERTICES );
            buffer.load();
            buffers.add( buffer );
        }
    }
}
