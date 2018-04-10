/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
class VulkanPhysicalDevice implements Comparable<VulkanPhysicalDevice> {
    private final VkPhysicalDevice physicalDevice;
    private final VkPhysicalDeviceFeatures features;
    private final VkPhysicalDeviceProperties properties;
    private final VKCapabilitiesInstance capabilities;
    
    VulkanPhysicalDevice( long deviceAddress, VkInstance instance ) {
        physicalDevice = new VkPhysicalDevice( deviceAddress, instance );
        capabilities = physicalDevice.getCapabilities();
        features = VkPhysicalDeviceFeatures.create();
        properties = VkPhysicalDeviceProperties.create();
        VK10.vkGetPhysicalDeviceFeatures( physicalDevice, features );
        VK10.vkGetPhysicalDeviceProperties( physicalDevice, properties );
    }
    
    @Override
    public int compareTo( VulkanPhysicalDevice o ) {
        return Long.compare( getScore(), o.getScore() );
    }
    
    boolean isSuitable() {
        return properties.limits().maxMemoryAllocationCount() > 32 ;
    }
    
    long getScore(){
        return (
            properties.limits().maxMemoryAllocationCount() +
            properties.limits().maxImageDimension2D()
        ) * typeMultiplier();
    }
    
    private long typeMultiplier() {
        switch(properties.deviceType()){
            case VK10.VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU:
                return 6;
            case VK10.VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU:
            case VK10.VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU:
                return 5;
            case VK10.VK_PHYSICAL_DEVICE_TYPE_CPU:
            case VK10.VK_PHYSICAL_DEVICE_TYPE_OTHER:
            default:
                return 4;
        }
    }
}
