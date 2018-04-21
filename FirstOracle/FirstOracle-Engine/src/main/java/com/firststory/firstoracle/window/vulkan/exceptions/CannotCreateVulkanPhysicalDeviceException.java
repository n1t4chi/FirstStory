/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanPhysicalDeviceException extends VulkanException {
    
    CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice  ) {
        this( physicalDevice, "" );
    }
    
     public CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice, String reason ) {
        super( "Cannot create physical device: "+physicalDevice+"."+ ( reason.isEmpty()? "" : "\nReason: "+reason ) );
    }
}
