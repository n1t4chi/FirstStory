/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
class VulkanTexture {
    
    private VulkanStagableDataBuffer< byte[] > buffer;
    private VulkanAddress textureImage;
    
    VulkanStagableDataBuffer< byte[] > getBuffer() {
        return buffer;
    }
    
    void setBuffer( VulkanStagableDataBuffer< byte[] > buffer ) {
        this.buffer = buffer;
    }
    
    VulkanAddress getTextureImage() {
        return textureImage;
    }
    
    void setTextureImage( VulkanAddress textureImage ) {
        this.textureImage = textureImage;
    }
}
