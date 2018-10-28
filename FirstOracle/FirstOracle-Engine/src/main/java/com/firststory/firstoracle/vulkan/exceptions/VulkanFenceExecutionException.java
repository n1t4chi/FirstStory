/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanFence;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class VulkanFenceExecutionException extends VulkanException {
    
    public VulkanFenceExecutionException(
        VulkanPhysicalDevice device,
        VulkanFence fence,
        int code
    ) {
        super( "Could not execute commands on fence: " + fence + " for device " + device + "." +
               " Status code: " + VulkanExceptionHelper.parseResultCode( code )
        );
    }
}
