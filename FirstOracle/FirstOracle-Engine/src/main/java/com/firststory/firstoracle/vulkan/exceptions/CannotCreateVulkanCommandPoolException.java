/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;

public class CannotCreateVulkanCommandPoolException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotCreateVulkanCommandPoolException( VulkanPhysicalDevice device, Integer errorCode ) {
        super( device, errorCode );
    }
}
