/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanCommandBuffersException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanCommandBufferException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanCommandPoolException;
import com.firststory.firstoracle.window.vulkan.exceptions.FailedToEndCommandBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VulkanCommandPool {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanCommandBuffer.class );
    private final long address;
    private final VulkanPhysicalDevice device;
    private final List< VulkanCommandBuffer > commandBuffers;
    
    VulkanCommandPool( VulkanPhysicalDevice device ) {
        this.device = device;
        address = createCommandPool();
        commandBuffers = createCommandBuffers();
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
    
    private List< VulkanCommandBuffer > createCommandBuffers() {
        List< VulkanCommandBuffer > commandBuffers = new ArrayList<>(  );
        PointerBuffer commandBufferBuffer = createCommandBufferBuffer();
        int iterator = 0;
        while ( commandBufferBuffer.hasRemaining() ) {
            commandBuffers.add( new VulkanCommandBuffer( commandBufferBuffer.get(), iterator++ ) );
        }
        return commandBuffers;
    }
    
    private PointerBuffer createCommandBufferBuffer( ) {
        PointerBuffer commandBuffersBuffer = MemoryUtil.memAllocPointer( device.getFrameBuffers().size() );
        if( VK10.vkAllocateCommandBuffers(
                device.getLogicalDevice(), createAllocateInfo(), commandBuffersBuffer ) != VK10.VK_SUCCESS
        ) {
            throw new CannotAllocateVulkanCommandBuffersException( device );
        }
        return commandBuffersBuffer;
    }
    
    private VkCommandBufferAllocateInfo createAllocateInfo( ) {
        return VkCommandBufferAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO )
            .commandPool( address )
            .level( VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY )
            .commandBufferCount( device.getFrameBuffers().size() );
    }
    
    private long createCommandPool() {
        long[] address = new long[1];
        if( VK10.vkCreateCommandPool(
            device.getLogicalDevice(), createCommandPoolCreateInfo(), null, address ) != VK10.VK_SUCCESS
        ) {
            throw new CannotCreateVulkanCommandPoolException( device );
        }
        return address[0];
    }
    
    private VkCommandPoolCreateInfo createCommandPoolCreateInfo() {
        return VkCommandPoolCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO )
            .queueFamilyIndex( device.getGraphicFamily().getIndex() )
            .flags(
                //0
                //todo: when rendering is complete maybe they can raise performance:
                VK10.VK_COMMAND_POOL_CREATE_TRANSIENT_BIT | VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT
            );
    }
    
    void testRender() {
        commandBuffers.forEach( commandBuffer -> commandBuffer.render( commandBuffer::drawVertices ) );
    }
    
    public class VulkanCommandBuffer {
    
        private final long address;
        private final VkCommandBuffer commandBuffer;
        private final VkCommandBufferBeginInfo beginInfo;
        private final VulkanFrameBuffer frameBuffer;
        private VkRenderPassBeginInfo renderPassBeginInfo;
    
        private VulkanCommandBuffer( long address, int iterator ) {
            this.address = address;
            frameBuffer = extractCorrespondingFrameBuffer( iterator );
            commandBuffer = createCommandBuffer();
            beginInfo = createBeginInfo();
            renderPassBeginInfo = createRenderPassBeginInfo();
        }
    
        void render( Commands commands ) {
            beginRecordingCommandBuffer();
            beginRenderPassForCommandBuffer();
            bindPipeline();
            
            commands.execute();
            
            endRenderPass();
            endCommandBuffer();
        }
    
        private void endCommandBuffer() {
            if( VK10.vkEndCommandBuffer( commandBuffer ) != VK10.VK_SUCCESS ) {
                logger.warning( "Failed to record command buffer!" );
                throw new FailedToEndCommandBuffer(this );
            }
        }
    
        void drawVertices() {
            VK10.vkCmdDraw(commandBuffer, 3,1,0,0);
            
        }
    
        private void endRenderPass() {
            VK10.vkCmdEndRenderPass( commandBuffer );
        }
    
        private void bindPipeline() {
            VK10.vkCmdBindPipeline(
                commandBuffer, VK10.VK_PIPELINE_BIND_POINT_GRAPHICS, device.getGraphicPipeline().getGraphicPipeline());
        }
    
        private void beginRenderPassForCommandBuffer() {
            VK10.vkCmdBeginRenderPass( commandBuffer, renderPassBeginInfo, VK10.VK_SUBPASS_CONTENTS_INLINE );
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
    
        private void beginRecordingCommandBuffer() {
            if( VK10.vkBeginCommandBuffer( commandBuffer, beginInfo ) != VK10.VK_SUCCESS ) {
                throw new CannotCreateVulkanCommandBufferException( device );
            }
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
    
    }
}
