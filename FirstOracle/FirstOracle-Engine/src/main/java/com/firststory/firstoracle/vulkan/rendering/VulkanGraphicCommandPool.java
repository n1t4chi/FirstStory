/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.vulkan.*;
import com.firststory.firstoracle.vulkan.commands.VulkanCommandBuffer;
import com.firststory.firstoracle.vulkan.commands.VulkanCommandPool;
import com.firststory.firstoracle.vulkan.exceptions.CannotSubmitVulkanDrawCommandBufferException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
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
    
    private final List< VulkanGraphicPrimaryCommandBuffer > primaryBuffers = new ArrayList<>(  );
    private final List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers = new ArrayList<>(  );
    
    private final Deque< VulkanGraphicPrimaryCommandBuffer > availablePrimaryBuffer = new LinkedList<>(  );
    private final Deque< VulkanGraphicSecondaryCommandBuffer > availableSecondaryBuffers = new LinkedList<>(  );
    
    public VulkanGraphicCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily
    ) {
        super( device, usedQueueFamily );
    }
    
    public void dispose() {
        super.dispose();
        disposeCommandBuffers();
    }
    
    void resetPrimaryBuffers() {
        availablePrimaryBuffer.clear();
        availablePrimaryBuffer.addAll( primaryBuffers );
    }
    
    void resetSecondaryBuffers() {
        availableSecondaryBuffers.clear();
        availableSecondaryBuffers.addAll( secondaryBuffers );
    }
    
    VulkanGraphicPrimaryCommandBuffer provideNextPrimaryBuffer( VulkanSwapChain swapChain ) {
        var buffer = availablePrimaryBuffer.poll();
        if( buffer == null ) {
            buffer = createPrimaryCommandBuffer( swapChain );
            primaryBuffers.add( buffer );
        }
        return buffer;
    }
    
    Deque< VulkanGraphicSecondaryCommandBuffer > provideNextSecondaryBuffers( int size ) {
        var buffers = new LinkedList< VulkanGraphicSecondaryCommandBuffer >();
        provideNextSecondaryBuffer( size, buffers );
        return buffers;
    }
    
    void submitQueue(
        VulkanGraphicPrimaryCommandBuffer commandBuffer,
        List< VulkanSemaphore > waitSemaphores,
        VulkanSemaphore signalSemaphore
    ) {
        submitQueue( createSubmitInfo(
            commandBuffer,
            waitSemaphores,
            signalSemaphore
        ) );
    }
    
    private void submitQueue( VkSubmitInfo submitInfo ) {
        VulkanHelper.assertCallOrThrow( () -> VK10.vkQueueSubmit(
                getUsedQueueFamily().getQueue(),
                submitInfo,
                VK10.VK_NULL_HANDLE
            ),
            resultCode -> new CannotSubmitVulkanDrawCommandBufferException(
                this, resultCode, getUsedQueueFamily().getQueue()
            )
        );
    }
    
    private VulkanGraphicPrimaryCommandBuffer createPrimaryCommandBuffer( VulkanSwapChain swapChain ) {
        return new VulkanGraphicPrimaryCommandBuffer(
            getDevice(),
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) ),
            swapChain,
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT
        );
    }
    
    private List< VulkanGraphicSecondaryCommandBuffer > createSecondaryCommandBuffers( int size ) {
        List< VulkanGraphicSecondaryCommandBuffer > buffers = new ArrayList<>( size );
        VulkanHelper.iterate( createSecondaryCommandBufferBuffer( size ),
            ( index, address ) -> buffers.add( createSecondaryCommandBuffer( new VulkanAddress( address ) ) )
        );
        return buffers;
    }
    
    private VkSubmitInfo createSubmitInfo(
        VulkanGraphicPrimaryCommandBuffer commandBuffer,
        List< VulkanSemaphore > waitSemaphores,
        VulkanSemaphore signalSemaphore
    ) {
        var waitSemaphoresBuffer = MemoryUtil.memAllocLong( waitSemaphores.size() );
        for ( var i = 0; i < waitSemaphores.size(); i++ ) {
            waitSemaphoresBuffer.put( i, waitSemaphores.get( i ).getAddress().getValue() );
        }
    
        return VkSubmitInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .pCommandBuffers( MemoryUtil.memAllocPointer( 1 )
                .put( 0, commandBuffer.getAddress().getValue() )
            )
            .pWaitDstStageMask( createWaitStageMaskBuffer() )
            .pWaitSemaphores( waitSemaphoresBuffer )
            .pSignalSemaphores( MemoryUtil.memAllocLong( 1 )
                .put( 0, signalSemaphore.getAddress().getValue() )
            )
        ;
    }
    
    private IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT );
    }
    
    private void provideNextSecondaryBuffer(
        int size,
        LinkedList< VulkanGraphicSecondaryCommandBuffer > buffers
    ) {
        while ( buffers.size() < size ) {
            synchronized ( availableSecondaryBuffers ) {
                var buffer = availableSecondaryBuffers.poll();
                if ( buffer == null ) {
                    var newBuffers = createSecondaryCommandBuffers( Math.max( size, DEFAULT_NEW_BUFFERS ) );
                    availableSecondaryBuffers.addAll( newBuffers );
                    secondaryBuffers.addAll( newBuffers );
                    while ( buffers.size() < size ) {
                        buffers.add( availableSecondaryBuffers.poll() );
                    }
                } else {
                    buffers.add( buffer );
                }
            }
        }
    }
    
    private void disposeCommandBuffers() {
        primaryBuffers.forEach( VulkanCommandBuffer::close );
        secondaryBuffers.forEach( VulkanCommandBuffer::close );
        primaryBuffers.clear();
        secondaryBuffers.clear();
    }
    
    private VulkanGraphicSecondaryCommandBuffer createSecondaryCommandBuffer( VulkanAddress address ) {
        return new VulkanGraphicSecondaryCommandBuffer(
            getDevice(),
            address,
            this,
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT | VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT
        );
    }
}
