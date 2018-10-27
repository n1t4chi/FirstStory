/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

/**
 * @author n1t4chi
 */
public class NoDeviceSupportingVulkanException extends VulkanException {
    
    public NoDeviceSupportingVulkanException() {
        super( "There are no devices that support Vulkan." );
    }
}
