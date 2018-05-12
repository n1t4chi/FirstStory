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

/**
 * @author n1t4chi
 */
class VulkanGraphicCommandPool extends VulkanCommandPool {
    
    private final VulkanSemaphore renderFinishedSemaphore;
    
    VulkanGraphicCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily,
        VulkanSemaphore imageAvailableSemaphore,
        VulkanSemaphore renderFinishedSemaphore
    ) {
        super(
            device,
            usedQueueFamily,
            imageAvailableSemaphore,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT
        );
        this.renderFinishedSemaphore = renderFinishedSemaphore;
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
