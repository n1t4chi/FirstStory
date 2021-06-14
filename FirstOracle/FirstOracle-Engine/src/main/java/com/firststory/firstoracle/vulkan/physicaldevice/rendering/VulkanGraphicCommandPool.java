/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.allocators.VulkanCommandBufferAllocator;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandPool;
import org.lwjgl.vulkan.VK10;

/**
 * @author n1t4chi
 */
public class VulkanGraphicCommandPool extends VulkanCommandPool {
    private final VulkanCommandBufferAllocator< VulkanGraphicPrimaryCommandBuffer > primaryBufferAllocator;
    private final VulkanCommandBufferAllocator< VulkanGraphicSecondaryCommandBuffer > secondaryBufferAllocator;
    
    public VulkanGraphicCommandPool(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        super( allocator, device, device.getGraphicQueueFamily() );
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
            new VulkanAddress( createPrimaryCommandBufferBuffer( 1 ).get( 0 ) )
        );
    }
    
    private VulkanGraphicSecondaryCommandBuffer createSecondaryCommandBuffer() {
        return new VulkanGraphicSecondaryCommandBuffer(
            secondaryBufferAllocator,
            getDevice(),
            this,
            new VulkanAddress( createSecondaryCommandBufferBuffer( 1 ).get( 0 ) ),
            VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT
        );
    }
}
