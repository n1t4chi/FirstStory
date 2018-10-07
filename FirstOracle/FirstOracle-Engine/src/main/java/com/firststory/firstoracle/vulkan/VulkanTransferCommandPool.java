/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;

import java.nio.IntBuffer;

/**
 * @author n1t4chi
 */
public class VulkanTransferCommandPool extends VulkanCommandPool< VulkanTransferCommandBuffer > {
    
    VulkanTransferCommandPool( VulkanPhysicalDevice device, VulkanQueueFamily usedQueueFamily ) {
        super( device, usedQueueFamily );
    }
    
    public void executeQueue( VulkanCommand< VulkanTransferCommandBuffer > commands ) {
        var commandBuffer = extractNextCommandBuffer();
        commandBuffer.fillQueueSetup();
        commandBuffer.fillQueue( commands );
        commandBuffer.fillQueueTearDown();
        submitQueue( commandBuffer );
        executeTearDown();
    }
    
    public VulkanTransferCommandBuffer extractNextCommandBuffer() {
        return createNewCommandBuffer();
    }
    
    private VulkanTransferCommandBuffer createNewCommandBuffer() {
        return new VulkanTransferCommandBuffer( getDevice(),
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) ),
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT
        );
    }
    
    public void executeTearDown() {
        getUsedQueueFamily().waitForQueue();
    }
    
    @Override
    public IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT );
    }
}
