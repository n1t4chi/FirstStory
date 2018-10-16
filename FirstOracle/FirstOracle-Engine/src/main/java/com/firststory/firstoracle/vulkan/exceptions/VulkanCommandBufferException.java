/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.commands.VulkanCommandBuffer;

/**
 * @author n1t4chi
 */
public class VulkanCommandBufferException extends VulkanException {
    
    public VulkanCommandBufferException(
        VulkanPhysicalDevice physicalDevice, VulkanCommandBuffer commandBuffer, String message
    ) {
        super( commandBuffer+" @ "+ physicalDevice+" : "+ message );
    }
}
