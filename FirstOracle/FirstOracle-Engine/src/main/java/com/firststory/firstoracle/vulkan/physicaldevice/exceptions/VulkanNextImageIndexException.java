/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class VulkanNextImageIndexException extends VulkanException {
    
    public VulkanNextImageIndexException( VulkanPhysicalDevice physicalDevice, int imageIndex, String message ) {
        super( "Error during acquiring next image index (returned value: " + imageIndex + ") for device: " +
            physicalDevice + ". Message:\n" + message );
    }
    
    public VulkanNextImageIndexException( VulkanPhysicalDevice physicalDevice, Throwable... suppressed ) {
        super( "Error during acquiring next image index for device: " + physicalDevice +
            ". Multiple exceptions intercepted." );
        for ( var throwable : suppressed ) {
            addSuppressed( throwable );
        }
    }
}
