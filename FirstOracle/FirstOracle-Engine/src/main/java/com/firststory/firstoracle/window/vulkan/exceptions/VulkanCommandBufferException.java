/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanCommandBuffer;
import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;

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
