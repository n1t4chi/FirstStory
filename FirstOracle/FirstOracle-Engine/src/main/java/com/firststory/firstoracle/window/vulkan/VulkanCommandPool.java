/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanCommandBuffersException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanCommandPoolException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotSubmitVulkanDrawCommandBufferException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class VulkanCommandPool {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandPool.class );
    private VulkanAddress address;
    private final VulkanPhysicalDevice device;
    private final VulkanQueueFamily usedQueueFamily;
    private final Map< Integer, VulkanCommandBuffer > commandBuffers = new HashMap<>(  );
    private final VulkanSemaphore imageAvailableSemaphore;
    private final VulkanSemaphore renderFinishedSemaphore;
    
    public VulkanQueueFamily getUsedQueueFamily() {
        return usedQueueFamily;
    }
    
    VulkanCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily,
        VulkanSemaphore imageAvailableSemaphore,
        VulkanSemaphore renderFinishedSemaphore
    ) {
        this.device = device;
        this.usedQueueFamily = usedQueueFamily;
        this.imageAvailableSemaphore = imageAvailableSemaphore;
        this.renderFinishedSemaphore = renderFinishedSemaphore;
        address = createCommandPool();
    }
    
    void dispose() {
        disposeCommandBuffers();
        if( !address.isNull() ){
            VK10.vkDestroyCommandPool( device.getLogicalDevice(), address.getValue(), null );
            address.setNull();
        }
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    VulkanCommandBuffer getCommandBuffer( int index ) {
        return commandBuffers.get( index );
    }
    
    void refreshCommandBuffers(
        Map< Integer, VulkanFrameBuffer > frameBuffers,
        VulkanGraphicPipeline graphicPipeline,
        VulkanSwapChain swapChain
    ) {
        disposeCommandBuffers();
        VulkanHelper.iterate( createCommandBufferBuffer( createAllocateInfo( frameBuffers ), frameBuffers.size() ),
            ( index, commandBufferAddress ) -> commandBuffers.put(
                index,
                new VulkanCommandBuffer(
                    device,
                    commandBufferAddress,
                    frameBuffers.get( index ),
                    graphicPipeline,
                    swapChain,
                    VulkanCommandPool.this
                )
            )
        );
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
        KHRSwapchain.vkQueuePresentKHR( usedQueueFamily.getQueue() , presentInfo );
    }
    
     void submitQueue( VulkanCommandBuffer currentCommandBuffer ) {
        VkSubmitInfo submitInfo = VkSubmitInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .waitSemaphoreCount( 1 )
            .pWaitSemaphores( MemoryUtil.memAllocLong( 1 ).put(
                0, imageAvailableSemaphore.getAddress().getValue() ) )
            .pWaitDstStageMask(
                MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT ) )
            .pCommandBuffers(
                MemoryUtil.memAllocPointer( 1 ).put(
                    0, currentCommandBuffer.getAddress().getValue() ) )
            .pSignalSemaphores( MemoryUtil.memAllocLong( 1 ).put(
                0, renderFinishedSemaphore.getAddress().getValue() ) )
            ;
        VulkanHelper.assertCallOrThrow(
            ()-> VK10.vkQueueSubmit( usedQueueFamily.getQueue(), submitInfo, VK10.VK_NULL_HANDLE ),
            resultCode -> new CannotSubmitVulkanDrawCommandBufferException( this, resultCode, usedQueueFamily.getQueue() )
        );
    }
    
    private void disposeCommandBuffers() {
        commandBuffers.forEach( ( integer, vulkanCommandBuffer ) -> vulkanCommandBuffer.close() );
        commandBuffers.clear();
    }
    
    PointerBuffer createCommandBufferBuffer(
        VkCommandBufferAllocateInfo allocateInfo, int size
    ) {
        PointerBuffer commandBuffersBuffer = MemoryUtil.memAllocPointer( size );
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkAllocateCommandBuffers(
                device.getLogicalDevice(), allocateInfo, commandBuffersBuffer ),
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
            address -> VK10.vkCreateCommandPool(
                device.getLogicalDevice(), createCommandPoolCreateInfo(), null, address ),
            resultCode -> new CannotCreateVulkanCommandPoolException( device, resultCode )
        );
    }
    
    private VkCommandPoolCreateInfo createCommandPoolCreateInfo() {
        return VkCommandPoolCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO )
            .queueFamilyIndex( usedQueueFamily.getIndex() )
            .flags(
                //0
                //todo: when rendering is complete maybe they can raise performance:
                VK10.VK_COMMAND_POOL_CREATE_TRANSIENT_BIT | VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT
            );
    }
    
}
