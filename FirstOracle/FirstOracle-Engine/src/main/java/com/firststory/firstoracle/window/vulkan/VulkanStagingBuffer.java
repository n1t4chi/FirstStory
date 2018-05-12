/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
class VulkanStagingBuffer extends VulkanMappableBuffer {
    
    private static final int[] STAGING_BUFFER_USAGE_FLAGS = { VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT };
    private static final int[] STAGING_BUFFER_MEMORY_FLAGS = { VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT,
        VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    };
    
    VulkanStagingBuffer( VulkanPhysicalDevice device, VulkanDataBufferProvider loader ) {
        super( device, STAGING_BUFFER_USAGE_FLAGS, STAGING_BUFFER_MEMORY_FLAGS );
    }
    
    void copyBuffer( VulkanDataBuffer dstBuffer, VulkanCommandPool commandPool ) {
        VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .level( VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY )
            .commandPool( commandPool.getAddress().getValue() )
            .commandBufferCount( 1 );
        
        VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO )
            .flags( VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT );
        
        PointerBuffer commandBufferBuffer = commandPool.createCommandBufferBuffer( allocInfo, 1 );
        VkCommandBuffer commandBuffer = new VkCommandBuffer( commandBufferBuffer.get( 0 ), getDeviceLogicalDevice() );
        VK10.vkBeginCommandBuffer( commandBuffer, beginInfo );
        
        VkBufferCopy copyRegion = VkBufferCopy.create().srcOffset( 0 ) // Optional
            .dstOffset( 0 ) // Optional
            .size( dstBuffer.getLength() );
        
        VK10.vkCmdCopyBuffer( commandBuffer,
            this.getBufferAddress().getValue(),
            dstBuffer.getBufferAddress().getValue(),
            VkBufferCopy.create( 1 ).put( 0, copyRegion )
        );
        
        VK10.vkEndCommandBuffer( commandBuffer );
        
        VkSubmitInfo submitInfo = VkSubmitInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .pCommandBuffers( commandBufferBuffer );
        
        VK10.vkQueueSubmit( commandPool.getUsedQueueFamily().getQueue(), submitInfo, VK10.VK_NULL_HANDLE );
        commandPool.getUsedQueueFamily().waitForQueue();
        VK10.vkFreeCommandBuffers(
            getDeviceLogicalDevice(),
            commandPool.getAddress().getValue(),
            commandBufferBuffer
        );
    }
    
}
