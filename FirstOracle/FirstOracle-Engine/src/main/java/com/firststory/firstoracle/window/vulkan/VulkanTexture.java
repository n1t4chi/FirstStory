/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
class VulkanTexture {
    
    private VulkanDataBuffer stagingBuffer;
    private VulkanAddress textureImage;
    
    VulkanDataBuffer getStagingBuffer() {
        return stagingBuffer;
    }
    
    void setStagingBuffer( VulkanDataBuffer buffer ) {
    
        this.stagingBuffer = buffer;
    }
    
    VulkanAddress getTextureImage() {
        return textureImage;
    }
    
    void setTextureImage( VulkanAddress textureImage ) {
        this.textureImage = textureImage;
    }
}
