/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.allocators.VulkanCommandBufferAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.*;

import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanGraphicPrimaryCommandBuffer extends VulkanCommandBuffer< VulkanGraphicPrimaryCommandBuffer > {
    
    VulkanGraphicPrimaryCommandBuffer(
        VulkanCommandBufferAllocator< VulkanGraphicPrimaryCommandBuffer > allocator,
        VulkanPhysicalDevice device,
        VulkanGraphicCommandPool commandPool,
        VulkanAddress address,
        int... usedBeginInfoFlags
    ) {
        super(
            allocator,
            device,
            commandPool,
            address,
            usedBeginInfoFlags
        );
    }
    
    @Override
    public void fillQueueTearDown() {
        endRenderPass();
        super.fillQueueTearDown();
    }
    
    void executeSecondaryBuffers( List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers ) {
        if( secondaryBuffers.isEmpty() ) {
            return;
        }
        var buffers = PointerBuffer.allocateDirect( secondaryBuffers.size() );
        for ( var i = 0; i < secondaryBuffers.size(); i++ ) {
            buffers.put( i, secondaryBuffers.get( i ).getCommandBuffer().address() );
        }
        VK10.vkCmdExecuteCommands( getCommandBuffer(), buffers );
    }
    
    private boolean activeRenderPass = false;
    
    void beginRenderPass( VulkanSwapChain swapChain, VulkanRenderPass renderPass, VulkanFrameBuffer frameBuffer, Colour backgroundColour ) {
        activeRenderPass = true;
        var renderPassBeginInfo = createRenderPassBeginInfo( renderPass, swapChain, frameBuffer, backgroundColour );
        VK10.vkCmdBeginRenderPass( getCommandBuffer(), renderPassBeginInfo, VK10.VK_SUBPASS_CONTENTS_SECONDARY_COMMAND_BUFFERS );
    }
    
    private void endRenderPass() {
        if( !activeRenderPass ) {
            throw new RuntimeException( "cannot end render pass" );
        }
        activeRenderPass = false;
        VK10.vkCmdEndRenderPass( getCommandBuffer() );
    }
    
    private VkRenderPassBeginInfo createRenderPassBeginInfo(
        VulkanRenderPass renderPass,
        VulkanSwapChain swapChain,
        VulkanFrameBuffer frameBuffer,
        Colour backgroundColour
    ) {
        return VkRenderPassBeginInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO )
            .renderPass( renderPass.getAddress().getValue() )
            .framebuffer( frameBuffer.getAddress().getValue() )
            .renderArea( createRenderArea( swapChain ) )
            .pClearValues( createClearValue( backgroundColour ) );
    }
    
    private VkRect2D createRenderArea( VulkanSwapChain swapChain ) {
        return VkRect2D.calloc()
            .offset( VkOffset2D.calloc().set( 0, 0 ) )
            .extent( swapChain.getExtent() );
    }
    
    private VkClearValue.Buffer createClearValue( Colour colour ) {
        return VkClearValue.calloc( 2 )
            .put( 0, createClearColour( colour ) )
            .put( 1, createDepthStencil() )
        ;
    }
    
    private VkClearValue createDepthStencil() {
        return VkClearValue.create().depthStencil( VkClearDepthStencilValue.calloc().depth( 1f ).stencil( 0 ) );
    }
    
    private VkClearValue createClearColour( Colour colour ) {
        return VkClearValue.create().color( VkClearColorValue.calloc()
            .float32( 0, colour.red() )
            .float32( 1, colour.green() )
            .float32( 2, colour.blue() )
            .float32( 3, colour.alpha() )
        );
    }
}
