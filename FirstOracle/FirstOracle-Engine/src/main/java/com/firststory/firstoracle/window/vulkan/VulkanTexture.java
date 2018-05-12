/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
class VulkanTexture {
    
    private VulkanStageableDataBuffer< byte[] > buffer;
    private VulkanAddress textureImage;
    
    VulkanStageableDataBuffer< byte[] > getBuffer() {
        return buffer;
    }
    
    void setBuffer( VulkanStageableDataBuffer< byte[] > buffer ) {
        this.buffer = buffer;
    }
    
    VulkanAddress getTextureImage() {
        return textureImage;
    }
    
    void setTextureImage( VulkanAddress textureImage ) {
        this.textureImage = textureImage;
    }
}
