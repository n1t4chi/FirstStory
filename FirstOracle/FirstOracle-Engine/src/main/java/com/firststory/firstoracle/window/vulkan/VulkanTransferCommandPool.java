/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;

import java.nio.IntBuffer;
import java.util.Map;

/**
 * @author n1t4chi
 */
class VulkanTransferCommandPool extends VulkanCommandPool< VulkanTransferCommandBuffer > {
    
    VulkanTransferCommandPool(
        VulkanPhysicalDevice device, VulkanQueueFamily usedQueueFamily
    ) {
        super( device, usedQueueFamily );
    }
    
    @Override
    VulkanTransferCommandBuffer createNewCommandBuffer(
        int index,
        VulkanAddress address,
        Map< Integer, VulkanFrameBuffer > frameBuffers
    ) {
        return createNewCommandBuffer( address );
    }
    
    private VulkanTransferCommandBuffer createNewCommandBuffer( VulkanAddress address ) {
        return new VulkanTransferCommandBuffer( getDevice(),
            address,
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT
        );
    }
    
    @Override
    VulkanTransferCommandBuffer extractNextCommandBuffer() {
        return createNewCommandBuffer( new VulkanAddress( createCommandBufferBuffer( 1 ).get( 0 ) ) );
    }
    
    @Override
    void postExecute( VulkanTransferCommandBuffer commandBuffer ) {
        getUsedQueueFamily().waitForQueue();
    }
    
    @Override
    IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT );
    }
}
