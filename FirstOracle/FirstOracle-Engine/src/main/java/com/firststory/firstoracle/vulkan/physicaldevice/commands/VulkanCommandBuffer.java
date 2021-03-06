/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.commands;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.allocators.VulkanCommandBufferAllocator;
import com.firststory.firstoracle.vulkan.exceptions.VulkanCommandBufferException;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandBufferInheritanceInfo;

import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public abstract class VulkanCommandBuffer< SELF extends VulkanCommandBuffer< ? > > {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandBuffer.class );
    private final VulkanCommandBufferAllocator< SELF > allocator;
    private final VulkanPhysicalDevice device;
    private final VulkanAddress address;
    private final VkCommandBuffer commandBuffer;
    private final VulkanCommandPool commandPool;
    private final int[] usedBeginInfoFlags;
    
    public VulkanCommandBuffer(
        VulkanCommandBufferAllocator< SELF > allocator,
        VulkanPhysicalDevice device,
        VulkanCommandPool commandPool,
        VulkanAddress address,
        int... usedBeginInfoFlags
    ) {
        this.allocator = allocator;
        this.device = device;
        this.address = address;
        this.commandPool = commandPool;
        this.usedBeginInfoFlags = usedBeginInfoFlags;
        commandBuffer = createCommandBuffer();
    }
    
    public VkCommandBuffer getCommandBuffer() {
        return commandBuffer;
    }
    
    public void fillQueueSetup() {
        beginRecordingCommandBuffer();
    }
    
    public void fillQueueTearDown() {
        endCommandBuffer();
    }
    
    @SuppressWarnings( "unchecked" )
    public void dispose() {
        allocator.deregisterBuffer( (SELF) this );
    }
    
    public void update() {
        resetCommandBuffer();
    }
    
    public void disposeUnsafe() {
        VK10.vkFreeCommandBuffers( device.getLogicalDevice(), commandPool.getAddress().getValue(), commandBuffer );
    }
    
    public VulkanAddress getAddress(){
        return address;
    }
    
    protected VkCommandBufferInheritanceInfo createInheritanceInfo() {
        return null;
    }
    
    private void resetCommandBuffer() {
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkResetCommandBuffer( commandBuffer, VK10.VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT ),
            resultCode -> {
                logger.warning( "Failed to reset command buffer!" );
                return new VulkanCommandBufferException( device, this, "Failed to reset command buffer" );
            }
        );
    }
    
    private void beginRecordingCommandBuffer() {
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkBeginCommandBuffer( commandBuffer, createBeginInfo() ),
            resultCode -> {
                logger.warning( "Failed to begin command buffer!" );
                return new VulkanCommandBufferException( device, this, "Failed to begin command buffer" );
            }
        );
    }
    
    private void endCommandBuffer() {
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkEndCommandBuffer( commandBuffer ),
            resultCode -> {
                logger.warning( "Failed to end command buffer!" );
                return new VulkanCommandBufferException( device, this, "Failed to end command buffer" );
            }
        );
    }
    
    private VkCommandBuffer createCommandBuffer() {
        return new VkCommandBuffer( address.getValue(), device.getLogicalDevice() );
    }
    
    private VkCommandBufferBeginInfo createBeginInfo() {
        return VkCommandBufferBeginInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO )
            .flags( VulkanHelper.flagsToInt( usedBeginInfoFlags ) )
            .pInheritanceInfo( createInheritanceInfo() );
    }
}
