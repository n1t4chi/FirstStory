/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanShaderException extends VulkanException {
    
    public CannotCreateVulkanShaderException( VulkanPhysicalDevice physicalDevice, String filepath ) {
        super( "Cannot create shader "+filepath+" for  device: " + physicalDevice );
    }
}
