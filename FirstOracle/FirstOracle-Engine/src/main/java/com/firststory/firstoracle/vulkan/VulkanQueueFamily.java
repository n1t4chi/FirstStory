/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

public class VulkanQueueFamily {
    
    private final VulkanPhysicalDevice device;
    private final VkQueueFamilyProperties properties;
    private final int index;
    private VkQueue queue = null;
    
    VulkanQueueFamily( VulkanPhysicalDevice device, VkQueueFamilyProperties properties, int index ) {
        this.device = device;
        this.properties = properties;
        this.index = index;
    }
    
    public VkQueue getQueue() {
        if( queue == null ) {
            queue = extractQueue();
        }
        return queue;
    }
    
    void waitForQueue() {
        VK10.vkQueueWaitIdle( getQueue() );
    }
    
    private VkQueue extractQueue() {
        var queuePointer = MemoryStack.stackMallocPointer( 1 );
        VK10.vkGetDeviceQueue( device.getLogicalDevice(), index, 0, queuePointer );
        return new VkQueue( queuePointer.get(), device.getLogicalDevice() );
        
    }
    
    VkQueueFamilyProperties getProperties() {
        return properties;
    }
    
    int getIndex() {
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
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
    
        var that = ( VulkanQueueFamily ) o;
        
        if ( index != that.index ) { return false; }
        return properties != null ? properties.equals( that.properties ) : that.properties == null;
    }
    
    @Override
    public int hashCode() {
        var result = properties != null ? properties.hashCode() : 0;
        result = 31 * result + index;
        return result;
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