/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
public class VulkanStagableDataBufferImpl< Data > extends VulkanStagableDataBuffer<Data> {
    
    
    
    VulkanStagableDataBufferImpl(
        VulkanPhysicalDevice device, VulkanDataBufferProvider provider ) {
        super( device, provider );
    }
    
    @Override
    int extractLength( Data data ) {
        return 0;
    }
    
    @Override
    int extractDataByteSize( Data data ) {
        return 0;
    }
    
    @Override
    ByteBuffer toByteBuffer( Data data ) {
        return null;
    }
}
