/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
class VulkanTextureData {
    
    private VulkanMappableBuffer buffer;
    private VulkanAddress textureImage;
    
    VulkanMappableBuffer getBuffer() {
        return buffer;
    }
    
    void setBuffer( VulkanMappableBuffer buffer ) {
        this.buffer = buffer;
    }
    
    VulkanAddress getTextureImage() {
        return textureImage;
    }
    
    void setTextureImage( VulkanAddress textureImage ) {
        this.textureImage = textureImage;
    }
}
