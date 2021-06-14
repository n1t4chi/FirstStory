/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandPool;

/**
 * @author n1t4chi
 */
public class VulkanCommandPoolException extends VulkanException {
    
    public VulkanCommandPoolException(
        VulkanPhysicalDevice physicalDevice, VulkanCommandPool commandPool, String message
    ) {
        super( commandPool+" @ "+ physicalDevice+" : "+ message );
    }
}
