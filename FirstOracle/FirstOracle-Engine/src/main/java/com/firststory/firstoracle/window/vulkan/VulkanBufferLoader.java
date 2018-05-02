/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.ArrayBufferLoader;
import com.firststory.firstoracle.data.CannotCreateBufferException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanBufferLoader implements ArrayBufferLoader {
    
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
    public int create() throws CannotCreateBufferException {
        return 1;
    }
    
    @Override
    public void bind( int bufferID ) {
    
    }
    
    @Override
    public void load( int bufferID, float[] bufferData ) {
    
    }
    
    @Override
    public void delete( int bufferID ) {
    
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
            buffers.add( buffer );
        }
    }
}
