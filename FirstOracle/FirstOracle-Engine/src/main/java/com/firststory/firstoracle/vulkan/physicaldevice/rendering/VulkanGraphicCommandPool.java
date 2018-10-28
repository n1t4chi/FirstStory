/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.allocators.VulkanCommandBufferAllocator;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanQueueFamily;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSemaphore;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandPool;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;

/**
 * @author n1t4chi
 */
public class VulkanGraphicCommandPool extends VulkanCommandPool {
    private final VulkanCommandBufferAllocator< VulkanGraphicPrimaryCommandBuffer > primaryBufferAllocator;
    private final VulkanCommandBufferAllocator< VulkanGraphicSecondaryCommandBuffer > secondaryBufferAllocator;
    
    public VulkanGraphicCommandPool(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily
    ) {
        super( allocator, device, usedQueueFamily );
        primaryBufferAllocator = allocator.createBufferAllocator( this::createPrimaryCommandBuffer );
        secondaryBufferAllocator = allocator.createBufferAllocator( this::createSecondaryCommandBuffer );
    }
    
    public void disposeUnsafe() {
        primaryBufferAllocator.dispose();
        secondaryBufferAllocator.dispose();
        super.disposeUnsafe();
    }
    
    @Override
    protected void dispose( VulkanDeviceAllocator allocator ) {
        allocator.deregisterGraphicCommandPool( this );
    }
    
    VulkanGraphicPrimaryCommandBuffer provideNextPrimaryBuffer() {
        return primaryBufferAllocator.createBuffer();
    }
    
    VulkanGraphicSecondaryCommandBuffer provideNextSecondaryBuffer() {
        return secondaryBufferAllocator.createBuffer();
    }
    
    private VulkanGraphicPrimaryCommandBuffer createPrimaryCommandBuffer() {
        return new VulkanGraphicPrimaryCommandBuffer(
            primaryBufferAllocator,
            getDevice(),
            this,
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) ),
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT
        );
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
    
    private VulkanGraphicSecondaryCommandBuffer createSecondaryCommandBuffer() {
        return new VulkanGraphicSecondaryCommandBuffer(
            secondaryBufferAllocator,
            getDevice(),
            this,
            new VulkanAddress( createSecondaryCommandBufferBuffer( 1 ).get( 0 ) ),
            VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT | VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT
        );
    }
    
    private IntBuffer createWaitStageMaskBuffer() {
        return MemoryUtil.memAllocInt( 1 ).put( 0, VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT );
    }
}
