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
    
    int compare( VulkanQueueFamily family ) {
        return family == null ? properties.queueCount() : properties.queueCount() - family.properties.queueCount();
    }
    
    static int compare( VulkanQueueFamily family1, VulkanQueueFamily family2 ) {
        return family1 == family2
            ? 0
            : family1 == null
                ? -family2.compare( null )
                : family1.compare( family2 )
        ;
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
