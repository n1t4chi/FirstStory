/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanFrameBufferException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

class VulkanFrameBuffer implements AutoCloseable {
    
    private final long address;
    private final VulkanPhysicalDevice device;
    
    VulkanFrameBuffer( VulkanPhysicalDevice device, VulkanImageView imageView ) {
        this.device = device;
        this.address = createFrameBuffer( imageView );
    }
    
    @Override
    public void close() {
        VK10.vkDestroyFramebuffer( device.getLogicalDevice(), address, null );
    }
    
    long getAddress() {
        return address;
    }
    
    private long createFrameBuffer( VulkanImageView imageView ) {
        long[] address = new long[1];
        VkFramebufferCreateInfo frameBufferCreateInfo = createFrameBufferCreateInfo( imageView );
        if( VK10.vkCreateFramebuffer( device.getLogicalDevice(), frameBufferCreateInfo, null, address ) != VK10.VK_SUCCESS ) {
            throw new CannotCreateVulkanFrameBufferException( device );
        }
        return address[0];
    }
    
    private VkFramebufferCreateInfo createFrameBufferCreateInfo( VulkanImageView imageView ) {
        return VkFramebufferCreateInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO )
            .renderPass( device.getGraphicPipeline().getRenderPass() )
            .pAttachments( MemoryUtil.memAllocLong( 1 ).put( 0, imageView.getAddress() ) )
            .width( ( int ) device.getSwapChain().getWidth() )
            .height( ( int ) device.getSwapChain().getHeight() )
            .layers( 1 )
            .flags( 0 )
            .pNext( VK10.VK_NULL_HANDLE )
        ;
    }
    
}
