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
    
    private VulkanTransferCommandBuffer commandBuffer;
    
    VulkanTransferCommandPool( VulkanPhysicalDevice device, VulkanQueueFamily usedQueueFamily ) {
        super( device, usedQueueFamily );
    }
    
    public void executeQueue( VulkanCommand< VulkanTransferCommandBuffer > commands ) {
        var commandBuffer = extractNextCommandBuffer();
        commands.execute( commandBuffer );
        executeTransfers();
    }
    
    public void executeQueueLater( VulkanCommand< VulkanTransferCommandBuffer > commands ) {
        var commandBuffer = extractNextCommandBuffer();
        commands.execute( commandBuffer );
    }
    
    public void executeTransfers() {
        if( commandBuffer != null ) {
            commandBuffer.fillQueueTearDown();
            submitQueue( commandBuffer );
            executeTearDown();
            commandBuffer = null;
        }
    }
    
    private synchronized VulkanTransferCommandBuffer extractNextCommandBuffer() {
        if( commandBuffer == null ) {
            commandBuffer = createNewCommandBuffer();
            commandBuffer.fillQueueSetup();
        }
        return commandBuffer;
    }
    
    @Override
    public IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT );
    }
    
    private VulkanTransferCommandBuffer createNewCommandBuffer() {
        return new VulkanTransferCommandBuffer( getDevice(),
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) ),
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT
        );
    }
    
    private void executeTearDown() {
        getDevice().waitForQueues();
        getUsedQueueFamily().waitForQueue();
    }
}
