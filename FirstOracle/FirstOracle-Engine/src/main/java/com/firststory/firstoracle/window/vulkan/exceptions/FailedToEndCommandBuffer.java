/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.vulkan.VulkanCommandPool;

/**
 * @author n1t4chi
 */
public class FailedToEndCommandBuffer extends RuntimeException {
    
    public FailedToEndCommandBuffer( VulkanCommandPool.VulkanCommandBuffer commandBuffer ) {
        super( "Failed to end command buffer for "+commandBuffer );
    }
}
