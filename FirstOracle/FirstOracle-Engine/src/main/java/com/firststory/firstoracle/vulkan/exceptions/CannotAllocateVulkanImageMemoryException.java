/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotAllocateVulkanImageMemoryException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotAllocateVulkanImageMemoryException( VulkanPhysicalDevice device, int errorCode ) {
        super( device, errorCode );
    }
}
