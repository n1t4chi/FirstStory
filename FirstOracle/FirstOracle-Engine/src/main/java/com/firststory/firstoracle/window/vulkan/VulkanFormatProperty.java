/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.vulkan.VkFormatProperties;

/**
 * @author n1t4chi
 */
public class VulkanFormatProperty {
    private final int format;
    private final VkFormatProperties property;
    
    VulkanFormatProperty( int format, VkFormatProperties property ) {
        this.format = format;
        this.property = property;
    }
    
    public int getFormat() {
        return format;
    }
    
    VkFormatProperties getProperty() {
        return property;
    }
    
    int linearTilingFeatures() {
        return property.linearTilingFeatures();
    }
    
    int optimalTilingFeatures() {
        return property.optimalTilingFeatures();
    }
}
