/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.vulkan.*;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanGraphicCommandPool extends VulkanCommandPool< VulkanGraphicPrimaryCommandBuffer > {
    
    public static final int DEFAULT_NEW_BUFFERS = 10;
    private final VulkanSwapChain swapChain;
    private final VulkanSemaphore renderFinishedSemaphore;
    private final VulkanSemaphore imageAvailableSemaphore;
    
    private final List< VulkanGraphicPrimaryCommandBuffer > primaryBuffers = new ArrayList<>(  );
    private final List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers = new ArrayList<>(  );
    
    private final Deque< VulkanGraphicPrimaryCommandBuffer > availablePrimaryBuffer = new LinkedList<>(  );
    private final Deque< VulkanGraphicSecondaryCommandBuffer > availableSecondaryBuffers = new LinkedList<>(  );
    
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
    
    public void resetPrimaryBuffers() {
        availablePrimaryBuffer.clear();
        availablePrimaryBuffer.addAll( primaryBuffers );
    }
    
    public void resetSecondaryBuffers() {
        availableSecondaryBuffers.clear();
        availableSecondaryBuffers.addAll( secondaryBuffers );
    }
    
    public VulkanGraphicPrimaryCommandBuffer provideNextPrimaryBuffer() {
        var buffer = availablePrimaryBuffer.poll();
        if( buffer == null ) {
            buffer = createPrimaryCommandBuffer();
            primaryBuffers.add( buffer );
        }
        return buffer;
    }
    
    public synchronized Deque< VulkanGraphicSecondaryCommandBuffer > provideNextSecondaryBuffers( int size ) {
        var buffers = new LinkedList<VulkanGraphicSecondaryCommandBuffer>();
        for ( var i = 0; i < size ; i++ ) {
            buffers.add( provideNextSecondaryBuffer() );
        }
        return buffers;
    }
    
    private VulkanGraphicSecondaryCommandBuffer provideNextSecondaryBuffer() {
        var buffer = availableSecondaryBuffers.poll();
        if( buffer == null ) {
            var buffers = createSecondaryCommandBuffers( DEFAULT_NEW_BUFFERS );
            availableSecondaryBuffers.addAll( buffers );
            secondaryBuffers.addAll( buffers );
            buffer = availableSecondaryBuffers.poll();
        }
        return buffer;
    }
    
    private void disposeCommandBuffers() {
        primaryBuffers.forEach( VulkanCommandBuffer::close );
        secondaryBuffers.forEach( VulkanCommandBuffer::close );
        primaryBuffers.clear();
        secondaryBuffers.clear();
    }
    
    public VulkanGraphicPrimaryCommandBuffer createPrimaryCommandBuffer() {
        return new VulkanGraphicPrimaryCommandBuffer(
            getDevice(),
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) ),
            swapChain,
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT
        );
    }
    
    public List< VulkanGraphicSecondaryCommandBuffer > createSecondaryCommandBuffers( int size ) {
        List< VulkanGraphicSecondaryCommandBuffer > buffers = new ArrayList<>( size );
        VulkanHelper.iterate( createSecondaryCommandBufferBuffer( size ),
            ( index, address ) -> buffers.add( createSecondaryCommandBuffer( new VulkanAddress( address ) ) )
        );
        return buffers;
    }
    
    private VulkanGraphicSecondaryCommandBuffer createSecondaryCommandBuffer( VulkanAddress address ) {
        return new VulkanGraphicSecondaryCommandBuffer(
            getDevice(),
            address,
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT | VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT
        );
    }
    
    public void executeTearDown( int index ) {
        presentQueue( swapChain, index );
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
