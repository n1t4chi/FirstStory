/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanCommandBuffer;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBufferInheritanceInfo;

/**
 * @author n1t4chi
 */
public class VulkanGraphicSecondaryCommandBuffer extends VulkanCommandBuffer {
    
    private static final int SUBPASS_INDEX = 0;
    
    private static VkCommandBufferInheritanceInfo createInheritanceInfo( VulkanGraphicPrimaryCommandBuffer primaryBuffer, VulkanRenderPass renderPass ) {
        return VkCommandBufferInheritanceInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_INHERITANCE_INFO )
            .renderPass( renderPass.getAddress().getValue() )
            .subpass( SUBPASS_INDEX )
            .framebuffer( primaryBuffer.getFrameBuffer().getAddress().getValue() )
        ;
    }
    
    private final VulkanGraphicPrimaryCommandBuffer primaryBuffer;
    
    VulkanGraphicSecondaryCommandBuffer(
        VulkanPhysicalDevice device,
        VulkanAddress address,
        VulkanGraphicPrimaryCommandBuffer primaryBuffer,
        VulkanGraphicCommandPool commandPool,
        VulkanRenderPass renderPass,
        int... usedBeginInfoFlags
    ) {
        super( device, address, commandPool, createInheritanceInfo( primaryBuffer, renderPass ), usedBeginInfoFlags );
        this.primaryBuffer = primaryBuffer;
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