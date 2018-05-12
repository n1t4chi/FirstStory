/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;

import java.nio.IntBuffer;

/**
 * @author n1t4chi
 */
class VulkanTransferCommandPool extends VulkanCommandPool {
    
    VulkanTransferCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily,
        VulkanSemaphore imageAvailableSemaphore
    ) {
        super(
            device,
            usedQueueFamily,
            imageAvailableSemaphore,
            VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT
        );
    }
    
    @Override
    IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT );
    }
}
