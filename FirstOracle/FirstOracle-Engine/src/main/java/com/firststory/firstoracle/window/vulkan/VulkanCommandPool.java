/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanCommandBuffersException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanCommandPoolException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

class VulkanCommandPool {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandBuffer.class );
    private final long address;
    private final VulkanPhysicalDevice device;
    
    VulkanCommandPool( VulkanPhysicalDevice device ) {
        this.device = device;
        address = createCommandPool();
    }
    
    Map< Integer, VulkanCommandBuffer > createCommandBuffers() {
        Map< Integer, VulkanCommandBuffer > commandBuffers = new HashMap<>( device.getFrameBuffers().size() );
        PointerBuffer commandBufferBuffer = createCommandBufferBuffer();
        int iterator = 0;
        while ( commandBufferBuffer.hasRemaining() ) {
            commandBuffers.put( iterator, new VulkanCommandBuffer( device, commandBufferBuffer.get(), iterator ) );
            iterator++;
        }
        return commandBuffers;
    }
    
    private PointerBuffer createCommandBufferBuffer( ) {
        PointerBuffer commandBuffersBuffer = MemoryUtil.memAllocPointer( device.getFrameBuffers().size() );
        if( VK10.vkAllocateCommandBuffers(
                device.getLogicalDevice(), createAllocateInfo(), commandBuffersBuffer ) != VK10.VK_SUCCESS
        ) {
            throw new CannotAllocateVulkanCommandBuffersException( device );
        }
        return commandBuffersBuffer;
    }
    
    private VkCommandBufferAllocateInfo createAllocateInfo( ) {
        return VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .commandPool( address )
            .level( VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY )
            .commandBufferCount( device.getFrameBuffers().size() );
    }
    
    private long createCommandPool() {
        long[] address = new long[1];
        if( VK10.vkCreateCommandPool(
            device.getLogicalDevice(), createCommandPoolCreateInfo(), null, address ) != VK10.VK_SUCCESS
        ) {
            throw new CannotCreateVulkanCommandPoolException( device );
        }
        return address[0];
    }
    
    private VkCommandPoolCreateInfo createCommandPoolCreateInfo() {
        return VkCommandPoolCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO )
            .queueFamilyIndex( device.getGraphicFamily().getIndex() )
            .flags(
                //0
                //todo: when rendering is complete maybe they can raise performance:
                VK10.VK_COMMAND_POOL_CREATE_TRANSIENT_BIT | VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT
            );
    }
    
}
