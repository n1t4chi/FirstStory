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
    private int width;
    private int height;
    private VulkanAddress imageView;
    
    void setImageView( VulkanAddress imageView ) {
        this.imageView = imageView;
    }
    
    int getWidth() {
        return width;
    }
    
    void setWidth( int width ) {
        this.width = width;
    }
    
    int getHeight() {
        return height;
    }
    
    void setHeight( int height ) {
        this.height = height;
    }
    
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
