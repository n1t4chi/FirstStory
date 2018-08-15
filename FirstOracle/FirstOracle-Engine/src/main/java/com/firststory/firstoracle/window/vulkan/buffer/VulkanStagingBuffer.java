/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.window.vulkan.VulkanTransferCommandPool;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCopy;

/**
 * @author n1t4chi
 */
public class VulkanStagingBuffer extends VulkanMappableBuffer {
    
    private static final int[] STAGING_BUFFER_USAGE_FLAGS = { VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT };
    private static final int[] STAGING_BUFFER_MEMORY_FLAGS = { VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT,
        VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    };
    
    public VulkanStagingBuffer( VulkanPhysicalDevice device ) {
        super( device, STAGING_BUFFER_USAGE_FLAGS, STAGING_BUFFER_MEMORY_FLAGS );
    }
    public void copyBuffer( VulkanDataBuffer dstBuffer, VulkanTransferCommandPool commandPool ) {
        commandPool.executeQueue( commandBuffer -> {
            VkBufferCopy copyRegion = VkBufferCopy.create().srcOffset( 0 ) // Optional
                .dstOffset( 0 ) // Optional
                .size( dstBuffer.getDataSizeInBytes() );
    
            VK10.vkCmdCopyBuffer( commandBuffer.getCommandBuffer(),
                this.getBufferAddress().getValue(),
                dstBuffer.getBufferAddress().getValue(),
                VkBufferCopy.create( 1 ).put( 0, copyRegion )
            );
        } );
    }
    
}
