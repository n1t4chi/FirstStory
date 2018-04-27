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
    private final long address;
    private final VkCommandBuffer commandBuffer;
    private final VkCommandBufferBeginInfo beginInfo;
    private final VulkanFrameBuffer frameBuffer;
    private final VkRenderPassBeginInfo renderPassBeginInfo;
    
    VulkanCommandBuffer( VulkanPhysicalDevice device, long address, int iterator ) {
        this.device = device;
        this.address = address;
        frameBuffer = extractCorrespondingFrameBuffer( iterator );
        commandBuffer = createCommandBuffer();
        beginInfo = createBeginInfo();
        renderPassBeginInfo = createRenderPassBeginInfo();
    }
    
    void fillRenderQueue( Commands commands ) {
        //resetCommandBuffer();
        beginRecordingCommandBuffer();
        beginRenderPassForCommandBuffer();
        bindPipeline();
        
        commands.execute();
        
        endRenderPass();
        endCommandBuffer();
    }
    
    private void resetCommandBuffer() {
        if (VK10.vkResetCommandBuffer( commandBuffer, VK10.VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT ) != VK10.VK_SUCCESS) {
            logger.warning( "Failed to reset command buffer!" );
            throw new VulkanCommandBufferException( device, this, "Failed to reset command buffer" );
        }
    }
    
    private void beginRecordingCommandBuffer() {
        if ( VK10.vkBeginCommandBuffer( commandBuffer, beginInfo ) != VK10.VK_SUCCESS ) {
            logger.warning( "Failed to begin command buffer!" );
            throw new VulkanCommandBufferException( device, this, "Failed to begin command buffer" );
        }
    }
    
    private void beginRenderPassForCommandBuffer() {
        VK10.vkCmdBeginRenderPass( commandBuffer, renderPassBeginInfo, VK10.VK_SUBPASS_CONTENTS_INLINE );
    }
    
    private void endCommandBuffer() {
        if ( VK10.vkEndCommandBuffer( commandBuffer ) != VK10.VK_SUCCESS ) {
            logger.warning( "Failed to end command buffer!" );
            throw new VulkanCommandBufferException( device, this, "Failed to end command buffer" );
        }
    }
    
    void drawVertices() {
        VK10.vkCmdDraw( commandBuffer, 3, 1, 0, 0 );
    }
    
    long getAddress(){
        return address;
    }
    
    private void endRenderPass() {
        VK10.vkCmdEndRenderPass( commandBuffer );
    }
    
    private void bindPipeline() {
        VK10.vkCmdBindPipeline(
            commandBuffer,
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            device.getGraphicPipeline().getGraphicPipeline()
        );
    }
    
    private VkRenderPassBeginInfo createRenderPassBeginInfo() {
        return VkRenderPassBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO )
            .renderPass( device.getGraphicPipeline().getRenderPass() )
            .framebuffer( frameBuffer.getAddress() )
            .renderArea( createRenderArea( device ) )
            .pClearValues( createClearValue() );
    }
    
    private VulkanFrameBuffer extractCorrespondingFrameBuffer( int iterator ) {
        return device.getFrameBuffers().get( iterator );
    }
    
    private VkCommandBuffer createCommandBuffer() {
        return new VkCommandBuffer( address, device.getLogicalDevice() );
    }
    
    private VkCommandBufferBeginInfo createBeginInfo() {
        return VkCommandBufferBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO )
            .flags( VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT )
            .pInheritanceInfo( null );
    }
    
    private VkRect2D createRenderArea( VulkanPhysicalDevice device ) {
        return VkRect2D.create()
            .offset( VkOffset2D.create().set( 0, 0 ) )
            .extent( device.getSwapChain().getExtent() );
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
