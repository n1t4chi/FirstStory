/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;


/**
 * @author n1t4chi
 */
public class NoDeviceSupportingVulkanEnoughException extends VulkanException {
    
    public NoDeviceSupportingVulkanEnoughException() {
        super( "There are no devices supporting Vulkan enough." );
    }
}
