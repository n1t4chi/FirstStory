/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.vulkan.*;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanRenderPassException;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanRenderPass {
    private final VulkanPhysicalDevice device;
    private final VulkanAddress renderPass = VulkanAddress.createNull();
    
     VulkanRenderPass( VulkanPhysicalDevice device ) {
        this.device = device;
    }
    
    public void dispose() {
        if( renderPass.isNotNull() ) {
            VK10.vkDestroyRenderPass( device.getLogicalDevice(), renderPass.getValue(), null );
            renderPass.setNull();
        }
    }
    
    public VulkanAddress getAddress() {
        return renderPass;
    }
    
    void updateRenderPass( VulkanSwapChain swapChain, VulkanDepthResources depthResources, boolean isBackground ) {
        VulkanHelper.updateAddress( renderPass,
            (address) -> VK10.vkCreateRenderPass(
                device.getLogicalDevice(), createRenderPassCreateInfo( swapChain, depthResources, isBackground ), null, address ),
            resultCode -> new CannotCreateVulkanRenderPassException( device, resultCode )
        );
    }
    
    private VkRenderPassCreateInfo createRenderPassCreateInfo(
        VulkanSwapChain swapChain,
        VulkanDepthResources depthResources,
        boolean isBackground
    ) {
        return VkRenderPassCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO )
            .pAttachments( VkAttachmentDescription.calloc( 2 )
                .put( 0, createColourAttachmentDescription( swapChain, isBackground ) )
                .put( 1, createDepthAttachmentDescription( depthResources ) )
            )
            .pSubpasses( VkSubpassDescription.create( 1 )
                .put( 0, createSubpassDescription() )
            )
            .pDependencies( VkSubpassDependency.calloc( 1 )
                .put( 0, createSubpassDependency() )
            )
        ;
    }
    
    private VkAttachmentDescription createDepthAttachmentDescription( VulkanDepthResources depthResources ) {
        return createAttachmentDescription(
            depthResources.getDepthFormat().getFormat(),
            VK10.VK_ATTACHMENT_LOAD_OP_CLEAR,
            VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE,
            VK10.VK_ATTACHMENT_LOAD_OP_CLEAR,
            VK10.VK_IMAGE_LAYOUT_UNDEFINED,
            VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL
        );
    }
    
    private VkAttachmentDescription createColourAttachmentDescription( VulkanSwapChain swapChain, boolean isBackground ) {
        return createAttachmentDescription(
            swapChain.getImageFormat(),
            isBackground ? VK10.VK_ATTACHMENT_LOAD_OP_CLEAR : VK10.VK_ATTACHMENT_LOAD_OP_LOAD,
            VK10.VK_ATTACHMENT_STORE_OP_STORE,
            isBackground ? VK10.VK_ATTACHMENT_LOAD_OP_CLEAR : VK10.VK_ATTACHMENT_LOAD_OP_LOAD,
            isBackground ? VK10.VK_IMAGE_LAYOUT_UNDEFINED : KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR,
            KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR
        );
    }
    
    private VkAttachmentDescription createAttachmentDescription(
        int imageFormat,
        int loadOperation,
        int storeOperation,
        int stencilLoadOperation,
        int initialLayout,
        int finalLayout
    ) {
        return VkAttachmentDescription.create()
            .format( imageFormat )
            .samples( VK10.VK_SAMPLE_COUNT_1_BIT )
            .loadOp( loadOperation )
            .storeOp( storeOperation )
            .stencilLoadOp( stencilLoadOperation )
            .stencilStoreOp( VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE )
            .initialLayout( initialLayout )
            .finalLayout( finalLayout );
    }
    
    private VkSubpassDependency createSubpassDependency() {
        return VkSubpassDependency.create()
            .srcSubpass( VK10.VK_SUBPASS_EXTERNAL )
            .dstSubpass( 0 )
            .srcStageMask( VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT )
            .dstStageMask( VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT )
            .srcAccessMask( 0 )
            .dstAccessMask( VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT );
    }
    
    private VkSubpassDescription createSubpassDescription() {
        return VkSubpassDescription.create()
            .pipelineBindPoint( VK10.VK_PIPELINE_BIND_POINT_GRAPHICS )
            .colorAttachmentCount( 1 )
            .pColorAttachments( VkAttachmentReference.create( 1 ).put( 0, createColourAttachmentReference() ) )
            .pDepthStencilAttachment( createDepthAttachmentReference() )
            ;
    }
    
    private VkAttachmentReference createColourAttachmentReference() {
        return createAttachmentReference( 0, VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL );
    }
    
    private VkAttachmentReference createDepthAttachmentReference() {
        return createAttachmentReference( 1, VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL );
    }
    
    private VkAttachmentReference createAttachmentReference( int index, int layout ) {
        return VkAttachmentReference.create().attachment( index ).layout( layout );
    }
}
