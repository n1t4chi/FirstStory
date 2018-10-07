/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.vulkan.*;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class VulkanGraphicCommandPool extends VulkanCommandPool< VulkanGraphicPrimaryCommandBuffer > {
    
    private final VulkanSwapChain swapChain;
    private final VulkanSemaphore renderFinishedSemaphore;
    private final VulkanSemaphore imageAvailableSemaphore;
    private final Map< Integer, VulkanGraphicPrimaryCommandBuffer > commandBuffers = new HashMap<>(  );
    private Colour backgroundColour = FirstOracleConstants.BLACK;
    
    public VulkanGraphicCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily,
        VulkanSwapChain swapChain,
        VulkanSemaphore imageAvailableSemaphore
    ) {
        super( device, usedQueueFamily );
        this.swapChain = swapChain;
        renderFinishedSemaphore = new VulkanSemaphore( device );
        this.imageAvailableSemaphore = imageAvailableSemaphore;
    }
    
    public void dispose() {
        super.dispose();
        disposeCommandBuffers();
        renderFinishedSemaphore.dispose();
    }
    private void disposeCommandBuffers() {
        commandBuffers.forEach( ( integer, vulkanCommandBuffer ) -> vulkanCommandBuffer.close() );
        commandBuffers.clear();
    }
    
    public void setBackgroundColour( Colour backgroundColour ) {
        this.backgroundColour = backgroundColour;
    }
    
    public VulkanGraphicPrimaryCommandBuffer createNewPrimaryCommandBuffer(
        int index,
        VulkanAddress address,
        Map< Integer, VulkanFrameBuffer > frameBuffers
    ) {
        return new VulkanGraphicPrimaryCommandBuffer(
            getDevice(),
            address,
            frameBuffers.get( index ),
            swapChain,
            this,
            index,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT
        );
    }
    public VulkanGraphicSecondaryCommandBuffer createNewSecondaryCommandBuffer(
        VulkanGraphicPrimaryCommandBuffer primaryBuffer,
        VulkanRenderPass renderPass,
        VulkanAddress address
    ) {
        return new VulkanGraphicSecondaryCommandBuffer(
            getDevice(),
            address,
            primaryBuffer,
            this,
            renderPass,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT | VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT
        );
    }
    
    public VulkanGraphicPrimaryCommandBuffer getCommandBuffer( int index ) {
        return commandBuffers.get( index );
    }
    
    public void refreshCommandBuffers( Map< Integer, VulkanFrameBuffer > frameBuffers ) {
        disposeCommandBuffers();
        VulkanHelper.iterate( createPrimaryCommandBufferBuffer( frameBuffers.size() ),
            ( index, address ) -> commandBuffers.put( index,
                createNewPrimaryCommandBuffer( index, new VulkanAddress( address ) , frameBuffers )
            )
        );
    }
    
    public List< VulkanGraphicSecondaryCommandBuffer > createSecondaryCommandBuffers( VulkanGraphicPrimaryCommandBuffer buffer, VulkanRenderPass renderPass, int size ) {
        List< VulkanGraphicSecondaryCommandBuffer > buffers = new ArrayList<>( size );
        VulkanHelper.iterate( createSecondaryCommandBufferBuffer( size ),
            ( index, address ) -> buffers.add( createNewSecondaryCommandBuffer( buffer, renderPass, new VulkanAddress( address ) ) )
        );
        return buffers;
    }
    
    @Override
    public VulkanGraphicPrimaryCommandBuffer extractNextCommandBuffer() {
        return getCommandBuffer( getDevice().acquireCurrentImageIndex() );
    }
    
    @Override
    public void executeTearDown( VulkanGraphicPrimaryCommandBuffer commandBuffer ) {
        presentQueue( swapChain, commandBuffer.getIndex() );
    }
    
    @Override
    public VkSubmitInfo createSubmitInfo( VulkanCommandBuffer... currentCommandBuffer ) {
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
    public IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT );
    }
    
    private void presentQueue( VulkanSwapChain swapChain, int index ) {
        var presentInfo = VkPresentInfoKHR.create()
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
