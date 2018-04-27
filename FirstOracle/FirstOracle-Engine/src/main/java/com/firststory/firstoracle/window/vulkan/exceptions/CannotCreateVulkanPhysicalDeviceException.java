/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanPhysicalDeviceException extends VulkanException {
    
    public CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice, Exception ex ) {
        super( getDefaultString( physicalDevice ), ex );
    }
    
    CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice  ) {
        this( physicalDevice, "" );
    }
    
     public CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice, String reason ) {
        super( getDefaultString( physicalDevice ) + ( reason.isEmpty() ? "" : "\nReason: " + reason ) );
    }
    
    private static String getDefaultString( VulkanPhysicalDevice physicalDevice ) {
        return "Cannot create physical device: " + physicalDevice + ".";
    }
}
