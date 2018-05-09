/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanPhysicalDeviceException extends VulkanException {
    
    private static String getDefaultString( VulkanPhysicalDevice physicalDevice ) {
        return "Cannot create physical device: " + physicalDevice + ".";
    }
    
    public CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice ) {
        super( getDefaultString( physicalDevice ) );
    }
    
    public CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice, Exception ex ) {
        super( getDefaultString( physicalDevice ), ex );
    }
    
    public CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice, int errorCode ) {
        this( physicalDevice, errorCode, "" );
    }
    
    public CannotCreateVulkanPhysicalDeviceException(
        VulkanPhysicalDevice physicalDevice,
        int errorCode,
        String reason
    ) {
        super( getDefaultString( physicalDevice ) +
            "Error code: " + errorCode +" ["+VulkanExceptionHelper.parseResultCode(errorCode)+"]" +
            ( reason.isEmpty() ? "" : "\nReason: " + reason )
        );
    }
    
    public CannotCreateVulkanPhysicalDeviceException( VulkanPhysicalDevice physicalDevice, String reason ) {
        super( getDefaultString( physicalDevice ) + ( reason.isEmpty() ? "" : "\nReason: " + reason ) );
    }
}
