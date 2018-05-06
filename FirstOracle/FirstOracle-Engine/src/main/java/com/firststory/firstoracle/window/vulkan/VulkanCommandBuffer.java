/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.window.vulkan.exceptions.VulkanCommandBufferException;
import org.lwjgl.vulkan.*;

import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class VulkanCommandBuffer {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandBuffer.class );
    private final VulkanPhysicalDevice device;
    private final VulkanAddress address;
    private final VkCommandBuffer commandBuffer;
    private final VkCommandBufferBeginInfo beginInfo;
    private final VulkanFrameBuffer frameBuffer;
    private final VkRenderPassBeginInfo renderPassBeginInfo;
    private final VulkanGraphicPipeline graphicPipeline;
    private final VulkanCommandPool commandPool;
    
    VulkanCommandBuffer(
        VulkanPhysicalDevice device,
        VulkanAddress address,
        VulkanFrameBuffer frameBuffer,
        VulkanGraphicPipeline graphicsPipeline,
        VulkanSwapChain swapChain,
        VulkanCommandPool commandPool
    ) {
        this.device = device;
        this.address = address;
        this.frameBuffer = frameBuffer;
        this.graphicPipeline = graphicsPipeline;
        this.commandPool = commandPool;
        commandBuffer = createCommandBuffer();
        beginInfo = createBeginInfo();
        renderPassBeginInfo = createRenderPassBeginInfo( graphicsPipeline, swapChain );
    }
    
    VulkanCommandBuffer(
        VulkanPhysicalDevice device,
        long address,
        VulkanFrameBuffer frameBuffer,
        VulkanGraphicPipeline graphicsPipeline,
        VulkanSwapChain swapChain,
        VulkanCommandPool commandPool
    ) {
        this( device, new VulkanAddress( address ), frameBuffer, graphicsPipeline, swapChain, commandPool );
    }
    
    void close() {
        VK10.vkFreeCommandBuffers( device.getLogicalDevice(), commandPool.getAddress().getValue(), commandBuffer );
    }
    
    void transferQueue( Commands commands ) {
        resetCommandBuffer();
        beginRecordingCommandBuffer();
        
        commands.execute();
        
        endCommandBuffer();
    }
    void renderQueue( Commands commands ) {
        resetCommandBuffer();
        beginRecordingCommandBuffer();
        beginRenderPassForCommandBuffer();
        bindPipeline( graphicPipeline );
        
        commands.execute();
        
        endRenderPass();
        endCommandBuffer();
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
    
    private void beginRenderPassForCommandBuffer() {
        VK10.vkCmdBeginRenderPass( commandBuffer, renderPassBeginInfo, VK10.VK_SUBPASS_CONTENTS_INLINE );
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
    
    void drawVertices( VulkanDataBuffer buffer ) {
    
        VK10.vkCmdBindVertexBuffers( commandBuffer, 0,
            new long[]{ buffer.getBufferAddress().getValue() },
            new long[]{ 0 }
        );
        VK10.vkCmdDraw( commandBuffer, 3, 1, 0, 0 );
    }
    
    VulkanAddress getAddress(){
        return address;
    }
    
    private void endRenderPass() {
        VK10.vkCmdEndRenderPass( commandBuffer );
    }
    
    private void bindPipeline( VulkanGraphicPipeline graphicPipeline ) {
        VK10.vkCmdBindPipeline(
            commandBuffer,
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicPipeline.getGraphicPipeline()
        );
    }
    
    private VkRenderPassBeginInfo createRenderPassBeginInfo(
        VulkanGraphicPipeline graphicPipeline, VulkanSwapChain swapChain
    ) {
        return VkRenderPassBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO )
            .renderPass( graphicPipeline.getRenderPass() )
            .framebuffer( frameBuffer.getAddress().getValue() )
            .renderArea( createRenderArea( swapChain ) )
            .pClearValues( createClearValue() );
    }
    
    private VkCommandBuffer createCommandBuffer() {
        return new VkCommandBuffer( address.getValue(), device.getLogicalDevice() );
    }
    
    private VkCommandBufferBeginInfo createBeginInfo() {
        return VkCommandBufferBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO )
            .flags( VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT )
            .pInheritanceInfo( null );
    }
    
    private VkRect2D createRenderArea( VulkanSwapChain swapChain ) {
        return VkRect2D.create()
            .offset( VkOffset2D.create().set( 0, 0 ) )
            .extent( swapChain.getExtent() );
    }
    
    private VkClearValue.Buffer createClearValue() {
        return VkClearValue.create( 1 ).put( createClearColour() ).flip();
    }
    
    private VkClearValue createClearColour() {
        return VkClearValue.create()
            .color(
                VkClearColorValue.create()
                    .float32( 0, 0f )
                    .float32( 1, 0f )
                    .float32( 2, 0f )
                    .float32( 3, 1f )
            );
    }
    
}
