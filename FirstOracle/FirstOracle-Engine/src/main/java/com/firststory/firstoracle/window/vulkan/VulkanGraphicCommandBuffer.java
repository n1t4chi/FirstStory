/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
class VulkanGraphicCommandBuffer extends VulkanCommandBuffer {
    
    private final VulkanGraphicPipeline graphicsPipeline;
    private final VkRenderPassBeginInfo renderPassBeginInfo;
    private final VulkanFrameBuffer frameBuffer;
    private final int index;
    
    VulkanGraphicCommandBuffer(
        VulkanPhysicalDevice device,
        VulkanAddress address,
        VulkanFrameBuffer frameBuffer,
        VulkanGraphicPipeline graphicsPipeline,
        VulkanSwapChain swapChain,
        VulkanCommandPool commandPool, int index, int... usedBeginInfoFlags
    ) {
        super( device, address, commandPool, usedBeginInfoFlags );
        this.frameBuffer = frameBuffer;
        this.graphicsPipeline = graphicsPipeline;
        this.index = index;
        renderPassBeginInfo = createRenderPassBeginInfo( graphicsPipeline, swapChain );
    }
    
    public int getIndex() {
        return index;
    }
    
    @Override
    void fillQueueSetup() {
        super.fillQueueSetup();
        beginRenderPassForCommandBuffer();
        bindPipeline( graphicsPipeline );
    }
    
    @Override
    void fillQueueTearDown() {
        endRenderPass();
        super.fillQueueTearDown();
    }
    
    void bindDescriptorSets( VulkanAddress descriptorSet ) {
        VK10.vkCmdBindDescriptorSets( getCommandBuffer(),
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicsPipeline.getPipelineLayout().getValue(),
            0,
            MemoryUtil.memAllocLong( 1 ).put( 0, descriptorSet.getValue() ),
            null
        );
    }
    
    private VkRenderPassBeginInfo createRenderPassBeginInfo(
        VulkanGraphicPipeline graphicPipeline, VulkanSwapChain swapChain
    ) {
        return VkRenderPassBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO )
            .renderPass( graphicPipeline.getRenderPass().getValue() )
            .framebuffer( frameBuffer.getAddress().getValue() )
            .renderArea( createRenderArea( swapChain ) )
            .pClearValues( createClearValue() );
    }
    
    private void beginRenderPassForCommandBuffer() {
        VK10.vkCmdBeginRenderPass( getCommandBuffer(), renderPassBeginInfo, VK10.VK_SUBPASS_CONTENTS_INLINE );
    }
    
    private void endRenderPass() {
        VK10.vkCmdEndRenderPass( getCommandBuffer() );
    }
    
    private void bindPipeline( VulkanGraphicPipeline graphicPipeline ) {
        VK10.vkCmdBindPipeline(
            getCommandBuffer(),
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicPipeline.getGraphicPipeline().getValue()
        );
    }
    
    private VkRect2D createRenderArea( VulkanSwapChain swapChain ) {
        return VkRect2D.create()
            .offset( VkOffset2D.create().set( 0, 0 ) )
            .extent( swapChain.getExtent() );
    }
    
    private VkClearValue.Buffer createClearValue() {
        return VkClearValue.create( 2 )
            .put( 0, createClearColour() )
            .put( 1, createDepthStencil() )
        ;
    }
    
    private VkClearValue createDepthStencil() {
        return VkClearValue.create().depthStencil( VkClearDepthStencilValue.create().depth( 1f ).stencil( 0 ) );
    }
    
    private VkClearValue createClearColour() {
        return VkClearValue.create().color( VkClearColorValue.create()
            .float32( 0, 0f )
            .float32( 1, 0f )
            .float32( 2, 0f )
            .float32( 3, 1f )
        );
    }
}
