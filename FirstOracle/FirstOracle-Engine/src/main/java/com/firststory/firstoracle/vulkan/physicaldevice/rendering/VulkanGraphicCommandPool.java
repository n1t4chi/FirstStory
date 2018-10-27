/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanQueueFamily;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSemaphore;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSwapChain;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandPool;
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
public class VulkanGraphicCommandPool extends VulkanCommandPool {
    
    private static final int DEFAULT_NEW_BUFFERS = 10;
    
    private final List< VulkanGraphicPrimaryCommandBuffer > primaryBuffers = new ArrayList<>(  );
    private final List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers = new ArrayList<>(  );
    
    private final Deque< VulkanGraphicPrimaryCommandBuffer > availablePrimaryBuffer = new LinkedList<>(  );
    private final Deque< VulkanGraphicSecondaryCommandBuffer > availableSecondaryBuffers = new LinkedList<>(  );
    
    public VulkanGraphicCommandPool(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily
    ) {
        super( allocator, device, usedQueueFamily );
    }
    
    public void disposeUnsafe() {
        disposeCommandBuffers();
        super.disposeUnsafe();
    }
    
    @Override
    protected void dispose( VulkanDeviceAllocator allocator ) {
        allocator.deregisterGraphicCommandPool( this );
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
    
    VkSubmitInfo createSubmitInfo(
        VulkanGraphicPrimaryCommandBuffer commandBuffer,
        VulkanSemaphore waitSemaphore,
        VulkanSemaphore signalSemaphore
    ) {
    
        return VkSubmitInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO )
            .pCommandBuffers( MemoryUtil.memAllocPointer( 1 )
                .put( 0, commandBuffer.getAddress().getValue() )
            )
            .pWaitDstStageMask( createWaitStageMaskBuffer() )
            .pWaitSemaphores( MemoryUtil.memAllocLong( 1 )
                .put( 0, waitSemaphore.getAddress().getValue() )
            )
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
