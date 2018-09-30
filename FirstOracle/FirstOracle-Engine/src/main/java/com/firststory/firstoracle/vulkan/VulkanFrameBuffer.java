/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanFrameBufferException;
import com.firststory.firstoracle.vulkan.rendering.VulkanRenderPass;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

public class VulkanFrameBuffer {
    
    private final VulkanAddress address;
    private final VulkanPhysicalDevice device;
    
    VulkanFrameBuffer(
        VulkanPhysicalDevice device,
        VulkanImageView imageView,
        VulkanRenderPass renderPass,
        VulkanSwapChain swapChain,
        VulkanDepthResources depthResources
    ) {
        this.device = device;
        this.address = createFrameBuffer( imageView, renderPass, swapChain, depthResources );
    }
    
    void dispose() {
        VK10.vkDestroyFramebuffer( device.getLogicalDevice(), address.getValue(), null );
    }
    
    public VulkanAddress getAddress() {
        return address;
    }
    
    private VulkanAddress createFrameBuffer(
        VulkanImageView imageView,
        VulkanRenderPass renderPass,
        VulkanSwapChain swapChain,
        VulkanDepthResources depthResources
    ) {
        return VulkanHelper.createAddress(
            () -> createFrameBufferCreateInfo( imageView, renderPass, swapChain, depthResources ),
            (createInfo, address) -> VK10.vkCreateFramebuffer(
                device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanFrameBufferException( device, resultCode )
        );
    }
    
    private VkFramebufferCreateInfo createFrameBufferCreateInfo(
        VulkanImageView imageView,
        VulkanRenderPass renderPass,
        VulkanSwapChain swapChain,
        VulkanDepthResources depthResources
    ) {
        return VkFramebufferCreateInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO )
            .renderPass( renderPass.getAddress().getValue() )
            .pAttachments( MemoryUtil.memAllocLong( 2 )
                .put( 0, imageView.getAddress().getValue() )
                .put( 1, depthResources.getDepthImageView().getAddress().getValue() )
            )
            .width( ( int ) swapChain.getWidth() )
            .height( ( int ) swapChain.getHeight() )
            .layers( 1 )
            .flags( 0 )
            .pNext( VK10.VK_NULL_HANDLE )
        ;
    }
    
}