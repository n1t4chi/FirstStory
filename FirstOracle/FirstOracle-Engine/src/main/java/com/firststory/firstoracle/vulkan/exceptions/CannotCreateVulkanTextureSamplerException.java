/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanTextureSamplerException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotCreateVulkanTextureSamplerException( VulkanPhysicalDevice physicalDevice, Integer errorCode ) {
        super( physicalDevice, errorCode );
    }
}