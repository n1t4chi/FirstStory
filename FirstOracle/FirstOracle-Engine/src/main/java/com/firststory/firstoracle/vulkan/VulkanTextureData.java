/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;

/**
 * @author n1t4chi
 */
public class VulkanTextureData {
    
    private VulkanDataBuffer buffer;
    private VulkanInMemoryImage image;
    private int width;
    private int height;
    private VulkanImageView imageView;
    
    public VulkanImageView getImageView() {
        return imageView;
    }
    
    void setImageView( VulkanImageView imageView ) {
        this.imageView = imageView;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth( int width ) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight( int height ) {
        this.height = height;
    }
    
    public VulkanDataBuffer getBuffer() {
        return buffer;
    }
    
    public void setBuffer( VulkanDataBuffer buffer ) {
        this.buffer = buffer;
    }
    
    public VulkanInMemoryImage getImage() {
        return image;
    }
    
    public void setImage( VulkanInMemoryImage image ) {
        this.image = image;
    }
    
    void close() {
        if ( buffer != null ) { buffer.close(); }
        if ( imageView != null ) { imageView.close(); }
        if ( image != null ) { image.dispose(); }
    }
}
