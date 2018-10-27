/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;

/**
 * @author n1t4chi
 */
public class VulkanTextureData {
    
    private final VulkanDeviceAllocator allocator;
    private VulkanDataBuffer buffer;
    private VulkanInMemoryImage image;
    private int width;
    private int height;
    private VulkanImageView imageView;
    
    public VulkanTextureData( VulkanDeviceAllocator allocator ) {
        this.allocator = allocator;
    }
    
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
    
    void dispose() {
        allocator.deregisterTextureData( this );
    }
    
    public void disposeUnsafe() {
        if ( buffer != null ) {
            buffer.close();
        }
        if ( imageView != null ) {
            imageView.dispose();
        }
        if ( image != null ) {
            image.dispose();
        }
        buffer = null;
        image = null;
        width = 0;
        height = 0;
        imageView = null;
    }
}
