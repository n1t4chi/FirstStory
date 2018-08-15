/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.buffer.VulkanDataBuffer;

/**
 * @author n1t4chi
 */
class VulkanTextureData {
    
    private VulkanDataBuffer buffer;
    private VulkanInMemoryImage image;
    private int width;
    private int height;
    private VulkanImageView imageView;
    
    void close() {
        if ( buffer != null ) { buffer.close(); }
        if ( imageView != null ) { imageView.close(); }
        if ( image!= null ) { image.close(); }
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
    
    VulkanDataBuffer getBuffer() {
        return buffer;
    }
    
    void setBuffer( VulkanDataBuffer buffer ) {
        this.buffer = buffer;
    }
    
    VulkanInMemoryImage getImage() {
        return image;
    }
    
    void setImage( VulkanInMemoryImage mage ) {
        this.image = mage;
    }
}
