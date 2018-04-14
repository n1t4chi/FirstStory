/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.vulkan.VkQueueFamilyProperties;

class VulkanQueueFamily {
    private final VkQueueFamilyProperties properties;
    private final int index;
    
    VulkanQueueFamily( VkQueueFamilyProperties properties, int index ) {
        this.properties = properties;
        this.index = index;
    }
    
    public VkQueueFamilyProperties getProperties() {
        return properties;
    }
    
    public int getIndex() {
        return index;
    }
    
    boolean isBetterThan( VulkanQueueFamily family ) {
        return family == null || properties.queueCount() > family.properties.queueCount();
    }
    
    boolean isFlagSet( int flag ) {
        return (properties.queueFlags() & flag) == flag;
    }
    
    @Override
    public String toString() {
        return "family:"+hashCode()+"[" +
            "queue count:"+properties.queueCount()+", " +
            "flags:"+properties.queueFlags()+
        "]";
    }
}
