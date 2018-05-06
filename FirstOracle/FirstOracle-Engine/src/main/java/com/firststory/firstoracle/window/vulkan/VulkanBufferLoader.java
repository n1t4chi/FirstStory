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
    
    private final VulkanPhysicalDevice device;
    private final List<VulkanDataBuffer > buffers = new ArrayList<>(  );
    
    VulkanBufferLoader( VulkanPhysicalDevice device ) {
        
        this.device = device;
    }
    
    @Override
    public VulkanDataBuffer create() throws CannotCreateBufferException {
        return new VulkanDataBuffer( device, this );
    }
    
    @Override
    public void bind( VulkanDataBuffer buffer ) {
    }
    
    @Override
    public void load( VulkanDataBuffer buffer, float[] bufferData ) {
        buffer.load( bufferData );
    }
    
    @Override
    public void delete( VulkanDataBuffer buffer ) {
        buffer.close();
    }
    
    @Override
    public void close() {
        buffers.forEach( VulkanDataBuffer::close );
        buffers.clear();
    }
    
    void update() {
    }
}
