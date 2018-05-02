/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanFrameBufferException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

class VulkanFrameBuffer {
    
    private final VulkanAddress address;
    private final VulkanPhysicalDevice device;
    
    VulkanFrameBuffer(
        VulkanPhysicalDevice device,
        VulkanImageView imageView,
        VulkanGraphicPipeline graphicPipeline,
        VulkanSwapChain swapChain
    ) {
        this.device = device;
        this.address = createFrameBuffer( imageView, graphicPipeline, swapChain );
    }
    
    void dispose() {
        VK10.vkDestroyFramebuffer( device.getLogicalDevice(), address.getValue(), null );
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    private VulkanAddress createFrameBuffer(
        VulkanImageView imageView, VulkanGraphicPipeline graphicPipeline, VulkanSwapChain swapChain
    ) {
        return VulkanHelper.createAddress(
            () -> createFrameBufferCreateInfo( imageView, graphicPipeline, swapChain ),
            (createInfo, address) -> VK10.vkCreateFramebuffer(
                device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanFrameBufferException( device, resultCode )
        );
    }
    
    private VkFramebufferCreateInfo createFrameBufferCreateInfo(
        VulkanImageView imageView, VulkanGraphicPipeline graphicPipeline, VulkanSwapChain swapChain
    ) {
        return VkFramebufferCreateInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO )
            .renderPass( graphicPipeline.getRenderPass() )
            .pAttachments( MemoryUtil.memAllocLong( 1 ).put( 0, imageView.getAddress().getValue() ) )
            .width( ( int ) swapChain.getWidth() )
            .height( ( int ) swapChain.getHeight() )
            .layers( 1 )
            .flags( 0 )
            .pNext( VK10.VK_NULL_HANDLE )
        ;
    }
    
}
