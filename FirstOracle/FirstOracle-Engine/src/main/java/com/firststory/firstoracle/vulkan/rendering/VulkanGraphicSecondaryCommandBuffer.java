/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanFrameBuffer;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
import com.firststory.firstoracle.vulkan.commands.VulkanCommandBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBufferInheritanceInfo;

/**
 * @author n1t4chi
 */
public class VulkanGraphicSecondaryCommandBuffer extends VulkanCommandBuffer {
    
    private VkCommandBufferInheritanceInfo inheritanceInfo;
    
    VulkanGraphicSecondaryCommandBuffer(
        VulkanPhysicalDevice device,
        VulkanAddress address,
        VulkanGraphicCommandPool commandPool,
        int... usedBeginInfoFlags
    ) {
        super( device, address, commandPool, usedBeginInfoFlags );
    }
    
    public void update(
        VulkanRenderPass renderPass,
        int subpassIndex,
        VulkanFrameBuffer frameBuffer
    ) {
        inheritanceInfo = VkCommandBufferInheritanceInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_INHERITANCE_INFO )
            .renderPass( renderPass.getAddress().getValue() )
            .subpass( subpassIndex )
            .framebuffer( frameBuffer.getAddress().getValue() );
    }
    
    @Override
    protected VkCommandBufferInheritanceInfo createInheritanceInfo() {
        return inheritanceInfo;
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
    
    void bindDescriptorSets( VulkanGraphicPipelines graphicPipelines, VulkanDescriptorSet descriptorSet ) {
        VK10.vkCmdBindDescriptorSets( getCommandBuffer(),
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicPipelines.getPipelineLayout().getValue(),
            0,
            MemoryUtil.memAllocLong( 1 ).put( 0, descriptorSet.getAddress().getValue() ),
            null
        );
    }
    
    void bindPipeline( VulkanGraphicPipelines.Pipeline graphicPipeline ) {
        VK10.vkCmdBindPipeline(
            getCommandBuffer(),
            VK10.VK_PIPELINE_BIND_POINT_GRAPHICS,
            graphicPipeline.getGraphicPipeline().getValue()
        );
    }
}
