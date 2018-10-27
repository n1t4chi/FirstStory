/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

public class CannotCreateVulkanFrameBufferException extends CannotCreateVulkanPhysicalDeviceException {
    
    public CannotCreateVulkanFrameBufferException( VulkanPhysicalDevice device, Integer errorCode ) {
        super( device, errorCode );
    }
}
