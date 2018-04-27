/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

public class CannotCreateVulkanCommandPoolException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotCreateVulkanCommandPoolException( VulkanPhysicalDevice device ) {
        super( device );
    }
}
