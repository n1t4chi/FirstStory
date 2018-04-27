/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;

/**
 * @author n1t4chi
 */
public class CannotSubmitVulkanDrawCommandBufferException extends VulkanException {
    
    public CannotSubmitVulkanDrawCommandBufferException(
        VulkanPhysicalDevice physicalDevice, VkQueue presentationQueue
    ) {
        super( "Cannot submit vulkan draw command buffer for " + presentationQueue + " at device" + physicalDevice );
    }
}
