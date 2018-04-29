/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

import java.util.Set;

/**
 * @author n1t4chi
 */
public class DeviceDoesNotHaveAllRequiredExtensionsException extends CannotCreateVulkanPhysicalDeviceException {
    
    public DeviceDoesNotHaveAllRequiredExtensionsException(
        VulkanPhysicalDevice physicalDevice,
        Set< String > unsupportedExtensions
    ) {
        super( physicalDevice, "Required extensions that are not supported: "+ unsupportedExtensions );
    }
}