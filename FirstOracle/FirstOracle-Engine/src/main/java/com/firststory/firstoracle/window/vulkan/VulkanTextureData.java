/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

/**
 * @author n1t4chi
 */
class VulkanTextureData {
    
    private VulkanMappableBuffer buffer;
    private VulkanAddress image;
    private int width;
    private int height;
    private VulkanImageView imageView;
    
    void close() {
        if ( buffer != null ) { buffer.close(); }
        if ( imageView != null ) { imageView.close(); }
    }
    
    void setImageView( VulkanImageView imageView ) {
        this.imageView = imageView;
    }
    
    VulkanImageView getImageView() {
        return imageView;
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
    
    VulkanAddress getImage() {
        return image;
    }
    
    void setImage( VulkanAddress mage ) {
        this.image = mage;
    }
}
