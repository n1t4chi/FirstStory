/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.exceptions;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanQueueFamily;

/**
 * @author n1t4chi
 */
public class CannotSubmitVulkanDrawCommandBufferException extends VulkanException {
    
    public CannotSubmitVulkanDrawCommandBufferException(
        VulkanQueueFamily queueFamily,
        Integer errorCode
    ) {
        super( errorCode, "Cannot submit vulkan draw command buffer for queue family: " + queueFamily + "." );
    }
}
