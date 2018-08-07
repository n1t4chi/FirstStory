/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import org.joml.Vector4fc;
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
    private final VulkanSemaphore imageAvailableSemaphore;
    private Vector4fc backgroundColour = FirstOracleConstants.VECTOR_ZERO_4F;
    
    VulkanGraphicCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily,
        VulkanSwapChain swapChain,
        VulkanGraphicPipeline graphicPipeline,
        VulkanSemaphore imageAvailableSemaphore,
        VulkanSemaphore renderFinishedSemaphore
    ) {
        super( device, usedQueueFamily );
        this.swapChain = swapChain;
        this.graphicPipeline = graphicPipeline;
        this.renderFinishedSemaphore = renderFinishedSemaphore;
        this.imageAvailableSemaphore = imageAvailableSemaphore;
    }
    
    void setBackgroundColour( Vector4fc backgroundColour ) {
        this.backgroundColour = backgroundColour;
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
            backgroundColour,
            swapChain,
            this,
            index,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT
        );
    }
    
    @Override
    VulkanGraphicCommandBuffer extractNextCommandBuffer() {
        return getCommandBuffer( getDevice().aquireNextImageIndex() );
    }
    
    @Override
    void executeTearDown( VulkanGraphicCommandBuffer commandBuffer ) {
        presentQueue( swapChain, commandBuffer.getIndex() );
    }
    
    VkSubmitInfo createSubmitInfo( VulkanCommandBuffer currentCommandBuffer ) {
        return super.createSubmitInfo( currentCommandBuffer )
            .pSignalSemaphores( MemoryUtil.memAllocLong( 1 ).put(
                0, renderFinishedSemaphore.getAddress().getValue() ) )
            .waitSemaphoreCount( 1 )
            .pWaitDstStageMask( createWaitStageMaskBuffer() )
            .pWaitSemaphores( MemoryUtil.memAllocLong( 1 ).put(
                0, imageAvailableSemaphore.getAddress().getValue() ) )
        ;
    }
    
    @Override
    IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT );
    }
    
    private void presentQueue( VulkanSwapChain swapChain, int index ) {
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
