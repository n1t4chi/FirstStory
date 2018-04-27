/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

public class CannotCreateVulkanFrameBufferException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotCreateVulkanFrameBufferException( VulkanPhysicalDevice vulkanPhysicalDevice ) {
        super( vulkanPhysicalDevice);
    }
}
