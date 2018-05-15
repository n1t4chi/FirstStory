/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.window.vulkan.exceptions.VulkanCommandBufferException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;

import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public abstract class VulkanCommandBuffer {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandBuffer.class );
    private final VulkanPhysicalDevice device;
    private final VulkanAddress address;
    private final VkCommandBuffer commandBuffer;
    private final VkCommandBufferBeginInfo beginInfo;
    private final VulkanCommandPool commandPool;
    private final int[] usedBeginInfoFlags;
    
    VulkanCommandBuffer(
        VulkanPhysicalDevice device,
        VulkanAddress address,
        VulkanCommandPool commandPool,
        int... usedBeginInfoFlags
    ) {
        this.device = device;
        this.address = address;
        this.commandPool = commandPool;
        this.usedBeginInfoFlags = usedBeginInfoFlags;
        commandBuffer = createCommandBuffer();
        beginInfo = createBeginInfo();
    }
    
    VkCommandBuffer getCommandBuffer() {
        return commandBuffer;
    }
    
    void fillQueue( VulkanCommands commands ) {
        fillQueueSetup();
        commands.execute( this );
        fillQueueTearDown();
    }
    
    void fillQueueSetup() {
        resetCommandBuffer();
        beginRecordingCommandBuffer();
    }
    
    void fillQueueTearDown() {
        endCommandBuffer();
    }
    
    void close() {
        VK10.vkFreeCommandBuffers( device.getLogicalDevice(), commandPool.getAddress().getValue(), commandBuffer );
    }
    
    void drawVertices(
        VulkanDataBuffer< float[] >vertexBuffer,
        VulkanDataBuffer< float[] > uvBuffer,
        VulkanDataBuffer< float[] > colourBuffer
    ) {
        VK10.vkCmdBindVertexBuffers( commandBuffer, 0,
            new long[]{
                vertexBuffer.getBufferAddress().getValue(),
                uvBuffer.getBufferAddress().getValue(),
                colourBuffer.getBufferAddress().getValue()
            },
            new long[]{ 0, 0, 0 }
        );
        VK10.vkCmdDraw( commandBuffer, 3, 1, 0, 0 );
    }
    
    VulkanAddress getAddress(){
        return address;
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
            () -> VK10.vkBeginCommandBuffer( commandBuffer, beginInfo ),
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
        return VkCommandBufferBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO )
            .flags( VulkanHelper.flagsToInt( usedBeginInfoFlags ) )
            .pInheritanceInfo( null );
    }
}
