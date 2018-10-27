/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanPipelineLayoutException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotCreateVulkanPipelineLayoutException( VulkanPhysicalDevice physicalDevice, int errorCode ) {
        super( physicalDevice, errorCode );
    }
}