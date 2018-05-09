/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotBindVulkanImageMemoryException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotBindVulkanImageMemoryException( VulkanPhysicalDevice device, int errorCode ) {
        super( device, errorCode );
    }
}
