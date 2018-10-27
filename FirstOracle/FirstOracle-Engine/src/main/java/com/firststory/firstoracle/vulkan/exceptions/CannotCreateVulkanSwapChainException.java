/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanSwapChainException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotCreateVulkanSwapChainException( VulkanPhysicalDevice device, Integer errorCode ) {
        super( device, errorCode );
    }
}
