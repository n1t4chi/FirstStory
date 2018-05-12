/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.util.Map;

/**
 * @author n1t4chi
 */
class VulkanGraphicCommandPool extends VulkanCommandPool< VulkanGraphicCommandBuffer > {
    
    private final VulkanSwapChain swapChain;
    private final VulkanGraphicPipeline graphicPipeline;
    private final VulkanSemaphore renderFinishedSemaphore;
    
    VulkanGraphicCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily,
        VulkanSwapChain swapChain,
        VulkanGraphicPipeline graphicPipeline,
        VulkanSemaphore imageAvailableSemaphore,
        VulkanSemaphore renderFinishedSemaphore
    ) {
        super(
            device,
            usedQueueFamily,
            imageAvailableSemaphore
        );
        this.swapChain = swapChain;
        this.graphicPipeline = graphicPipeline;
        this.renderFinishedSemaphore = renderFinishedSemaphore;
    }
    
    @Override
    VulkanGraphicCommandBuffer createNewCommandBuffer(
        int index,
        VulkanAddress address,
        Map< Integer, VulkanFrameBuffer > frameBuffers
    ) {
        return new VulkanGraphicCommandBuffer(
            getDevice(),
            address,
            frameBuffers.get( index ),
            graphicPipeline,
            swapChain,
            this,
            index,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT
        );
    }
    
    @Override
    void postExecute( VulkanGraphicCommandBuffer commandBuffer ) {
        presentQueue( swapChain, commandBuffer.getIndex() );
    }
    
    VkSubmitInfo createSubmitInfo( VulkanCommandBuffer currentCommandBuffer ) {
        return super.createSubmitInfo( currentCommandBuffer )
            .pSignalSemaphores( MemoryUtil.memAllocLong( 1 ).put(
                0, renderFinishedSemaphore.getAddress().getValue() ) )
        ;
    }
    
    @Override
    IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT );
    }
    
    void presentQueue( VulkanSwapChain swapChain, int index ) {
        VkPresentInfoKHR presentInfo = VkPresentInfoKHR.create()
            .sType( KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR )
            .pWaitSemaphores(
                MemoryUtil.memAllocLong( 1 ).put( 0, renderFinishedSemaphore.getAddress().getValue() ) )
            .pSwapchains( MemoryUtil.memAllocLong( 1 ).put( 0, swapChain.getAddress().getValue() ) )
            .swapchainCount( 1 )
            .pImageIndices( MemoryUtil.memAllocInt( 1 ).put( 0, index ) )
            .pResults( null )
        ;
        KHRSwapchain.vkQueuePresentKHR( getUsedQueueFamily().getQueue() , presentInfo );
    }
}
