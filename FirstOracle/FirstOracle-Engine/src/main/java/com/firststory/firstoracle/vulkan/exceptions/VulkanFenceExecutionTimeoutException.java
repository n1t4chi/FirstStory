/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanFence;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;

/**
 * @author n1t4chi
 */
public class VulkanFenceExecutionTimeoutException extends VulkanException {
    
    public VulkanFenceExecutionTimeoutException(
        VulkanPhysicalDevice device,
        VulkanFence fence
    ) {
        super( "Could not execute commands due to timeout on fence: " + fence + " for device " + device + "." );
    }
}
