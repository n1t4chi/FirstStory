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

import java.util.Map;
import java.util.logging.Logger;

class VulkanCommandPool {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandBuffer.class );
    private VulkanAddress address = VulkanAddress.NULL;
    private final VulkanPhysicalDevice device;
    
    VulkanCommandPool( VulkanPhysicalDevice device ) {
        this.device = device;
        address = createCommandPool();
    }
    
    void dispose() {
        if( !address.isNull() ){
            VK10.vkDestroyCommandPool( device.getLogicalDevice(), address.getValue(), null );
            address.setNull();
        }
    }
    
    public VulkanAddress getAddress() {
        return address;
    }
    
    void refreshCommandBuffers(
        Map< Integer, VulkanCommandBuffer > commandBuffers,
        Map< Integer, VulkanFrameBuffer > frameBuffers,
        VulkanGraphicPipeline graphicPipeline,
        VulkanSwapChain swapChain
    ) {
        PointerBuffer commandBufferBuffer = createCommandBufferBuffer( frameBuffers );
        int iterator = 0;
        while ( commandBufferBuffer.hasRemaining() ) {
            commandBuffers.put(
                iterator,
                new VulkanCommandBuffer(
                    device,
                    commandBufferBuffer.get(),
                    frameBuffers.get( iterator ),
                    graphicPipeline,
                    swapChain,
                    this
                )
            );
            iterator++;
        }
    }
    
    private PointerBuffer createCommandBufferBuffer( Map< Integer, VulkanFrameBuffer > frameBuffers ) {
        PointerBuffer commandBuffersBuffer = MemoryUtil.memAllocPointer( frameBuffers.size() );
        VulkanHelper.assertCallAndThrow(
            () -> VK10.vkAllocateCommandBuffers(
                device.getLogicalDevice(), createAllocateInfo( frameBuffers ), commandBuffersBuffer ),
            resultCode -> new CannotAllocateVulkanCommandBuffersException( device, resultCode )
        );
        return commandBuffersBuffer;
    }
    
    private VkCommandBufferAllocateInfo createAllocateInfo( Map< Integer, VulkanFrameBuffer > frameBuffers ) {
        return VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .commandPool( address.getValue() )
            .level( VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY )
            .commandBufferCount( frameBuffers.size() );
    }
    
    private VulkanAddress createCommandPool() {
        return VulkanHelper.createAddress(
            (address)-> VK10.vkCreateCommandPool(
                device.getLogicalDevice(), createCommandPoolCreateInfo(), null, address ),
            resultCode -> new CannotCreateVulkanCommandPoolException( device, resultCode )
        );
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
