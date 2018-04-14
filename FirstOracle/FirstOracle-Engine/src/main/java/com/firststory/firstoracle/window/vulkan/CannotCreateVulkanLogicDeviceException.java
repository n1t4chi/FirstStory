/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.vulkan.VkPhysicalDevice;

/**
 * @author n1t4chi
 */
class CannotCreateVulkanLogicDeviceException extends VulkanException {
    
    public CannotCreateVulkanLogicDeviceException( VkPhysicalDevice physicalDevice ) {
        super( "Cannot create Logical device for physical device: "+physicalDevice );
    }
}
