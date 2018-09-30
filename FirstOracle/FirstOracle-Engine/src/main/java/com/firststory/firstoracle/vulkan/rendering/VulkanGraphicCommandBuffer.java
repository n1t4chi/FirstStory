/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.vulkan.*;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanGraphicCommandBuffer extends VulkanCommandBuffer {
    private final VulkanFrameBuffer frameBuffer;
    private final Colour backgroundColour;
    private final VulkanSwapChain swapChain;
    private final int index;
    
    VulkanGraphicCommandBuffer(
        VulkanPhysicalDevice device,
        VulkanAddress address,
        VulkanFrameBuffer frameBuffer,
        Colour backgroundColour,
        VulkanSwapChain swapChain,
        VulkanCommandPool< VulkanGraphicCommandBuffer > commandPool,
        int index,
        int... usedBeginInfoFlags
    ) {
        super( device, address, commandPool, usedBeginInfoFlags );
        this.frameBuffer = frameBuffer;
        this.backgroundColour = backgroundColour;
        this.swapChain = swapChain;
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    @Override
    public void fillQueueTearDown() {
        endRenderPassIfActive();
        super.fillQueueTearDown();
    }
    
    
    
    void setLineWidth( Float lineWidth ) {
        VK10.vkCmdSetLineWidth( getCommandBuffer(), lineWidth );
    }
    
    void draw(
        VulkanDataBuffer vertexBuffer,
        VulkanDataBuffer uvBuffer,
        VulkanDataBuffer colourBuffer,
        VulkanDataBuffer dataBuffer,
        int bufferSize
    ) {
        VK10.vkCmdBindVertexBuffers( getCommandBuffer(), 0,
            new long[]{
                vertexBuffer.getBufferAddress().getValue(),
                uvBuffer.getBufferAddress().getValue(),
                colourBuffer.getBufferAddress().getValue(),
                dataBuffer.getBufferAddress().getValue()
            },
            new long[]{
                vertexBuffer.getMemoryOffset(),
                uvBuffer.getMemoryOffset(),
                colourBuffer.getMemoryOffset(),
                dataBuffer.getMemoryOffset(),
            }
        );
        
        VK10.vkCmdDraw(
            getCommandBuffer(),
            bufferSize,
            1,
            0,
            0
        );
    }
    
    void bindDescriptorSets( VulkanGraphicPipeline graphicPipeline, VulkanDescriptorSet descriptorSet ) {
        VK10.vkCmdBindDescriptorSets( getCommandBuffer(),
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicPipeline.getPipelineLayout().getValue(),
            0,
            MemoryUtil.memAllocLong( 1 ).put( 0, descriptorSet.getAddress().getValue() ),
            null
        );
    }
    
    private boolean activeRenderPass = false;
    void endRenderPassIfActive() {
        if( activeRenderPass ) {
            endRenderPass();
        }
    }
    
    void beginRenderPass( VulkanRenderPass renderPass ) {
        activeRenderPass = true;
        var renderPassBeginInfo = createRenderPassBeginInfo( renderPass, swapChain, backgroundColour );
        VK10.vkCmdBeginRenderPass( getCommandBuffer(), renderPassBeginInfo, VK10.VK_SUBPASS_CONTENTS_INLINE );
    }
    
    private void endRenderPass() {
        activeRenderPass = false;
        VK10.vkCmdEndRenderPass( getCommandBuffer() );
    }
    
    void bindPipeline( VulkanGraphicPipeline graphicPipeline ) {
        VK10.vkCmdBindPipeline(
            getCommandBuffer(),
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicPipeline.getGraphicPipeline().getValue()
        );
    }
    
    private VkRenderPassBeginInfo createRenderPassBeginInfo(
        VulkanRenderPass renderPass,
        VulkanSwapChain swapChain,
        Colour backgroundColour
    ) {
        return VkRenderPassBeginInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO )
            .renderPass( renderPass.getAddress().getValue() )
            .framebuffer( frameBuffer.getAddress().getValue() )
            .renderArea( createRenderArea( swapChain ) )
            .pClearValues( createClearValue( backgroundColour ) );
    }
    
    private VkRect2D createRenderArea( VulkanSwapChain swapChain ) {
        return VkRect2D.create()
            .offset( VkOffset2D.create().set( 0, 0 ) )
            .extent( swapChain.getExtent() );
    }
    
    private VkClearValue.Buffer createClearValue( Colour colour ) {
        return VkClearValue.create( 2 )
            .put( 0, createClearColour( colour ) )
            .put( 1, createDepthStencil() )
        ;
    }
    
    private VkClearValue createDepthStencil() {
        return VkClearValue.create().depthStencil( VkClearDepthStencilValue.create().depth( 1f ).stencil( 0 ) );
    }
    
    private VkClearValue createClearColour( Colour colour ) {
        return VkClearValue.create().color( VkClearColorValue.create()
            .float32( 0, colour.red() )
            .float32( 1, colour.green() )
            .float32( 2, colour.blue() )
            .float32( 3, colour.alpha() )
        );
    }
}
